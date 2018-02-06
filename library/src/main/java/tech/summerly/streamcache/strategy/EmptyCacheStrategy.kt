package tech.summerly.streamcache.strategy

import java.io.File

/**
 * author : YangBin
 */
internal object EmptyCacheStrategy : CacheStrategy {

    override fun onFileCached(file: File) {
        //do nothing
    }

}