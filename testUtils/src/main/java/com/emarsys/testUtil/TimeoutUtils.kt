package com.emarsys.testUtil

import android.support.test.rule.DisableOnAndroidDebug

import org.junit.rules.Timeout

object TimeoutUtils {

    @JvmStatic
    val timeoutRule
        get () = DisableOnAndroidDebug(Timeout.seconds(30))

}