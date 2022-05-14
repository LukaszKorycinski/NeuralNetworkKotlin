package com.example.neuralnetworkkotlin.ext

import java.util.function.DoubleBinaryOperator
import kotlin.random.Random

fun Random.nextDoubleFromRange(rangeMin: Double, rangeMax: Double):Double{
    return  rangeMin + (rangeMax - rangeMin) * Random.nextDouble()
}