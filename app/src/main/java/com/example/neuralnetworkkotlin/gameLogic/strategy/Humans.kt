package com.example.neuralnetworkkotlin.gameLogic.strategy

import android.view.MotionEvent
import com.example.neuralnetworkkotlin.geometry.Camera
import com.example.neuralnetworkkotlin.geometry.plain3d.anim.File3dA
import com.example.neuralnetworkkotlin.geometry.plain3d.nonanim.File3d
import com.example.neuralnetworkkotlin.geometry.plain3d.nonanim.MODELS_3D
import com.example.neuralnetworkkotlin.helpers.Collision
import timber.log.Timber
import javax.vecmath.Vector2f
import kotlin.time.ExperimentalTime
import kotlin.time.measureTime

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
                pathPointer.addDestination(pos, force = true)

                banners.forEach { banner ->
                    if (banner.isSelected) {
                        banner.path = pathPointer.path
                    }
                }
            }

            else -> {}
        }
    }


    @OptIn(ExperimentalTime::class)
    fun loop() {
        measureTime {
            banners.forEach { banner ->
                measureTime {
                    banner.loop(collision)
                }.let { /*Timber.w("loop banner $it") */}
            }
        }.let { /*Timber.w("loop total $it")*/ }

    }

    fun draw(file3D: File3d, file3DA: File3dA, camera: Camera) {
        banners.forEach { banner ->
            file3D.drawBanner(
                camera.viewProjectionMatrix,
                MODELS_3D.BANNER,
                position = banner.position,
                wave = banner.wave.value,
                waveWalk = banner.humans.firstOrNull()?.waveWalk ?: 0f,
                selected = banner.isSelected
            )
        }

        file3D.bindThingsHuman()

        banners.forEach { banner ->
            banner.humans.forEach { human ->
                file3D.drawHuman(
                    camera.viewProjectionMatrix,
                    MODELS_3D.MEN,
                    human,
                )
//                file3DA.drawHuman(
//                    camera.viewProjectionMatrix,
//                    MODELS_3DA.MEN,
//                    human = human,
//                )
//                file3DA.drawGear(
//                    camera.viewProjectionMatrix,
//                    MODELS_3DA.SWORD,
//                    human
//                )
                //draw soldier
            }
        }
    }

    private fun selectBanners(pos: Vector2f) {
        val closest = banners.closestIndex(pos)

        banners.forEach { banner ->
            banner.isSelected = false
        }

        if(closest.second < 0.5f){
            banners[closest.first].isSelected = true
        }
    }

}


