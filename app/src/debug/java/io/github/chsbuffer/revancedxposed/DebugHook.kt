package io.github.chsbuffer.revancedxposed

import app.revanced.extension.shared.Logger
import de.robv.android.xposed.XC_MethodHook
import org.luckypray.dexkit.exceptions.NonUniqueResultException
import org.luckypray.dexkit.result.BaseDataList
import org.luckypray.dexkit.result.ClassDataList
import kotlin.reflect.KFunction0
import kotlin.reflect.jvm.javaMethod

class DebugHook(override val classLoader: ClassLoader) : IHook {
    override fun Hook() {
        val single: KFunction0<Any> = ClassDataList()::single
        single.javaMethod!!.hookMethod(object : XC_MethodHook() {
            override fun afterHookedMethod(param: XC_MethodHook.MethodHookParam) {
                if (param.throwable is NonUniqueResultException) {
                    (param.thisObject as BaseDataList<*>).forEach {
                        Logger.printDebug { "$it" }
                    }
                }
            }
        })
    }
}