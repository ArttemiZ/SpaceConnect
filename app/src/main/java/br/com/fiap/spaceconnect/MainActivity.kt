package br.com.fiap.spaceconnect

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import br.com.fiap.spaceconnect.presentation.navigation.SpaceConnectNavGraph
import br.com.fiap.spaceconnect.ui.theme.SpaceConnectTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        installSplashScreen()
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            SpaceConnectTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = SpaceConnectTheme.colors.background
                ) {
                    SpaceConnectNavGraph()
                }
            }
        }
    }
}