object Storage {
    val content: Map<ContentId, ContentInfo> = data.associateBy { it.id }

    val substitution: Map<String, ContentId> = buildMap {
        data.forEach { info ->
            info.permanentShortNames.forEach { name ->
                require(name !in this)
                put(name, info.id)
            }
            info.shortNames.forEach { name ->
                require(name !in this)
                put(name, info.id)
            }
        }
    }
}

value class ContentTitle(val v: String) {
    init {
        require(v.isNotBlank())
    }
}

value class ContentDate(val v: String?) {
    init {
        require(v == null || v.isNotBlank())
    }

    override fun toString(): String = v.orEmpty()
}

val ContentDate.isEmpty: Boolean
    get() = v == null

val ContentDate.isNotEmpty: Boolean
    get() = !isEmpty

data class ContentInfo(
    val title: ContentTitle?,
    val date: ContentDate,
    val id: ContentId,
    val access: ContentAccess,
    val permanentShortNames: List<String>,
    val shortNames: List<String>,
    val hideTitle: Boolean,
    val contentWidthMultiplier: Double,
) {
    val titleNotNull: String
        get() = title?.v ?: id.name.v
}

private class ContentInfoBuilder {
    var title: String? = null
    var access = ContentAccess.values().maxOf { it }
    var dir = emptyList<String>()
    var name: String? = null
    var date: String? = null
    var format: ContentFormat? = null
    val permanentShortNames = mutableListOf<String>() // Do not remove permanent names!
    val shortNames = mutableListOf<String>()
    var hideTitle: Boolean = false
    var contentWidthMultiplier: Double = 1.0

    fun build() = ContentInfo(
        title?.let { ContentTitle(it) },
        ContentDate(date),
        ContentId(
            ContentDir(dir),
            ContentName(name!!),
            format!!
        ),
        access,
        permanentShortNames = permanentShortNames,
        shortNames = shortNames,
        hideTitle = hideTitle,
        contentWidthMultiplier = contentWidthMultiplier
    )
}

private inline fun contentInfo(block: ContentInfoBuilder.() -> Unit): ContentInfo =
    ContentInfoBuilder().apply(block).build()

// Assign production data instead of test one
private val data by lazy { testData }

private val testData = listOf(
    contentInfo {
        access = ContentAccess.Common
        name = "test1"
        date = "15.08.2022"
        format = ContentFormat.TXT
        hideTitle = true
    },
    contentInfo {
        name = "test2"
        format = ContentFormat.TXT
    },
    contentInfo {
        title = "Very interesting article"
        access = ContentAccess.Common
        name = "test3"
        date = "15.08.2022"
        format = ContentFormat.MD
    },
    contentInfo {
        access = ContentAccess.Extended
        name = "test2"
        format = ContentFormat.TXT
        permanentShortNames.add("short")
    },
    contentInfo {
        title = "Failed to load data"
        access = ContentAccess.Common
        name = "eesdfyguhijklkjb"
        format = ContentFormat.TXT
    }
)
