package com.example.neuralnetworkkotlin.renderer

import android.content.Context
import android.graphics.BitmapFactory
import android.opengl.GLES20
import android.opengl.GLUtils
import com.example.neuralnetworkkotlin.R

class TexturesLoader(var context: Context) {

    companion object{
        const val TEXTURES_QTY = 14
    }


    val textureHandle = IntArray(TEXTURES_QTY+1)

    fun loadTexture() {
        GLES20.glGenTextures(TEXTURES_QTY, textureHandle, 0)
        val textResIds: IntArray = IntArray(TEXTURES_QTY+1)
        textResIds[0] = R.drawable.b0
        textResIds[1] = R.drawable.b1
        textResIds[2] = R.drawable.b2
        textResIds[3] = R.drawable.b3
        textResIds[4] = R.drawable.b4
        textResIds[5] = R.drawable.b5
        textResIds[6] = R.drawable.b6
        textResIds[7] = R.drawable.b7
        textResIds[8] = R.drawable.b8
        textResIds[9] = R.drawable.b9
        textResIds[10] = R.drawable.b10
        textResIds[11] = R.drawable.terrain
        textResIds[12] = R.drawable.fog_background
        textResIds[13] = R.drawable.sky
        textResIds[14] = R.drawable.champ

        for (i in 0..TEXTURES_QTY) {
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
            if(i==12){
                GLES20.glTexParameteri(
                    GLES20.GL_TEXTURE_2D,
                    GLES20.GL_TEXTURE_WRAP_S,
                    GLES20. GL_REPEAT
                )
                GLES20.glTexParameteri(
                    GLES20.GL_TEXTURE_2D,
                    GLES20.GL_TEXTURE_WRAP_T,
                    GLES20.GL_CLAMP_TO_EDGE
                )
            }else {
                GLES20.glTexParameteri(
                    GLES20.GL_TEXTURE_2D,
                    GLES20.GL_TEXTURE_WRAP_S,
                    GLES20. GL_REPEAT
                )
                GLES20.glTexParameteri(
                    GLES20.GL_TEXTURE_2D,
                    GLES20.GL_TEXTURE_WRAP_T,
                    GLES20.GL_CLAMP_TO_EDGE
                )
            }

            GLUtils.texImage2D(GLES20.GL_TEXTURE_2D, 0, bitmap, 0)

            bitmap.recycle()
        }
    }

}
