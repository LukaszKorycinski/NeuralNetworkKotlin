package com.example.neuralnetworkkotlin.geometry.collada.animConverter

import android.content.Context
import android.opengl.Matrix
import android.util.Log
import com.example.neuralnetworkkotlin.R
import com.example.neuralnetworkkotlin.geometry.collada.FileOperation
import com.example.neuralnetworkkotlin.geometry.collada.converter.Bone
import com.example.neuralnetworkkotlin.geometry.collada.converter.Json
import com.example.neuralnetworkkotlin.geometry.collada.converter.Mesh
import com.example.neuralnetworkkotlin.geometry.collada.pojos.ColladaModel
import com.google.gson.Gson
import java.util.ArrayList

class LoadFromAnimCollada(private val ctx: Context) {


    fun load(): Mesh {
        val xmlFileInputStream = ctx.resources.openRawResource(R.raw.model)
        val xmlFileString = FileOperation.readTextFile(xmlFileInputStream)

        val jSonFile = Json.xmlToJson(xmlFileString)

        val colladaModel = fromJson(jSonFile!!.toString())

        val mesh = Mesh()

        var iterator = 0
        for (s in colladaModel.collada!!.library_geometries!!.geometry!!.mesh!!.source!!) {
            val floatList = stringToFloatList(s.float_array!!.content!!)

            when (iterator) {
                0 -> {
                    mesh.setPosByFloat(floatList)
                    Log.e("setPosByFloat", " " + mesh.pos.size)
                }
                1 -> {
                    mesh.setNormByFloat(floatList)
                    Log.e("setNormByFloat", " " + mesh.norm.size)
                }
                2 -> {
                    mesh.setTexCoordByFloat(floatList)
                    Log.e("setTexCoordByFloat", " " + mesh.texCoord.size)
                }
            }
            iterator++
        }


        var bones_indices = colladaModel.collada!!.library_controllers!!.controller!!.skin!!.vertex_weights!!.v

        val inverseBindMatrixesString = colladaModel.collada!!.library_controllers!!.controller!!.skin!!.source


        val inverseBindMatrixes = getMatrixesListFromString(inverseBindMatrixesString!!.get(2).float_array!!.content!!)

        bones_indices = removeTrash(bones_indices)

        mesh.setBonesIndicesFromDataWithWeights(stringToFloatList(bones_indices!!))

        var boneIndex = 0
        colladaModel.collada!!.library_animations!!.animation!!.forEach {
            val bone = Bone()

            //mesh.setAnimByFloat(floatList);

            var matrixes = getMatrixesListFromString(it.source!![1].float_array!!.content!!)

            matrixes = mullEvery(matrixes, inverseBindMatrixes.copyOfRange(boneIndex * 16, boneIndex * 16 + 16))
            //każde klatka musi być pomnona przez INVMATRIX kości





            bone.posesMatrices = matrixes

            mesh.addBone(bone)
            boneIndex++
        }


        colladaModel.collada?.library_visual_scenes?.visual_scene?.node?.forEach {
            //Log.e("Node", "n "+ it._name)
        }




        if (colladaModel.collada!!.library_geometries!!.geometry!!.mesh!!.polylist != null) {
            mesh.setIndicesByFloat(colladaModel.collada!!.library_geometries!!.geometry!!.mesh!!.polylist!!.asInteger)
        } else {
            mesh.setIndicesByFloat(colladaModel.collada!!.library_geometries!!.geometry!!.mesh!!.triangles!!.asInteger)
        }



        mesh.recalTexCoordIndices()

        mesh.flipTextCoorsd()

        return mesh
    }

    private fun mullEvery(framesMatrices: FloatArray, inv_bind_matrix: FloatArray): FloatArray {//matrixes każdej klatki danej kościi z pliku     matrix current bone INV_BIND_MATRIX
        val matricesQty = framesMatrices.size / 16
        val matrixesOut = framesMatrices

        for (i in 0..matricesQty - 1) {
            val tmpMatrix = FloatArray(16)

            for (j in 0..15) {
                tmpMatrix[j] = framesMatrices[16 * i + j]//macieże zapisane pokolei
            }

            //Matrix.invertM(matrix, 0, matrix, 0)
            Matrix.multiplyMM(tmpMatrix, 0, inv_bind_matrix , 0, tmpMatrix, 0)

            for (j in 0..15) {
                matrixesOut[16 * i +j] = tmpMatrix[j]
            }
        }

        return matrixesOut
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


    private fun fromJson(jsonString: String): ColladaModel {
        return Gson().fromJson(jsonString, ColladaModel::class.java)
    }


}
