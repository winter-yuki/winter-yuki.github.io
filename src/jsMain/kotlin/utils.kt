import androidx.compose.runtime.Composable
import org.jetbrains.compose.web.css.px
import org.jetbrains.compose.web.css.width
import org.jetbrains.compose.web.dom.Div

@Composable
fun Container(content: @Composable () -> Unit) {
    Div(attrs = {
        style {
            width(930.px)
            property("margin", "0 auto")
        }
    }) {
        content()
    }
}
