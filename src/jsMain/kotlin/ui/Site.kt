package ui

import Const
import Registry
import Route
import Routing
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import content.Content
import content.ContentFormat
import content.ContentInfo
import org.jetbrains.compose.web.css.Color
import org.jetbrains.compose.web.css.DisplayStyle
import org.jetbrains.compose.web.css.backgroundColor
import org.jetbrains.compose.web.css.border
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
import org.jetbrains.compose.web.css.percent
import org.jetbrains.compose.web.css.pt
import org.jetbrains.compose.web.css.px
import org.jetbrains.compose.web.css.rgb
import org.jetbrains.compose.web.css.textAlign
import org.jetbrains.compose.web.css.textDecoration
import org.jetbrains.compose.web.css.width
import org.jetbrains.compose.web.dom.A
import org.jetbrains.compose.web.dom.Article
import org.jetbrains.compose.web.dom.Div
import org.jetbrains.compose.web.dom.Footer
import org.jetbrains.compose.web.dom.H2
import org.jetbrains.compose.web.dom.Header
import org.jetbrains.compose.web.dom.P
import org.jetbrains.compose.web.dom.Section
import org.jetbrains.compose.web.dom.Span
import org.jetbrains.compose.web.dom.Text

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
            A(routing.url(null), attrs = {
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
fun Main(routing: Routing) {
    when (val route = routing.route) {
        is Route.Root -> {
            val items = remember {
                Registry.content.values.filter { it.access <= routing.access }
            }
            ItemsView(routing, items)
        }
        is Route.Unknown -> NotFound()
        is Route.Content -> {
            val info = remember {
                Registry.content.getValue(route.id)
            }
            ContentView(info)
        }
    }
}

@Composable
fun ItemsView(routing: Routing, items: Iterable<ContentInfo>) {
    Section {
        Container {
            items.forEach { item ->
                key(item) {
                    Item(item, routing)
                }
            }
        }
    }
}

@Composable
fun Item(info: ContentInfo, routing: Routing) {
    Div(attrs = {
        style {
            paddingLeft(40.px)
            paddingTop(14.px)
            width(100.percent)
            textAlign("left")
            backgroundColor(Color.white)
            border {
                color(Color.white)
            }
        }
    }) {
        A(routing.url(info.id), attrs = {
            style {
                fontSize(2.em)
                display(DisplayStyle.Block)
                textDecoration("none")
                color(Color.black)
                fontWeight("330")
            }
            onClick { routing.onNavigate(info.id) }
        }) {
            Text(info.titleNotNull)
        }
        Span(attrs = {
            style {
                fontSize(1.4.em)
                color(Color.darkgray)
            }
        }) {
            Text(info.date.toString())
        }
    }
}

@Composable
fun NotFound() {
    Section {
        Container(widthMultiplier = 1.1) {
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
fun ContentView(info: ContentInfo) {
    val state = loadContent(info)
    when (val result = state.value) {
        is LoadResult.Loading -> Loading()
        is LoadResult.Fail -> FailedToLoad()
        is LoadResult.Success -> ContentView(info, result.content)
    }
}

@Composable
fun Loading() {
    Section {
        Container {
            var wait by remember { mutableStateOf(true) }
            if (wait) {
                Delay(2000) { wait = false }
            } else {
                P(attrs = {
                    style {
                        padding(60.px)
                        fontSize(25.px)
                    }
                }) {
                    Text("Loading...")
                }
            }
        }
    }
}

@Composable
fun FailedToLoad() {
    Section {
        Container {
            P(attrs = {
                style {
                    padding(60.px)
                    fontSize(25.px)
                }
            }) {
                Text("Failed to load content. Please contact with developer ${Const.EMAIL}")
            }
        }
    }
}

@Composable
fun ContentView(info: ContentInfo, content: Content) {
    Article {
        Container(widthMultiplier = info.contentWidthMultiplier) {
            if (info.hideTitle || info.format != ContentFormat.TXT) {
                Div(attrs = { style { height(3.em) } }) { }
            } else {
                H2(attrs = {
                    style {
                        paddingTop(1.5.em)
                        textAlign("center")
                        fontSize(17.pt)
                        fontWeight("normal")
                    }
                }) {
                    Text(info.titleNotNull)
                }
            }
            when (val rendered = render(info.id, content)) {
                is RenderingResult.Plain -> {
                    P(attrs = { rendered.run { attrs() } }) {
                        content.v.lines().forEach {
                            Line(it)
                        }
                    }
                }
                is RenderingResult.Rendered -> {
                    P(attrs = {
                        rendered.run { attrs() }
                        ref {
                            it.innerHTML = rendered.html
                            onDispose {
                                it.innerHTML = ""
                            }
                        }
                    }) { }
                }
            }
        }
    }
}

@Composable
fun Line(line: String) {
    Div(attrs = {
        style {
            fontSize(12.pt)
            padding(0.9.pt)
        }
    }) {
        Text(line)
    }
}
