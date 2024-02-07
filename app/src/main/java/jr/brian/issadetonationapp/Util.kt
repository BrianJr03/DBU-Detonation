package jr.brian.issadetonationapp

import androidx.compose.runtime.MutableState
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

val DEFAULT_FONT_SIZE = 30.sp
val DEFAULT_FONT_SIZE_SMALL = 25.sp
val DEFAULT_FONT_SIZE_LARGE = 50.sp

val DEFAULT_VERTICAL_SPACING = 30.dp

const val GAME_SCREEN_ROUTE = "game"
const val HOME_SCREEN_ROUTE = "home"
const val SETTINGS_SCREEN_ROUTE = "settings"

const val CODE_DIGIT_COUNT = 8

fun List<MutableState<String>>.getValue() : String {
    var str = ""
    this.onEach {
        str += it.value
    }
    return str
}

fun handleFocus(
    index: Int,
    value: String,
    frList: List<FocusRequester>,
    textFieldsState: List<MutableState<String>>,
) {
    if (value.isNotEmpty() && index < textFieldsState.lastIndex) {
        // Focus on the next field
        frList[index + 1].requestFocus()
    } else if (value.isEmpty() && index > 0 && textFieldsState[index - 1].value.isNotEmpty()) {
        // Focus on the previous field if current one is empty
        frList[index - 1].requestFocus()
    } else if (value.isEmpty() && index > 0 && textFieldsState[index - 1].value.isEmpty()) {
        // Focus on the previous field if current one is empty after deletion
        frList[index - 1].requestFocus()
    }
}

fun resetField(
    state: List<MutableState<String>>,
    list: List<FocusRequester>
) {
    state.onEach {
        it.value = ""
    }
    list.firstOrNull()?.requestFocus()
}