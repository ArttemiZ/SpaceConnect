package br.com.fiap.spaceconnect.presentation.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import br.com.fiap.spaceconnect.ui.theme.SpaceColors
import br.com.fiap.spaceconnect.ui.theme.SpaceConnectTheme
import br.com.fiap.spaceconnect.ui.theme.SpaceType
import coil.compose.AsyncImage

// SpaceTopBar
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SpaceTopBar(
    title: String,
    onBack: (() -> Unit)? = null,
    actions: @Composable () -> Unit = {}
) {
    TopAppBar(
        title = { Text(title, style = SpaceType.headlineMedium) },
        navigationIcon = {
            if (onBack != null) {
                IconButton(onClick = onBack) {
                    Box(
                        modifier = Modifier
                            .size(34.dp)
                            .background(SpaceColors.CardSurface, CircleShape)
                            .border(1.dp, SpaceColors.CardBorder, CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Voltar",
                            tint = SpaceColors.IndigoBlueDim,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }
            }
        },
        actions = { actions() },
        colors = TopAppBarDefaults.topAppBarColors(containerColor = SpaceColors.DeepSpace)
    )
}

// LoadingScreen
@Composable
fun LoadingScreen(message: String = "Conectando ao espaço...") {
    Box(
        modifier = Modifier.fillMaxSize().background(SpaceColors.DeepSpace),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.spacedBy(16.dp)) {
            CircularProgressIndicator(color = SpaceColors.IndigoBlue, strokeWidth = 2.dp)
            Text(message, style = SpaceType.bodyMedium)
        }
    }
}

//ErrorScreen
@Composable
fun ErrorScreen(message: String, onRetry: () -> Unit) {
    Box(
        modifier = Modifier.fillMaxSize().background(SpaceColors.DeepSpace),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(12.dp),
            modifier = Modifier.padding(32.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(64.dp)
                    .background(SpaceColors.CoralRedDim, CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(Icons.Default.Warning, null, tint = SpaceColors.CoralRed, modifier = Modifier.size(28.dp))
            }
            Text("Falha na conexão", style = SpaceType.headlineMedium)
            Text(message, style = SpaceType.bodyMedium, color = SpaceColors.TextHint)
            Spacer(Modifier.height(4.dp))
            Button(
                onClick = onRetry,
                shape = RoundedCornerShape(14.dp),
                colors = ButtonDefaults.buttonColors(containerColor = SpaceColors.IndigoGlow)
            ) {
                Icon(Icons.Default.Refresh, null, tint = SpaceColors.IndigoBlue, modifier = Modifier.size(16.dp))
                Spacer(Modifier.width(6.dp))
                Text("Tentar novamente", color = SpaceColors.IndigoBlue)
            }
        }
    }
}

// ApodCard
@Composable
fun ApodCard(
    title: String,
    date: String,
    description: String,
    imageUrl: String,
    isFavorite: Boolean,
    onFavoriteToggle: () -> Unit,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(20.dp))
            .border(1.dp, SpaceColors.CardBorder, RoundedCornerShape(20.dp))
            .background(SpaceColors.CardSurface)
            .clickable(onClick = onClick)
    ) {
        Column {
            // Image with overlays
            Box(modifier = Modifier.fillMaxWidth().height(170.dp)) {
                AsyncImage(
                    model = imageUrl,
                    contentDescription = title,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxSize()
                )
                // Dark gradient overlay bottom
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(
                            Brush.verticalGradient(
                                colors = listOf(Color.Transparent, Color(0xE609090F)),
                                startY = 60f
                            )
                        )
                )
                // NASA badge top-left
                Box(
                    modifier = Modifier
                        .padding(10.dp)
                        .align(Alignment.TopStart)
                        .background(Color(0xCC4A6CF7), RoundedCornerShape(6.dp))
                        .padding(horizontal = 8.dp, vertical = 3.dp)
                ) {
                    Text("NASA APOD", style = SpaceType.badge)
                }
                //Favorite button top-right
                Box(
                    modifier = Modifier
                        .padding(8.dp)
                        .align(Alignment.TopEnd)
                        .size(30.dp)
                        .background(Color(0x73000000), CircleShape)
                        .border(1.dp, Color(0x26FFFFFF), CircleShape)
                        .clickable(onClick = onFavoriteToggle),
                    contentAlignment = Alignment.Center
                ) {
                    val tint by animateColorAsState(
                        if (isFavorite) SpaceColors.CoralRed else Color(0x99FFFFFF),
                        tween(300), label = "fav"
                    )
                    Icon(
                        if (isFavorite) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                        contentDescription = "Favoritar",
                        tint = tint,
                        modifier = Modifier.size(14.dp)
                    )
                }
                // titulo mais o date overlaid at bottom
                Column(
                    modifier = Modifier
                        .align(Alignment.BottomStart)
                        .padding(start = 12.dp, bottom = 10.dp, end = 50.dp)
                ) {
                    Text(title, style = SpaceType.cardTitle, maxLines = 2, overflow = TextOverflow.Ellipsis)
                    Spacer(Modifier.height(2.dp))
                    Text(date, style = SpaceType.cardDate)
                }
            }
            // Footer row: description + arrow
            Row(
                modifier = Modifier.padding(horizontal = 14.dp, vertical = 10.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    description,
                    style = SpaceType.cardDesc,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.weight(1f)
                )
                Spacer(Modifier.width(10.dp))
                Box(
                    modifier = Modifier
                        .size(28.dp)
                        .background(SpaceColors.IndigoGlow, CircleShape)
                        .border(1.dp, SpaceColors.IndigoBlue.copy(alpha = 0.3f), CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        Icons.AutoMirrored.Filled.ArrowForward,
                        null,
                        tint = SpaceColors.IndigoBlueDim,
                        modifier = Modifier.size(14.dp)
                    )
                }
            }
        }
    }
}

