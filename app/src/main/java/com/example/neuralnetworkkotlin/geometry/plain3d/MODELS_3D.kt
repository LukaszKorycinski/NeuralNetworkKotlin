package com.example.neuralnetworkkotlin.geometry.plain3d

import com.example.neuralnetworkkotlin.R
import com.example.neuralnetworkkotlin.renderer.Shaders
import com.example.neuralnetworkkotlin.renderer.TEXTURES

enum class MODELS_3D(val index: Int, val rawResId: Int, val shader: Shaders, val texture: TEXTURES) {
    DRAGON_MODEL(0, R.raw.kwadrat, Shaders.BASIC, TEXTURES.TERRAINTEXTURE),
    COW_MODEL(1, R.raw.kwadrat, Shaders.BASIC, TEXTURES.COWS_TEXTURE),
}