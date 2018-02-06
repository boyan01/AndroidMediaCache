package tech.summerly.streamcache.cache

import kotlinx.coroutines.experimental.Job
import kotlinx.coroutines.experimental.cancelChildren
import kotlinx.coroutines.experimental.launch
import java.io.InputStream

/**
 * author : YangBin
 */
internal class InputStreamCache(
        private val inputStream: InputStream,
        private val fileCache: FileCache
) : Cache() {

    companion object {
        const val SIZE_BUFFER = 4096
    }

    private val cacheJob: Job

    /**
     * initial to read the input stream and cache bytes to file
     */
    init {
        cacheJob = launch {
            val buffer = ByteArray(SIZE_BUFFER)
            var bytes = inputStream.read(buffer)
            while (bytes > 0) {
                fileCache.write(buffer, 0, bytes)
                notifyNewDataAvailable()
                bytes = inputStream.read(buffer)
            }
            fileCache.close()
        }
    }


    private fun notifyNewDataAvailable() = synchronized(lock) {
        lock.notifyAll()
    }

    private val lock = java.lang.Object()

    override fun write(byteArray: ByteArray, off: Int, len: Int) = throw UnsupportedOperationException()

    override fun read(position: Long, byteArray: ByteArray, offset: Int, len: Int): Int {
        while (!fileCache.isComplete && position + len > available) {
            waitForInputStream()
        }
        return fileCache.read(position, byteArray, offset, len)
    }

    private fun waitForInputStream() = synchronized(lock) {
        lock.wait(1000)
    }

    override val available: Long
        get() = fileCache.available

    override val isComplete: Boolean
        get() = throw UnsupportedOperationException()

    override fun close() {
        cacheJob.cancelChildren()
        fileCache.close()
    }

}