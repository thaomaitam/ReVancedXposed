package io.github.chsbuffer.revancedxposed.tiktok

import app.revanced.extension.tiktok.feedfilter.FeedFilterPatch
import de.robv.android.xposed.XC_MethodHook
import io.github.chsbuffer.revancedxposed.Opcode
import io.github.chsbuffer.revancedxposed.fingerprint
import io.github.chsbuffer.revancedxposed.shared.misc.settings.preference.SwitchPreference

fun TikTokHook.RemoveAds() {
    // Thêm preference
    PreferenceScreen.TIKTOK.addPreferences(
        SwitchPreference("revanced_tiktok_remove_ads"),
        SwitchPreference("revanced_tiktok_hide_live"),
        SwitchPreference("revanced_tiktok_hide_story")
    )
    
    // Hook FeedItemList để lọc ads
    getDexMethod("FeedItemListFingerprint") {
        findMethod {
            matcher {
                returnType = "com.ss.android.ugc.aweme.feed.model.FeedItemList"
                strings("items", "hasMore", "maxCursor")
                opcodes(
                    Opcode.IGET_OBJECT,
                    Opcode.IF_EQZ,
                    Opcode.NEW_INSTANCE
                )
            }
        }.single()
    }.hookMethod(object : XC_MethodHook() {
        override fun afterHookedMethod(param: MethodHookParam) {
            val feedItemList = param.result ?: return
            val itemsField = feedItemList.javaClass.getDeclaredField("items")
            itemsField.isAccessible = true
            
            @Suppress("UNCHECKED_CAST")
            val items = itemsField.get(feedItemList) as? MutableList<Any> ?: return
            
            // Lọc ads
            val filtered = items.filterNot { item ->
                isAd(item) || isLive(item) || isStory(item)
            }
            
            itemsField.set(feedItemList, filtered)
        }
        
        private fun isAd(item: Any): Boolean {
            return try {
                val isAd = item.javaClass.getDeclaredField("isAd")
                isAd.isAccessible = true
                isAd.getBoolean(item)
            } catch (e: Exception) {
                false
            }
        }
        
        private fun isLive(item: Any): Boolean {
            return try {
                val isLive = item.javaClass.getDeclaredField("isLive") 
                isLive.isAccessible = true
                isLive.getBoolean(item)
            } catch (e: Exception) {
                false
            }
        }
        
        private fun isStory(item: Any): Boolean {
            return try {
                val cellType = item.javaClass.getDeclaredField("cellType")
                cellType.isAccessible = true
                cellType.getInt(item) == 2 // Story type
            } catch (e: Exception) {
                false
            }
        }
    })
}
