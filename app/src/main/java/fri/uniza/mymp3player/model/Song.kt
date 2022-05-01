package fri.uniza.mymp3player.model
//TODO zmen z data class na normalnu
data class Song(
    var id: String,
    var title: String,
    var path: String,
    var artist: String = "Unknown",
    var album: String,
    var duration: Long
) {
  fun changeUnknown() {
      if (artist == "<unknown>") {
          artist = "Unknown"
      }
  }

}