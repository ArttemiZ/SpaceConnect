package br.com.fiap.spaceconnect.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import br.com.fiap.spaceconnect.domain.model.AstronomyPicture
import br.com.fiap.spaceconnect.domain.model.FavoriteItem
import br.com.fiap.spaceconnect.domain.model.MarsPhoto
import br.com.fiap.spaceconnect.domain.model.NearEarthObject
import br.com.fiap.spaceconnect.domain.usecase.CheckFavoriteUseCase
import br.com.fiap.spaceconnect.domain.usecase.GetAstronomyPicturesUseCase
import br.com.fiap.spaceconnect.domain.usecase.GetFavoritesUseCase
import br.com.fiap.spaceconnect.domain.usecase.GetMarsPhotosUseCase
import br.com.fiap.spaceconnect.domain.usecase.GetNearEarthObjectsUseCase
import br.com.fiap.spaceconnect.domain.usecase.ToggleFavoriteApodUseCase
import br.com.fiap.spaceconnect.domain.usecase.ToggleFavoriteMarsUseCase
import br.com.fiap.spaceconnect.domain.usecase.ToggleFavoriteNeoUseCase
import br.com.fiap.spaceconnect.utils.PreferencesManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

//UiState sealed class
sealed class UiState<out T> {
    object Initial  : UiState<Nothing>()
    object Loading  : UiState<Nothing>()
    data class Success<T>(val data: T) : UiState<T>()
    data class Error(val message: String) : UiState<Nothing>()
}

//SplashViewModel
@HiltViewModel
class SplashViewModel @Inject constructor(
    private val prefs: PreferencesManager
) : ViewModel() {

    private val _hasSeenOnboarding = MutableStateFlow<Boolean?>(null)
    val hasSeenOnboarding: StateFlow<Boolean?> = _hasSeenOnboarding.asStateFlow()

    init {
        viewModelScope.launch {
            _hasSeenOnboarding.value = prefs.hasSeenOnboarding.first()
        }
    }
}

//OnboardingViewModel
@HiltViewModel
class OnboardingViewModel @Inject constructor(
    private val prefs: PreferencesManager
) : ViewModel() {
    fun completeOnboarding() {
        viewModelScope.launch { prefs.setOnboardingCompleted() }
    }
}

//HomeViewModel (APOD)
data class HomeUiState(
    val apodState: UiState<List<AstronomyPicture>> = UiState.Initial,
    val searchQuery: String = "",
    val favoriteIds: Set<String> = emptySet()
)

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val getAstronomyPictures: GetAstronomyPicturesUseCase,
    private val toggleFavoriteApod: ToggleFavoriteApodUseCase,
    private val getFavorites: GetFavoritesUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(HomeUiState())
    val uiState: StateFlow<HomeUiState> = _uiState.asStateFlow()

    init {
        loadPictures()
        observeFavorites()
    }

    private fun observeFavorites() {
        viewModelScope.launch {
            getFavorites().collect { list ->
                _uiState.update { it.copy(favoriteIds = list.map { f -> f.id }.toSet()) }
            }
        }
    }

    fun loadPictures() {
        viewModelScope.launch {
            _uiState.update { it.copy(apodState = UiState.Loading) }
            getAstronomyPictures()
                .onSuccess { pics -> _uiState.update { it.copy(apodState = UiState.Success(pics)) } }
                .onFailure { e  -> _uiState.update { it.copy(apodState = UiState.Error(e.message ?: "Erro desconhecido")) } }
        }
    }

    fun updateSearch(query: String) = _uiState.update { it.copy(searchQuery = query) }

    fun toggleFavorite(picture: AstronomyPicture) {
        viewModelScope.launch { toggleFavoriteApod(picture) }
    }

    fun filteredPictures(pictures: List<AstronomyPicture>): List<AstronomyPicture> {
        val q = _uiState.value.searchQuery.lowercase()
        return if (q.isBlank()) pictures
        else pictures.filter { it.title.lowercase().contains(q) || it.date.contains(q) }
    }
}

//NeoViewModel
data class NeoUiState(
    val neoState: UiState<List<NearEarthObject>> = UiState.Initial,
    val filterHazardous: Boolean = false,
    val favoriteIds: Set<String> = emptySet()
)

