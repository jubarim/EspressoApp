package org.juba.espressoapp.ui.auth

import android.app.Activity
import androidx.activity.compose.LocalActivity
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import org.juba.espressoapp.R
import org.juba.espressoapp.ui.theme.EspressoAppTheme

@Composable
fun AuthScreen(
    onAuthSuccess: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: AuthViewModel = hiltViewModel(),
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val activity = LocalActivity.current as Activity
    val serverClientId = stringResource(R.string.default_web_client_id)

    LaunchedEffect(Unit) {
        viewModel.events.collect { event ->
            when (event) {
                AuthEvent.NavigateToMain -> onAuthSuccess()
            }
        }
    }

    AuthContent(
        uiState = uiState,
        onGoogleSignIn = { viewModel.signInWithGoogle(activity, serverClientId) },
        onContinueAsGuest = viewModel::continueAsGuest,
        modifier = modifier,
    )
}

@Composable
private fun AuthContent(
    uiState: AuthUiState,
    onGoogleSignIn: () -> Unit,
    onContinueAsGuest: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val isLoading = uiState is AuthUiState.Loading

    Scaffold(modifier = modifier) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 32.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Text(
                text = stringResource(R.string.auth_title),
                style = MaterialTheme.typography.displaySmall,
            )

            Spacer(Modifier.height(8.dp))

            Text(
                text = stringResource(R.string.auth_subtitle),
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = TextAlign.Center,
            )

            Spacer(Modifier.height(32.dp))

            if (isLoading) {
                CircularProgressIndicator()
            } else {
                Button(
                    onClick = onGoogleSignIn,
                    modifier = Modifier.fillMaxWidth(),
                ) {
                    Text(stringResource(R.string.auth_google_sign_in_button))
                }

                Spacer(Modifier.height(8.dp))

                TextButton(onClick = onContinueAsGuest) {
                    Text(stringResource(R.string.auth_continue_as_guest))
                }

                if (uiState is AuthUiState.Error) {
                    Spacer(Modifier.height(12.dp))
                    Text(
                        text = stringResource(uiState.messageRes),
                        color = MaterialTheme.colorScheme.error,
                        style = MaterialTheme.typography.bodySmall,
                        textAlign = TextAlign.Center,
                    )
                }
            }
        }
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
private fun AuthIdlePreview() {
    EspressoAppTheme {
        AuthContent(
            uiState = AuthUiState.Idle,
            onGoogleSignIn = {},
            onContinueAsGuest = {},
        )
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
private fun AuthLoadingPreview() {
    EspressoAppTheme {
        AuthContent(
            uiState = AuthUiState.Loading,
            onGoogleSignIn = {},
            onContinueAsGuest = {},
        )
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
private fun AuthErrorPreview() {
    EspressoAppTheme {
        AuthContent(
            uiState = AuthUiState.Error(R.string.auth_error_no_account),
            onGoogleSignIn = {},
            onContinueAsGuest = {},
        )
    }
}
