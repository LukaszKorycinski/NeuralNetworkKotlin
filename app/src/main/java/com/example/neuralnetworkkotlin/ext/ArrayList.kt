package com.example.neuralnetworkkotlin.ext

fun <E> ArrayList<E>.middle(): E? {
    if(this.isNotEmpty())
        return this.get(this.size/2)
    else
        return null
}