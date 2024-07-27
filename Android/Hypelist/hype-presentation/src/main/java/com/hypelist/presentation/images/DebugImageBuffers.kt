package com.hypelist.presentation.images

import com.hypelist.presentation.images.TempImagesBuffer

// TODO Need to refactor!
object DebugImageBuffers {

    var loggedUserID = ""

    val hypelistsCoverBuffer = TempImagesBuffer(12)
    val hypelistsItemsBuffer = TempImagesBuffer(30)
    val profileAvatarsBuffer = TempImagesBuffer(10)
    val profileCoversBuffer = TempImagesBuffer(10)

}