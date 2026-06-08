package br.com.fiap.spaceconnect.presentation.screens.onboarding

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import br.com.fiap.spaceconnect.presentation.viewmodel.OnboardingViewModel
import br.com.fiap.spaceconnect.ui.theme.SpaceColors
import br.com.fiap.spaceconnect.ui.theme.SpaceType

private data class OnboardingPage(
    val emoji: String,
    val title: String,
    val description: String,
    val accentColor: Color
)

private val pages = listOf(
    OnboardingPage("🚀", "Space Connect", "Conecte-se ao ecossistema espacial em tempo real. Dados reais da NASA ao alcance das suas mãos — alinhado ao desafio FIAP Space Connect 2026.", SpaceColors.IndigoBlue),
    OnboardingPage("🌌", "Imagens do Universo", "Explore a Astronomy Picture of the Day da NASA — uma nova foto deslumbrante do cosmos todos os dias com descrições científicas detalhadas.", SpaceColors.PurpleAccent),
    OnboardingPage("☄️", "Radar de Asteroides", "Monitore objetos próximos à Terra em tempo real. Veja quais asteroides são potencialmente perigosos com classificação de risco atualizada.", SpaceColors.CoralRed),
    OnboardingPage("🔴", "Galeria de Marte", "Veja fotos reais tiradas pelo rover Curiosity em Marte. Filtre por câmera e salve suas imagens favoritas do Planeta Vermelho.", SpaceColors.AmberGold)
)

@Composable
fun OnboardingScreen(
    onFinish: () -> Unit,
    viewModel: OnboardingViewModel = hiltViewModel()
) {
    var currentPage by remember { mutableIntStateOf(0) }
    val page = pages[currentPage]

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(SpaceColors.DeepSpace)
    ) {
        // Background glow per page
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.radialGradient(
                        colors = listOf(page.accentColor.copy(alpha = 0.08f), Color.Transparent),
                        radius = 700f
                    )
                )
        )

        Column(
            modifier = Modifier.fillMaxSize().padding(horizontal = 28.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Spacer(Modifier.height(60.dp))

            AnimatedContent(
                targetState = currentPage,
                transitionSpec = {
                    slideInHorizontally { it } togetherWith slideOutHorizontally { -it }
                },
                label = "onboarding"
            ) { idx ->
                val p = pages[idx]
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(20.dp)
                ) {
                    // Emoji in styled container
                    Box(
                        modifier = Modifier
                            .size(110.dp)
                            .background(
                                Brush.radialGradient(
                                    listOf(p.accentColor.copy(0.2f), Color.Transparent)
                                ),
                                CircleShape
                            )
                            .clip(CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Box(
                            modifier = Modifier
                                .size(80.dp)
                                .background(p.accentColor.copy(0.12f), CircleShape)
                                .clip(CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(p.emoji, fontSize = 40.sp)
                        }
                    }

                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Text(p.title, style = SpaceType.headlineLarge, textAlign = TextAlign.Center)
                        Text(
                            p.description,
                            style = SpaceType.bodyLarge,
                            textAlign = TextAlign.Center,
                            color = SpaceColors.TextSecondary,
                            lineHeight = 24.sp
                        )
                    }
                }
            }

            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(28.dp)
            ) {
                // Dots indicator
                Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    pages.forEachIndexed { i, _ ->
                        val isActive = i == currentPage
                        Box(
                            modifier = Modifier
                                .width(if (isActive) 22.dp else 6.dp)
                                .height(6.dp)
                                .clip(RoundedCornerShape(3.dp))
                                .background(
                                    if (isActive) pages[currentPage].accentColor else SpaceColors.CardBorder
                                )
                        )
                    }
                }

                // Navigation
                Row(
                    modifier = Modifier.fillMaxWidth().padding(bottom = 48.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Back button
                    Box(
                        modifier = Modifier
                            .size(46.dp)
                            .background(
                                if (currentPage > 0) SpaceColors.CardSurface else Color.Transparent,
                                CircleShape
                            )
                            .clip(CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        if (currentPage > 0) {
                            IconButton(onClick = { currentPage-- }) {
                                Icon(
                                    Icons.AutoMirrored.Filled.ArrowBack, null,
                                    tint = SpaceColors.TextSecondary, modifier = Modifier.size(20.dp)
                                )
                            }
                        }
                    }

                    // Skip
                    if (currentPage < pages.lastIndex) {
                        TextButton(onClick = {
                            viewModel.completeOnboarding()
                            onFinish()
                        }) {
                            Text("Pular", color = SpaceColors.TextSecondary, fontSize = 13.sp)
                        }
                    } else {
                        Spacer(Modifier.width(64.dp))
                    }

                    // Next / Start button
                    Button(
                        onClick = {
                            if (currentPage < pages.lastIndex) currentPage++
                            else {
                                viewModel.completeOnboarding()
                                onFinish()
                            }
                        },
                        shape = RoundedCornerShape(50.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = page.accentColor),
                        modifier = Modifier.height(46.dp)
                    ) {
                        Text(
                            if (currentPage == pages.lastIndex) "Começar!" else "Próximo",
                            color = Color.White,
                            fontWeight = androidx.compose.ui.text.font.FontWeight.SemiBold
                        )
                        Spacer(Modifier.width(4.dp))
                        Icon(
                            Icons.AutoMirrored.Filled.ArrowForward, null,
                            tint = Color.White, modifier = Modifier.size(16.dp)
                        )
                    }
                }
            }
        }
    }
}
