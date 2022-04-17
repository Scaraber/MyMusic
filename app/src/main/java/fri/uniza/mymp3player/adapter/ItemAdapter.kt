package fri.uniza.mymp3player.adapter

import android.content.Context
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import fri.uniza.mymp3player.R
import fri.uniza.mymp3player.models.Song

class ItemAdapter(
    private val context: Context, private val dataset: List<Song>
) : RecyclerView.Adapter<ItemAdapter.ItemViewHolder>() {

    class ItemViewHolder(private val view: View) : RecyclerView.ViewHolder(view) {
        val songName: TextView = view.findViewById(R.id.song_title)
        val artist: TextView = view.findViewById(R.id.song_artist)
        val menuButton: ImageButton = view.findViewById(R.id.song_button)
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
            holder.songName.isSelected = true
            holder.songName.isSingleLine = true
        }
        holder.artist.text = item.artist
    }

    override fun getItemCount() = dataset.size

}