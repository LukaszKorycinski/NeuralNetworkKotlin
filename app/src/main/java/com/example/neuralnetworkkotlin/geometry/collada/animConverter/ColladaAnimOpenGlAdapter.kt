package com.example.neuralnetworkkotlin.geometry.collada.animConverter


import com.example.neuralnetworkkotlin.geometry.collada.converter.Bone
import com.example.neuralnetworkkotlin.geometry.collada.converter.Mesh
import com.example.neuralnetworkkotlin.geometry.collada.converter.Vector4f
import com.example.neuralnetworkkotlin.geometry.collada.converter.VertexV4N3T2
import java.util.ArrayList

class ColladaAnimOpenGlAdapter(mesh: Mesh) {

    val faces = ArrayList<VertexV4N3T2>()
    val indices = ArrayList<Short>()
    var bones = ArrayList<Bone>()


    init {
        buildFacesMap(mesh)
        removeDuplicats()
    }

    fun getIndices(): List<Short> {
        return indices
    }


    private fun buildFacesMap(mesh: Mesh) {
        bones = mesh.bones

        for (i in 0 until mesh.indices.size) {
            faces.add(VertexV4N3T2(
                    Vector4f(mesh.pos[mesh.indices[i].x], mesh.bonesIndices[mesh.indices[i].x]) ,
                    mesh.norm[mesh.indices[i].y],
                    mesh.texCoord[mesh.indices[i].z]
            ))

            this.indices.add(i.toShort())
        }

    }


    private fun removeDuplicats() {
        for (i in faces.indices) {
            for (j in faces.indices) {
                if (faces[i] == faces[j] && i != j) {//znajduje duplikaty
                    indices[j] = i.toShort()//do indexów pod miejsce z j zapisuje ten z i
                }
            }
        }

        var i = 0
        while (i < faces.size) {
            var deleteFlag = true
            for (j in indices.indices) {//szukam vertexów do których nie ma odwołań w liście indexów
                if (indices[j] == i.toShort()) {
                    deleteFlag = false
                }
            }

            if (deleteFlag) {
                faces.removeAt(i)
                for (j in indices.indices) {
                    if (indices[j] > i) {
                        val tmp = indices[j] - 1
                        indices[j] = tmp.toShort()
                    }
                }
                i--
            }
            i++

        }
    }


}
