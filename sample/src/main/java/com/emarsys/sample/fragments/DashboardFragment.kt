package com.emarsys.sample.fragments

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.text.Editable
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.emarsys.Emarsys
import com.emarsys.sample.BuildConfig
import com.emarsys.sample.R
import com.emarsys.sample.SampleApplication
import com.emarsys.sample.extensions.showSnackBar
import com.emarsys.sample.prefs.Cache
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.tasks.Task
import kotlinx.android.synthetic.main.fragment_dashboard.*
import kotlin.system.exitProcess

class DashboardFragment : Fragment() {
    companion object {
        const val REQUEST_CODE_SIGN_IN = 12
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_dashboard, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        loggedInContact.text = Cache.contactFieldValue ?: getString(R.string.logged_in_anonymous)

        refreshConfig()

        buttonGoogleLogin.setOnClickListener {
            val client = generateGoogleSignInClient()
            val signInIntent: Intent = client.signInIntent
            activity?.startActivityForResult(signInIntent, REQUEST_CODE_SIGN_IN)
        }

        buttonLogin.setOnClickListener {
            val contactId = contactId.text.toString()
            val contactFieldId = contactFieldId.text.toString().toInt()
            Emarsys.setContact(contactFieldId, contactId) {
                handleLoginResult(it)
            }
        }

        buttonLogout.setOnClickListener {
            Emarsys.clearContact {
                if (it != null) {
                    Log.e(MobileEngageFragmentTracking.TAG, it.toString())
                } else {
                    val message = "Logout was successful."
                    Log.i(MobileEngageFragmentTracking.TAG, message)
                    view.showSnackBar(message)
                    Cache.contactFieldValue = getString(R.string.logged_in_anonymous)
                    loggedInContact.text = getString(R.string.logged_in_anonymous)
                }
            }
        }

        buttonSetupChange.setOnClickListener {
            if (forceChange.isChecked) {
                Cache.applicationCode = newApplicationCode.text?.toNullableString()
                Thread.sleep(200)
                exitProcess(0)
            }
            when {
                contactFieldId.text.isNullOrEmpty() -> {
                    Emarsys.config.changeApplicationCode(newApplicationCode.text?.toNullableString()) { throwable ->
                        onApplicationCodeChanged(throwable, view)
                    }
                }
                newApplicationCode.text.isNullOrEmpty() -> {
                    view.showSnackBar("ApplicationCode is needed!")
                }
                else -> {
                    Emarsys.config.changeApplicationCode(
                        newApplicationCode.text?.toNullableString()
                    ) { throwable ->
                        onApplicationCodeChanged(throwable, view)
                    }
                }
            }

            if (newMerchantId.text?.isNotEmpty() == true) {
                Emarsys.config.changeMerchantId(newMerchantId.text?.toNullableString())
                newMerchantId.text?.clear()
                Cache.merchantId = newMerchantId.text.toString()
                view.showSnackBar("MerchantId has been changed!")
                refreshConfig()
            }
        }
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        handleGoogleLogin(requestCode, resultCode, data)
    }

    private fun handleGoogleLogin(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == REQUEST_CODE_SIGN_IN && resultCode != 0) {
            val task: Task<GoogleSignInAccount> = GoogleSignIn.getSignedInAccountFromIntent(data)
            val contactFieldId = contactFieldId.text.toString().toInt()
            Emarsys.setAuthenticatedContact(contactFieldId, task.result.idToken!!) {
                handleLoginResult(it, task.result.email)
            }
            Log.i("GOOGLE_OAUTH_ID_TOKEN", task.result.idToken!!)
        }
    }

    private fun handleLoginResult(error: Throwable?, contactName: String? = null) {
        val contactId = contactName ?: contactId.text.toString()
        if (error != null) {
            Log.e(MobileEngageFragmentTracking.TAG, error.toString())
            view?.showSnackBar("Login: failed :(")
        } else {
            Log.i(MobileEngageFragmentTracking.TAG, "Login successful.")
            Cache.contactFieldValue = contactId
            loggedInContact.text = contactId
            view?.showSnackBar("Login: OK :)")
        }
    }

    private fun onApplicationCodeChanged(throwable: Throwable?, view: View) {
        if (throwable == null) {
            if (Cache.applicationCode == null
                && newApplicationCode?.text?.toNullableString() != null
            ) {
                (activity?.application as SampleApplication).setupEventHandlers()
            }
            Cache.applicationCode = newApplicationCode?.text?.toNullableString()
            Cache.contactFieldId = Emarsys.config.contactFieldId ?: 0
            view.showSnackBar("ApplicationCode has been changed!")
        } else {
            Cache.applicationCode = resources.getString(R.string.not_set)
            view.showSnackBar(
                throwable.message
                    ?: "Error during ApplicationCode change, Mobile Engage is disabled!"
            )
        }

        refreshConfig()
    }

    private fun refreshConfig() {
        currentApplicationCode?.text = resources.getString(
            R.string.current_application_code,
            if (Cache.applicationCode.isNullOrEmpty()) "not set" else Cache.applicationCode
        )
        currentMerchantId?.text = resources.getString(
            R.string.current_merchant_id,
            if (Cache.merchantId.isNullOrEmpty()) "not set" else Cache.merchantId
        )
        hardwareIdField?.text = Emarsys.config.hardwareId
        languageCodeField?.text = Emarsys.config.languageCode
        pushSettingsField?.text = resources.getString(
            R.string.config_information,
            Emarsys.config.notificationSettings.areNotificationsEnabled,
            Emarsys.config.notificationSettings.importance,
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) Emarsys.config.notificationSettings.channelSettings else "[not supported on this API level]"
        )
        newApplicationCode?.setText(Cache.applicationCode ?: "")
        contactFieldId?.setText(Cache.contactFieldId.toString() ?: "")
    }

    private fun Editable.toNullableString(): String? {
        return if (this.toString() == "") null else this.toString()
    }

    private fun generateGoogleSignInClient(): GoogleSignInClient {
        val options: GoogleSignInOptions =
            GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .requestIdToken(BuildConfig.GOOGLE_OAUTH_SERVER_CLIENT_ID)
                .build()
        return GoogleSignIn.getClient(requireActivity(), options)
    }

}
