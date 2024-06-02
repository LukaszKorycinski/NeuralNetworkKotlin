package com.example.neuralnetworkkotlin.geometry.plain3d.anim

import com.example.neuralnetworkkotlin.R
import com.example.neuralnetworkkotlin.geometry.plain3d.Model
import com.example.neuralnetworkkotlin.renderer.Shaders
import com.example.neuralnetworkkotlin.renderer.TEXTURES


enum class MODELS_3DA(
    override val index: Int,
    override val rawResId: Int,
    override val shader: Shaders,
    override val texture: TEXTURES,
    val framesQty: Int,
    override val textureAlpha: TEXTURES? = null
): Model {
    //TEST_MODEL(0, R.raw.test_many_frames, Shaders.BASIC_ANIM, TEXTURES.SEED, 13),
    //MEN_MODEL(1, R.raw.mentest, Shaders.BASIC_ANIM, TEXTURES.MEN, 4),
    MEN(0, R.raw.men, Shaders.HUMAN_ANIM, TEXTURES.MEN, 17, TEXTURES.MEN_ALPHA),
    SWORD(1, R.raw.sword, Shaders.BASIC_ANIM, TEXTURES.WARPEONS, 17),
}
