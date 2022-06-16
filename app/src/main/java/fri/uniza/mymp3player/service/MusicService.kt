package fri.uniza.mymp3player.service

import android.app.Service
import android.content.Intent
import android.media.MediaPlayer
import android.os.Binder
import android.os.IBinder

class MusicService: Service() {


    private var msBinder = MSBinder()


    var mediaplayer: MediaPlayer? = null



    override fun onBind(p0: Intent?): IBinder {

        return msBinder
    }

    inner class MSBinder: Binder() {
        fun currentService(): MusicService {
            return this@MusicService
        }
    }


}