package com.example.neuralnetworkkotlin.gameLogic.strategy

private const val RANDOM_SOLDIERS_QTY = 16

class Banner {

    val humans = mutableListOf<Human>()

    fun makeBanner() : Banner {
        for (i in 0..RANDOM_SOLDIERS_QTY) {

            humans.add(Human.random())
        }
        return this
    }

}