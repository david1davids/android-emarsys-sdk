package com.emarsys.predict


import com.emarsys.di.FakeDependencyContainer
import com.emarsys.di.emarsys
import com.emarsys.di.setupEmarsysComponent
import com.emarsys.di.tearDownEmarsysComponent
import com.emarsys.testUtil.TimeoutUtils
import com.nhaarman.mockitokotlin2.mock
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.rules.TestRule
import org.mockito.Mockito

class PredictRestrictedTest {

    private lateinit var mockPredictInternal: PredictInternal
    private lateinit var predictRestricted: PredictRestricted

    @Rule
    @JvmField
    val timeout: TestRule = TimeoutUtils.timeoutRule

    @Before
    fun setUp() {
        mockPredictInternal = mock()

        val dependencyContainer = FakeDependencyContainer(predictInternal = mockPredictInternal)

        setupEmarsysComponent(dependencyContainer)
        predictRestricted = PredictRestricted()
    }

    @After
    fun tearDown() {
        try {
            emarsys().coreSdkHandler.looper.quit()
            tearDownEmarsysComponent()
        } catch (e: Exception) {
            e.printStackTrace()
            throw e
        }
    }

    @Test
    fun testPredict_setContact_delegatesTo_Predict_Internal() {
        predictRestricted.setContact("contactId")
        Mockito.verify(mockPredictInternal).setContact("contactId")
    }

    @Test
    fun testPredict_clearContact_delegatesTo_Predict_Internal() {
        predictRestricted.clearContact()
        Mockito.verify(mockPredictInternal).clearContact()
    }
}