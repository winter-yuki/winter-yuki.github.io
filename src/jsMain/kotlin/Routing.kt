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
        window.history.pushState(
            data = Unit,
            title = if (id == null) Const.TITLE else "${Const.TITLE}: ${id.name}",
            url = "${window.location.origin}/${id.toUrl()}".also {
                console.log("onNavigate: url: $it")
            }
        )
        route = id.toRoute().also {
            console.log("onNavigate: route: $it")
        }
    }

    private fun ContentId?.toUrl(): String = "#/" + (this?.path ?: "!${access.repr}")

    private fun ContentId?.toRoute(): Route =
        if (this == null) Route.Root
        else Route.Content(this)

    private fun route(): Route {
        val hash = window.location.hash
        console.log("route: hash = $hash")
        if (hash.isEmpty()) return Route.Root
        if (!hash.startsWith("#/")) return Route.Unknown
        val accessOrPath = hash.drop(2)
        if (accessOrPath.isEmpty()) return Route.Root
        if (accessOrPath.startsWith("!")) {
            val accessRaw = accessOrPath.drop(1)
            val accessRepr = accessRaw.takeWhile { it != '/' }
            console.log("route: access = $accessRepr")
            val tail = accessRaw.dropWhile { it != '/' }
            if (tail != "/" && tail.isNotEmpty()) return Route.Unknown
            access = ContentAccess.ofRepr(accessRepr) ?: return Route.Unknown
            return Route.Root
        }
        val id = ContentId.fromPath(accessOrPath)
        console.log("route: id = $id")
        if (id == null) return Route.Unknown
        if (id !in Storage.content) {
            console.log("route: unknown id")
            return Route.Unknown
        }
        return Route.Content(id)
    }
}
