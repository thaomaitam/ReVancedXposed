package io.github.chsbuffer.revancedxposed

import android.annotation.SuppressLint
import android.app.Application
import android.content.Context
import android.content.SharedPreferences
import android.view.View
import de.robv.android.xposed.XC_MethodHook
import de.robv.android.xposed.XC_MethodReplacement
import de.robv.android.xposed.XposedBridge
import de.robv.android.xposed.XposedHelpers
import de.robv.android.xposed.callbacks.XC_LoadPackage.LoadPackageParam
import org.luckypray.dexkit.DexKitBridge
import org.luckypray.dexkit.query.enums.StringMatchType
import org.luckypray.dexkit.result.MethodData
import org.luckypray.dexkit.wrap.DexMethod
import java.lang.reflect.Modifier

@SuppressLint("CommitPrefEdits")
class MusicHook(val app: Application, val lpparam: LoadPackageParam) {
    lateinit var dexkit: DexKitBridge
    lateinit var pref: SharedPreferences
    lateinit var map: MutableMap<String, String>
    var cached: Boolean = false

    fun Hook() {
        cached = loadCache()
        XposedBridge.log("Using cached keys: $cached")

        if (!cached) {
            System.loadLibrary("dexkit")
            dexkit = DexKitBridge.create(lpparam.classLoader, true)
        }

        try {
            HideMusicVideoAds()
            MinimizedPlayback()
            RemoveUpgradeButton()
            HideGetPremium()
            saveCache()
        } catch (err: Exception) {
            pref.edit().clear().apply()
            throw err
        } finally {
            if (!cached) dexkit.close()
        }
    }

    @Suppress("UNCHECKED_CAST")
    fun loadCache(): Boolean {
        pref = app.getSharedPreferences("xprevanced", Context.MODE_PRIVATE)
        val packageInfo = app.packageManager.getPackageInfo(app.packageName, 0)

        val id = packageInfo.lastUpdateTime.toString()
        val cachedId = pref.getString("id", null)

        XposedBridge.log("cache ID: $id")
        XposedBridge.log("cached ID: ${cachedId ?: ""}")

        if (!cachedId.equals(id)) {
            map = mutableMapOf("id" to id)
            return false
        } else {
            map = pref.all.toMutableMap() as MutableMap<String, String>
            return true
        }
    }

    fun saveCache() {
//        pref.edit(true) {
//            map.forEach { k, v ->
//                putString(k, v)
//            }
//        }

        val edit = pref.edit()
        map.forEach { k, v ->
            edit.putString(k, v)
        }
        edit.commit()
    }

    fun getMethodData(key: String, f: () -> MethodData): DexMethod {
        return map[key]?.let { DexMethod(it) } ?: f().apply {
            map[key] = this.descriptor
            XposedBridge.log("$key Matches: ${this.toDexMethod()}")
        }.toDexMethod()
    }

    fun getData(key: String, f: () -> String): String {
        return map[key] ?: f().also {
            map[key] = it
            XposedBridge.log("$key Matches: ${it}")
        }
    }

    fun HideGetPremium() {
        // HideGetPremiumFingerprint
        getMethodData("HideGetPremiumFingerprint") {
            dexkit.findMethod {
                matcher {
                    modifiers = Modifier.FINAL or Modifier.PUBLIC
                    usingStrings(
                        listOf("FEmusic_history", "FEmusic_offline"), StringMatchType.Equals
                    )
                }
            }.single()
        }.let {
            XposedBridge.hookMethod(
                it.getMethodInstance(lpparam.classLoader),
                object : XC_MethodHook() {
                    lateinit var hook: XC_MethodHook.Unhook

                    override fun beforeHookedMethod(param: MethodHookParam) {
                        hook = XposedHelpers.findAndHookMethod("android.view.View",
                            lpparam.classLoader,
                            "setVisibility",
                            Int::class.java,
                            object : XC_MethodHook() {
                                override fun beforeHookedMethod(param: MethodHookParam) {
                                    param.args[0] = View.GONE
                                }
                            })
                    }

                    override fun afterHookedMethod(param: MethodHookParam?) {
                        hook.unhook()
                    }
                })
        }
    }

