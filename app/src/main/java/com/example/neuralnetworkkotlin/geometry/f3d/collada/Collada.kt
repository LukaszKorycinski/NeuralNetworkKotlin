package com.example.neuralnetworkkotlin.geometry.f3d.collada

import org.simpleframework.xml.Attribute
import org.simpleframework.xml.Element
import org.simpleframework.xml.ElementList
import org.simpleframework.xml.Root

@Root(strict = false, name = "COLLADA")
class Collada {
    @field:ElementList(name = "library_geometries", required = false)
    var library_geometries: List<LibraryGeometries>? = null
}

@Root(strict = false, name = "library_geometries")
class LibraryGeometries {
    @field:ElementList(name = "geometry", required = false)
    var geometry: List<ColladaGeometry>? = null
}

@Root(strict = false, name = "geometry")
class ColladaGeometry {
    @field:ElementList(name = "mesh", required = false)
    var mesh: List<ColladaMesh>? = null
}

@Root(strict = false, name = "mesh")
class ColladaMesh {
    @field:ElementList(name = "source", required = false)
    val sources: List<ColladaSource>? = null

    //val vertices: ColladaVertices,
    @field:ElementList(name = "triangles", required = false)
    val triangles: List<ColladaTriangles>? = null
}

class ColladaSource {
    @field:ElementList(name = "float_array", required = false)
    val float_array: List<FloatArray>? = null
}

data class ColladaTriangles(
    @field:ElementList(name = "p", required = false)
    val indices: List<Int>
)
