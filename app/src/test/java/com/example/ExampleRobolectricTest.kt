package com.example

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import com.example.ui.viewmodel.ClinicalContentProvider
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [36])
class ExampleRobolectricTest {

    @Test
    fun `verify AbiBicara Pro app name and clinical protocols`() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val appName = context.getString(R.string.app_name)
        assertEquals("AbiBicara Pro", appName)
        assertEquals(4, ClinicalContentProvider.jawExerciseStages.size)
        assertTrue(ClinicalContentProvider.kakaktuaLyrics.isNotEmpty())
        assertEquals(3, ClinicalContentProvider.parentGoldenRules.size)
    }
}
