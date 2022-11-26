package com.example.neuralnetworkkotlin.geometry

import android.content.Context
import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import android.opengl.GLES20
import android.opengl.Matrix
import androidx.core.content.ContextCompat
import com.example.neuralnetworkkotlin.R
import com.example.neuralnetworkkotlin.geometry.collada.converter.Vector2f
import com.example.neuralnetworkkotlin.geometry.collada.converter.Vector3f
import com.example.neuralnetworkkotlin.renderer.TexturesLoader
import com.example.neuralnetworkkotlin.viewgroups.COORDS_PER_VERTEX
import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.nio.FloatBuffer
import java.nio.ShortBuffer

class ParticleEffects(context: Context) {

    var particles: List<Particle> = emptyList()
    val size = 0.1f

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


    fun loop(){
        for (p in particles){
            p.size = p.size + 0.0125f
        }

        particles = particles.filter { it.size<1.0f }
    }

    fun drawParticles(mvpMatrix: FloatArray, textures: TexturesLoader, shader: Int) {
        loop()
        GLES20.glEnable(GLES20.GL_BLEND)
        GLES20.glBlendFunc(GLES20.GL_SRC_ALPHA, GLES20.GL_ONE_MINUS_SRC_ALPHA)
        GLES20.glBlendEquationSeparate(GLES20.GL_FUNC_ADD, GLES20.GL_FUNC_ADD)
        GLES20.glUseProgram(shader)
        val mvpMatrixHandler = GLES20.glGetUniformLocation(shader, "uMVPMatrix")
        //GLES20.glUniformMatrix4fv(mvpMatrixHandler, 1, false, mvpMatrix, 0)

        val texHandler = GLES20.glGetUniformLocation(shader, "u_Texture")
        GLES20.glUniform1i(texHandler, 0)
        GLES20.glActiveTexture(GLES20.GL_TEXTURE0)
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, textures.textureHandle[2])
        val mTextureCoordinateHandle = GLES20.glGetAttribLocation(shader, "a_TexCoordinate")
        val waveHandler = GLES20.glGetUniformLocation(shader, "size")

        positionHandle = GLES20.glGetAttribLocation(shader, "vPosition").also {

            for (p in particles) {
                GLES20.glUniform1f(waveHandler, p.size)
                val mvptMatrix = FloatArray(16)
                val transMatrix = FloatArray(16)
                Matrix.setIdentityM(transMatrix, 0)
                Matrix.translateM(transMatrix, 0, p.pos.x, p.pos.y, 0f)
                Matrix.scaleM(transMatrix, 0, 0.25f+p.size, 0.25f+p.size, 0.25f+p.size)
                Matrix.multiplyMM(mvptMatrix, 0, mvpMatrix, 0, transMatrix, 0)

                GLES20.glUniformMatrix4fv(mvpMatrixHandler, 1, false, mvptMatrix, 0)



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
            }
            GLES20.glDisableVertexAttribArray(it)
        }
        GLES20.glDisable(GLES20.GL_BLEND)
    }
}


class Particle (
    var pos: Vector2f = Vector2f(),
    var size: Float = 0.0f
)