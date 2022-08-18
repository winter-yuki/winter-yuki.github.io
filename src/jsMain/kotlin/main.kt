import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.runtime.key
import androidx.compose.runtime.produceState
import androidx.compose.runtime.remember
import org.jetbrains.compose.web.css.Color
import org.jetbrains.compose.web.css.DisplayStyle
import org.jetbrains.compose.web.css.Style
import org.jetbrains.compose.web.css.backgroundColor
import org.jetbrains.compose.web.css.color
import org.jetbrains.compose.web.css.display
import org.jetbrains.compose.web.css.em
import org.jetbrains.compose.web.css.fontSize
import org.jetbrains.compose.web.css.fontWeight
import org.jetbrains.compose.web.css.height
import org.jetbrains.compose.web.css.marginLeft
import org.jetbrains.compose.web.css.padding
import org.jetbrains.compose.web.css.paddingLeft
import org.jetbrains.compose.web.css.paddingTop
import org.jetbrains.compose.web.css.pt
import org.jetbrains.compose.web.css.px
import org.jetbrains.compose.web.css.rgb
import org.jetbrains.compose.web.css.textAlign
import org.jetbrains.compose.web.css.textDecoration
import org.jetbrains.compose.web.dom.A
import org.jetbrains.compose.web.dom.Article
import org.jetbrains.compose.web.dom.Div
import org.jetbrains.compose.web.dom.Footer
import org.jetbrains.compose.web.dom.H2
import org.jetbrains.compose.web.dom.Header
import org.jetbrains.compose.web.dom.P
import org.jetbrains.compose.web.dom.Section
import org.jetbrains.compose.web.dom.Text
import org.jetbrains.compose.web.renderComposable

fun main() {
    renderComposable(rootElementId = "root") {
        Style(SiteStylesheet)
        Site()
    }
}

@Composable
fun Site() {
    val routing = remember { Routing() }
    Head(routing)
    Main(routing)
    Foot()
}

@Composable
fun Head(routing: Routing) {
    Header(attrs = {
        style {
            backgroundColor(rgb(251, 127, 220))
            if (routing.route.isRoot) {
                height(15.em)
            } else {
                height(7.em)
            }
        }
    }) {
        Container {
            A(routing.rootUrl, attrs = {
                style {
                    if (routing.route.isRoot) {
                        paddingTop(1.em)
                        fontSize(5.em)
                    } else {
                        paddingTop(0.5.em)
                        fontSize(3.em)
                    }
                    marginLeft(1.em)
                    fontWeight("bold")
                    color(Color.white)
                    textDecoration("none")
                    display(DisplayStyle.Block)
                }
                onClick {
                    routing.onNavigate(null)
                }
            }) {
                Text(Const.TITLE)
            }
        }
    }
}

@Composable
fun Main(routing: Routing) {
    Section {
        Container {
            when (val route = routing.route) {
                is Route.Root -> {
                    val items = remember {
                        Storage.content.filter { it.value <= routing.access }.keys
                    }
                    Items(items, routing)
                }
                is Route.Unknown -> NotFound()
                is Route.Content -> {
                    val state = loadContent(route.id)
                    when (val result = state.value) {
                        is LoadResult.Loading -> Loading()
                        is LoadResult.Success -> ContentView(route.id, result.content)
                    }
                }
            }
        }
    }
}

sealed interface LoadResult {
    object Loading : LoadResult
    data class Success(val content: Content) : LoadResult
}

@Composable
fun loadContent(id: ContentId): State<LoadResult> =
    produceState<LoadResult>(initialValue = LoadResult.Loading, id) {
        val content = Content.load(id)
        value = LoadResult.Success(content)
    }

@Composable
fun Loading() {
    P(attrs = {
        style {
            padding(60.px)
            fontSize(30.px)
        }
    }) {
        Text("Loading...")
    }
}

@Composable
fun Foot() {
    Footer(attrs = {
        style {
            height(100.px)
        }
    }) {
        Text("")
    }
}

@Composable
fun Items(ids: Iterable<ContentId>, routing: Routing) {
    ids.forEach { id ->
        key(id) {
            Item(id, routing)
        }
    }
}

@Composable
fun Item(id: ContentId, routing: Routing) {
    Div(attrs = {
        style {
            paddingLeft(40.px)
            paddingTop(20.px)
        }
        onClick { routing.onNavigate(id) }
    }) {
        A(attrs = {
            style {
                textDecoration("none")
                fontSize(30.px)
            }
        }) {
            Text(id.name.toString())
        }
        Div(attrs = {
            style {
                color(Color.darkgray)
                fontSize(25.px)
            }
        }) {
            Text(id.date.toString())
        }
    }
}

@Composable
fun NotFound() {
    Section {
        Container {
            P(attrs = {
                style {
                    textAlign("center")
                    fontSize(40.px)
                    padding(60.px)
                    attr("margin", "0 auto")
                }
            }) {
                Text("Страница не найдена")
            }
        }
    }
}

@Composable
fun ContentView(id: ContentId, content: Content) {
    Article {
        Container {
            H2(attrs = {
                style {
                    paddingTop(1.5.em)
                    textAlign("center")
                    fontSize(20.pt)
                    fontWeight("normal")
                }
            }) {
                Text(id.name.v)
            }
            P {
                content.v.lines().forEach {
                    Line(it)
                }
            }
        }
    }
}

@Composable
fun Line(line: String) {
    Div(attrs = {
        style {
            fontSize(14.pt)
            padding(0.9.pt)
        }
    }) {
        Text(line)
    }
}
