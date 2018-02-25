package tech.summerly.streamcache.cache.source

import tech.summerly.streamcache.utils.LoggerLevel
import tech.summerly.streamcache.utils.log
import java.io.IOException
import java.io.RandomAccessFile

/**
 * Created by summer on 18-2-23
 *
 * 本地文件资源
 *
 */
internal class FileSource(path: String) : Source() {


    val file = java.io.File(path)

    private lateinit var data: RandomAccessFile

    override val size: Long = file.length()

    init {
        if (!file.exists()) {
            throw IOException("file do not exists : ${file.path}")
        }
    }

    override fun open(offset: Long) {
        log(LoggerLevel.DEBUG) { "open source :$this at $offset" }
        data = RandomAccessFile(file, "r")
        if (offset > 0) {
            data.seek(offset)
        }
    }

    override fun read(byteArray: ByteArray, off: Int, len: Int): Int {
        return data.read(byteArray, off, len)
    }

    override fun close() {
        log(LoggerLevel.DEBUG) { "close source :$this" }
        if (this::data.isInitialized) {
            data.close()
        }
    }


    override fun toString(): String {
        return "source :path=${file.path}"
    }
}