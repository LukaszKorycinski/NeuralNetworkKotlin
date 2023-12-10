package com.example.neuralnetworkkotlin.assimp.format.X

import com.example.neuralnetworkkotlin.assimp.AiMatrix4x4
import com.example.neuralnetworkkotlin.assimp.AiVector3D
import com.example.neuralnetworkkotlin.assimp.AiVector2D
import com.example.neuralnetworkkotlin.assimp.AiColor3D
import com.example.neuralnetworkkotlin.assimp.AiColor4D
import com.example.neuralnetworkkotlin.assimp.AiVectorKey
import com.example.neuralnetworkkotlin.assimp.AiQuatKey
import com.example.neuralnetworkkotlin.assimp.ai_real


data class Face(
            var mIndices: MutableList<Int> = mutableListOf()
            )

data class TexEntry(
            var mName: String? = null,
            var mIsNormalMap: Boolean = false
            )

data class Material(
            var mName: String? = null,
            var mIsReference: Boolean = false,
            var mDiffuse: AiColor4D = AiColor4D(),
            var mSpecularExponent: ai_real = 0.0.a,
            var mSpecular: AiColor3D = AiColor3D(),
            var mEmissive: AiColor3D = AiColor3D(),
            var mTextures: MutableList<TexEntry> = mutableListOf(),
            
            var sceneIndex: Int = Int.MAX_VALUE //TODO: Not sure if this is true
            )

data class BoneWeight(
            var mVertex: Int = 0,
            var mWeight: ai_real = 0.0.a
            )

data class Bone(
            var mName: String? = null,
            var mWeights: MutableList<BoneWeight> = mutableListOf(),
            var mOffsetMatrix: AiMatrix4x4 = AiMatrix4x4()) //TODO: Not sure if this is true

data class Mesh(
            var mName: String = "",
            var mPositions: MutableList<AiVector3D> = mutableListOf(),
            var mPosFaces: MutableList<Face> = mutableListOf(),
            var mNormals: MutableList<AiVector3D> = mutableListOf(),
            var mNormFaces: MutableList<Face> = mutableListOf(),
            var mNumTextures: Int = 0,
            var mTexCoords: MutableList<MutableList<AiVector2D>> = mutableListOf(),
            var mNumColorSets: Int = 0,
            var mColors: MutableList<MutableList<AiColor4D>> = mutableListOf(),

            var mFaceMaterials: MutableList<Int> = mutableListOf(),
            var mMaterials: MutableList<Material> = mutableListOf(),

            var mBones: MutableList<Bone> = mutableListOf()


            )

data class Node(
            var mName: String = "",
            var mTrafoMatrix: AiMatrix4x4 = AiMatrix4x4(), //TODO: Not sure if this is true
            var mParent: Node?,
            var mChildren: MutableList<Node> = mutableListOf(),
            var mMeshes: MutableList<Mesh> = mutableListOf()
            )

data class MatrixKey(
            var mTime: Double = 0.0,
            var mMatrix: AiMatrix4x4 = AiMatrix4x4()) //TODO: Not sure if this is true

data class AnimBone(
            var mBoneName: String? = null,
            var mPosKeys: MutableList<AiVectorKey> = mutableListOf(),
            var mRotKeys: MutableList<AiQuatKey> = mutableListOf(),
            var mScaleKeys: MutableList<AiVectorKey> = mutableListOf(),
            var mTrafoKeys: MutableList<MatrixKey> = mutableListOf()
            )

data class Animation(
            var mName: String? = null,
            var mAnims: MutableList<AnimBone> = mutableListOf()
            )

data class Scene(
            var mRootNode: Node? = null,

            var mGlobalMeshes: MutableList<Mesh> = mutableListOf(),
            var mGlobalMaterials: MutableList<Material> = mutableListOf(),

            var mAnims: MutableList<Animation> = mutableListOf(),
            var mAnimTicksPerSecond: Int = 0

            )

