package br.com.fiap.spaceconnect.presentation.screens.home

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import br.com.fiap.spaceconnect.domain.model.AstronomyPicture
import br.com.fiap.spaceconnect.presentation.components.*
import br.com.fiap.spaceconnect.presentation.viewmodel.HomeViewModel
import br.com.fiap.spaceconnect.presentation.viewmodel.UiState
import br.com.fiap.spaceconnect.ui.theme.SpaceColors
import br.com.fiap.spaceconnect.ui.theme.SpaceType

@Composable
fun HomeScreen(
    onNavigateToNeo: () -> Unit,
    onNavigateToMars: () -> Unit,
    onNavigateToFavorites: () -> Unit,
    onNavigateToApodDetail: (AstronomyPicture) -> Unit = {},
    viewModel: HomeViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    val navItems = listOf(
        NavItem(Icons.Default.Home,       "Início"),
        NavItem(Icons.Default.Place,      "NEO"),
        NavItem(Icons.Default.AccountBox, "Marte"),
        NavItem(Icons.Default.Favorite,   "Favoritos")
    )

    Scaffold(
        containerColor = SpaceColors.DeepSpace,
        bottomBar = {
            SpaceBottomNavBar(
                selectedIndex  = 0,
                items          = navItems,
                onItemSelected = { i ->
                    when (i) {
                        1 -> onNavigateToNeo()
                        2 -> onNavigateToMars()
                        3 -> onNavigateToFavorites()
                    }
                }
            )
        }
    ) { padding ->
        when (val state = uiState.apodState) {
            is UiState.Initial, is UiState.Loading -> LoadingScreen("Conectando à NASA...")
            is UiState.Error -> ErrorScreen(state.message) { viewModel.loadPictures() }
            is UiState.Success -> {
                val filtered = viewModel.filteredPictures(state.data)
                LazyColumn(
                    modifier            = Modifier.fillMaxSize().padding(padding),
                    verticalArrangement = Arrangement.spacedBy(0.dp)
                ) {
                    item { HeroBanner(onNavigateToFavorites) }

                    item {
                        OutlinedTextField(
                            value         = uiState.searchQuery,
                            onValueChange = viewModel::updateSearch,
                            modifier      = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 16.dp, vertical = 14.dp),
                            placeholder = {
                                Text("Buscar imagens do cosmos...", color = SpaceColors.TextHint, fontSize = 13.sp)
                            },
                            leadingIcon = {
                                Icon(Icons.Default.Search, null, tint = SpaceColors.IndigoBlue, modifier = Modifier.size(18.dp))
                            },
                            shape      = RoundedCornerShape(14.dp),
                            singleLine = true,
                            colors     = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor      = SpaceColors.IndigoBlue,
                                unfocusedBorderColor    = SpaceColors.CardBorder,
                                focusedTextColor        = SpaceColors.TextPrimary,
                                unfocusedTextColor      = SpaceColors.TextPrimary,
                                cursorColor             = SpaceColors.IndigoBlue,
                                unfocusedContainerColor = SpaceColors.CardSurface,
                                focusedContainerColor   = SpaceColors.CardSurface
                            )
                        )
                    }

                    item {
                        Row(
                            modifier              = Modifier.fillMaxWidth().padding(horizontal = 16.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment     = Alignment.CenterVertically
                        ) {
                            Text("Explorar", style = SpaceType.sectionTitle)
                            Text("ver todos", style = SpaceType.labelMedium, color = SpaceColors.IndigoBlue, fontSize = 11.sp)
                        }
                        Spacer(Modifier.height(10.dp))
                    }

                    item {
                        LazyRow(
                            contentPadding        = PaddingValues(horizontal = 16.dp),
                            horizontalArrangement = Arrangement.spacedBy(10.dp),
                            modifier              = Modifier.padding(bottom = 20.dp)
                        ) {
                            item {
                                QuickAccessCard(
                                    icon     = Icons.Default.Place,
                                    iconTint = SpaceColors.IndigoBlueDim,
                                    iconBg   = SpaceColors.IndigoGlow,
                                    title    = "Radar NEO",
                                    subtitle = "Asteroides",
                                    onClick  = onNavigateToNeo
                                )
                            }
                            item {
                                QuickAccessCard(
                                    icon     = Icons.Default.AccountBox,
                                    iconTint = SpaceColors.CoralRed,
                                    iconBg   = SpaceColors.CoralRedDim,
                                    title    = "Galeria Marte",
                                    subtitle = "Curiosity",
                                    onClick  = onNavigateToMars
                                )
                            }
                            item {
                                QuickAccessCard(
                                    icon     = Icons.Default.Star,
                                    iconTint = SpaceColors.AmberGold,
                                    iconBg   = SpaceColors.AmberGoldDim,
                                    title    = "Favoritos",
                                    subtitle = "Salvos",
                                    onClick  = onNavigateToFavorites
                                )
                            }
                        }
                    }

                    item {
                        Row(
                            modifier              = Modifier.fillMaxWidth().padding(horizontal = 16.dp),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text("Imagens do Dia · APOD", style = SpaceType.sectionTitle)
                            Text("${filtered.size} fotos", style = SpaceType.cardDesc)
                        }
                        Spacer(Modifier.height(10.dp))
                    }

                    items(filtered, key = { it.date }) { picture ->
                        ApodCard(
                            title            = picture.title,
                            date             = picture.date,
                            description      = picture.explanation.take(90) + "…",
                            imageUrl         = picture.imageUrl,
                            isFavorite       = picture.date in uiState.favoriteIds,
                            onFavoriteToggle = { viewModel.toggleFavorite(picture) },
                            onClick          = { onNavigateToApodDetail(picture) },
                            modifier         = Modifier.padding(horizontal = 16.dp, vertical = 6.dp)
                        )
                    }
                    item { Spacer(Modifier.height(16.dp)) }
                }
            }
        }
    }
}

