package com.example.neuralnetworkkotlin.geometry.plain3d

import android.content.Context
import com.example.neuralnetworkkotlin.ext.Vector2f
import com.example.neuralnetworkkotlin.helpers.getIdentityMatrix
import com.example.neuralnetworkkotlin.helpers.invert
import com.example.neuralnetworkkotlin.helpers.rotateZ
import com.example.neuralnetworkkotlin.helpers.times
import com.example.neuralnetworkkotlin.helpers.translate
import com.example.neuralnetworkkotlin.renderer.TexturesLoader
import timber.log.Timber
import javax.vecmath.Vector2f
import kotlin.math.max
import kotlin.math.min

class Plain3df(val context: Context, val textures: TexturesLoader) {

    private val drawer = Drawer(textures)
    private val loadedModels = mutableListOf<Loader>()
    var frameTest = 0

    init {
        MODELS_3D.values().forEach { model ->
            loadedModels.add(Loader(context, LoaderType.ANIMATED).loadModel(model).fillBuffers())
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

        val frame = max(0,min(frameTest, model.framesQty - 1))
        //val frame = if ((System.currentTimeMillis() % 1000)<500) { 1 } else { 0 }
//        val frame = when(System.currentTimeMillis() % 13000){
//            in 0..1000 -> 0
//            in 1000..2000 -> 1
//            in 2000..3000 -> 2
//            in 3000..4000 -> 3
//            in 4000..5000 -> 4
//            in 5000..6000 -> 5
//            in 6000..7000 -> 6
//            in 7000..8000 -> 7
//            in 8000..9000 -> 8
//            in 9000..10000 -> 9
//            in 10000..11000 -> 10
//            in 11000..12000 -> 11
//            in 12000..13000 -> 12
//            else -> 0
//        }

        val currMatrixes = FloatArray(16 * loadedModels[model.index].bones.size)

        loadedModels[model.index].bones.forEachIndexed { index, bone ->
            var boneOffsetM = getIdentityMatrix()

            //boneOffsetM = boneOffsetM * bone.offsetLocRot.quat.toRotationMatrix()
            boneOffsetM = boneOffsetM.translate(-bone.offsetLocRot.loc.x, -bone.offsetLocRot.loc.y, bone.offsetLocRot.loc.z)
            boneOffsetM = boneOffsetM * bone.offsetLocRot.quat.toRotationMatrix()



            var bonTransformMatrix = getIdentityMatrix()



            //bonTransformMatrix = bonTransformMatrix.rotateZ(45f)
            bonTransformMatrix = bonTransformMatrix.translate(-bone.frames[frame].locRot.loc.x, -bone.frames[frame].locRot.loc.y, bone.frames[frame].locRot.loc.z)
            bonTransformMatrix = bonTransformMatrix * bone.frames[frame].locRot.quat.toRotationMatrix4f()





            Timber.e("bone: ${bone.name}")

            Timber.e("boneOffset: locX:  ${bone.offsetLocRot.loc.x } locY: ${bone.offsetLocRot.loc.y} locZ: ${bone.offsetLocRot.loc.z}")
            Timber.e("boneOffset: quatX: ${bone.offsetLocRot.quat.x} quatY: ${bone.offsetLocRot.quat.y} quatZ: ${bone.offsetLocRot.quat.z} quatW: ${bone.offsetLocRot.quat.w}")

            Timber.e("locX: ${bone.frames[frame].locRot.loc.x} locY: ${bone.frames[frame].locRot.loc.y} locZ: ${bone.frames[frame].locRot.loc.z}")
            Timber.e("quatX: ${bone.frames[frame].locRot.quat.x} quatY: ${bone.frames[frame].locRot.quat.y} quatZ: ${bone.frames[frame].locRot.quat.z} quatW: ${bone.frames[frame].locRot.quat.w}")

            //val boneMatrixTotal = boneOffsetM.invert() * bonTransformMatrix * boneOffsetM
            val boneMatrixTotal = boneOffsetM * bonTransformMatrix * boneOffsetM.invert()

                for (i in 0..15) {
                currMatrixes[index * 16 + i] = boneMatrixTotal[i]
            }
        }
        return currMatrixes
    }
}