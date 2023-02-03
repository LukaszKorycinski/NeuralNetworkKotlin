package com.example.neuralnetworkkotlin.geometry.collada.converter

import android.opengl.Matrix
import android.util.Log
import com.example.neuralnetworkkotlin.geometry.collada.pojos.Vector3i

import java.util.ArrayList
import java.util.Scanner


class Mesh {
    val pos = ArrayList<Vector3f>()
    val norm = ArrayList<Vector3f>()
    val texCoord = ArrayList<Vector2f>()
    val indices = ArrayList<Vector3i>()


    var bones = ArrayList<Bone>()
    var bonesIndices = ArrayList<Float>()

    //    private ArrayList<Matrix> bonesMatrixes = new ArrayList<>();
    //    private ArrayList<Integer> bonesIndex = new ArrayList<>();


    fun setBonesIndicesFromDataWithWeights(indicesIN: ArrayList<Float>) {
        for (i in indicesIN.indices) {
            if (i % 2 == 0) {
                bonesIndices.add(indicesIN[i])
            }
        }
    }

    fun getAnimQty():Int{
        bones.get(0).posesMatrices?.size?.let {
            return ( it / 16)
        }
        return 0
    }

    fun getBonesQty():Int{
        return bones.size
    }


    fun addBone(bone: Bone) {
        bones.add(bone)
    }

    fun setPosByFloat(floatArray: ArrayList<Float>) {
        var i = 0
        while (i < floatArray.size) {
            pos.add(Vector3f(-floatArray[i], floatArray[i + 1], floatArray[i + 2]))
            i += 3
        }
    }

    fun setNormByFloat(floatArray: ArrayList<Float>) {
        var i = 0
        while (i < floatArray.size) {
            norm.add(Vector3f(floatArray[i], floatArray[i + 1], floatArray[i + 2]))
            i += 3
        }
    }

    fun setTexCoordByFloat(floatArray: ArrayList<Float>) {
        var i = 0
        while (i < floatArray.size) {
            texCoord.add(Vector2f(floatArray[i], floatArray[i + 1]))
            i += 2
        }
    }

    fun setIndicesByFloat(intArray: ArrayList<Int>) {
        var i = 0
        while (i < intArray.size) {
            indices.add(Vector3i(intArray[i], intArray[i + 1], intArray[i + 2]))
            i += 3
        }
    }


    fun recalTexCoordIndices() {
        for (i in indices.indices) {
            for (j in indices.indices) {
                if (texCoord[indices[i].z].equals(texCoord[indices[j].z]) && i != j) {
                    val tmp = indices[j]
                    tmp.z = indices[i].z
                    indices[j] = tmp
                }
            }
        }

        var i = 0
        while (i < texCoord.size) {
            var deleteFlag = true
            for (j in indices.indices) {
                if (indices[j].z == i) {
                    deleteFlag = false
                }
            }

            if (deleteFlag) {
                for (j in indices.indices) {
                    if (indices[j].z > i) {
                        indices[j].z--
                    }
                }
                texCoord.removeAt(i)
                i--
            }
            i++
        }
    }

    fun flipTextCoorsd() {
        texCoord.forEach {
            it.y = 1.0f-it.y

        }
    }
}
