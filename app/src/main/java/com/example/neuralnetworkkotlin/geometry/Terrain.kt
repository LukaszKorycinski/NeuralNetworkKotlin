package com.example.neuralnetworkkotlin.geometry

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Color
import android.graphics.drawable.BitmapDrawable
import android.opengl.GLES20
import android.opengl.GLES31
import androidx.core.content.ContextCompat
import com.example.neuralnetworkkotlin.R
import com.example.neuralnetworkkotlin.renderer.ShaderLoader
import com.example.neuralnetworkkotlin.renderer.Shaders
import com.example.neuralnetworkkotlin.renderer.TEXTURES
import com.example.neuralnetworkkotlin.renderer.TexturesLoader
import com.example.neuralnetworkkotlin.viewgroups.COORDS_PER_VERTEX
import timber.log.Timber
import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.nio.FloatBuffer
import java.nio.ShortBuffer
import javax.vecmath.Vector2f
import javax.vecmath.Vector3f
import kotlin.math.max
import kotlin.math.min

class Terrain(context: Context) {



    var bitmap:Bitmap

    lateinit var layerCoords: FloatArray
    lateinit var textureCoords: FloatArray
    private lateinit var drawOrder: ShortArray


    lateinit var vertexBufferCoords: FloatBuffer
    lateinit var indicesBuffer: ShortBuffer
    lateinit var vertexBufferTextCoords: FloatBuffer
    lateinit var pixels: IntArray
    lateinit var redChannel: IntArray

    val size = 40.0f
    val resolution = Vector2f(32f, 32f)
    private val heightMul = .03f

    val textureSpace = resolution.x / size

    var dupa = .0f

    init {
        bitmap = (ContextCompat.getDrawable(context, R.drawable.terrain) as BitmapDrawable).bitmap
    }

    fun getHeight(xIn: Float, zIn: Float): Float{
//        return 0f
        val x = xIn * textureSpace /// resolution.x // 1/16
        val xSafe = max(0f, min(x, resolution.x - 1f))  // 1/16 = 0.0625
        val z = zIn * textureSpace /// resolution.y
        val zSafe = max(0f, min(z, resolution.y - 1f)) //0

        // Przekształcenie współrzędnych do indeksów w tablicy bitmap
        val xi = xSafe.toInt() // 0
        val zi = zSafe.toInt()

        // Obliczenie części ułamkowej dla obu współrzędnych
        //val xf = xSafe - xi
        //val zf = zSafe - zi

        // Indeksy czterech punktów wokół (xi, zi)
        val x0 = xi //* resolution.x
        //val x1 = if (x0 + 1 < resolution.x) x0 + 1 else x0
        val z0 = zi //* resolution.y
        //val z1 = if (z0 + 1 < resolution.y) z0 + 1 else z0

        // Pobranie kolorów z bitmapy w punktach (x0, z0), (x1, z0), (x0, z1), (x1, z1)
//        val topLeft = redChannel[(x0 + z0 * resolution.x).toInt()]
//        val topRight = redChannel[(x1 + z0 * resolution.x).toInt()]
//        val bottomLeft = redChannel[(x0 + z1 * resolution.x).toInt()]
//        val bottomRight = redChannel[(x1 + z1 * resolution.x).toInt()]
//
//        // Interpolacja najpierw w osi x (między topLeft i topRight)
//        val topInterpolated = topLeft + (topRight - topLeft) * xf
//        val bottomInterpolated = bottomLeft + (bottomRight - bottomLeft) * xf

        // Interpolacja w osi z (między topInterpolated i bottomInterpolated)
        return redChannel[(x0 + z0 * resolution.x).toInt()] * heightMul
    }

    fun build(){

        pixels = IntArray(bitmap.width * bitmap.height)
        bitmap.getPixels(pixels, 0, bitmap.width, 0, 0, bitmap.width, bitmap.height)

        redChannel = IntArray(bitmap.width * bitmap.height)
        for (i in pixels.indices) {
            redChannel[i] = Color.red(pixels[i])
        }

        val tmpCoords = mutableListOf<Float>()
        val tmpTexCoords = mutableListOf<Float>()
        val tmpDrawOrder = mutableListOf<Short>()

        val tailSize = size / resolution.x

        // Iterate over the resolution (rows and columns of the grid)gfGF

        for (y in 0 until resolution.y.toInt()) {
            for (x in 0 until resolution.x.toInt()) {
                // Współrzędne wierzchołków

                val posX = x * tailSize
                val posZ = y * tailSize

                val posY = getHeight(posX, posZ) // red channel
                Timber.e("terrain:")
                Timber.d("x: $x, y: $y height: $posY")


                // Dodaj współrzędne wierzchołków (x, y, z)
                tmpCoords.add(posX)
                tmpCoords.add(posY)
                tmpCoords.add(posZ)

                // Współrzędne tekstury (UV)
                val texCoordX = x / (resolution.x )
                val texCoordY = y / (resolution.y )
                tmpTexCoords.add(texCoordX)
                tmpTexCoords.add(texCoordY)

                // Indeksowanie wierzchołków (indeksowanie trójkątów)
                if (x < resolution.x.toInt() - 1 && y < resolution.y.toInt() - 1) {
                    val topLeft = y * resolution.x.toInt() + x
                    val topRight = y * resolution.x.toInt() + (x + 1)
                    val bottomLeft = (y + 1) * resolution.x.toInt() + x
                    val bottomRight = (y + 1) * resolution.x.toInt() + (x + 1)

                    // Pierwszy trójkąt
                    tmpDrawOrder.add(topLeft.toShort())
                    tmpDrawOrder.add(bottomLeft.toShort())
                    tmpDrawOrder.add(topRight.toShort())

                    // Drugi trójkąt
                    tmpDrawOrder.add(topRight.toShort())
                    tmpDrawOrder.add(bottomLeft.toShort())
                    tmpDrawOrder.add(bottomRight.toShort())
                }
            }
        }

        layerCoords = tmpCoords.toFloatArray()
        textureCoords = tmpTexCoords.toFloatArray()
        drawOrder = tmpDrawOrder.toShortArray()

        fillBuffers()
    }

