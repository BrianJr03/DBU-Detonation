package jr.brian.issadetonationapp

import android.media.MediaPlayer
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalTextInputService
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import jr.brian.issadetonationapp.ui.theme.IssaDetonationAppTheme
import jr.brian.issadetonationapp.ui.theme.borderStroke
import jr.brian.issadetonationapp.ui.theme.createPlayer
import jr.brian.issadetonationapp.ui.theme.generateCode
import jr.brian.issadetonationapp.ui.theme.prepareForPlayback
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {
    companion object {
        val vm = CountDownTimerViewModel()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        var code = generateCode(8)
        setContent {
            IssaDetonationAppTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = Color.Black
                ) {
                    var player = MediaPlayer.create(applicationContext, R.raw.intense)

                    LazyColumn(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Top
                    ) {
                        item {
                            val text = rememberSaveable { mutableStateOf("") }
                            val scope = rememberCoroutineScope()

                            val guessedWrong = vm.guessedWrong.collectAsState()
                            val guessedCorrect = vm.guessedCorrect.collectAsState()
                            val isReset = vm.isReset.collectAsState()
                            val isOver = vm.isOver.collectAsState()
                            val timeElapsed = vm.timeElapsed.collectAsState()

                            val focusRequester = remember { FocusRequester() }

                            LaunchedEffect(key1 = 1, block = {
                                focusRequester.requestFocus()
                            })

                            Spacer(modifier = Modifier.height(30.dp))

                            Box(
                                modifier = Modifier
                                    .border(borderStroke(Color.White), shape = RectangleShape)
                                    .background(Color.Black)
                                    .width(200.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    vm.timerText.value,
                                    color = Color.Red,
                                    style = TextStyle(fontSize = 50.sp),
                                    modifier = Modifier.padding(10.dp)
                                )
                            }

                            Spacer(modifier = Modifier.height(50.dp))

                            when (timeElapsed.value) {
                                44 -> {
                                    player.prepareForPlayback {
                                        player = applicationContext.createPlayer(R.raw.war)
                                        player.start()
                                    }
                                }

                                60 -> {
                                    player.prepareForPlayback {
                                        player = applicationContext.createPlayer(R.raw.explosion)
                                        player.start()
                                    }
                                    vm.setIsOver(true)
                                }
                            }

                            CompositionLocalProvider(
                                LocalTextInputService provides null
                            ) {
                                TextField(
                                    value = text.value,
                                    maxLines = 1,
                                    label = {
                                        Text(text = "Enter Code")
                                    },
                                    onValueChange = {
                                        if (it.length <= 8 && !guessedWrong.value) {
                                            text.value = it
                                        }
                                    },
                                    textStyle = TextStyle(fontSize = 50.sp),
                                    modifier = Modifier
                                        .padding(start = 40.dp, end = 40.dp)
                                        .fillMaxWidth()
                                        .focusRequester(focusRequester),
                                    keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
                                    keyboardActions = KeyboardActions(onDone = {
                                        if (isOver.value) {
                                            vm.reset {
                                                text.value = ""
                                                code = generateCode(8)
                                            }
                                        } else {
                                            if (text.value.lowercase() == code.lowercase()) {
                                                if (vm.isPlaying.value) {
                                                    vm.stopCountDownTimer()
                                                    vm.guessCorrect()
                                                    player.prepareForPlayback {
                                                        player = applicationContext.createPlayer(
                                                                R.raw.success
                                                            )
                                                        player.start()
                                                    }
                                                    scope.launch {
                                                        vm.startResetDelay()
                                                    }
                                                } else {
                                                    player =
                                                        applicationContext.createPlayer(R.raw.intense)
                                                    player.start()
                                                    vm.startCountDownTimer()
                                                    text.value = ""
                                                }
                                            } else {
                                                scope.launch {
                                                    vm.guessWrong()
                                                    text.value = ""
                                                }
                                            }
                                        }
                                    }),
                                    isError = guessedWrong.value
                                )
                            }

                            Spacer(modifier = Modifier.height(50.dp))

                            Box(
                                modifier = Modifier
                                    .border(borderStroke(Color.White), shape = RectangleShape)
                                    .background(Color.Black),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    code,
                                    color = Color.Red,
                                    letterSpacing = 20.sp,
                                    style = TextStyle(fontSize = 50.sp),
                                    modifier = Modifier.padding(20.dp)
                                )
                            }

                            AnimatedVisibility(visible = guessedWrong.value) {
                                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                    Spacer(modifier = Modifier.height(50.dp))
                                    Text(
                                        text = "Incorrect Code",
                                        color = Color.Red,
                                        style = TextStyle(fontSize = 40.sp)
                                    )
                                    Text(
                                        text = "Locked for 5 seconds",
                                        color = Color.Red,
                                        style = TextStyle(fontSize = 35.sp)
                                    )
                                }
                            }

                            AnimatedVisibility(visible = isOver.value) {
                                Column {
                                    Spacer(modifier = Modifier.height(50.dp))
                                    Text(
                                        text = "Device Destroyed",
                                        color = Color.Red,
                                        style = TextStyle(fontSize = 40.sp)
                                    )
                                }
                            }

                            AnimatedVisibility(visible = guessedCorrect.value) {
                                Column {
                                    Spacer(modifier = Modifier.height(50.dp))
                                    Text(
                                        text = "Device Disarmed",
                                        color = Color.Green,
                                        style = TextStyle(fontSize = 40.sp)
                                    )
                                }
                            }

                            AnimatedVisibility(visible = isReset.value || isOver.value) {
                                Column {
                                    Spacer(modifier = Modifier.height(50.dp))
                                    Button(
                                        modifier = Modifier.size(100.dp),
                                        onClick = {
                                            vm.reset {
                                                text.value = ""
                                                code = generateCode(8)
                                            }
                                        }) {
                                        Text(text = "Reset")
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}