package fri.uniza.mymp3player.activity

import android.annotation.SuppressLint
import android.content.Intent
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.os.Bundle
import android.provider.MediaStore
import android.text.TextUtils
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.PopupMenu
import androidx.core.app.ActivityCompat
import com.google.android.material.tabs.TabLayoutMediator
import com.google.gson.Gson
import fri.uniza.mymp3player.R
import fri.uniza.mymp3player.adapter.PagerAdapter
import fri.uniza.mymp3player.data.SongList
import fri.uniza.mymp3player.databinding.ActivitySonglistBinding
import fri.uniza.mymp3player.fragment.SongListFragment
import fri.uniza.mymp3player.model.Song
import kotlinx.android.synthetic.main.activity_songlist.*
import java.io.File

/**
 * Main Activity je hlavná trieda, ktorá zodpovedá za zobrazovanie zoznamu songov v mobile.
 * Na zobrazovanie Songov využíva fragmenty SongListFragment, ktoré sú vo viewpageri
 *
 * @author Patrik Gazdík
 */
class MainActivity : AppCompatActivity() {

    private lateinit var prefs: SharedPreferences

    companion object{
        lateinit var binding: ActivitySonglistBinding
        lateinit var MusicListMA : ArrayList<Song>
        lateinit var favorites: ArrayList<Song>
        lateinit var currentSong : Song
        lateinit var songlist: SongListFragment
        lateinit var favolist: SongListFragment
    }

    /**
     * Metóda onCreate initializuje všetky atribúty a fragmenty,
     * a prečita mp.3 zložky z mobilu
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        prefs = getSharedPreferences(localClassName, MODE_PRIVATE)
        binding = ActivitySonglistBinding.inflate(layoutInflater)
        setContentView(binding.root)
        requestRuntimePermision()
        val list =  prefs.getString("songs", "empty")

        MusicListMA = getAllAudio()
        val current = prefs.getString("currentSong", "empty")
        //ziska udaje zo shared preferences, osetrene proti null
        if (list != "empty") {
            if (list != null) {
                getList(list)
            }
        }
        if (current != "empty") {
            if (current != null) {
                currentSong = deserializeCurrent(current)
            }


        }
        currentSong = MusicListMA[0]

        favorites = findFavorites()
        songlist = SongListFragment.newInstance(MusicListMA) as SongListFragment
        favolist = SongListFragment.newInstance(favorites) as SongListFragment

        setUpTabLayout()
        binding.sortButton.setOnClickListener { sortButton() }

        this.setUpBottomPlayer()
    }

    /**
     * Inicializuje dolnú lištu, ktorá otvára PlaySongActivity
     */
    private fun setUpBottomPlayer() {
        binding.bottomPlayerLayout.setOnClickListener { clickPlaySong() }
        binding.textViewMini.isSingleLine = true
        binding.textViewMini.ellipsize = TextUtils.TruncateAt.END
        binding.textViewMini.text = currentSong.title
    }

    /**
     * Metóda volaná z setUpBottomPlayer na otvorenie PlaySongActivity
     */
    private fun clickPlaySong() {
        val intent = Intent (this, PlaySongActivity::class.java)
        startActivity(intent)
    }

    /**
     * Metóda sa vykoná, keď používateľ klikne na sortButton
     * zobrazuje popup menu ktoré ponúka možnosti zoradenia zoznamov songov
     */
    private fun sortButton() {
        val popMenu =  PopupMenu(this, binding.sortButton)
        popMenu.setOnMenuItemClickListener {
            item -> when (item.itemId){
                R.id.alpha_asc -> {
                    MusicListMA.sortBy { it.title }
                    favorites.sortBy { it.title }
                    if (tabLayout.selectedTabPosition == 0) songlist.reloadDataSet() else favolist.reloadDataSet()
                    true
                }
                R.id.alpha_desc -> {
                    MusicListMA.sortByDescending { it.title }
                    favorites.sortByDescending { it.title }
                    if (tabLayout.selectedTabPosition == 0) songlist.reloadDataSet() else favolist.reloadDataSet()
                    true
                }
                R.id.author_asc -> {
                    MusicListMA.sortBy { it.artist }
                    favorites.sortBy { it.artist }
                    if (tabLayout.selectedTabPosition == 0) songlist.reloadDataSet() else favolist.reloadDataSet()
                    true
                }
                R.id.author_desc -> {
                    MusicListMA.sortByDescending { it.artist }
                    favorites.sortByDescending { it.artist }
                    if (tabLayout.selectedTabPosition == 0) songlist.reloadDataSet() else favolist.reloadDataSet()
                    true
                }
                R.id.date_asc -> {
                    MusicListMA.sortBy { it.date }
                    favorites.sortBy { it.date }
                    if (tabLayout.selectedTabPosition == 0) songlist.reloadDataSet() else favolist.reloadDataSet()
                    true
                }
                R.id.date_desc -> {
                    MusicListMA.sortByDescending { it.date }
                    favorites.sortByDescending { it.date }
                    if (tabLayout.selectedTabPosition == 0) songlist.reloadDataSet() else favolist.reloadDataSet()
                    true
                }
                else ->  false
        }
        }
        popMenu.inflate(R.menu.sort_menu)
        popMenu.show()
    }

    /**
     * Metóda prechádza cez všetky songy, a pridá obľubené songy do zoznamu obľúbených songov.
     */
    fun findFavorites(): ArrayList<Song> {
        val favorites = ArrayList<Song>()
        for (song in MusicListMA) {
            if (song.isFavorite) favorites.add(song)
        }
        return favorites
    }

