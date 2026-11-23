package com.lumina.ui

import android.app.WallpaperManager
import android.content.ComponentName
import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.edit
import com.lumina.ui.wallpaper.LuminaWallpaperService

private val Night = Color(0xFF101426)
private val NightCard = Color(0xFF202941)
private val Day = Color(0xFFF4F6FC)
private val DayCard = Color(0xFFFFFFFF)
private val Accent = Color(0xFF8B9DFF)

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val prefs = getSharedPreferences("lumina_preferences", MODE_PRIVATE)
        setContent {
            var dark by remember { mutableStateOf(prefs.getBoolean("dark_mode", true)) }
            LuminaApp(
                dark = dark,
                onThemeChange = {
                    dark = it
                    prefs.edit { putBoolean("dark_mode", it) }
                }
            )
        }
    }
}

@Composable
private fun LuminaApp(dark: Boolean, onThemeChange: (Boolean) -> Unit) {
    val context = LocalContext.current
    var selected by remember { mutableStateOf("Discover") }
    val bg by animateColorAsState(if (dark) Night else Day, label = "background")
    val fg = if (dark) Color(0xFFF5F6FF) else Color(0xFF20243A)
    val card = if (dark) NightCard.copy(alpha = 0.82f) else DayCard.copy(alpha = 0.86f)
    val scheme = if (dark) darkColorScheme(primary = Accent, background = Night, surface = NightCard, onSurface = fg)
        else lightColorScheme(primary = Color(0xFF6478E8), background = Day, surface = DayCard, onSurface = fg)

    MaterialTheme(colorScheme = scheme) {
        Scaffold(
            containerColor = bg,
            bottomBar = {
                NavigationBar(containerColor = card, contentColor = fg) {
                    listOf("Discover", "Wallpapers", "Widgets", "Settings").forEach { item ->
                        val icon = when (item) {
                            "Discover" -> Icons.Rounded.AutoAwesome
                            "Wallpapers" -> Icons.Rounded.Wallpaper
                            "Widgets" -> Icons.Rounded.Widgets
                            else -> Icons.Rounded.Tune
                        }
                        NavigationBarItem(
                            selected = selected == item,
                            onClick = { selected = item },
                            icon = { Icon(icon, contentDescription = item) },
                            label = { Text(item, fontSize = 10.sp) }
                        )
                    }
                }
            }
        ) { padding ->
            Column(
                Modifier.fillMaxSize().background(bg)
                    .padding(padding).padding(horizontal = 20.dp)
                    .verticalScroll(rememberScrollState())
            ) {
                Spacer(Modifier.height(22.dp))
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.SpaceBetween, modifier = Modifier.fillMaxWidth()) {
                    Column {
                        Text("LUMINA", color = fg, fontSize = 25.sp, fontWeight = FontWeight.Black, letterSpacing = 3.sp)
                        Text("Make your screen feel alive", color = fg.copy(alpha = .62f), fontSize = 12.sp)
                    }
                    Box(Modifier.size(44.dp).clip(CircleShape).background(card), contentAlignment = Alignment.Center) {
                        Icon(Icons.Rounded.AutoAwesome, null, tint = Accent)
                    }
                }
                Spacer(Modifier.height(24.dp))
                when (selected) {
                    "Discover" -> {
                        Text("A little more beautiful.", color = fg, fontSize = 24.sp, fontWeight = FontWeight.SemiBold)
                        Text("Personalize your Android, your way.", color = fg.copy(alpha = .62f), fontSize = 13.sp)
                        Spacer(Modifier.height(18.dp))
                        HeroWallpaper(dark)
                        Spacer(Modifier.height(22.dp))
                        SectionTitle("Made for your home screen", fg)
                        FeatureCard("Live wallpaper", "Flowing light, soft glow and floating particles", Icons.Rounded.Waves, card, fg) {
                            openWallpaperPicker(context)
                        }
                        FeatureCard("Glass clock widget", "A minimal clock for your home screen", Icons.Rounded.Schedule, card, fg) {
                            selected = "Widgets"
                        }
                        Spacer(Modifier.height(18.dp))
                        SectionTitle("Appearance", fg)
                        ThemeSwitch(dark, onThemeChange, card, fg)
                    }
                    "Wallpapers" -> {
                        SectionTitle("Wallpapers that breathe", fg)
                        Text("Ambient motion designed to feel calm, not distracting.", color = fg.copy(alpha = .62f), fontSize = 13.sp)
                        Spacer(Modifier.height(18.dp))
                        HeroWallpaper(dark)
                        Spacer(Modifier.height(14.dp))
                        FeatureCard("LUMINA Flow", "Animated gradients and subtle floating particles", Icons.Rounded.Waves, card, fg) {
                            openWallpaperPicker(context)
                        }
                        Button(onClick = { openWallpaperPicker(context) }, modifier = Modifier.fillMaxWidth()) {
                            Icon(Icons.Rounded.CheckCircle, null)
                            Spacer(Modifier.width(8.dp))
                            Text("Preview & apply wallpaper")
                        }
                    }
                    "Widgets" -> {
                        SectionTitle("Widgets", fg)
                        Text("Bring useful details to your home screen.", color = fg.copy(alpha = .62f), fontSize = 13.sp)
                        Spacer(Modifier.height(18.dp))
                        WidgetPreview(dark, card, fg)
                        Spacer(Modifier.height(12.dp))
                        FeatureCard("LUMINA Clock", "A clean clock and date widget. Add it from your launcher's widget menu.", Icons.Rounded.Schedule, card, fg) {}
                        Text("Tip: Long-press an empty area on your home screen, choose Widgets, then select LUMINA Clock.", color = fg.copy(alpha = .65f), fontSize = 12.sp)
                    }
                    else -> {
                        SectionTitle("Settings", fg)
                        ThemeSwitch(dark, onThemeChange, card, fg)
                        FeatureCard("About LUMINA", "A calm, expressive customization toolkit for Android.", Icons.Rounded.Info, card, fg) {}
                        FeatureCard("Compatibility", "Designed for Android 6.0 and newer. Available effects depend on device and launcher support.", Icons.Rounded.PhoneAndroid, card, fg) {}
                    }
                }
                Spacer(Modifier.height(28.dp))
                Text("LUMINA UI  •  EARLY ACCESS", color = fg.copy(alpha = .38f), fontSize = 10.sp, letterSpacing = 1.5.sp)
                Spacer(Modifier.height(18.dp))
            }
        }
    }
}

