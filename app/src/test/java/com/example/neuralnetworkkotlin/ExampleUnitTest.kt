package com.example.neuralnetworkkotlin

import com.example.neuralnetworkkotlin.geometry.collada.converter.Triangle
import com.example.neuralnetworkkotlin.geometry.collada.converter.Vector2f
import com.example.neuralnetworkkotlin.helpers.Collision
import org.junit.Test

import org.junit.Assert.*

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
class ExampleUnitTest {
    @Test
    fun pointTriangleCollision() {
        val start = Vector2f(1.0f, 1.0f)
        val triangle = Triangle(start, Vector2f(start.x+0.5f, start.y+0.5f), Vector2f(start.x-0.5f, start.y+0.5f))


        var point = Vector2f(1.0f, 1.1f)
        var out = Collision().pointTriangleColision(point, triangle)
        assertTrue(out)

        point = Vector2f(1.0f, 0.1f)
        out = Collision().pointTriangleColision(point, triangle)
        assertFalse(out)
    }
}
