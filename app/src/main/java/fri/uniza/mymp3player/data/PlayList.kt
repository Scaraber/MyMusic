package fri.uniza.mymp3player.data

data class PlayList(var song_id: String, var type: Enum<Type>)

enum class Type {
        ALBUM, PLAYLIST
}
