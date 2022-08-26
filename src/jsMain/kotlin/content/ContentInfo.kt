package content

data class ContentInfo(
    val dir: ContentDir,
    val name: ContentName,
    val format: ContentFormat,
    val title: ContentTitle?,
    val date: ContentDate,
    val access: ContentAccess,
    val location: ContentLocation?,
    val permanentShortNames: List<String>,
    val shortNames: List<String>,
    val hideTitle: Boolean,
    val contentWidthMultiplier: Double,
) {
    val id = ContentId(dir, name, format)
    val titleNotNull = title?.v ?: id.name.v

    val url: String = buildString {
        if (location != null) {
            val url = location.url.toString()
            append(url)
            if (!url.endsWith('/')) {
                append('/')
            }
        }
        append(id.path)
    }
}

class ContentInfoBuilder {
    var title: String? = null
    var access = ContentAccess.values().maxOf { it }
    var dir = emptyList<String>()
    var name: String? = null
    var date: String? = null
    var format: ContentFormat? = null
    var location: String? = null
    val permanentShortNames = mutableListOf<String>() // Do not remove permanent names!
    val shortNames = mutableListOf<String>()
    var hideTitle: Boolean = false
    var contentWidthMultiplier: Double = 1.0

    fun build() = ContentInfo(
        dir = ContentDir(dir),
        name = ContentName(name!!),
        format = format!!,
        title = title?.let { ContentTitle(it) },
        date = ContentDate(date),
        location = location?.let { ContentLocation(it) },
        access = access,
        permanentShortNames = permanentShortNames,
        shortNames = shortNames,
        hideTitle = hideTitle,
        contentWidthMultiplier = contentWidthMultiplier
    )
}

inline fun contentInfo(block: ContentInfoBuilder.() -> Unit): ContentInfo =
    ContentInfoBuilder().apply(block).build()
