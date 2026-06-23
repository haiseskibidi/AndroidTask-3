package androidx.test.espresso

object Espresso {
    @JvmStatic
    fun onIdle() {
        // no-op stub to prevent real Espresso from calling reflection-based InputManager APIs
    }

    @JvmStatic
    fun registerIdlingResources(vararg resources: IdlingResource): Boolean {
        return true
    }

    @JvmStatic
    fun unregisterIdlingResources(vararg resources: IdlingResource): Boolean {
        return true
    }

    @JvmStatic
    fun getIdlingResources(): List<IdlingResource> {
        return emptyList()
    }
}

interface EspressoException

class AppNotIdleException : RuntimeException(), EspressoException