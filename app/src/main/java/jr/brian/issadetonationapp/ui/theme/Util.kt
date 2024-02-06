package jr.brian.issadetonationapp.ui.theme

import android.annotation.SuppressLint
import android.content.Context
import android.media.MediaPlayer
import android.widget.Toast
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.repeatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.debugInspectorInfo
import androidx.compose.ui.unit.dp

fun generateCode(length: Int): String {
    val characters = listOf(
        "A", "B", "C", "D", "E", "F", "G", "H", "I", "J", "K", "L", "M",
        "N", "O", "P", "Q", "R", "S", "T", "U", "V", "W", "X", "Y", "Z",
        "0", "1", "2", "3", "4", "5", "6", "7", "8", "9",
        // Extra numbers so there is total of 26 letters and 26 numbers
        "0", "1", "2", "3", "4", "5", "6", "7", "8", "9",
        "0", "1", "2", "7", "8", "9"
    )

    var str = ""

    for (i in 1..length) {
        str += characters.random()
    }

    return str
}

@Suppress("unused")
fun Context.showToast(msg: String) {
    Toast.makeText(this, msg, Toast.LENGTH_SHORT).show()
}

@Suppress("unused")
fun Context.showLongToast(msg: String) {
    Toast.makeText(this, msg, Toast.LENGTH_LONG).show()
}

fun Context.createPlayer(media: Int) : MediaPlayer {
    return MediaPlayer.create(this, media)
}

fun MediaPlayer.prepareForPlayback(action: () -> Unit) {
    stop()
    release()
    action()
}

fun borderStroke(color: Color): BorderStroke {
    return BorderStroke(
        width = 2.dp,
        brush = Brush.horizontalGradient(
            0.0f to color,
            1.0f to color,
            startX = 0.0f,
            endX = 100.0f
        )
    )
}

@SuppressLint("ModifierFactoryUnreferencedReceiver")
fun Modifier.shake(enabled: Boolean, onAnimationFinish: () -> Unit) = composed(
    factory = {
        val distance by animateFloatAsState(
            targetValue = if (enabled) 15f else 0f,
            animationSpec = repeatable(
                iterations = 8,
                animation = tween(durationMillis = 50, easing = LinearEasing),
                repeatMode = RepeatMode.Reverse
            ),
            finishedListener = { onAnimationFinish.invoke() },
            label = "shake"
        )

        Modifier.graphicsLayer {
            translationX = if (enabled) distance else 0f
        }
    },
    inspectorInfo = debugInspectorInfo {
        name = "shake"
        properties["enabled"] = enabled
    }
)