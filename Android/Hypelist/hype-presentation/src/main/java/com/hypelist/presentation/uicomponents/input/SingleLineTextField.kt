@file:OptIn(ExperimentalMaterial3Api::class)

package com.hypelist.presentation.uicomponents.input

import android.app.Activity
import android.content.Intent
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.hypelist.resources.R

@Composable
fun SingleLineTextField(
    modifier: Modifier,
    textValue: MutableState<String>,
    placeholder: String,
    leadingIcon: @Composable (() -> Unit)? = null,
    autofocus: Boolean = false,
    noPadding: Boolean = false,
    forceClear: MutableState<Boolean>? = null,
) {
    val focusRequester = remember { FocusRequester() }

    var textFieldValue by remember {
        val value = TextFieldValue(textValue.value).copy(
            selection = TextRange(textValue.value.length)
        )
        mutableStateOf(value)
    }
    if (forceClear != null && forceClear.value) {
        textFieldValue = TextFieldValue("")
        forceClear.value = false
    }

    val focusManager = LocalFocusManager.current
    val activity = LocalContext.current as Activity

    BasicTextField(
        textStyle = TextStyle(
            fontFamily = FontFamily(Font(R.font.hkgrotesk)),
            fontSize = 14.sp
        ),
        value = textFieldValue,
        onValueChange = { newInput ->
            val newValue = newInput.text
            textValue.value = newValue

            textFieldValue = newInput.copy(
                text = newValue
            )
        },
        modifier = modifier
            .focusRequester(focusRequester)
            .onFocusChanged {
                val intent = Intent("HuaweiKeyboardWorkaround")
                intent.putExtra("HuaweiKeyboardWorkaround", it.isFocused)

                LocalBroadcastManager
                    .getInstance(activity)
                    .sendBroadcast(intent)
            }
            .height(45.dp),
        singleLine = true,
        keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
        keyboardActions = KeyboardActions(
            onDone = {
                focusManager.clearFocus()
            },
            onGo = {},
            onNext = {},
            onPrevious = {},
            onSearch = {},
            onSend = {}
        )
    ) {
        TextFieldDefaults.DecorationBox(
            value = textFieldValue.text,
            innerTextField = it,
            enabled = true,
            singleLine = true,
            visualTransformation = VisualTransformation.None,
            interactionSource = remember { MutableInteractionSource() },
            placeholder = {
                Text(
                    text = placeholder,
                    fontFamily = FontFamily(Font(R.font.hkgrotesk))
                )
            },
            leadingIcon = leadingIcon,
            shape = RoundedCornerShape(8.dp),
            colors = TextFieldDefaults.colors(
                focusedContainerColor = Color.White,
                unfocusedContainerColor = Color.White,
                disabledContainerColor = Color.White,
                focusedIndicatorColor = Color.Transparent,
                unfocusedIndicatorColor = Color.Transparent,
                disabledIndicatorColor = Color.Transparent,
            ),
            contentPadding = if (noPadding) PaddingValues(0.dp) else PaddingValues(start = 16.dp), // this is how you can remove the padding
        )
    }

    LaunchedEffect(Unit) {
        if (autofocus) {
            focusRequester.requestFocus()
        }
    }
}