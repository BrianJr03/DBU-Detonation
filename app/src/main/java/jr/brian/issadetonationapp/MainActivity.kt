package jr.brian.issadetonationapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import jr.brian.issadetonationapp.ui.theme.IssaDetonationAppTheme

class MainActivity : ComponentActivity() {
    companion object {
        val vm = CountDownTimerViewModel()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val code = 8.generateCodeWithThisLength()
        setContent {
            IssaDetonationAppTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = Color.Black
                ) {
                    HomeScreen(context = this, code = code)
                }
            }
        }
    }
}