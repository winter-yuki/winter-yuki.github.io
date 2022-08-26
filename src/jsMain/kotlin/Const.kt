import content.ContentAccess
import content.ContentFormat
import content.ContentId
import content.ContentInfo
import content.contentInfo

object Const {
    const val TITLE = "Words"
    const val EMAIL = "andrey.stoyan.csam@gmail.com"
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
    },
    contentInfo {
        title = "Kotlin"
        access = ContentAccess.Common
        name = "ReadMe"
        format = ContentFormat.MD
        location = "https://raw.githubusercontent.com/winter-yuki/kotlin/master"
    },
)

private val prodData = listOf(
    contentInfo {
        title = "Материалы по CS"
        access = ContentAccess.Common
        name = "cs-edu-materials"
        format = ContentFormat.MD
    },
    contentInfo {
        title = "Материалы по soft skills"
        access = ContentAccess.Extended
        name = "soft-materials"
        format = ContentFormat.MD
    },
)
