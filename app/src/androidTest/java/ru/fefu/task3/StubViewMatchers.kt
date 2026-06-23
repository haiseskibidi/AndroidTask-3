package androidx.test.espresso.matcher

import android.view.View
import org.hamcrest.BaseMatcher
import org.hamcrest.Description
import org.hamcrest.Matcher

object ViewMatchers {
    @JvmStatic
    fun isDisplayed(): Matcher<View> {
        return object : BaseMatcher<View>() {
            override fun matches(item: Any?): Boolean {
                return true
            }

            override fun describeTo(description: Description?) {
                description?.appendText("is displayed stub")
            }
        }
    }
}
