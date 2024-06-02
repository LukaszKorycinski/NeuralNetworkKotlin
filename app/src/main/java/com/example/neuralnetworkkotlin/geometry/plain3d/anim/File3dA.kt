package com.example.neuralnetworkkotlin.geometry.plain3d.anim

import android.content.Context
import com.example.neuralnetworkkotlin.gameLogic.strategy.Human
import com.example.neuralnetworkkotlin.geometry.vectors.Vector2f
import com.example.neuralnetworkkotlin.geometry.plain3d.Drawer
import com.example.neuralnetworkkotlin.geometry.plain3d.Loader
import com.example.neuralnetworkkotlin.geometry.plain3d.data.LoaderType
import com.example.neuralnetworkkotlin.geometry.vectors.copy
import com.example.neuralnetworkkotlin.helpers.getIdentityMatrix
import com.example.neuralnetworkkotlin.helpers.invert
import com.example.neuralnetworkkotlin.helpers.times
import com.example.neuralnetworkkotlin.helpers.translate
import com.example.neuralnetworkkotlin.renderer.TexturesLoader
import javax.vecmath.Vector2f

class File3dA(val context: Context, val textures: TexturesLoader) {

    private val drawer = Drawer(textures)
    private val loadedModels = mutableListOf<Loader>()

    init {
        MODELS_3DA.values().forEach { model ->
            loadedModels.add(Loader(context, LoaderType.ANIMATED).loadModel(model).fillBuffers())
        }
    }

    fun drawAnim(
        mvpMatrix: FloatArray,
        model: MODELS_3DA,
        position: Vector2f = Vector2f(0f),
        wave: Float,
        anim: Animations
    ) {
        drawer.draw(
            mvpMatrix,
            loadedModels[model.index],
            position,
            interpolateSkeletons(model, wave, anim)
        )
    }

    fun draGear(mvpMatrix: FloatArray, model: MODELS_3DA, human: Human) {
        drawer.draw(
            mvpMatrix,
            loadedModels[model.index],
            human.position,
            interpolateSkeletons(model, human.wave,  human.look.animation),
            human.look.direction.scaleX,
            human.box.y
        )
    }

    fun drawHuman(mvpMatrix: FloatArray, model: MODELS_3DA, human: Human) {
        drawer.bindProgram(loadedModels[model.index])
        drawer.drawHuman(
            mvpMatrix,
            loadedModels[model.index],
            human,
            interpolateSkeletons(model, human.wave, human.look.animation)
        )
    }

    private fun interpolateSkeletons(model: MODELS_3DA, wave: Float, animation: Animations): FloatArray {

        val currMatrixes = FloatArray(16 * loadedModels[model.index].bones.size)

        loadedModels[model.index].bones.forEachIndexed { index, bone ->
            var boneOffsetM = getIdentityMatrix().translate(
                -bone.offsetLocRot.loc.x,
                -bone.offsetLocRot.loc.y,
                bone.offsetLocRot.loc.z
            )
            boneOffsetM *= bone.offsetLocRot.quat.toRotationMatrix()

            val interpolatedFrame = bone.interpolatedFrame(wave)

            var bonTransformMatrix = getIdentityMatrix().translate(
                -interpolatedFrame.loc.x,
                -interpolatedFrame.loc.y,
                interpolatedFrame.loc.z
            )
            bonTransformMatrix *= interpolatedFrame.quat.toRotationMatrix4f()

            val boneMatrixTotal = boneOffsetM * bonTransformMatrix * boneOffsetM.invert()

            for (i in 0..15) {
                currMatrixes[index * 16 + i] = boneMatrixTotal[i]
            }
        }
        return currMatrixes
    }
}