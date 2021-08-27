package com.example.neuralnetworkkotlin.helpers

import com.example.neuralnetworkkotlin.geometry.collada.converter.Triangle
import com.example.neuralnetworkkotlin.geometry.collada.converter.Vector2f
import timber.log.Timber


class Collision {

    fun pointTriangleColision(s: Vector2f, triangle: Triangle):Boolean{
        val as_x = (s.x - triangle.a.x)
        val as_y = (s.y - triangle.a.y)

        Timber.e("as_x "+as_x)
        Timber.e("as_y "+as_y)

        val s_ab = (triangle.b.x - triangle.a.x) * as_y - (triangle.b.y - triangle.a.y) * as_x > 0

        Timber.e("s_ab "+s_ab)

        Timber.e("if1 "+ ((triangle.c.x-triangle.a.x)*as_y-(triangle.c.y-triangle.a.y)*as_x > 0 == s_ab))
        Timber.e("if2 "+ ((triangle.c.x-triangle.b.x)*(s.y-triangle.b.y)-(triangle.c.y-triangle.b.y)*(s.x-triangle.b.x) > 0 != s_ab))


        if((triangle.c.x-triangle.a.x)*as_y-(triangle.c.y-triangle.a.y)*as_x > 0 == s_ab) return false

        if((triangle.c.x-triangle.b.x)*(s.y-triangle.b.y)-(triangle.c.y-triangle.b.y)*(s.x-triangle.b.x) > 0 != s_ab) return false

        return true
    }

}