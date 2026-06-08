package br.com.fiap.spaceconnect.presentation.screens.favorites

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import br.com.fiap.spaceconnect.domain.model.FavoriteItem
import br.com.fiap.spaceconnect.domain.model.FavoriteType
import br.com.fiap.spaceconnect.presentation.viewmodel.FavoritesViewModel
import br.com.fiap.spaceconnect.ui.theme.SpaceColors
import br.com.fiap.spaceconnect.ui.theme.SpaceType
import coil.compose.AsyncImage

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FavoritesScreen(onBack: () -> Unit, viewModel: FavoritesViewModel = hiltViewModel()) {
    val favorites by viewModel.favorites.collectAsStateWithLifecycle()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Meus Favoritos", style = SpaceType.headlineMedium) },
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
        if (favorites.isEmpty()) {
            Box(
                modifier = Modifier.fillMaxSize().padding(padding),
                contentAlignment = Alignment.Center
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(90.dp)
                            .background(SpaceColors.IndigoGlow, CircleShape),
                        contentAlignment = Alignment.Center
                    ) { Text("🌌", fontSize = 40.sp) }
                    Text("Nenhum favorito ainda", style = SpaceType.headlineMedium, textAlign = TextAlign.Center)
                    Text(
                        "Explore o cosmos e salve o que te inspirar!",
                        style = SpaceType.bodyMedium,
                        textAlign = TextAlign.Center
                    )
                }
            }
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxSize().padding(padding),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                item {
                    Text("${favorites.size} item(s) salvos", style = SpaceType.cardDesc)
                    Spacer(Modifier.height(4.dp))
                }
                items(favorites, key = { it.id }) { fav ->
                    FavoriteItemCard(fav) { viewModel.removeFavorite(fav.id) }
                }
            }
        }
    }
}

@Composable
private fun FavoriteItemCard(item: FavoriteItem, onDelete: () -> Unit) {
    val (emoji, label, accentColor) = when (item.type) {
        FavoriteType.APOD       -> Triple("🌌", "APOD", SpaceColors.IndigoBlue)
        FavoriteType.NEO        -> Triple("☄️", "Asteroide", SpaceColors.AmberGold)
        FavoriteType.MARS_PHOTO -> Triple("🔴", "Marte", SpaceColors.CoralRed)
    }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(SpaceColors.CardSurface, RoundedCornerShape(18.dp))
            .padding(12.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        // Image or emoji fallback
        Box(
            modifier = Modifier.size(60.dp).clip(RoundedCornerShape(14.dp)).background(accentColor.copy(0.12f)),
            contentAlignment = Alignment.Center
        ) {
            if (item.imageUrl.isNotBlank()) {
                AsyncImage(
                    model = item.imageUrl,
                    contentDescription = null,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxSize().clip(RoundedCornerShape(14.dp))
                )
            } else {
                Text(emoji, fontSize = 26.sp)
            }
        }

        Column(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(3.dp)) {
            // Type badge
            Box(
                modifier = Modifier
                    .background(accentColor.copy(0.13f), RoundedCornerShape(6.dp))
                    .padding(horizontal = 8.dp, vertical = 2.dp)
            ) {
                Text(label, style = SpaceType.badge.copy(fontSize = 9.sp), color = accentColor)
            }
            Text(item.title, style = SpaceType.sectionTitle, maxLines = 2, overflow = TextOverflow.Ellipsis)
            Text(item.subtitle, style = SpaceType.cardDesc, maxLines = 1, overflow = TextOverflow.Ellipsis)
        }

        // Delete button
        Box(
            modifier = Modifier
                .size(34.dp)
                .background(SpaceColors.CoralRed.copy(0.1f), CircleShape),
            contentAlignment = Alignment.Center
        ) {
            IconButton(onClick = onDelete, modifier = Modifier.size(34.dp)) {
                Icon(Icons.Default.Delete, null, tint = SpaceColors.CoralRed, modifier = Modifier.size(16.dp))
            }
        }
    }
}
