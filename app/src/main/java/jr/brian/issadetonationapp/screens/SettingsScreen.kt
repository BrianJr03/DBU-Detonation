package jr.brian.issadetonationapp.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import jr.brian.issadetonationapp.AppDataStore
import kotlinx.coroutines.launch

@Composable
fun SettingsScreen(
    dataStore: AppDataStore,
    savedMinutes: String,
    savedSeconds: String
) {
    val scope = rememberCoroutineScope()
    val minutes = remember { mutableStateOf(savedMinutes) }
    val seconds = remember { mutableStateOf(savedSeconds) }

    Row(
        modifier = Modifier.fillMaxSize(),
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically
    ) {
        TextField(
            value = minutes.value,
            onValueChange = {
                minutes.value = it
                scope.launch {
                    dataStore.saveMinutes(it.toIntOrNull().toString())
                }
            },
            label = { Text("Minutes") },
            modifier = Modifier.size(100.dp),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
        )

        Spacer(modifier = Modifier.width(10.dp))

        TextField(
            value = seconds.value,
            onValueChange = {
                seconds.value = it
                scope.launch {
                    dataStore.saveSeconds(it.toIntOrNull().toString())
                }
            },
            label = { Text("Seconds") },
            modifier = Modifier.size(100.dp),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
        )
    }
}