//QuickAccessCard
@Composable
fun QuickAccessCard(
    icon: ImageVector,
    iconTint: Color,
    iconBg: Color,
    title: String,
    subtitle: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .width(104.dp)
            .height(98.dp)
            .clip(RoundedCornerShape(18.dp))
            .border(1.dp, SpaceColors.CardBorder, RoundedCornerShape(18.dp))
            .background(SpaceColors.CardSurface)
            .clickable(onClick = onClick)
            .padding(12.dp)
    ) {
        Column(verticalArrangement = Arrangement.SpaceBetween, modifier = Modifier.fillMaxSize()) {
            Box(
                modifier = Modifier
                    .size(36.dp)
                    .background(iconBg, RoundedCornerShape(12.dp)),
                contentAlignment = Alignment.Center
            ) {
                Icon(icon, null, tint = iconTint, modifier = Modifier.size(18.dp))
            }
            Column {
                Text(title, style = SpaceType.quickTitle, maxLines = 1, overflow = TextOverflow.Ellipsis)
                Text(subtitle, style = SpaceType.quickSub)
            }
        }
    }
}

//NeoCard
@Composable
fun NeoCard(
    name: String,
    date: String,
    isPotentiallyHazardous: Boolean,
    hazardColor: Color,
    hazardLabel: String,
    diameterKm: String,
    distanceLunar: String,
    isFavorite: Boolean,
    onFavoriteToggle: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(20.dp))
            .border(
                1.dp,
                if (isPotentiallyHazardous) SpaceColors.CoralRed.copy(0.25f) else SpaceColors.CardBorder,
                RoundedCornerShape(20.dp)
            )
            .background(SpaceColors.CardSurface)
            .padding(16.dp)
    ) {
        Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(name, style = SpaceType.sectionTitle, maxLines = 1, overflow = TextOverflow.Ellipsis)
                    Spacer(Modifier.height(2.dp))
                    Text("Aproximação: $date", style = SpaceType.cardDesc)
                }
                Box(
                    modifier = Modifier
                        .size(34.dp)
                        .background(if (isFavorite) Color(0x26F87171) else SpaceColors.CardSurface, CircleShape)
                        .border(1.dp, if (isFavorite) SpaceColors.CoralRed.copy(0.4f) else SpaceColors.CardBorder, CircleShape)
                        .clickable(onClick = onFavoriteToggle),
                    contentAlignment = Alignment.Center
                ) {
                    val tint by animateColorAsState(
                        if (isFavorite) SpaceColors.CoralRed else SpaceColors.TextSecondary,
                        tween(300), label = "neo_fav"
                    )
                    Icon(
                        if (isFavorite) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                        null, tint = tint, modifier = Modifier.size(15.dp)
                    )
                }
            }
            // Badges
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                SpaceBadge(hazardLabel, hazardColor)
                if (isPotentiallyHazardous) SpaceBadge("⚠ Perigoso", SpaceColors.CoralRed)
            }
            // Info row
            HorizontalDivider(color = SpaceColors.CardBorder, thickness = 0.5.dp)
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                NeoInfoItem("Diâmetro", diameterKm)
                NeoInfoItem("Distância Lunar", distanceLunar)
            }
        }
    }
}

