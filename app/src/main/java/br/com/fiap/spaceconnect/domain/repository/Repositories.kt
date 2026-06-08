package br.com.fiap.spaceconnect.domain.repository

import br.com.fiap.spaceconnect.domain.model.AstronomyPicture
import br.com.fiap.spaceconnect.domain.model.FavoriteItem
import br.com.fiap.spaceconnect.domain.model.MarsPhoto
import br.com.fiap.spaceconnect.domain.model.NearEarthObject
import kotlinx.coroutines.flow.Flow

interface NasaRepository {
    suspend fun getAstronomyPictures(): Result<List<AstronomyPicture>>
    suspend fun getNearEarthObjects(startDate: String, endDate: String): Result<List<NearEarthObject>>
    suspend fun getMarsPhotos(): Result<List<MarsPhoto>>
}

interface FavoriteRepository {
    fun getAllFavorites(): Flow<List<FavoriteItem>>
    suspend fun isFavorite(id: String): Boolean
    suspend fun addFavorite(item: FavoriteItem)
    suspend fun removeFavorite(id: String)
}