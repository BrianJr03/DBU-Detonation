package jr.brian.issadetonationapp.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.navigation.NavController
import jr.brian.issadetonationapp.DEFAULT_FONT_SIZE_LARGE
import jr.brian.issadetonationapp.GAME_SCREEN_ROUTE
import jr.brian.issadetonationapp.SETTINGS_SCREEN_ROUTE

@Composable
fun HomeScreen(navController: NavController) {
    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        TextButton(
            onClick = {
                navController.navigate(GAME_SCREEN_ROUTE) {
                    launchSingleTop = true
                }
            }) {
            Text(
                text = "Start Game",
                fontSize = DEFAULT_FONT_SIZE_LARGE,
                color = Color.Red
            )
        }
        TextButton(
            onClick = {
                navController.navigate(SETTINGS_SCREEN_ROUTE) {
                    launchSingleTop = true
                }
            }) {
            Text(
                text = "Settings",
                fontSize = DEFAULT_FONT_SIZE_LARGE,
                color = Color.Red
            )

        }
    }
}