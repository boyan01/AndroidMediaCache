# AndroidStreamCache [![Download](https://api.bintray.com/packages/summerly/maven/streamcache/images/download.svg)](https://bintray.com/summerly/maven/streamcache/_latestVersion)

为 MediaPlayer 提供一个扩展，实现边放边缓存的功能。

## 如何使用

1. add gradle dependencies

```groovy
implementation "tech.summerly:streamcache:$latest_version"
```

2. 初始化一些变量

```kotlin
override fun onCreate(){
    //to init cache folder
    CacheGlobalSetting.CACHE_PATH = externalCacheDir.path
    CacheGlobalSetting.CACHE_SIZE = 800_000_000
}
```

3. create a custom `MediaDataSource`

```kotlin
private class MediaDataSourceDelegate(dataSource: DataSource)
    : MediaDataSource(), DataSource by dataSource
```

4. done
   ```kotlin
   val dataSource = CachedDataSource(Uri.parse(url))
   mediaPlayer.setDataSource(MediaDataSourceDelegate(dataSource))
   ```

## 附

```kotlin
val dataSource = CachedDataSource(uri = Uri.parse(url),
                                 cacheNameGenerator = md5NameGenerator,
                                 httpHeaderInjector = emptyHeaderInjector,
                                 cacheStrategy = LruCacheStrategy)
```

* **CacheNameGenerator** 为指定的 url 生成一个独特的名字
* **HeaderInjector** 访问 url 所附带的 http 头 
* **CacheStrategy** 成功缓存后的策略

## for other MediaPlayer 

例如 : `ijkMediaPlayer`

```kotlin
//create a custom MediaDataSource
private class MediaDataSourceDelegate(dataSource: DataSource)
    : IMediaDataSource, DataSource by dataSource

//the simple to use
val dataSource = CachedDataSource(Uri.parse(url))
mediaPlayer.setDataSource(MediaDataSourceDelegate(dataSource))
```

## LICENSE
Apache License Version 2.0
