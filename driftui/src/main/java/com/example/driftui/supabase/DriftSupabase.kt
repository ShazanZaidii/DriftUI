package com.example.driftui.supabase

import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.createSupabaseClient
import io.github.jan.supabase.SupabaseClientBuilder
import android.content.Context
import androidx.credentials.CredentialManager
import androidx.credentials.GetCredentialRequest
import com.google.android.libraries.identity.googleid.GetGoogleIdOption
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential
import io.github.jan.supabase.auth.auth
import io.github.jan.supabase.auth.providers.Google
import io.github.jan.supabase.auth.providers.builtin.IDToken

import io.github.jan.supabase.auth.user.UserInfo



//Creating the client:
fun createDriftSupabaseClient(
    supabaseUrl: String,
    supabaseKey: String,
    builder: SupabaseClientBuilder.() -> Unit
): SupabaseClient {

    return createSupabaseClient(
        supabaseUrl = supabaseUrl,
        supabaseKey = supabaseKey,
        builder = builder
    )
}


sealed class DriftAuthResult {
    data class Success(val user: UserInfo) : DriftAuthResult()
    data class Failure(val error: Throwable) : DriftAuthResult()
}


suspend fun SupabaseClient.signInWithGoogle(
    context: Context,
    webClientId: String
): DriftAuthResult {

    return try {

        val credentialManager = CredentialManager.create(context)

        val googleIdOption = GetGoogleIdOption.Builder()
            .setFilterByAuthorizedAccounts(false)
            .setServerClientId(webClientId)
            .setAutoSelectEnabled(false)
            .build()

        val request = GetCredentialRequest.Builder()
            .addCredentialOption(googleIdOption)
            .build()

        val result = credentialManager.getCredential(context, request)

        val googleIdToken = GoogleIdTokenCredential
            .createFrom(result.credential.data)
            .idToken

        this.auth.signInWith(IDToken) {
            idToken = googleIdToken
            provider = Google
        }

        val user = this.auth.currentUserOrNull()
            ?: throw IllegalStateException("User is null after login")

        DriftAuthResult.Success(user)

    } catch (e: Exception) {
        DriftAuthResult.Failure(e)
    }
}