package com.example.baselineprofile

import android.util.Log
import androidx.benchmark.macro.BaselineProfileMode
import androidx.benchmark.macro.CompilationMode
import androidx.benchmark.macro.ExperimentalMetricApi
import androidx.benchmark.macro.StartupMode
import androidx.benchmark.macro.StartupTimingMetric
import androidx.benchmark.macro.junit4.MacrobenchmarkRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.LargeTest
import androidx.test.platform.app.InstrumentationRegistry
import androidx.test.uiautomator.textAsString
import androidx.test.uiautomator.uiAutomator
import androidx.test.uiautomator.watcher.PermissionDialog
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import kotlin.system.measureTimeMillis

/**
 * This test class benchmarks the speed of app startup.
 * Run this benchmark to verify how effective a Baseline Profile is.
 * It does this by comparing [CompilationMode.None], which represents the app with no Baseline
 * Profiles optimizations, and [CompilationMode.Partial], which uses Baseline Profiles.
 *
 * Run this benchmark to see startup measurements and captured system traces for verifying
 * the effectiveness of your Baseline Profiles. You can run it directly from Android
 * Studio as an instrumentation test, or run all benchmarks for a variant, for example benchmarkRelease,
 * with this Gradle task:
 * ```
 * ./gradlew :baselineprofile:connectedBenchmarkReleaseAndroidTest
 * ```
 *
 * You should run the benchmarks on a physical device, not an Android emulator, because the
 * emulator doesn't represent real world performance and shares system resources with its host.
 *
 * For more information, see the [Macrobenchmark documentation](https://d.android.com/macrobenchmark#create-macrobenchmark)
 * and the [instrumentation arguments documentation](https://d.android.com/topic/performance/benchmarking/macrobenchmark-instrumentation-args).
 **/
@RunWith(AndroidJUnit4::class)
@LargeTest
class StartupBenchmarks {

    @get:Rule
    val rule = MacrobenchmarkRule()

    @Test
    fun startupCompilationNone() =
        benchmark(CompilationMode.None())

    @Test
    fun startupCompilationBaselineProfiles() =
        benchmark(CompilationMode.Partial(BaselineProfileMode.Require))

    @OptIn(ExperimentalMetricApi::class)
    private fun benchmark(compilationMode: CompilationMode) {
        // The application id for the running build variant is read from the instrumentation arguments.
        rule.measureRepeated(
            packageName = InstrumentationRegistry.getArguments().getString("targetAppId")
                ?: throw Exception("targetAppId not passed as instrumentation runner arg"),
            metrics = listOf(StartupTimingMetric()),
            compilationMode = compilationMode,
            startupMode = StartupMode.COLD,
            iterations = 10,
            setupBlock = {
                pressHome()
            },
            measureBlock = {
                uiAutomator {
                    startApp(packageName)
                    waitForAppToBeVisible(packageName)

                    val auth = onElements(0) { className == "android.widget.EditText" }
                    if(auth.isNotEmpty()) {
                        val args = InstrumentationRegistry.getArguments()
                        val id = args.getString("usaint_id") ?: ""
                        val pw = args.getString("usaint_pw") ?: ""

                        onElement { viewIdResourceName == "LOGIN_ID" }.text = id
                        onElement { viewIdResourceName == "LOGIN_PW" }.text = pw
                        onElement { textAsString() == "로그인" }.click()

                        watchFor(PermissionDialog) {
                            clickAllow()
                        }
                    }
                    val elapsed = measureTimeMillis {
                        onElement(30000) { textAsString() == "평균학점" }.click()
                        onElement(60000) { textAsString() == "22년 2학기" }.click()
                        pressBack()
                        onElement(30000) { textAsString() == "채플" }.click()
                        onElement(60000) { textAsString() == "22년 2학기" }.click()
                    }
                    println("[Bench Common] ${elapsed}ms 걸렸습니다")
                    Log.i("[Bench Common]", "${elapsed}ms 걸렸습니다")
                }
            }
        )
    }
}