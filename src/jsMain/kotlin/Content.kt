import kotlinx.browser.window
import kotlinx.coroutines.CompletableDeferred
import kotlin.js.Promise

enum class ContentAccess(val repr: String) {
    Common(""), Extended("ext"), LinkAccess("nowaytoaccessthisthing42");

    companion object {
        fun ofRepr(repr: String): ContentAccess? = values().find { it.repr == repr }
    }
}

value class ContentDir(val elements: List<String>) {
    init {
        require("" !in elements)
    }

    override fun toString(): String = elements.joinToString("/")

    companion object {
        fun of(s: String) = ContentDir(s.split('/').filterNot { it.isEmpty() })
    }
}

fun ContentDir.isEmpty(): Boolean = elements.isEmpty()

fun ContentDir.isNotEmpty(): Boolean = !isEmpty()

value class ContentName(val v: String) {
    init {
        require(v.isNotBlank())
    }

    override fun toString(): String = v
}

value class ContentDate(val v: String?) {
    init {
        require(v == null || v.isNotBlank())
    }

    val isEmpty: Boolean
        get() = v == null

    override fun toString(): String = v.orEmpty()
}

val ContentDate.isNotEmpty: Boolean
    get() = !isEmpty

enum class ContentFormat(val repr: String) {
    TXT("txt"), MD("md"), ADOC("adoc");

    val extension get() = ".$repr"

    companion object {
        fun ofRepr(repr: String): ContentFormat? = values().find { it.repr == repr }
    }
}

/**
 * Identifies content on server and on page as [path].
 */
data class ContentId(val dir: ContentDir, val name: ContentName, val date: ContentDate, val format: ContentFormat) {
    val path by lazy {
        buildString {
            if (dir.isNotEmpty()) {
                append(dir)
                append('/')
            }
            append(name)
            if (date.isNotEmpty) {
                append('|')
                append(date)
                append('|')
            }
            append(format.extension)
        }
    }

    companion object {
        // Example: dir1/dir2/name|day.month.year|.txt
        private val regex = """(?:([\w\s/]+)/)?([\w\s\-]+)(?:\|([.\w]+)\|)?\.(\w+)""".toRegex()
        //                     dir             name       date             ext

        fun fromPath(path: String): ContentId? {
            val groups = regex.matchEntire(path)?.groups ?: return null
            val dir = ContentDir.of(groups[1]?.value.orEmpty())
            val name = groups[2]?.let { ContentName(it.value) } ?: return null
            val date = ContentDate(groups[3]?.value)
            val ext = groups[4]?.let { ContentFormat.ofRepr(it.value) } ?: return null
            return ContentId(dir, name, date, ext)
        }
    }
}

value class Content(val v: String) {
    override fun toString(): String = v

    companion object {
        suspend fun load(id: ContentId): Content? {
            val completable = CompletableDeferred<Content?>()
            window.fetch(id.path).then {
                if (it.ok) it.text() else Promise<String?> { resolve, _ -> resolve(null) }
            }.then {
                val content = it?.let { Content(it) }
                completable.complete(content)
            }.catch {
                completable.completeExceptionally(it)
            }
            return completable.await()
        }
    }
}
