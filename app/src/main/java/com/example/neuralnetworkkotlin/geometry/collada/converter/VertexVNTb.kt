package com.example.neuralnetworkkotlin.geometry.collada.converter

import java.util.ArrayList

class VertexVNTb(v4f: Vector4f, n3f: Vector3f, t2f: Vector2f) {
    var v4f = Vector4f()
    var n3f = Vector3f()
    var t2f = Vector2f()

    init {
        this.v4f = v4f
        this.n3f = n3f
        this.t2f = t2f
    }

    override fun equals(obj: Any?): Boolean {
        return obj is VertexVNTb && obj.v4f.equals(v4f)!! && obj.n3f.equals(n3f)!! && obj.t2f.equals(t2f)!!
    }
}

class VertexV3N3T2(v3f: Vector3f, n3f: Vector3f, t2f: Vector2f) {
    var v3f = Vector3f()
    var n3f = Vector3f()
    var t2f = Vector2f()

    init {
        this.v3f = v3f
        this.n3f = n3f
        this.t2f = t2f
    }

    override fun equals(obj: Any?): Boolean {
        return obj is VertexVNTb && obj.v4f.equals(v3f)!! && obj.n3f.equals(n3f)!! && obj.t2f.equals(t2f)!!
    }
}

class VertexV4N3T2(v3f: Vector4f, n3f: Vector3f, t2f: Vector2f) {
    var v4f = Vector4f()
    var n3f = Vector3f()
    var t2f = Vector2f()

    init {
        this.v4f = v3f
        this.n3f = n3f
        this.t2f = t2f
    }

    override fun equals(obj: Any?): Boolean {
        return obj is VertexVNTb && obj.v4f.equals(v4f)!! && obj.n3f.equals(n3f)!! && obj.t2f.equals(t2f)!!
    }
}