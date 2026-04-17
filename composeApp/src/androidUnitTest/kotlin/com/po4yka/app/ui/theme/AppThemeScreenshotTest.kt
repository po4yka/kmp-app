package com.po4yka.app.ui.theme

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.ui.Modifier
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onRoot
import androidx.compose.ui.unit.dp
import com.github.takahirom.roborazzi.RoborazziRule
import com.github.takahirom.roborazzi.captureRoboImage
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(qualifiers = "w360dp-h640dp-xxhdpi")
class AppThemeScreenshotTest {

    @get:Rule
    val composeRule = createComposeRule()

    @get:Rule
    val roborazziRule = RoborazziRule(
        options = RoborazziRule.Options(
            captureType = RoborazziRule.CaptureType.LastImage(),
        ),
    )

    @Test
    fun appTheme_lightMode_rendersIndustrialPalette() {
        composeRule.setContent {
            AppTheme(darkTheme = false) {
                Column(
                    Modifier
                        .background(MaterialTheme.colorScheme.background)
                        .padding(16.dp),
                ) {
                    Text(
                        "SESSION STATUS",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                    Text(
                        "36",
                        style = MaterialTheme.typography.displayLarge,
                        color = MaterialTheme.colorScheme.primary,
                    )
                }
            }
        }
        composeRule.onRoot().captureRoboImage()
    }

    @Test
    fun appTheme_darkMode_rendersIndustrialPalette() {
        composeRule.setContent {
            AppTheme(darkTheme = true) {
                Column(
                    Modifier
                        .background(MaterialTheme.colorScheme.background)
                        .padding(16.dp),
                ) {
                    Text(
                        "SESSION STATUS",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                    Text(
                        "36",
                        style = MaterialTheme.typography.displayLarge,
                        color = MaterialTheme.colorScheme.primary,
                    )
                }
            }
        }
        composeRule.onRoot().captureRoboImage()
    }
}
