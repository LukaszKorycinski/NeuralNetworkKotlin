package com.example.neuralnetworkkotlin.geometry.f3d.collada

import com.google.gson.annotations.SerializedName
import org.simpleframework.xml.Attribute
import org.simpleframework.xml.Element
import org.simpleframework.xml.ElementList
import org.simpleframework.xml.Root
import javax.vecmath.Vector2f
import javax.vecmath.Vector3f


class ColladaFile {
    @SerializedName("COLLADA")
    var collada: Collada? = null

    fun getVertex(): List<Vector3f> {
        val source = collada?.library_geometries?.geometry?.mesh?.sources?.get(ColladaSource.CHANNEL.VERT.index)
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
        val source = collada?.library_geometries?.geometry?.mesh?.sources?.get(ColladaSource.CHANNEL.TEXC.index)
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
        val indices = collada?.library_geometries?.geometry?.mesh?.triangles?.getAsList()
        return indices ?: emptyList()
    }
}

class Collada {
    @SerializedName("library_geometries")
    var library_geometries: LibraryGeometries? = null
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
    val float_array: Float_Array? = null

    fun getAsList(): List<Float> {
        return float_array?.content?.split(" ")?.filter { it.isNotBlank() }?.map { it.toFloat() } ?: emptyList()
    }

    enum class CHANNEL(val index: Int) {
        VERT(0),
        NORM(1),
        TEXC(2),
    }
}

class Float_Array {
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
