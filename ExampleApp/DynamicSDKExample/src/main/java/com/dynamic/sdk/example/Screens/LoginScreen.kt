package com.dynamic.sdk.example.Screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.dynamic.sdk.example.Components.ErrorMessageView
import com.dynamic.sdk.example.Components.OtpVerificationSheet
import com.dynamic.sdk.example.Components.PrimaryButton

@Composable
fun LoginScreen(
    onNavigateToHome: () -> Unit
) {
    val viewModel: LoginScreenViewModel = viewModel()
    val email by viewModel.email.collectAsState()
    val phone by viewModel.phone.collectAsState()
    val externalJwt by viewModel.externalJwt.collectAsState()
    val externalUserId by viewModel.externalUserId.collectAsState()
    val isSendingEmailOTP by viewModel.isSendingEmailOTP.collectAsState()
    val isSendingSmsOTP by viewModel.isSendingSmsOTP.collectAsState()
    val isSigningInWithExternalJwt by viewModel.isSigningInWithExternalJwt.collectAsState()
    val errorMessage by viewModel.errorMessage.collectAsState()
    val isEmailOtpSheetPresented by viewModel.isEmailOtpSheetPresented.collectAsState()
    val isSmsOtpSheetPresented by viewModel.isSmsOtpSheetPresented.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.startListening(onNavigateToHome = onNavigateToHome)
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 24.dp)
    ) {
        Spacer(modifier = Modifier.height(80.dp))

        Text(
            text = "Dynamic SDK",
            style = MaterialTheme.typography.headlineLarge,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onBackground
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = "Please sign in to continue",
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        Spacer(modifier = Modifier.height(48.dp))

        // Email field
        OutlinedTextField(
            value = email,
            onValueChange = { viewModel.updateEmail(it) },
            label = { Text("Email") },
            placeholder = { Text("Enter your email") },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true,
            shape = RoundedCornerShape(12.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = MaterialTheme.colorScheme.primary,
                unfocusedBorderColor = MaterialTheme.colorScheme.outline,
                focusedContainerColor = MaterialTheme.colorScheme.surface,
                unfocusedContainerColor = MaterialTheme.colorScheme.surface
            )
        )

        Spacer(modifier = Modifier.height(16.dp))

        PrimaryButton(
            title = "Send Email OTP",
            onClick = { viewModel.sendEmailOTP() },
            isLoading = isSendingEmailOTP,
            isDisabled = email.trim().isEmpty()
        )

        Spacer(modifier = Modifier.height(32.dp))

        // Phone field
        OutlinedTextField(
            value = phone,
            onValueChange = { viewModel.updatePhone(it) },
            label = { Text("Phone (US/CA)") },
            placeholder = { Text("+1 234 567 8900") },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true,
            shape = RoundedCornerShape(12.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = MaterialTheme.colorScheme.primary,
                unfocusedBorderColor = MaterialTheme.colorScheme.outline,
                focusedContainerColor = MaterialTheme.colorScheme.surface,
                unfocusedContainerColor = MaterialTheme.colorScheme.surface
            )
        )

        Spacer(modifier = Modifier.height(16.dp))

        PrimaryButton(
            title = "Send SMS OTP",
            onClick = { viewModel.sendSmsOTP() },
            isLoading = isSendingSmsOTP,
            isDisabled = phone.trim().isEmpty()
        )

        Spacer(modifier = Modifier.height(32.dp))

        // Social login buttons
        Text(
            text = "Or continue with",
            style = MaterialTheme.typography.labelMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.padding(vertical = 8.dp)
        )

        Spacer(modifier = Modifier.height(8.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            SocialButton(
                text = "Farcaster",
                onClick = { viewModel.signInWithFarcaster() },
                modifier = Modifier.weight(1f)
            )
            SocialButton(
                text = "Google",
                onClick = { viewModel.signInWithGoogle() },
                modifier = Modifier.weight(1f)
            )
        }

        Spacer(modifier = Modifier.height(12.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            SocialButton(
                text = "Apple",
                onClick = { viewModel.signInWithApple() },
                modifier = Modifier.weight(1f)
            )
            SocialButton(
                text = "Passkey",
                onClick = { viewModel.signInWithPasskey() },
                modifier = Modifier.weight(1f)
            )
        }

        Spacer(modifier = Modifier.height(32.dp))

        Divider(color = MaterialTheme.colorScheme.outline)

        Spacer(modifier = Modifier.height(24.dp))

        // External JWT field
        Text(
            text = "Developer Options",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.SemiBold,
            color = MaterialTheme.colorScheme.onBackground
        )

        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(
            value = externalJwt,
            onValueChange = { viewModel.updateExternalJwt(it) },
            label = { Text("External JWT") },
            placeholder = { Text("Paste your JWT token") },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true,
            shape = RoundedCornerShape(12.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = MaterialTheme.colorScheme.primary,
                unfocusedBorderColor = MaterialTheme.colorScheme.outline,
                focusedContainerColor = MaterialTheme.colorScheme.surface,
                unfocusedContainerColor = MaterialTheme.colorScheme.surface
            )
        )

        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(
            value = externalUserId,
            onValueChange = { viewModel.updateExternalUserId(it) },
            label = { Text("External User ID") },
            placeholder = { Text("External user ID") },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true,
            shape = RoundedCornerShape(12.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = MaterialTheme.colorScheme.primary,
                unfocusedBorderColor = MaterialTheme.colorScheme.outline,
                focusedContainerColor = MaterialTheme.colorScheme.surface,
                unfocusedContainerColor = MaterialTheme.colorScheme.surface
            )
        )

        Spacer(modifier = Modifier.height(16.dp))

        PrimaryButton(
            title = "Sign in with External JWT",
            onClick = { viewModel.signInWithExternalJwt() },
            isLoading = isSigningInWithExternalJwt,
            isDisabled = externalJwt.trim().isEmpty() || externalUserId.trim().isEmpty()
        )

        if (errorMessage != null) {
            Spacer(modifier = Modifier.height(20.dp))
            ErrorMessageView(message = errorMessage!!)
        }

        Spacer(modifier = Modifier.height(32.dp))

        Divider(color = MaterialTheme.colorScheme.outline)

        Spacer(modifier = Modifier.height(24.dp))

        Text(
            text = "Dynamic Widget",
            style = MaterialTheme.typography.labelMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        Spacer(modifier = Modifier.height(12.dp))

        FilledTonalButton(
            onClick = { viewModel.openAuthFlow() },
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp),
            colors = ButtonDefaults.filledTonalButtonColors(
                containerColor = MaterialTheme.colorScheme.primaryContainer,
                contentColor = MaterialTheme.colorScheme.primary
            ),
            contentPadding = PaddingValues(horizontal = 24.dp, vertical = 14.dp)
        ) {
            Text(
                "Open Auth Flow",
                style = MaterialTheme.typography.labelLarge,
                fontWeight = FontWeight.SemiBold
            )
        }

        Spacer(modifier = Modifier.height(48.dp))
    }

    // Email OTP verification sheet
    if (isEmailOtpSheetPresented) {
        OtpVerificationSheet(
            title = "Email verification",
            subtitle = if (email.isEmpty()) null else "We sent a code to $email",
            onVerify = { code -> viewModel.verifyEmailOTP(code) },
            onResend = { viewModel.resendEmailOTP() },
            onDismiss = { viewModel.dismissEmailOtpSheet() }
        )
    }

    // SMS OTP verification sheet
    if (isSmsOtpSheetPresented) {
        OtpVerificationSheet(
            title = "SMS verification",
            subtitle = if (phone.isEmpty()) null else "We sent a code to $phone",
            onVerify = { code -> viewModel.verifySmsOTP(code) },
            onResend = { viewModel.resendSmsOTP() },
            onDismiss = { viewModel.dismissSmsOtpSheet() }
        )
    }
}

@Composable
private fun SocialButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    OutlinedButton(
        onClick = onClick,
        modifier = modifier,
        shape = RoundedCornerShape(12.dp),
        border = androidx.compose.foundation.BorderStroke(
            width = 1.dp,
            color = MaterialTheme.colorScheme.outline
        ),
        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 12.dp)
    ) {
        Text(
            text = text,
            style = MaterialTheme.typography.labelMedium,
            color = MaterialTheme.colorScheme.onSurface
        )
    }
}
