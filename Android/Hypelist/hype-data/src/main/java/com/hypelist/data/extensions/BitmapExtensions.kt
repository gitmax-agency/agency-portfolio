package com.hypelist.data.extensions

import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.PorterDuff
import android.graphics.PorterDuffXfermode
import android.graphics.Rect
import android.graphics.RectF


fun Bitmap.tryScaleForWidth(
    viewWidth: Int
) : Bitmap {
    val debugDiff = width.toFloat() / viewWidth.toFloat()

    val resultBitmap = if (debugDiff > 1.0) {
        val scaled = Bitmap.createScaledBitmap(
            this,
            (width / debugDiff).toInt(),
            (height / debugDiff).toInt(),
            true
        )
        scaled
    } else {
        this
    }

    System.gc()
    Runtime.getRuntime().gc()
    return resultBitmap
}

fun Bitmap.roundedImage() : Bitmap {
    val output = Bitmap.createBitmap(width, width, Bitmap.Config.ARGB_8888)

    val canvas = Canvas(output)

    val color = -0xbdbdbe

    val paint = Paint()

    val rect = Rect(
        0, 0, width, width
    )

    val rectF = RectF(rect)

    paint.isAntiAlias = true

    canvas.drawARGB(0, 0, 0, 0)

    paint.color = color

    canvas.drawRoundRect(rectF, (width / 2).toFloat(), (width / 2).toFloat(), paint)

    paint.xfermode = PorterDuffXfermode(PorterDuff.Mode.SRC_IN)

    canvas.drawBitmap(this, rect, rect, paint)

    return output
}