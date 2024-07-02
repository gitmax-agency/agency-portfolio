@file:OptIn(ExperimentalMaterial3Api::class, ExperimentalGlideComposeApi::class)

package com.hypelist.presentation.ui.profile.my.edit

import android.app.Activity
import android.content.Intent
import android.graphics.BitmapFactory
import android.util.DisplayMetrics
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
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
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.navigation.NavHostController
import com.bumptech.glide.integration.compose.ExperimentalGlideComposeApi
import com.bumptech.glide.integration.compose.GlideImage
import com.hypelist.resources.R
import com.hypelist.presentation.ui.profile.my.MyProfileViewModel
import com.hypelist.presentation.uicomponents.control.SolidButton
import com.hypelist.presentation.uicomponents.footer.DummyFooter
import com.hypelist.presentation.uicomponents.header.ContentsSectionHeader
import com.hypelist.presentation.uicomponents.header.TopTitleBar
import com.hypelist.presentation.uicomponents.image.ProfileImage
import org.koin.androidx.compose.koinViewModel

@Composable
fun EditProfileScreen(
    navController: NavHostController
) {
    val activity = LocalContext.current as Activity
    val displayMetrics: DisplayMetrics = activity.resources.displayMetrics
    val height = displayMetrics.heightPixels
    val density = displayMetrics.density

    val profileViewModel = koinViewModel<MyProfileViewModel>()
    val photoCover by profileViewModel.userCoverFlow.collectAsState()
    val photoAvatar by profileViewModel.userAvatarFlow.collectAsState()
    val avatarLauncher = rememberLauncherForActivityResult(ActivityResultContracts.StartActivityForResult()) { result: ActivityResult ->
        if (result.resultCode == Activity.RESULT_OK) {
            val uri = result.data?.data?.toString() ?: return@rememberLauncherForActivityResult
            profileViewModel.updateAvatar(uri)
        }
    }
    val coverLauncher = rememberLauncherForActivityResult(ActivityResultContracts.StartActivityForResult()) { result: ActivityResult ->
        if (result.resultCode == Activity.RESULT_OK) {
            val uri = result.data?.data?.toString() ?: return@rememberLauncherForActivityResult
            profileViewModel.updateCover(uri)
        }
    }
    val focusManager = LocalFocusManager.current
    Column(modifier = Modifier.fillMaxSize().background(color = Color(red = 230, green = 230, blue = 230))) {
        TopTitleBar(
            title = LocalContext.current.getString(R.string.edit_profile_screen),
            navController = navController,
            isWhiteColor = false)

        ConstraintLayout(Modifier.fillMaxSize()) {
            LazyColumn(Modifier.fillMaxWidth().fillMaxHeight()) {
                items(1) {
                    ConstraintLayout(modifier = Modifier.fillMaxWidth()) {
                        val (cover, avatar, contents) = createRefs()

                        if (photoCover != null) {
                            GlideImage(
                                model = photoCover,
                                contentScale = ContentScale.Crop,
                                modifier = Modifier
                                    .constrainAs(cover) {
                                        start.linkTo(parent.start)
                                        end.linkTo(parent.end)
                                        top.linkTo(parent.top)
                                    }
                                    .fillMaxWidth()
                                    .height(((height / density) * 0.35f).dp)
                                    .clickable {
                                        val intent = Intent()
                                        intent.type = "image/*"
                                        intent.action = Intent.ACTION_GET_CONTENT
                                        coverLauncher.launch(intent)
                                    },
                                contentDescription = "cover",
                                )
                        } else {
                            Image(
                                contentScale = ContentScale.Crop,
                                modifier = Modifier
                                    .constrainAs(cover) {
                                        start.linkTo(parent.start)
                                        end.linkTo(parent.end)
                                        top.linkTo(parent.top)
                                    }
                                    .fillMaxWidth()
                                    .height(((height / density) * 0.35f).dp)
                                    .clickable {
                                        val intent = Intent()
                                        intent.type = "image/*"
                                        intent.action = Intent.ACTION_GET_CONTENT
                                        coverLauncher.launch(intent)
                                    },
                                painter = painterResource(id = R.drawable.nocoverempty), contentDescription = "cover")
                        }

                        Column(modifier = Modifier
                            .constrainAs(contents) {
                                start.linkTo(parent.start)
                                end.linkTo(parent.end)
                                top.linkTo(avatar.bottom, 25.dp)
                            }
                            .fillMaxWidth()) {
                            var yourName by remember {
                                mutableStateOf(TextFieldValue(""))
                            }
                            var userName by remember {
                                mutableStateOf(TextFieldValue(""))
                            }
                            var email by remember {
                                mutableStateOf(TextFieldValue("admin@google.com"))
                            }
                            var bio by remember {
                                mutableStateOf(TextFieldValue(""))
                            }
                            var tiktokUser by remember {
                                mutableStateOf(TextFieldValue(""))
                            }
                            var instagramUser by remember {
                                mutableStateOf(TextFieldValue(""))
                            }

                            ContentsSectionHeader(title = LocalContext.current.getString(R.string.personal_info))

                            TextField(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(
                                        start = 10.dp, end = 10.dp, top = 5.dp, bottom = 5.dp
                                    ),
                                shape = RoundedCornerShape(10.dp),
                                colors = TextFieldDefaults.textFieldColors(
                                    containerColor = Color.White,
                                    focusedIndicatorColor = Color.Transparent,
                                    unfocusedIndicatorColor = Color.Transparent,
                                    disabledIndicatorColor = Color.Transparent
                                ),
                                value = yourName,
                                onValueChange = { newInput ->
                                    val newValue = newInput.text

                                    yourName = newInput.copy(
                                        text = newValue
                                    )
                                },
                                placeholder = { Text(text = LocalContext.current.getString(R.string.name_chooser)) },
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
                                    .fillMaxWidth()
                                    .padding(
                                        start = 10.dp, end = 10.dp, top = 5.dp, bottom = 5.dp
                                    ),
                                shape = RoundedCornerShape(10.dp),
                                colors = TextFieldDefaults.textFieldColors(
                                    containerColor = Color.White,
                                    focusedIndicatorColor = Color.Transparent,
                                    unfocusedIndicatorColor = Color.Transparent,
                                    disabledIndicatorColor = Color.Transparent
                                ),
                                value = userName,
                                onValueChange = { newInput ->
                                    val newValue = newInput.text

                                    userName = newInput.copy(
                                        text = newValue
                                    )
                                },
                                placeholder = { Text(text = LocalContext.current.getString(R.string.username_chooser)) },
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

                            ContentsSectionHeader(title = LocalContext.current.getString(R.string.email_address))

                            TextField(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(
                                        start = 10.dp, end = 10.dp, top = 5.dp, bottom = 5.dp
                                    ),
                                shape = RoundedCornerShape(10.dp),
                                colors = TextFieldDefaults.textFieldColors(
                                    containerColor = Color(red = 240, green = 240, blue = 240),
                                    focusedIndicatorColor = Color.Transparent,
                                    unfocusedIndicatorColor = Color.Transparent,
                                    disabledIndicatorColor = Color.Transparent
                                ),
                                enabled = false,
                                value = email,
                                onValueChange = { newInput ->
                                    /*val newValue = newInput.text

                                    email = newInput.copy(
                                        text = newValue
                                    )*/
                                },
                                placeholder = { Text(text = LocalContext.current.getString(R.string.email_address)) },
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

                            ContentsSectionHeader(title = LocalContext.current.getString(R.string.bio))

                            TextField(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(
                                        start = 10.dp, end = 10.dp, top = 5.dp, bottom = 5.dp
                                    ),
                                shape = RoundedCornerShape(10.dp),
                                colors = TextFieldDefaults.textFieldColors(
                                    containerColor = Color.White,
                                    focusedIndicatorColor = Color.Transparent,
                                    unfocusedIndicatorColor = Color.Transparent,
                                    disabledIndicatorColor = Color.Transparent
                                ),
                                value = bio,
                                onValueChange = { newInput ->
                                    val newValue = newInput.text

                                    bio = newInput.copy(
                                        text = newValue
                                    )
                                },
                                placeholder = { Text(text = LocalContext.current.getString(R.string.bio_placeholder)) },
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

                            ContentsSectionHeader(title = LocalContext.current.getString(R.string.social_networks))

                            TextField(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(
                                        start = 10.dp, end = 10.dp, top = 5.dp, bottom = 5.dp
                                    ),
                                shape = RoundedCornerShape(10.dp),
                                colors = TextFieldDefaults.textFieldColors(
                                    containerColor = Color.White,
                                    focusedIndicatorColor = Color.Transparent,
                                    unfocusedIndicatorColor = Color.Transparent,
                                    disabledIndicatorColor = Color.Transparent
                                ),
                                value = instagramUser,
                                onValueChange = { newInput ->
                                    val newValue = newInput.text

                                    instagramUser = newInput.copy(
                                        text = newValue
                                    )
                                },
                                placeholder = { Text(text = LocalContext.current.getString(R.string.instagram_placeholder)) },
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
                                    .fillMaxWidth()
                                    .padding(
                                        start = 10.dp, end = 10.dp, top = 5.dp, bottom = 5.dp
                                    ),
                                shape = RoundedCornerShape(10.dp),
                                colors = TextFieldDefaults.textFieldColors(
                                    containerColor = Color.White,
                                    focusedIndicatorColor = Color.Transparent,
                                    unfocusedIndicatorColor = Color.Transparent,
                                    disabledIndicatorColor = Color.Transparent
                                ),
                                value = tiktokUser,
                                onValueChange = { newInput ->
                                    val newValue = newInput.text

                                    tiktokUser = newInput.copy(
                                        text = newValue
                                    )
                                },
                                placeholder = { Text(text = LocalContext.current.getString(R.string.tiktok_placeholder)) },
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

                            SolidButton(
                                modifier = Modifier
                                    .padding(
                                        start = 10.dp, end = 10.dp, top = 10.dp
                                    )
                                    .fillMaxWidth()
                                    .clickable {
                                        navController.navigateUp()
                                    },
                                text = LocalContext.current.getString(R.string.save))

                            DummyFooter()
                            DummyFooter()
                        }

                        if (photoAvatar != null) {
                            ProfileImage(photoAvatar!!, ContentScale.Crop, modifier = Modifier
                                .constrainAs(avatar) {
                                    start.linkTo(parent.start)
                                    end.linkTo(parent.end)
                                    top.linkTo(cover.bottom, (-100).dp)
                                }
                                .fillMaxWidth(0.35f)
                                .aspectRatio(3.0f / 4.0f)
                                .clickable {
                                    val intent = Intent()
                                    intent.type = "image/*"
                                    intent.action = Intent.ACTION_GET_CONTENT
                                    avatarLauncher.launch(intent)
                                })
                        } else {
                            ProfileImage(R.drawable.chooser_placeholder, ContentScale.Fit, modifier = Modifier
                                .constrainAs(avatar) {
                                    start.linkTo(parent.start)
                                    end.linkTo(parent.end)
                                    top.linkTo(cover.bottom, (-100).dp)
                                }
                                .fillMaxWidth(0.35f)
                                .aspectRatio(3.0f / 4.0f)
                                .clickable {
                                    val intent = Intent()
                                    intent.type = "image/*"
                                    intent.action = Intent.ACTION_GET_CONTENT
                                    avatarLauncher.launch(intent)
                                })
                        }
                    }
                }
            }
        }
    }
}