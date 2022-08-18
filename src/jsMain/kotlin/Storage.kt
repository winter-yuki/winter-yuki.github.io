object Storage {
    val content: Map<ContentId, ContentAccess> = data.associate { it.id to it.access }

    val substitution: Map<String, ContentId> = buildMap {
        data.forEach { info ->
            info.shortNames.forEach { name ->
                require(name !in this)
                put(name, info.id)
            }
        }
    }
}

private data class ContentInfo(
    val id: ContentId,
    val access: ContentAccess,
    val shortNames: List<String>,
)

private class ContentInfoBuilder {
    var access = ContentAccess.values().maxOf { it }
    var dir = emptyList<String>()
    var name: String? = null
    var date: String? = null
    var format: ContentFormat? = null
    var shortNames = mutableListOf<String>()

    fun build() = ContentInfo(
        ContentId(
            ContentDir(dir),
            ContentName(name!!),
            ContentDate(date),
            format!!
        ),
        access,
        shortNames
    )
}

private inline fun contentInfo(block: ContentInfoBuilder.() -> Unit): ContentInfo =
    ContentInfoBuilder().apply(block).build()

private val data = listOf(
    contentInfo {
        access = ContentAccess.Common
        name = "test1"
        date = "15.08.2022"
        format = ContentFormat.TXT
    },
    contentInfo {
        name = "test2"
        format = ContentFormat.TXT
    },
    contentInfo {
        access = ContentAccess.Common
        name = "test3"
        date = "15.08.2022"
        format = ContentFormat.TXT
    },
    contentInfo {
        access = ContentAccess.Extended
        name = "test2"
        format = ContentFormat.TXT
        shortNames.add("short")
    },
)
