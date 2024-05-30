package com.example.neuralnetworkkotlin.gameLogic.strategy

import com.example.neuralnetworkkotlin.geometry.Camera
import com.example.neuralnetworkkotlin.geometry.plain3d.anim.File3dA
import com.example.neuralnetworkkotlin.geometry.plain3d.anim.MODELS_3DA
import com.example.neuralnetworkkotlin.geometry.vectors.copy
import com.example.neuralnetworkkotlin.geometry.vectors.plus
import com.example.neuralnetworkkotlin.helpers.Collision
import javax.vecmath.Vector2f

class Humans {

    val banners = mutableListOf<Banner>()
    val collision = Collision()

    fun loop() {
        banners.forEach { banner ->
            banner.humans.forEach { soldier ->
                soldier.look.animation.handleWave()

                var newPosition = soldier.position + Vector2f(soldier.velocity.x, 0f)
                if (!collision.checkCollision(newPosition)) {
                    soldier.position.set(newPosition)
                }
                newPosition = soldier.position + Vector2f(soldier.velocity.x, soldier.velocity.y)
                if (!collision.checkCollision(newPosition)) {
                    soldier.position.set(newPosition)
                }
            }
        }
    }

    fun draw(file3DA: File3dA, camera: Camera) {
        banners.forEach { banner ->
            banner.humans.forEach { soldier ->
                file3DA.drawHuman(
                    camera.viewProjectionMatrix,
                    MODELS_3DA.MEN,
                    human = soldier,
                )
                file3DA.draGear(
                    camera.viewProjectionMatrix,
                    MODELS_3DA.SWORD,
                    soldier
                )
                //draw soldier
            }
        }
    }

}


