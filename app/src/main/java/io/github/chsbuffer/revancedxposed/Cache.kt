package io.github.chsbuffer.revancedxposed

import android.annotation.SuppressLint
import android.app.Application
import android.content.Context
import android.content.SharedPreferences
import de.robv.android.xposed.XposedBridge
import org.luckypray.dexkit.result.ClassData
import org.luckypray.dexkit.result.FieldData
import org.luckypray.dexkit.result.MethodData
import org.luckypray.dexkit.wrap.DexClass
import org.luckypray.dexkit.wrap.DexField
import org.luckypray.dexkit.wrap.DexMethod

@SuppressLint("CommitPrefEdits")
open class Cache(val app: Application) {
    lateinit var pref: SharedPreferences
    lateinit var map: MutableMap<String, String>

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
        val edit = pref.edit()
        map.forEach { k, v ->
            edit.putString(k, v)
        }
        edit.commit()
    }

    fun getDexClass(key: String, f: () -> ClassData): DexClass {
        return map[key]?.let { DexClass(it) } ?: f().apply {
            map[key] = this.descriptor
            XposedBridge.log("$key Matches: ${this.toDexType()}")
        }.toDexType()
    }

    fun getDexMethod(key: String, f: () -> MethodData): DexMethod {
        return map[key]?.let { DexMethod(it) } ?: f().apply {
            map[key] = this.descriptor
            XposedBridge.log("$key Matches: ${this.toDexMethod()}")
        }.toDexMethod()
    }

    fun getDexField(key: String, f: () -> FieldData): DexField {
        return map[key]?.let { DexField(it) } ?: f().apply {
            map[key] = this.descriptor
            XposedBridge.log("$key Matches: ${this.toDexField()}")
        }.toDexField()
    }

    fun getString(key: String, f: () -> String): String {
        return map[key] ?: f().also {
            map[key] = it
            XposedBridge.log("$key Matches: ${it}")
        }
    }

    fun getNumber(key: String, f: () -> Number): Number {
        return map[key]?.let { return Integer.parseInt(it) } ?: f().also {
            map[key] = it.toString()
            XposedBridge.log("$key Matches: ${it}")
        }
    }

    fun getDexMethod(key: String): DexMethod {
        return DexMethod(map[key]!!)
    }

    fun getDexClass(key: String): DexClass {
        return DexClass(map[key]!!)
    }

    fun getDexField(key: String): DexField {
        return DexField(map[key]!!)
    }

    fun getString(key: String): String {
        return map[key]!!
    }

    fun setString(key: String, value: Any) {
        val str = when (value) {
            is ClassData -> value.toDexType()
            is MethodData -> value.toDexMethod()
            is FieldData -> value.toDexField()
            else -> value
        }
        XposedBridge.log("$key Matches: $str")
        map[key] = str.toString()
    }
}