import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberUpdatedState
import kotlinx.coroutines.delay
import org.jetbrains.compose.web.css.px
import org.jetbrains.compose.web.css.width
import org.jetbrains.compose.web.dom.AttrBuilderContext
import org.jetbrains.compose.web.dom.Div
import org.w3c.dom.HTMLParagraphElement

@Composable
fun Container(widthMultiplier: Double = 1.0, content: @Composable () -> Unit) {
    Div(attrs = {
        style {
            width((widthMultiplier * 930).px)
            property("margin", "0 auto")
        }
    }) {
        content()
    }
}

@Composable
fun Delay(timeMillis: Long, onTimeout: () -> Unit) {
    val currentOnTimeout by rememberUpdatedState(onTimeout)
    LaunchedEffect(true) {
        delay(timeMillis)
        currentOnTimeout()
    }
}

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
