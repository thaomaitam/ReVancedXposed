package io.github.chsbuffer.revancedxposed

import android.app.Application
import de.robv.android.xposed.IXposedHookLoadPackage
import de.robv.android.xposed.XC_MethodHook
import de.robv.android.xposed.XposedBridge
import de.robv.android.xposed.XposedHelpers
import de.robv.android.xposed.callbacks.XC_LoadPackage.LoadPackageParam
import kotlin.system.measureTimeMillis

class MainHook : IXposedHookLoadPackage {
    override fun handleLoadPackage(lpparam: LoadPackageParam) {
        when (lpparam.packageName) {
            "com.google.android.apps.youtube.music" -> {
                inContext(lpparam) { app ->
                    val t = measureTimeMillis {
                        MusicHook(app, lpparam).Hook()
                    }
                    XposedBridge.log("Youtube Music handleLoadPackage: ${t}ms")
                }
            }
        }
    }
}

fun inContext(lpparam: LoadPackageParam, f: (Application) -> Unit) {
    val appClazz = XposedHelpers.findClass(lpparam.appInfo.className, lpparam.classLoader)
    XposedBridge.hookMethod(appClazz.getMethod("onCreate"), object : XC_MethodHook() {
        override fun afterHookedMethod(param: MethodHookParam) {
            val app = param.thisObject as Application
            f(app)
        }
    })
}
