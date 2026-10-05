package com.iexceed.di.component

import com.iexceed.appzillonapp.AppzillonMainScreen
import com.iexceed.di.ActivityScope
import com.iexceed.di.module.ActivityModule
import dagger.Component

@ActivityScope
@Component(
    dependencies = [AppzillonComponent::class],
    modules = [ActivityModule::class]
)
interface ActivityComponent {

    fun inject(activity: AppzillonMainScreen)

    //fun inject(activity: LoginActivity)

}