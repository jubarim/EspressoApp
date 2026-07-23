package org.juba.espressoapp.data.repository

import android.util.Log
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.functions.FirebaseFunctions
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await
import org.juba.espressoapp.domain.model.AuthUser
import org.juba.espressoapp.domain.repository.AuthRepository
import org.json.JSONArray
import org.json.JSONObject
import javax.inject.Inject

private const val TAG = "jm_PasskeyAuth"
private const val FN_NAME = "ext-firebase-web-authn-api"

class AuthRepositoryImpl @Inject constructor(
    private val firebaseAuth: FirebaseAuth,
    private val functions: FirebaseFunctions,
) : AuthRepository {

    override suspend fun prepareRegistration(displayName: String): Result<String> = runCatching {
        Log.d(TAG, "prepareRegistration: signing in anonymously (displayName=$displayName)")
        firebaseAuth.signInAnonymously().await()
        
        Log.d(TAG, "prepareRegistration: anonymous sign-in OK, uid=${firebaseAuth.currentUser?.uid}")

        Log.d(TAG, "prepareRegistration: calling create registration challenge")
        val result = functions.getHttpsCallable(FN_NAME).call(
            mapOf(
                "operation" to "create registration challenge",
                "name" to displayName,
                "registeringCredential" to "first",
            ),
        ).await()

        val data = result.data.asMap()
        Log.d(TAG, "prepareRegistration: response success=${data["success"]} code=${data["code"]}")
        check(data["success"] == true) { "${data["code"]}: ${data["message"]}" }

        val challengeJson = JSONObject(data["creationOptions"].asMap()).toString()
        Log.d(TAG, "prepareRegistration: challenge received (${challengeJson.length} chars)")
        challengeJson
    }.also { it.onFailure { e -> Log.e(TAG, "prepareRegistration: FAILED", e) } }

    override suspend fun completeRegistration(registrationResponseJson: String): Result<Unit> = runCatching {
        Log.d(TAG, "completeRegistration: sending signed credential to server")
        val result = functions.getHttpsCallable(FN_NAME).call(
            mapOf(
                "operation" to "verify registration",
                "registrationResponse" to registrationResponseJson.toNestedMap(),
            ),
        ).await()

        val data = result.data.asMap()
        Log.d(TAG, "completeRegistration: response success=${data["success"]} code=${data["code"]}")
        check(data["success"] == true) { "${data["code"]}: ${data["message"]}" }

        val customToken = data["customToken"] as String
        Log.d(TAG, "completeRegistration: verification OK, signing in with custom token")
        firebaseAuth.signInWithCustomToken(customToken).await()
        Log.d(TAG, "completeRegistration: signed in, uid=${firebaseAuth.currentUser?.uid}")
        Unit
    }.also { it.onFailure { e -> Log.e(TAG, "completeRegistration: FAILED", e) } }

    override suspend fun getLoginChallenge(displayName: String): Result<String> = runCatching {
        Log.d(TAG, "getLoginChallenge: signing in anonymously")
        firebaseAuth.signInAnonymously().await()
        Log.d(TAG, "getLoginChallenge: calling create authentication challenge")
        val result = functions.getHttpsCallable(FN_NAME).call(
            mapOf(
                "operation" to "create authentication challenge",
                "authenticatingCredential" to "first",
            ),
        ).await()

        val data = result.data.asMap()
        Log.d(TAG, "getLoginChallenge: response success=${data["success"]} code=${data["code"]}")
        check(data["success"] == true) { "${data["code"]}: ${data["message"]}" }

        val challengeJson = JSONObject(data["requestOptions"].asMap().filterValues { it != null }).toString()
        Log.d(TAG, "getLoginChallenge: challenge received (${challengeJson.length} chars)")
        challengeJson
    }.also { it.onFailure { e -> Log.e(TAG, "getLoginChallenge: FAILED", e) } }

    override suspend fun completeLogin(authResponseJson: String): Result<Unit> = runCatching {
        Log.d(TAG, "completeLogin: sending signed assertion to server")
        val result = functions.getHttpsCallable(FN_NAME).call(
            mapOf(
                "operation" to "verify authentication",
                "authenticationResponse" to authResponseJson.toNestedMap(),
            ),
        ).await()

        val data = result.data.asMap()
        Log.d(TAG, "completeLogin: response success=${data["success"]} code=${data["code"]}")
        check(data["success"] == true) { "${data["code"]}: ${data["message"]}" }

        val customToken = data["customToken"] as String
        Log.d(TAG, "completeLogin: custom token received, signing in with Firebase")
        firebaseAuth.signInWithCustomToken(customToken).await()
        Log.d(TAG, "completeLogin: signed in, uid=${firebaseAuth.currentUser?.uid}")
        Unit
    }.also { it.onFailure { e -> Log.e(TAG, "completeLogin: FAILED", e) } }

    override suspend fun signOut() {
        Log.d(TAG, "signOut: clearing Firebase session")
        firebaseAuth.signOut()
        Log.d(TAG, "signOut: done")
    }

    override fun currentUser(): Flow<AuthUser?> = callbackFlow {
        val listener = FirebaseAuth.AuthStateListener { auth ->
            val user = auth.currentUser
            Log.d(TAG, "authStateChanged: user=${user?.uid ?: "null"}")
            trySend(user?.let { AuthUser(uid = it.uid, displayName = it.displayName) })
        }
        firebaseAuth.addAuthStateListener(listener)
        awaitClose { firebaseAuth.removeAuthStateListener(listener) }
    }
}

@Suppress("UNCHECKED_CAST")
private fun Any?.asMap(): Map<String, Any?> = this as Map<String, Any?>

private fun String.toNestedMap(): Map<String, Any?> = JSONObject(this).toNestedMap()

private fun JSONObject.toNestedMap(): Map<String, Any?> =
    keys().asSequence().associateWith { key ->
        when (val v = get(key)) {
            is JSONObject -> v.toNestedMap()
            is JSONArray -> v.toNestedList()
            JSONObject.NULL -> null
            else -> v
        }
    }

private fun JSONArray.toNestedList(): List<Any?> =
    (0 until length()).map { i ->
        when (val v = get(i)) {
            is JSONObject -> v.toNestedMap()
            is JSONArray -> v.toNestedList()
            JSONObject.NULL -> null
            else -> v
        }
    }
