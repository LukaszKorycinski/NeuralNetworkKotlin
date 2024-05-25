package com.example.neuralnetworkkotlin.geometry.plain3d.data

import java.nio.FloatBuffer
import java.nio.ShortBuffer

class Buffers (
    var vertexBuffer: FloatBuffer? = null,
    var texBuffer: FloatBuffer? = null,
    var indicesBuffer: ShortBuffer? = null,
    var indicesQty: Int = 0,
)