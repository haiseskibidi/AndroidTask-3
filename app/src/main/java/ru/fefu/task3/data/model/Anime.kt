package ru.fefu.task3.data.model

interface AnimeModel {
    val id: Long
    val name: String
    val russian: String?
    val image: AnimeImage?
    val score: String?
    val kind: String?
    val status: String?

    fun getImageUrl(): String? {
        val original = image?.original ?: return null
        return if (original.startsWith("/")) {
            "https://shikimori.one$original"
        } else {
            original
        }
    }
    fun getDisplayName(): String = russian?.takeIf { it.isNotBlank() } ?: name
}

data class AnimeBase(
    override val id: Long,
    override val name: String,
    override val russian: String?,
    override val image: AnimeImage?,
    override val score: String?,
    override val kind: String?,
    override val status: String?
) : AnimeModel

data class AnimeDetails(
    override val id: Long,
    override val name: String,
    override val russian: String?,
    override val image: AnimeImage?,
    override val score: String?,
    override val kind: String?,
    override val status: String?,
    val description: String?,
    val descriptionHtml: String?,
    val episodes: Int?,
    val airedOn: String?,
    val genres: List<Genre>?
) : AnimeModel {
    fun getCleanDescription(): String? {
        val raw = description ?: descriptionHtml?.replace(Regex("<.*?>"), "")
        return raw?.replace(Regex("""\[[^\]]*\]"""), "") // удаляем любые теги в квадратных скобках
            ?.replace("[", "") // убираем возможные одиночные скобки
            ?.replace("]", "")
            ?.trim()
    }
}

data class AnimeImage(
    val original: String?,
    val preview: String?,
    val x96: String?,
    val x48: String?
)

data class Genre(
    val id: Long,
    val name: String,
    val russian: String?
)