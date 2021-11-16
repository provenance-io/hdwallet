package io.provenance.hdwallet.wallet

import io.provenance.hdwallet.bech32.toBech32
import io.provenance.hdwallet.bip32.AccountType.ROOT
import io.provenance.hdwallet.bip32.ExtKey
import io.provenance.hdwallet.bip44.PathElement
import io.provenance.hdwallet.common.hashing.sha256
import io.provenance.hdwallet.common.hashing.sha256hash160
import io.provenance.hdwallet.ec.ECKeyPair
import io.provenance.hdwallet.signer.BCECSigner

class DefaultWallet(private val hrp: String, private val key: ExtKey) : Wallet {
    init {
        require(key.depth == ROOT) { "cannot init wallet with non-root key" }
    }

    override fun get(path: List<PathElement>): Account = path
        .fold(key) { t, p -> t.childKey(p.hardenedNumber, p.hardened) }
        .let { DefaultAccount(hrp, it) }
}

class DefaultAccount(hrp: String, key: ExtKey) : Account {
    override val address: String =
        key.keyPair.publicKey.compressed().sha256hash160().toBech32(hrp).address

    override val keyPair: ECKeyPair = key.keyPair
    
    private val keyMaker = hrp to { index: Int, hard: Boolean -> key.childKey(index, hard) }
    private val signateur = BCECSigner()
    private val signer = { bytes: ByteArray -> signateur.sign(key.keyPair.privateKey, bytes.sha256()) }

    override fun sign(payload: ByteArray): ByteArray =
        signer.invoke(payload).encodeAsBTC()

    override fun get(index: Int, hardened: Boolean): Account =
        DefaultAccount(keyMaker.first, keyMaker.second(index, hardened))
}
