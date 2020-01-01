package com.example.neuralnetworkkotlin.geometry.collada.pojos

import com.google.gson.annotations.SerializedName


/**
 * Created by dell on 08.06.2017.
 */

class Collada {

    @SerializedName("library_geometries")
    var library_geometries: Library_geometries? = null

    @SerializedName("library_controllers")
    var library_controllers: Library_controllers? = null

    @SerializedName("library_animations")
    var library_animations: Library_animations? = null

    @SerializedName("library_visual_scenes")
    var library_visual_scenes: Library_visual_scenes? = null


}

class Library_visual_scenes {

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