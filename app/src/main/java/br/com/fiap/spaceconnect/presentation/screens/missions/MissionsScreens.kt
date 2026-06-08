package br.com.fiap.spaceconnect.presentation.screens.missions

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import br.com.fiap.spaceconnect.presentation.components.*
import br.com.fiap.spaceconnect.presentation.viewmodel.MarsViewModel
import br.com.fiap.spaceconnect.presentation.viewmodel.NeoViewModel
import br.com.fiap.spaceconnect.presentation.viewmodel.UiState
import br.com.fiap.spaceconnect.ui.theme.SpaceColors
import br.com.fiap.spaceconnect.ui.theme.SpaceType
import java.text.DecimalFormat

// NEO Radar Screen
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NeoRadarScreen(onBack: () -> Unit, viewModel: NeoViewModel = hiltViewModel()) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Radar de Asteroides", style = SpaceType.headlineMedium) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, null, tint = SpaceColors.IndigoBlueDim)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = SpaceColors.DeepSpace)
            )
        },
        containerColor = SpaceColors.DeepSpace
    ) { padding ->
        when (val state = uiState.neoState) {
            is UiState.Initial, is UiState.Loading -> LoadingScreen("Rastreando asteroides...")
            is UiState.Error -> ErrorScreen(state.message) { viewModel.loadNeos() }
            is UiState.Success -> {
                val filtered = viewModel.filteredNeos(state.data)
                val fmt = DecimalFormat("#,##0.00")
                LazyColumn(
                    modifier = Modifier.fillMaxSize().padding(padding),
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    // Stats row
                    item {
                        Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                            StatCard(
                                value = "${state.data.size}",
                                label = "Detectados",
                                color = SpaceColors.IndigoBlue,
                                modifier = Modifier.weight(1f)
                            )
                            StatCard(
                                value = "${state.data.count { it.isPotentiallyHazardous }}",
                                label = "Perigosos",
                                color = SpaceColors.CoralRed,
                                modifier = Modifier.weight(1f)
                            )
                        }
                        Spacer(Modifier.height(4.dp))
                        Text("Últimos 7 dias", style = SpaceType.cardDesc, modifier = Modifier.padding(bottom = 8.dp))
                    }

                    // Filter chips
                    item {
                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            SpaceFilterChip("Todos", !uiState.filterHazardous) {
                                if (uiState.filterHazardous) viewModel.toggleHazardousFilter()
                            }
                            SpaceFilterChip("⚠ Perigosos", uiState.filterHazardous) {
                                if (!uiState.filterHazardous) viewModel.toggleHazardousFilter()
                            }
                        }
                    }

                    items(filtered, key = { it.id }) { neo ->
                        NeoCard(
                            name = neo.name,
                            date = neo.closeApproachDate,
                            isPotentiallyHazardous = neo.isPotentiallyHazardous,
                            hazardColor = Color(neo.hazardLevel.color),
                            hazardLabel = neo.hazardLevel.label,
                            diameterKm = "${fmt.format(neo.diameterMinKm)}–${fmt.format(neo.diameterMaxKm)} km",
                            distanceLunar = "${fmt.format(neo.missDistanceLunar.toDoubleOrNull() ?: 0.0)} LD",
                            isFavorite = neo.id in uiState.favoriteIds,
                            onFavoriteToggle = { viewModel.toggleFavorite(neo) }
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun StatCard(value: String, label: String, color: Color, modifier: Modifier = Modifier) {
    Box(
        modifier = modifier
            .background(color.copy(0.1f), RoundedCornerShape(16.dp))
            .padding(vertical = 14.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.spacedBy(2.dp)) {
            Text(value, style = SpaceType.heroTitle, color = color)
            Text(label, style = SpaceType.cardDesc)
        }
    }
}

//Mars Gallery Screen
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MarsGalleryScreen(onBack: () -> Unit, viewModel: MarsViewModel = hiltViewModel()) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Galeria de Marte", style = SpaceType.headlineMedium) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, null, tint = SpaceColors.IndigoBlueDim)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = SpaceColors.DeepSpace)
            )
        },
        containerColor = SpaceColors.DeepSpace
    ) { padding ->
        when (val state = uiState.photosState) {
            is UiState.Initial, is UiState.Loading -> LoadingScreen("Conectando ao Curiosity...")
            is UiState.Error -> ErrorScreen(state.message) { viewModel.loadPhotos() }
            is UiState.Success -> {
                val cameras = viewModel.availableCameras(state.data)
                val filtered = viewModel.filteredPhotos(state.data)
                Column(modifier = Modifier.fillMaxSize().padding(padding)) {
                    LazyRow(
                        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 10.dp),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        items(cameras) { cam ->
                            SpaceFilterChip(cam, uiState.selectedCamera == cam) { viewModel.selectCamera(cam) }
                        }
                    }
                    Text(
                        "${filtered.size} fotos · Rover Curiosity",
                        style = SpaceType.cardDesc,
                        modifier = Modifier.padding(horizontal = 16.dp, vertical = 2.dp)
                    )
                    LazyVerticalGrid(
                        columns = GridCells.Fixed(2),
                        contentPadding = PaddingValues(16.dp),
                        horizontalArrangement = Arrangement.spacedBy(10.dp),
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        items(filtered, key = { it.id }) { photo ->
                            MarsPhotoCard(
                                imageUrl = photo.imageUrl,
                                cameraName = photo.cameraName,
                                sol = photo.sol,
                                earthDate = photo.earthDate,
                                isFavorite = photo.id.toString() in uiState.favoriteIds,
                                onFavoriteToggle = { viewModel.toggleFavorite(photo) }
                            )
                        }
                    }
                }
            }
        }
    }
}
