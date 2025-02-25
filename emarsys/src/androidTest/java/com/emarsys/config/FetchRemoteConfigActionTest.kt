package com.emarsys.config

import com.emarsys.core.api.result.CompletionListener
import com.emarsys.fake.FakeEmarsysDependencyContainer
import com.emarsys.mobileengage.di.setupMobileEngageComponent
import com.emarsys.mobileengage.di.tearDownMobileEngageComponent
import com.emarsys.testUtil.TimeoutUtils
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.rules.TestRule
import org.mockito.Mockito.verify
import org.mockito.kotlin.mock
import org.mockito.kotlin.timeout

class FetchRemoteConfigActionTest {

    private lateinit var fetchAction: FetchRemoteConfigAction
    private lateinit var mockConfigInternal: ConfigInternal
    private lateinit var mockCompletionListener: CompletionListener

    @Rule
    @JvmField
    val timeout: TestRule = TimeoutUtils.timeoutRule

    @Before
    fun setup() {
        mockConfigInternal = mock()
        mockCompletionListener = mock()

        setupMobileEngageComponent(FakeEmarsysDependencyContainer())

        fetchAction = FetchRemoteConfigAction(mockConfigInternal, mockCompletionListener)
    }

    @After
    fun tearDown() {
        tearDownMobileEngageComponent()
    }

    @Test
    fun testExecute_invokesConfigInternalsRefreshRemoteConfigMethod() {
        fetchAction.execute(null)
        verify(mockConfigInternal, timeout(100)).refreshRemoteConfig(mockCompletionListener)
    }

}