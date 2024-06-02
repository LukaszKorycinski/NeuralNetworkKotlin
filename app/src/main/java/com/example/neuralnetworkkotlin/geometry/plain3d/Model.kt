package com.example.neuralnetworkkotlin.geometry.plain3d

import com.example.neuralnetworkkotlin.renderer.Shaders
import com.example.neuralnetworkkotlin.renderer.TEXTURES

interface Model {
    val index: Int
    val rawResId: Int
    val shader: Shaders
    val texture: TEXTURES
    val textureAlpha: TEXTURES?
}