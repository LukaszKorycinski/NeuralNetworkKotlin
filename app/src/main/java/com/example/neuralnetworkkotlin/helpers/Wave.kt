package com.example.neuralnetworkkotlin.helpers

import kotlin.math.PI

class Wave (
    var value: Float
    ){
    operator fun plusAssign(value: Float){
        this.value += value
        if(this.value > PI * 2) this.value = 0f
    }
}