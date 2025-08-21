fun TikTokHook.RemoveAds() {
    // Tìm phương thức load ads
    getDexMethod("LoadAdsFingerprint") {
        findMethod {
            matcher {
                // TikTok dùng FeedApiService để load ads
                declaredClass("com.ss.android.ugc.aweme.feed.api.FeedApiService")
                strings("feed/v1", "aweme/v1/feed")
            }
        }.single()
    }.hookMethod(object : XC_MethodHook() {
        override fun afterHookedMethod(param: MethodHookParam) {
            // Lọc ads từ response
            val response = param.result as? List<*> ?: return
            val filtered = response.filter { item ->
                // Gọi logic từ ReVanced
                !FeedFilterPatch.isAd(item)
            }
            param.result = filtered
        }
    })
}
