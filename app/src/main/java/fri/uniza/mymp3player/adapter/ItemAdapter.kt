package fri.uniza.mymp3player.adapter

import android.content.Context
import android.net.Uri
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.cardview.widget.CardView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import fri.uniza.mymp3player.R
import fri.uniza.mymp3player.model.Song
import java.io.File

class ItemAdapter(
    private val context: Context, private val dataset: List<Song>
) : RecyclerView.Adapter<ItemAdapter.ItemViewHolder>() {

    class ItemViewHolder(private val view: View) : RecyclerView.ViewHolder(view) {
        val songName: TextView = view.findViewById(R.id.song_title)
        val artist: TextView = view.findViewById(R.id.song_artist)
        val menuButton: ImageButton = view.findViewById(R.id.song_button)
        val card: CardView = view.findViewById(R.id.song_card)
        val layout: LinearLayout = view.findViewById(R.id.card_layout)
        val image: ImageView = view.findViewById(R.id.song_image)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemViewHolder {
        val adapterLayout = LayoutInflater.from(parent.context)
            .inflate(R.layout.song_card, parent, false)
        return ItemViewHolder(adapterLayout)
    }

    override fun onBindViewHolder(holder: ItemViewHolder, position: Int) {
        val item = dataset[position]
        holder.songName.text = item.title
        //TODO settings
        if(true) {
            holder.songName.ellipsize = TextUtils.TruncateAt.MARQUEE
            //holder.songName.isSelected = true
            holder.songName.isSingleLine = true
        }
        holder.artist.text = item.artist
        holder.card.setOnClickListener {
            this.clickButton(holder, position)
        }

    }

    private fun clickButton(holder: ItemAdapter.ItemViewHolder, position: Int): View.OnClickListener? {
        Toast.makeText(this.context, dataset[position].title, Toast.LENGTH_SHORT).show()
        val uri = Uri.fromFile(
            File(dataset[position].path))

        return null
    }

    override fun getItemCount() = dataset.size

}