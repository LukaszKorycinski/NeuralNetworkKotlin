package com.example.neuralnetworkkotlin.geometry.collada.converter

import android.content.Context
import android.util.Log
import com.example.neuralnetworkkotlin.R

import com.google.gson.Gson

import com.example.neuralnetworkkotlin.geometry.collada.pojos.ColladaModel
import com.example.neuralnetworkkotlin.geometry.collada.FileOperation

import java.util.ArrayList


class LoadFromCollada(private val ctx: Context) {


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



        if(colladaModel.collada!!.library_geometries!!.geometry!!.mesh!!.polylist != null){
            mesh.setIndicesByFloat(colladaModel.collada!!.library_geometries!!.geometry!!.mesh!!.polylist!!.asInteger)
        }else{
            mesh.setIndicesByFloat(colladaModel.collada!!.library_geometries!!.geometry!!.mesh!!.triangles!!.asInteger)
        }



        mesh.recalTexCoordIndices()

        mesh.flipTextCoorsd()

        return mesh
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
        for (number in parts){
            if(!number.equals("")){
                outList.add(java.lang.Float.parseFloat(number))
            }
        }


        return outList
    }


    private fun fromJson(jsonString: String): ColladaModel {
        return Gson().fromJson(jsonString, ColladaModel::class.java)
    }


}
