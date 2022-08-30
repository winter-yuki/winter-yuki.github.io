import content.ContentAccess
import content.ContentFormat
import content.ContentId
import content.ContentInfo
import content.ContentUpdateStatus
import content.contentInfo

object Const {
    const val TITLE = "Words"
    const val EMAIL = "andrey.stoyan.csam@gmail.com"
    const val GITHUB = "https://github.com/winter-yuki"
    const val NOT_FOUND = "Page not found"
    const val LOADING = "Loading..."
    const val LOAD_FAILED = "Failed to load content. Please contact with developer $EMAIL"
    const val SHOW_FOOTER_CONTENT = true
    const val STATUS_UPDATING = "Regularly updated"
    const val STATUS_DRAFT = "Draft"
    const val SOURCE = "source"
}

object Registry {
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

// Assign production data instead of test one
private val data by lazy { prodData }

private val testData = listOf(
    contentInfo {
        access = ContentAccess.Common
        name = "test1"
        status = ContentUpdateStatus.Done("15.08.2022")
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
        status = ContentUpdateStatus.Draft
        format = ContentFormat.MD
    },
    contentInfo {
        access = ContentAccess.Extended
        name = "test2"
        format = ContentFormat.TXT
        permanentShortNames.add("short")
        status = ContentUpdateStatus.Draft
        accessPromoter = true
    },
    contentInfo {
        title = "Failed to load data"
        access = ContentAccess.Common
        name = "sxdcfvgbh"
        format = ContentFormat.TXT
    },
    contentInfo {
        title = "Failed to load data"
        access = ContentAccess.Common
        name = "gfcvcjhsbckjaf"
        format = ContentFormat.TXT
    },
    contentInfo {
        title = "Failed to load data"
        access = ContentAccess.Common
        name = "kqkqkq"
        format = ContentFormat.TXT
    },
    contentInfo {
        title = "Failed to load data"
        access = ContentAccess.Common
        name = "gfcvcjhsbckjaf"
        format = ContentFormat.TXT
    },
    contentInfo {
        title = "Failed to load data"
        access = ContentAccess.Common
        name = "kqkqkq1"
        format = ContentFormat.TXT
    },
    contentInfo {
        title = "Kotlin"
        access = ContentAccess.Common
        name = "kotlin"
        format = ContentFormat.MD
        location = "https://raw.githubusercontent.com/JetBrains/kotlin/master/ReadMe.md"
        status = ContentUpdateStatus.Continuous
        source = "https://github.com/JetBrains/kotlin"
    },
)

private val prodData = listOf(
    contentInfo {
        title = "Материалы по computer science"
        access = ContentAccess.Common
        name = "cs-edu-materials"
        format = ContentFormat.MD
        status = ContentUpdateStatus.Continuous
    },
    contentInfo {
        title = "Материалы по soft skills"
        access = ContentAccess.Extended
        name = "soft-materials"
        format = ContentFormat.MD
        status = ContentUpdateStatus.Continuous
    },
    contentInfo {
        title = "Self-types design for Kotlin language"
        access = ContentAccess.Common
        name = "kotlin-self-types"
        format = ContentFormat.MD
        location = "https://raw.githubusercontent.com/winter-yuki/kotlin-self-types/main/README.md"
        status = ContentUpdateStatus.Draft
        source = "https://github.com/winter-yuki/kotlin-self-types"
    },
).asReversed()
