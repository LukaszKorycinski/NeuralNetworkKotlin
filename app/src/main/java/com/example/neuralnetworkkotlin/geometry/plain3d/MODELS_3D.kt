package com.example.neuralnetworkkotlin.geometry.plain3d

import com.example.neuralnetworkkotlin.R
import com.example.neuralnetworkkotlin.renderer.Shaders
import com.example.neuralnetworkkotlin.renderer.TEXTURES

enum class MODELS_3D(val index: Int, val rawResId: Int, val shader: Shaders, val texture: TEXTURES, val framesQty: Int) {
    DRAGON_MODEL(0, R.raw.test_multi, Shaders.BASIC_ANIM, TEXTURES.SEED, 13),
    //DRAGON_MODEL(0, R.raw.mentest, Shaders.BASIC_ANIM, TEXTURES.MEN, 14),
    //COW_MODEL(1, R.raw.test, Shaders.BASIC_ANIM, TEXTURES.COWS_TEXTURE),
}
