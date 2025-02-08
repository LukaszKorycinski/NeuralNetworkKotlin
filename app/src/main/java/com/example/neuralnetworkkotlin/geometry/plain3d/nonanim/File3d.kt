package com.example.neuralnetworkkotlin.geometry.plain3d.nonanim

import android.content.Context
import android.opengl.GLES20
import android.opengl.GLES31
import android.opengl.Matrix
import com.example.neuralnetworkkotlin.ext.HALF_PI
import com.example.neuralnetworkkotlin.gameLogic.strategy.Human
import com.example.neuralnetworkkotlin.geometry.vectors.Vector2f
import com.example.neuralnetworkkotlin.geometry.plain3d.Drawer
import com.example.neuralnetworkkotlin.geometry.plain3d.Loader
import com.example.neuralnetworkkotlin.geometry.plain3d.data.LoaderType
import com.example.neuralnetworkkotlin.geometry.vectors.vector3f.Vector3f
import com.example.neuralnetworkkotlin.renderer.ShaderLoader
import com.example.neuralnetworkkotlin.renderer.TEXTURES
import com.example.neuralnetworkkotlin.renderer.TexturesLoader
import timber.log.Timber
import java.nio.FloatBuffer
import javax.vecmath.Vector2f
import javax.vecmath.Vector3f

class File3d(val context: Context, val textures: TexturesLoader) {

    private val drawer = Drawer(textures)
    private val loadedModels = mutableListOf<Loader>()

    init {
        MODELS_3D.values().forEach { model ->
            loadedModels.add(Loader(context, LoaderType.STATIC).loadModel(model).fillBuffers())
        }
    }


    fun bindThingsHuman() {
        bindProgram(MODELS_3D.MEN)
        val tex = loadedModels[MODELS_3D.MEN.index].model3d.texture.id
        drawer.bindTexture(tex, MODELS_3D.MEN.shader)
        val texA = loadedModels[MODELS_3D.MEN.index].model3d.textureAlpha?.id ?: tex
        drawer.bindTexture(texA, MODELS_3D.MEN.shader, "a_Texture", 1)
    }

    private fun minMaxSwordAngle(angle: Float): Float {
        //wieksze od -4.520323 i -miejsze po -2.5 ban
        val  min = -4.520323f
        val  max = -2.5f
        val middle = (min + max) / 2
        return if (angle > min && angle < max) { if (angle > middle) max else min } else { angle }
        return angle
    }

    fun drawHuman(
        mvpMatrix: FloatArray,
        model: MODELS_3D,
        human: Human,
    ) {
        drawer.bindFloat(model, "legsWave", human.waveWalk)
        val humanAngle = -human.angle - HALF_PI - human.waveSword
        val swordAngle = minMaxSwordAngle(humanAngle)
        drawer.bindFloat(model, "swordWave", swordAngle)

        if (human.isCenturion) {
            //Timber.e("humanAngle a$humanAngle")
            //Timber.e("sword a$swordAngle")
        }


        drawer.drawPrepared(mvpMatrix, loadedModels[model.index], human.position)
    }

    fun draw(mvpMatrix: FloatArray, model: MODELS_3D, position: Vector3f = Vector3f(0f)) {

        drawer.draw(mvpMatrix, loadedModels[model.index], position)
    }

    fun drawInstanced(mvpMatrix: FloatArray, model: MODELS_3D, quantity: Int, instancedBuffer: FloatBuffer) {
        drawer.drawInstanced(mvpMatrix, loadedModels[model.index], quantity, instancedBuffer)
    }

    fun setVariable3F(model: MODELS_3D, value: Vector3f, name: String) {
        val handler = GLES31.glGetUniformLocation(
            ShaderLoader.getShaderProgram(model.shader),
            name
        )
        GLES31.glUniform3f(handler, value.x, value.y, value.z)
    }

    fun setVariableF(model: MODELS_3D, value: Float, name: String) {
        val handler = GLES31.glGetUniformLocation(
            ShaderLoader.getShaderProgram(model.shader),
            name
        )
        GLES31.glUniform1f(handler, value)
    }

    fun setVariableTexture1(model: MODELS_3D, texture: TEXTURES, name: String) {
        val handler = GLES31.glGetUniformLocation(
            ShaderLoader.getShaderProgram(model.shader),
            name
        )
        GLES31.glUniform1i(handler, 1)
        GLES31.glActiveTexture(GLES20.GL_TEXTURE1)

        GLES31.glBindTexture(GLES20.GL_TEXTURE_2D, textures.textureHandle[texture.id])
    }

    fun setVariableMatrix4fv(model: MODELS_3D, value: FloatArray, name: String) {
        val handler = GLES31.glGetUniformLocation(
            ShaderLoader.getShaderProgram(model.shader),
            name
        )
        GLES31.glUniformMatrix4fv(handler, 1, false, value, 0)
    }

    fun drawTrees(
        mvpMatrix: FloatArray,
        model: MODELS_3D,
        position: Vector3f = Vector3f(0f),
        wave: Float,
        kind: Int
    ) {
        setVariableF(model, wave, "wave")
        setVariableF(model, kind.toFloat(), "kind")

        val tmpMatrix = FloatArray(16)
        Matrix.setIdentityM(tmpMatrix, 0)
        Matrix.translateM(tmpMatrix, 0, position.x, position.y, position.z)

        setVariableMatrix4fv(model, tmpMatrix, "uWorldMatrix")

        drawer.draw(mvpMatrix, loadedModels[model.index], position)
    }


    fun drawBanner(
        mvpMatrix: FloatArray,
        model: MODELS_3D,
        position: Vector2f = Vector2f(0f),
        wave: Float,
        waveWalk: Float,
        selected: Boolean
    ) {
        drawer.bindProgram(model)
        drawer.drawBanner(mvpMatrix, loadedModels[model.index], position, wave, waveWalk, selected)
    }

    fun bindProgram(model: MODELS_3D) {
        drawer.bindProgram(model)
    }
}