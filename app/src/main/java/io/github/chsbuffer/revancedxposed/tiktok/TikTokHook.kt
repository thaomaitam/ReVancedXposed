package io.github.chsbuffer.revancedxposed.tiktok

import android.app.Application
import app.revanced.extension.shared.Utils
import de.robv.android.xposed.callbacks.XC_LoadPackage.LoadPackageParam
import io.github.chsbuffer.revancedxposed.BaseHook
import io.github.chsbuffer.revancedxposed.addModuleAssets
import io.github.chsbuffer.revancedxposed.injectHostClassLoaderToSelf

class TikTokHook(
    app: Application,
    lpparam: LoadPackageParam
) : BaseHook(app, lpparam) {
    
    override val hooks = arrayOf(
        ::ExtensionHook,
        ::RemoveAds,
        ::EnableDownload,
        ::FeedFilter,
        ::PlaybackSpeed,
        ::SettingsHook
    )
    
    fun ExtensionHook() {
        Utils.setContext(app)
        injectHostClassLoaderToSelf(this::class.java.classLoader!!, classLoader)
        app.addModuleAssets()
    }
}
