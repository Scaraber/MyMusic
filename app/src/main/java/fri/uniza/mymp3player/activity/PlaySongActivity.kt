package fri.uniza.mymp3player.activity

import android.content.ComponentName
import android.content.Intent
import android.content.ServiceConnection
import android.content.SharedPreferences
import android.media.MediaPlayer
import android.os.Bundle
import android.os.Handler
import android.os.IBinder
import android.os.Looper
import android.text.TextUtils
import android.widget.SeekBar
import androidx.appcompat.app.AppCompatActivity
import fri.uniza.mymp3player.R
import fri.uniza.mymp3player.databinding.ActivityPlaySongBinding
import fri.uniza.mymp3player.model.Song
import fri.uniza.mymp3player.service.MusicService
import kotlinx.android.synthetic.main.activity_play_song.*
import java.util.concurrent.TimeUnit

/**
 * Aktivita implementújúca [ServiceConnection] a [MediaPlayer.OnCompletionListener]
 * Je určená na hranie songov. Dokáže song zastaviť, íst dopredu, dozadu, opakovať a pridať song medzi obľúbené.
 * Môže íst medzi songami zaradom alebo náhodne preskakovať
 */
class PlaySongActivity : AppCompatActivity(), ServiceConnection, MediaPlayer.OnCompletionListener {

    private var shuffle: Boolean = false
    private lateinit var binding: ActivityPlaySongBinding
    var isPlaying:Boolean = false
    private lateinit var prefs: SharedPreferences
    private var repeat = 0;

    /**
     * companion object uchováva niektoré atribúty triedy
     */
    companion object {
        var musicService: MusicService? = null
        var database: ArrayList<Song> = MainActivity.MusicListMA
        var position: Int = database.indexOf(MainActivity.currentSong)
    }

    /**
     * inicializácia atribútov a listenerov
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        prefs = getSharedPreferences(localClassName, MODE_PRIVATE)
        isPlaying = prefs.getBoolean("isPlaying", false)
        binding = ActivityPlaySongBinding.inflate(layoutInflater)
        setContentView(binding.root)

        //music service
        val intent = Intent(this, MusicService::class.java)
        bindService(intent, this, BIND_AUTO_CREATE)
        startService(intent)


        binding.songName.ellipsize = TextUtils.TruncateAt.MARQUEE
        binding.songName.isSingleLine = true

        binding.shuffleButton.setOnClickListener {
            shuffleButton()
        }

        binding.repeatButton.setOnClickListener {
            repeatButton()
        }

        binding.favourite.setOnClickListener {
            favorite()
        }

        binding.backbutton.setOnClickListener {
            back()
        }

        binding.nextbutton.setOnClickListener {
            next()
        }

        binding.playbutton.setOnClickListener {
            this.playButton()
        }
        refreshTitle()
        initializeSeekBar()
    }

    /**
     * obnoví ukazované časi na aktuálne
     */
    private fun refreshTimer() {
        binding.currentTime.text = getTime(musicService?.mediaplayer?.currentPosition)
        binding.totalTime.text = getTime(database[position].duration.toInt())
    }

