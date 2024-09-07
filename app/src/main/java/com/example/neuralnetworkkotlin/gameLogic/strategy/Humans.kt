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
import timber.log.Timber
import javax.vecmath.Vector2f

class Humans(val collision: Collision) {

    val banners = mutableListOf<Banner>()

    val pathPointer = PathPointer()

    fun onClick(motionEvent: MotionEvent, pos: Vector2f) {

        when (motionEvent.action) {
            MotionEvent.ACTION_DOWN -> {
                selectBanners(pos)
                pathPointer.clearDestination(pos)
            }

            MotionEvent.ACTION_MOVE -> {
                pathPointer.addDestination(pos)
            }

            MotionEvent.ACTION_UP -> {
                pathPointer.addDestination(pos)

                banners.forEachIndexed{ index, banner ->
                    Timber.e("banner $index: isSelected: ${banner.isSelected}, path: ${banner.path}")
                }

                banners.forEach { banner ->
                    if (banner.isSelected) {
                        banner.path = pathPointer.path
                    }
                }
            }

            else -> {}
        }
    }


    fun loop() {
        banners.forEach { banner ->
            banner.loop(collision)
        }
    }

    fun draw(file3D: File3d, file3DA: File3dA, camera: Camera) {
        banners.forEach { banner ->
            file3D.drawBanner(
                camera.viewProjectionMatrix,
                MODELS_3D.BANNER,
                position = banner.position,
                wave = banner.wave.value,
                selected = banner.isSelected
            )
            banner.humans.forEach { human ->
                file3DA.drawHuman(
                    camera.viewProjectionMatrix,
                    MODELS_3DA.MEN,
                    human = human,
                )
                file3DA.drawGear(
                    camera.viewProjectionMatrix,
                    MODELS_3DA.SWORD,
                    human
                )
                //draw soldier
            }
        }
    }

    private fun selectBanners(pos: Vector2f) {
        val closestIndex = banners.closestIndex(pos)

        banners.forEachIndexed { index, banner ->
            banner.isSelected = index == closestIndex && banner.position.distance(pos) < 0.3f
        }
    }

}


