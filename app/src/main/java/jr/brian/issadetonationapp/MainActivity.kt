package jr.brian.issadetonationapp

import android.content.Context
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import jr.brian.issadetonationapp.screens.GameScreen
import jr.brian.issadetonationapp.screens.HomeScreen
import jr.brian.issadetonationapp.screens.SettingsScreen
import jr.brian.issadetonationapp.ui.theme.IssaDetonationAppTheme

class MainActivity : ComponentActivity() {
    companion object {
        val vm = MainViewModel()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val dataStore = AppDataStore(this)
        setContent {
            IssaDetonationAppTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = Color.Black
                ) {
                    val savedMinutes = dataStore.getMinutes.collectAsState(initial = "1").value
                        ?: "1"

                    val savedSeconds =
                        dataStore.getSeconds.collectAsState(initial = "0").value
                            ?: "0"

                    val savedSoundPath =
                        dataStore.getSoundPath.collectAsState(initial = "").value
                            ?: ""

                    App(
                        savedSoundPath = savedSoundPath,
                        savedMinutes = savedMinutes,
                        savedSeconds = savedSeconds,
                        dataStore = dataStore
                    )
                }
            }
        }
    }
}

@Composable
fun Context.App(
    savedSoundPath: String,
    savedMinutes: String,
    savedSeconds: String,
    dataStore: AppDataStore
) {
    val navController = rememberNavController()
    NavHost(
        navController = navController,
        startDestination = HOME_SCREEN_ROUTE,
        builder = {
            composable(
                route = HOME_SCREEN_ROUTE,
                content = {
                    HomeScreen(navController = navController)
                }
            )

            composable(
                route = GAME_SCREEN_ROUTE,
                content = {
                    BackHandler(true) {
                        // Prevents player from using back on Game Screen
                    }
                    GameScreen(
                        context = this@App,
                        code = CODE_DIGIT_COUNT.generateCodeWithThisLength(),
                        onNavToHome = {
                            navController.navigate(HOME_SCREEN_ROUTE) {
                                launchSingleTop = true
                            }
                        }
                    )
                }
            )

            composable(
                route = SETTINGS_SCREEN_ROUTE,
                content = {
                    SettingsScreen(
                        dataStore = dataStore,
                        savedMinutes = savedMinutes,
                        savedSeconds = savedSeconds
                    )
                }
            )
        }
    )
}