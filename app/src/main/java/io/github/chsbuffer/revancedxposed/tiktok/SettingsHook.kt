fun TikTokHook.SettingsHook() {
    // Hook vào settings activity
    getDexMethod("SettingsActivityFingerprint") {
        findClass {
            matcher {
                className("com.ss.android.ugc.aweme.setting.ui.SettingActivity")
            }
        }.single().methods.single { it.name == "onCreate" }
    }.hookMethod(object : XC_MethodHook() {
        override fun afterHookedMethod(param: MethodHookParam) {
            val activity = param.thisObject as Activity
            
            // Inject ReVanced settings
            injectReVancedSettings(activity)
        }
    })
    
    PreferenceScreen.TIKTOK.addPreferences(
        SwitchPreference("revanced_tiktok_remove_ads"),
        SwitchPreference("revanced_tiktok_enable_download"),
        SwitchPreference("revanced_tiktok_remove_watermark"),
        ListPreference("revanced_tiktok_feed_filter"),
        ListPreference("revanced_tiktok_playback_speed")
    )
}
