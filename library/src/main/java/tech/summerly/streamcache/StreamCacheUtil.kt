@file:Suppress("MemberVisibilityCanBePrivate", "unused")

package tech.summerly.streamcache

import android.content.Context
import java.io.File

/**
 * author : YangBin
 */

object StreamCacheUtil {


    private var cache_dir_: File? = null

    internal val CACHE_DIR: File
        get() = cache_dir_ ?: throw RuntimeException("haven't init")

    fun init(context: Context) {
        cache_dir_ = File(context.cacheDir, "stream_cache")
        cache_dir_?.mkdirs()
    }

    fun checkIsCached(fileName: String): Boolean {
        return getCachedFile(fileName) != null
    }

    fun getCachedFile(fileName: String): File? {
        val file = File(CACHE_DIR, fileName)
        return if (file.exists()) {
            file
        } else {
            null
        }
    }

}