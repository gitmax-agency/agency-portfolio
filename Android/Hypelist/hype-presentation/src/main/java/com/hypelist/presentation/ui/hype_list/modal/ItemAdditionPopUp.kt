@file:OptIn(ExperimentalMaterialApi::class, ExperimentalMaterial3Api::class,
    ExperimentalMaterialApi::class
)

package com.hypelist.presentation.ui.hype_list.modal

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.graphics.BitmapFactory
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.BottomSheetScaffoldState
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import androidx.constraintlayout.compose.ConstraintLayout
import com.hypelist.entities.hypelist.Hypelist
import com.hypelist.resources.R
import com.hypelist.presentation.extensions.createHypelistItem
import com.hypelist.presentation.extensions.geocodeCurrentLocation
import com.hypelist.presentation.ui.hype_list.detail.HypelistViewModel
import com.hypelist.presentation.uicomponents.control.SolidButton
import com.hypelist.presentation.uicomponents.footer.SmallDummyFooter
import com.hypelist.presentation.uicomponents.header.SettingsBar
import com.hypelist.presentation.uicomponents.modal.SimpleAlertDialog
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext


@Composable
fun ItemAdditionPopUp(
    hypelist: Hypelist?, hypelistViewModel: HypelistViewModel,
    scaffoldState: BottomSheetScaffoldState, focusRequester: FocusRequester

) {
    val activity = LocalContext.current as ComponentActivity

    val photoCover by hypelistViewModel.photoCoverFlow.collectAsState()
    val currentAddress by hypelistViewModel.geocodedAddressFlow.collectAsState()

    var itemTitle by remember {
        mutableStateOf(TextFieldValue(""))
    }
    var itemDescription by remember {
        mutableStateOf(TextFieldValue(""))
    }

    var note by remember {
        mutableStateOf(TextFieldValue(""))
    }

    var gps = TextFieldValue(if (currentAddress != null)
        AnnotatedString(currentAddress!!)
    else
        AnnotatedString("Please Wait...")
    )

    var link by remember {
        mutableStateOf(TextFieldValue(""))
    }

    val showNote = remember {
        mutableStateOf(false)
    }
    val showGPS = remember {
        mutableStateOf(false)
    }
    val showLink = remember {
        mutableStateOf(false)
    }

    val saveIsActive = remember {
        mutableStateOf(itemTitle.text.isNotEmpty())
    }

    SettingsBar(title = LocalContext.current.getString(R.string.new_item), scaffoldState) {
        saveIsActive.value = false
        hypelistViewModel.justReset()
        itemTitle = TextFieldValue("")
        itemDescription = TextFieldValue("")
        note = TextFieldValue("")
        gps = TextFieldValue("")
        link = TextFieldValue("")
        showNote.value = false
        showGPS.value = false
        showLink.value = false
    }

    val inputCheckError = remember { mutableStateOf("")  }

    val scope = rememberCoroutineScope()

    val launcher = rememberLauncherForActivityResult(ActivityResultContracts.StartActivityForResult()) { result: ActivityResult ->
        if (result.resultCode == Activity.RESULT_OK) {
            val uri = result.data?.data
            if (uri != null) {
                scope.launch {
                    withContext(Dispatchers.IO) {
                        val contentResolver = activity.contentResolver
                        contentResolver.openInputStream(uri).use {
                            val bitmap = BitmapFactory.decodeStream(it)
                            hypelistViewModel.updateCover(bitmap)
                        }
                    }
                }
            }
        }
    }

    val locationLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted: Boolean ->
        if (isGranted) {
            showGPS.value = true
            hypelistViewModel.fetchCurrentLocation() {
                hypelistViewModel.geocodeCurrentLocation(it.latitude, it.longitude)
            }
        }
    }

    val focusManager = LocalFocusManager.current
    Row(
        Modifier
            .fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {

        if (photoCover != null) {
            Image(
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .fillMaxWidth(0.3f)
                    .aspectRatio(1.0f)
                    .padding(10.dp)
                    .clip(RoundedCornerShape(25.dp))
                    .clickable {
                        val intent = Intent()
                        intent.type = "image/*"
                        intent.action = Intent.ACTION_GET_CONTENT
                        launcher.launch(intent)
                    },
                bitmap = photoCover!!.asImageBitmap(),
                contentDescription = "cover")
        } else {
            Image(
                //contentScale = ContentScale.Crop,
                modifier = Modifier
                    .fillMaxWidth(0.3f)
                    .aspectRatio(1.0f)
                    .padding(10.dp)
                    .clickable {
                        val intent = Intent()
                        intent.type = "image/*"
                        intent.action = Intent.ACTION_GET_CONTENT
                        launcher.launch(intent)
                    },
                painter = painterResource(id = R.drawable.additemplaceholder),
                contentDescription = "cover")
        }

        Column(
            Modifier
                .fillMaxWidth(0.7f)
                .padding(10.dp)
                .weight(1f),
            horizontalAlignment = Alignment.Start,
            verticalArrangement = Arrangement.Center) {

            //Text(text = "Upper", modifier = Modifier.padding(20.dp))

            TextField(
                modifier = Modifier
                    .focusRequester(focusRequester)
                    .padding(10.dp)
                    .fillMaxWidth(),
                value = itemTitle,
                onValueChange = { newInput ->
                    val newValue = newInput.text

                    itemTitle = newInput.copy(
                        text = newValue
                    )

                    saveIsActive.value = itemTitle.text.isNotEmpty()
                },
                shape = RoundedCornerShape(10.dp),
                colors = TextFieldDefaults.textFieldColors(
                    focusedIndicatorColor = Color.Transparent,
                    unfocusedIndicatorColor = Color.Transparent,
                    disabledIndicatorColor = Color.Transparent
                ),
                placeholder = { Text(text = "Title") },
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

            TextField(
                modifier = Modifier
                    .padding(10.dp)
                    .fillMaxWidth(),
                value = itemDescription,
                onValueChange = { newInput ->
                    val newValue = newInput.text

                    itemDescription = newInput.copy(
                        text = newValue
                    )
                },
                shape = RoundedCornerShape(10.dp),
                colors = TextFieldDefaults.textFieldColors(
                    focusedIndicatorColor = Color.Transparent,
                    unfocusedIndicatorColor = Color.Transparent,
                    disabledIndicatorColor = Color.Transparent
                ),
                placeholder = { Text(text = "Optional Subtitle") },
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

            //Text(text = "Lower", modifier = Modifier.padding(20.dp))
        }
    }

    if (showNote.value) {
        TextField(
            modifier = Modifier
                .fillMaxWidth()
                .padding(
                    start = 10.dp, end = 10.dp, top = 5.dp, bottom = 15.dp
                ),
            shape = RoundedCornerShape(10.dp),
            colors = TextFieldDefaults.textFieldColors(
                containerColor = Color.White,
                focusedIndicatorColor = Color.Transparent,
                unfocusedIndicatorColor = Color.Transparent,
                disabledIndicatorColor = Color.Transparent
            ),
            value = note,
            onValueChange = { newInput ->
                val newValue = newInput.text

                note = newInput.copy(
                    text = newValue
                )
            },
            placeholder = { Text(text = "Item description") },
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
    }

    if (showGPS.value) {
        TextField(
            modifier = Modifier
                .fillMaxWidth()
                .padding(
                    start = 10.dp, end = 10.dp, top = 5.dp, bottom = 15.dp
                ),
            shape = RoundedCornerShape(10.dp),
            colors = TextFieldDefaults.textFieldColors(
                containerColor = Color.White,
                focusedIndicatorColor = Color.Transparent,
                unfocusedIndicatorColor = Color.Transparent,
                disabledIndicatorColor = Color.Transparent
            ),
            enabled = false,
            value = gps,
            onValueChange = { newInput ->
            },
            placeholder = { Text(text = "") },
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
    }

    if (showLink.value) {
        TextField(
            modifier = Modifier
                .fillMaxWidth()
                .padding(
                    start = 10.dp, end = 10.dp, top = 5.dp, bottom = 15.dp
                ),
            shape = RoundedCornerShape(10.dp),
            colors = TextFieldDefaults.textFieldColors(
                containerColor = Color.White,
                focusedIndicatorColor = Color.Transparent,
                unfocusedIndicatorColor = Color.Transparent,
                disabledIndicatorColor = Color.Transparent
            ),
            value = link,
            onValueChange = { newInput ->
                val newValue = newInput.text

                link = newInput.copy(
                    text = newValue
                )
            },
            placeholder = { Text(text = "Item URL") },
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
    }

    ConstraintLayout(modifier = Modifier.fillMaxWidth()) {
        val (additionButtons, save) = createRefs()

        Row(modifier = Modifier.constrainAs(additionButtons) {
            start.linkTo(parent.start, 10.dp)
            top.linkTo(parent.top)
            bottom.linkTo(parent.bottom, 30.dp)
        }, horizontalArrangement = Arrangement.spacedBy(10.dp),
            verticalAlignment =  Alignment.CenterVertically) {
            for (i in 0 until 3) {
                Image(modifier = Modifier
                    .width(45.dp)
                    .height(45.dp)
                    .clickable {
                        when (i) {
                            0 -> showNote.value = true
                            1 -> locationLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION)
                            2 -> showLink.value = true
                        }
                    }, painter = painterResource(id = when (i) {
                    0 -> R.drawable.additemnote
                    1 -> R.drawable.additemgps
                    else -> R.drawable.additemlink
                }), contentDescription = "img")
            }
        }

        var saveButtonModifier = Modifier
            .constrainAs(save) {
                end.linkTo(parent.end, 10.dp)
                top.linkTo(parent.top)
                bottom.linkTo(parent.bottom, 30.dp)
            }
            .fillMaxWidth(0.3f)
        if (saveIsActive.value) {
            saveButtonModifier = saveButtonModifier.clickable {
                if (itemTitle.text.isNotEmpty()) {
                    scope.launch {
                        focusManager.clearFocus()

                        scaffoldState.bottomSheetState.collapse()
                        hypelistViewModel.createHypelistItem(
                            hypelist!!,
                            itemTitle.text,
                            itemDescription.text,
                            if (note.text.isNotEmpty()) note.text else null,
                            if (link.text.isNotEmpty()) link.text else null) {
                                saveIsActive.value = false
                                hypelistViewModel.resetState()
                                itemTitle = TextFieldValue("")
                                itemDescription = TextFieldValue("")
                                note = TextFieldValue("")
                                gps = TextFieldValue("")
                                link = TextFieldValue("")
                                showNote.value = false
                                showGPS.value = false
                                showLink.value = false
                            }
                    }
                } else {
                    inputCheckError.value = "error"
                }
            }
        }

        SolidButton(modifier = saveButtonModifier, text = LocalContext.current.getString(R.string.save), saveIsActive)
    }

    SmallDummyFooter()

    if (inputCheckError.value.isNotEmpty()) {
        SimpleAlertDialog(inputCheckError, LocalContext.current.getString(R.string.bad_new_item_error))
    }
}