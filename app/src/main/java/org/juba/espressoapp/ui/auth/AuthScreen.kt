package org.juba.espressoapp.ui.auth

import android.app.Activity
import androidx.activity.compose.LocalActivity
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import org.juba.espressoapp.R
import org.juba.espressoapp.designsystem.EspressoTextField
import org.juba.espressoapp.ui.theme.EspressoAppTheme

@Composable
fun AuthScreen(
    onAuthSuccess: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: AuthViewModel = hiltViewModel(),
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val activity = LocalActivity.current as Activity

    LaunchedEffect(Unit) {
        viewModel.events.collect { event ->
            when (event) {
                AuthEvent.NavigateToMain -> onAuthSuccess()
            }
        }
    }

    AuthScreenContent(
        uiState = uiState,
        onRegister = { displayName -> viewModel.register(displayName, activity) },
        onSignIn = { displayName -> viewModel.signIn(displayName, activity) },
        modifier = modifier,
    )
}

@Composable
private fun AuthScreenContent(
    uiState: AuthUiState,
    onRegister: (String) -> Unit,
    onSignIn: (String) -> Unit,
    modifier: Modifier = Modifier,
) {
    var displayName by rememberSaveable { mutableStateOf("") }
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

            EspressoTextField(
                value = displayName,
                onValueChange = { displayName = it },
                label = stringResource(R.string.auth_display_name_label),
                enabled = !isLoading,
                errorMessage = (uiState as? AuthUiState.Error)?.message?.takeIf { displayName.isBlank() },
            )

            Spacer(Modifier.height(24.dp))

            if (isLoading) {
                CircularProgressIndicator()
            } else {
                Button(
                    onClick = { onSignIn(displayName) },
                    modifier = Modifier.padding(bottom = 8.dp),
                ) {
                    Text(stringResource(R.string.auth_sign_in_button))
                }

                OutlinedButton(onClick = { onRegister(displayName) }) {
                    Text(stringResource(R.string.auth_register_button))
                }

                if (uiState is AuthUiState.Error && displayName.isNotBlank()) {
                    Spacer(Modifier.height(12.dp))
                    Text(
                        text = uiState.message,
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
private fun AuthScreenIdlePreview() {
    EspressoAppTheme {
        AuthScreenContent(
            uiState = AuthUiState.Idle,
            onRegister = {},
            onSignIn = {},
        )
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
private fun AuthScreenErrorPreview() {
    EspressoAppTheme {
        AuthScreenContent(
            uiState = AuthUiState.Error("Sign-in cancelled"),
            onRegister = {},
            onSignIn = {},
        )
    }
}
