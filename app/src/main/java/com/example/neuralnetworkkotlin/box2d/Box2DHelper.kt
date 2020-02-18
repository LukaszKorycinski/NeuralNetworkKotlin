package com.example.neuralnetworkkotlin.box2d

import org.jbox2d.collision.AABB
import org.jbox2d.common.Vec2
import org.jbox2d.dynamics.World

class Box2DHelper {

    var worldBox2d : World


    init {
        val worldAABB = AABB()

        worldAABB.lowerBound.set( Vec2( -10f,  -10f))
        worldAABB.upperBound.set( Vec2( 10f,  10f))

        worldBox2d = World( AABB(), Vec2(0f, -10f), true )
    }




}