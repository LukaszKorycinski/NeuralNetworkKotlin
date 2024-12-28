package com.example.neuralnetworkkotlin.geometry.grass

import android.opengl.GLES31
import com.example.neuralnetworkkotlin.geometry.plain3d.nonanim.File3d
import com.example.neuralnetworkkotlin.geometry.plain3d.nonanim.MODELS_3D
import com.example.neuralnetworkkotlin.geometry.vectors.distance
import java.nio.Buffer
import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.nio.FloatBuffer
import javax.vecmath.Vector2f
import javax.vecmath.Vector3f
import kotlin.random.Random

class Grass(val file3Df: File3d) {
    var items: ArrayList<GrassData> = ArrayList()
    var wave = 0f

    val instancedBufferId = 0

    val QUANTITY = 20

    init {
        val density = 20f
        for (i in 0..QUANTITY) {
            items.add(
                GrassData(
                    position = Vector2f(
                        (Random.nextFloat() - .5f) * density,
                        (Random.nextFloat() - .5f) * density,
                    ), wave = Random.nextFloat() * 6f, kindIndexL = 0,
                    model = MODELS_3D.GRASS
                )
            )
        }
        items = items.filter { it.position.distance(Vector2f(0f, 0f)) > 5f } as ArrayList<GrassData>

        buildInstancedBuffer()
    }

    private fun buildInstancedBuffer() {

        GLES31.glBindBuffer(GLES31.GL_ARRAY_BUFFER, instancedBufferId)

        GLES31.glBufferData(
            GLES31.GL_ARRAY_BUFFER,
            QUANTITY * 4 * 2,
            null,
            GLES31.GL_STATIC_DRAW
        )

        val positionsBuffer: FloatBuffer

        val vbb = ByteBuffer.allocateDirect(QUANTITY * 4 * 2)
        vbb.order(ByteOrder.nativeOrder())
        positionsBuffer = vbb.asFloatBuffer()
        positionsBuffer.put(items.flatMap { listOf(it.position.x, it.position.y) }.toFloatArray())
        positionsBuffer.position(0)

        GLES31.glBufferSubData(GLES31.GL_ARRAY_BUFFER,
            0,
            QUANTITY * 4 * 2,
            positionsBuffer
        )

        //instancedBufferId
    }

    fun draw(mvpMatrix: FloatArray) {



        file3Df.drawInstanced(mvpMatrix, MODELS_3D.GRASS, instancedBufferId)



        file3Df.draw(mvpMatrix, MODELS_3D.GRASS, position = Vector2f())

    }
}