package com.hypelist.presentation.uicomponents.modal

import androidx.compose.foundation.layout.Column
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext

@Composable
fun SimpleAlertDialog(
    inputCheckError: MutableState<String>,
    errorText: String,
    customTitle: String? = null,
) {
    MaterialTheme {
        Column {
            val openDialog = remember { mutableStateOf(true)  }

            if (openDialog.value) {
                AlertDialog(
                    onDismissRequest = {
                        openDialog.value = false
                    },
                    title = {
                        Text(
                            text = customTitle ?: LocalContext.current.getString(com.hypelist.resources.R.string.bad_input_title)
                        )
                    },
                    text = {
                        Text(errorText)
                    },
                    confirmButton = {
                    },
                    dismissButton = {
                        Button(
                            onClick = {
                                openDialog.value = false
                                inputCheckError.value = ""
                            }) {
                            Text(LocalContext.current.getString(com.hypelist.resources.R.string.bad_input_close))
                        }
                    }
                )
            }
        }

    }
}