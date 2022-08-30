package ui

import org.jetbrains.compose.web.css.Color
import org.jetbrains.compose.web.css.StyleSheet
import org.jetbrains.compose.web.css.color
import org.jetbrains.compose.web.css.fontFamily

object SiteStylesheet : StyleSheet() {
    const val primaryColor = "#FB77FF"

    init {
        "a" style {
            color(Color.black)
        }
        "div" style {
            fontFamily("segoe-ui")
        }
    }
}
