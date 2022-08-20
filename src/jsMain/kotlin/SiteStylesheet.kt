import org.jetbrains.compose.web.css.StyleSheet
import org.jetbrains.compose.web.css.fontSize
import org.jetbrains.compose.web.css.padding
import org.jetbrains.compose.web.css.px

object SiteStylesheet : StyleSheet() {
    val errorText by style {
        padding(60.px)
        fontSize(30.px)
    }
}
