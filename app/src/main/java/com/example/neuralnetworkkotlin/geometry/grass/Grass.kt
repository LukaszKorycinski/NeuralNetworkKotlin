package com.example.neuralnetworkkotlin.geometry.grass

import com.example.neuralnetworkkotlin.geometry.plain3d.nonanim.File3d
import com.example.neuralnetworkkotlin.geometry.plain3d.nonanim.MODELS_3D
import com.example.neuralnetworkkotlin.helpers.Wave
import com.example.neuralnetworkkotlin.renderer.TEXTURES
import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.nio.FloatBuffer
import javax.vecmath.Vector2f
import javax.vecmath.Vector3f
import kotlin.random.Random

class Grass(val file3Df: File3d) {
    private var items: ArrayList<GrassData> = ArrayList()
    private var wave = Wave(0f)

    var instancedBuffer: FloatBuffer? = null
    var instancedBuffer2: FloatBuffer? = null

    val QUANTITY = 512

    init {
        generateRandomGrass()
        buildInstancedBuffer()
        generateRandomGrass()
        buildInstancedBuffer2()
    }

    private fun generateRandomGrass() {
        val densityX = 40f
        val densityY = 20f
        items.clear()
        for (i in 0..QUANTITY) {
            items.add(
                GrassData(
                    position = Vector2f(
                        (Random.nextFloat() - .5f) * densityX,
                        (Random.nextFloat() - .5f) * densityY,
                    ), wave = Random.nextFloat() * 6f, kindIndexL = 0,
                    model = MODELS_3D.GRASS
                )
            )
        }
    }

    private fun buildInstancedBuffer() {
        val vbb = ByteBuffer.allocateDirect((QUANTITY+1) * 4 * 2)
        vbb.order(ByteOrder.nativeOrder())
        instancedBuffer = vbb.asFloatBuffer()
        instancedBuffer?.put(items.flatMap { listOf(it.position.x, it.position.y) }.toFloatArray())
        instancedBuffer?.position(0)
    }

    private fun buildInstancedBuffer2() {
        val vbb = ByteBuffer.allocateDirect((QUANTITY+1) * 4 * 2)
        vbb.order(ByteOrder.nativeOrder())
        instancedBuffer2 = vbb.asFloatBuffer()
        instancedBuffer2?.put(items.flatMap { listOf(it.position.x, it.position.y) }.toFloatArray())
        instancedBuffer2?.position(0)
    }

    fun draw(mvpMatrix: FloatArray, eyePosition: Vector3f) {

        wave += 0.03f
        file3Df.bindProgram(MODELS_3D.GRASS)
        file3Df.setVariable3F(MODELS_3D.GRASS, eyePosition, "eyePosition")
        instancedBuffer?.let {


            file3Df.setVariableF(MODELS_3D.GRASS, wave.value, "wave")
            file3Df.setVariableTexture1(MODELS_3D.GRASS, TEXTURES.SHADOW_TERRAIN, "shadowTexture")
            file3Df.drawInstanced(mvpMatrix, MODELS_3D.GRASS, QUANTITY, it)
        }

        instancedBuffer2?.let {
            file3Df.setVariableF(MODELS_3D.GRASS2, wave.value, "wave")
            file3Df.setVariableTexture1(MODELS_3D.GRASS2, TEXTURES.SHADOW_TERRAIN,"shadowTexture")
            file3Df.drawInstanced(mvpMatrix, MODELS_3D.GRASS2, QUANTITY, it)
        }




        //file3Df.draw(mvpMatrix, MODELS_3D.GRASS, position = Vector2f())

    }
}