package jp.co.soramitsu.feature_staking_api.domain.api

import java.math.BigInteger
import jp.co.soramitsu.feature_staking_api.domain.model.Exposure
import jp.co.soramitsu.feature_staking_api.domain.model.ValidatorPrefs

interface StakingRepository {

    suspend fun getTotalIssuance(): BigInteger

    suspend fun getActiveEraIndex(): BigInteger

    suspend fun getElectedValidatorsExposure(eraIndex: BigInteger): AccountIdMap<Exposure>

    suspend fun getElectedValidatorsPrefs(eraIndex: BigInteger): AccountIdMap<ValidatorPrefs>

    suspend fun getSlashes(accountIdsHex: List<String>): AccountIdMap<Boolean>
}
