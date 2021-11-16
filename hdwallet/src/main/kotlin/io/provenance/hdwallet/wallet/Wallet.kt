package io.provenance.hdwallet.wallet

import io.provenance.hdwallet.bip32.ExtKey
import io.provenance.hdwallet.bip32.toRootKey
import io.provenance.hdwallet.bip39.DeterministicSeed
import io.provenance.hdwallet.bip39.MnemonicWords
import io.provenance.hdwallet.bip44.PathElement
import io.provenance.hdwallet.bip44.PathElements
import io.provenance.hdwallet.ec.ECKeyPair
import io.provenance.hdwallet.encoding.base58.base58DecodeChecked
import java.security.KeyException

interface Wallet {
    operator fun get(path: List<PathElement>): Account
    operator fun get(path: String): Account = get(PathElements.from(path))

    companion object {
        fun fromBip32(hrp: String, bip32: String): Wallet {
            val data = try {
                bip32.base58DecodeChecked()
            } catch (e: Throwable) {
                // Eat the exception so no sensitive info gets logged.
                throw KeyException()
            }
            return DefaultWallet(hrp, ExtKey.deserialize(data))
        }

        fun fromSeed(hrp: String, seed: DeterministicSeed): Wallet =
            DefaultWallet(hrp, seed.toRootKey())

        fun fromMnemonic(hrp: String, passphrase: CharArray, mnemonicWords: MnemonicWords): Wallet =
            fromSeed(hrp, mnemonicWords.toSeed(passphrase))
    }
}

interface Account {
    val address: String
    val keyPair: ECKeyPair
    fun sign(payload: ByteArray): ByteArray
    operator fun get(index: Int, hardened: Boolean = true): Account
}

interface Discoverer {
    fun discover(account: Account, query: (path: String) -> List<Account>): List<Account>
}
