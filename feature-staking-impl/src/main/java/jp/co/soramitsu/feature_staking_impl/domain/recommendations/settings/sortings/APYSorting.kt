package jp.co.soramitsu.feature_staking_impl.domain.recommendations.settings.sortings

import java.util.Comparator
import jp.co.soramitsu.feature_staking_api.domain.model.Validator
import jp.co.soramitsu.feature_staking_impl.domain.recommendations.settings.RecommendationSorting

object APYSorting : RecommendationSorting by Comparator.comparing(Validator::apy).reversed()
