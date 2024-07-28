package io.github.chsbuffer.revancedxposed

import de.robv.android.xposed.callbacks.XC_LoadPackage.LoadPackageParam
import org.luckypray.dexkit.DexKitBridge
import org.luckypray.dexkit.util.OpCodeUtil

fun createDexKit(lpparam: LoadPackageParam): DexKitBridge {
    System.loadLibrary("dexkit")
    return DexKitBridge.create(lpparam.classLoader, true)
}


fun opCodesOf(
    vararg opNames: String?,
): Collection<Int> {
    return opNames.map {
        if (it == null) {
            -1
        } else {
            OpCodeUtil.getOpCode(it)
        }
    }
}
