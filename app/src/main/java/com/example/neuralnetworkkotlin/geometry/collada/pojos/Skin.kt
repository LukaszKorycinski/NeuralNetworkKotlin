package com.example.neuralnetworkkotlin.geometry.collada.pojos

import com.google.gson.annotations.SerializedName

class Skin {

    @SerializedName("vertex_weights")
    var vertex_weights: Vertex_weights? = null

    @SerializedName("source")
    var source: List<Source>? = null
}
