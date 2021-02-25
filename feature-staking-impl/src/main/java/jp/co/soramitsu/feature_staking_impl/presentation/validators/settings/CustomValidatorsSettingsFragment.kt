package jp.co.soramitsu.feature_staking_impl.presentation.validators.settings

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CompoundButton
import jp.co.soramitsu.common.base.BaseFragment
import jp.co.soramitsu.common.di.FeatureUtils
import jp.co.soramitsu.common.utils.bindTo
import jp.co.soramitsu.feature_staking_api.di.StakingFeatureApi
import jp.co.soramitsu.feature_staking_impl.R
import jp.co.soramitsu.feature_staking_impl.di.StakingFeatureComponent
import jp.co.soramitsu.feature_staking_impl.domain.recommendations.settings.RecommendationFilter
import jp.co.soramitsu.feature_staking_impl.domain.recommendations.settings.filters.HasIdentityFilter
import jp.co.soramitsu.feature_staking_impl.domain.recommendations.settings.filters.NotOverSubscribedFilter
import jp.co.soramitsu.feature_staking_impl.domain.recommendations.settings.filters.NotSlashedFilter
import kotlinx.android.synthetic.main.fragment_custom_validators_settings.customValidatorSettingsApply
import kotlinx.android.synthetic.main.fragment_custom_validators_settings.customValidatorSettingsFilterIdentity
import kotlinx.android.synthetic.main.fragment_custom_validators_settings.customValidatorSettingsFilterOverSubscribed
import kotlinx.android.synthetic.main.fragment_custom_validators_settings.customValidatorSettingsFilterSlashes
import kotlinx.android.synthetic.main.fragment_custom_validators_settings.customValidatorSettingsSort
import kotlinx.android.synthetic.main.fragment_custom_validators_settings.customValidatorSettingsToolbar

class CustomValidatorsSettingsFragment : BaseFragment<CustomValidatorsSettingsViewModel>() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_custom_validators_settings, container, false)
    }

    override fun initViews() {
        customValidatorSettingsApply.setOnClickListener { viewModel.applyChanges() }

        customValidatorSettingsToolbar.setHomeButtonListener { viewModel.backClicked() }
        customValidatorSettingsToolbar.setRightActionClickListener { viewModel.reset() }
    }

    override fun inject() {
        FeatureUtils.getFeature<StakingFeatureComponent>(
            requireContext(),
            StakingFeatureApi::class.java
        )
            .customValidatorsSettingsComponentFactory()
            .create(this)
            .inject(this)
    }

    override fun subscribe(viewModel: CustomValidatorsSettingsViewModel) {
        customValidatorSettingsSort.bindTo(viewModel.selectedSortingLiveData, viewLifecycleOwner)

        customValidatorSettingsFilterIdentity.bindFilter(HasIdentityFilter::class.java)
        customValidatorSettingsFilterSlashes.bindFilter(NotSlashedFilter::class.java)
        customValidatorSettingsFilterOverSubscribed.bindFilter(NotOverSubscribedFilter::class.java)
    }

    private fun CompoundButton.bindFilter(filterClass: Class<out RecommendationFilter>) {
        val source = viewModel.filtersEnabledMap[filterClass] ?: error("Cannot find $filterClass source")

        bindTo(source, viewLifecycleOwner)
    }
}