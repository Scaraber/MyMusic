package fri.uniza.mymp3player.data

import fri.uniza.mymp3player.model.Song

/**
 * dátova trieda ktorá uchováva zoznam songov
 * je vytvorená kvôli jednoduchšej serializácii
 */
data class SongList(val songs: ArrayList<Song>)