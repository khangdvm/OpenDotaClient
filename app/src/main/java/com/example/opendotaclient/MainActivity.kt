package com.example.opendotaclient

import androidx.compose.runtime.saveable.rememberSaveable

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
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.opendotaclient.data.remote.HeroesRepository
import com.example.opendotaclient.data.remote.MatchDetailRepository
import com.example.opendotaclient.data.remote.MatchesRepository
import com.example.opendotaclient.data.remote.RetrofitClient
import com.example.opendotaclient.data.remote.TeamsRepository
import com.example.opendotaclient.ui.heroes.HeroesScreen
import com.example.opendotaclient.ui.heroes.HeroesVMFactory
import com.example.opendotaclient.ui.heroes.HeroesViewModel
import com.example.opendotaclient.ui.matches.MatchDetailScreen
import com.example.opendotaclient.ui.matches.MatchDetailVMFactory
import com.example.opendotaclient.ui.matches.MatchDetailViewModel
import com.example.opendotaclient.ui.matches.MatchesScreen
import com.example.opendotaclient.ui.matches.MatchesVMFactory
import com.example.opendotaclient.ui.matches.MatchesViewModel
import com.example.opendotaclient.ui.matches.PublicFeedScreen
import com.example.opendotaclient.ui.matches.PublicFeedVMFactory
import com.example.opendotaclient.ui.matches.PublicFeedViewModel
import com.example.opendotaclient.ui.teams.TeamsScreen
import com.example.opendotaclient.ui.teams.TeamsVMFactory
import com.example.opendotaclient.ui.teams.TeamsViewModel

// >>> ADDED imports for Player Overview <<<
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.opendotaclient.data.PlayerOverviewRepository
import com.example.opendotaclient.ui.player.PlayerOverviewScreen
import com.example.opendotaclient.ui.player.PlayerOverviewViewModel
// <<< END ADDED >>>

/* ===== Palette ===== */
private val Navy = Color(0xFF123A4A)
private val NavyDark = Color(0xFF0E2D3A)
private val Ink = Color(0xFF0B1C24)
private val AccentBlue = Color(0xFF3BA1E6)
private val TextLight = Color(0xFFEAF2F7)
private val TextDim = Color(0xFFBDD1DE)

