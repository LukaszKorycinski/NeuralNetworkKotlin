package com.example.neuralnetworkkotlin.gameLogic.strategy.fightmode

import android.view.MotionEvent
import com.example.neuralnetworkkotlin.gameLogic.strategy.Direction
import com.example.neuralnetworkkotlin.gameLogic.strategy.Human
import com.example.neuralnetworkkotlin.gameLogic.strategy.Look
import com.example.neuralnetworkkotlin.gameLogic.strategy.Variant
import com.example.neuralnetworkkotlin.geometry.Camera
import com.example.neuralnetworkkotlin.geometry.plain3d.anim.Animations
import com.example.neuralnetworkkotlin.geometry.plain3d.anim.File3dA
import com.example.neuralnetworkkotlin.geometry.plain3d.anim.MODELS_3DA
import com.example.neuralnetworkkotlin.geometry.vectors.Vector2f

class Playable {

    var human = Human.random().copy(
        look = Look(variant= Variant(
            head = 1,
            beard = 1,
            skinColor = 0f
        )),
        position = Vector2f(0f),
    )

    fun loop() {
        human.handleWave()
        human.look.animation = Animations.IDENTITY

        if (left) {
            human.position.x += 0.01f
            human.look.animation = Animations.WALK
            human.look.direction = Direction.LEFT
        }
        if (right) {
            human.position.x -= 0.01f
            human.look.animation = Animations.WALK
            human.look.direction = Direction.RIGHT
        }
        if (up) {
            human.position.y += 0.01f
            human.look.animation = Animations.WALK
        }
        if (down) {
            human.position.y -= 0.01f
            human.look.animation = Animations.WALK
        }
    }

    fun draw(file3DA: File3dA, camera: Camera) {
        file3DA.drawHuman(
            camera.viewProjectionMatrix,
            MODELS_3DA.MEN,
            human = human,
        )
        file3DA.draGear(
            camera.viewProjectionMatrix,
            MODELS_3DA.SWORD,
            human
        )
    }

    var left = false
    var right = false
    var up = false
    var down = false

    fun leftKey(action: MotionEvent) {
        when(action.action){
            MotionEvent.ACTION_DOWN -> left = true
            MotionEvent.ACTION_UP -> left = false
        }
    }

    fun rightKey(action: MotionEvent) {
        when(action.action){
            MotionEvent.ACTION_DOWN -> right = true
            MotionEvent.ACTION_UP -> right = false
        }
    }

    fun upKey(action: MotionEvent) {
        when (action.action) {
            MotionEvent.ACTION_DOWN -> up = true
            MotionEvent.ACTION_UP -> up = false
        }
    }

    fun downKey(action: MotionEvent) {
        when (action.action) {
            MotionEvent.ACTION_DOWN -> down = true
            MotionEvent.ACTION_UP -> down = false
        }
    }
}