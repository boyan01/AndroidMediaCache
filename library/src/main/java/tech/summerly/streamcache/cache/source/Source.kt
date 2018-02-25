package tech.summerly.streamcache.cache.source

import java.io.Closeable

/**
 * Created by summer on 18-2-23
 *
 * 资源
 *
 */
abstract class Source : Closeable {

    /**
     * 返回可用字节数
     * 如果资源未知,那么返回负数.
     */
    abstract val size: Long


    abstract fun read(byteArray: ByteArray, off: Int = 0, len: Int = byteArray.size): Int

    /**
     * 打开资源,必须在 [read] 方法前调用此方法
     *
     * @param offset 距离资源起点的偏移量
     */
    abstract fun open(offset: Long = 0)


}