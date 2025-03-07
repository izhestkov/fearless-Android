package jp.co.soramitsu.feature_staking_impl.presentation.confirm.di

import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import dagger.Module
import dagger.Provides
import dagger.multibindings.IntoMap
import jp.co.soramitsu.common.address.AddressIconGenerator
import jp.co.soramitsu.common.di.viewmodel.ViewModelKey
import jp.co.soramitsu.common.di.viewmodel.ViewModelModule
import jp.co.soramitsu.common.resources.ResourceManager
import jp.co.soramitsu.common.validation.ValidationSystem
import jp.co.soramitsu.feature_account_api.presenatation.actions.ExternalAccountActions
import jp.co.soramitsu.feature_staking_impl.domain.StakingInteractor
import jp.co.soramitsu.feature_staking_impl.domain.model.SetupStakingPayload
import jp.co.soramitsu.feature_staking_impl.domain.recommendations.settings.RecommendationSettingsProviderFactory
import jp.co.soramitsu.feature_staking_impl.domain.setup.MaxFeeEstimator
import jp.co.soramitsu.feature_staking_impl.domain.setup.validations.StakingValidationFailure
import jp.co.soramitsu.feature_staking_impl.presentation.StakingRouter
import jp.co.soramitsu.feature_staking_impl.presentation.common.StakingSharedState
import jp.co.soramitsu.feature_staking_impl.presentation.common.fee.FeeLoaderMixin
import jp.co.soramitsu.feature_staking_impl.presentation.confirm.ConfirmStakingViewModel

@Module(includes = [ViewModelModule::class])
class ConfirmStakingModule {

    @Provides
    @IntoMap
    @ViewModelKey(ConfirmStakingViewModel::class)
    fun provideViewModel(
        interactor: StakingInteractor,
        router: StakingRouter,
        addressIconGenerator: AddressIconGenerator,
        resourceManager: ResourceManager,
        maxFeeEstimator: MaxFeeEstimator,
        validationSystem: ValidationSystem<SetupStakingPayload, StakingValidationFailure>,
        stakingSharedState: StakingSharedState,
        feeLoaderMixin: FeeLoaderMixin.Presentation,
        externalAccountActions: ExternalAccountActions.Presentation,
        recommendationSettingsProviderFactory: RecommendationSettingsProviderFactory
    ): ViewModel {
        return ConfirmStakingViewModel(
            router,
            interactor,
            addressIconGenerator,
            resourceManager,
            validationSystem,
            stakingSharedState,
            maxFeeEstimator,
            feeLoaderMixin,
            externalAccountActions,
            recommendationSettingsProviderFactory
        )
    }

    @Provides
    fun provideViewModelCreator(
        fragment: Fragment,
        viewModelFactory: ViewModelProvider.Factory
    ): ConfirmStakingViewModel {
        return ViewModelProvider(fragment, viewModelFactory).get(ConfirmStakingViewModel::class.java)
    }
}