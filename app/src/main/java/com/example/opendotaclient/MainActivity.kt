package com.example.opendotaclient

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.BorderStroke
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Login
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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

/* --------- Palette --------- */
private val Navy = Color(0xFF123A4A)
private val NavyDark = Color(0xFF0E2D3A)
private val Ink = Color(0xFF0B1C24)
private val AccentBlue = Color(0xFF3BA1E6)
private val TextLight = Color(0xFFEAF2F7)
private val TextDim = Color(0xFFBDD1DE)

/* --------- Typography --------- */
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
        fontSize = 18.sp,
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

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent { OpenDotaHomePage() }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OpenDotaHomePage() {
    var query by remember { mutableStateOf("") }

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
                                fontSize = 18.sp
                            )
                            Spacer(Modifier.width(18.dp))
                            TopNavItem("Matches"); TopNavItem("Heroes"); TopNavItem("Teams")
                            TopNavItem("Explorer"); TopNavItem("Combos"); TopNavItem("Records"); TopNavItem("API")
                            Spacer(Modifier.weight(1f))
                            OutlinedButton(
                                onClick = { /* TODO */ },
                                border = BorderStroke(1.dp, TextLight),
                                contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp),
                                colors = ButtonDefaults.outlinedButtonColors(contentColor = TextLight)
                            ) {
                                Icon(Icons.Default.Login, contentDescription = null, modifier = Modifier.size(16.dp))
                                Spacer(Modifier.width(6.dp))
                                Text("LOGIN")
                            }
                        }
                    }
                )
            },
            containerColor = Ink
        ) { inner ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(inner)
                    .verticalScroll(rememberScrollState())
            ) {
                /* -------- Hero banner -------- */
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(260.dp)
                        .background(NavyDark)
                ) {
                    // Đặt file ảnh vào res/drawable/dota_bg.jpg (đổi id bên dưới nếu khác)
                    Image(
                        painter = painterResource(id = R.drawable.dota2),
                        contentDescription = "Hero background",
                        contentScale = ContentScale.Crop,
                        modifier = Modifier.fillMaxSize()
                    )
                    Box(
                        Modifier
                            .fillMaxSize()
                            .background(
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
                            TextField(
                                value = query,
                                onValueChange = { query = it },
                                singleLine = true,
                                leadingIcon = { Icon(Icons.Default.Search, null) },
                                placeholder = { Text("Search by player name, match ID…") },
                                modifier = Modifier
                                    .weight(1f)
                                    .height(48.dp)
                                    .clip(RoundedCornerShape(6.dp)),
                                colors = TextFieldDefaults.colors(
                                    focusedContainerColor = Color(0xFF142732),
                                    unfocusedContainerColor = Color(0xFF142732),
                                    focusedIndicatorColor = Color.Transparent,
                                    unfocusedIndicatorColor = Color.Transparent,
                                    cursorColor = AccentBlue
                                )
                            )
                            Spacer(Modifier.width(10.dp))
                            OutlinedButton(onClick = { /* TODO */ }, modifier = Modifier.height(48.dp)) {
                                Text("Login")
                            }
                            Spacer(Modifier.width(8.dp))
                            Button(onClick = { /* TODO */ }, modifier = Modifier.height(48.dp)) {
                                Text("Request")
                            }
                        }
                    }
                }

                /* -------- Vertical Info cards (3) -------- */
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

                /* -------- Sponsor -------- */
                SponsorBlock()

                /* -------- Footer -------- */
                FooterBlock()
            }
        }
    }
}

/* -------------------- Components -------------------- */
@Composable
private fun TopNavItem(label: String) {
    Text(
        label,
        style = MaterialTheme.typography.labelSmall,
        color = TextDim,
        modifier = Modifier.padding(end = 14.dp)
    )
}

@Composable
private fun InfoCard(
    icon: @Composable () -> Unit,
    title: String,
    body: String
) {
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
        // Đặt file vào res/drawable/openai_logo.png
        Image(
            painter = painterResource(id = R.drawable.openai),
            contentDescription = "OpenAI logo",
            modifier = Modifier.height(28.dp)
        )
        Spacer(Modifier.height(12.dp))
        OutlinedButton(onClick = { /* TODO */ }) {
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
        Row(horizontalArrangement = Arrangement.spacedBy(12.dp), verticalAlignment = Alignment.CenterVertically) {
            FooterLink("About"); FooterLink("Privacy & Terms"); FooterLink("API Docs")
            FooterLink("Blog"); FooterLink("Translate")
        }
        Spacer(Modifier.height(4.dp))
        Row(horizontalArrangement = Arrangement.spacedBy(12.dp), verticalAlignment = Alignment.CenterVertically) {
            FooterLink("Deploys by Netlify"); FooterLink("A Gravitech LLC Site")
        }
    }
}

@Composable
private fun FooterLink(label: String) {
    Text(text = label, color = AccentBlue, fontSize = 12.sp)
}
