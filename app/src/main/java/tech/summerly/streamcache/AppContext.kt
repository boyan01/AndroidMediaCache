package tech.summerly.streamcache

import android.app.Application
import android.os.Environment

/**
 * author : YangBin
 */
class AppContext : Application() {

    override fun onCreate() {
        super.onCreate()
        StreamCacheUtil.init(this)

        println("cache1 ${Environment.getDownloadCacheDirectory().path}")
        println("cache2 ${this.externalCacheDir.path}")
        println("cache2 ${Environment.getDataDirectory().path}")
        println("cache2 ${Environment.getDataDirectory().path}")
    }
}