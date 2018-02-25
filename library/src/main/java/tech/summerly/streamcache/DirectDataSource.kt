package tech.summerly.streamcache

import android.net.Uri
import tech.summerly.streamcache.cache.source.FileSource
import tech.summerly.streamcache.cache.source.HttpSource
import tech.summerly.streamcache.cache.source.Source
import tech.summerly.streamcache.utils.LoggerLevel
import tech.summerly.streamcache.utils.emptyHeaderInjector
import tech.summerly.streamcache.utils.log
import java.io.IOException

/**
 * Created by summer on 18-2-23
 *
 * 直接从Source获取数据,不提供缓存支持.
 */
class DirectDataSource(private val source: Source) : DataSource {

    companion object {

        operator fun invoke(uri: Uri): DirectDataSource {
            val source = if (uri.scheme == "file") {
                FileSource(uri.path)
            } else {
                HttpSource(uri.toString(), emptyHeaderInjector)
            }
            return DirectDataSource(source)
        }

    }

    private var currentPosition: Long = -1

    override fun readAt(position: Long, buffer: ByteArray, offset: Int, size: Int): Int {
        log(LoggerLevel.DEBUG) { "position : $currentPosition , $offset $position " }
        return try {
            if (position != currentPosition) {
                seekTo(position)
            }
            var n = source.read(buffer, offset, size)
            if (n < 0) {
                n = 0
            }
            currentPosition += n
            n
        } catch (e: IOException) {
            log(LoggerLevel.ERROR) {
                e.printStackTrace()
                "io error to read :$position at $source"
            }
            -1
        } catch (e: Exception) {
            e.printStackTrace()
            log(LoggerLevel.ERROR) {
                "unknown error to read :$position at $source"
            }
            -1
        }
    }

    /**
     *
     * seek data reader index to [offset]
     *
     * @throws IOException
     */
    private fun seekTo(offset: Long) {
        log(LoggerLevel.INFO) { "seek to $offset" }
        try {
            source.open(offset)
            currentPosition = offset
        } catch (e: IOException) {
            e.printStackTrace()
            currentPosition = -1
            throw e
        }
    }

    override fun getSize(): Long {
        return source.size
    }

    override fun close() {
        source.close()
    }
}