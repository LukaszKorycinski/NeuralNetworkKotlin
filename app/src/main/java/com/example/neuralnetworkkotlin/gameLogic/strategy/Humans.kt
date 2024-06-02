package com.example.neuralnetworkkotlin.gameLogic.strategy

import android.view.MotionEvent
import com.example.neuralnetworkkotlin.geometry.Camera
import com.example.neuralnetworkkotlin.geometry.plain3d.anim.File3dA
import com.example.neuralnetworkkotlin.geometry.plain3d.anim.MODELS_3DA
import com.example.neuralnetworkkotlin.geometry.plain3d.nonanim.File3d
import com.example.neuralnetworkkotlin.geometry.plain3d.nonanim.MODELS_3D
import com.example.neuralnetworkkotlin.geometry.vectors.distance
import com.example.neuralnetworkkotlin.geometry.vectors.plus
import com.example.neuralnetworkkotlin.helpers.Collision
import javax.vecmath.Vector2f

class Humans {

    val banners = mutableListOf<Banner>()
    val collision = Collision()

    fun onClick(motionEvent: MotionEvent, pos: Vector2f) {

        when (motionEvent.action){
            MotionEvent.ACTION_DOWN -> {
                val closestIndex = banners.closestIndex(pos)
                if(banners[closestIndex].position.distance(pos) < 0.5f){
                    banners[closestIndex].isSelected = true
                }
            }
            MotionEvent.ACTION_MOVE -> {
                //pointer.addDestination(pointer3d)
            }
            MotionEvent.ACTION_UP -> {
                //pointer.addDestination(pointer3d)
            }
            else -> {}
        }
    }

    fun loop() {
        banners.forEach { banner ->
            banner.handleWave()
            banner.humans.forEach { soldier ->
                soldier.handleWave()

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

    fun draw(file3D: File3d, file3DA: File3dA, camera: Camera) {

        banners.forEach { banner ->
            file3D.drawBanner(
                camera.viewProjectionMatrix,
                MODELS_3D.BANNER,
                position = banner.position,
                wave = banner.wave
            )
            banner.humans.forEach { soldier ->
                file3DA.drawHuman(
                    camera.viewProjectionMatrix,
                    MODELS_3DA.MEN,
                    human = soldier,
                )
                file3DA.drawGear(
                    camera.viewProjectionMatrix,
                    MODELS_3DA.SWORD,
                    soldier
                )
                //draw soldier
            }
        }
    }

}


