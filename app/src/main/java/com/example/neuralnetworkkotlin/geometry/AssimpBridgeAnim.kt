package com.example.neuralnetworkkotlin.geometry

import android.content.Context
import android.opengl.GLES20
import android.opengl.Matrix
import com.example.neuralnetworkkotlin.assimp.AiScene
import com.example.neuralnetworkkotlin.assimp.Importer
import com.example.neuralnetworkkotlin.assimp.getFileFromAssets
import com.example.neuralnetworkkotlin.ext.Vector2f
import com.example.neuralnetworkkotlin.geometry.vectors.Quaternion
import com.example.neuralnetworkkotlin.helpers.getIdentityMatrix
import com.example.neuralnetworkkotlin.helpers.intIterator
import com.example.neuralnetworkkotlin.helpers.times
import com.example.neuralnetworkkotlin.helpers.translate
import com.example.neuralnetworkkotlin.renderer.ShaderLoader
import com.example.neuralnetworkkotlin.renderer.Shaders
import com.example.neuralnetworkkotlin.renderer.TEXTURES
import com.example.neuralnetworkkotlin.renderer.TexturesLoader
import timber.log.Timber
import java.nio.ByteBuffer
import java.nio.ByteOrder
import javax.vecmath.Vector2f

enum class MODELS_ANIM_ASSIMP(
    val index: Int,
    val assetFilePath: String,
    val shader: Shaders,
    val texture: TEXTURES
) {
    DRAGON_MODEL(0, "kwadrat.dae", Shaders.BASIC_ANIM, TEXTURES.TERRAINTEXTURE),
    DRAGON_MODEL2(1, "test.dae", Shaders.BASIC_ANIM, TEXTURES.DRAGON),
}


class AssimpBridgeAnim(val context: Context, val textures: TexturesLoader) {
    val files = mutableListOf(
        AssimpAnimFile(
            MODELS_ANIM_ASSIMP.DRAGON_MODEL,
        ),
        AssimpAnimFile(
            MODELS_ANIM_ASSIMP.DRAGON_MODEL2,
        )
    )

    init {
        files.forEach { assimpAnimFile ->
            assimpAnimFile.scene = Importer().readFile(
                getFileFromAssets(
                    context,
                    assimpAnimFile.file.assetFilePath
                ).absolutePath
            )
            Timber.e("BONES: ${assimpAnimFile.bonesQty}")
        }
    }

    var frame = 0

