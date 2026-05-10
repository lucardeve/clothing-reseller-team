package com.cegep.reseller.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import com.cegep.reseller.R

@Composable
fun LoginScreen(state: ResellerUiState, onLogin: (String, String) -> Unit, onRegister: () -> Unit) {
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }

    AuthForm(R.string.title_login) {
        Text(stringResource(R.string.title_login_subtitle), color = MaterialTheme.colorScheme.onSurfaceVariant)
        AppTextField(email, { email = it }, R.string.label_email)
        AppTextField(password, { password = it }, R.string.label_password, visualTransformation = PasswordVisualTransformation())
        ErrorMessage(state.error)
        Button(onClick = { onLogin(email, password) }) { Text(stringResource(R.string.action_login)) }
        TextButton(onClick = onRegister) { Text(stringResource(R.string.prompt_no_account)) }
        Text(stringResource(R.string.demo_hint), style = MaterialTheme.typography.bodySmall)
    }
}

@Composable
fun RegisterScreen(
    state: ResellerUiState,
    onRegister: (String, String, String) -> Unit,
    onLogin: () -> Unit
) {
    var username by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }

    AuthForm(R.string.title_register) {
        AppTextField(username, { username = it }, R.string.label_username)
        AppTextField(email, { email = it }, R.string.label_email)
        AppTextField(password, { password = it }, R.string.label_password, visualTransformation = PasswordVisualTransformation())
        ErrorMessage(state.error)
        Button(onClick = { onRegister(username, email, password) }) { Text(stringResource(R.string.action_register)) }
        TextButton(onClick = onLogin) { Text(stringResource(R.string.prompt_have_account)) }
    }
}

@Composable
private fun AuthForm(titleId: Int, content: @Composable ColumnScope.() -> Unit) {
    Column(
        Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(24.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        Text(stringResource(titleId), style = MaterialTheme.typography.headlineLarge)
        content()
    }
}
