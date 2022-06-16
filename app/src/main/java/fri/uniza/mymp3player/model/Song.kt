package fri.uniza.mymp3player.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.io.Serializable

/**
 * Dátová trieda ktorá drží všetky relevantné údaje o jednom songu
 *
 * @author Patrik Gazdík
 */
@Entity(tableName = "songs")
data class Song(
    @PrimaryKey
    var id: String,
    var title: String,
    var path: String,
    var artist: String = "Unknown",
    var album: String,
    var duration: Long,
    var isFavorite: Boolean = false,
    var currentTime: Long = 0,
    val date: String
) : Serializable {
  fun changeUnknown() {
      if (artist == "<unknown>") {
          artist = "Unknown"
      }
  }

}