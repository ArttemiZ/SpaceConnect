package br.com.fiap.spaceconnect.presentation.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import br.com.fiap.spaceconnect.domain.model.AstronomyPicture
import br.com.fiap.spaceconnect.presentation.screens.detail.ApodDetailScreen
import br.com.fiap.spaceconnect.presentation.screens.favorites.FavoritesScreen
import br.com.fiap.spaceconnect.presentation.screens.home.HomeScreen
import br.com.fiap.spaceconnect.presentation.screens.missions.MarsGalleryScreen
import br.com.fiap.spaceconnect.presentation.screens.missions.NeoRadarScreen
import br.com.fiap.spaceconnect.presentation.screens.onboarding.OnboardingScreen
import br.com.fiap.spaceconnect.presentation.screens.splash.SplashScreen

sealed class Screen(val route: String) {
    object Splash      : Screen("splash")
    object Onboarding  : Screen("onboarding")
    object Home        : Screen("home")
    object NeoRadar    : Screen("neo_radar")
    object MarsGallery : Screen("mars_gallery")
    object Favorites   : Screen("favorites")
    object ApodDetail  : Screen("apod_detail")
}

// Guarda o APOD selecionado entre telas (simples, sem serialização em rota)
object NavState {
    var selectedApod: AstronomyPicture? = null
}

@Composable
fun SpaceConnectNavGraph() {
    val navController = rememberNavController()

    NavHost(navController = navController, startDestination = Screen.Splash.route) {

        composable(Screen.Splash.route) {
            SplashScreen(
                onNavigateToOnboarding = {
                    navController.navigate(Screen.Onboarding.route) {
                        popUpTo(Screen.Splash.route) { inclusive = true }
                    }
                },
                onNavigateToHome = {
                    navController.navigate(Screen.Home.route) {
                        popUpTo(Screen.Splash.route) { inclusive = true }
                    }
                }
            )
        }

        composable(Screen.Onboarding.route) {
            OnboardingScreen(
                onFinish = {
                    navController.navigate(Screen.Home.route) {
                        popUpTo(Screen.Onboarding.route) { inclusive = true }
                    }
                }
            )
        }

        composable(Screen.Home.route) {
            HomeScreen(
                onNavigateToNeo       = { navController.navigate(Screen.NeoRadar.route) },
                onNavigateToMars      = { navController.navigate(Screen.MarsGallery.route) },
                onNavigateToFavorites = { navController.navigate(Screen.Favorites.route) },
                onNavigateToApodDetail = { apod ->
                    NavState.selectedApod = apod
                    navController.navigate(Screen.ApodDetail.route)
                }
            )
        }

        composable(Screen.ApodDetail.route) {
            val apod = NavState.selectedApod
            if (apod != null) {
                ApodDetailScreen(
                    picture = apod,
                    onBack  = { navController.popBackStack() }
                )
            } else {
                navController.popBackStack()
            }
        }

        composable(Screen.NeoRadar.route) {
            NeoRadarScreen(onBack = { navController.popBackStack() })
        }

        composable(Screen.MarsGallery.route) {
            MarsGalleryScreen(onBack = { navController.popBackStack() })
        }

        composable(Screen.Favorites.route) {
            FavoritesScreen(onBack = { navController.popBackStack() })
        }
    }
}
