package tech.summerly.streamcache

import android.Manifest
import android.annotation.TargetApi
import android.content.pm.PackageManager
import android.media.MediaDataSource
import android.media.MediaPlayer
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.widget.SeekBar
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.coroutines.experimental.Job
import kotlinx.coroutines.experimental.android.UI
import kotlinx.coroutines.experimental.delay
import kotlinx.coroutines.experimental.launch
import java.io.File

@TargetApi(Build.VERSION_CODES.M)
class MainActivity : AppCompatActivity() {

    private val mediaPlayer = MediaPlayer()

    private val job = Job()

    private val url = "http://m10.music.126.net/20180225130559/499c312b4b33381d8f94c8f9f003af49/ymusic/f7bd/08f4/4466/391ca02c47cb6477cfe2c1dd061c54e8.mp3"

    private val file = File("sdcard/test.mp3")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        if (checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE), 100)
        }
        button.setOnClickListener {
            button.isClickable = false
            if (!mediaPlayer.isPlaying) {
                seekBar.isEnabled = false
                val dataSource = CachedDataSource(Uri.parse(url))
                mediaPlayer.reset()
                mediaPlayer.setDataSource(MediaDataSourceDelegate(dataSource))
                mediaPlayer.prepareAsync()
                mediaPlayer.setOnPreparedListener {
                    mediaPlayer.start()
                    seekBar.isEnabled = true
                    button.isClickable = true
                }
            }
        }

        launch(UI + job) {
            while (true) {
                delay(500)
                text.text = "%s/%s".format(mediaPlayer.currentPosition.toMusicTimeStamp(), mediaPlayer.duration.toMusicTimeStamp())
                seekBar.progress = (mediaPlayer.currentPosition / 1000).toInt()
                seekBar.max = (mediaPlayer.duration / 1000).toInt()
            }
        }
        seekBar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar, progress: Int, fromUser: Boolean) {
                if (fromUser) {
                    val seek = progress * 1000L
                    println("seek to $seek")
                    mediaPlayer.seekTo(seek.toInt())
                }
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {
            }

            override fun onStopTrackingTouch(seekBar: SeekBar?) {
            }
        })
    }

    private fun Number.toMusicTimeStamp(): String = with(this.toLong() / 1000) {
        val second = this % 60
        val minute = this / 60
        "%02d:%02d".format(minute, second)
    }

    override fun onDestroy() {
        super.onDestroy()
        job.cancel()
        mediaPlayer.release()
    }

    private class MediaDataSourceDelegate(dataSource: DataSource)
        : MediaDataSource(), DataSource by dataSource
}
