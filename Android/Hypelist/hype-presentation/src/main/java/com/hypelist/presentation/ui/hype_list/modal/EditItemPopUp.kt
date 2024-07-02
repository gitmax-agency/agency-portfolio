@file:OptIn(ExperimentalMaterialApi::class)

package com.hypelist.presentation.ui.hype_list.modal

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.graphics.BitmapFactory
import android.view.View
import android.view.inputmethod.InputMethodManager
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
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.SideEffect
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
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import androidx.constraintlayout.compose.ConstraintLayout
import com.hypelist.entities.hypelist.Hypelist
import com.hypelist.resources.R
import com.hypelist.presentation.extensions.geocodeCurrentLocation
import com.hypelist.presentation.extensions.resetLocation
import com.hypelist.presentation.extensions.updateEditableItem
import com.hypelist.presentation.extensions.updateHypelistItem
import com.hypelist.presentation.extensions.updateLocationFromItem
import com.hypelist.presentation.ui.hype_list.detail.HypelistViewModel
import com.hypelist.presentation.uicomponents.header.SettingsBar
import com.hypelist.presentation.uicomponents.modal.SimpleAlertDialog
import com.hypelist.presentation.uicomponents.control.SolidButton
import com.hypelist.presentation.uicomponents.footer.SmallDummyFooter
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditItemPopUp(
    hypelist: Hypelist?, hypelistViewModel: HypelistViewModel,
    scaffoldState: BottomSheetScaffoldState, forceReload: MutableState<Boolean>,
    focusRequester: FocusRequester
) {
    val itemToEdit by hypelistViewModel.itemToEdit.collectAsState()
    hypelistViewModel.updateLocationFromItem(itemToEdit)

    val photoCover by hypelistViewModel.photoCoverFlow.collectAsState()
    val currentAddress by hypelistViewModel.geocodedAddressFlow.collectAsState()

    var itemTitle by remember {
        mutableStateOf(TextFieldValue(
            itemToEdit?.name ?: "",
            selection = TextRange(itemToEdit?.name!!.length))
        )
    }
    var itemDescription by remember {
        mutableStateOf(TextFieldValue(itemToEdit?.description ?: ""))
    }

    if (itemToEdit?.name != null) {
        itemTitle = TextFieldValue(
            AnnotatedString(itemToEdit?.name!!),
            selection = TextRange(itemToEdit?.name!!.length)
        )
    }
    if (itemToEdit?.description != null) {
        itemDescription = TextFieldValue(AnnotatedString(itemToEdit?.description!!))
    }

    var note by remember {
        mutableStateOf(TextFieldValue(itemToEdit?.note ?: ""))
    }

    var gps = TextFieldValue(if (currentAddress != null)
        AnnotatedString(currentAddress!!)
    else
        AnnotatedString("Please Wait...")
    )

    var link by remember {
        mutableStateOf(TextFieldValue(itemToEdit?.link ?: ""))
    }

    val showNote = remember {
        mutableStateOf(
            itemToEdit?.note != null && itemToEdit?.note != ""
        )
    }
    val showGPS = remember {
        mutableStateOf(
            itemToEdit?.gpsPlaceName != null && itemToEdit?.gpsPlaceName != ""
        )
    }
    val showLink = remember {
        mutableStateOf(
            itemToEdit?.note != null && itemToEdit?.note != ""
        )
    }

    if (itemToEdit?.note != null) {
        showNote.value = true
        note = TextFieldValue(itemToEdit?.note!!)
    }
    if (currentAddress != null) {
        showGPS.value = true
        gps = TextFieldValue(AnnotatedString(currentAddress!!))
    }
    if (itemToEdit?.link != null) {
        showLink.value = true
        link = TextFieldValue(itemToEdit?.link!!)
    }

    val activity = LocalContext.current as Activity
    //if (!isKeyboardOpen) {
        SettingsBar(title = LocalContext.current.getString(R.string.edit_item), scaffoldState) {
            val inputMethodManager =
                activity.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            inputMethodManager.hideSoftInputFromWindow(activity.currentFocus?.windowToken, 0)
            activity.window.decorView
                .findViewById<View>(android.R.id.content)
                .clearFocus()

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
    //}

    val inputCheckError = remember { mutableStateOf("")  }

    val scope = rememberCoroutineScope()
    SideEffect {
        scope.launch {
            withContext(Dispatchers.IO) {
                if (hypelistViewModel.photoCoverFlow.value == null) {
                    itemToEdit?.id?.let { editableItemID ->
                        val cacheDir = activity.filesDir.absolutePath + "/CachedHypelists"
                        val hypelistCacheDir = "$cacheDir/Hypelist_${hypelist!!.id!!}"
                        val hypelistItemsCacheDir = "$hypelistCacheDir/Items"
                        val hypelistItemCacheDir =
                            "$hypelistItemsCacheDir/Item_${editableItemID}"
                        val hypelistItemCoverPath = "$hypelistItemCacheDir/HypelistItemCover.jpg"

                        if (File(hypelistItemCoverPath).exists()) {
                            val bitmap = BitmapFactory.decodeFile(hypelistItemCoverPath)
                            hypelistViewModel.updateCover(bitmap)
                        }
                    }
                }

                hypelistViewModel.updateEditableItem(null)
            }
        }
    }

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
            hypelistViewModel.resetLocation()
            gps = TextFieldValue(AnnotatedString("Please Wait..."))
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

            TextField(
                modifier = Modifier.focusRequester(focusRequester)
                    .padding(10.dp)
                    .fillMaxWidth(),
                value = itemTitle,
                onValueChange = { newInput ->
                    val newValue = newInput.text

                    itemTitle = newInput.copy(
                        text = newValue
                    )
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

        SolidButton(modifier = Modifier
            .constrainAs(save) {
                end.linkTo(parent.end, 10.dp)
                top.linkTo(parent.top)
                bottom.linkTo(parent.bottom, 30.dp)
            }
            .fillMaxWidth(0.3f)
            .clickable {
                if (itemTitle.text.isNotEmpty()) {
                    scope.launch {
                        focusManager.clearFocus()

                        scaffoldState.bottomSheetState.collapse()
                        hypelistViewModel.updateHypelistItem(
                            hypelist!!,
                            hypelistViewModel.keyToEdit,
                            itemTitle.text,
                            itemDescription.text,
                            if (note.text.isNotEmpty()) note.text else null,
                            if (link.text.isNotEmpty()) link.text else null,
                            forceReload
                        ) {
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
            }, text = LocalContext.current.getString(R.string.save))
    }

    SmallDummyFooter()

    if (inputCheckError.value.isNotEmpty()) {
        SimpleAlertDialog(inputCheckError, LocalContext.current.getString(R.string.bad_new_item_error))
    }
}