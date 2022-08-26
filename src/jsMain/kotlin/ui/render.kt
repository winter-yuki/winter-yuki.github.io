package ui

import content.Content
import content.ContentFormat
import content.ContentId
import org.jetbrains.compose.web.dom.AttrBuilderContext
import org.w3c.dom.HTMLParagraphElement

sealed interface RenderingResult {
    val attrs: AttrBuilderContext<HTMLParagraphElement>

    data class Plain(override val attrs: AttrBuilderContext<HTMLParagraphElement> = {}) : RenderingResult

    data class Rendered(
        val html: String,
        override val attrs: AttrBuilderContext<HTMLParagraphElement>
    ) : RenderingResult
}

fun render(id: ContentId, content: Content): RenderingResult =
    when (id.format) {
        ContentFormat.TXT -> RenderingResult.Plain()
        ContentFormat.MD -> {
            val parse = js("marked.parse") as (String) -> String
            RenderingResult.Rendered(parse(content.v), attrs = { classes("markdown-body") })
        }
        ContentFormat.ADOC -> RenderingResult.Plain()
    }
