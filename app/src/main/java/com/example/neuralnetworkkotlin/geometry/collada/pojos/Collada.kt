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
}
