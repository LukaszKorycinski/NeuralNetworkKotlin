package com.example.neuralnetworkkotlin.renderer

import android.content.Context
import android.graphics.BitmapFactory
import android.opengl.GLES20
import android.opengl.GLUtils
import com.example.neuralnetworkkotlin.R


enum class TEXTURES(val id: Int, val resId: Int) {
    BANNER(0, R.drawable.banner),
    MEN_ALPHA(1, R.drawable.men_texture_alfa),
    SMOKE(2, R.drawable.smoke),
    PATH(3, R.drawable.path),
    STRING(4, R.drawable.string),
    MEN(5, R.drawable.men_texture),
    TERRAINTEXTURE3(6, R.drawable.terraintexture3),
    TERRAINTEXTURE2(7, R.drawable.terraintexture2),
    TERRAINTEXTURE(8, R.drawable.terraintexture),
    WARPEONS(9, R.drawable.warpeon),
    TREE(10, R.drawable.tree),
    TERRAIN(11, R.drawable.terrain),
    BANNER_GRADIENT(12, R.drawable.banner_gradient),
    SKY(13, R.drawable.sky),
    COWS_TEXTURE(14, R.drawable.cows_texture),
}


class TexturesLoader(var context: Context) {

    companion object {
        val TEXTURES_QTY = TEXTURES.values().size
    }

    val textureHandle = IntArray(TEXTURES_QTY + 1)

    fun loadTexture() {
        GLES20.glGenTextures(TEXTURES_QTY, textureHandle, 0)


        TEXTURES.values().forEach { texture ->
            val options = BitmapFactory.Options()
            options.inScaled = true // No pre-scaling

            val bitmap = BitmapFactory.decodeResource(context.resources, texture.resId, options)

            GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, textureHandle[texture.id])

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

            if (texture == TEXTURES.SKY) {
                GLES20.glTexParameteri(
                    GLES20.GL_TEXTURE_2D,
                    GLES20.GL_TEXTURE_WRAP_S,
                    GLES20.GL_MIRRORED_REPEAT
                )
                GLES20.glTexParameteri(
                    GLES20.GL_TEXTURE_2D,
                    GLES20.GL_TEXTURE_WRAP_T,
                    GLES20.GL_MIRRORED_REPEAT
                )
            } else {
                GLES20.glTexParameteri(
                    GLES20.GL_TEXTURE_2D,
                    GLES20.GL_TEXTURE_WRAP_S,
                    GLES20.GL_REPEAT
                )
                GLES20.glTexParameteri(
                    GLES20.GL_TEXTURE_2D,
                    GLES20.GL_TEXTURE_WRAP_T,
                    GLES20.GL_REPEAT
                )
            }

            GLUtils.texImage2D(GLES20.GL_TEXTURE_2D, 0, bitmap, 0)
            bitmap.recycle()
        }
    }

}
