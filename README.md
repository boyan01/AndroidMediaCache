# AndroidStreamCache

to cache MediaPlayer stream when playing

## HOW TO USE

1. add gradle dependencies

```groovy
implementation "tech.summerly:streamcache:$latest_version"
```

2. initial in Application or others

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

## APPENDIX

```kotlin
val dataSource = CachedDataSource(uri = Uri.parse(url),
                                 cacheNameGenerator = md5NameGenerator,
                                 httpHeaderInjector = emptyHeaderInjector,
                                 cacheStrategy = LruCacheStrategy)
```

* **CacheNameGenerator** to generate a unique file name for url
* **HeaderInjector** inject header for http request
* **CacheStrategy** manage caching

## for other MediaPlayer 

ex : `ijkMediaPlayer`

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
