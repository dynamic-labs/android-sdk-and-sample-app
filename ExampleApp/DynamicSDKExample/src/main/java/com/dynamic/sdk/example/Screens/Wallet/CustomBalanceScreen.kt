package com.dynamic.sdk.example.Screens.Wallet

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Error
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.compose.viewModel
import com.dynamic.sdk.android.DynamicSDK
import com.dynamic.sdk.android.Models.BaseWallet
import com.dynamic.sdk.android.Models.BalanceRequestItem
import com.dynamic.sdk.android.Models.ChainEnum
import com.dynamic.sdk.android.Models.MultichainBalanceRequest
import com.dynamic.sdk.android.Models.TokenBalance
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

// Predefined token data class
data class PredefinedToken(
    val name: String,
    val symbol: String,
    val networkId: Int,
    val contractAddress: String,
    val networkName: String
)

// EVM tokens
val evmTokens = listOf(
    PredefinedToken(
        name = "USDC Mainnet",
        symbol = "USDC",
        networkId = 1,
        contractAddress = "0xA0b86991c6218b36c1d19D4a2e9Eb0cE3606eB48",
        networkName = "Ethereum"
    ),
    PredefinedToken(
        name = "USDC Sepolia",
        symbol = "USDC",
        networkId = 11155111,
        contractAddress = "0x1c7D4B196Cb0C7B01d743Fbc6116a902379C7238",
        networkName = "Sepolia"
    ),
    PredefinedToken(
        name = "USDC Polygon",
        symbol = "USDC",
        networkId = 137,
        contractAddress = "0x3c499c542cEF5E3811e1192ce70d8cC03d5c3359",
        networkName = "Polygon"
    ),
    PredefinedToken(
        name = "USDC Arbitrum",
        symbol = "USDC",
        networkId = 42161,
        contractAddress = "0xaf88d065e77c8cC2239327C5EDb3A432268e5831",
        networkName = "Arbitrum"
    ),
    PredefinedToken(
        name = "USDC Base",
        symbol = "USDC",
        networkId = 8453,
        contractAddress = "0x833589fCD6eDb6E08f4c7C32D4f71b54bdA02913",
        networkName = "Base"
    )
)

