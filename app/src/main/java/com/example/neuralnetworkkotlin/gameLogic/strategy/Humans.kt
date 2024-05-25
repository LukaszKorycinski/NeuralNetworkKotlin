package com.example.neuralnetworkkotlin.gameLogic.strategy

import com.example.neuralnetworkkotlin.geometry.vectors.plus
import com.example.neuralnetworkkotlin.helpers.Collision
import javax.vecmath.Vector2f

class Humans {

    val banners = mutableListOf<Banner>()
    val collision = Collision()

    fun loop() {



        banners.forEach { banner ->
            banner.soldiers.forEach { soldier ->
                var newPosition = soldier.position + Vector2f(soldier.velocity.x, 0f)
                if(!collision.checkCollision(newPosition)){
                    soldier.position.set(newPosition)
                }
                newPosition = soldier.position + Vector2f(soldier.velocity.x, soldier.velocity.y)
                if(!collision.checkCollision(newPosition)){
                    soldier.position.set(newPosition)
                }
            }
        }
    }

}


