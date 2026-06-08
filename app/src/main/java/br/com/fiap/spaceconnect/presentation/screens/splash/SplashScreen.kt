package br.com.fiap.spaceconnect.presentation.screens.splash

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.EaseOutBack
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import br.com.fiap.spaceconnect.presentation.viewmodel.SplashViewModel
import br.com.fiap.spaceconnect.ui.theme.SpaceColors
import br.com.fiap.spaceconnect.ui.theme.SpaceType
import kotlinx.coroutines.delay

@Composable
fun SplashScreen(
    onNavigateToOnboarding: () -> Unit,
    onNavigateToHome: () -> Unit,
    viewModel: SplashViewModel = hiltViewModel()
) {
    val hasSeenOnboarding by viewModel.hasSeenOnboarding.collectAsState()
    val scale = remember { Animatable(0f) }
    val alpha = remember { Animatable(0f) }

    LaunchedEffect(Unit) {
        scale.animateTo(1f, tween(700, easing = EaseOutBack))
        alpha.animateTo(1f, tween(400))
    }

    LaunchedEffect(hasSeenOnboarding) {
        if (hasSeenOnboarding != null) {
            delay(2000)
            if (hasSeenOnboarding == true) onNavigateToHome()
            else onNavigateToOnboarding()
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.radialGradient(
                    colors = listOf(Color(0xFF1A0A2E), SpaceColors.DeepSpace),
                    radius = 900f
                )
            ),
        contentAlignment = Alignment.Center
    ) {
        // Decorative dots
        Box(modifier = Modifier.fillMaxSize()) {
            repeat(12) { i ->
                val x = (i * 37 % 90 + 5).dp
                val y = (i * 53 % 85 + 5).dp
                Box(
                    modifier = Modifier
                        .offset(x, y)
                        .size((1 + i % 3).dp)
                        .background(Color.White.copy(alpha = 0.3f + (i % 4) * 0.1f), CircleShape)
                )
            }
        }

        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier.alpha(alpha.value)
        ) {
            // Logo container
            Box(
                modifier = Modifier
                    .scale(scale.value)
                    .size(100.dp)
                    .background(
                        Brush.linearGradient(listOf(SpaceColors.IndigoBlue, SpaceColors.PurpleAccent)),
                        RoundedCornerShape(28.dp)
                    ),
                contentAlignment = Alignment.Center
            ) {
                Text("🚀", fontSize = 46.sp)
            }

            Spacer(Modifier.height(28.dp))

            Text(
                "SPACE CONNECT",
                style = SpaceType.displayLarge.copy(fontSize = 26.sp, letterSpacing = 3.sp),
                color = SpaceColors.TextPrimary,
                textAlign = TextAlign.Center
            )
            Spacer(Modifier.height(8.dp))
            Text(
                "Exploração Espacial • FIAP GS 2026",
                style = SpaceType.heroSubtitle,
                color = SpaceColors.IndigoBlue,
                textAlign = TextAlign.Center
            )

            Spacer(Modifier.height(56.dp))

            // Loading indicator
            Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                repeat(3) { i ->
                    Box(
                        modifier = Modifier
                            .size(6.dp)
                            .background(
                                if (i == 0) SpaceColors.IndigoBlue else SpaceColors.CardBorder,
                                CircleShape
                            )
                    )
                }
            }
        }
    }
}
