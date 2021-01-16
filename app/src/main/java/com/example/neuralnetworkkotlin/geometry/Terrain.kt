package com.example.neuralnetworkkotlin.geometry

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Color
import android.graphics.drawable.BitmapDrawable
import android.opengl.GLES20
import android.support.v4.content.ContextCompat
import com.example.neuralnetworkkotlin.R
import com.example.neuralnetworkkotlin.geometry.collada.converter.Vector2f
import com.example.neuralnetworkkotlin.geometry.collada.converter.Vector3f
import com.example.neuralnetworkkotlin.renderer.TexturesLoader
import com.example.neuralnetworkkotlin.viewgroups.COORDS_PER_VERTEX
import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.nio.FloatBuffer
import java.nio.ShortBuffer

class Terrain(context: Context) {

    var bitmap:Bitmap

    init {
        bitmap = (ContextCompat.getDrawable(context, R.drawable.terrain) as BitmapDrawable).bitmap
    }

    val size = 2.0f

    val layerCoords = floatArrayOf(
        -size, size, 0.0f,      // top left
        -size, -size, 0.0f,      // bottom left
        size, -size, 0.0f,      // bottom right
        size, size, 0.0f       // top right
    )


    val textureCoords = floatArrayOf(
        1.0f, 0.0f,      // top left
        1.0f, 1.0f,      // bottom left
        0.0f, 1f,      // bottom right
        0.0f, 0.0f       // top right
    )


    private val drawOrder = shortArrayOf(0, 1, 2, 0, 2, 3) // order to draw vertices

    private val vertexBufferBackground: FloatBuffer =
        // (# of coordinate values * 4 bytes per float)
        ByteBuffer.allocateDirect(layerCoords.size * 4)
            .run {
                order(ByteOrder.nativeOrder())
                asFloatBuffer().apply {
                    put(layerCoords)
                    position(0)
                }
            }

    // initialize byte buffer for the draw list
    private val drawListBufferTrack: ShortBuffer =
        // (# of coordinate values * 2 bytes per short)
        ByteBuffer.allocateDirect(drawOrder.size * 2).run {
            order(ByteOrder.nativeOrder())
            asShortBuffer().apply {
                put(drawOrder)
                position(0)
            }
        }

    private val vertexBufferTextCoords: FloatBuffer =
        // (# of coordinate values * 4 bytes per float)
        ByteBuffer.allocateDirect(textureCoords.size * 4)
            .run {
                order(ByteOrder.nativeOrder())
                asFloatBuffer().apply {
                    put(textureCoords)
                    position(0)
                }
            }


    private var positionHandle: Int = 0

    private val vertexStride: Int = COORDS_PER_VERTEX * 4 // 4 bytes per vertex


    fun drawTerrain(mvpMatrix: FloatArray, textures: TexturesLoader, shader: Int) {

        GLES20.glUseProgram(shader)
        val propertyHandler = GLES20.glGetUniformLocation(shader, "uMVPMatrix")
        GLES20.glUniformMatrix4fv(propertyHandler, 1, false, mvpMatrix, 0)

        val texHandler = GLES20.glGetUniformLocation(shader, "u_Texture")
        GLES20.glUniform1i(texHandler, 0)
        GLES20.glActiveTexture(GLES20.GL_TEXTURE0)
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, textures.textureHandle[11])

        val texHandlerTerrain = GLES20.glGetUniformLocation(shader, "u_TextureTerrain")
        GLES20.glUniform1i(texHandlerTerrain, 1)
        GLES20.glActiveTexture(GLES20.GL_TEXTURE1)
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, textures.textureHandle[8])

        val texHandlerTerrain2 = GLES20.glGetUniformLocation(shader, "u_TextureTerrain2")
        GLES20.glUniform1i(texHandlerTerrain2, 2)
        GLES20.glActiveTexture(GLES20.GL_TEXTURE2)
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, textures.textureHandle[7])

        val texHandlerTerrain3 = GLES20.glGetUniformLocation(shader, "u_TextureTerrain3")
        GLES20.glUniform1i(texHandlerTerrain3, 3)
        GLES20.glActiveTexture(GLES20.GL_TEXTURE3)
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, textures.textureHandle[6])


        positionHandle = GLES20.glGetAttribLocation(shader, "vPosition").also {
            val mTextureCoordinateHandle = GLES20.glGetAttribLocation(shader, "a_TexCoordinate")

            GLES20.glEnableVertexAttribArray(mTextureCoordinateHandle)


            GLES20.glVertexAttribPointer(
                mTextureCoordinateHandle,
                2,
                GLES20.GL_FLOAT,
                false,
                2 * 4,
                vertexBufferTextCoords
            )

            GLES20.glEnableVertexAttribArray(it)

            GLES20.glVertexAttribPointer(
                it,
                COORDS_PER_VERTEX,
                GLES20.GL_FLOAT,
                false,
                vertexStride,
                vertexBufferBackground
            )

            GLES20.glDrawElements(
                GLES20.GL_TRIANGLES, drawOrder.size,
                GLES20.GL_UNSIGNED_SHORT, drawListBufferTrack
            )

            GLES20.glDisableVertexAttribArray(it)
        }
    }


    fun collision(position: Vector2f):Boolean{

        val posX = (-position.x + 0.5)*512
        val posY = (-position.y + 0.5)*512

        var alpha = 0

        if( posX.toInt()>=0 && posY.toInt()>=0 && posX.toInt()<bitmap.width && posY.toInt()<bitmap.height ) {
            alpha = Color.alpha( bitmap.getPixel(posX.toInt(), posY.toInt()) )
        }

        //val alpha = Color.alpha(color)

        return alpha>128
    }


}