package br.com.fiap.spaceconnect.data.remote.dto

import com.google.gson.annotations.SerializedName

// ── APOD (Astronomy Picture of the Day) ───────────────────────────────────────
data class ApodDto(
    @SerializedName("date")        val date: String,
    @SerializedName("title")       val title: String,
    @SerializedName("explanation") val explanation: String,
    @SerializedName("url")         val url: String,
    @SerializedName("hdurl")       val hdUrl: String?,
    @SerializedName("media_type")  val mediaType: String,
    @SerializedName("copyright")   val copyright: String?
)

// ── Near Earth Objects ─────────────────────────────────────────────────────────
data class NeoFeedDto(
    @SerializedName("element_count")          val elementCount: Int,
    @SerializedName("near_earth_objects")     val nearEarthObjects: Map<String, List<NeoDto>>
)

data class NeoDto(
    @SerializedName("id")                     val id: String,
    @SerializedName("name")                   val name: String,
    @SerializedName("nasa_jpl_url")           val nasaJplUrl: String,
    @SerializedName("absolute_magnitude_h")   val absoluteMagnitude: Double,
    @SerializedName("is_potentially_hazardous_asteroid") val isPotentiallyHazardous: Boolean,
    @SerializedName("estimated_diameter")     val estimatedDiameter: EstimatedDiameterDto,
    @SerializedName("close_approach_data")    val closeApproachData: List<CloseApproachDto>
)

data class EstimatedDiameterDto(
    @SerializedName("kilometers") val kilometers: DiameterRangeDto
)

data class DiameterRangeDto(
    @SerializedName("estimated_diameter_min") val min: Double,
    @SerializedName("estimated_diameter_max") val max: Double
)

data class CloseApproachDto(
    @SerializedName("close_approach_date")        val closeApproachDate: String,
    @SerializedName("relative_velocity")          val relativeVelocity: RelativeVelocityDto,
    @SerializedName("miss_distance")              val missDistance: MissDistanceDto,
    @SerializedName("orbiting_body")              val orbitingBody: String
)

data class RelativeVelocityDto(
    @SerializedName("kilometers_per_hour") val kmPerHour: String
)

data class MissDistanceDto(
    @SerializedName("lunar")      val lunar: String,
    @SerializedName("kilometers") val kilometers: String
)

//Mars fotos
data class MarsRoverResponseDto(
    @SerializedName("photos") val photos: List<MarsPhotoDto>
)

data class MarsPhotoDto(
    @SerializedName("id")          val id: Int,
    @SerializedName("sol")         val sol: Int,
    @SerializedName("camera")      val camera: CameraDto,
    @SerializedName("img_src")     val imgSrc: String,
    @SerializedName("earth_date")  val earthDate: String,
    @SerializedName("rover")       val rover: RoverDto
)

data class CameraDto(
    @SerializedName("id")         val id: Int,
    @SerializedName("name")       val name: String,
    @SerializedName("full_name")  val fullName: String
)

data class RoverDto(
    @SerializedName("id")     val id: Int,
    @SerializedName("name")   val name: String,
    @SerializedName("status") val status: String
)