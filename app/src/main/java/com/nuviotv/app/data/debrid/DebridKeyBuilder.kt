package com.nuviotv.app.data.debrid

/**
 * يبني رابط Torrentio مع مفاتيح Debrid
 * Torrentio URL format:
 * https://torrentio.strem.fun/{providers}/{rd|tb|pm}={key}/manifest.json
 */
object DebridKeyBuilder {

    private const val TORRENTIO_BASE = "https://torrentio.strem.fun"

    /**
     * يبني رابط Torrentio الأساسي مع المفاتيح (إن وجدت).
     * إذا لم توجد مفاتيح، يعيد Torrentio بدون مفاتيح (مصادر torrent عادية).
     */
    fun buildTorrentioUrl(
        realDebridKey: String,
        torboxKey: String,
        premiumizeKey: String
    ): String {
        val providers = listOf("yts", "eztv", "rarbg", "1337x", "thepiratebay", "kickasstorrents", "torrent9", "ilcorsaronero")
        val providerPath = providers.joinToString(",")

        val debridParts = mutableListOf<String>()
        if (realDebridKey.isNotBlank()) debridParts.add("realdebrid=$realDebridKey")
        if (torboxKey.isNotBlank()) debridParts.add("torbox=$torboxKey")
        if (premiumizeKey.isNotBlank()) debridParts.add("premiumize=$premiumizeKey")

        return if (debridParts.isEmpty()) {
            "$TORRENTIO_BASE/$providerPath/manifest.json"
        } else {
            "$TORRENTIO_BASE/$providerPath/${debridParts.joinToString("|")}/manifest.json"
        }
    }

    /**
     * رابط Cinemeta العام (بدون مفتاح).
     */
    const val CINEMETA_URL = "https://v3-cinemeta.strem.io"

    /**
     * رابط OpenSubtitles العام.
     */
    const val OPENSUBTITLES_URL = "https://opensubtitles-v3.strem.io"
}
