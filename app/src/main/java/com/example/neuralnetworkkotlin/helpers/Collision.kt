package com.example.neuralnetworkkotlin.helpers

import com.example.neuralnetworkkotlin.geometry.Terrain
import com.example.neuralnetworkkotlin.geometry.vectors.vector3f.distance
import java.util.UUID
import javax.vecmath.Vector2f

class Circle(val position: Vector2f, val radius: Float, val uuid: UUID)

class Triangle(val a: Vector2f, val b: Vector2f, val c: Vector2f)

class Line(val v: Vector2f, val w: Vector2f)

class Collision {
    private val circles = mutableListOf<Circle>()

    fun setCircles(list: List<Circle>) {
        circles.clear()
        circles.addAll(list)
    }

    fun checkCollision(position: Vector2f, uuid: UUID): Boolean {
        for (circle in circles) {
            if (circle.position.distance(position) < circle.radius && uuid != circle.uuid) {
                return true
            }
        }
        return false
    }

    fun terrainColision(position: Vector2f, terrain: Terrain): Boolean {
        return terrain.collision(Vector2f(position.x/4, position.y/4))
    }


    fun pointTriangleColision(s: Vector2f, triangle: Triangle):Boolean{
        val as_x = (s.x - triangle.a.x)
        val as_y = (s.y - triangle.a.y)

        val s_ab = (triangle.b.x - triangle.a.x) * as_y - (triangle.b.y - triangle.a.y) * as_x > 0

        if((triangle.c.x-triangle.a.x)*as_y-(triangle.c.y-triangle.a.y)*as_x > 0 == s_ab) return false

        if((triangle.c.x-triangle.b.x)*(s.y-triangle.b.y)-(triangle.c.y-triangle.b.y)*(s.x-triangle.b.x) > 0 != s_ab) return false

        return true
    }


    fun pointLineColision(p: Vector2f, line: Line):Float{
        val l2 = dist2(line.v, line.w)

        if(Math.abs(l2) < 0.0001f) return dist2(p, line.v)

        var t: Float = ((p.x - line.v.x) * (line.w.x - line.v.x) + (p.y - line.v.y) * (line.w.y - line.v.y)) / l2
        t = Math.max(0.0f, Math.min(1.0f, t))

        val distanceToPoint = dist2(p, Vector2f(
            line.v.x + t * (line.w.x - line.v.x),
            line.v.y + t * (line.w.y - line.v.y) ))

        //return if(p.distance(line.w)<0.05) return 0.0f else 1.0f

        return if(distanceToPoint<0.002f) return 0.0f else 1.0f
    }

    private fun dist2(v: Vector2f, w: Vector2f): Float {
        return (sqr((v.x - w.x)) + sqr((v.y - w.y)))
    }

    private fun sqr(x: Float): Float {
        return x * x
    }

}