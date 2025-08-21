package io.github.chsbuffer.revancedxposed.tiktok

import android.net.Uri
import app.revanced.extension.tiktok.download.DownloadsPatch
import de.robv.android.xposed.XC_MethodHook
import de.robv.android.xposed.XC_MethodReplacement
import io.github.chsbuffer.revancedxposed.Opcode
import io.github.chsbuffer.revancedxposed.fingerprint
import io.github.chsbuffer.revancedxposed.opcodes
import io.github.chsbuffer.revancedxposed.shared.misc.settings.preference.SwitchPreference

fun TikTokHook.EnableDownload() {
    PreferenceScreen.TIKTOK.addPreferences(
        SwitchPreference("revanced_tiktok_download_enabled"),
        SwitchPreference("revanced_tiktok_download_remove_watermark"),
        SwitchPreference("revanced_tiktok_download_hd")
    )
    
    // Bypass download restrictions
    getDexMethod("DownloadRestrictionFingerprint") {
        findMethod {
            matcher {
                returnType = "boolean"
                strings(
                    "download_prevent_duet",
                    "download_prevent_react", 
                    "download_prevent_stitch"
                )
            }
        }.single()
    }.hookMethod(XC_MethodReplacement.returnConstant(false))
    
    // Enable download button visibility
    getDexMethod("DownloadButtonVisibilityFingerprint") {
        fingerprint {
            returns("V")
            opcodes(
                Opcode.IGET_BOOLEAN,
                Opcode.IF_EQZ,
                Opcode.CONST_16,
                Opcode.INVOKE_VIRTUAL
            )
            strings("share_download_button")
        }
    }.hookMethod(object : XC_MethodHook() {
        override fun beforeHookedMethod(param: MethodHookParam) {
            // Force show download button
            param.args.forEachIndexed { index, arg ->
                if (arg is Int && arg == 8) { // GONE visibility
                    param.args[index] = 0 // VISIBLE
                }
            }
        }
    })
    
    // Remove watermark from download URL
    getDexMethod("DownloadUrlFingerprint") {
        findMethod {
            matcher {
                returnType = "java.lang.String"
                strings("video", "play_addr", "url_list")
                addInvoke {
                    name = "parse"
                    declaredClass = "android.net.Uri"
                }
            }
        }.single()
    }.hookMethod(object : XC_MethodHook() {
        override fun afterHookedMethod(param: MethodHookParam) {
            val url = param.result as? String ?: return
            
            // Parse and modify URL
            val uri = Uri.parse(url)
            val newUri = uri.buildUpon()
                .appendQueryParameter("watermark", "0")
                .appendQueryParameter("logo", "0")
                .appendQueryParameter("download", "1")
                .build()
            
            param.result = newUri.toString()
        }
    })
}