// ── Hero Banner — Opção A (aurora + planeta roxo) ─────────────────────────────
@Composable
private fun HeroBanner(onNavigateToFavorites: () -> Unit) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(210.dp)
            .drawBehind { drawHeroBackground() }
    ) {
        // ── Aurora (brilho nebulosa no canto superior direito) ─────────────
        Box(
            modifier = Modifier
                .align(Alignment.TopEnd)
                .size(220.dp)
                .background(
                    Brush.radialGradient(
                        colors = listOf(
                            Color(0x47633EDC), // roxo vibrante semi-transparente
                            Color(0x1A3B6CF7), // azul índigo suave
                            Color.Transparent
                        ),
                        center = Offset.Unspecified,
                        radius = 300f
                    )
                )
        )

        // Planeta roxo
        Box(
            modifier = Modifier
                .align(Alignment.TopEnd)
                .offset(x = 22.dp, y = (-20).dp)
                .size(210.dp)
                .background(
                    Brush.radialGradient(
                        colors = listOf(
                            Color(0xFF6B3FA0), // roxo claro (topo do planeta)
                            Color(0xFF3A1870), // roxo médio
                            Color(0xFF160840), // roxo escuro
                            Color(0xFF080318)  // quase preto (borda)
                        ),
                        center = Offset(140f, 120f),
                        radius = 200f
                    ),
                    CircleShape
                )
        ) {
            // Cratera 1
            Box(
                modifier = Modifier
                    .offset(x = 28.dp, y = 22.dp)
                    .size(13.dp)
                    .background(Color(0x12FFFFFF), CircleShape)
            )
            // Cratera 2
            Box(
                modifier = Modifier
                    .offset(x = 14.dp, y = 42.dp)
                    .size(8.dp)
                    .background(Color(0x0AFFFFFF), CircleShape)
            )
            // Highlight (reflexo de luz)
            Box(
                modifier = Modifier
                    .offset(x = 20.dp, y = 14.dp)
                    .size(18.dp)
                    .background(
                        Brush.radialGradient(
                            listOf(Color(0x22FFFFFF), Color.Transparent)
                        ),
                        CircleShape
                    )
            )
        }

        //Anel do planeta
        Box(
            modifier = Modifier
                .align(Alignment.TopEnd)
                .offset(x = 46.dp, y = 32.dp)
                .width(178.dp)
                .height(80.dp)
                .background(
                    Brush.horizontalGradient(
                        listOf(
                            Color.Transparent,
                            Color(0x2A8250DC),
                            Color(0x3DA060F0),
                            Color(0x2A8250DC),
                            Color.Transparent
                        )
                    ),
                    RoundedCornerShape(50)
                )
        )
        // Linha superior do anel
        Box(
            modifier = Modifier
                .align(Alignment.TopEnd)
                .offset(x = 46.dp, y = 72.dp)
                .width(178.dp)
                .height(1.dp)
                .background(
                    Brush.horizontalGradient(
                        listOf(
                            Color.Transparent,
                            Color(0x55A070FF),
                            Color(0x88C090FF),
                            Color(0x55A070FF),
                            Color.Transparent
                        )
                    )
                )
        )

        //Top bar
        Column(modifier = Modifier.fillMaxSize()) {
            Row(
                modifier              = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 18.dp, vertical = 14.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment     = Alignment.CenterVertically
            ) {
                // Logo + nome
                Row(
                    verticalAlignment     = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(9.dp)
                ) {
                    // Logo: quadrado com gradiente índigo → roxo
                    Box(
                        modifier = Modifier
                            .size(32.dp)
                            .background(
                                Brush.linearGradient(
                                    listOf(Color(0xFF4A6CF7), Color(0xFF7C3AED))
                                ),
                                RoundedCornerShape(10.dp)
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Text("🚀", fontSize = 15.sp)
                    }

                    // Nome — peso bold, espaçamento sutil
                    Text(
                        text  = "Space Connect",
                        style = SpaceType.headlineMedium.copy(
                            fontSize      = 16.sp,
                            letterSpacing = 0.3.sp,
                            color         = Color(0xFFE8EEFF)
                        )
                    )
                }

                // Botão favoritos — círculo semitransparente
                Box(
                    modifier = Modifier
                        .size(34.dp)
                        .background(Color(0x1AFFFFFF), CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    IconButton(onClick = onNavigateToFavorites) {
                        Icon(
                            Icons.Default.Favorite, null,
                            tint     = SpaceColors.CoralRed,
                            modifier = Modifier.size(17.dp)
                        )
                    }
                }
            }

            Spacer(Modifier.weight(1f))

            // Texto hero
            Column(modifier = Modifier.padding(start = 20.dp, bottom = 20.dp)) {
                Text(
                    "NASA · TEMPO REAL",
                    style = SpaceType.heroLabel.copy(letterSpacing = 2.sp, fontSize = 9.sp)
                )
                Spacer(Modifier.height(5.dp))
                Text(
                    "Explore o Cosmos",
                    style = SpaceType.heroTitle.copy(fontSize = 24.sp)
                )
                Spacer(Modifier.height(3.dp))
                Text(
                    "Dados espaciais ao alcance das suas mãos",
                    style = SpaceType.heroSubtitle.copy(fontSize = 11.sp)
                )
            }
        }
    }
}

// Canvas: gradiente de fundo + estrelas
private fun DrawScope.drawHeroBackground() {
    // Fundo: azul-marinho escuro → roxo profundo
    drawRect(
        brush = Brush.linearGradient(
            colors = listOf(
                Color(0xFF080B1A), // quase preto azulado
                Color(0xFF0D1638), // azul-marinho
                Color(0xFF150D30)  // roxo escuro
            ),
            start = Offset(0f, 0f),
            end   = Offset(size.width, size.height)
        )
    )

    // Estrelas — pontos brancos espalhados
    val stars = listOf(
        Triple(0.07f, 0.15f, 2.2f),
        Triple(0.18f, 0.07f, 1.4f),
        Triple(0.30f, 0.25f, 1.0f),
        Triple(0.12f, 0.50f, 1.8f),
        Triple(0.22f, 0.68f, 1.2f),
        Triple(0.42f, 0.12f, 1.4f),
        Triple(0.10f, 0.35f, 1.0f),
        Triple(0.48f, 0.55f, 1.0f),
        Triple(0.35f, 0.42f, 0.8f),
        Triple(0.16f, 0.80f, 1.4f),
        Triple(0.52f, 0.38f, 0.8f),
        Triple(0.06f, 0.88f, 1.0f),
        Triple(0.40f, 0.75f, 0.8f),
        Triple(0.25f, 0.88f, 1.0f),
    )
    stars.forEach { (rx, ry, radius) ->
        drawCircle(
            color  = Color.White.copy(alpha = 0.45f + (radius / 10f)),
            radius = radius,
            center = Offset(size.width * rx, size.height * ry)
        )
    }
}