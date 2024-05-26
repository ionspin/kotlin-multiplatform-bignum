package com.ionspin.kotlin.bignum.integer

actual object RuntimePlatform {
    /**
     * We need to know if we are running on a platform that doesn't know how to tell decimal and integer apart.
     */
    actual fun currentPlatform(): Platform = Platform.WASMJS
}