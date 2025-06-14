package com.cyberlabs.krishisetu

import android.app.Application
import android.util.Log
import com.amplifyframework.AmplifyException
import com.amplifyframework.api.aws.AWSApiPlugin
import com.amplifyframework.auth.cognito.AWSCognitoAuthPlugin
import com.amplifyframework.core.Amplify
import com.amplifyframework.storage.s3.AWSS3StoragePlugin
import dagger.hilt.android.HiltAndroidApp
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

@HiltAndroidApp
class MyApp : Application() {

    companion object {
        private val _amplifyReady = MutableStateFlow(false)
        val amplifyReady: StateFlow<Boolean> = _amplifyReady
    }

    override fun onCreate() {
        super.onCreate()
        try {
            Amplify.addPlugin(AWSCognitoAuthPlugin())
            Amplify.addPlugin(AWSApiPlugin())
            Amplify.addPlugin(AWSS3StoragePlugin())
            Amplify.configure(applicationContext)
            Log.i("KrishiSetuApp", "Amplify configured successfully")
            _amplifyReady.value = true
        } catch (e: AmplifyException) {
            Log.e("KrishiSetuApp", "Amplify configuration error", e)
        }
    }
}