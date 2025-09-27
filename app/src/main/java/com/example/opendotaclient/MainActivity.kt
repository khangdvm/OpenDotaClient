package com.example.opendotaclient

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.outlined.AutoFixHigh
import androidx.compose.material.icons.outlined.BarChart
import androidx.compose.material.icons.outlined.Public
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavDestination
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController

// ---- data / retrofit ----
import com.example.opendotaclient.data.remote.RetrofitClient

// ---- repositories (đều ở data.remote) ----
import com.example.opendotaclient.data.remote.PublicMatchesRepository
import com.example.opendotaclient.data.remote.MatchesRepository
import com.example.opendotaclient.data.remote.HeroesRepository
import com.example.opendotaclient.data.remote.TeamsRepository

// ---- UI screens + factories ----
import com.example.opendotaclient.ui.matches.PublicFeedScreen
import com.example.opendotaclient.ui.matches.PublicFeedVMFactory
import com.example.opendotaclient.ui.matches.PublicFeedViewModel
import com.example.opendotaclient.ui.matches.MatchesScreen
import com.example.opendotaclient.ui.matches.MatchesVMFactory
import com.example.opendotaclient.ui.matches.MatchesViewModel

import com.example.opendotaclient.ui.heroes.HeroesScreen
import com.example.opendotaclient.ui.heroes.HeroesVMFactory
import com.example.opendotaclient.ui.heroes.HeroesViewModel

import com.example.opendotaclient.ui.teams.TeamsScreen
import com.example.opendotaclient.ui.teams.TeamsVMFactory
import com.example.opendotaclient.ui.teams.TeamsViewModel

// import thêm:
import com.example.opendotaclient.data.remote.MatchDetailRepository
import com.example.opendotaclient.ui.matches.MatchDetailScreen
import com.example.opendotaclient.ui.matches.MatchDetailVMFactory
import com.example.opendotaclient.ui.matches.MatchDetailViewModel

// ========== Palette ==========
private val Navy = Color(0xFF123A4A)
private val NavyDark = Color(0xFF0E2D3A)
private val Ink = Color(0xFF0B1C24)
private val AccentBlue = Color(0xFF3BA1E6)
private val TextLight = Color(0xFFEAF2F7)
private val TextDim = Color(0xFFBDD1DE)

// ========== Typography ==========
private val AppTypography = Typography(
    displayLarge = TextStyle(
        fontFamily = FontFamily.SansSerif,
        fontWeight = FontWeight.ExtraBold,
        fontSize = 44.sp,
        letterSpacing = (-0.5).sp
    ),
    titleLarge = TextStyle(
        fontFamily = FontFamily.SansSerif,
        fontWeight = FontWeight.Medium,
        fontSize = 18.sp
    ),
    labelSmall = TextStyle(
        fontFamily = FontFamily.SansSerif,
        fontWeight = FontWeight.Medium,
        fontSize = 13.sp,
        letterSpacing = 0.15.sp
    ),
    titleMedium = TextStyle(
        fontFamily = FontFamily.SansSerif,
        fontWeight = FontWeight.SemiBold,
        fontSize = 16.sp
    ),
    bodyMedium = TextStyle(
        fontFamily = FontFamily.SansSerif,
        fontSize = 14.sp,
        lineHeight = 20.sp
    )
)

