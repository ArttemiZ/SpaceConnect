package br.com.fiap.spaceconnect.data.repository

import br.com.fiap.spaceconnect.BuildConfig
import br.com.fiap.spaceconnect.data.model.FavoriteDao
import br.com.fiap.spaceconnect.data.model.toDomain
import br.com.fiap.spaceconnect.data.model.toEntity
import br.com.fiap.spaceconnect.data.remote.api.NasaApiService
import br.com.fiap.spaceconnect.domain.model.AstronomyPicture
import br.com.fiap.spaceconnect.domain.model.FavoriteItem
import br.com.fiap.spaceconnect.domain.model.MarsPhoto
import br.com.fiap.spaceconnect.domain.model.NearEarthObject
import br.com.fiap.spaceconnect.domain.repository.FavoriteRepository
import br.com.fiap.spaceconnect.domain.repository.NasaRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import javax.inject.Inject

class NasaRepositoryImpl @Inject constructor(
    private val api: NasaApiService
) : NasaRepository {

    private val apiKey = BuildConfig.NASA_API_KEY

    override suspend fun getAstronomyPictures(): Result<List<AstronomyPicture>> = runCatching {
        api.getAstronomyPictureOfDay(apiKey = apiKey, count = 15).map { dto ->
            AstronomyPicture(
                date        = dto.date,
                title       = dto.title,
                explanation = dto.explanation,
                imageUrl    = dto.url.replace("http://", "https://"),
                hdImageUrl  = dto.hdUrl?.replace("http://", "https://"),
                mediaType   = dto.mediaType,
                copyright   = dto.copyright
            )
        }.filter { it.mediaType == "image" }
    }

    override suspend fun getNearEarthObjects(
        startDate: String,
        endDate: String
    ): Result<List<NearEarthObject>> = runCatching {
        val response = api.getNearEarthObjects(startDate, endDate, apiKey)
        response.nearEarthObjects.values.flatten().map { dto ->
            val approach = dto.closeApproachData.firstOrNull()
            NearEarthObject(
                id                     = dto.id,
                name                   = dto.name.removePrefix("(").removeSuffix(")"),
                absoluteMagnitude      = dto.absoluteMagnitude,
                isPotentiallyHazardous = dto.isPotentiallyHazardous,
                diameterMinKm          = dto.estimatedDiameter.kilometers.min,
                diameterMaxKm          = dto.estimatedDiameter.kilometers.max,
                closeApproachDate      = approach?.closeApproachDate ?: startDate,
                velocityKmh            = approach?.relativeVelocity?.kmPerHour ?: "N/A",
                missDistanceLunar      = approach?.missDistance?.lunar ?: "N/A",
                missDistanceKm         = approach?.missDistance?.kilometers ?: "N/A",
                orbitingBody           = approach?.orbitingBody ?: "Earth"
            )
        }.sortedByDescending { it.isPotentiallyHazardous }
    }

    override suspend fun getMarsPhotos(): Result<List<MarsPhoto>> = runCatching {

        val sols = listOf(3500, 3000, 2500, 2000, 1500, 1000)
        var photos = emptyList<MarsPhoto>()

        for (sol in sols) {
            val result = runCatching {
                api.getMarsRoverPhotos(sol = sol, page = 1, apiKey = apiKey).photos.map { dto ->
                    MarsPhoto(
                        id             = dto.id,
                        sol            = dto.sol,
                        cameraName     = dto.camera.name,
                        cameraFullName = dto.camera.fullName,
                        imageUrl       = dto.imgSrc.replace("http://", "https://"),
                        earthDate      = dto.earthDate,
                        roverName      = dto.rover.name,
                        roverStatus    = dto.rover.status
                    )
                }
            }
            photos = result.getOrNull() ?: emptyList()
            if (photos.isNotEmpty()) break
        }


        if (photos.isEmpty()) {
            photos = marsPhotosFallback()
        }

        photos
    }


    private fun marsPhotosFallback(): List<MarsPhoto> = listOf(
        MarsPhoto(1, 1000, "FHAZ", "Front Hazard Avoidance Camera",
            "https://mars.nasa.gov/msl-raw-images/proj/msl/redops/ods/surface/sol/01000/opgs/edr/fcam/FLB_486265257EDR_F0481570FHAZ00323M_.JPG",
            "2015-05-30", "Curiosity", "active"),
        MarsPhoto(2, 1000, "RHAZ", "Rear Hazard Avoidance Camera",
            "https://mars.nasa.gov/msl-raw-images/proj/msl/redops/ods/surface/sol/01000/opgs/edr/rcam/RLB_486265291EDR_F0481570RHAZ00323M_.JPG",
            "2015-05-30", "Curiosity", "active"),
        MarsPhoto(3, 1000, "MAST", "Mast Camera",
            "https://mars.nasa.gov/msl-raw-images/proj/msl/redops/ods/surface/sol/01000/opgs/edr/ccam/CR0_486265262EDR_F0481570CCAM02000M_.JPG",
            "2015-05-30", "Curiosity", "active"),
        MarsPhoto(4, 2000, "NAVCAM", "Navigation Camera",
            "https://mars.nasa.gov/msl-raw-images/proj/msl/redops/ods/surface/sol/02000/opgs/edr/ncam/NLB_554668923EDR_F0632840NCAM00353M_.JPG",
            "2018-02-22", "Curiosity", "active"),
        MarsPhoto(5, 2000, "MAST", "Mast Camera",
            "https://mars.nasa.gov/msl-raw-images/proj/msl/redops/ods/surface/sol/02000/opgs/edr/mcam/0002ML0105680010501614C00_DXXX.jpg",
            "2018-02-22", "Curiosity", "active"),
        MarsPhoto(6, 3000, "FHAZ", "Front Hazard Avoidance Camera",
            "https://mars.nasa.gov/msl-raw-images/proj/msl/redops/ods/surface/sol/03000/opgs/edr/fcam/FLB_636136044EDR_F0810628FHAZ00341M_.JPG",
            "2020-11-16", "Curiosity", "active"),
    )
}

class FavoriteRepositoryImpl @Inject constructor(
    private val dao: FavoriteDao
) : FavoriteRepository {

    override fun getAllFavorites(): Flow<List<FavoriteItem>> =
        dao.getAllFavorites().map { list -> list.map { it.toDomain() } }

    override suspend fun isFavorite(id: String): Boolean = dao.isFavorite(id)

    override suspend fun addFavorite(item: FavoriteItem) = dao.insert(item.toEntity())

    override suspend fun removeFavorite(id: String) = dao.deleteById(id)
}
