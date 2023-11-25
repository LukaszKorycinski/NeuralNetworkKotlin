package com.example.neuralnetworkkotlin.geometry.f3d

import android.content.Context
import com.example.neuralnetworkkotlin.R
import com.example.neuralnetworkkotlin.ext.readTextFile
import com.example.neuralnetworkkotlin.ext.xmlToJson
import com.example.neuralnetworkkotlin.geometry.File3d
import com.example.neuralnetworkkotlin.geometry.MODELS_3D
import com.example.neuralnetworkkotlin.geometry.f3d.collada.ColladaFile
import com.google.gson.Gson
import java.nio.FloatBuffer
import java.nio.ShortBuffer
import javax.vecmath.Vector2f
import javax.vecmath.Vector3f

enum class MODELS_3DA(val index: Int, val rawResId: Int){
    DRAGON_MODEL(0, R.raw.dragon),
    DRAGON2_MODEL(1, R.raw.dragon),
}

data class File3da(
    val fileName: MODELS_3DA,
    var vertexBuffer: FloatBuffer? = null,
    var texBuffer: FloatBuffer? = null,
    var indicesBuffer: ShortBuffer? = null,
    var indicesQty: Int = 0,

    var bones = ArrayList<Bone>()
    var bonesIndices = ArrayList<Float>()
) {

}


class F3da(val appContext: Context) {
    val files = mutableListOf(File3d(MODELS_3D.DRAGON_MODEL), File3d(MODELS_3D.DRAGON2_MODEL))
    init {
        files.map { file ->

            val xmlFileInputStream = appContext.resources.openRawResource(file.fileName.rawResId)
            val xmlFileString = String.readTextFile(xmlFileInputStream)

            val jSonFile = xmlFileString.xmlToJson()

            val colladaModel = Gson().fromJson(jSonFile!!.toString(), ColladaFile::class.java)

            val vertices: MutableList<Vector3f> = colladaModel.getVertex().toMutableList()
            val textCoords: MutableList<Vector2f> = colladaModel.getTexcoords().toMutableList()
            val indices: MutableList<Int> = colladaModel.getIndices().toMutableList()


            var bonesIndices = colladaModel.collada?.libraryControllers?.controller?.skin?.vertexWeights?.v

            if( bonesIndices == null){
                throw Exception("File ${appContext.resources.getResourceEntryName(file.fileName.rawResId)} has no bones")
            }

            val bindMatrixesString = colladaModel.collada?.libraryControllers?.controller?.skin?.source

            val inverseBindMatrixes = getMatrixesListFromString(bindMatrixesString?.get(2)?.float_array?.content ?: "")

            bonesIndices = removeTrash(bonesIndices)

            //mesh.setBonesIndicesFromDataWithWeights(stringToFloatList(bonesIndices ?: ""))



            file.generateIndicesObject(vertices, textCoords, indices)
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
        val parts = content.split(" ".toRegex()).dropLastWhile({ it.isEmpty() }).toTypedArray()
        //ArrayList<Float> matricesAsFloatList = new ArrayList<>();
        val matrices = FloatArray(parts.size)

        for (i in parts.indices) {
            matrices[i] = java.lang.Float.parseFloat(parts[i])
        }

        return matrices
    }
}