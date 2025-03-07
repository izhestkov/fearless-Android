package jp.co.soramitsu.feature_wallet_impl.data.network.blockchain.updaters

import jp.co.soramitsu.common.data.network.runtime.binding.ExtrinsicStatusEvent
import jp.co.soramitsu.common.utils.networkType
import jp.co.soramitsu.common.utils.toAddress
import jp.co.soramitsu.core.updater.SubscriptionBuilder
import jp.co.soramitsu.core.updater.Updater
import jp.co.soramitsu.core_db.dao.TransactionDao
import jp.co.soramitsu.core_db.model.TransactionLocal
import jp.co.soramitsu.fearless_utils.runtime.Module
import jp.co.soramitsu.fearless_utils.scale.EncodableStruct
import jp.co.soramitsu.fearless_utils.ss58.SS58Encoder.toAccountId
import jp.co.soramitsu.feature_account_api.domain.updaters.AccountUpdateScope
import jp.co.soramitsu.feature_wallet_api.data.cache.AssetCache
import jp.co.soramitsu.feature_wallet_api.data.mappers.mapTokenTypeToTokenTypeLocal
import jp.co.soramitsu.feature_wallet_api.domain.model.Token
import jp.co.soramitsu.feature_wallet_api.domain.model.Transaction
import jp.co.soramitsu.feature_wallet_api.domain.model.amountFromPlanks
import jp.co.soramitsu.feature_wallet_impl.data.mappers.mapTransactionStatusToTransactionStatusLocal
import jp.co.soramitsu.feature_wallet_impl.data.network.blockchain.SubstrateRemoteSource
import jp.co.soramitsu.feature_wallet_impl.data.network.blockchain.struct.account.AccountData
import jp.co.soramitsu.feature_wallet_impl.data.network.blockchain.struct.account.AccountInfoFactory
import jp.co.soramitsu.feature_wallet_impl.data.network.blockchain.struct.account.AccountInfoSchema
import jp.co.soramitsu.feature_wallet_impl.data.network.blockchain.struct.extrinsic.TransferExtrinsic
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.onEach

class PaymentUpdater(
    private val substrateSource: SubstrateRemoteSource,
    private val assetCache: AssetCache,
    private val accountInfoFactory: AccountInfoFactory,
    private val transactionsDao: TransactionDao,
    override val scope: AccountUpdateScope
) : Updater {

    override suspend fun listenForUpdates(storageSubscriptionBuilder: SubscriptionBuilder): Flow<Updater.SideEffect> {
        val address = scope.getAccount().address

        val key = Module.System.Account.storageKey(address.toAccountId())

        return storageSubscriptionBuilder.subscribe(key)
            .onEach { change ->
                val newAccountInfo = readAccountInfo(change.value)

                updateAssetBalance(address, newAccountInfo)

                fetchTransfers(address, change.block)
            }
            .flowOn(Dispatchers.IO)
            .noSideAffects()
    }

    private suspend fun readAccountInfo(hex: String?): EncodableStruct<AccountInfoSchema> {
        return hex?.let { accountInfoFactory.decode(it) } ?: accountInfoFactory.createEmpty()
    }

    private suspend fun updateAssetBalance(
        address: String,
        accountInfo: EncodableStruct<AccountInfoSchema>
    ) = assetCache.updateAsset(address) { cachedAsset ->
        val data = accountInfo[accountInfo.schema.data]

        cachedAsset.copy(
            freeInPlanks = data[AccountData.free],
            reservedInPlanks = data[AccountData.reserved],
            miscFrozenInPlanks = data[AccountData.miscFrozen],
            feeFrozenInPlanks = data[AccountData.feeFrozen]
        )
    }

    private suspend fun fetchTransfers(address: String, blockHash: String) {
        val result = substrateSource.fetchAccountTransfersInBlock(blockHash, address)

        val blockTransfers = result.getOrNull() ?: return

        val local = blockTransfers.map {
            val localStatus = when (it.statusEvent) {
                ExtrinsicStatusEvent.SUCCESS -> Transaction.Status.COMPLETED
                ExtrinsicStatusEvent.FAILURE -> Transaction.Status.FAILED
                null -> Transaction.Status.PENDING
            }

            createTransactionLocal(it.extrinsic, localStatus, address)
        }

        transactionsDao.insert(local)
    }

    private suspend fun createTransactionLocal(
        extrinsic: TransferExtrinsic,
        status: Transaction.Status,
        accountAddress: String
    ): TransactionLocal {
        val localCopy = transactionsDao.getTransaction(extrinsic.hash)

        val fee = localCopy?.feeInPlanks

        val networkType = accountAddress.networkType()
        val tokenType = Token.Type.fromNetworkType(networkType)

        val senderAddress = extrinsic.senderId.toAddress(networkType)
        val recipientAddress = extrinsic.recipientId.toAddress(networkType)

        return TransactionLocal(
            hash = extrinsic.hash,
            accountAddress = accountAddress,
            senderAddress = senderAddress,
            recipientAddress = recipientAddress,
            source = TransactionLocal.Source.BLOCKCHAIN,
            status = mapTransactionStatusToTransactionStatusLocal(status),
            feeInPlanks = fee,
            token = mapTokenTypeToTokenTypeLocal(tokenType),
            amount = tokenType.amountFromPlanks(extrinsic.amountInPlanks),
            date = System.currentTimeMillis(),
            networkType = networkType
        )
    }
}