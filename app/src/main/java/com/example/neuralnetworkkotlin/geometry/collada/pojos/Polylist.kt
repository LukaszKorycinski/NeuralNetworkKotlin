package com.example.neuralnetworkkotlin.geometry.collada.pojos

import com.google.gson.annotations.SerializedName

import java.util.ArrayList

/**
 * Created by dell on 03.07.2017.
 */

class Polylist {

    @SerializedName("p")
    var polylist: String? = null

    val asInteger: ArrayList<Int>
        get() {
            val w = ArrayList<Int>()

            val parts = polylist!!.split(" ".toRegex()).dropLastWhile({ it.isEmpty() }).toTypedArray()
            for (number in parts)
                w.add(Integer.parseInt(number))

            return w
        }

}