@Composable
fun SpaceBadge(label: String, color: Color) {
    Box(
        modifier = Modifier
            .background(color.copy(alpha = 0.13f), RoundedCornerShape(8.dp))
            .border(1.dp, color.copy(alpha = 0.35f), RoundedCornerShape(8.dp))
            .padding(horizontal = 10.dp, vertical = 4.dp)
    ) {
        Text(label, color = color, fontSize = 10.sp, fontWeight = androidx.compose.ui.text.font.FontWeight.Bold)
    }
}

@Composable
fun NeoInfoItem(label: String, value: String) {
    Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
        Text(label, style = SpaceType.labelMedium)
        Text(value, style = SpaceType.bodyMedium, color = SpaceColors.TextPrimary)
    }
}

//MarsPhotoCard
@Composable
fun MarsPhotoCard(
    imageUrl: String,
    cameraName: String,
    sol: Int,
    earthDate: String,
    isFavorite: Boolean,
    onFavoriteToggle: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(16.dp))
            .border(1.dp, SpaceColors.CardBorder, RoundedCornerShape(16.dp))
            .background(SpaceColors.CardSurface)
    ) {
        Column {
            Box(modifier = Modifier.fillMaxWidth().height(150.dp)) {
                AsyncImage(
                    model = imageUrl,
                    contentDescription = "Foto Marte",
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxSize()
                )
                Box(
                    modifier = Modifier.fillMaxSize().background(
                        Brush.verticalGradient(listOf(Color.Transparent, Color(0xCC09090F)), startY = 60f)
                    )
                )
                Box(
                    modifier = Modifier
                        .padding(6.dp)
                        .align(Alignment.TopEnd)
                        .size(26.dp)
                        .background(Color(0x73000000), CircleShape)
                        .clickable(onClick = onFavoriteToggle),
                    contentAlignment = Alignment.Center
                ) {
                    val tint by animateColorAsState(
                        if (isFavorite) SpaceColors.CoralRed else Color.White,
                        tween(300), label = "mars_fav"
                    )
                    Icon(
                        if (isFavorite) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                        null, tint = tint, modifier = Modifier.size(12.dp)
                    )
                }
                Column(
                    modifier = Modifier.align(Alignment.BottomStart).padding(8.dp)
                ) {
                    Text(cameraName, style = SpaceType.badge.copy(fontSize = 10.sp), color = SpaceColors.IndigoBlueDim)
                    Text("Sol $sol", style = SpaceType.cardTitle.copy(fontSize = 12.sp))
                }
            }
            Text(
                earthDate,
                style = SpaceType.cardDesc,
                modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                maxLines = 1
            )
        }
    }
}

// SpaceFilterChip
@Composable
fun SpaceFilterChip(label: String, selected: Boolean, onClick: () -> Unit) {
    val bg by animateColorAsState(
        if (selected) SpaceColors.IndigoBlue else SpaceColors.CardSurface,
        tween(200), label = "chip_bg"
    )
    val borderColor by animateColorAsState(
        if (selected) SpaceColors.IndigoBlue else SpaceColors.CardBorder,
        tween(200), label = "chip_border"
    )
    val textColor by animateColorAsState(
        if (selected) Color.White else SpaceColors.TextSecondary,
        tween(200), label = "chip_text"
    )
    Box(
        modifier = Modifier
            .background(bg, RoundedCornerShape(50.dp))
            .border(1.dp, borderColor, RoundedCornerShape(50.dp))
            .clickable(onClick = onClick)
            .padding(horizontal = 14.dp, vertical = 7.dp)
    ) {
        Text(label, color = textColor, fontSize = 12.sp, fontWeight = androidx.compose.ui.text.font.FontWeight.Medium)
    }
}

//BottomNavBar
data class NavItem(val icon: ImageVector, val label: String)

@Composable
fun SpaceBottomNavBar(
    selectedIndex: Int,
    items: List<NavItem>,
    onItemSelected: (Int) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(SpaceColors.DeepSpace)
            .border(width = 0.5.dp, color = SpaceColors.CardBorder, shape = RoundedCornerShape(0.dp))
            .padding(vertical = 12.dp),
        horizontalArrangement = Arrangement.SpaceAround
    ) {
        items.forEachIndexed { i, item ->
            val active = i == selectedIndex
            val tint by animateColorAsState(
                if (active) SpaceColors.IndigoBlue else SpaceColors.NavInactive,
                tween(200), label = "nav_$i"
            )
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.clickable { onItemSelected(i) }
            ) {
                Icon(item.icon, null, tint = tint, modifier = Modifier.size(22.dp))
                Spacer(Modifier.height(3.dp))
                Text(item.label, style = SpaceType.navLabel, color = tint)
                if (active) {
                    Spacer(Modifier.height(3.dp))
                    Box(modifier = Modifier.size(4.dp).background(SpaceColors.IndigoBlue, CircleShape))
                }
            }
        }
    }
}
