package tech.summerly.streamcache

import android.Manifest
import android.annotation.TargetApi
import android.content.pm.PackageManager
import android.media.MediaPlayer
import android.os.Build
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_main.*
import java.io.File
import java.io.InputStream

@TargetApi(Build.VERSION_CODES.M)
class MainActivity : AppCompatActivity() {

    private val mediaPlayer = MediaPlayer()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        if (checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE), 100)
        }
        button.setOnClickListener {
            button.isClickable = false
            if (!mediaPlayer.isPlaying) {
                val inputStream = getInputStream()
                val dataSource = MediaDataSource(inputStream, "cache")
                mediaPlayer.reset()
                mediaPlayer.setDataSource(dataSource)
                mediaPlayer.prepare()
                mediaPlayer.start()
            }
            button.isClickable = true
        }
    }

    private fun getInputStream(): InputStream {
        val file = File("sdcard/send moon.mp3")
        return file.inputStream()
    }

    private fun Int.toMusicTimeStamp(): String = with(this / 1000) {
        val second = this % 60
        val minute = this / 60
        "%02d:%02d".format(minute, second)
    }

    override fun onDestroy() {
        super.onDestroy()
        mediaPlayer.release()
    }
}
