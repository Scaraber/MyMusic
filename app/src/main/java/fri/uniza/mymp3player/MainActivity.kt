package fri.uniza.mymp3player

import android.annotation.SuppressLint
import android.content.pm.PackageManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.view.View
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.view.get
import androidx.recyclerview.widget.RecyclerView
import fri.uniza.mymp3player.adapter.ItemAdapter
import fri.uniza.mymp3player.databinding.ActivitySonglistBinding
import fri.uniza.mymp3player.databinding.SongCardBinding
import fri.uniza.mymp3player.model.Song
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupWithNavController
import java.io.File

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySonglistBinding

    companion object{
        lateinit var MusicListMA : ArrayList<Song>
        //lateinit var PlayLists : ArrayList<ArrayList<Song>>
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestRuntimePermision()
        MusicListMA = getAllAudio()
        //setContentView(R.layout.activity_songlist)
        binding = ActivitySonglistBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val navHostFragment  = supportFragmentManager.findFragmentById(R.id.fragmentContainerView) as NavHostFragment
        val navController = navHostFragment.navController
        binding.topMenu.setupWithNavController(navController)

        //val recyclerView = findViewById<RecyclerView>(R.id.song_list)
        //recyclerView.adapter = ItemAdapter(this, MusicListMA)

        //recyclerView.setHasFixedSize(true)
    }



    private fun requestRuntimePermision(){
        if(ActivityCompat.checkSelfPermission(this, android.Manifest.permission.WRITE_EXTERNAL_STORAGE)
            != PackageManager.PERMISSION_GRANTED)
        {
            ActivityCompat.requestPermissions(this, arrayOf(android.Manifest.permission.WRITE_EXTERNAL_STORAGE), 100)
        }
    }

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
                    val song = Song(id = c_id, title = c_title, album = c_album, artist = c_artist, duration = c_duration, path = c_path)
                    val file = File(song.path)
                    if (file.exists())
                        if (song.path.endsWith(".mp3") or song.path.endsWith(".wav")) {
                            song.changeUnknown()
                            tempList.add(song)
                        }
                } while (cursor.moveToNext())

        cursor?.close()
        return tempList
    }
}