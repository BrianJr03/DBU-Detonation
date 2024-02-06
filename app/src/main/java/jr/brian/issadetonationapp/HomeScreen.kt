package jr.brian.issadetonationapp

import android.content.Context
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
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
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
    var player = context.createPlayer(R.raw.countdown)
    val isShake = remember { mutableStateOf(false) }
    val detonationCode = rememberSaveable { mutableStateOf(code) }

    LazyColumn(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Top
    ) {
        item {
            val scope = rememberCoroutineScope()
            val text = rememberSaveable { mutableStateOf("") }

            val isOver = MainActivity.vm.isOver.collectAsState()
            val isReset = MainActivity.vm.isReset.collectAsState()

            LaunchedEffect(key1 = 4, block = {
                MainActivity.vm.timeElapsed.collect {
                    if (it == 60) {
                        MainActivity.vm.setIsOver(true)
                    }
                }
            })

            val isIncorrectCode = MainActivity.vm.isIncorrectCode.collectAsState()
            val isCorrectCode = MainActivity.vm.isCorrectCode.collectAsState()

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
                isCodeIncorrect = isIncorrectCode.value,
                onIsOver = {
                    MainActivity.vm.reset {
                        text.value = ""
                        detonationCode.value = 8.generateCodeWithThisLength()
                    }
                },
                onCorrectCode = {
                    if (MainActivity.vm.isPlaying.value) {
                        MainActivity.vm.stopCountDownTimer()
                        MainActivity.vm.onCorrectCode()
                        player.prepareForPlayback {
                            player = context.createPlayer(
                                R.raw.defused
                            )
                            player.start()
                        }
                        scope.launch {
                            MainActivity.vm.startResetDelay()
                        }
                    } else {
                        player.prepareForPlayback {
                            player = context.createPlayer(
                                R.raw.countdown
                            )
                            player.start()
                        }
                        MainActivity.vm.startCountDownTimer()
                        text.value = ""
                    }
                },
                onIncorrectCode = {
                    scope.launch {
                        if (!MainActivity.vm.isIncorrectCode.value) {
                            isShake.value = true
                            MainActivity.vm.onIncorrectCode()
                            text.value = ""
                        }
                    }
                }
            )

            Spacer(modifier = Modifier.height(50.dp))

            DetonationCodeBox(
                detonationCode = detonationCode.value,
                isShake = isShake.value,
                onShakeFinished = {
                    isShake.value = false
                }
            )

            WrongCodeEnteredMsg(wrongEntry = isIncorrectCode.value)

            IsOverMsg(isOver = isOver.value)

            CorrectCodeMsg(
                isCodeCorrect = isCorrectCode.value,
                isReset = isReset.value
            )

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
    isCodeIncorrect: Boolean,
    onIsOver: () -> Unit,
    onCorrectCode: () -> Unit,
    onIncorrectCode: () -> Unit
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

    LaunchedEffect(key1 = 1, block = {
        MainActivity.vm.isReset.collect {
            if (it) {
                resetField(
                    state = textFieldsState,
                    list = frList
                )
            }
        }
    })

    Row {
        textFieldsState.forEachIndexed { index, text ->
            CompositionLocalProvider(
                LocalTextInputService provides null
            ) {
                OutlinedTextField(
                    value = text.value.uppercase(),
                    onValueChange = { newValue ->
                        if (!isCodeIncorrect) {
                            if (index == textFieldsState.lastIndex) {
                                if (newValue.length <= 1) {
                                    textFieldsState = textFieldsState.toMutableList().also { list ->
                                        list[index].value = newValue.uppercase()
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
                                if (textFieldsState.getValue()
                                        .equals(detonationCode.lowercase(), ignoreCase = true)
                                ) {
                                    onCorrectCode()
                                    resetField(
                                        state = textFieldsState,
                                        list = frList
                                    )

                                } else {
                                    onIncorrectCode()
                                    resetField(
                                        state = textFieldsState,
                                        list = frList
                                    )
                                }
                            }
                        }
                    ),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = Color.Red,
                        focusedContainerColor = Color.Black,

                        unfocusedTextColor = Color.Red,
                        unfocusedContainerColor = Color.Black,

                        focusedLabelColor = Color.Red,
                        unfocusedLabelColor = Color.Red,

                        cursorColor = Color.Red,
                        focusedBorderColor = Color.White,
                        unfocusedBorderColor = Color.White
                    ),
                    singleLine = true,
                    maxLines = 1,
                    modifier = Modifier
                        .focusRequester(frList[index])
                        .padding(start = 10.dp)
                        .size(60.dp),
                    isError = isCodeIncorrect
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
            style = TextStyle(fontSize = DEFAULT_FONT_SIZE_LARGE),
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
            style = TextStyle(fontSize = DEFAULT_FONT_SIZE_LARGE),
            modifier = Modifier.padding(20.dp)
        )
    }
}

@Composable
fun WrongCodeEnteredMsg(wrongEntry: Boolean) {
    AnimatedVisibility(visible = wrongEntry) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Spacer(modifier = Modifier.height(DEFAULT_VERTICAL_SPACING))
            Text(
                text = "Incorrect Code",
                color = Color.Red,
                style = TextStyle(fontSize = DEFAULT_FONT_SIZE)
            )
            Text(
                text = "Locked for 5 seconds",
                color = Color.Red,
                style = TextStyle(fontSize = DEFAULT_FONT_SIZE_SMALL)
            )
        }
    }
}

@Composable
fun IsOverMsg(isOver: Boolean) {
    AnimatedVisibility(visible = isOver) {
        Column {
            Spacer(modifier = Modifier.height(DEFAULT_VERTICAL_SPACING))
            Text(
                text = "Device Destroyed",
                color = Color.Red,
                style = TextStyle(fontSize = DEFAULT_FONT_SIZE)
            )
        }
    }
}

@Composable
fun CorrectCodeMsg(
    isCodeCorrect: Boolean,
    isReset: Boolean
) {
    AnimatedVisibility(visible = isCodeCorrect && !isReset) {
        Column {
            Spacer(modifier = Modifier.height(DEFAULT_VERTICAL_SPACING))
            Text(
                text = "Device Disarmed",
                color = Color.Green,
                style = TextStyle(fontSize = DEFAULT_FONT_SIZE)
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
            if (isReset) {
                Spacer(modifier = Modifier.height(10.dp))
            }
            TextButton(
                modifier = Modifier.clip(RectangleShape),
                onClick = {
                    onClick()
                }) {
                Text(text = "Reset", fontSize = DEFAULT_FONT_SIZE, color = Color.Red)
            }
        }
    }
}