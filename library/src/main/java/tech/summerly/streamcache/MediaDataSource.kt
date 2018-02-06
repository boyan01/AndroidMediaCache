package tech.summerly.streamcache

import android.annotation.TargetApi
import android.media.MediaDataSource
import android.os.Build
import tech.summerly.streamcache.cache.Cache
import tech.summerly.streamcache.cache.FileCache
import tech.summerly.streamcache.cache.InputStreamCache
import tech.summerly.streamcache.strategy.EmptyCacheStrategy
import java.io.InputStream

/**
 * author : YangBin
 */
@TargetApi(Build.VERSION_CODES.M)
class MediaDataSource internal constructor(
        private val cache: Cache
) : MediaDataSource() {

    constructor(inputStream: InputStream, cacheName: String) : this(Unit.let {
        val fileCache = FileCache(cacheName, EmptyCacheStrategy)
        if (fileCache.isComplete) {
            fileCache
        } else {
            InputStreamCache(inputStream, fileCache)
        }
    })

    override fun readAt(position: Long, buffer: ByteArray, offset: Int, size: Int): Int {
        if (size == 0) {
            return 0
        }
        return cache.read(position, buffer, offset, size)
    }

    override fun getSize(): Long {
        return cache.available
    }

    override fun close() {
        cache.close()
    }

}