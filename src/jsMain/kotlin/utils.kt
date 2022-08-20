import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberUpdatedState
import kotlinx.coroutines.delay
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

@Composable
fun Delay(timeMillis: Long, onTimeout: () -> Unit) {
    val currentOnTimeout by rememberUpdatedState(onTimeout)
    LaunchedEffect(true) {
        delay(timeMillis)
        currentOnTimeout()
    }
}
