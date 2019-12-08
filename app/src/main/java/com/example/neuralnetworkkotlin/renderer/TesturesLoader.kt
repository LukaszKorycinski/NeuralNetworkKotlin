package com.example.neuralnetworkkotlin.renderer

import android.content.Context
import android.graphics.BitmapFactory
import android.opengl.GLES20
import android.opengl.GLUtils
import com.example.neuralnetworkkotlin.R

class TexturesLoader(var context: Context) {

    private val texturesQty = 3

    val textureHandle = IntArray(texturesQty)

    fun loadTexture() {
        GLES20.glGenTextures(3, textureHandle, 0)
        val textResIds: IntArray = IntArray(texturesQty)
        textResIds[0] = R.drawable.car
        textResIds[1] = R.drawable.track
        textResIds[2] = R.drawable.selector

        for (i in 0..texturesQty - 1) {
            val options = BitmapFactory.Options()
            options.inScaled = true // No pre-scaling

            val bitmap = BitmapFactory.decodeResource(context.resources, textResIds[i], options)

            GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, textureHandle[i])

            GLES20.glTexParameteri(
                GLES20.GL_TEXTURE_2D,
                GLES20.GL_TEXTURE_MIN_FILTER,
                GLES20.GL_NEAREST
            )
            GLES20.glTexParameteri(
                GLES20.GL_TEXTURE_2D,
                GLES20.GL_TEXTURE_MAG_FILTER,
                GLES20.GL_NEAREST
            )

            GLUtils.texImage2D(GLES20.GL_TEXTURE_2D, 0, bitmap, 0)

            bitmap.recycle()
        }
    }

}