    fun fillBuffers(){
        vertexBufferCoords = ByteBuffer.allocateDirect(layerCoords.size * 4)
            .run {
                order(ByteOrder.nativeOrder())
                asFloatBuffer().apply {
                    put(layerCoords)
                    position(0)
                }
            }
        indicesBuffer = ByteBuffer.allocateDirect(drawOrder.size * 2).run {
            order(ByteOrder.nativeOrder())
            asShortBuffer().apply {
                put(drawOrder)
                position(0)
            }
        }

        vertexBufferTextCoords = ByteBuffer.allocateDirect(textureCoords.size * 4)
            .run {
                order(ByteOrder.nativeOrder())
                asFloatBuffer().apply {
                    put(textureCoords)
                    position(0)
                }
            }
    }


    private val vertexStride: Int = COORDS_PER_VERTEX * 4 // 4 bytes per vertex

    private fun setVariable3F(shader: Shaders, value: Vector3f, name: String) {
        val handler = GLES31.glGetUniformLocation(
            ShaderLoader.getShaderProgram(shader),
            name
        )
        GLES31.glUniform3f(handler, value.x, value.y, value.z)
    }

    fun drawTerrain(mvpMatrix: FloatArray, textures: TexturesLoader, shader: Int, eyePosition: Vector3f) {

        GLES20.glUseProgram(shader)

        setVariable3F(Shaders.TERRAIN, eyePosition, "eyePosition")

        val propertyHandler = GLES20.glGetUniformLocation(shader, "uMVPMatrix")
        GLES20.glUniformMatrix4fv(propertyHandler, 1, false, mvpMatrix, 0)

        val texHandler = GLES20.glGetUniformLocation(shader, "u_Texture")
        GLES20.glUniform1i(texHandler, 0)
        GLES20.glActiveTexture(GLES20.GL_TEXTURE0)
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, textures.textureHandle[TEXTURES.TERRAIN.id])

        val texHandlerTerrain = GLES20.glGetUniformLocation(shader, "u_TextureTerrain")
        GLES20.glUniform1i(texHandlerTerrain, 1)
        GLES20.glActiveTexture(GLES20.GL_TEXTURE1)
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, textures.textureHandle[TEXTURES.TERRAINTEXTURE.id])

        val texHandlerTerrain2 = GLES20.glGetUniformLocation(shader, "u_TextureTerrain2")
        GLES20.glUniform1i(texHandlerTerrain2, 2)
        GLES20.glActiveTexture(GLES20.GL_TEXTURE2)
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, textures.textureHandle[TEXTURES.TERRAINTEXTURE2.id])

        val texHandlerTerrain3 = GLES20.glGetUniformLocation(shader, "u_TextureTerrain3")
        GLES20.glUniform1i(texHandlerTerrain3, 3)
        GLES20.glActiveTexture(GLES20.GL_TEXTURE3)
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, textures.textureHandle[TEXTURES.TERRAINTEXTURE3.id])

        val texHandlerTerrain4 = GLES20.glGetUniformLocation(shader, "shadowTexture")
        GLES20.glUniform1i(texHandlerTerrain4, 4)
        GLES20.glActiveTexture(GLES20.GL_TEXTURE4)
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, textures.textureHandle[TEXTURES.SHADOW_ON_TERRAIN.id])

        val texHandlerShadowMap = GLES20.glGetUniformLocation(shader, "shadowMapTexture")
        GLES20.glUniform1i(texHandlerShadowMap, 5)
        GLES20.glActiveTexture(GLES20.GL_TEXTURE5)
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, textures.textureHandle[TEXTURES.SHADOW_MAP_TERRAIN.id])


        val positionHandle = GLES20.glGetAttribLocation(shader, "vPosition")
        GLES20.glEnableVertexAttribArray(positionHandle)

       GLES20.glVertexAttribPointer(
           positionHandle,
           COORDS_PER_VERTEX,
           GLES20.GL_FLOAT,
           false,
           vertexStride,
           vertexBufferCoords
       )

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


       GLES20.glDrawElements(
           GLES20.GL_TRIANGLES, drawOrder.size,
           GLES20.GL_UNSIGNED_SHORT, indicesBuffer
       )

            //GLES20.glDisableVertexAttribArray(positionHandle)

    }


    fun collision(position: Vector2f):Boolean{

        val posX = (-position.x + 1.0)*256
        val posY = (-position.y + 1.0)*256

        var alpha = 0

        if( posX.toInt()>=0 && posY.toInt()>=0 && posX.toInt()<bitmap.width && posY.toInt()<bitmap.height ) {
            alpha = Color.alpha( bitmap.getPixel(posX.toInt(), posY.toInt()) )
        }

        //val alpha = Color.alpha(color)

        return alpha>128
    }


}