@OptIn(ExperimentalMaterial3Api::class)
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MaterialTheme(
                colorScheme = darkColorScheme(
                    primary = AccentBlue,
                    background = Ink,
                    surface = Ink,
                    onBackground = TextLight,
                    onSurface = TextLight
                ),
                typography = AppTypography
            ) {
                val nav = rememberNavController()
                val backStack by nav.currentBackStackEntryAsState()
                val dest = backStack?.destination

                Scaffold(
                    topBar = {
                        TopAppBar(
                            colors = TopAppBarDefaults.topAppBarColors(containerColor = Navy),
                            title = {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Text(
                                        "<OPENDOTA/>",
                                        color = AccentBlue,
                                        fontWeight = FontWeight.Black,
                                        fontSize = 18.sp,
                                        modifier = Modifier.clickable { nav.safeNavigate("home", dest) }
                                    )
                                    Spacer(Modifier.width(18.dp))
                                    TopNavItem("Matches") { nav.safeNavigate("matches", dest) }
                                    TopNavItem("Heroes")  { nav.safeNavigate("heroes", dest) }
                                    TopNavItem("Teams")   { nav.safeNavigate("teams", dest) }
                                    Spacer(Modifier.weight(1f))
                                }
                            }
                        )
                    }
                ) { inner ->
                    Box(Modifier.fillMaxSize().padding(inner)) {
                        NavHost(navController = nav, startDestination = "home") {
                            composable("match/{matchId}") { backStackEntry ->
                                val matchId = backStackEntry.arguments?.getString("matchId")?.toLongOrNull()
                                if (matchId != null) {
                                    val api = RetrofitClient.api
                                    val detailRepo = MatchDetailRepository(api)
                                    val heroesRepo = HeroesRepository(api)

                                    val factory = MatchDetailVMFactory(
                                        matchId = matchId,
                                        detailRepo = detailRepo,
                                        heroesRepo = heroesRepo,
                                        apiKey = null
                                    )
                                    val vm = viewModel<MatchDetailViewModel>(factory = factory)
                                    MatchDetailScreen(vm)
                                }
                            }



                            // ----- Public feed (/matches) -----
                            composable("matches") {
                                val api = RetrofitClient.api
                                val repo = PublicMatchesRepository(api)
                                val factory = PublicFeedVMFactory(repo)
                                val vm = viewModel<PublicFeedViewModel>(factory = factory)
                                PublicFeedScreen(
                                    vm = vm,
                                    onOpenMatch = { id -> nav.navigate("match/$id") }
                                )

                            }


                            // ----- Teams list -----
                            composable("teams") {
                                val api = RetrofitClient.api
                                val repo = TeamsRepository(api)
                                val factory = TeamsVMFactory(repo, null) // cách B: VM nhận apiKey?
                                val vm = viewModel<TeamsViewModel>(factory = factory)
                                TeamsScreen(vm) // gọi trực tiếp, không cần TeamsHost
                            }

                            // ----- Home: nhập Steam32/64 → mở matches/{id} -----
                            composable("home") {
                                HomeScreen(
                                    onOpenPlayer = { rawId ->
                                        val id32 = toSteam32(rawId)
                                        if (id32 != null && id32 > 0) {
                                            nav.safeNavigate("matches/$id32", dest)
                                        }
                                    }
                                )
                            }

                            // ----- Matches theo player -----
                            composable("matches/{accountId32}") { backStackEntry ->
                                val idStr = backStackEntry.arguments?.getString("accountId32")
                                val accountId32 = idStr?.toLongOrNull()
                                if (accountId32 != null) {
                                    val api = RetrofitClient.api
                                    val repo = MatchesRepository(api)
                                    val factory = MatchesVMFactory(repo, accountId32, null)
                                    val vm = viewModel<MatchesViewModel>(factory = factory)
                                    MatchesScreen(vm)
                                } else {
                                    LaunchedEffect(Unit) { nav.safeNavigate("home", dest) }
                                }
                            }

                            // ----- Heroes grid -----
                            composable("heroes") {
                                val api = RetrofitClient.api
                                val repo = HeroesRepository(api)
                                val factory = HeroesVMFactory(repo, null)
                                val vm = viewModel<HeroesViewModel>(factory = factory)
                                HeroesScreen(vm)
                            }
                        }
                    }
                }
            }
        }
    }
}

/* ===== Helpers ===== */
private fun toSteam32(input: String): Long? {
    val trimmed = input.trim()
    if (trimmed.isEmpty()) return null
    val n = trimmed.toLongOrNull() ?: return null
    return if (trimmed.length > 10 || trimmed.startsWith("7656")) {
        n - 76561197960265728L
    } else n
}

private fun androidx.navigation.NavHostController.safeNavigate(
    route: String,
    current: NavDestination?
) {
    if (current?.route == route) return
    navigate(route) {
        popUpTo(graph.findStartDestination().id) { saveState = true }
        launchSingleTop = true
        restoreState = true
    }
}

