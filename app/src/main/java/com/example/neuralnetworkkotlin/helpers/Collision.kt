package com.example.neuralnetworkkotlin.helpers

import com.example.neuralnetworkkotlin.geometry.collada.converter.Line
import com.example.neuralnetworkkotlin.geometry.collada.converter.Triangle
import com.example.neuralnetworkkotlin.geometry.collada.converter.Vector2f
import timber.log.Timber
import java.util.ArrayList


class Collision {

    fun pointTriangleColision(s: Vector2f, triangle: Triangle):Boolean{

        val as_x = (s.x - triangle.a.x)
        val as_y = (s.y - triangle.a.y)



        val s_ab = (triangle.b.x - triangle.a.x) * as_y - (triangle.b.y - triangle.a.y) * as_x > 0

        if((triangle.c.x-triangle.a.x)*as_y-(triangle.c.y-triangle.a.y)*as_x > 0 == s_ab) return false

        if((triangle.c.x-triangle.b.x)*(s.y-triangle.b.y)-(triangle.c.y-triangle.b.y)*(s.x-triangle.b.x) > 0 != s_ab) return false

        return true
    }


    fun pointLineColision(p: Vector2f, line: Line, position: Vector2f):Float{


        val l2 = dist2(line.v, line.w)

        if(Math.abs(l2) < 0.0001f) return dist2(p, line.v)

        var t: Float = ((p.x - line.v.x) * (line.w.x - line.v.x) + (p.y - line.v.y) * (line.w.y - line.v.y)) / l2
        t = Math.max(0.0f, Math.min(1.0f, t))

        val distanceToPoint = dist2(p, Vector2f(
            line.v.x + t * (line.w.x - line.v.x),
            line.v.y + t * (line.w.y - line.v.y) ))

        return p.distance(position) //if(distanceToPoint<0.025f) return 1.0f else 0.0f
    }

    private fun dist2(v: Vector2f, w: Vector2f): Float {
        return (sqr((v.x - w.x)) + sqr((v.y - w.y)))
    }

    private fun sqr(x: Float): Float {
        return x * x
    }

}