@HiltViewModel
class NeoViewModel @Inject constructor(
    private val getNearEarthObjects: GetNearEarthObjectsUseCase,
    private val toggleFavoriteNeo: ToggleFavoriteNeoUseCase,
    private val getFavorites: GetFavoritesUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(NeoUiState())
    val uiState: StateFlow<NeoUiState> = _uiState.asStateFlow()

    init {
        loadNeos()
        observeFavorites()
    }

    private fun observeFavorites() {
        viewModelScope.launch {
            getFavorites().collect { list ->
                _uiState.update { it.copy(favoriteIds = list.map { f -> f.id }.toSet()) }
            }
        }
    }

    fun loadNeos() {
        viewModelScope.launch {
            _uiState.update { it.copy(neoState = UiState.Loading) }
            getNearEarthObjects()
                .onSuccess { neos -> _uiState.update { it.copy(neoState = UiState.Success(neos)) } }
                .onFailure { e   -> _uiState.update { it.copy(neoState = UiState.Error(e.message ?: "Erro")) } }
        }
    }

    fun toggleHazardousFilter() = _uiState.update { it.copy(filterHazardous = !it.filterHazardous) }

    fun toggleFavorite(neo: NearEarthObject) {
        viewModelScope.launch { toggleFavoriteNeo(neo) }
    }

    fun filteredNeos(neos: List<NearEarthObject>): List<NearEarthObject> =
        if (_uiState.value.filterHazardous) neos.filter { it.isPotentiallyHazardous } else neos
}

//MarsViewModel
data class MarsUiState(
    val photosState: UiState<List<MarsPhoto>> = UiState.Initial,
    val selectedCamera: String = "ALL",
    val favoriteIds: Set<String> = emptySet()
)

@HiltViewModel
class MarsViewModel @Inject constructor(
    private val getMarsPhotos: GetMarsPhotosUseCase,
    private val toggleFavoriteMars: ToggleFavoriteMarsUseCase,
    private val getFavorites: GetFavoritesUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(MarsUiState())
    val uiState: StateFlow<MarsUiState> = _uiState.asStateFlow()

    init {
        loadPhotos()
        observeFavorites()
    }

    private fun observeFavorites() {
        viewModelScope.launch {
            getFavorites().collect { list ->
                _uiState.update { it.copy(favoriteIds = list.map { f -> f.id }.toSet()) }
            }
        }
    }

    fun loadPhotos() {
        viewModelScope.launch {
            _uiState.update { it.copy(photosState = UiState.Loading) }
            getMarsPhotos()
                .onSuccess { photos -> _uiState.update { it.copy(photosState = UiState.Success(photos)) } }
                .onFailure { e     -> _uiState.update { it.copy(photosState = UiState.Error(e.message ?: "Erro")) } }
        }
    }

    fun selectCamera(camera: String) = _uiState.update { it.copy(selectedCamera = camera) }

    fun toggleFavorite(photo: MarsPhoto) {
        viewModelScope.launch { toggleFavoriteMars(photo) }
    }

    fun filteredPhotos(photos: List<MarsPhoto>): List<MarsPhoto> {
        val cam = _uiState.value.selectedCamera
        return if (cam == "ALL") photos else photos.filter { it.cameraName == cam }
    }

    fun availableCameras(photos: List<MarsPhoto>): List<String> =
        listOf("ALL") + photos.map { it.cameraName }.distinct().sorted()
}

//FavoritesViewModel
@HiltViewModel
class FavoritesViewModel @Inject constructor(
    private val getFavorites: GetFavoritesUseCase,
    private val favoriteRepo: br.com.fiap.spaceconnect.domain.repository.FavoriteRepository
) : ViewModel() {

    val favorites: StateFlow<List<FavoriteItem>> = MutableStateFlow<List<FavoriteItem>>(emptyList()).also { flow ->
        viewModelScope.launch {
            getFavorites().collect { flow.value = it }
        }
    }.asStateFlow()

    fun removeFavorite(id: String) {
        viewModelScope.launch { favoriteRepo.removeFavorite(id) }
    }
}
