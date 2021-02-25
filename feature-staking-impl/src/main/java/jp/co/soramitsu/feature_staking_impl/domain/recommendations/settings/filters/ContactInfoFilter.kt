package jp.co.soramitsu.feature_staking_impl.domain.recommendations.settings.filters

import jp.co.soramitsu.feature_staking_api.domain.model.Identity
import jp.co.soramitsu.feature_staking_api.domain.model.Validator
import jp.co.soramitsu.feature_staking_impl.domain.recommendations.settings.RecommendationFilter

typealias IdentityFieldExtractor = (Identity) -> String?

class ContactInfoFilter(
    private val fields: List<IdentityFieldExtractor>
) : RecommendationFilter {

    override fun shouldInclude(model: Validator): Boolean {
        val identity = model.identity ?: return false

        return fields.any { it.invoke(identity) != null }
    }

    companion object {
        val email = Identity::email
        val web = Identity::web
        val twitter = Identity::twitter
        val riot = Identity::riot

        val all = listOf(email, web, twitter, riot)

        val atLeastOnceFilter = ContactInfoFilter(all)
    }
}