    private fun RemoveUpgradeButton() {
        // PivotBarConstructorFingerprint
        getMethodData("PivotBarConstructorFingerprint") {
            val result = dexkit.findMethod {
                matcher {
                    name = "<init>"
                    returnType = "void"
                    modifiers = Modifier.PUBLIC
                    paramTypes(null, "boolean")
                    opNames = listOf(
                        "check-cast",
                        "invoke-interface",
                        "goto",
                        "iput-object",
                        "return-void",
                    )
                }
            }.single()
            getData("pivotBarElementField") {
                result.declaredClass!!.fields.single { f -> f.typeName == "java.util.List" }.fieldName
            }
            result
        }.let {
            val pivotBarElementField =
                getData("pivotBarElementField") { throw Exception("WTF, Shouldn't i already searched?") }
            XposedBridge.hookMethod(it.getConstructorInstance(lpparam.classLoader),
                object : XC_MethodHook() {
                    override fun afterHookedMethod(param: MethodHookParam) {
                        val list = XposedHelpers.getObjectField(
                            param.thisObject, pivotBarElementField
                        )
                        XposedHelpers.callMethod(list, "remove", 4)
                    }
                })
        }
    }

    private fun MinimizedPlayback() {
        getMethodData("BackgroundPlaybackDisableFingerprint") {
            dexkit.findMethod {
                matcher {
                    returnType = "boolean"
                    modifiers = Modifier.PUBLIC or Modifier.STATIC
                    paramCount = 1
                    opNames = listOf(
                        "const/4",
                        "if-eqz",
                        "iget",
                        "and-int/lit16",
                        "if-eqz",
                        "iget-object",
                        "if-nez",
                        "sget-object",
                        "iget",
                    )
                }
            }.single()
        }.let {
            XposedBridge.hookMethod(
                it.getMethodInstance(lpparam.classLoader), XC_MethodReplacement.returnConstant(true)
            )
        }

        // KidsMinimizedPlaybackPolicyControllerFingerprint
        getMethodData("KidsMinimizedPlaybackPolicyControllerFingerprint") {

            dexkit.findMethod {
                matcher {
                    returnType = "void"
                    modifiers = Modifier.PUBLIC or Modifier.FINAL
                    paramTypes("int", null, "boolean")
                    opNames = listOf(
                        "iget",
                        "if-ne",
                        "iget-object",
                        "if-ne",
                        "iget-boolean",
                        "if-eq",
                        "goto",
                        "return-void",
                        "sget-object",
                        "const/4",
                        "if-ne",
                        "iput-boolean"
                    )
                }
            }.single()
        }.let {
            XposedBridge.hookMethod(
                it.getMethodInstance(lpparam.classLoader), XC_MethodReplacement.DO_NOTHING
            )
        }
    }

    private fun HideMusicVideoAds() {
        // ShowMusicVideoAdsParentFingerprint
        getMethodData("ShowMusicVideoAdsMethod") {
            dexkit.findMethod {
                matcher {
                    usingStrings(
                        listOf("maybeRegenerateCpnAndStatsClient called unexpectedly, but no error."),
                        StringMatchType.Equals
                    )
                }
            }.single().also {
                XposedBridge.log("ShowMusicVideoAdsParentFingerprint Matches: ${it.toDexMethod()}")
            }.invokes.findMethod {
                matcher {
                    paramTypes("boolean")
                }
            }.single()
        }.let {
            XposedBridge.hookMethod(it.getMethodInstance(lpparam.classLoader),
                object : XC_MethodHook() {
                    override fun beforeHookedMethod(param: MethodHookParam) {
                        param.args[0] = false
                    }
                })
        }
    }
}