/* ===== Typography ===== */
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
                                        modifier = Modifier.clickable { nav.goHome() }

                                    )
                                    Spacer(Modifier.width(18.dp))
                                    TopNavItem("Matches") { nav.safeNavigate("matches") }
                                    TopNavItem("Heroes")  { nav.safeNavigate("heroes") }
                                    TopNavItem("Teams")   { nav.safeNavigate("teams") }
                                    Spacer(Modifier.weight(1f))
                                }
                            }
                        )
                    }
                ) { inner ->
                    Box(Modifier.fillMaxSize().padding(inner)) {
                        NavHost(navController = nav, startDestination = "home") {
                            composable("match/{matchId}") { backStackEntry ->
                                backStackEntry.arguments?.getString("matchId")?.toLongOrNull()?.let { matchId ->
                                    val api = RetrofitClient.api
                                    val detailRepo = MatchDetailRepository(api)
                                    val heroesRepo = HeroesRepository(api)
                                    val vm = viewModel<MatchDetailViewModel>(
                                        factory = MatchDetailVMFactory(matchId, detailRepo, heroesRepo, apiKey = null)
                                    )
                                    MatchDetailScreen(vm)
                                }
                            }
                            composable("matches") {
                                val vm = viewModel<PublicFeedViewModel>(factory = PublicFeedVMFactory())
                                PublicFeedScreen(vm = vm, onOpenMatch = { id -> nav.navigate("match/$id") })
                            }
                            composable("teams") {
                                val vm = viewModel<TeamsViewModel>(
                                    factory = TeamsVMFactory(TeamsRepository(RetrofitClient.api), apiKey = null)
                                )
                                TeamsScreen(vm)
                            }
                            composable("home") {
                                HomeScreen(
                                    onOpenPlayer = { rawId ->
                                        val id32 = toSteam32(rawId)
                                        if (id32 != null && id32 > 0) nav.safeNavigate("player/$id32")
                                    }
                                )
                            }
                            composable("matches/{accountId32}") { backStackEntry ->
                                val id32 = backStackEntry.arguments?.getString("accountId32")?.toLongOrNull()
                                if (id32 != null) {
                                    val vm = viewModel<MatchesViewModel>(
                                        factory = MatchesVMFactory(
                                            repo = MatchesRepository(RetrofitClient.api),
                                            heroesRepo = HeroesRepository(RetrofitClient.api),
                                            accountId32 = id32,
                                            apiKey = null
                                        )
                                    )
                                    MatchesScreen(vm)
                                } else {
                                    LaunchedEffect(Unit) { nav.safeNavigate("home") }
                                }
                            }

                            // >>> Player Overview route
                            composable("player/{accountId32}") { backStackEntry ->
                                val id32 = backStackEntry.arguments?.getString("accountId32")?.toLongOrNull()
                                if (id32 != null) {
                                    val vm = viewModel<PlayerOverviewViewModel>(
                                        factory = object : ViewModelProvider.Factory {
                                            @Suppress("UNCHECKED_CAST")
                                            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                                                val api = RetrofitClient.api
                                                val overviewRepo = PlayerOverviewRepository(api)
                                                return PlayerOverviewViewModel(
                                                    id32 = id32,
                                                    overviewRepo = overviewRepo,
                                                    api = api
                                                ) as T
                                            }
                                        }
                                    )
                                    PlayerOverviewScreen(vm)
                                } else {
                                    LaunchedEffect(Unit) { nav.safeNavigate("home") }
                                }
                            }
                            // <<< END

                            composable("heroes") {
                                val vm = viewModel<HeroesViewModel>(
                                    factory = HeroesVMFactory(HeroesRepository(RetrofitClient.api), apiKey = null)
                                )
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
private fun androidx.navigation.NavHostController.goHome() {
    // Nếu 'home' đã có trong back stack: pop về thẳng home (không xoá nó)
    val popped = popBackStack(route = "home", inclusive = false)
    if (!popped) {
        // Nếu back stack chưa có 'home' thì navigate bình thường
        navigate("home") {
            popUpTo(graph.findStartDestination().id) { saveState = true }
            launchSingleTop = true
            restoreState = true
        }
    }
}

private fun toSteam32(input: String): Long? {
    val t = input.trim()
    if (t.isEmpty()) return null
    val n = t.toLongOrNull() ?: return null
    return if (t.length > 10 || t.startsWith("7656")) n - 76561197960265728L else n
}

private fun androidx.navigation.NavHostController.safeNavigate(route: String) {
    val current = this.currentDestination
    if (current?.route == route) return

    // Route có tham số động? (vd: "player/123", "match/456")
    val isDynamic = route.startsWith("player/") ||
            route.startsWith("match/")  ||
            route.startsWith("matches/")

    navigate(route) {
        if (!isDynamic) {
            // Với các tab tĩnh thì giữ behavior cũ
            popUpTo(graph.findStartDestination().id) { saveState = true }
            launchSingleTop = true
            restoreState = true
        } else {
            // Với màn hình động: luôn tạo state mới theo ID mới
            launchSingleTop = false
            restoreState = false
            // Không popUpTo/saveState để tránh khôi phục entry cũ với args cũ
        }
    }
}

/* ================= HOME SCREEN ================= */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(onOpenPlayer: (String) -> Unit) {
    var query by rememberSaveable { mutableStateOf("") }   // saveable để không reset
    var error by rememberSaveable { mutableStateOf<String?>(null) }

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

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        // ===== Search field =====
                        OutlinedTextField(
                            value = query,
                            onValueChange = {
                                query = it
                                error = null
                            },
                            singleLine = true,
                            maxLines = 1,
                            leadingIcon = { Icon(Icons.Filled.Search, contentDescription = null) },
                            placeholder = { Text("Enter Player ID…") },
                            textStyle = TextStyle(color = Color.White),
                            visualTransformation = VisualTransformation.None,
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier
                                .weight(1f)
                                .heightIn(min = 56.dp),
                            isError = error != null,
                            supportingText = { error?.let { Text(it, color = Color.Red) } },
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedContainerColor = Color(0xFF142732),
                                unfocusedContainerColor = Color(0xFF142732),
                                focusedBorderColor = AccentBlue,
                                unfocusedBorderColor = Color.Transparent,
                                cursorColor = AccentBlue,
                                focusedTextColor = Color.White,
                                unfocusedTextColor = Color.White,
                                focusedPlaceholderColor = Color.White.copy(alpha = 0.6f),
                                unfocusedPlaceholderColor = Color.White.copy(alpha = 0.6f),
                                focusedLeadingIconColor = Color.White,
                                unfocusedLeadingIconColor = Color.White
                            )
                        )

                        Spacer(Modifier.width(10.dp))
                        Button(
                            onClick = {
                                val input = query.trim()
                                if (input.isEmpty()) error = "Please enter a Steam ID"
                                else onOpenPlayer(input)
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

/* ===== UI Bits ===== */
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
                CompositionLocalProvider(LocalContentColor provides AccentBlue) { icon() }
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
        OutlinedButton(onClick = { }) { Text("Become a Sponsor") }
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
