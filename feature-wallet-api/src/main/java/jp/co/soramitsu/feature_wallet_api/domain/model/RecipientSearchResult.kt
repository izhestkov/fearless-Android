package jp.co.soramitsu.feature_wallet_api.domain.model

class RecipientSearchResult(
    val myAccounts: List<WalletAccount>,
    val contacts: List<String>
)