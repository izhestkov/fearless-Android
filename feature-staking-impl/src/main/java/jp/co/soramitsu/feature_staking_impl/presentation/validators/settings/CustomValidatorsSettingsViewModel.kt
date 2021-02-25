package jp.co.soramitsu.feature_staking_impl.presentation.validators.settings

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import jp.co.soramitsu.common.base.BaseViewModel
import jp.co.soramitsu.common.utils.reversed
import jp.co.soramitsu.feature_staking_api.domain.api.StakingInteractor
import jp.co.soramitsu.feature_staking_impl.R
import jp.co.soramitsu.feature_staking_impl.domain.recommendations.settings.RecommendationFilter
import jp.co.soramitsu.feature_staking_impl.domain.recommendations.settings.RecommendationSettings
import jp.co.soramitsu.feature_staking_impl.domain.recommendations.settings.RecommendationSettingsProvider
import jp.co.soramitsu.feature_staking_impl.domain.recommendations.settings.RecommendationSettingsProviderFactory
import jp.co.soramitsu.feature_staking_impl.domain.recommendations.settings.filters.HasIdentityFilter
import jp.co.soramitsu.feature_staking_impl.domain.recommendations.settings.filters.NotOverSubscribedFilter
import jp.co.soramitsu.feature_staking_impl.domain.recommendations.settings.filters.NotSlashedFilter
import jp.co.soramitsu.feature_staking_impl.domain.recommendations.settings.sortings.APYSorting
import jp.co.soramitsu.feature_staking_impl.domain.recommendations.settings.sortings.CommissionSorting
import jp.co.soramitsu.feature_staking_impl.domain.recommendations.settings.sortings.StakeSorting
import jp.co.soramitsu.feature_staking_impl.presentation.StakingRouter
import kotlinx.coroutines.launch

private val SORT_MAPPING = mapOf(
    R.id.customValidatorSettingsSortAPY to APYSorting,
    R.id.customValidatorSettingsSortStake to StakeSorting,
    R.id.customValidatorSettingsSortCommission to CommissionSorting
)

private val SORT_MAPPING_REVERSE = SORT_MAPPING.reversed()

class CustomValidatorsSettingsViewModel(
    private val router: StakingRouter,
    private val recommendationSettingsProviderFactory: RecommendationSettingsProviderFactory,
    private val interactor: StakingInteractor
) : BaseViewModel() {

    private var recommendationSettingsProvider: RecommendationSettingsProvider? = null

    val selectedSortingLiveData = MutableLiveData<Int>()

    val filtersEnabledMap = createFiltersMap(
        HasIdentityFilter::class.java,
        NotOverSubscribedFilter::class.java,
        NotSlashedFilter::class.java
    )

    init {
        viewModelScope.launch {
            recommendationSettingsProvider = recommendationSettingsProviderFactory.get()

            val currentSettings = recommendationSettingsProvider!!.currentRecommendationSettings()

            initFromSettings(currentSettings)
        }
    }

    private fun initFromSettings(currentSettings: RecommendationSettings) {
        currentSettings.filters.forEach {
            filtersEnabledMap[it::class.java]?.value = true
        }

        selectedSortingLiveData.value = SORT_MAPPING_REVERSE[currentSettings.sorting]
    }

    fun reset() {
        viewModelScope.launch {
            val defaultSettings = recommendationSettingsProvider!!.defaultSettings()

            initFromSettings(defaultSettings)
        }
    }

    fun applyChanges() {
        if (recommendationSettingsProvider == null) return

        viewModelScope.launch {
            val currentSettings = recommendationSettingsProvider!!.currentRecommendationSettings()

            val allFilters = recommendationSettingsProvider!!.getAllFilters()

            val newFilters = allFilters.filter {
                filtersEnabledMap[it::class.java]?.value ?: false
            }

            val newSort = SORT_MAPPING[selectedSortingLiveData.value!!] ?: return@launch

            val newSettings = currentSettings.copy(
                filters = newFilters,
                sorting = newSort
            )

            recommendationSettingsProvider!!.setRecommendationSettings(newSettings)
        }
    }

    fun backClicked() {
        router.back()
    }

    private fun createFiltersMap(vararg filterClasses: Class<out RecommendationFilter>) = filterClasses.associate {
        it to MutableLiveData(false)
    }
}