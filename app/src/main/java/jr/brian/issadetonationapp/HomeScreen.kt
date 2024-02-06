package jr.brian.issadetonationapp

import android.content.Context
import android.media.MediaPlayer
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.platform.LocalTextInputService
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.launch

@Composable
fun HomeScreen(context: Context, code: String) {
    var player = MediaPlayer.create(context, R.raw.intense3)
    val isShake = remember { mutableStateOf(false) }
    val detonationCode = remember { mutableStateOf(code) }

    LazyColumn(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Top
    ) {
        item {
            val scope = rememberCoroutineScope()
            val text = rememberSaveable { mutableStateOf("") }

            val isOver = MainActivity.vm.isOver.collectAsState()
            val isReset = MainActivity.vm.isReset.collectAsState()
            val timeElapsed = MainActivity.vm.timeElapsed.collectAsState()
            val guessedWrong = MainActivity.vm.guessedWrong.collectAsState()
            val guessedCorrect = MainActivity.vm.guessedCorrect.collectAsState()

            player.handleMusicPlayback(
                timeElapsed = timeElapsed.value,
                onPreparedForFirstPlayBack = {
                    player = context.createPlayer(R.raw.intense)
                    player.start()
                },
                onPreparedForSecondPlayBack = {
                    player = context.createPlayer(R.raw.explosion)
                    player.start()
                }
            )

            Spacer(modifier = Modifier.height(30.dp))

            TimerBox(
                isShake = isShake.value,
                onShakeFinished = {
                    isShake.value = false
                }
            )

            Spacer(modifier = Modifier.height(50.dp))

            CustomTextFieldRow(
                detonationCode = detonationCode.value,
                isOver = isOver.value,
                onIsOver = {
                    MainActivity.vm.reset {
                        text.value = ""
                        detonationCode.value = 8.generateCodeWithThisLength()
                    }
                },
                onCodeGuessed = {
                    if (MainActivity.vm.isPlaying.value) {
                        MainActivity.vm.stopCountDownTimer()
                        MainActivity.vm.guessCorrect()
                        player.prepareForPlayback {
                            player = context.createPlayer(
                                R.raw.success
                            )
                            player.start()
                        }
                        scope.launch {
                            MainActivity.vm.startResetDelay()
                        }
                    } else {
                        player =
                            context.createPlayer(R.raw.intense3)
                        player.start()
                        MainActivity.vm.startCountDownTimer()
                        text.value = ""
                    }
                }
            ) {
                scope.launch {
                    if (!MainActivity.vm.guessedWrong.value) {
                        isShake.value = true
                        MainActivity.vm.guessWrong()
                        text.value = ""
                    }
                }
            }

            Spacer(modifier = Modifier.height(50.dp))

            DetonationCodeBox(
                detonationCode = detonationCode.value,
                isShake = isShake.value,
                onShakeFinished = {
                    isShake.value = false
                }
            )

            ImpossibleCodeMsg(guessedWrong = guessedWrong.value)

            IsOverMsg(isOver = isOver.value)

            GuessedCorrectMsg(guessedCorrect = guessedCorrect.value)

            ResetButton(
                isReset = isReset.value,
                isOver = isOver.value,
                onClick = {
                    MainActivity.vm.reset {
                        text.value = ""
                        detonationCode.value = 8.generateCodeWithThisLength()
                    }
                }
            )
        }
    }
}

