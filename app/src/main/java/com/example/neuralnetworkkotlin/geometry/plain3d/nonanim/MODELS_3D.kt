package com.example.neuralnetworkkotlin.geometry.plain3d.nonanim

import com.example.neuralnetworkkotlin.R
import com.example.neuralnetworkkotlin.renderer.Shaders
import com.example.neuralnetworkkotlin.renderer.TEXTURES

enum class MODELS_3D(val index: Int, val rawResId: Int, val shader: Shaders, val texture: TEXTURES, val textureAlpha: TEXTURES? = null) {
    COW(0, R.raw.cows, Shaders.BASIC, TEXTURES.COWS_TEXTURE, ),
    //SWORD(1, R.raw.sword, Shaders.BASIC, TEXTURES.TERRAIN, ),
}
