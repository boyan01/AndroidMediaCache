package tech.summerly.streamcache

import android.app.Application

/**
 * author : YangBin
 */
class AppContext : Application() {
    override fun onCreate() {
        super.onCreate()
        CacheGlobalSetting.CACHE_PATH = externalCacheDir.path
//        CacheGlobalSetting.CACHE_SIZE = 800_000_000
    }
}