// Solana tokens
val solanaTokens = listOf(
    PredefinedToken(
        name = "USDC Mainnet",
        symbol = "USDC",
        networkId = 101,
        contractAddress = "EPjFWdd5AufqSSqeM2qN1xzybapC8G4wEGGkZwyTDt1v",
        networkName = "Solana Mainnet"
    ),
    PredefinedToken(
        name = "USDC Devnet",
        symbol = "USDC",
        networkId = 103,
        contractAddress = "4zMMC9srt5Ri5X14GAgXhaHii3GnPAEERYPJgZJDncDU",
        networkName = "Solana Devnet"
    )
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CustomBalanceScreen(
    onNavigateBack: () -> Unit,
    wallet: BaseWallet
) {
    val viewModel: CustomBalanceViewModel = viewModel(
        factory = CustomBalanceViewModelFactory(wallet)
    )
    val contractAddress by viewModel.contractAddress.collectAsState()
    val networkId by viewModel.networkId.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val errorMessage by viewModel.errorMessage.collectAsState()
    val result by viewModel.result.collectAsState()
    val hasChecked by viewModel.hasChecked.collectAsState()

    val focusManager = LocalFocusManager.current

    val isSolanaWallet = wallet.chain.uppercase() == "SOL" || wallet.chain.uppercase() == "SOLANA"
    val predefinedTokens = if (isSolanaWallet) solanaTokens else evmTokens

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        "Custom Balance",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.SemiBold
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(
                            imageVector = Icons.Filled.ArrowBack,
                            contentDescription = "Back"
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background
                )
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
                .padding(paddingValues)
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Quick Select Section
            Text(
                text = "Quick Select",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold
            )

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                predefinedTokens.forEach { token ->
                    val isSelected = contractAddress == token.contractAddress &&
                            networkId == token.networkId.toString()
                    TokenChip(
                        token = token,
                        isSelected = isSelected,
                        onClick = {
                            viewModel.selectToken(token)
                        }
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Contract Address Field
            Text(
                text = "Contract Address",
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.Medium
            )
            OutlinedTextField(
                value = contractAddress,
                onValueChange = { viewModel.updateContractAddress(it) },
                modifier = Modifier.fillMaxWidth(),
                placeholder = { Text("Enter token contract address") },
                singleLine = true,
                shape = RoundedCornerShape(12.dp)
            )

            // Network ID Field
            Text(
                text = "Network ID",
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.Medium
            )
            OutlinedTextField(
                value = networkId,
                onValueChange = { viewModel.updateNetworkId(it) },
                modifier = Modifier.fillMaxWidth(),
                placeholder = { Text("Enter network ID (e.g., 1 for Ethereum)") },
                singleLine = true,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                shape = RoundedCornerShape(12.dp)
            )

            Spacer(modifier = Modifier.height(8.dp))

            // Check Balance Button
            Button(
                onClick = {
                    focusManager.clearFocus()
                    viewModel.checkBalance()
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp),
                enabled = !isLoading,
                shape = RoundedCornerShape(12.dp)
            ) {
                if (isLoading) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(20.dp),
                        strokeWidth = 2.dp,
                        color = MaterialTheme.colorScheme.onPrimary
                    )
                } else {
                    Text("Check Balance")
                }
            }

            // Error Message
            errorMessage?.let { error ->
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.errorContainer
                    ),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Row(
                        modifier = Modifier.padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.Error,
                            contentDescription = "Error",
                            tint = MaterialTheme.colorScheme.error
                        )
                        Spacer(modifier = Modifier.width(12.dp))
                        Text(
                            text = error,
                            color = MaterialTheme.colorScheme.onErrorContainer,
                            style = MaterialTheme.typography.bodyMedium
                        )
                    }
                }
            }

            // Result Card
            result?.let { token ->
                ResultCard(token = token)
            }

            // No Balance Card
            if (result == null && !isLoading && errorMessage == null && contractAddress.isNotEmpty() && hasChecked) {
                NoBalanceCard()
            }

            Spacer(modifier = Modifier.height(20.dp))
        }
    }
}

@Composable
fun TokenChip(
    token: PredefinedToken,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .width(100.dp)
            .height(80.dp)
            .clickable { onClick() },
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (isSelected)
                MaterialTheme.colorScheme.primaryContainer
            else
                MaterialTheme.colorScheme.surfaceVariant
        ),
        border = BorderStroke(
            width = if (isSelected) 2.dp else 1.dp,
            color = if (isSelected)
                MaterialTheme.colorScheme.primary
            else
                MaterialTheme.colorScheme.outline
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(8.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = token.symbol,
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.Bold,
                color = if (isSelected)
                    MaterialTheme.colorScheme.primary
                else
                    MaterialTheme.colorScheme.onSurface
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = token.networkName,
                style = MaterialTheme.typography.labelSmall,
                color = if (isSelected)
                    MaterialTheme.colorScheme.primary
                else
                    MaterialTheme.colorScheme.onSurfaceVariant,
                maxLines = 2
            )
        }
    }
}

