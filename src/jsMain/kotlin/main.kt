import org.jetbrains.compose.web.css.Style
import org.jetbrains.compose.web.renderComposable
import ui.Site
import ui.SiteStylesheet

fun main() {
    renderComposable(rootElementId = "root") {
        Style(SiteStylesheet)
        Site()
    }
}
