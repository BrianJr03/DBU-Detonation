package jr.brian.issadetonationapp

import android.os.CountDownTimer
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import jr.brian.issadetonationapp.CountDownTimerViewModel.TimeFormatExt.timeFormat
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import java.util.concurrent.TimeUnit

class CountDownTimerViewModel : ViewModel() {

    private var countDownTimer: CountDownTimer? = null

    private val userInputHour = TimeUnit.HOURS.toMillis(0)
    private val userInputMinute = TimeUnit.MINUTES.toMillis(1)
    private val userInputSecond = TimeUnit.SECONDS.toMillis(0)

    private val initialTotalTimeInMillis = userInputHour + userInputMinute + userInputSecond
    var timeLeft = mutableLongStateOf(initialTotalTimeInMillis)
    val timeElapsed = MutableStateFlow(0)
    val countDownInterval = 1000L // 1 seconds is the lowest

    val guessedCorrect = MutableStateFlow(false)
    val guessedWrong = MutableStateFlow(false)

    val isReset = MutableStateFlow(false)
    val isOver = MutableStateFlow(false)

    val timerText = mutableStateOf(timeLeft.longValue.timeFormat())

    val isPlaying = mutableStateOf(false)

    fun setIsOver(boolean: Boolean) {
        isOver.value = boolean
        timeElapsed.value = 0
    }

    suspend fun guessWrong() {
        guessedWrong.value = true
        delay(5000)
        guessedWrong.value = false
    }

    fun guessCorrect() {
        guessedCorrect.value = true
    }

    suspend fun startResetDelay() {
        delay(15000)
        isReset.value = true
    }

    fun reset(action: () -> Unit) {
        timerText.value = initialTotalTimeInMillis.timeFormat()
        isPlaying.value = false
        guessedCorrect.value = false
        guessedWrong.value = false
        isReset.value = false
        isOver.value = false
        timeElapsed.value = 0
        resetCountDownTimer()
        action()
    }

    fun startCountDownTimer() = viewModelScope.launch {
        isPlaying.value = true
        countDownTimer = object : CountDownTimer(timeLeft.longValue, countDownInterval) {
            override fun onTick(currentTimeLeft: Long) {
                timerText.value = currentTimeLeft.timeFormat()
                timeLeft.longValue = currentTimeLeft
                timeElapsed.value += 1
            }

            override fun onFinish() {
                isPlaying.value = false
            }
        }.start()
    }

    fun stopCountDownTimer() = viewModelScope.launch {
        isPlaying.value = false
        countDownTimer?.cancel()
    }

    private fun resetCountDownTimer() = viewModelScope.launch {
        isPlaying.value = false
        countDownTimer?.cancel()
        timerText.value = initialTotalTimeInMillis.timeFormat()
        timeLeft.longValue = initialTotalTimeInMillis
    }

    object TimeFormatExt {
        private const val FORMAT = "%02d:%02d"

        fun Long.timeFormat(): String = String.format(
            FORMAT,
            TimeUnit.MILLISECONDS.toMinutes(this) % 60,
            TimeUnit.MILLISECONDS.toSeconds(this) % 60
        )
    }
}
