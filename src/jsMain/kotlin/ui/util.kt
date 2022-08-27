package ui

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.State
import androidx.compose.runtime.getValue
import androidx.compose.runtime.produceState
import androidx.compose.runtime.rememberUpdatedState
import content.Content
import content.ContentInfo
import content.load
import kotlinx.coroutines.delay
import org.jetbrains.compose.web.css.em
import org.jetbrains.compose.web.css.width
import org.jetbrains.compose.web.dom.Div

@Composable
fun Container(widthMultiplier: Double = 1.0, content: @Composable () -> Unit) {
    Div(attrs = {
        style {
            width((widthMultiplier * 50).em)
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

sealed interface LoadResult {
    object Loading : LoadResult
    object Fail : LoadResult
    data class Success(val content: Content) : LoadResult
}

@Composable
fun loadContent(info: ContentInfo): State<LoadResult> =
    produceState<LoadResult>(initialValue = LoadResult.Loading, info) {
        val content = Content.load(info)
        value = if (content != null) {
            LoadResult.Success(content)
        } else {
            LoadResult.Fail
        }
    }