@Composable
fun ResultCard(token: TokenBalance) {
    val displayBalance = token.balanceDecimal ?: token.balance ?: "0"

    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = Color(0xFFE8F5E9) // Light green
        ),
        shape = RoundedCornerShape(12.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Default.CheckCircle,
                    contentDescription = "Success",
                    tint = Color(0xFF2E7D32) // Dark green
                )
                Spacer(modifier = Modifier.width(12.dp))
                Text(
                    text = "$displayBalance ${token.symbol ?: "tokens"}",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF1B5E20) // Darker green
                )
            }
            Spacer(modifier = Modifier.height(8.dp))
            token.name?.let { name ->
                Text(
                    text = name,
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color(0xFF2E7D32)
                )
            }
            token.networkId?.let { networkId ->
                Text(
                    text = "Network ID: $networkId",
                    style = MaterialTheme.typography.bodySmall,
                    color = Color(0xFF388E3C)
                )
            }
            token.contractAddress?.let { address ->
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "Contract: ${address.take(10)}...${address.takeLast(8)}",
                    style = MaterialTheme.typography.bodySmall,
                    fontFamily = FontFamily.Monospace,
                    color = Color(0xFF388E3C)
                )
            }
        }
    }
}

@Composable
fun NoBalanceCard() {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        ),
        shape = RoundedCornerShape(12.dp)
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = Icons.Default.Info,
                contentDescription = "Info",
                tint = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Spacer(modifier = Modifier.width(12.dp))
            Text(
                text = "0 tokens (no balance found)",
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

// ViewModel
class CustomBalanceViewModel(private val wallet: BaseWallet) : ViewModel() {
    private val sdk = DynamicSDK.getInstance()

    private val _contractAddress = MutableStateFlow("")
    val contractAddress: StateFlow<String> = _contractAddress.asStateFlow()

    private val _networkId = MutableStateFlow("")
    val networkId: StateFlow<String> = _networkId.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage.asStateFlow()

    private val _result = MutableStateFlow<TokenBalance?>(null)
    val result: StateFlow<TokenBalance?> = _result.asStateFlow()

    private val _hasChecked = MutableStateFlow(false)
    val hasChecked: StateFlow<Boolean> = _hasChecked.asStateFlow()

    private val isSolanaWallet: Boolean
        get() = wallet.chain.uppercase() == "SOL" || wallet.chain.uppercase() == "SOLANA"

    private val chain: ChainEnum
        get() = if (isSolanaWallet) ChainEnum.SOL else ChainEnum.EVM

    fun selectToken(token: PredefinedToken) {
        _contractAddress.value = token.contractAddress
        _networkId.value = token.networkId.toString()
        _result.value = null
        _errorMessage.value = null
        _hasChecked.value = false
    }

    fun updateContractAddress(value: String) {
        _contractAddress.value = value
        if (_hasChecked.value || _result.value != null) {
            _hasChecked.value = false
            _result.value = null
        }
    }

    fun updateNetworkId(value: String) {
        _networkId.value = value
        if (_hasChecked.value || _result.value != null) {
            _hasChecked.value = false
            _result.value = null
        }
    }

    fun checkBalance() {
        val contract = _contractAddress.value.trim()
        val networkIdStr = _networkId.value.trim()

        if (contract.isEmpty()) {
            _errorMessage.value = "Please enter a contract address"
            return
        }

        val networkIdInt = networkIdStr.toIntOrNull()
        if (networkIdInt == null) {
            _errorMessage.value = "Please enter a valid network ID"
            return
        }

        _isLoading.value = true
        _errorMessage.value = null
        _result.value = null

        viewModelScope.launch {
            try {
                val request = MultichainBalanceRequest(
                    balanceRequests = listOf(
                        BalanceRequestItem(
                            address = wallet.address,
                            chain = chain,
                            networkIds = listOf(networkIdInt),
                            whitelistedContracts = listOf(contract)
                        )
                    ),
                    filterSpamTokens = false
                )

                val response = sdk.wallets.getMultichainBalances(request)

                val token = response.balances.firstOrNull { balance ->
                    balance.contractAddress?.lowercase() == contract.lowercase()
                }

                _result.value = token
                _hasChecked.value = true

            } catch (e: Exception) {
                _errorMessage.value = "Failed to fetch balance: ${e.localizedMessage}"
            }

            _isLoading.value = false
        }
    }
}

class CustomBalanceViewModelFactory(private val wallet: BaseWallet) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(CustomBalanceViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return CustomBalanceViewModel(wallet) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
