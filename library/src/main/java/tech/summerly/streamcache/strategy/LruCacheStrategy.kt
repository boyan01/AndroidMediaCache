package tech.summerly.streamcache.strategy

import kotlinx.coroutines.experimental.async
import tech.summerly.streamcache.StreamCacheUtil
import tech.summerly.streamcache.utils.LoggerLevel
import tech.summerly.streamcache.utils.log
import java.io.File

/**
 * author : YangBin
 */
object LruCacheStrategy : CacheStrategy {

    private const val DEFAULT_MAX_LIMIT_SIZE = 1024 * 1024 * 1024 // 1GB

    override fun onFileCached(file: File) {
        if (!file.exists()) {
            log(LoggerLevel.ERROR) { "file has been cached , but still not exists" }
            return
        }
        async {
            file.setLastModified(System.currentTimeMillis())
            val files = StreamCacheUtil.CACHE_DIR.listFiles() ?: return@async
            clearUp(files)
        }
    }

    //to clear up cache
    private fun clearUp(files: Array<File>) {
        files.sortBy { it.lastModified() }
        var totalSize = files.calculateTotalSize()
        files.forEach { file ->
            if (totalSize > DEFAULT_MAX_LIMIT_SIZE) {
                val size = file.length()
                if (file.delete()) {
                    totalSize -= size
                } else {
                    log(LoggerLevel.ERROR) { "error to delete cache" }
                }
            } else {
                return@forEach
            }
        }
    }

    /**
     * calculate a list of file's size
     */
    private fun Array<File>.calculateTotalSize() = fold(0L) { acc, file ->
        if (file.isDirectory) {
            //delete ?
            acc
        } else {
            acc + file.length()
        }
    }

}