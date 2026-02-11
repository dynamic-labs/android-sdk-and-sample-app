package com.dynamic.sdk.example.Screens

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dynamic.sdk.android.DynamicSDK
import com.dynamic.sdk.android.Models.PhoneData
import com.dynamic.sdk.android.Models.SignInWithExternalJwtParams
import com.dynamic.sdk.android.Module.Auth.SocialAuthModule
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

/**
 * LoginScreenViewModel - handles login flow and authentication.
 */
class LoginScreenViewModel : ViewModel() {
    private val sdk = DynamicSDK.getInstance()
    
    private val _email = MutableStateFlow("")
    val email: StateFlow<String> = _email.asStateFlow()
    
    private val _phone = MutableStateFlow("")
    val phone: StateFlow<String> = _phone.asStateFlow()
    
    private val _externalJwt = MutableStateFlow("")
    val externalJwt: StateFlow<String> = _externalJwt.asStateFlow()

    private val _externalUserId = MutableStateFlow("")
    val externalUserId: StateFlow<String> = _externalUserId.asStateFlow()
    
    private val _isSendingEmailOTP = MutableStateFlow(false)
    val isSendingEmailOTP: StateFlow<Boolean> = _isSendingEmailOTP.asStateFlow()
    
    private val _isSendingSmsOTP = MutableStateFlow(false)
    val isSendingSmsOTP: StateFlow<Boolean> = _isSendingSmsOTP.asStateFlow()
    
    private val _isSigningInWithExternalJwt = MutableStateFlow(false)
    val isSigningInWithExternalJwt: StateFlow<Boolean> = _isSigningInWithExternalJwt.asStateFlow()
    
    private val _isEmailOtpSheetPresented = MutableStateFlow(false)
    val isEmailOtpSheetPresented: StateFlow<Boolean> = _isEmailOtpSheetPresented.asStateFlow()
    
    private val _isSmsOtpSheetPresented = MutableStateFlow(false)
    val isSmsOtpSheetPresented: StateFlow<Boolean> = _isSmsOtpSheetPresented.asStateFlow()
    
    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage.asStateFlow()
    
    private var onNavigateToHome: (() -> Unit)? = null
    private var didNavigateHome = false
    
    fun updateEmail(value: String) { _email.value = value }
    fun updatePhone(value: String) { _phone.value = value }
    fun updateExternalJwt(value: String) { _externalJwt.value = value }
    fun updateExternalUserId(value: String) { _externalUserId.value = value }
    
    fun dismissEmailOtpSheet() { _isEmailOtpSheetPresented.value = false }
    fun dismissSmsOtpSheet() { _isSmsOtpSheetPresented.value = false }
    
    fun startListening(onNavigateToHome: () -> Unit) {
        this.onNavigateToHome = onNavigateToHome
        
        if (didNavigateHome) return

        // Listen for token changes (primary trigger)
        viewModelScope.launch {
            sdk.auth.tokenChanges.collect { token ->
                android.util.Log.d("LoginVM", "📨 tokenChanges: ${token?.take(20)}")
                if (!token.isNullOrEmpty() && !didNavigateHome) {
                    android.util.Log.d("LoginVM", "✅ Token received! -> Home")
                    didNavigateHome = true
                    onNavigateToHome()
                }
            }
        }
    }
    
    fun openAuthFlow() {
        sdk.ui.showAuth()
    }
    
    fun sendEmailOTP() {
        val trimmed = _email.value.trim()
        if (trimmed.isEmpty()) return
        
        _errorMessage.value = null
        _isSendingEmailOTP.value = true
        
        viewModelScope.launch {
            try {
                sdk.auth.email.sendOTP(trimmed)
                _isEmailOtpSheetPresented.value = true
            } catch (e: Exception) {
                _errorMessage.value = "Failed to send email OTP: ${e.message}"
            }
            _isSendingEmailOTP.value = false
        }
    }
    
    suspend fun verifyEmailOTP(code: String) {
        val trimmed = code.trim()
        if (trimmed.isEmpty()) return
        sdk.auth.email.verifyOTP(trimmed)
    }
    
    suspend fun resendEmailOTP() {
        sdk.auth.email.resendOTP()
    }
    
    fun sendSmsOTP() {
        val trimmed = _phone.value.trim()
        if (trimmed.isEmpty()) return
        
        _errorMessage.value = null
        _isSendingSmsOTP.value = true
        
        viewModelScope.launch {
            try {
                val phoneData = when {
                    trimmed.startsWith("+1") -> {
                        PhoneData(dialCode = "+1", iso2 = "US", phone = trimmed.drop(2))
                    }
                    trimmed.startsWith("+") -> {
                        _errorMessage.value = "Only United States/Canada phone numbers are supported"
                        _isSendingSmsOTP.value = false
                        return@launch
                    }
                    else -> {
                        PhoneData(dialCode = "+1", iso2 = "US", phone = trimmed)
                    }
                }
                
                sdk.auth.sms.sendOTP(phoneData)
                _isSmsOtpSheetPresented.value = true
            } catch (e: Exception) {
                _errorMessage.value = "Failed to send SMS OTP: ${e.message}"
            }
            _isSendingSmsOTP.value = false
        }
    }
    
    suspend fun verifySmsOTP(code: String) {
        val trimmed = code.trim()
        if (trimmed.isEmpty()) return
        sdk.auth.sms.verifyOTP(trimmed)
    }
    
    suspend fun resendSmsOTP() {
        sdk.auth.sms.resendOTP()
    }
    
    fun signInWithFarcaster() {
        _errorMessage.value = null
        viewModelScope.launch {
            try {
                sdk.auth.social.connect(SocialAuthModule.SocialProvider.FARCASTER)
            } catch (e: Exception) {
                _errorMessage.value = "Farcaster sign-in failed: ${e.message}"
            }
        }
    }
    
    fun signInWithGoogle() {
        _errorMessage.value = null
        viewModelScope.launch {
            try {
                sdk.auth.social.connect(SocialAuthModule.SocialProvider.GOOGLE)
            } catch (e: Exception) {
                _errorMessage.value = "Google sign-in failed: ${e.message}"
            }
        }
    }
    
    fun signInWithApple() {
        _errorMessage.value = null
        viewModelScope.launch {
            try {
                sdk.auth.social.connect(SocialAuthModule.SocialProvider.APPLE)
            } catch (e: Exception) {
                _errorMessage.value = "Apple sign-in failed: ${e.message}"
            }
        }
    }
    
    fun signInWithPasskey() {
        _errorMessage.value = null
        viewModelScope.launch {
            try {
                sdk.auth.passkey.signIn()
            } catch (e: Exception) {
                _errorMessage.value = "Passkey sign-in failed: ${e.message}"
            }
        }
    }
    
    fun signInWithExternalJwt() {
        val jwtTrimmed = _externalJwt.value.trim()
        val userIdTrimmed = _externalUserId.value.trim()
        if (jwtTrimmed.isEmpty() || userIdTrimmed.isEmpty()) return

        _errorMessage.value = null
        _isSigningInWithExternalJwt.value = true

        viewModelScope.launch {
            try {
                sdk.auth.externalAuth.signInWithExternalJwt(
                    SignInWithExternalJwtParams(
                        externalJwt = jwtTrimmed,
                        externalUserId = userIdTrimmed
                    )
                )
            } catch (e: Exception) {
                _errorMessage.value = "External JWT sign-in failed: ${e.message}"
            }
            _isSigningInWithExternalJwt.value = false
        }
    }
}


