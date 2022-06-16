package fri.uniza.mymp3player.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.RecyclerView
import fri.uniza.mymp3player.R
import fri.uniza.mymp3player.adapter.ItemAdapter
import fri.uniza.mymp3player.databinding.ActivitySonglistBinding
import fri.uniza.mymp3player.model.Song

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * trieda [Fragment] ktorá implementuje RecyclerView na zobrazovanie listu songov
 * pomocou newInstance sa trieda môže vytvoriť z akýmkoľvek zoznamom songov
 *
 * @author Patrik Gazdík
 */
//class SongListFragment(p_database: ArrayList<Song> ) : Fragment() {
class SongListFragment() : Fragment() {
    private lateinit var binding: ActivitySonglistBinding
    private lateinit var recycler: RecyclerView
    lateinit var database: ArrayList<Song>

    /**
     * Pri metóde onCreate sa zoznam songov [database] prečíta z argumentov vložených cez newInstance()
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        database = arguments?.getSerializable("dataset") as ArrayList<Song>
    }

    /**
     * Inicializuje fragment
     */
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view: View = inflater.inflate(R.layout.fragment_song_list, container, false)

        val recyclerView = view.findViewById<RecyclerView>(R.id.song_list)
        recyclerView.setHasFixedSize(false);
        recyclerView.adapter = ItemAdapter(activity, database)
        recycler = recyclerView
        return view
    }

    /**
     * onResume znovu načíta RecyclerView ak by sa v ňom stali nejaké zmeny
     */
    override fun onResume() {
        super.onResume()
        reloadDataSet()
    }

    /**
     * notifikuje recyclerview adaptér o prípadných zmenách
     */
    fun reloadDataSet() {
        recycler.adapter?.notifyDataSetChanged()
    }

    /**
     * Companion objekt má metódu [newInstance]
     * ktorá vytvorí fragment [SongListFragment] so zoznamom songov
     */
    companion object {
        /**
         * parameter[dataset] zoznam songov vložený do fragmentu
         * vráti [fragment] inštancia SongListFragment s vloženým zoznamom songov
         */
        fun newInstance(dataset: ArrayList<Song>): Fragment {
            val fragment = SongListFragment()
            val args = Bundle()
            args.putSerializable("dataset", dataset)
            fragment.arguments = args
            return fragment
        }
    }



}