package tech.summerly.streamcache.cache

/**
 * author : YangBin
 */


abstract class Cache {

    abstract val isComplete: Boolean


    abstract fun read(position: Long, byteArray: ByteArray, offset: Int, len: Int): Int

    abstract fun write(byteArray: ByteArray, off: Int = 0, len: Int = byteArray.size)

    /**
     * return the available content length of cache
     */
    open val available: Long = 0

    /**
     * complete this cache
     */
     open fun close() {

    }

}