private fun openWallpaperPicker(context: android.content.Context) {
    val intent = Intent(WallpaperManager.ACTION_CHANGE_LIVE_WALLPAPER).apply {
        putExtra(WallpaperManager.EXTRA_LIVE_WALLPAPER_COMPONENT, ComponentName(context, LuminaWallpaperService::class.java))
    }
    context.startActivity(intent)
}

@Composable
private fun HeroWallpaper(dark: Boolean) {
    val colors = if (dark) listOf(Color(0xFF18264A), Color(0xFF6458B8), Color(0xFF36A6B5))
        else listOf(Color(0xFFB9D7FF), Color(0xFFC9BDFB), Color(0xFFFFD9E7))
    Box(Modifier.fillMaxWidth().height(205.dp).clip(RoundedCornerShape(28.dp)).background(Brush.linearGradient(colors))) {
        Box(Modifier.offset(x = 150.dp, y = 18.dp).size(150.dp).blur(34.dp).background(Color.White.copy(alpha = .36f), CircleShape))
        Box(Modifier.offset(x = (-24).dp, y = 112.dp).size(145.dp).blur(30.dp).background(Color(0xFF8DE7E0).copy(alpha = .5f), CircleShape))
        Column(Modifier.align(Alignment.BottomStart).padding(20.dp)) {
            Text("LUMINA FLOW", color = Color.White.copy(alpha = .82f), fontSize = 10.sp, letterSpacing = 2.sp)
            Text("Breathe in color.", color = Color.White, fontSize = 23.sp, fontWeight = FontWeight.SemiBold)
            Text("A softer kind of motion", color = Color.White.copy(alpha = .8f), fontSize = 12.sp)
        }
        Icon(Icons.Rounded.AutoAwesome, null, tint = Color.White.copy(alpha = .8f), modifier = Modifier.align(Alignment.TopEnd).padding(20.dp))
    }
}

@Composable
private fun SectionTitle(text: String, color: Color) {
    Text(text, color = color, fontSize = 19.sp, fontWeight = FontWeight.SemiBold)
    Spacer(Modifier.height(12.dp))
}

@Composable
private fun FeatureCard(title: String, subtitle: String, icon: androidx.compose.ui.graphics.vector.ImageVector, card: Color, fg: Color, onClick: () -> Unit) {
    Row(
        Modifier.fillMaxWidth().padding(bottom = 10.dp).clip(RoundedCornerShape(20.dp))
            .background(card).clickable(onClick = onClick).padding(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(Modifier.size(44.dp).clip(RoundedCornerShape(14.dp)).background(Accent.copy(alpha = .17f)), contentAlignment = Alignment.Center) {
            Icon(icon, null, tint = Accent)
        }
        Spacer(Modifier.width(14.dp))
        Column(Modifier.weight(1f)) {
            Text(title, color = fg, fontWeight = FontWeight.SemiBold, fontSize = 14.sp)
            Text(subtitle, color = fg.copy(alpha = .62f), fontSize = 11.sp, lineHeight = 16.sp)
        }
        Icon(Icons.Rounded.ChevronRight, null, tint = fg.copy(alpha = .45f))
    }
}

@Composable
private fun ThemeSwitch(dark: Boolean, onChange: (Boolean) -> Unit, card: Color, fg: Color) {
    Row(Modifier.fillMaxWidth().clip(RoundedCornerShape(20.dp)).background(card).padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
        Icon(if (dark) Icons.Rounded.DarkMode else Icons.Rounded.LightMode, null, tint = Accent)
        Spacer(Modifier.width(12.dp))
        Column(Modifier.weight(1f)) {
            Text(if (dark) "Dark appearance" else "Light appearance", color = fg, fontWeight = FontWeight.SemiBold)
            Text("Switch the whole LUMINA interface", color = fg.copy(alpha = .6f), fontSize = 11.sp)
        }
        Switch(checked = dark, onCheckedChange = onChange)
    }
}

@Composable
private fun WidgetPreview(dark: Boolean, card: Color, fg: Color) {
    val gradient = if (dark) listOf(Color(0xFF303D62), Color(0xFF6257A2)) else listOf(Color(0xFFD9E7FF), Color(0xFFE8DFFF))
    Column(Modifier.fillMaxWidth().clip(RoundedCornerShape(24.dp)).background(Brush.linearGradient(gradient)).padding(20.dp)) {
        Text("09:41", color = if (dark) Color.White else Color(0xFF252A45), fontSize = 38.sp, fontWeight = FontWeight.Light)
        Text("Monday  •  24 November", color = if (dark) Color.White.copy(.8f) else Color(0xFF414765), fontSize = 12.sp)
        Spacer(Modifier.height(12.dp))
        Text("A moment to make yours.", color = if (dark) Color.White.copy(.75f) else Color(0xFF414765), fontSize = 11.sp)
    }
}

