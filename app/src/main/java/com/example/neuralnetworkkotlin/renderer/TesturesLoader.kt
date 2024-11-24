package com.example.neuralnetworkkotlin.renderer

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.opengl.GLES20
import android.opengl.GLUtils
import com.example.neuralnetworkkotlin.R
import javax.vecmath.Vector2f


enum class TEXTURES(val id: Int, val resId: Int?) {
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
    LEAF(10, R.drawable.leaf),
    TERRAIN(11, R.drawable.terrain),
    BANNER_GRADIENT(12, R.drawable.banner_gradient),
    SKY(13, R.drawable.sky),
    COWS_TEXTURE(14, R.drawable.cows_texture),
    LEAF_CHANNELS(15, R.drawable.leaf_channels),
    TERRARIN_SHADOW(16, null),
}


class TexturesLoader(var context: Context) {
    private var shadowBitmap: Bitmap? = null
    private var shadowBitmapBackup: Bitmap? = null

    fun generateShadowTesture(positions: List<Vector2f>) {

    }

    fun clearTrack() {
        shadowBitmap = shadowBitmapBackup!!.copy(shadowBitmapBackup!!.getConfig(), true)
    }

    fun drawLane(x: Float, y: Float, angle: Float?): ArrayList<Float> {
        var xF = (-x + 8.333333f) / 16.666666f
        var yF = (-y + 16.666666f) / 33.333333f
        xF = xF * trackBitmap.getWidth()
        yF = yF * trackBitmap.getHeight()
        return drawLaneInt(xF.toInt(), yF.toInt(), angle)
    }

    companion object {
        val TEXTURES_QTY = TEXTURES.values().size
    }

    val textureHandle = IntArray(TEXTURES_QTY + 1)

    fun generateShadowTexture() {
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, textureHandle[TEXTURES.TERRARIN_SHADOW.id])

        
    }

    private fun redrawTrack() {
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, textureHandle[TEXTURES.TERRARIN_SHADOW.id])
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
        GLUtils.texImage2D(GLES20.GL_TEXTURE_2D, 0, shadowBitmap, 0)
    }

    fun loadTexture() {
        TEXTURES.values().forEach { texture ->
            texture.resId?.let {
                GLES20.glGenTextures(TEXTURES_QTY, textureHandle, 0)

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

}
