package com.example.neuralnetworkkotlin.geometry.f3d.collada

import com.google.gson.annotations.SerializedName
import javax.vecmath.Vector2f
import javax.vecmath.Vector3f


class ColladaFile {
    @SerializedName("COLLADA")
    var collada: Collada? = null

    fun getVertex(): List<Vector3f> {
        val source = collada?.libraryGeometries?.geometry?.mesh?.sources?.get(ColladaSource.CHANNEL.VERT.index)
        val vertices = mutableListOf<Vector3f>()

        source?.getAsList()?.let{
            for (i in it.indices step 3) {
                val x = it[i]
                val y = it[i+1]
                val z = it[i+2]
                vertices.add(Vector3f(x, y, z))
            }
        }

        return vertices
    }

    fun getTexcoords(): List<Vector2f> {
        val source = collada?.libraryGeometries?.geometry?.mesh?.sources?.get(ColladaSource.CHANNEL.TEXC.index)
        val texcoords = mutableListOf<Vector2f>()

        source?.getAsList()?.let{
            for (i in it.indices step 2) {
                val x = it[i]
                val y = it[i+1]
                texcoords.add(Vector2f(x, y))
            }
        }

        return texcoords
    }

    fun getIndices(): List<Int> {
        val indices = collada?.libraryGeometries?.geometry?.mesh?.triangles?.getAsList()
        return indices ?: emptyList()
    }
}

class Source {
    @SerializedName("float_array")
    var float_array: FloatArray? = null
}

class Animation {
    @SerializedName("source")
    var source: List<Source>? = null
}

class Collada {
    @SerializedName("library_geometries")
    var libraryGeometries: LibraryGeometries? = null

    @SerializedName("library_controllers")
    var libraryControllers: LibraryControllers? = null

    @SerializedName("library_animations")
    var libraryAnimations: LibraryAnimations? = null

    @SerializedName("library_visual_scenes")
    var libraryVisualScenes: LibraryVisualScenes? = null
}

class LibraryVisualScenes {
    @SerializedName("visual_scene")
    var visual_scene: Visual_scene? = null
}

class Visual_scene {
    @SerializedName("node")
    var node: List<NodeList>? = null
}

class NodeList {
    @SerializedName("node")
    var node: List<Node>? = null
}

class Node {

}

class LibraryAnimations {
    @SerializedName("animation")
    var animation: List<Animation>? = null
}

class LibraryControllers {
    @SerializedName("controller")
    var controller: Controller? = null
}

class Skin {
    @SerializedName("vertex_weights")
    var vertexWeights: VertexWeights? = null

    @SerializedName("source")
    var source: List<Source>? = null
}

class VertexWeights {
    @SerializedName("v")
    var v: String? = null
}

class Controller {
    @SerializedName("skin")
    var skin: Skin? = null
}

class LibraryGeometries {
    @SerializedName("geometry")
    var geometry: ColladaGeometry? = null
}

class ColladaGeometry {
    @SerializedName("mesh")
    var mesh: ColladaMesh? = null
}

class ColladaMesh {
    @SerializedName("source")
    val sources: List<ColladaSource>? = null

    //val vertices: ColladaVertices,
    @SerializedName("triangles")
    val triangles: ColladaTriangles? = null
}

class ColladaSource {
    @SerializedName("float_array")
    val floatArray: FloatArray? = null

    fun getAsList(): List<Float> {
        return floatArray?.content?.split(" ")?.filter { it.isNotBlank() }?.map { it.toFloat() } ?: emptyList()
    }

    enum class CHANNEL(val index: Int) {
        VERT(0),
        NORM(1),
        TEXC(2),
    }
}

class FloatArray {
    @SerializedName("content")
    val content: String? = null
}



class ColladaTriangles {
    @SerializedName("p")
    val indices: String? = null

    fun getAsList(): List<Int> {
        return indices?.split(" ")?.filter { it.isNotBlank() }?.map { it.toInt() } ?: emptyList()
    }
}
