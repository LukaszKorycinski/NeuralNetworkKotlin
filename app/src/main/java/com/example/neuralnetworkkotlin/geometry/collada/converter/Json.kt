package com.example.neuralnetworkkotlin.geometry.collada.converter

import android.content.Context

import org.json.JSONObject

import fr.arnaudguyon.xmltojsonlib.XmlToJson


/**
 * Created by dell on 08.06.2017.
 */

class Json {
    companion object {


        fun xmlToJson(xmlString: String): JSONObject? {

            val xmlToJson = XmlToJson.Builder(xmlString).build()

            return xmlToJson.toJson()
        }
    }


}