    /**
     * inicializácia [seekBar], a nastavenie [setOnSeekBarChangeListener]
     */
    private fun initializeSeekBar() {
                binding.seekBar.max = database[position].duration.toInt()
                val handler = Handler(Looper.getMainLooper())
                handler.postDelayed(object : Runnable {
                    override fun run() {
                        try {
                            seekBar.progress = musicService!!.mediaplayer!!.currentPosition
                            handler.postDelayed(this, 1000)
                        } catch (e: Exception) {
                            seekBar.progress = 0
                        }
                    }
                }, 0)



        binding.seekBar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener{
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                if (musicService != null) {
                    if (musicService!!.mediaplayer != null) {
                      if (fromUser)  musicService!!.mediaplayer!!.seekTo(progress)
                        database[position].currentTime = musicService!!.mediaplayer!!.currentPosition.toLong()
                    }
                }

                refreshTimer()
            }

            override fun onStartTrackingTouch(p0: SeekBar?) {

            }

            override fun onStopTrackingTouch(p0: SeekBar?) {

            }


        })

    }

    /**
     * po kliknutí na [shuffleButton] obráti hodnotu [shuffle]
     */
    private fun shuffleButton() {
        shuffle = !shuffle
        refreshTitle()
    }

    /**
     * po kliknutí na [repeatButton] zmení hodnotu [repeat]
     * používa sa na určenie, či chce používateľ po skončení songu isť ďalej, opakovať raz, alebo opakovať neustále
     */
    private fun repeatButton() {
        when (repeat) {
            0 -> {
                repeat = 1
                binding.repeatButton.setImageResource(R.drawable.ic_baseline_repeat_one_24)
            }
            1 -> {repeat = 2
                binding.repeatButton.setImageResource(R.drawable.ic_baseline_repeat_24)
            }
            2 -> {repeat = 0
                binding.repeatButton.setImageResource(R.drawable.dont_repeat)
            }
            else -> {
                repeat = 0
                binding.repeatButton.setImageResource(R.drawable.dont_repeat)
            }
        }
    }

    /**
     * prida alebo odstráni [Song] z obľúbených
     */
    private fun favorite() {
        if (MainActivity.currentSong.isFavorite) {
            binding.favourite.setImageResource(R.drawable.ic_baseline_favorite_border_24)
            MainActivity.favorites.remove(MainActivity.currentSong)
        } else {
            binding.favourite.setImageResource(R.drawable.ic_baseline_favorite_24)
            MainActivity.favorites.add(MainActivity.currentSong)
        }
        MainActivity.currentSong.isFavorite = !MainActivity.currentSong.isFavorite
    }

    /**
     * prejde na ďaľší song, preskočí [repeat]
     */
    private fun next() {
        if (shuffle) {
            position = (0 until database.size).random()
        } else {
            position = (position + 1) % database.size
        }
        repeat = 0
        this.refreshTitle()
        createMediaPlayer()
    }
    /**
     * prejde na predošlý song, preskočí [repeat]
     */
    private fun back() {
        if (position > 0) {
            position--
        } else {
            position = database.size - 1
        }
        repeat = 0
        this.refreshTitle()
        createMediaPlayer()
    }

    override fun onStop() {
        super.onStop()
        musicService = null
    }

    /**
     * znovu načíta zobrazované časti aktivity, aby sa zobrazili aktuálne údaje
     */
    private fun refreshTitle() {
        var currentSong = database[position]
        MainActivity.currentSong = currentSong
        binding.songName.text = currentSong.title
        binding.songArtist.text = currentSong.artist
        if (isPlaying) binding.playbutton.setImageResource(R.drawable.ic_baseline_play_arrow_24) else binding.playbutton.setImageResource(R.drawable.ic_baseline_pause_24)
        if (MainActivity.currentSong.isFavorite) {
            binding.favourite.setImageResource(R.drawable.ic_baseline_favorite_24)
        } else {
            binding.favourite.setImageResource(R.drawable.ic_baseline_favorite_border_24)
        }
        if (shuffle) binding.shuffleButton.setImageResource(R.drawable.ic_baseline_shuffle_24) else binding.shuffleButton.setImageResource(R.drawable.dont_shuffle)
        refreshTimer()
        MainActivity.binding.textViewMini.text = currentSong.title
    }

    /**
     * zmení hodnotu času v milisekundách na formát "MM:SS"
     */
    private fun getTime(time: Int?): String {
        if (time != null) {
            var millis = time.toLong()
            return String.format(
                "%02d:%02d",
                TimeUnit.MILLISECONDS.toMinutes(millis),
                TimeUnit.MILLISECONDS.toSeconds(millis) % TimeUnit.MINUTES.toSeconds(1))
        } else return "00:00"
    }

    private fun playButton() {
        if (isPlaying) pauseMusic() else playMusic()
    }

    /**
     * pri pauze sa uložia údaje z aplikácie
     */
    override fun onPause() {
        super.onPause()
        val ed: SharedPreferences.Editor = prefs.edit()
        ed.putBoolean("isPlaying", isPlaying)
        ed.commit()
        database[position].currentTime = musicService!!.mediaplayer!!.currentPosition.toLong()
    }

    /**
     * pripojí [MusicService] k aktivite a inicializuje [MediaPlayer]
     */
    override fun onServiceConnected(name: ComponentName?, service: IBinder?) {
        val binder = service as MusicService.MSBinder
        musicService = binder.currentService()
        createMediaPlayer()
    }

    /**
     * pri odpojení [MusicService] odstráni [musicService]
     */
    override fun onServiceDisconnected(p0: ComponentName?) {
        musicService = null
    }

    /**
     * inicializuje [musicService] a [MediaPlayer]
     */
    private fun createMediaPlayer() {
            try {
                if (musicService!!.mediaplayer == null) musicService!!.mediaplayer = MediaPlayer()
                musicService!!.mediaplayer!!.reset()
                musicService!!.mediaplayer!!.setDataSource(database[position].path)
                musicService!!.mediaplayer!!.prepare()
                musicService!!.mediaplayer!!.setOnCompletionListener(this)
                if (isPlaying) playMusic()
            }catch (e: Exception){
            }

    }

    /**
     * začne hrať hudbu
     */
    private fun playMusic(){
        isPlaying = true
        musicService!!.mediaplayer!!.seekTo(database[position].currentTime.toInt())
        musicService!!.mediaplayer!!.start()

        binding.playbutton.setImageResource(R.drawable.ic_baseline_play_arrow_24)
        initializeSeekBar()
    }

    /**
     * zastaví hudbu
     */
    private fun pauseMusic(){
        isPlaying = false
        musicService!!.mediaplayer!!.pause()
        binding.playbutton.setImageResource(R.drawable.ic_baseline_pause_24)
    }

    /**
     * po dokončení songu automaticky začne hrať ďaľší
     */
    override fun onCompletion(p0: MediaPlayer?) {
        database[position].currentTime = 0
        when (repeat) {
            0 -> next()
            1 -> {
                repeat = 0
                binding.repeatButton.setImageResource(R.drawable.dont_repeat)
                playMusic()
            }
            2 -> playMusic()
            else -> next()
        }
    }
}