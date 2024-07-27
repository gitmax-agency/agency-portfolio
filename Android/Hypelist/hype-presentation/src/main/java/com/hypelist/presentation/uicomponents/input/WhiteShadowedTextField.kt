@file:OptIn(ExperimentalMaterial3Api::class)

package com.hypelist.presentation.uicomponents.input

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import com.hypelist.resources.R
import com.hypelist.presentation.ui.createoredit.CreateOrEditViewModel

@Composable
fun WhiteShadowedTextField(
    modifier: Modifier, modifierL: Modifier,
    textValue: MutableState<String>, creationViewModel: CreateOrEditViewModel, saveIsActive: MutableState<Boolean>
) {
    val focusRequester = remember { FocusRequester() }

    var textFieldValue by remember {
        val value = TextFieldValue(textValue.value).copy(
            selection = TextRange(textValue.value.length)
        )
        mutableStateOf(value)
    }
    textFieldValue = textFieldValue.copy(
        text = textValue.value,
        selection = TextRange(textValue.value.length)
    )
    val focusManager = LocalFocusManager.current

    TextField(
        modifier = modifier.border(
                width = 1.dp,
                color = Color.Black,
                shape = RoundedCornerShape(15.dp)
            ),
        value = textFieldValue,
        onValueChange = {},
        colors = TextFieldDefaults.colors(
            focusedTextColor = Color.Black,
            unfocusedTextColor = Color.Black,
            focusedContainerColor = Color.Transparent,
            unfocusedContainerColor = Color.Transparent,
            disabledContainerColor = Color.Transparent,
            focusedIndicatorColor = Color.Transparent,
            unfocusedIndicatorColor = Color.Transparent,
            disabledIndicatorColor = Color.Transparent,
        ),
        placeholder = { Text(text = "") },
        //textStyle = LocalTextStyle.current.copy(textAlign = TextAlign.Center),
        singleLine = true
    )
    TextField(
        modifier = modifierL.focusRequester(focusRequester).border(
                width = 1.dp,
                color = Color.White,
                shape = RoundedCornerShape(15.dp)
            ),
        value = textFieldValue,
        onValueChange = { newInput ->
            val newValue = newInput.text
            textValue.value = newValue

            textFieldValue = newInput.copy(
                text = newValue
            )

            creationViewModel.updateName(newValue)
            saveIsActive.value = textFieldValue.text.isNotEmpty()
        },
        colors = TextFieldDefaults.textFieldColors(
            focusedTextColor = Color.White,
            unfocusedTextColor = Color.White,
            containerColor = Color.Transparent,
            focusedIndicatorColor = Color.Transparent,
            unfocusedIndicatorColor = Color.Transparent,
            disabledIndicatorColor = Color.Transparent
        ),
        placeholder = { Text(
            modifier = Modifier.fillMaxWidth(),
            text = LocalContext.current.getString(R.string.creation_placeholder),
            //textAlign = TextAlign.Center,
            //style = LocalTextStyle.current.copy(textAlign = TextAlign.Center)
        ) },
        //textStyle = LocalTextStyle.current.copy(textAlign = TextAlign.Center),
        singleLine = true,
        keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
        keyboardActions = KeyboardActions(
            onDone = {
                focusManager.clearFocus()
            },
            onGo = {},
            onNext = {},
            onPrevious ={},
            onSearch ={},
            onSend = {}
        )
    )

    LaunchedEffect(Unit) {
        focusRequester.requestFocus()
    }
}