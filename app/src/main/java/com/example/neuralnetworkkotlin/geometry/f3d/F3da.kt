package com.example.neuralnetworkkotlin.geometry.f3d

import android.content.Context
import android.opengl.GLES20
import android.opengl.Matrix
import com.example.neuralnetworkkotlin.R
import com.example.neuralnetworkkotlin.assimp.Importer
import com.example.neuralnetworkkotlin.assimp.getFileFromAssets
import com.example.neuralnetworkkotlin.ext.Vector2f
import com.example.neuralnetworkkotlin.ext.readTextFile
import com.example.neuralnetworkkotlin.ext.xmlToJson
import com.example.neuralnetworkkotlin.geometry.MODELS_3D
import com.example.neuralnetworkkotlin.geometry.f3d.collada.ColladaFile
import com.example.neuralnetworkkotlin.geometry.vectors.Quaternion
import com.example.neuralnetworkkotlin.helpers.mullEvery
import com.google.gson.Gson
import java.nio.FloatBuffer
import java.nio.ShortBuffer
import javax.vecmath.Vector2f
import javax.vecmath.Vector3f
import com.example.neuralnetworkkotlin.geometry.vectors.Vector3i
import com.example.neuralnetworkkotlin.helpers.ControlHelper
import com.example.neuralnetworkkotlin.helpers.intIterator
import com.example.neuralnetworkkotlin.renderer.ShaderLoader
import com.example.neuralnetworkkotlin.renderer.Shaders
import com.example.neuralnetworkkotlin.renderer.TEXTURES
import com.example.neuralnetworkkotlin.renderer.TexturesLoader
import timber.log.Timber
import java.nio.ByteBuffer
import java.nio.ByteOrder
import javax.vecmath.Matrix4f
import javax.vecmath.Quat4f

enum class MODELS_3DA(
    val index: Int,
    val rawResId: Int,
    val shader: Shaders,
    val texture: TEXTURES
) {
    DRAGON_MODEL(0, R.raw.kwadrat, Shaders.BASIC_ANIM, TEXTURES.TERRAINTEXTURE),
    DRAGON2_MODEL(1, R.raw.kwadrat, Shaders.BASIC_ANIM, TEXTURES.DRAGON),
}

class File3da(
    val fileName: MODELS_3DA,
    var vertexBuffer: FloatBuffer? = null,
    var texBuffer: FloatBuffer? = null,
    var indicesBuffer: ShortBuffer? = null,
    var indicesQty: Int = 0,
    var bones: ArrayList<Bones> = ArrayList(),
) {
    lateinit var bonseMatrices: FloatArray

    var bonesIndices: ArrayList<Float> = ArrayList()
        set(value) {
            val tmpList = ArrayList<Float>()
            for (i in value.indices) {
                if (i % 2 == 0) {
                    tmpList.add(value[i])
                }
            }
            field = tmpList
        }

    fun generateIndicesObject(
        vertices: MutableList<Vector3f>,
        textCoords: MutableList<Vector2f>,
        indices: MutableList<Int>,
        boneIndicesIN: java.util.ArrayList<Float>
    ) {
        bonesIndices = boneIndicesIN
        val coordsFloatArray = FloatArray(vertices.size * 3)
        val texCoordsFloatArray = FloatArray(vertices.size * 3)
        val indicesShortArray = ShortArray(indices.size)

        intIterator = 0
        vertices.forEach { vertice ->
            coordsFloatArray[intIterator] = -vertice.x
            coordsFloatArray[intIterator] = vertice.y
            coordsFloatArray[intIterator] = -vertice.z
        }
        val vbb = ByteBuffer.allocateDirect(coordsFloatArray.size * 4)
        vbb.order(ByteOrder.nativeOrder())
        vertexBuffer = vbb.asFloatBuffer()
        vertexBuffer?.put(coordsFloatArray)
        vertexBuffer?.position(0)

        intIterator = 0
        var boneIterator = 0
        textCoords.forEach { textCoord ->
            texCoordsFloatArray[intIterator] = textCoord.x
            texCoordsFloatArray[intIterator] = 1f - textCoord.y
            texCoordsFloatArray[intIterator] = bonesIndices[boneIterator]
            boneIterator++
        }
        val tcbb = ByteBuffer.allocateDirect(texCoordsFloatArray.size * 4)
        tcbb.order(ByteOrder.nativeOrder())
        texBuffer = tcbb.asFloatBuffer()
        texBuffer?.put(texCoordsFloatArray)
        texBuffer?.position(0)

        indicesQty = indices.size
        indices.forEachIndexed { index, indice ->
            indicesShortArray[index] = indice.toShort()
        }
        val ibb = ByteBuffer.allocateDirect(indices.size * 2)
        ibb.order(ByteOrder.nativeOrder())
        indicesBuffer = ibb.asShortBuffer()
        indicesBuffer?.put(indicesShortArray)
        indicesBuffer?.position(0)

        Timber.e("vertices: "+coordsFloatArray.joinToString { "$it" })
    }

//    fun setIndicesByFloat(intArray: java.util.ArrayList<Int>) {
//        var i = 0
//        val indices = java.util.ArrayList<Vector3i>()
//        while (i < intArray.size) {
//            indices.add(Vector3i(intArray[i], intArray[i + 1], intArray[i + 2]))
//            i += 3
//        }
//    }
}

