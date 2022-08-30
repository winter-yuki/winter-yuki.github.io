package content

/**
 * Identifies content on page and on server [path].
 */
data class ContentId(
    val dir: ContentDir,
    val name: ContentName,
    val format: ContentFormat,
) {
    val path by lazy {
        buildString {
            if (dir.isNotEmpty()) {
                append(dir)
                append('/')
            }
            append(name)
            append(format.extension)
        }
    }

    companion object {
        // Example: dir1/dir2/name|day.month.year|.txt
        private val regex = """(?:([\w\s/]+)/)?([\w\s\-а-яА-Я]+)?\.(\w+)""".toRegex()
        //                     dir             name       ext

        fun fromPath(path: String): ContentId? {
            val groups = regex.matchEntire(path)?.groups ?: return null
            val dir = ContentDir.of(groups[1]?.value.orEmpty())
            val name = groups[2]?.let { ContentName(it.value) } ?: return null
            val ext = groups[3]?.let { ContentFormat.ofRepr(it.value) } ?: return null
            return ContentId(dir, name, ext)
        }
    }
}
