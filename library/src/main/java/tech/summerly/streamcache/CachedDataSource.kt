package tech.summerly.streamcache

import android.net.Uri
import kotlinx.coroutines.experimental.Job
import kotlinx.coroutines.experimental.launch
import tech.summerly.streamcache.cache.Cache
import tech.summerly.streamcache.cache.FileCache
import tech.summerly.streamcache.cache.source.HttpSource
import tech.summerly.streamcache.cache.source.Source
import tech.summerly.streamcache.strategy.CacheStrategy
import tech.summerly.streamcache.strategy.LruCacheStrategy
import tech.summerly.streamcache.utils.*

/**
 * author : YangBin
 *
 */
open class CachedDataSource internal constructor(
        private val cache: Cache,
        private val source: Source
) : DataSource {


    companion object {

        private const val SIZE_BUFFER = 4096

        /**
         * 使用此方法来构造 CachedDataSource
         *
         * @param uri 文件地址
         * @param cacheNameGenerator 缓存文件命名生成器,默认使用MD5值命名.
         * @param httpHeaderInjector http请求头参数,默认不做任何处理
         * @param cacheStrategy 缓存策略,默认使用[LruCacheStrategy]
         */
        operator fun invoke(uri: Uri,
                            cacheNameGenerator: CacheNameGenerator = md5NameGenerator,
                            httpHeaderInjector: HeaderInjector = emptyHeaderInjector,
                            cacheStrategy: CacheStrategy = LruCacheStrategy): CachedDataSource {
            val cacheName = cacheNameGenerator(uri.path)
            val fileCache = FileCache(cacheName, cacheStrategy)
            val source = HttpSource(uri.toString(), httpHeaderInjector)
            return CachedDataSource(fileCache, source)
        }
    }

    private var currentPosition: Long = -1

    override fun readAt(position: Long, buffer: ByteArray, offset: Int, size: Int): Int {
        if (size == 0) {
            return 0
        }
        val read = if (isUseCache(position)) {
            readByCache(position, buffer, offset, size)
        } else {
            readDirectly(position, buffer, offset, size)
        }
        currentPosition = position
        if (read >= 0) {
            currentPosition += read
        }
        return read
    }

    private fun readByCache(position: Long, buffer: ByteArray, offset: Int, size: Int): Int {
        while (!cache.isComplete && cache.available < position + size) {
            readSourceAsync()
            waitForSource()
        }
        return cache.read(position, buffer, offset, size)
    }

    //直接从source读取资源
    private fun readDirectly(position: Long, buffer: ByteArray, offset: Int, size: Int): Int {
        //stop cache process
        sourceReaderJob?.cancel()
        //重新打开资源
        if (currentPosition != position) {
            source.open(position)
        }
        //再从资源开始读取
        return source.read(buffer, offset, size)
    }

    /**
     *
     * 判断读取此position的资源时,是否可以使用缓存或者等待缓存
     *
     * @param position 距离资源起点的偏移
     */
    private fun isUseCache(position: Long): Boolean {
        val isSourceLengthKnown = source.size > 0
        return !isSourceLengthKnown || position < cache.available + source.size * .2f
    }

    override fun getSize(): Long {
        return source.size
    }

    override fun close() {
        log { "close DataSource..." }
        cache.close()
        source.close()
        sourceReaderJob?.cancel()
    }

    private var sourceReaderJob: Job? = null

    private fun readSourceAsync() {
        val isReading = sourceReaderJob?.isActive
        if (!cache.isComplete && isReading != true) {
            sourceReaderJob = readSource()
        }
    }

    private fun readSource() = launch {
        source.open(cache.available)
        val buffer = ByteArray(SIZE_BUFFER)
        var bytes = source.read(buffer)
        while (bytes > 0) {
            cache.write(buffer, 0, bytes)
            notifyNewDataAvailable()
            bytes = source.read(buffer)
        }
        cache.complete()
    }

    private fun notifyNewDataAvailable() = synchronized(lock) {
        lock.notifyAll()
    }


    private fun waitForSource() = synchronized(lock) {
        lock.wait(1000)
    }

    private val lock = java.lang.Object()

}