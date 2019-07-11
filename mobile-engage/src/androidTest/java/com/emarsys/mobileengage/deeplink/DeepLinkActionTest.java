package com.emarsys.mobileengage.deeplink;

import android.app.Activity;
import android.content.Intent;

import com.emarsys.testUtil.TimeoutUtils;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TestRule;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyZeroInteractions;
import static org.mockito.Mockito.when;

public class DeepLinkActionTest {

    static {
        mock(Intent.class);
        mock(Activity.class);
    }

    private DeepLinkInternal deepLinkInternal;
    private DeepLinkAction action;

    @Rule
    public TestRule timeout = TimeoutUtils.getTimeoutRule();

    @Before
    public void setUp() {
        deepLinkInternal = mock(DeepLinkInternal.class);
        action = new DeepLinkAction(deepLinkInternal);

    }

    @Test(expected = IllegalArgumentException.class)
    public void testConstructor_mobileEngageInternalMustNotBeNull() {
        new DeepLinkAction(null);
    }

    @Test
    public void testExecute_callsMobileEngageInternal() {
        Intent intent = mock(Intent.class);
        Activity activity = mock(Activity.class);
        when(activity.getIntent()).thenReturn(intent);

        action.execute(activity);

        verify(deepLinkInternal).trackDeepLinkOpen(activity, intent, null);
    }

    @Test
    public void testExecute_neverCallsMobileEngageInternal_whenIntentFromActivityIsNull() {
        Activity activity = mock(Activity.class);

        action.execute(activity);

        verifyZeroInteractions(deepLinkInternal);
    }

    @Test
    public void testExecute_neverCallsMobileEngageInternal_whenActivityIsNull() {
        action.execute(null);

        verifyZeroInteractions(deepLinkInternal);
    }

}