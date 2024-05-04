package com.example.neuralnetworkkotlin.geometry.plain3d

import android.content.Context
import com.example.neuralnetworkkotlin.ext.Vector2f
import com.example.neuralnetworkkotlin.ext.times
import com.example.neuralnetworkkotlin.ext.translate
import com.example.neuralnetworkkotlin.geometry.vectors.Quaternion
import com.example.neuralnetworkkotlin.helpers.getIdentityMatrix
import com.example.neuralnetworkkotlin.renderer.TexturesLoader
import javax.vecmath.Vector2f

class Plain3df(val context: Context, val textures: TexturesLoader) {

    private val drawer = Drawer(textures)
    private val loadedModels = mutableListOf<Loader>()

    init {
        MODELS_3D.values().forEach { model ->
            loadedModels.add(Loader(context).loadModel(model).fillBuffers())
        }
    }

    fun draw(mvpMatrix: FloatArray, model: MODELS_3D, position: Vector2f = Vector2f(0f)) {
        drawer.draw(mvpMatrix, loadedModels[model.index], position)
    }

    fun drawAnim(mvpMatrix: FloatArray, model: MODELS_3D, position: Vector2f = Vector2f(0f)) {
        drawer.draw(mvpMatrix, loadedModels[model.index], position, interpolateSkeletons(model))
    }

    private fun interpolateSkeletons(model: MODELS_3D): FloatArray {
        //mam zwrócić po 4x4 na każdą kość w danej klatce
        val frame = 0

        val currMatrixes = FloatArray(16 * loadedModels[model.index].bones.size)

        loadedModels[model.index].bones.forEachIndexed { index, bone ->
            var boneOffsetM = getIdentityMatrix()
            boneOffsetM.translate(bone.offsetLocRot.loc.x, bone.offsetLocRot.loc.y, bone.offsetLocRot.loc.z)
            boneOffsetM = boneOffsetM * bone.offsetLocRot.quat.toRotationMatrix()

            var bonTransformMatrix = getIdentityMatrix()
            bonTransformMatrix.translate(bone.frames[frame].locRot.loc.x, bone.frames[frame].locRot.loc.y, bone.frames[frame].locRot.loc.z)
            bonTransformMatrix = bonTransformMatrix * bone.frames[frame].locRot.quat.toRotationMatrix()

            val boneMatrixTotal = boneOffsetM * bonTransformMatrix

            for (i in 0..15) {
                currMatrixes[index * 16 + i] = boneMatrixTotal[i]
            }
        }
        return currMatrixes
    }
}