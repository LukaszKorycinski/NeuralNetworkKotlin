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

    fun drawAnim(mvpMatrix: FloatArray, model: MODELS_3D, position: Vector2f = Vector2f(0f), anim: Animations) {
        drawer.draw(mvpMatrix, loadedModels[model.index], position, interpolateSkeletons(model, anim))
    }

    fun drawHuman(mvpMatrix: FloatArray, model: MODELS_3D, position: Vector2f = Vector2f(0f), anim: Animations, variant: Int) {
        drawer.drawHuman(mvpMatrix, loadedModels[model.index], position, interpolateSkeletons(model, anim), variant)
    }

    var frame = 0f

    private fun interpolateSkeletons(model: MODELS_3D, animation: Animations): FloatArray {
        //mam zwrócić po 4x4 na każdą kość w danej klatce



        if(frame >= animation.end) frame = animation.start.toFloat()
        if(frame < animation.start) frame = animation.start.toFloat()

        frame += animation.speed * 0.25f


        val currMatrixes = FloatArray(16 * loadedModels[model.index].bones.size)

        loadedModels[model.index].bones.forEachIndexed { index, bone ->
            var boneOffsetM = getIdentityMatrix()

            //boneOffsetM = boneOffsetM * bone.offsetLocRot.quat.toRotationMatrix()
            boneOffsetM = boneOffsetM.translate(-bone.offsetLocRot.loc.x, -bone.offsetLocRot.loc.y, bone.offsetLocRot.loc.z)
            boneOffsetM = boneOffsetM * bone.offsetLocRot.quat.toRotationMatrix()



            var bonTransformMatrix = getIdentityMatrix()

            val interpolatedFrame = bone.interpolatedFrame(frame)

            //bonTransformMatrix = bonTransformMatrix.rotateZ(45f)
            bonTransformMatrix = bonTransformMatrix.translate(-interpolatedFrame.loc.x, -interpolatedFrame.loc.y, interpolatedFrame.loc.z)
            bonTransformMatrix = bonTransformMatrix * interpolatedFrame.quat.toRotationMatrix4f()





//            Timber.e("bone: ${bone.name}")
//
//            Timber.e("boneOffset: locX:  ${bone.offsetLocRot.loc.x } locY: ${bone.offsetLocRot.loc.y} locZ: ${bone.offsetLocRot.loc.z}")
//            Timber.e("boneOffset: quatX: ${bone.offsetLocRot.quat.x} quatY: ${bone.offsetLocRot.quat.y} quatZ: ${bone.offsetLocRot.quat.z} quatW: ${bone.offsetLocRot.quat.w}")
//
//            Timber.e("locX: ${bone.frames[frame].locRot.loc.x} locY: ${bone.frames[frame].locRot.loc.y} locZ: ${bone.frames[frame].locRot.loc.z}")
//            Timber.e("quatX: ${bone.frames[frame].locRot.quat.x} quatY: ${bone.frames[frame].locRot.quat.y} quatZ: ${bone.frames[frame].locRot.quat.z} quatW: ${bone.frames[frame].locRot.quat.w}")

            //val boneMatrixTotal = boneOffsetM.invert() * bonTransformMatrix * boneOffsetM
            val boneMatrixTotal = boneOffsetM * bonTransformMatrix * boneOffsetM.invert()

                for (i in 0..15) {
                currMatrixes[index * 16 + i] = boneMatrixTotal[i]
            }
        }
        return currMatrixes
    }
}