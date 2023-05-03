package ui

import content.Content
import content.ContentFormat
import content.ContentId
import org.jetbrains.compose.web.css.fontSize
import org.jetbrains.compose.web.css.padding
import org.jetbrains.compose.web.css.pt
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
        ContentFormat.TXT -> RenderingResult.Plain {
            style {
                fontSize(12.pt)
                padding(0.9.pt)
            }
        }

        ContentFormat.MD -> {
            val parse = js("marked.parse") as (String) -> String
            RenderingResult.Rendered(parse(content.v)) { classes("markdown-body") }
        }

        ContentFormat.ADOC -> RenderingResult.Plain()
    }
