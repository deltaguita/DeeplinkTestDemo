package com.example.deeplink

import android.app.Activity
import android.app.Instrumentation
import android.content.Intent
import android.net.Uri
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import org.junit.Assert

import org.junit.Test
import org.junit.runner.RunWith


@RunWith(AndroidJUnit4::class)
class DeeplinkInstrumentedTest {

    private val githubLink = "https://github.com"
    private val githubExploreLink = "https://github.com/explore"

    @Test
    fun testGitHubLink() {
        checkLinkCanBeResolvedByICookApp(githubLink)
    }

    @Test
    fun testGitHubExploreUrlLink() {
        checkLinkCanBeResolvedByICookApp(githubExploreLink)
    }


    private fun checkLinkCanBeResolvedByICookApp(it: String) {
        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(it))
        intent.setPackage(BuildConfig.APPLICATION_ID)
        if (intent.resolveActivity(InstrumentationRegistry
                .getInstrumentation().context.packageManager) == null) {
            Assert.fail("fail:$it should be resolved")
        }

    }

    @Test
    fun testGitHubNotSupportLink() {
        checkLinkCanNotBeResolvedByICookApp(githubLink+"/not")
    }

    @Test
    fun testGitHubNotSupportLink2() {
        checkLinkCanNotBeResolvedByICookApp(githubLink+"/not/")
    }


    private fun checkLinkCanNotBeResolvedByICookApp(it: String) {
        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(it))
        intent.setPackage(BuildConfig.APPLICATION_ID)
        if (intent.resolveActivity(InstrumentationRegistry.getInstrumentation().context.packageManager) != null
        ) {
            Assert.fail("fail:$it should not be resolved")
        }
    }

    @Test
    fun testGitHubLinkTarget(){
        testDeeplinkTarget(githubLink,MainActivity::class.java)
    }

    @Test
    fun testGitHubExploreUrlLinkTarget(){
        testDeeplinkTarget(githubExploreLink,ExploreActivity::class.java)
    }


    private fun testDeeplinkTarget(urlString: String, targetClazz: Class<*>) {
        val instrumentation = InstrumentationRegistry.getInstrumentation()
        val monitor: Instrumentation.ActivityMonitor =
            instrumentation.addMonitor(targetClazz.name, null, false)
        instrumentation.addMonitor(monitor)

        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(urlString))
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        intent.setPackage(BuildConfig.APPLICATION_ID)
        instrumentation.startActivitySync(intent)

        val currentActivity: Activity = instrumentation.waitForMonitorWithTimeout(monitor, 1000)
        Assert.assertNotNull(currentActivity)

        //Check extra data if you need
        //Assert.assertNotNull(currentActivity.intent.data)

        instrumentation.removeMonitor(monitor)


    }
}