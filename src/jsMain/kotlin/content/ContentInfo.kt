package content

data class ContentInfo(
    val dir: ContentDir,
    val name: ContentName,
    val format: ContentFormat,
    val title: ContentTitle?,
    val status: ContentUpdateStatus,
    val access: ContentAccess,
    val location: ContentLocation?,
    val permanentShortNames: List<String>,
    val shortNames: List<String>,
    val hideTitle: Boolean,
    val contentWidthMultiplier: Double,
) {
    val id = ContentId(dir, name, format)
    val titleNotNull = title?.v ?: id.name.v
    val url: String = location?.url?.toString() ?: id.path
}

class ContentInfoBuilder {
    var title: String? = null
    var access = ContentAccess.values().maxOf { it }
    var dir = emptyList<String>()
    var name: String? = null
    var status: ContentUpdateStatus = ContentUpdateStatus.None
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
        status = status,
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