@Composable
fun CustomTextFieldRow(
    detonationCode: String,
    isOver: Boolean,
    onIsOver: () -> Unit,
    onCodeGuessed: () -> Unit,
    onCodeNotGuessed: () -> Unit
) {
    var textFieldsState by remember {
        mutableStateOf(
            listOf(
                mutableStateOf(""),
                mutableStateOf(""),
                mutableStateOf(""),
                mutableStateOf(""),
                mutableStateOf(""),
                mutableStateOf(""),
                mutableStateOf(""),
                mutableStateOf("")
            )
        )
    }

    val frList = remember { List(8) { FocusRequester() } }

    Row {
        textFieldsState.forEachIndexed { index, text ->
            CompositionLocalProvider(
                LocalTextInputService provides null
            ) {
                TextField(
                    value = text.value,
                    onValueChange = { newValue ->
                        if (index == textFieldsState.lastIndex) {
                            if (newValue.length <= 1) {
                                textFieldsState = textFieldsState.toMutableList().also { list ->
                                    list[index].value = newValue
                                }
                                handleFocus(
                                    index = index,
                                    value = newValue,
                                    frList = frList,
                                    textFieldsState = textFieldsState
                                )
                            }
                        } else {
                            textFieldsState = textFieldsState.toMutableList().also { list ->
                                list[index].value = newValue
                            }
                            handleFocus(
                                index = index,
                                value = newValue,
                                frList = frList,
                                textFieldsState = textFieldsState
                            )
                        }
                    },
                    keyboardOptions = KeyboardOptions.Default.copy(
                        imeAction = if (index == textFieldsState.lastIndex)
                            ImeAction.Done else ImeAction.Next
                    ),
                    keyboardActions = KeyboardActions(
                        onDone = {
                            if (isOver) {
                                onIsOver()
                                resetField(
                                    state = textFieldsState,
                                    list = frList
                                )
                            } else {
                                if (textFieldsState.getValue() == detonationCode.lowercase()) {
                                    onCodeGuessed()
                                    resetField(
                                        state = textFieldsState,
                                        list = frList
                                    )

                                } else {
                                    onCodeNotGuessed()
                                    resetField(
                                        state = textFieldsState,
                                        list = frList
                                    )
                                }
                            }
                        }
                    ),
                    singleLine = true,
                    maxLines = 1,
                    modifier = Modifier
                        .focusRequester(frList[index])
                        .padding(start = 10.dp)
                        .size(60.dp)
                )
            }
        }
    }
}

@Composable
fun TimerBox(
    isShake: Boolean,
    onShakeFinished: () -> Unit
) {
    Box(
        modifier = Modifier
            .border(Color.White.borderStroke(), shape = RectangleShape)
            .background(Color.Black)
            .width(200.dp)
            .shake(isShake) {
                onShakeFinished()
            },
        contentAlignment = Alignment.Center
    ) {
        Text(
            MainActivity.vm.timerText.value,
            color = Color.Red,
            style = TextStyle(fontSize = 50.sp),
            modifier = Modifier.padding(10.dp)
        )
    }
}

@Composable
fun DetonationCodeBox(
    detonationCode: String,
    isShake: Boolean,
    onShakeFinished: () -> Unit
) {
    Box(
        modifier = Modifier
            .border(Color.White.borderStroke(), shape = RectangleShape)
            .background(Color.Black)
            .shake(isShake) {
                onShakeFinished()
            },
        contentAlignment = Alignment.Center
    ) {
        Text(
            detonationCode,
            color = Color.Red,
            letterSpacing = 20.sp,
            style = TextStyle(fontSize = 50.sp),
            modifier = Modifier.padding(20.dp)
        )
    }
}

@Composable
fun ImpossibleCodeMsg(guessedWrong: Boolean) {
    AnimatedVisibility(visible = guessedWrong) {
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
}

@Composable
fun IsOverMsg(isOver: Boolean) {
    AnimatedVisibility(visible = isOver) {
        Column {
            Spacer(modifier = Modifier.height(50.dp))
            Text(
                text = "Device Destroyed",
                color = Color.Red,
                style = TextStyle(fontSize = 40.sp)
            )
        }
    }
}

@Composable
fun GuessedCorrectMsg(guessedCorrect: Boolean) {
    AnimatedVisibility(visible = guessedCorrect) {
        Column {
            Spacer(modifier = Modifier.height(50.dp))
            Text(
                text = "Device Disarmed",
                color = Color.Green,
                style = TextStyle(fontSize = 40.sp)
            )
        }
    }
}

@Composable
fun ResetButton(
    isReset: Boolean,
    isOver: Boolean,
    onClick: () -> Unit
) {
    AnimatedVisibility(visible = isReset || isOver) {
        Column {
            Spacer(modifier = Modifier.height(50.dp))
            Button(
                modifier = Modifier.size(100.dp),
                onClick = {
                    onClick()
                }) {
                Text(text = "Reset")
            }
        }
    }
}