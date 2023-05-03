package content

import org.jetbrains.compose.web.css.CSSSizeValue
import org.jetbrains.compose.web.css.CSSUnit

data class ContentInfo(
    val dir: ContentDir,
    val name: ContentName,
    val format: ContentFormat,
    val title: ContentTitle?,
    val status: ContentUpdateStatus,
    val access: ContentAccess,
    val location: ContentLocation?,
    val source: ContentSource?,
    val permanentShortNames: List<String>,
    val shortNames: List<String>,
    val hideTitle: Boolean,
    val contentWidth: CSSSizeValue<CSSUnit.em>?,
    val centerPlain: Boolean,
    val accessPromoter: Boolean,
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
    var source: String? = null
    val permanentShortNames = mutableListOf<String>() // Do not remove permanent names!
    val shortNames = mutableListOf<String>()
    var hideTitle: Boolean = false
    var contentWidth: CSSSizeValue<CSSUnit.em>? = null
    var centerPlain = true
    var accessPromoter: Boolean = false

    fun build() = ContentInfo(
        dir = ContentDir(dir),
        name = ContentName(name!!),
        format = format!!,
        title = title?.let { ContentTitle(it) },
        status = status,
        location = location?.let { ContentLocation(it) },
        source = source?.let { ContentSource(it) },
        access = access,
        permanentShortNames = permanentShortNames,
        shortNames = shortNames,
        hideTitle = hideTitle,
        contentWidth = contentWidth,
        centerPlain = centerPlain,
        accessPromoter = accessPromoter
    )
}

inline fun contentInfo(block: ContentInfoBuilder.() -> Unit): ContentInfo =
    ContentInfoBuilder().apply(block).build()
