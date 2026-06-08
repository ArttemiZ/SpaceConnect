package br.com.fiap.spaceconnect.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp

object SpaceColors {
    val DeepSpace       = Color(0xFF09090F)
    val SpaceNavy       = Color(0xFF0D1B3E)
    val Nebula          = Color(0xFF1A0A2E)
    val CardSurface     = Color(0x0DFFFFFF)  // 5% white
    val CardBorder      = Color(0x14FFFFFF)  // 8% white
    val CardBorderHover = Color(0x66B4C8FF)
    val IndigoBlue      = Color(0xFF4A6CF7)
    val IndigoBlueDim   = Color(0xFF7C9FFF)
    val IndigoGlow      = Color(0x2E4A6CF7)
    val PurpleAccent    = Color(0xFF7C3AED)
    val CoralRed        = Color(0xFFF87171)
    val CoralRedDim     = Color(0x26EF4444)
    val AmberGold       = Color(0xFFFBBF24)
    val AmberGoldDim    = Color(0x26F59E0B)
    val TextPrimary     = Color(0xFFE8EEFF)
    val TextSecondary   = Color(0xFF6A7090)
    val TextHint        = Color(0xFF505070)
    val BadgeBg         = Color(0xCC4A6CF7)
    val NavInactive     = Color(0xFF3C3C5C)
    val Success         = Color(0xFF4CAF50)
    val Error           = Color(0xFFF87171)
    val Warning         = Color(0xFFFBBF24)
}

data class SpaceColorScheme(
    val background: Color       = SpaceColors.DeepSpace,
    val surface: Color          = SpaceColors.CardSurface,
    val surfaceBorder: Color    = SpaceColors.CardBorder,
    val primary: Color          = SpaceColors.IndigoBlue,
    val primaryDim: Color       = SpaceColors.IndigoBlueDim,
    val primaryGlow: Color      = SpaceColors.IndigoGlow,
    val accent: Color           = SpaceColors.PurpleAccent,
    val danger: Color           = SpaceColors.CoralRed,
    val dangerDim: Color        = SpaceColors.CoralRedDim,
    val warning: Color          = SpaceColors.AmberGold,
    val warningDim: Color       = SpaceColors.AmberGoldDim,
    val onSurface: Color        = SpaceColors.TextPrimary,
    val onSurfaceVariant: Color = SpaceColors.TextSecondary,
    val hint: Color             = SpaceColors.TextHint,
    val navInactive: Color      = SpaceColors.NavInactive,
    val success: Color          = SpaceColors.Success,
    val error: Color            = SpaceColors.Error
)

val LocalSpaceColors = staticCompositionLocalOf { SpaceColorScheme() }

private val DarkColorScheme = darkColorScheme(
    primary      = SpaceColors.IndigoBlue,
    secondary    = SpaceColors.PurpleAccent,
    tertiary     = SpaceColors.AmberGold,
    background   = SpaceColors.DeepSpace,
    surface      = SpaceColors.CardSurface,
    onPrimary    = SpaceColors.DeepSpace,
    onSecondary  = SpaceColors.TextPrimary,
    onBackground = SpaceColors.TextPrimary,
    onSurface    = SpaceColors.TextPrimary,
    error        = SpaceColors.CoralRed
)

@Composable
fun SpaceConnectTheme(content: @Composable () -> Unit) {
    CompositionLocalProvider(LocalSpaceColors provides SpaceColorScheme()) {
        MaterialTheme(colorScheme = DarkColorScheme, content = content)
    }
}

object SpaceConnectTheme {
    val colors: SpaceColorScheme
        @Composable get() = LocalSpaceColors.current
}

object SpaceType {
    val heroLabel   = TextStyle(fontSize = 10.sp, fontWeight = FontWeight.SemiBold, color = SpaceColors.IndigoBlue,    letterSpacing = 2.sp)
    val heroTitle   = TextStyle(fontSize = 22.sp, fontWeight = FontWeight.Bold,     color = SpaceColors.TextPrimary,   lineHeight = 28.sp)
    val heroSubtitle= TextStyle(fontSize = 12.sp, fontWeight = FontWeight.Normal,   color = SpaceColors.TextSecondary)
    val displayLarge= TextStyle(fontSize = 32.sp, fontWeight = FontWeight.Bold,     color = SpaceColors.TextPrimary,   letterSpacing = 1.sp)
    val headlineLarge=TextStyle(fontSize = 24.sp, fontWeight = FontWeight.Bold,     color = SpaceColors.TextPrimary)
    val headlineMedium=TextStyle(fontSize=16.sp,  fontWeight = FontWeight.SemiBold, color = SpaceColors.TextPrimary)
    val sectionTitle= TextStyle(fontSize = 14.sp, fontWeight = FontWeight.SemiBold, color = SpaceColors.TextPrimary)
    val cardTitle   = TextStyle(fontSize = 14.sp, fontWeight = FontWeight.Bold,     color = SpaceColors.TextPrimary,   lineHeight = 20.sp)
    val cardDate    = TextStyle(fontSize = 10.sp, fontWeight = FontWeight.Medium,   color = SpaceColors.IndigoBlueDim)
    val cardDesc    = TextStyle(fontSize = 11.sp, fontWeight = FontWeight.Normal,   color = SpaceColors.TextHint,      lineHeight = 16.sp)
    val bodyLarge   = TextStyle(fontSize = 16.sp, fontWeight = FontWeight.Normal,   color = SpaceColors.TextPrimary)
    val bodyMedium  = TextStyle(fontSize = 14.sp, fontWeight = FontWeight.Normal,   color = SpaceColors.TextSecondary)
    val badge       = TextStyle(fontSize = 9.sp,  fontWeight = FontWeight.Bold,     color = Color.White,               letterSpacing = 1.sp)
    val labelMedium = TextStyle(fontSize = 12.sp, fontWeight = FontWeight.Medium,   color = SpaceColors.IndigoBlue,    letterSpacing = 0.5.sp)
    val navLabel    = TextStyle(fontSize = 9.sp,  fontWeight = FontWeight.Medium)
    val quickTitle  = TextStyle(fontSize = 12.sp, fontWeight = FontWeight.SemiBold, color = SpaceColors.TextPrimary)
    val quickSub    = TextStyle(fontSize = 10.sp, fontWeight = FontWeight.Normal,   color = SpaceColors.TextHint)
}