class Bones {
    var posesMatrices: FloatArray? = null
}

class F3da(val appContext: Context, val textures: TexturesLoader) {
    val files = mutableListOf(
        File3da(
            MODELS_3DA.DRAGON_MODEL
        ),
        File3da(
            MODELS_3DA.DRAGON2_MODEL
        )
    )


    var frame: Int = 0

    private fun interpolateSkeletons(id: MODELS_3DA): FloatArray {
        val currMatrixes = FloatArray(16 * files[id.index].bones.size)

        if (System.currentTimeMillis() % 1000 < 500)
            frame = 0
        else
            frame = 1
        Timber.e("begin:")
        for (boneIndex in 0 until files[id.index].bones.size) {
            val tmpMatrixStart = FloatArray(16)


            files[id.index].bonseMatrices.let { bonesMatricesNS ->
                for (j in 0..15) {
                    tmpMatrixStart[j] = bonesMatricesNS[boneIndex * 32 + j + frame * 16]
                }
            }


            Timber.e("frame: $frame, bone: $boneIndex, " + files[id.index].fileName.name + ": " + tmpMatrixStart.joinToString { "($it)" })



//            files[id.index].bonseMatrices.let { bonesMatricesNS ->
//                for (j in 0..15) {
//                    tmpMatrixStart[j] = bonesMatricesNS[i * 16 * 2 + j + frame * 16]
//                    //tmpMatrixStart[j] = bonesMatricesNS[i * 16 * 2 + j +16]
//                }
//            }

            val matStart = Matrix4f(tmpMatrixStart)


//            val tmpMatrixOut = Matrix4f()
//            tmpMatrixOut.setIdentity()
//            tmpMatrixOut.setTranslation(Vector3f(posStart.x, posStart.y, posStart.z))
//            tmpMatrixOut.setRotation(
//                Quat4f(
//                    quaterionStart.x,
//                    quaterionStart.y,
//                    quaterionStart.z,
//                    quaterionStart.w
//                )
//            )

            var iterator = 0
            for (x in 0..3)
                for (y in 0..3) {
                    currMatrixes[boneIndex * 16 + iterator] = matStart.getElement(x, y)
                    iterator++
                }
        }
        Timber.e("end: "+currMatrixes.joinToString { "$it" })
        return currMatrixes
    }

    fun draw(mvpMatrix: FloatArray, id: MODELS_3DA, position: Vector2f = Vector2f(0f)) {

        val currentBonesPosesArray = interpolateSkeletons(files[id.index].fileName)

        val tmpMatrix = FloatArray(16)
        Matrix.setIdentityM(tmpMatrix, 0)
        Matrix.translateM(tmpMatrix, 0, position.x, position.y, 0.0f)

        GLES20.glUseProgram(ShaderLoader.getShaderProgram(files[id.index].fileName.shader))
        val iVPMatrix = GLES20.glGetUniformLocation(
            ShaderLoader.getShaderProgram(files[id.index].fileName.shader),
            "uMVPMatrix"
        ) //, iVMatrix;
        Matrix.multiplyMM(tmpMatrix, 0, mvpMatrix, 0, tmpMatrix, 0)
        GLES20.glUniformMatrix4fv(iVPMatrix, 1, false, tmpMatrix, 0)

        val texHandler = GLES20.glGetUniformLocation(
            ShaderLoader.getShaderProgram(files[id.index].fileName.shader),
            "u_Texture"
        )
        GLES20.glUniform1i(texHandler, 0)
        GLES20.glActiveTexture(GLES20.GL_TEXTURE0)
        GLES20.glBindTexture(
            GLES20.GL_TEXTURE_2D,
            textures.textureHandle[files[id.index].fileName.texture.id]
        )

        val bonesMatricesHandle = GLES20.glGetUniformLocation(
            ShaderLoader.getShaderProgram(files[id.index].fileName.shader),
            "bonesMatrices"
        )
        GLES20.glUniformMatrix4fv(
            bonesMatricesHandle,
            files[id.index].bones.size,
            false,
            currentBonesPosesArray,
            0
        )

        val mPositionHandle = GLES20.glGetAttribLocation(
            ShaderLoader.getShaderProgram(files[id.index].fileName.shader),
            "vPosition"
        )
        GLES20.glEnableVertexAttribArray(mPositionHandle)
        GLES20.glVertexAttribPointer(
            mPositionHandle,
            3,
            GLES20.GL_FLOAT,
            false,
            0,
            files[id.index].vertexBuffer
        )

        val mTexCoordHandle = GLES20.glGetAttribLocation(
            ShaderLoader.getShaderProgram(files[id.index].fileName.shader),
            "a_TexCoordinate"
        )
        GLES20.glEnableVertexAttribArray(mTexCoordHandle)
        GLES20.glVertexAttribPointer(
            mTexCoordHandle,
            3,
            GLES20.GL_FLOAT,
            false,
            0,
            files[id.index].texBuffer
        )

        GLES20.glDrawElements(
            GLES20.GL_TRIANGLES,
            files[id.index].indicesQty,
            GLES20.GL_UNSIGNED_SHORT,
            files[id.index].indicesBuffer
        )
        GLES20.glDisableVertexAttribArray(mPositionHandle) //pole do optymalizacji

        GLES20.glDisableVertexAttribArray(mTexCoordHandle) //pole do optymalizacji
    }

