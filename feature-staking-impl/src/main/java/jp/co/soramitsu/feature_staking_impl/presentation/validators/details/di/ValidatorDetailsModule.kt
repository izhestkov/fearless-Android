package jp.co.soramitsu.feature_staking_impl.presentation.validators.details.di

import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import dagger.Module
import dagger.Provides
import dagger.multibindings.IntoMap
import jp.co.soramitsu.common.address.AddressIconGenerator
import jp.co.soramitsu.common.di.viewmodel.ViewModelKey
import jp.co.soramitsu.common.di.viewmodel.ViewModelModule
import jp.co.soramitsu.feature_staking_impl.domain.StakingInteractor
import jp.co.soramitsu.feature_staking_impl.presentation.StakingRouter
import jp.co.soramitsu.feature_staking_impl.presentation.validators.details.ValidatorDetailsViewModel
import jp.co.soramitsu.feature_staking_impl.presentation.validators.parcel.ValidatorDetailsParcelModel

@Module(includes = [ViewModelModule::class])
class ValidatorDetailsModule {

    @Provides
    @IntoMap
    @ViewModelKey(ValidatorDetailsViewModel::class)
    fun provideViewModel(
        interactor: StakingInteractor,
        router: StakingRouter,
        validator: ValidatorDetailsParcelModel,
        addressIconGenerator: AddressIconGenerator
    ): ViewModel {
        return ValidatorDetailsViewModel(
            interactor,
            router,
            validator,
            addressIconGenerator
        )
    }

    @Provides
    fun provideViewModelCreator(
        fragment: Fragment,
        viewModelFactory: ViewModelProvider.Factory
    ): ValidatorDetailsViewModel {
        return ViewModelProvider(fragment, viewModelFactory).get(ValidatorDetailsViewModel::class.java)
    }
}