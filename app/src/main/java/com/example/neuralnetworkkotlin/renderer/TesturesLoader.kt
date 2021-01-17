package com.example.neuralnetworkkotlin.renderer

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Color
import android.opengl.GLES20
import android.opengl.GLUtils


class TexturesLoader(var context: Context) {

    companion object {
        const val TEXTURES_QTY = 14
    }

    lateinit var trackBitmap: Bitmap
    lateinit var trackBitmapBackup: Bitmap


    fun getTrackPixel(x: Float, y: Float): Int {
        var xF = (-x + 8.333333f) / 16.666666f
        var yF = (-y + 16.666666f) / 33.333333f
        xF = xF * trackBitmap.getWidth()
        yF = yF * trackBitmap.getHeight()
        val pixel: Int = trackBitmap.getPixel(xF.toInt(), yF.toInt())

        //int redValue = Color.red(pixel);
        //int blueValue = Color.blue(pixel);

        //drawLane(xF.intValue(), yF.intValue(), 0.0f);
        return Color.alpha(pixel) //0-255
    }


    fun clearTrack() {
        trackBitmap = trackBitmapBackup.copy(trackBitmapBackup.getConfig(), true)
    }

    fun drawLane(x: Float, y: Float, angle: Float?): ArrayList<Float?>? {
        var xF: Float = (-x + 8.333333f) / 16.666666f
        var yF: Float = (-y + 16.666666f) / 33.333333f
        xF = xF * trackBitmap.width
        yF = yF * trackBitmap.height
        return drawLaneInt(xF.toInt(), yF.toInt(), angle)
    }


    fun drawLaneInt(xStart: Int, yStart: Int, angle: Float): ArrayList<Float>? {
        var xEnd = xStart
        var yEnd = yStart
        val outputList: ArrayList<Float> = ArrayList()
        val color = context.getColor(R.color.rayColor)
        trackBitmap.setPixel(xEnd, yEnd, color)
        var angles: Float
        for (i in 0..4) {
            angles = angle - 50 * ToRadians + i * 25 * ToRadians
            var xEndF = java.lang.Double.valueOf(xStart.toDouble())
            var yEndF = java.lang.Double.valueOf(yStart.toDouble())
            var pixel = trackBitmap.getPixel(xStart, yStart)
            var greenValue = Color.green(pixel)
            while (greenValue > 128) {
                xEndF -= Math.sin(angles.toDouble())
                yEndF -= Math.cos(angles.toDouble())
                xEnd = xEndF.toInt()
                yEnd = yEndF.toInt()
                if (xEnd > trackBitmap.width - 1 || xEnd < 0 || yEnd > trackBitmap.height - 1 || yEnd < 0) {
                    outputList.add(length(xStart, yStart, xEnd, yEnd))
                    break
                }
                pixel = trackBitmap.getPixel(xEnd, yEnd)
                greenValue = Color.green(pixel)
                if (greenValue >= 128) {
                    trackBitmap.setPixel(xEnd, yEnd, color)
                } else {
                    outputList.add(length(xStart, yStart, xEnd, yEnd))
                }
            }
        }
        redrawTrack()
        return outputList
    }

    private fun redrawTrack() {
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, textureHandle[1])
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
        GLUtils.texImage2D(GLES20.GL_TEXTURE_2D, 0, trackBitmap, 0)
    }

    fun length(xStart: Int, yStart: Int, xEnd: Int, yEnd: Int): Float {
        return Math.sqrt(((xEnd - xStart) * (xEnd - xStart) + (yEnd - yStart) * (yEnd - yStart)).toDouble())
            .toFloat()
    }

    fun length(xStart: Float, yStart: Float, xEnd: Float, yEnd: Float): Float {
        return Math.sqrt(((xEnd - xStart) * (xEnd - xStart) + (yEnd - yStart) * (yEnd - yStart)).toDouble())
            .toFloat()
    }





    val textureHandle = IntArray(TEXTURES_QTY + 1)

    fun loadTexture() {
        GLES20.glGenTextures(TEXTURES_QTY, textureHandle, 0)
        val textResIds: IntArray = IntArray(TEXTURES_QTY + 1)
        textResIds[0] = R.drawable.b0
        textResIds[1] = R.drawable.b1
        textResIds[2] = R.drawable.b2
        textResIds[3] = R.drawable.b3
        textResIds[4] = R.drawable.b4
        textResIds[5] = R.drawable.egg
        textResIds[6] = R.drawable.terraintexture3
        textResIds[7] = R.drawable.terraintexture2
        textResIds[8] = R.drawable.terraintexture
        textResIds[9] = R.drawable.seed
        textResIds[10] = R.drawable.plant
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

            if(i==13){
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
            }else{
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
