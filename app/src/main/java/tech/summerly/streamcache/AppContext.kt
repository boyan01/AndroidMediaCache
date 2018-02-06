package tech.summerly.streamcache

import android.app.Application

/**
 * author : YangBin
 */
class AppContext : Application() {

    override fun onCreate() {
        super.onCreate()
        StreamCacheUtil.init(this)
    }
}