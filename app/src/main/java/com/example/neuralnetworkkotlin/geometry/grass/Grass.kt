package com.example.neuralnetworkkotlin.geometry.grass

import android.opengl.GLES31
import com.example.neuralnetworkkotlin.geometry.plain3d.nonanim.File3d
import com.example.neuralnetworkkotlin.geometry.plain3d.nonanim.MODELS_3D
import com.example.neuralnetworkkotlin.geometry.vectors.distance
import com.example.neuralnetworkkotlin.helpers.Wave
import java.nio.Buffer
import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.nio.FloatBuffer
import javax.vecmath.Vector2f
import javax.vecmath.Vector3f
import kotlin.math.PI
import kotlin.random.Random

class Grass(val file3Df: File3d) {
    private var items: ArrayList<GrassData> = ArrayList()
    private var wave = Wave(0f)

    var instancedBuffer: FloatBuffer? = null

    val QUANTITY = 256

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
        val vbb = ByteBuffer.allocateDirect(QUANTITY * 4 * 2)
        vbb.order(ByteOrder.nativeOrder())
        instancedBuffer = vbb.asFloatBuffer()
        instancedBuffer?.put(items.flatMap { listOf(it.position.x, it.position.y) }.toFloatArray())
        instancedBuffer?.position(0)
    }

    fun draw(mvpMatrix: FloatArray) {

        wave += 0.02f

        instancedBuffer?.let {
            file3Df.bindProgram(MODELS_3D.GRASS)
            file3Df.setVariableF(MODELS_3D.GRASS, wave.value, "wave")
            file3Df.drawInstanced(mvpMatrix, MODELS_3D.GRASS, QUANTITY, it)
        }




        //file3Df.draw(mvpMatrix, MODELS_3D.GRASS, position = Vector2f())

    }
}