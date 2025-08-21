package io.github.chsbuffer.revancedxposed.tiktok

import app.revanced.extension.shared.settings.Settings
import app.revanced.extension.tiktok.feedfilter.FeedFilterPatch
import de.robv.android.xposed.XC_MethodHook
import io.github.chsbuffer.revancedxposed.AccessFlags
import io.github.chsbuffer.revancedxposed.fingerprint
import io.github.chsbuffer.revancedxposed.shared.misc.settings.preference.ListPreference
import io.github.chsbuffer.revancedxposed.shared.misc.settings.preference.SwitchPreference

fun TikTokHook.FeedFilter() {
    PreferenceScreen.TIKTOK.addPreferences(
        SwitchPreference("revanced_tiktok_filter_enabled"),
        ListPreference(
            key = "revanced_tiktok_filter_keywords",
            entriesKey = "revanced_tiktok_filter_keywords_entries",
            entryValuesKey = "revanced_tiktok_filter_keywords_values"
        ),
        SwitchPreference("revanced_tiktok_hide_image_video"),
        SwitchPreference("revanced_tiktok_hide_slideshow")
    )
    
    // Hook Aweme (video) model constructor
    getDexMethod("AwemeModelFingerprint") {
        fingerprint {
            accessFlags(AccessFlags.PUBLIC, AccessFlags.CONSTRUCTOR)
            classMatcher {
                usingStrings("Aweme", "aweme_id", "desc", "video")
            }
        }
    }.hookMethod(object : XC_MethodHook() {
        override fun afterHookedMethod(param: MethodHookParam) {
            val aweme = param.thisObject
            
            // Check filters
            if (shouldFilter(aweme)) {
                markAsFiltered(aweme)
            }
        }
        
        private fun shouldFilter(aweme: Any): Boolean {
            if (!Settings.TIKTOK_FILTER_ENABLED.get()) return false
            
            try {
                // Check description for keywords
                val descField = aweme.javaClass.getDeclaredField("desc")
                descField.isAccessible = true
                val description = descField.get(aweme) as? String ?: ""
                
                val keywords = Settings.TIKTOK_FILTER_KEYWORDS.get()
                    .split(",")
                    .map { it.trim() }
                    .filter { it.isNotEmpty() }
                
                if (keywords.any { description.contains(it, ignoreCase = true) }) {
                    return true
                }
                
                // Check if image/slideshow
                if (Settings.TIKTOK_HIDE_IMAGE_VIDEO.get()) {
                    val imagesField = aweme.javaClass.getDeclaredField("imageInfos")
                    imagesField.isAccessible = true
                    val images = imagesField.get(aweme) as? List<*>
                    if (!images.isNullOrEmpty()) return true
                }
                
            } catch (e: Exception) {
                // Field not found, skip
            }
            
            return false
        }
        
        private fun markAsFiltered(aweme: Any) {
            try {
                // Set a flag or modify visibility
                val field = aweme.javaClass.getDeclaredField("isFilteredByReVanced")
                field.isAccessible = true
                field.setBoolean(aweme, true)
            } catch (e: NoSuchFieldException) {
                // Add field dynamically if possible
                aweme.javaClass.getDeclaredField("desc").apply {
                    isAccessible = true
                    set(aweme, "[FILTERED]")
                }
            }
        }
    })
    
    // Hook RecyclerView Adapter to skip filtered items
    getDexMethod("FeedAdapterFingerprint") {
        findClass {
            matcher {
                usingStrings("FeedAdapter", "onBindViewHolder", "ViewHolder")
            }
        }.single().findMethod {
            matcher {
                name = "onBindViewHolder"
            }
        }.single()
    }.hookMethod(object : XC_MethodHook() {
        override fun beforeHookedMethod(param: MethodHookParam) {
            val position = param.args[1] as Int
            val holder = param.args[0]
            
            // Skip filtered items
            if (isItemFiltered(position)) {
                hideViewHolder(holder)
            }
        }
        
        private fun isItemFiltered(position: Int): Boolean {
            // Implementation depends on adapter structure
            return false
        }
        
        private fun hideViewHolder(holder: Any) {
            try {
                val itemView = holder.javaClass.getField("itemView").get(holder)
                val view = itemView as android.view.View
                view.visibility = android.view.View.GONE
                view.layoutParams.height = 0
            } catch (e: Exception) {
                // Handle error
            }
        }
    })
}
