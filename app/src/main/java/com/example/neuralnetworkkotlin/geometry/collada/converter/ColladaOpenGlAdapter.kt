package com.example.neuralnetworkkotlin.geometry.collada.converter


import java.util.ArrayList

class ColladaOpenGlAdapter(mesh: Mesh) {

    private val vertexVNTList = ArrayList<VertexV3N3T2>()
    val indices = ArrayList<Short>()

    val faces: List<VertexV3N3T2>
        get() = vertexVNTList


    init {
        buildFacesMap(mesh)
        removeDuplicats()
    }

    fun getIndices(): List<Short> {
        return indices
    }


    private fun buildFacesMap(mesh: Mesh) {
        for (i in 0 until mesh.indices.size) {
            vertexVNTList.add(VertexV3N3T2(
                    mesh.pos[mesh.indices[i].x],
                    mesh.norm[mesh.indices[i].y],
                    mesh.texCoord[mesh.indices[i].z]
            ))

            this.indices.add(i.toShort())
        }
    }


    private fun removeDuplicats() {
        for (i in vertexVNTList.indices) {
            for (j in vertexVNTList.indices) {
                if (vertexVNTList[i] == vertexVNTList[j] && i != j) {//znajduje duplikaty
                    indices[j] = i.toShort()//do indexów pod miejsce z j zapisuje ten z i
                }
            }
        }

        var i = 0
        while (i < vertexVNTList.size) {
            var deleteFlag = true
            for (j in indices.indices) {//szukam vertexów do których nie ma odwołań w liście indexów
                if (indices[j] == i.toShort()) {
                    deleteFlag = false
                }
            }

            if (deleteFlag) {
                vertexVNTList.removeAt(i)
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