    fun interpolateSkeletons(id: MODELS_ANIM_ASSIMP): FloatArray {

        frame = if (System.currentTimeMillis() % 1000 < 500) 0 else 1

        val currMatrixes = FloatArray(16 * files[id.index].bonesQty)
        val scene = files[id.index].scene

        val outputMatricse: ArrayList<FloatArray> = arrayListOf()

        scene?.animations?.first()?.channels?.subList(0, 2)?.forEachIndexed { index, animation ->
            val boneOffsetMatrix = scene.meshes.first().bones[index].offsetMatrix
            val globalInverseTransform = scene.rootNode.transformation.inverse().toFloatArray()

            Timber.e("START current: ${animation?.nodeName}")
            val parent = scene.rootNode.children?.first()?.children?.firstOrNull { children ->
                        Timber.e("sprawdzam z top hierarchi ${children.name}")
                        children.children
                            ?.firstOrNull { children2 ->
                                Timber.e("sprawdzam jego childy ${children2.name}")
                                children2.name == animation?.nodeName
                            } != null
                    }// find parenta
            Timber.e("znalazlo: ${parent?.name}, ktory ma dzieci:${parent?.children?.joinToString { it.name }}")
            Timber.e("END")

            val parentAnimation = parent?.let {
                scene.animations.first().channels.firstOrNull { it?.nodeName == parent.name }//to są animacje parenta, obie klatki
            }
            val boneOffsetMatrixParent = parent?.let {
                scene.meshes.first().bones.firstOrNull { it.name == parent.name }?.offsetMatrix?.toFloatArray()
            } ?: getIdentityMatrix()

            var translationMatrixParent = getIdentityMatrix()
            var rotationMatrixParent = getIdentityMatrix()

            parentAnimation?.let {
                translationMatrixParent = translationMatrixParent.translate(
                    parentAnimation.positionKeys.get(frame).value.x,
                    parentAnimation.positionKeys.get(frame).value.y,
                    parentAnimation.positionKeys.get(frame).value.z,
                )
                parentAnimation.rotationKeys.get(frame).value.let {
                    Quaternion(it).toRotationMatrix()
                }.let {
                    rotationMatrixParent = it
                }
            }

            val translationMatrix = getIdentityMatrix().translate(
                animation?.positionKeys?.get(frame)?.value?.x ?: 0.0f,
                animation?.positionKeys?.get(frame)?.value?.y ?: 0.0f,
                animation?.positionKeys?.get(frame)?.value?.z ?: 0.0f,
            )

            val rotationMatrix = animation?.rotationKeys?.get(frame)?.value?.let {
                Quaternion(it).toRotationMatrix()
            } ?: getIdentityMatrix()

            val rotationChildPArentMatrix=rotationMatrixParent*rotationMatrix
            val translationChildParentMatrix=translationMatrixParent*translationMatrix

            val boneLocalTransformMatrix = rotationChildPArentMatrix*translationChildParentMatrix

            val finalBoneMatrice = boneLocalTransformMatrix * boneOffsetMatrix.toFloatArray()

            outputMatricse.add(finalBoneMatrice)
        }

        outputMatricse.forEachIndexed { index, floats ->
            for (i in 0..15) {
                currMatrixes[index * 16 + i] = floats[i]
            }
        }
        return currMatrixes
    }

    fun draw(mvpMatrix: FloatArray, id: MODELS_ANIM_ASSIMP, position: Vector2f = Vector2f(0f)) {
        val currentBonesPosesArray = interpolateSkeletons(id)

        val tmpMatrix = FloatArray(16)
        Matrix.setIdentityM(tmpMatrix, 0)
        Matrix.translateM(tmpMatrix, 0, position.x, position.y, 0.0f)

        GLES20.glUseProgram(ShaderLoader.getShaderProgram(files[id.index].file.shader))
        val iVPMatrix = GLES20.glGetUniformLocation(
            ShaderLoader.getShaderProgram(files[id.index].file.shader),
            "uMVPMatrix"
        )
        Matrix.multiplyMM(tmpMatrix, 0, mvpMatrix, 0, tmpMatrix, 0)
        GLES20.glUniformMatrix4fv(iVPMatrix, 1, false, tmpMatrix, 0)

        val texHandler = GLES20.glGetUniformLocation(
            ShaderLoader.getShaderProgram(files[id.index].file.shader),
            "u_Texture"
        )
        GLES20.glUniform1i(texHandler, 0)
        GLES20.glActiveTexture(GLES20.GL_TEXTURE0)
        GLES20.glBindTexture(
            GLES20.GL_TEXTURE_2D,
            textures.textureHandle[files[id.index].file.texture.id]
        )

        val bonesMatricesHandle = GLES20.glGetUniformLocation(
            ShaderLoader.getShaderProgram(files[id.index].file.shader),
            "bonesMatrices"
        )
        GLES20.glUniformMatrix4fv(
            bonesMatricesHandle,
            files[id.index].bonesQty,
            false,
            currentBonesPosesArray,
            0
        )

        val mPositionHandle = GLES20.glGetAttribLocation(
            ShaderLoader.getShaderProgram(files[id.index].file.shader),
            "vPosition"
        )
        GLES20.glEnableVertexAttribArray(mPositionHandle)
        GLES20.glVertexAttribPointer(
            mPositionHandle,
            3,
            GLES20.GL_FLOAT,
            false,
            0,
            files[id.index].buffers.vertexBuffer
        )

        val mTexCoordHandle = GLES20.glGetAttribLocation(
            ShaderLoader.getShaderProgram(files[id.index].file.shader),
            "a_TexCoordinate"
        )
        GLES20.glEnableVertexAttribArray(mTexCoordHandle)
        GLES20.glVertexAttribPointer(
            mTexCoordHandle,
            3,
            GLES20.GL_FLOAT,
            false,
            0,
            files[id.index].buffers.texBuffer
        )

        GLES20.glDrawElements(
            GLES20.GL_TRIANGLES,
            files[id.index].buffers.indicesQty,
            GLES20.GL_UNSIGNED_SHORT,
            files[id.index].buffers.indicesBuffer
        )
        GLES20.glDisableVertexAttribArray(mPositionHandle) //pole do optymalizacji

        GLES20.glDisableVertexAttribArray(mTexCoordHandle) //pole do optymalizacji
    }
}

