package com.example.neuralnetworkkotlin.geometry.plants

import android.opengl.GLES20
import android.opengl.Matrix
import com.example.neuralnetworkkotlin.Const
import com.example.neuralnetworkkotlin.gameLogic.Collidor
import com.example.neuralnetworkkotlin.geometry.PlantsData
import com.example.neuralnetworkkotlin.geometry.collada.converter.Vector2f
import com.example.neuralnetworkkotlin.renderer.TexturesLoader
import com.example.neuralnetworkkotlin.viewgroups.COORDS_PER_VERTEX
import java.lang.Float.max
import java.lang.Float.min
import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.nio.FloatBuffer
import java.nio.ShortBuffer
import kotlin.random.Random
import kotlin.reflect.KFunction1

class Seed(val collidor: Collidor) {
    var seedsList = ArrayList<SeedData>()
    val MAX_QTY = 100

    fun add(seed: List<SeedData>) {
        if(seedsList.size < MAX_QTY)
            seedsList.addAll(seed)
    }

    fun add(seed: SeedData) {
        if(seedsList.size < MAX_QTY)
            seedsList.add(seed)
    }

    fun loop(onSeedAdded: KFunction1<@ParameterName(name = "seed") List<SeedData>, Unit>) {
        val seedsToAdd: ArrayList<SeedData> = ArrayList()
        seedsList.forEach {


            it.age = it.age + 0.25f * Const.step

            move(it)
            if (it.age > 2.0f) {
                seedsToAdd.add(SeedData(Vector2f(it.pos.x+0.1f, it.pos.y), Vector2f().randomVelocity(0.5f), 0.3f))
                seedsToAdd.add(SeedData(it.pos, Vector2f().randomVelocity( 0.7f ), 0.2f))
            }

        }


        onSeedAdded(seedsToAdd)

        if(seedsList.size < 20){
            onSeedAdded(listOf(
                SeedData(Vector2f((Random.nextFloat()-0.5f)*5.0f, (Random.nextFloat()-0.5f)*5.0f), Vector2f().randomVelocity(0.9f), Random.nextFloat()*2.0f),
                SeedData(Vector2f((Random.nextFloat()-0.5f)*5.0f, (Random.nextFloat()-0.5f)*5.0f), Vector2f().randomVelocity(0.7f), Random.nextFloat()*2.0f),
                SeedData(Vector2f((Random.nextFloat()-0.5f)*5.0f, (Random.nextFloat()-0.5f)*5.0f), Vector2f().randomVelocity(0.8f), Random.nextFloat()*2.0f)
            ))
        }

        seedsList = seedsList.filter { it.age <= 2.0f }.filter { !collidor.colision(it.pos) } as ArrayList<SeedData>
    }

    private fun move(it: SeedData) {

        val newPosition = Vector2f(
            it.pos.x + it.velocity.x * Const.step,
            it.pos.y + it.velocity.y * Const.step
        )



        if (!collidor.colision(Vector2f(newPosition.x, it.pos.y))) {
            it.pos.x = newPosition.x
        } else {
            it.velocity.x = -it.velocity.x * 0.2f
            it.velocity.y = it.velocity.y * 0.75f
        }

        if (!collidor.colision(Vector2f(it.pos.x, newPosition.y))) {
            it.pos.y = newPosition.y
        } else {
            it.velocity.x = it.velocity.x * 0.75f
            it.velocity.y = -it.velocity.y * 0.2f
        }
        it.velocity = it.velocity.mull(0.995f)
        //it.velocity.y = it.velocity.y + Const.gravity * Const.step * 100.0f//gravity
    }

    fun draw(mvpMatrix: FloatArray, textures: TexturesLoader, shader: Int) {
        GLES20.glUseProgram(shader)
        val mvpMatrixHandler = GLES20.glGetUniformLocation(shader, "uMVPMatrix")
        val ageHandler = GLES20.glGetUniformLocation(shader, "age")

        val texHandler = GLES20.glGetUniformLocation(shader, "u_Texture")
        GLES20.glUniform1i(texHandler, 0)
        GLES20.glActiveTexture(GLES20.GL_TEXTURE0);
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, textures.textureHandle[9])


        seedsList.forEach {
            val mvptMatrix = FloatArray(16)
            val transMatrix = FloatArray(16)
            Matrix.setIdentityM(transMatrix, 0)
            Matrix.translateM(transMatrix, 0, it.pos.x, it.pos.y, 0f)
            Matrix.scaleM(transMatrix, 0, it.age + if(it.glow)2.2f else 0.0f, it.age, 0f)
            Matrix.multiplyMM(mvptMatrix, 0, mvpMatrix, 0, transMatrix, 0)

            GLES20.glUniformMatrix4fv(mvpMatrixHandler, 1, false, mvptMatrix, 0)


            GLES20.glUniform1f(ageHandler, min(it.age, 1.0f))

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


    }


    val size = 0.025f

    val layerCoords = floatArrayOf(
        -size, size, -0.1f,      // top left
        -size, -size, -0.1f,      // bottom left
        size, -size, -0.1f,      // bottom right
        size, size, -0.1f       // top right
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
}

class SeedData(
    var pos: Vector2f,
    var velocity: Vector2f,
    var age: Float,
    var glow: Boolean = false
) {


}