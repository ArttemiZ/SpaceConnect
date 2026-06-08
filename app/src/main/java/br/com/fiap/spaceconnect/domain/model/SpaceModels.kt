package br.com.fiap.spaceconnect.domain.model

//APOD domain model
data class AstronomyPicture(
    val date: String,
    val title: String,
    val explanation: String,
    val imageUrl: String,
    val hdImageUrl: String?,
    val mediaType: String,
    val copyright: String?,
    var isFavorite: Boolean = false
)

//NEO domain model
data class NearEarthObject(
    val id: String,
    val name: String,
    val absoluteMagnitude: Double,
    val isPotentiallyHazardous: Boolean,
    val diameterMinKm: Double,
    val diameterMaxKm: Double,
    val closeApproachDate: String,
    val velocityKmh: String,
    val missDistanceLunar: String,
    val missDistanceKm: String,
    val orbitingBody: String,
    var isFavorite: Boolean = false
) {
    val hazardLevel: HazardLevel
        get() = when {
            isPotentiallyHazardous && diameterMaxKm > 0.5 -> HazardLevel.CRITICAL
            isPotentiallyHazardous                         -> HazardLevel.HIGH
            diameterMaxKm > 0.1                            -> HazardLevel.MEDIUM
            else                                           -> HazardLevel.LOW
        }
}

enum class HazardLevel(val label: String, val color: Long) {
    CRITICAL("CRÍTICO",  0xFFEF5350),
    HIGH    ("ALTO",     0xFFFF7043),
    MEDIUM  ("MÉDIO",    0xFFFFC107),
    LOW     ("BAIXO",    0xFF4CAF50)
}

//Mars Photo domain model
data class MarsPhoto(
    val id: Int,
    val sol: Int,
    val cameraName: String,
    val cameraFullName: String,
    val imageUrl: String,
    val earthDate: String,
    val roverName: String,
    val roverStatus: String,
    var isFavorite: Boolean = false
)

//Favorite
data class FavoriteItem(
    val id: String,
    val type: FavoriteType,
    val title: String,
    val imageUrl: String,
    val subtitle: String,
    val savedAt: Long = System.currentTimeMillis()
)

enum class FavoriteType { APOD, NEO, MARS_PHOTO }