/* ================= HOME SCREEN ================= */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(onOpenPlayer: (String) -> Unit) {
    var query by remember { mutableStateOf(TextFieldValue("")) }
    var error by remember { mutableStateOf<String?>(null) }

    Scaffold(containerColor = Ink) { inner ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(inner)
                .verticalScroll(rememberScrollState())
        ) {
            // Banner
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(260.dp)
                    .background(NavyDark)
            ) {
                Image(
                    painter = painterResource(id = R.drawable.dota2),
                    contentDescription = "Hero background",
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxSize()
                )
                Box(
                    Modifier.fillMaxSize().background(
                        Brush.verticalGradient(
                            colors = listOf(Color(0xAA061722), Color(0xEE061722))
                        )
                    )
                )
                Column(
                    Modifier
                        .fillMaxSize()
                        .padding(horizontal = 20.dp),
                    verticalArrangement = Arrangement.Center
                ) {
                    Text("OPENDOTA", style = MaterialTheme.typography.displayLarge)
                    Text(
                        "Open source Dota 2 data platform",
                        style = MaterialTheme.typography.titleLarge,
                        color = TextDim
                    )
                    Spacer(Modifier.height(16.dp))
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        OutlinedTextField(
                            value = query,
                            onValueChange = { query = it; error = null },
                            singleLine = true,
                            leadingIcon = { Icon(Icons.Filled.Search, null) },
                            placeholder = { Text("Enter Steam32 or Steam64 ID…", color = TextDim) },
                            textStyle = LocalTextStyle.current.copy(color = TextLight),
                            modifier = Modifier
                                .weight(1f)
                                .height(52.dp)
                                .clip(RoundedCornerShape(6.dp)),
                            isError = error != null,
                            supportingText = { error?.let { Text(it) } },
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedContainerColor = Color(0xFF142732),
                                unfocusedContainerColor = Color(0xFF142732),
                                focusedBorderColor = AccentBlue,
                                unfocusedBorderColor = Color.Transparent,
                                cursorColor = AccentBlue
                            )
                        )
                        Spacer(Modifier.width(10.dp))
                        Button(
                            onClick = {
                                val input = query.text.trim()
                                if (input.isEmpty()) {
                                    error = "Please enter a Steam ID"
                                } else onOpenPlayer(input)
                            },
                            modifier = Modifier.height(52.dp)
                        ) { Text("View Player") }
                    }
                }
            }

            // Info cards
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp, vertical = 22.dp),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                InfoCard(
                    icon = { Icon(Icons.Outlined.Public, contentDescription = null) },
                    title = "Open Source",
                    body = "All project code is open source and available for contributors to improve and modify."
                )
                InfoCard(
                    icon = { Icon(Icons.Outlined.BarChart, contentDescription = null) },
                    title = "In-Depth Data",
                    body = "Parsing replay files provides highly detailed match data."
                )
                InfoCard(
                    icon = { Icon(Icons.Outlined.AutoFixHigh, contentDescription = null) },
                    title = "Free of Charge",
                    body = "Servers are funded by sponsors and volunteers maintain the code."
                )
            }

            SponsorBlock()
            FooterBlock()
        }
    }
}

/* ========== UI Components ========== */
@Composable
private fun TopNavItem(label: String, onClick: () -> Unit) {
    TextButton(onClick = onClick) {
        Text(
            label,
            style = MaterialTheme.typography.labelSmall,
            color = TextDim,
            modifier = Modifier.padding(end = 6.dp)
        )
    }
}

@Composable
private fun InfoCard(icon: @Composable () -> Unit, title: String, body: String) {
    Surface(
        color = Color(0xFF132631),
        tonalElevation = 2.dp,
        shape = RoundedCornerShape(12.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(44.dp)
                    .clip(RoundedCornerShape(10.dp))
                    .background(Color(0x1A3BA1E6)),
                contentAlignment = Alignment.Center
            ) {
                CompositionLocalProvider(LocalContentColor provides AccentBlue) {
                    icon()
                }
            }
            Spacer(Modifier.width(14.dp))
            Column(Modifier.weight(1f)) {
                Text(title, style = MaterialTheme.typography.titleMedium, color = TextLight)
                Spacer(Modifier.height(6.dp))
                Text(body, style = MaterialTheme.typography.bodyMedium, color = TextDim, textAlign = TextAlign.Start)
            }
        }
    }
}

@Composable
private fun SponsorBlock() {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 40.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text("SPONSORED BY", color = TextDim, fontSize = 12.sp)
        Spacer(Modifier.height(8.dp))
        Image(
            painter = painterResource(id = R.drawable.openai),
            contentDescription = "OpenAI logo",
            modifier = Modifier.size(96.dp)
        )
        Spacer(Modifier.height(12.dp))
        OutlinedButton(onClick = { /* no-op */ }) {
            Text("Become a Sponsor")
        }
    }
}

@Composable
private fun FooterBlock() {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color(0xFF0D1A21))
            .padding(vertical = 20.dp, horizontal = 16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text("<OPENDOTA/>", color = AccentBlue, fontWeight = FontWeight.Bold)
        Text("Open source Dota 2 data platform", color = TextDim, fontSize = 12.sp)
        Spacer(Modifier.height(8.dp))
        Row(
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            FooterLink("About"); FooterLink("Privacy & Terms"); FooterLink("API Docs")
            FooterLink("Blog"); FooterLink("Translate")
        }
        Spacer(Modifier.height(4.dp))
        Row(
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            FooterLink("Deploys by Netlify"); FooterLink("A Gravitech LLC Site")
        }
    }
}

@Composable
private fun FooterLink(label: String) {
    Text(text = label, color = AccentBlue, fontSize = 12.sp)
}
