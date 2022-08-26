package content

import org.w3c.dom.url.URL

value class ContentTitle(val v: String) {
    init {
        require(v.isNotBlank())
    }

    override fun toString(): String = v
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

enum class ContentAccess(val repr: String) {
    Common(""),
    Draft("draft"),
    Extended("ext"),
    LinkAccess("nowaytoaccessthisthing42");

    companion object {
        fun ofRepr(repr: String): ContentAccess? = values().find { it.repr == repr }
    }
}

value class ContentLocation(val url: URL) {
    constructor(s: String) : this(URL(s))

    override fun toString(): String = url.toString()
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

enum class ContentFormat(val repr: String) {
    TXT("txt"), MD("md"), ADOC("adoc");

    val extension get() = ".$repr"

    companion object {
        fun ofRepr(repr: String): ContentFormat? = values().find { it.repr == repr }
    }
}
