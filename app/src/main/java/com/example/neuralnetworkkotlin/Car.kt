package com.example.neuralnetworkkotlin

import com.example.neuralnetworkkotlin.nn.NeuralNetwork
import java.util.ArrayList

val ToDegrees = 57.2957795f


class Car{
    var neuralNetwork = NeuralNetwork()

    fun setNeurakNetwork(nN:NeuralNetwork){
        neuralNetwork=nN
    }

    //evry angles is in radians

    var position:Vector3f = Vector3f()
    var velocity:Vector3f = Vector3f()//force
    var angle = 0.5f
    var angularVelocity = 0.0f
    var drag = 0.95f //how fast can slow down
    var angularDrag = 0.9f
    //var isSelected = false



    fun physicsLoop(inputInfo: ArrayList<Float>){

        if(Math.abs( position.x + velocity.x )>8.33f)
            velocity.x=-velocity.x*0.8f

        if(Math.abs( position.y + velocity.y )>16.66f)
            velocity.y=-velocity.y*0.8f

        position.x += velocity.x
        position.y += velocity.y
        velocity.x *= drag
        velocity.y *= drag
        angle += angularVelocity
        angularVelocity *= angularDrag


        val neuralOutput = neuralNetwork.inputToOutput(inputInfo)

        accelerate(neuralOutput.get(0))
        turn(neuralOutput.get(1))
    }

    fun accelerate(power:Float){
        var powerReal = Math.min(power, 0.005f)
        powerReal = Math.max(powerReal, -0.00025f)

        velocity.x += Math.sin(angle.toDouble()).toFloat() * powerReal
        velocity.y += Math.cos(angle.toDouble()).toFloat() * powerReal
    }

    fun turn(turnSpeed:Float){
        var turnSpeedReal = Math.min(turnSpeed, 0.02f)
        turnSpeedReal = Math.max(turnSpeedReal, -0.02f)

        angularVelocity += turnSpeedReal;
    }

    fun reset() {
        position = Vector3f()
        velocity = Vector3f()//force
        angle = 0.5f
        angularVelocity = 0.0f
    }

}