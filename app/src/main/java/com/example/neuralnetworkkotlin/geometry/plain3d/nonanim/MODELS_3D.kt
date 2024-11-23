package com.example.neuralnetworkkotlin.geometry.plain3d.nonanim

import com.example.neuralnetworkkotlin.R
import com.example.neuralnetworkkotlin.geometry.plain3d.Model
import com.example.neuralnetworkkotlin.renderer.Shaders
import com.example.neuralnetworkkotlin.renderer.TEXTURES

enum class MODELS_3D(
    override var index: Int,
    override val rawResId: Int,
    override val shader: Shaders,
    override val texture: TEXTURES,
    override val textureAlpha: TEXTURES? = null
) : Model {
    COW(0, R.raw.cows, Shaders.BASIC, TEXTURES.COWS_TEXTURE),
    BANNER(1, R.raw.banner, Shaders.BANNER, TEXTURES.BANNER, TEXTURES.BANNER_GRADIENT),
    TREE(2, R.raw.tree, Shaders.TREE, TEXTURES.LEAF),
    LEAFS(3, R.raw.leafs, Shaders.LEAFS, TEXTURES.LEAF, TEXTURES.LEAF_CHANNELS),
    TREE_LOW_POLY(4, R.raw.tree_low_poly, Shaders.TREE, TEXTURES.LEAF),
    MEN(5, R.raw.men, Shaders.HUMAN_ANIM, TEXTURES.MEN, TEXTURES.MEN_ALPHA),
}
