package io.provenance.ec

import org.bouncycastle.jcajce.provider.asymmetric.ec.BCECPrivateKey
import org.bouncycastle.jcajce.provider.asymmetric.ec.BCECPublicKey
import org.bouncycastle.jce.spec.ECParameterSpec
import java.security.PrivateKey as JavaPrivateKey
import java.security.PublicKey as JavaPublicKey

private fun ECParameterSpec.toCurve() = Curve("", curve, n, h, g, seed)
private fun Curve.toECParameterSpec() = ECParameterSpec(c, g, n, h, seed)

fun JavaPublicKey.toECPublicKey(): PublicKey {
    val bcec = requireNotNull(this as? BCECPublicKey) { "key type invalid" }
    return PublicKey(bcec.q.getEncoded(true).toBigInteger(), bcec.parameters.toCurve())
}

fun JavaPrivateKey.toECPrivateKey(): PrivateKey {
    val bcec = requireNotNull(this as? BCECPrivateKey) { "key type invalid" }
    return PrivateKey(bcec.d, bcec.parameters.toCurve())
}