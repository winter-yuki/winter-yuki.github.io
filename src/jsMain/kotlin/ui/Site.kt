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
import org.jetbrains.compose.web.css.Style
import org.jetbrains.compose.web.css.StyleScope
import org.jetbrains.compose.web.css.backgroundColor
import org.jetbrains.compose.web.css.backgroundImage
import org.jetbrains.compose.web.css.border
import org.jetbrains.compose.web.css.color
import org.jetbrains.compose.web.css.display
import org.jetbrains.compose.web.css.em
import org.jetbrains.compose.web.css.fontSize
import org.jetbrains.compose.web.css.fontWeight
import org.jetbrains.compose.web.css.height
import org.jetbrains.compose.web.css.marginLeft
import org.jetbrains.compose.web.css.marginRight
import org.jetbrains.compose.web.css.padding
import org.jetbrains.compose.web.css.paddingLeft
import org.jetbrains.compose.web.css.paddingTop
import org.jetbrains.compose.web.css.percent
import org.jetbrains.compose.web.css.pt
import org.jetbrains.compose.web.css.px
import org.jetbrains.compose.web.css.textAlign
import org.jetbrains.compose.web.css.textDecoration
import org.jetbrains.compose.web.css.vh
import org.jetbrains.compose.web.css.width
import org.jetbrains.compose.web.dom.A
import org.jetbrains.compose.web.dom.Article
import org.jetbrains.compose.web.dom.Br
import org.jetbrains.compose.web.dom.Div
import org.jetbrains.compose.web.dom.Footer
import org.jetbrains.compose.web.dom.H2
import org.jetbrains.compose.web.dom.Header
import org.jetbrains.compose.web.dom.Li
import org.jetbrains.compose.web.dom.P
import org.jetbrains.compose.web.dom.Section
import org.jetbrains.compose.web.dom.Span
import org.jetbrains.compose.web.dom.Text
import org.jetbrains.compose.web.dom.Ul
import org.jetbrains.compose.web.renderComposable

fun renderSite() {
    renderComposable(rootElementId = "root") {
        Style(SiteStylesheet)
        Site()
    }
}

class ContentState {
    var contentLoaded: Boolean by mutableStateOf(false)
}

@Composable
fun Site() {
    val routing = remember { Routing() }
    val contentState = remember { ContentState() }
    SiteHeader(routing)
    SiteBody(routing, contentState)
    SiteFooter(routing, contentState.contentLoaded)
}

@Composable
fun SiteHeader(routing: Routing) {
    Header(attrs = {
        style {
            backgroundImage("linear-gradient(${SiteStylesheet.primaryColor}, white)")
            if (routing.route.isRoot) {
                height(28.vh)
            } else {
                height(7.em)
            }
        }
    }) {
        Container {
            A(routing.url(null), attrs = {
                style {
                    if (routing.route.isRoot) {
                        paddingTop(0.8.em)
                        paddingLeft(0.4.em)
                        fontSize(5.em)
                    } else {
                        paddingTop(0.3.em)
                        fontSize(3.em)
                    }
                    marginLeft(1.em)
                    fontWeight("300")
                    color(Color("#565656"))
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
fun SiteFooter(routing: Routing, contentLoaded: Boolean) {
    Footer(attrs = {
        style {
            textAlign("center")
        }
    }) {
        Container {
            val ready = routing.route !is Route.Content || contentLoaded
            if (Const.SHOW_FOOTER_CONTENT && ready) {
                Ul(attrs = {
                    style {
                        padding(3.5.em)
                        display(DisplayStyle.InlineBlock)
                    }
                }) {
                    fun StyleScope.li() {
                        property("float", "left")
                        display(DisplayStyle.Block) // Remove bullets
                    }
                    Li(attrs = {
                        style {
                            li()
                            marginRight(1.em)
                        }
                    }) {
                        A("mailto:" + Const.EMAIL) {
                            Text("email")
                        }
                    }
                    Li(attrs = {
                        style {
                            li()
                        }
                    }) {
                        A(Const.GITHUB) {
                            Text("github")
                        }
                    }
                }
            } else {
                Div(attrs = {
                    style {
                        height(if (routing.route.isRoot) 5.em else 2.em)
                    }
                }) { }
            }
        }
    }
}

@Composable
fun SiteBody(routing: Routing, contentState: ContentState) {
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
            ContentView(info, contentState)
        }
    }
}

@Composable
fun ItemsView(routing: Routing, items: Iterable<ContentInfo>) {
    Section {
        Container {
            Div(attrs = {
                style {
                    width(35.em)
                    center()
                }
            }) {
                items.forEach { item ->
                    key(item) {
                        Item(item, routing)
                    }
                }
            }
        }
    }
}

@Composable
fun Item(info: ContentInfo, routing: Routing) {
    Div(attrs = {
        style {
            paddingTop(1.em)
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
            Text(info.status.toString())
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
                    center()
                }
            }) {
                Text(Const.NOT_FOUND)
            }
        }
    }
}

@Composable
fun ContentView(info: ContentInfo, contentState: ContentState) {
    val state = loadContent(info)
    when (val result = state.value) {
        is LoadResult.Loading -> Loading()
        is LoadResult.Fail -> FailedToLoad()
        is LoadResult.Success -> ContentView(info, result.content, contentState)
    }
}

@Composable
fun Loading() {
    Section {
        Container {
            DelayView(2000) {
                P(attrs = {
                    style {
                        padding(60.px)
                        fontSize(25.px)
                        center()
                    }
                }) {
                    Text(Const.LOADING)
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
                Text(Const.LOAD_FAILED)
            }
        }
    }
}

@Composable
fun ContentView(info: ContentInfo, content: Content, contentState: ContentState) {
    Article {
        Container {
            Div(attrs = {
                style {
                    if (info.contentWidth == null) {
                        width(100.percent)
                    } else {
                        width(info.contentWidth)
                    }
                    center()
                }
            }) {
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
                        P(attrs = {
                            if (info.centerPlain) {
                                style {
                                    widthContent()
                                    center()
                                }
                            }
                            rendered.run { attrs() }
                        }) {
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
                                js("hljs.highlightAll();")
                                contentState.contentLoaded = true
                                onDispose {
                                    it.innerHTML = ""
                                    contentState.contentLoaded = false
                                }
                            }
                        }) { }
                    }
                }
                if (info.source != null) {
                    Div(attrs = {
                        style {
                            paddingTop(1.em)
                            width(100.percent)
                            textAlign("right")
                        }
                    }) {
                        A(info.source.url.href) {
                            Text(Const.SOURCE)
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun Line(line: String) {
    Text(line)
    Br()
}
