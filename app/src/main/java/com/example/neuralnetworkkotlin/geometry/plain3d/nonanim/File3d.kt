package com.example.neuralnetworkkotlin.geometry.plain3d.nonanim

import android.content.Context
import android.opengl.GLES20
import com.example.neuralnetworkkotlin.ext.HALF_PI
import com.example.neuralnetworkkotlin.gameLogic.strategy.Human
import com.example.neuralnetworkkotlin.geometry.vectors.Vector2f
import com.example.neuralnetworkkotlin.geometry.plain3d.Drawer
import com.example.neuralnetworkkotlin.geometry.plain3d.Loader
import com.example.neuralnetworkkotlin.geometry.plain3d.data.LoaderType
import com.example.neuralnetworkkotlin.renderer.ShaderLoader
import com.example.neuralnetworkkotlin.renderer.TexturesLoader
import timber.log.Timber
import javax.vecmath.Vector2f

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
            Timber.e("humanAngle a$humanAngle")
            Timber.e("sword a$swordAngle")
        }


        drawer.drawPrepared(mvpMatrix, loadedModels[model.index], human.position)
    }

    fun draw(mvpMatrix: FloatArray, model: MODELS_3D, position: Vector2f = Vector2f(0f)) {
        drawer.bindProgram(loadedModels[model.index].modelInterface)
        drawer.draw(mvpMatrix, loadedModels[model.index], position)
    }

    fun drawTrees(
        mvpMatrix: FloatArray,
        model: MODELS_3D,
        position: Vector2f = Vector2f(0f),
        wave: Float,
        kind: Int
    ) {
        drawer.bindProgram(loadedModels[model.index].modelInterface)
        val waveHandler = GLES20.glGetUniformLocation(
            ShaderLoader.getShaderProgram(loadedModels[model.index].modelInterface.shader),
            "wave"
        )
        GLES20.glUniform1f(waveHandler, wave)
        val kindHandler = GLES20.glGetUniformLocation(
            ShaderLoader.getShaderProgram(loadedModels[model.index].modelInterface.shader),
            "kind"
        )

        GLES20.glUniform1f(kindHandler, kind.toFloat())
        drawer.draw(mvpMatrix, loadedModels[model.index], position)
    }


    fun drawBanner(
        mvpMatrix: FloatArray,
        model: MODELS_3D,
        position: Vector2f = Vector2f(0f),
        wave: Float,
        selected: Boolean
    ) {
        drawer.bindProgram(loadedModels[model.index].modelInterface)
        drawer.drawBanner(mvpMatrix, loadedModels[model.index], position, wave, selected)
    }

    private fun bindProgram(model: MODELS_3D) {
        drawer.bindProgram(loadedModels[model.index].modelInterface)
    }
}