fun TikTokHook.FeedFilter() {
    getDexMethod("FeedItemFingerprint") {
        fingerprint {
            classMatcher {
                usingStrings("FeedItem", "aweme_id")
            }
            methodMatcher {
                name = "<init>"
            }
        }
    }.hookMethod(object : XC_MethodHook() {
        override fun afterHookedMethod(param: MethodHookParam) {
            val feedItem = param.thisObject
            
            // Áp dụng các filter từ ReVanced
            if (FeedFilterPatch.shouldHide(feedItem)) {
                // Đánh dấu item để ẩn
                feedItem.setObjectField("isHidden", true)
            }
        }
    })
}
