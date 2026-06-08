package br.com.fiap.spaceconnect.domain.usecase

import br.com.fiap.spaceconnect.domain.model.AstronomyPicture
import br.com.fiap.spaceconnect.domain.model.FavoriteItem
import br.com.fiap.spaceconnect.domain.model.FavoriteType
import br.com.fiap.spaceconnect.domain.model.MarsPhoto
import br.com.fiap.spaceconnect.domain.model.NearEarthObject
import br.com.fiap.spaceconnect.domain.repository.FavoriteRepository
import br.com.fiap.spaceconnect.domain.repository.NasaRepository
import kotlinx.coroutines.flow.Flow
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import javax.inject.Inject

class GetAstronomyPicturesUseCase @Inject constructor(
    private val repository: NasaRepository
) {
    suspend operator fun invoke(): Result<List<AstronomyPicture>> =
        repository.getAstronomyPictures()
}

class GetNearEarthObjectsUseCase @Inject constructor(
    private val repository: NasaRepository
) {
    suspend operator fun invoke(): Result<List<NearEarthObject>> {
        val formatter  = DateTimeFormatter.ofPattern("yyyy-MM-dd")
        val today      = LocalDate.now()
        val startDate  = today.minusDays(7).format(formatter)
        val endDate    = today.format(formatter)
        return repository.getNearEarthObjects(startDate, endDate)
    }
}

class GetMarsPhotosUseCase @Inject constructor(
    private val repository: NasaRepository
) {
    suspend operator fun invoke(): Result<List<MarsPhoto>> =
        repository.getMarsPhotos()
}

class GetFavoritesUseCase @Inject constructor(
    private val repository: FavoriteRepository
) {
    operator fun invoke(): Flow<List<FavoriteItem>> = repository.getAllFavorites()
}

class ToggleFavoriteApodUseCase @Inject constructor(
    private val repository: FavoriteRepository
) {
    suspend operator fun invoke(picture: AstronomyPicture) {
        val isFav = repository.isFavorite(picture.date)
        if (isFav) {
            repository.removeFavorite(picture.date)
        } else {
            repository.addFavorite(
                FavoriteItem(
                    id       = picture.date,
                    type     = FavoriteType.APOD,
                    title    = picture.title,
                    imageUrl = picture.imageUrl,
                    subtitle = picture.date
                )
            )
        }
    }
}

class ToggleFavoriteNeoUseCase @Inject constructor(
    private val repository: FavoriteRepository
) {
    suspend operator fun invoke(neo: NearEarthObject) {
        val isFav = repository.isFavorite(neo.id)
        if (isFav) {
            repository.removeFavorite(neo.id)
        } else {
            repository.addFavorite(
                FavoriteItem(
                    id       = neo.id,
                    type     = FavoriteType.NEO,
                    title    = neo.name,
                    imageUrl = "",
                    subtitle = "Aproximação: ${neo.closeApproachDate}"
                )
            )
        }
    }
}

class ToggleFavoriteMarsUseCase @Inject constructor(
    private val repository: FavoriteRepository
) {
    suspend operator fun invoke(photo: MarsPhoto) {
        val isFav = repository.isFavorite(photo.id.toString())
        if (isFav) {
            repository.removeFavorite(photo.id.toString())
        } else {
            repository.addFavorite(
                FavoriteItem(
                    id       = photo.id.toString(),
                    type     = FavoriteType.MARS_PHOTO,
                    title    = "${photo.roverName} – ${photo.cameraFullName}",
                    imageUrl = photo.imageUrl,
                    subtitle = "Sol ${photo.sol} • ${photo.earthDate}"
                )
            )
        }
    }
}

class CheckFavoriteUseCase @Inject constructor(
    private val repository: FavoriteRepository
) {
    suspend operator fun invoke(id: String): Boolean = repository.isFavorite(id)
}