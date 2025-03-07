package jp.co.soramitsu.feature_wallet_api.domain.interfaces

import jp.co.soramitsu.core.model.SigningData
import jp.co.soramitsu.feature_wallet_api.domain.model.Asset
import jp.co.soramitsu.feature_wallet_api.domain.model.Fee
import jp.co.soramitsu.feature_wallet_api.domain.model.Token
import jp.co.soramitsu.feature_wallet_api.domain.model.Transaction
import jp.co.soramitsu.feature_wallet_api.domain.model.Transfer
import jp.co.soramitsu.feature_wallet_api.domain.model.TransferValidityStatus
import jp.co.soramitsu.feature_wallet_api.domain.model.WalletAccount
import kotlinx.coroutines.flow.Flow
import java.math.BigDecimal

interface WalletRepository {

    fun assetsFlow(account: WalletAccount): Flow<List<Asset>>

    suspend fun syncAssetsRates(account: WalletAccount)

    fun assetFlow(account: WalletAccount, type: Token.Type): Flow<Asset>

    suspend fun getAsset(account: WalletAccount, type: Token.Type): Asset?

    suspend fun syncAsset(account: WalletAccount, type: Token.Type)

    fun transactionsFirstPageFlow(currentAccount: WalletAccount, pageSize: Int, accounts: List<WalletAccount>): Flow<List<Transaction>>

    suspend fun syncTransactionsFirstPage(pageSize: Int, account: WalletAccount, accounts: List<WalletAccount>)

    suspend fun getTransactionPage(pageSize: Int, page: Int, currentAccount: WalletAccount, accounts: List<WalletAccount>): List<Transaction>

    suspend fun getContacts(account: WalletAccount, query: String): Set<String>

    suspend fun getTransferFee(account: WalletAccount, transfer: Transfer): Fee

    suspend fun performTransfer(account: WalletAccount, signingData: SigningData, transfer: Transfer, fee: BigDecimal)

    suspend fun checkTransferValidity(account: WalletAccount, transfer: Transfer): TransferValidityStatus

    suspend fun updatePhishingAddresses()

    suspend fun isAddressFromPhishingList(address: String): Boolean
}