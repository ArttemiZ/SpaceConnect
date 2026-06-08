package br.com.fiap.spaceconnect.presentation.screens.detail

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import br.com.fiap.spaceconnect.domain.model.AstronomyPicture
import br.com.fiap.spaceconnect.presentation.viewmodel.HomeViewModel
import br.com.fiap.spaceconnect.ui.theme.SpaceColors
import br.com.fiap.spaceconnect.ui.theme.SpaceType
import coil.compose.AsyncImage

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ApodDetailScreen(
    picture: AstronomyPicture,
    onBack: () -> Unit,
    viewModel: HomeViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val isFavorite = picture.date in uiState.favoriteIds

    Scaffold(containerColor = SpaceColors.DeepSpace) { padding ->
        Box(modifier = Modifier.fillMaxSize().padding(padding)) {
            Column(modifier = Modifier.fillMaxSize().verticalScroll(rememberScrollState())) {

                //imagem
                Box(modifier = Modifier.fillMaxWidth().height(320.dp)) {
                    AsyncImage(
                        model              = picture.hdImageUrl ?: picture.imageUrl,
                        contentDescription = picture.title,
                        contentScale       = ContentScale.Crop,
                        modifier           = Modifier.fillMaxSize()
                    )
                    //Gradiente superior
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(100.dp)
                            .align(Alignment.TopStart)
                            .background(
                                Brush.verticalGradient(
                                    listOf(Color(0xCC09090F), Color.Transparent)
                                )
                            )
                    )

                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(160.dp)
                            .align(Alignment.BottomStart)
                            .background(
                                Brush.verticalGradient(
                                    listOf(Color.Transparent, Color(0xFF09090F))
                                )
                            )
                    )
                    //botão
                    IconButton(
                        onClick  = onBack,
                        modifier = Modifier
                            .padding(12.dp)
                            .align(Alignment.TopStart)
                            .size(38.dp)
                            .background(Color(0x80000000), CircleShape)
                    ) {
                        Icon(
                            Icons.AutoMirrored.Filled.ArrowBack, null,
                            tint = Color.White, modifier = Modifier.size(20.dp)
                        )
                    }
                    //favorito
                    IconButton(
                        onClick  = { viewModel.toggleFavorite(picture) },
                        modifier = Modifier
                            .padding(12.dp)
                            .align(Alignment.TopEnd)
                            .size(38.dp)
                            .background(Color(0x80000000), CircleShape)
                    ) {
                        Icon(
                            if (isFavorite) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                            null,
                            tint = if (isFavorite) SpaceColors.CoralRed else Color.White,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                    //Badge
                    Box(
                        modifier = Modifier
                            .padding(12.dp)
                            .align(Alignment.BottomStart)
                            .background(Color(0xCC4A6CF7), RoundedCornerShape(6.dp))
                            .padding(horizontal = 10.dp, vertical = 4.dp)
                    ) { Text("NASA APOD", style = SpaceType.badge) }
                }

                //Content
                Column(
                    modifier              = Modifier.padding(horizontal = 20.dp, vertical = 16.dp),
                    verticalArrangement   = Arrangement.spacedBy(14.dp)
                ) {
                    //Date
                    Box(
                        modifier = Modifier
                            .background(SpaceColors.IndigoGlow, RoundedCornerShape(8.dp))
                            .padding(horizontal = 12.dp, vertical = 5.dp)
                    ) {
                        Text(picture.date, style = SpaceType.cardDate.copy(fontSize = 12.sp))
                    }

                    //titulo
                    Text(
                        picture.title,
                        style      = SpaceType.heroTitle.copy(fontSize = 20.sp, lineHeight = 28.sp),
                        color      = SpaceColors.TextPrimary
                    )

                    //Copyright
                    if (!picture.copyright.isNullOrBlank()) {
                        Text(
                            "© ${picture.copyright}",
                            style = SpaceType.cardDesc.copy(fontWeight = FontWeight.Medium),
                            color = SpaceColors.IndigoBlueDim
                        )
                    }

                    HorizontalDivider(color = SpaceColors.CardBorder, thickness = 0.5.dp)

                    //Explanation
                    Text(
                        "Sobre esta imagem",
                        style = SpaceType.sectionTitle
                    )
                    Text(
                        picture.explanation,
                        style      = SpaceType.bodyLarge.copy(fontSize = 14.sp, lineHeight = 22.sp),
                        color      = SpaceColors.TextSecondary
                    )

                    Spacer(Modifier.height(24.dp))
                }
            }
        }
    }
}
