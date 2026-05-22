package com.po4yka.app.baselineprofile

import androidx.benchmark.macro.junit4.BaselineProfileRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.uiautomator.By
import androidx.test.uiautomator.Direction
import androidx.test.uiautomator.Until
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

/**
 * Generates a Baseline Profile for `:androidApp` by exercising the critical
 * startup journey: cold start to the Home screen, then a light scroll.
 *
 * Run with `./gradlew :androidApp:generateBaselineProfile` — the generated
 * `baseline-prof.txt` lands in `androidApp/src/release/generated/baselineProfiles/`.
 *
 * Requires a connected device / emulator OR the `pixel6Api30` Gradle Managed
 * Device declared in this module's `build.gradle.kts`.
 */
@RunWith(AndroidJUnit4::class)
class BaselineProfileGenerator {

    @get:Rule
    val rule = BaselineProfileRule()

    @Test
    fun generate() {
        rule.collect(packageName = APP_PACKAGE_NAME) {
            pressHome()
            startActivityAndWait()

            // Defensive light interaction: scroll the Home list if a scrollable
            // surface is present. The journey still produces a useful profile
            // for cold start even when no scrollable view is found.
            device.wait(Until.hasObject(By.scrollable(true)), UI_WAIT_TIMEOUT_MS)
            val scrollable = device.findObject(By.scrollable(true))
            if (scrollable != null) {
                scrollable.setGestureMargin(device.displayWidth / GESTURE_MARGIN_DIVISOR)
                scrollable.fling(Direction.DOWN)
                scrollable.fling(Direction.UP)
            }
        }
    }
}
