fun TikTokHook.EnableDownload() {
    // Hook permission check
    getDexMethod("DownloadPermissionCheck") {
        fingerprint {
            strings("download_prevent_duet", "download_prevent_react")
            returns("Z") // boolean
        }
    }.hookMethod(XC_MethodReplacement.returnConstant(true))
    
    // Remove watermark
    getDexMethod("WatermarkFingerprint") {
        findMethod {
            matcher {
                strings("video_watermark", "download_with_watermark")
            }
        }.single()
    }.hookMethod(object : XC_MethodHook() {
        override fun beforeHookedMethod(param: MethodHookParam) {
            // Set watermark flag to false
            param.args.forEachIndexed { index, arg ->
                if (arg is Boolean && arg == true) {
                    param.args[index] = false
                }
            }
        }
    })
}
