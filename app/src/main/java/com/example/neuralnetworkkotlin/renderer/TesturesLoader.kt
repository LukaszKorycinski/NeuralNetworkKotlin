package com.example.neuralnetworkkotlin.renderer

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Color
import android.opengl.GLES20
import android.opengl.GLUtils
import com.example.neuralnetworkkotlin.R
import com.example.neuralnetworkkotlin.geometry.vectors.distance
import javax.vecmath.Vector2f
import kotlin.math.min
import kotlin.math.max

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
    LEAF(10, R.drawable.leaf),
    TERRAIN(11, R.drawable.terrain),
    BANNER_GRADIENT(12, R.drawable.banner_gradient),
    SKY(13, R.drawable.sky),
    COWS_TEXTURE(14, R.drawable.cows_texture),
    LEAF_CHANNELS(15, R.drawable.leafchannels),
    SHADOW_TERRAIN(16, R.drawable.terain_shadow),
    GRASS(17, R.drawable.grass),
}


class TexturesLoader(var context: Context) {

    companion object {
        val TEXTURES_QTY = TEXTURES.values().size
    }

    val textureHandle = IntArray(TEXTURES_QTY + 1)

    private var shadowBitmap: Bitmap? = null
    private var shadowBitmapBackup: Bitmap? = null

    //for 515x512
    fun burnPointsShadows(trees: List<Vector2f>) {
        shadowBitmap = Bitmap.createBitmap(125, 125, Bitmap.Config.ARGB_8888)

        val resolution = 125

        for (x in 0 until resolution) {
            for (y in 0 until resolution) {

                val closestDistance = trees.minOf { tree ->
                    Vector2f(
                        ((tree.x+10f) * .5f) * resolution * .1f,
                        ((tree.y+10f) * .5f) * resolution * .1f,
                    ).distance(Vector2f(x.toFloat(), y.toFloat()))
                }

                val shadowRadius = 8f

                val shadow = max(min(shadowRadius, closestDistance), shadowRadius * .3f)

                val shadowColor = shadow / shadowRadius

                shadowBitmap!!.setPixel(x, y, Color.argb(1f, shadowColor, shadowColor, shadowColor))
            }
        }

        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, textureHandle[TEXTURES.SHADOW_TERRAIN.id])

        GLES20.glTexParameteri(
            GLES20.GL_TEXTURE_2D,
            GLES20.GL_TEXTURE_MIN_FILTER,
            GLES20.GL_LINEAR
        )
        GLES20.glTexParameteri(
            GLES20.GL_TEXTURE_2D,
            GLES20.GL_TEXTURE_MAG_FILTER,
            GLES20.GL_LINEAR
        )

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

        GLUtils.texImage2D(GLES20.GL_TEXTURE_2D, 0, shadowBitmap, 0)
        shadowBitmap!!.recycle()
    }

    fun clearShadow() {
        shadowBitmap = shadowBitmapBackup!!.copy(shadowBitmapBackup!!.config, true)

    }


    fun loadTexture() {
        GLES20.glGenTextures(TEXTURES_QTY, textureHandle, 0)


        TEXTURES.values().forEach { texture ->
            if (texture == TEXTURES.SHADOW_TERRAIN) return@forEach

            val options = BitmapFactory.Options()
            options.inScaled = true // No pre-scaling

            val bitmap = BitmapFactory.decodeResource(context.resources, texture.resId, options)

            GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, textureHandle[texture.id])

            GLES20.glTexParameteri(
                GLES20.GL_TEXTURE_2D,
                GLES20.GL_TEXTURE_MIN_FILTER,
                GLES20.GL_LINEAR
            )
            GLES20.glTexParameteri(
                GLES20.GL_TEXTURE_2D,
                GLES20.GL_TEXTURE_MAG_FILTER,
                GLES20.GL_LINEAR
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