class AssimpAnimFile(
    val file: MODELS_ANIM_ASSIMP,
) {
    var buffers: BuffersAssimp = BuffersAssimp()
    var bonesQty = 0

    var scene: AiScene? = null
        set(value) {
            field = value

            bonesQty = scene?.meshes?.first()?.bones?.size ?: 0

            val coordsFloatArray = FloatArray((scene?.meshes?.first()?.vertices?.size ?: 0) * 3)
            val texCoordsFloatArray =
                FloatArray((scene?.meshes?.first()?.textureCoords?.first()?.size ?: 0) * 3)
            val indicesShortArray = ShortArray((scene?.meshes?.first()?.faces?.size ?: 0) * 3)

            intIterator = 0
            scene?.meshes?.first()?.vertices?.forEach { vertice ->
                coordsFloatArray[intIterator] = -vertice.x
                coordsFloatArray[intIterator] = vertice.y
                coordsFloatArray[intIterator] = -vertice.z
            }
            val vbb = ByteBuffer.allocateDirect(coordsFloatArray.size * 4)
            vbb.order(ByteOrder.nativeOrder())
            buffers.vertexBuffer = vbb.asFloatBuffer()
            buffers.vertexBuffer?.put(coordsFloatArray)
            buffers.vertexBuffer?.position(0)

            intIterator = 0
            scene?.meshes?.first()?.textureCoords?.first()?.forEachIndexed { index, textCoord ->
                texCoordsFloatArray[intIterator] = textCoord[0]
                texCoordsFloatArray[intIterator] = 1f - textCoord[1]
                texCoordsFloatArray[intIterator] =
                    scene?.meshes?.first()?.bones?.indexOfFirst { it.weights.firstOrNull { it.vertexId == index } != null }
                        ?.toFloat() ?: 0.0f
            }
            val tcbb = ByteBuffer.allocateDirect(texCoordsFloatArray.size * 4)
            tcbb.order(ByteOrder.nativeOrder())
            buffers.texBuffer = tcbb.asFloatBuffer()
            buffers.texBuffer?.put(texCoordsFloatArray)
            buffers.texBuffer?.position(0)

            buffers.indicesQty = (scene?.meshes?.first()?.faces?.size ?: 0) * 3
            var index = 0
            scene?.meshes?.first()?.faces?.forEach { indice ->
                indicesShortArray[index] = indice[0].toShort()
                index++
                indicesShortArray[index] = indice[1].toShort()
                index++
                indicesShortArray[index] = indice[2].toShort()
                index++
            }
            val ibb = ByteBuffer.allocateDirect(buffers.indicesQty * 2)
            ibb.order(ByteOrder.nativeOrder())
            buffers.indicesBuffer = ibb.asShortBuffer()
            buffers.indicesBuffer?.put(indicesShortArray)
            buffers.indicesBuffer?.position(0)
        }
}