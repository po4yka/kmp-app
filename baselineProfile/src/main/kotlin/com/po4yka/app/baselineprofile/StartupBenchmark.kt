package com.po4yka.app.baselineprofile

import androidx.benchmark.macro.CompilationMode
import androidx.benchmark.macro.StartupMode
import androidx.benchmark.macro.StartupTimingMetric
import androidx.benchmark.macro.junit4.MacrobenchmarkRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

/**
 * Cold-start Macrobenchmark for `:androidApp`.
 *
 * [startupCompilationNone] measures startup with no AOT compilation (worst case);
 * [startupCompilationPartial] measures startup with the Baseline Profile applied.
 * The delta between the two quantifies the profile's benefit — see the
 * cold-start budget rows in `docs/performance.md`.
 *
 * Run with `./gradlew :baselineProfile:connectedBenchmarkAndroidTest` (connected
 * device) or via the `pixel6Api30` Gradle Managed Device.
 */
@RunWith(AndroidJUnit4::class)
class StartupBenchmark {

    @get:Rule
    val rule = MacrobenchmarkRule()

    @Test
    fun startupCompilationNone() = startup(CompilationMode.None())

    @Test
    fun startupCompilationPartial() = startup(CompilationMode.Partial())

    private fun startup(compilationMode: CompilationMode) {
        rule.measureRepeated(
            packageName = APP_PACKAGE_NAME,
            metrics = listOf(StartupTimingMetric()),
            compilationMode = compilationMode,
            startupMode = StartupMode.COLD,
            iterations = STARTUP_ITERATIONS,
            setupBlock = {
                pressHome()
            },
        ) {
            startActivityAndWait()
        }
    }

    private companion object {
        const val STARTUP_ITERATIONS = 10
    }
}
