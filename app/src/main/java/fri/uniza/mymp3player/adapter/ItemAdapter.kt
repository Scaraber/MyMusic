package fri.uniza.mymp3player.adapter

import android.content.ClipData.Item
import android.content.Intent
import android.net.Uri
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.cardview.widget.CardView
import androidx.fragment.app.FragmentActivity
import androidx.recyclerview.widget.RecyclerView
import fri.uniza.mymp3player.R
import fri.uniza.mymp3player.activity.MainActivity
import fri.uniza.mymp3player.activity.PlaySongActivity
import fri.uniza.mymp3player.model.Song
import java.io.File

/**
 * ItemAdapter
 * @author Patrik Gazdík
 */
class ItemAdapter(
    private val context: FragmentActivity?, private var dataset: ArrayList<Song>
) : RecyclerView.Adapter<ItemAdapter.ItemViewHolder>() {

    /**
     * trieda na držanie údajov o songu
     */
    class ItemViewHolder(private val view: View) : RecyclerView.ViewHolder(view) {
        val songName: TextView = view.findViewById(R.id.song_title)
        val artist: TextView = view.findViewById(R.id.song_artist)
        val card: CardView = view.findViewById(R.id.song_card)
        val layout: LinearLayout = view.findViewById(R.id.card_layout)
        val image: ImageView = view.findViewById(R.id.song_image)
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemViewHolder {
        val adapterLayout = LayoutInflater.from(parent.context)
            .inflate(R.layout.song_card, parent, false)
        return ItemViewHolder(adapterLayout)
    }

    /**
     * Inicializácia údajov a metód
     */
    override fun onBindViewHolder(holder: ItemViewHolder, position: Int) {
        val item = dataset[position]
        holder.songName.text = item.title
        //TODO settings
        if(true) {
            holder.songName.ellipsize = TextUtils.TruncateAt.MARQUEE
            holder.songName.isSingleLine = true
        }
        holder.artist.text = item.artist
        holder.card.setOnClickListener {
            this.clickButton(holder, position)
        }
    }

    /**
     * Po klknutí na kartu songu sa zmení momentálny song a prepne sa na aktivituy PlaySongActivity
     */
    private fun clickButton(holder: ItemAdapter.ItemViewHolder, position: Int): View.OnClickListener? {
        val uri = Uri.fromFile(
            File(dataset[position].path))
        MainActivity.currentSong = dataset[position]

        val intent = Intent (this.context, PlaySongActivity::class.java)
        this.context?.startActivity(intent)
        PlaySongActivity.database = dataset
        PlaySongActivity.position = position
        return null
    }

    /**
     * vráti velkosť zoznamu songov
     */
    override fun getItemCount() = dataset.size




}