    init {
        files.map { file ->

            val xmlFileInputStream = appContext.resources.openRawResource(file.fileName.rawResId)
            val xmlFileString = String.readTextFile(xmlFileInputStream)

            val jSonFile = xmlFileString.xmlToJson()

            val colladaModel = Gson().fromJson(jSonFile!!.toString(), ColladaFile::class.java)

            val vertices: MutableList<Vector3f> = colladaModel.getVertex().toMutableList()
            val textCoords: MutableList<Vector2f> = colladaModel.getTexcoords().toMutableList()
            val indices: MutableList<Int> = colladaModel.getIndices().toMutableList()

            var bonesIndices =
                colladaModel.collada?.libraryControllers?.controller?.skin?.vertexWeights?.v

            if (bonesIndices == null) {
                throw Exception("File ${appContext.resources.getResourceEntryName(file.fileName.rawResId)} has no bones")
            }

            val bindMatrixesString =
                colladaModel.collada?.libraryControllers?.controller?.skin?.source

            val inverseBindMatrixes =
                getMatrixesListFromString(bindMatrixesString?.get(2)?.float_array?.content ?: "")

            bonesIndices = removeTrash(bonesIndices)

            file.generateIndicesObject(
                vertices,
                textCoords,
                indices,
                stringToFloatList(bonesIndices ?: "")
            )

            var boneIndex = 0
            colladaModel.collada?.libraryAnimations?.animation?.subList(1, 3)?.forEach { boneItem ->
                val bone = Bones()
                //mesh.setAnimByFloat(floatList);

                var matrixes =
                    getMatrixesListFromString(boneItem.source!![1].float_array!!.content!!)



                //każde klatka musi być pomnona przez INVMATRIX kości

                bone.posesMatrices = matrixes

                file.bones.add(bone)
                boneIndex++
            }

            Timber.e("bones qty: " + file.fileName.name + " " + file.bones.size)

            file.bonseMatrices = FloatArray(file.bones.size * 16 * 2)
            var bLiterator = 0
            file.bones.forEach {
                it.posesMatrices?.forEach {
                    file.bonseMatrices[bLiterator] = it
                    bLiterator++
                }
            }

//            colladaModel.collada?.libraryVisualScenes?.visual_scene?.node?.forEach {
//                //Log.e("Node", "n "+ it._name)
//            }

//            if (colladaModel.collada?.libraryGeometries?.geometry?.mesh?.polylist != null) {
//                file.setIndicesByFloat(colladaModel.collada!!.libraryGeometries!!.geometry!!.mesh!!.polylist!!.asInteger)
//            } else {
//                file.setIndicesByFloat(colladaModel.collada!!.libraryGeometries!!.geometry!!.mesh!!.triangles!!.asInteger)
//            }


            file
        }
    }

    private fun stringToFloatList(inString: String): ArrayList<Float> {
        val outList = ArrayList<Float>()

        val parts = inString.split(" ".toRegex()).dropLastWhile({ it.isEmpty() }).toTypedArray()
        for (number in parts) {
            if (!number.equals("")) {
                outList.add(java.lang.Float.parseFloat(number))
            }
        }


        return outList
    }

    private fun removeTrash(string: String?): String? {
        var stringOut = string?.replace("\n".toRegex(), " ")
        stringOut = stringOut?.replace("\t".toRegex(), " ")

        return stringOut
    }

    private fun getMatrixesListFromString(content: String): FloatArray {
        val parts =
            content.split(" ".toRegex()).dropLastWhile { it.isEmpty() }.filter { it.isNotBlank() }
                .toTypedArray()
        //ArrayList<Float> matricesAsFloatList = new ArrayList<>();
        //val matrices = FloatArray(parts.size)
        val matrices = FloatArray(2 * 16)

//        for (i in parts.indices) {
//            matrices[i] = java.lang.Float.parseFloat(parts[i])
//        }

        for (i in 0..(2 * 16 - 1)) {
            matrices[i] = java.lang.Float.parseFloat(parts[i])
        }

        return matrices
    }
}