    /**
     * Inicializuje TabLayout ktorý naviguje Fragmenty
     */
    private fun setUpTabLayout() {
        var tabTitle = arrayOf("Songs", "Favorites", "Playlists")
        binding.pager.adapter = PagerAdapter(supportFragmentManager, lifecycle, songlist, favolist)
        TabLayoutMediator(binding.tabLayout, binding.pager) { tab, position ->
            tab.text = tabTitle[position]
        }.attach()

    }


    /**
     * Metóda na vypýtanie povolenia na čítanie súborov od používateľa
     */
    private fun requestRuntimePermision(){
        if(ActivityCompat.checkSelfPermission(this, android.Manifest.permission.WRITE_EXTERNAL_STORAGE)
            != PackageManager.PERMISSION_GRANTED)
        {
            ActivityCompat.requestPermissions(this, arrayOf(android.Manifest.permission.WRITE_EXTERNAL_STORAGE), 100)
        }
    }

    /**
     * Metóda spracuje výsledok po metóde requestRuntimePermision()
     */
    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if(requestCode == 100) {
            if(grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED)
                Toast.makeText(this, "Permission Granted", Toast.LENGTH_SHORT).show()
             else
                Toast.makeText(this, "Permission Denied", Toast.LENGTH_SHORT).show()
        }
    }

    /**
     * Metóda na prečítanie všetkých súborov z mobilu a pridanie mp.3 súborov do ArrayListu
     * Songy balí v Dátovej triede Song
     * Metóda vylúči songy kratšie ako 10 sekúnd
     */
    @SuppressLint("Range")
    private fun getAllAudio(): ArrayList<Song>{
        val tempList = ArrayList<Song>()
        val selection = MediaStore.Audio.Media.IS_MUSIC + " != 0"
        val projection = arrayOf(
            MediaStore.Audio.Media._ID, MediaStore.Audio.Media.TITLE, MediaStore.Audio.Media.ALBUM,
            MediaStore.Audio.Media.ARTIST, MediaStore.Audio.Media.DURATION, MediaStore.Audio.Media.DATE_ADDED,
            MediaStore.Audio.Media.DATA)
        val cursor = this.contentResolver.query(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, projection, selection, null,
            MediaStore.Audio.Media.DATE_ADDED + " DESC", null)
        if(cursor != null)
            if(cursor.moveToFirst())
                do {
                    val c_title = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.TITLE))
                    val c_id = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media._ID))
                    val c_album = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.ALBUM))
                    val c_artist = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.ARTIST))
                    val c_duration = cursor.getLong(cursor.getColumnIndex(MediaStore.Audio.Media.DURATION))
                    val c_path = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.DATA))
                    val c_date = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.DATE_ADDED))
                    val song = Song(id = c_id, title = c_title, album = c_album, artist = c_artist, duration = c_duration, path = c_path,date = c_date)
                    val file = File(song.path)
                    if (file.exists())
                        if (song.path.endsWith(".mp3") or song.path.endsWith(".wav")) {

                            //TODO zmenit nastavenia
                            if (c_duration > 10000) {
                                song.changeUnknown()
                                tempList.add(song)
                            }
                        }
                } while (cursor.moveToNext())

        cursor?.close()
        return tempList
    }


    /**
     * onDestroy uloží preferencie aplikácie
     */
    override fun onDestroy() {
        super.onDestroy()
        savePreferences()
    }

    /**
     * Metóda onStop znovu prejde songy aby uložila obľúbené
     */
    override fun onStop() {
        super.onStop()
        findFavorites()
    }

    /**
     * onPause uloží preferencie aplikácie
     */
    override fun onPause() {
        super.onPause()
        savePreferences()
    }

    /**
     * Pomocou SharedPreferenced uloží dáta z aplikácie
     * uloží sa posledný hraný song a zoznam songov
     */
    private fun savePreferences() {
        findFavorites()
        val ed: SharedPreferences.Editor = prefs.edit()
        ed.putString("songs", getStrings())
        ed.putString("currentSong", serializeCurrent())
        ed.commit()
    }

    /**
     * pomocou Gson() premení posledný hraný song na string.
     */
    private fun serializeCurrent(): String? {
        val json = Gson().toJson(currentSong)
        return json
    }

    /**
     * pomocou Gson() premení string na posledný hraný song.
     */
    private fun deserializeCurrent(string: String): Song {
        val json = Gson().fromJson(string, Song::class.java)
        return json
    }

    /**
     * Pomocou Gson() premení zoznam songov na string
     */
    fun getStrings(): String {
        val json = Gson().toJson(SongList(MusicListMA))
        return json
    }

    /**
     * Pomocou Gson() premení string na zoznam listov
     * tento zoznam potom pošle do rememberSongs() na spojenie dát zo songami prečítanými z mobilu
     */
    fun getList(string: String) {
       val back = Gson().fromJson(string, SongList::class.java)
        rememberSongs(back.songs)
    }


    /**
     * Po prečítaní songov táto metóda spojí dáta zo súboru a dáta uložené aplikáciou
     */
    private fun rememberSongs(songs: ArrayList<Song>) {
        //songs.addAll(MusicListMA)
        //MusicListMA = songs.distinctBy { it.id } as ArrayList<Song>
        for (song_file in MusicListMA) {
            for (song_last in songs) {
                if(song_file.id == song_last.id) {
                    song_file.isFavorite = song_last.isFavorite
                    song_file.currentTime = song_last.currentTime
                    break;
                }
            }
        }
    }


}