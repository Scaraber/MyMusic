package fri.uniza.mymp3player.adapter

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.Lifecycle
import androidx.viewpager2.adapter.FragmentStateAdapter
import fri.uniza.mymp3player.activity.MainActivity
import fri.uniza.mymp3player.fragment.SongListFragment

/**
 * Trieda na zobrazovanie fragmentov na viewpageri
 *
 * @author Patrik Gazdík
 */
class PagerAdapter(fragmentManager: FragmentManager, lifecycle: Lifecycle,
                   var songlist: Fragment, var favorites: Fragment) : FragmentStateAdapter(fragmentManager, lifecycle) {

    /**
     * vráti počet strán
     */
    override fun getItemCount(): Int {
        return 2
    }

    /**
     * vráti fragmenty vytvorené v MainActivity podľa strany na ktorej používateľ je
     */
    override fun createFragment(position: Int): Fragment {
        return when (position) {
            0 -> songlist
            1 -> favorites
            //2 -> playlists
            else -> songlist
        }
    }


}