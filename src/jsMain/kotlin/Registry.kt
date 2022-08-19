object Storage {
    val content: Map<ContentId, ContentInfo> = data.associate { it.id to it }

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

data class ContentInfo(
    val id: ContentId,
    val access: ContentAccess,
    val permanentShortNames: List<String>,
    val shortNames: List<String>,
    val hideTitle: Boolean,
)

private class ContentInfoBuilder {
    var access = ContentAccess.values().maxOf { it }
    var dir = emptyList<String>()
    var name: String? = null
    var date: String? = null
    var format: ContentFormat? = null
    val permanentShortNames = mutableListOf<String>() // Do not remove permanent names!
    val shortNames = mutableListOf<String>()
    var hideTitle: Boolean = false

    fun build() = ContentInfo(
        ContentId(
            ContentDir(dir),
            ContentName(name!!),
            ContentDate(date),
            format!!
        ),
        access,
        permanentShortNames = permanentShortNames,
        shortNames = shortNames,
        hideTitle = hideTitle
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
)
