package com.simulator.neuralnetworkkotlin.ext

import kotlin.random.Random

fun Random.nextDoubleFromRange(rangeMin: Double, rangeMax: Double):Double{
    return  rangeMin + (rangeMax - rangeMin) * Random.nextDouble()
}