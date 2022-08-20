import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import kotlinx.browser.window

sealed interface Route {
    val isRoot get() = this is Root

    object Root : Route {
        override fun toString(): String = "Root"
    }

    object Unknown : Route {
        override fun toString(): String = "Unknown"
    }

    data class Content(val id: ContentId) : Route
}

class Routing {
    var access: ContentAccess by mutableStateOf(ContentAccess.Common)
        private set

    var route: Route by mutableStateOf(route())
        private set

    val rootUrl: String
        get() = null.toUrl()

    init {
        window.onpopstate = {
            route = route().also {
                console.log("onpopstate: route = $route, newRoute = $it")
            }
            Unit.asDynamic()
        }
    }

    fun onNavigate(id: ContentId?) {
        val newRoute = id.toRoute().also {
            console.log("onNavigate: route: $it")
        }
        if (route != newRoute) {
            route = newRoute
            window.history.pushState(
                data = Unit,
                title = Const.TITLE,
                url = "${window.location.origin}/${id.toUrl()}".also {
                    console.log("onNavigate: url: $it")
                }
            )
        }
    }

    private fun ContentId?.toUrl(): String =
        // Prefix is always needed to not reload page
        PREFIX + (this?.path ?: if (access.repr.isEmpty()) "" else "!${access.repr}")

    private fun ContentId?.toRoute(): Route =
        if (this == null) Route.Root
        else Route.Content(this)

    private fun route(): Route {
        fun <T> T.log(msg: (T) -> String): T = also { console.log("route: ${msg(it)}") }
        val hash = window.location.hash.log { "hash = $it" }
        if (hash.isEmpty()) return Route.Root
        if (!hash.startsWith(PREFIX)) return Route.Unknown.log { "wrong prefix" }
        val accessOrPath = hash.drop(PREFIX.length)
        if (accessOrPath.isEmpty()) return Route.Root
        when {
            accessOrPath.startsWith("!") -> {
                val accessWithTail = accessOrPath.drop(1) // remove '!'
                val accessRepr = accessWithTail.takeWhile { it != '/' }.log { "accessRepr = $it" }
                val tail = accessWithTail.dropWhile { it != '/' }
                if (tail != "/" && tail.isNotEmpty()) {
                    return Route.Unknown.log { "non empty tail = $tail" }
                }
                access = ContentAccess.ofRepr(accessRepr) ?: return Route.Unknown.log { "wrong repr" }
                return Route.Root
            }
            else -> {
                val path = accessOrPath
                val id = ContentId.fromPath(path).log { "id = $it" }
                if (id != null && id in Storage.content) return Route.Content(id)
                log { "not found in content" }
                val afterSubstitution = Storage.substitution[path] ?: return Route.Unknown.log { "no substitution" }
                return Route.Content(afterSubstitution)
            }
        }
    }

    companion object {
        private const val PREFIX = "#/"
    }
}
