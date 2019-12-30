package com.example.neuralnetworkkotlin.geometry.collada.pojos

import com.google.gson.annotations.SerializedName

/**
 * Created by dell on 08.06.2017.
 */

class Mesh {


    @SerializedName("source")
    var source: List<Source>? = null

    @SerializedName("polylist")
    var polylist: Polylist? = null

    @SerializedName("triangles")
    var triangles: Polylist? = null

}
