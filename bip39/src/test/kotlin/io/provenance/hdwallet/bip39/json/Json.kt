package io.provenance.hdwallet.bip39.json

import com.fasterxml.jackson.core.TreeNode
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.registerKotlinModule

object Json {
    val om = ObjectMapper().registerKotlinModule()

    inline fun String.asTree(): TreeNode = om.readTree(this)
}
