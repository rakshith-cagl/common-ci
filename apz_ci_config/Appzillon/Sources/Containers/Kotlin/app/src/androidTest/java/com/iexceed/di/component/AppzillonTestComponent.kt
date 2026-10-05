package com.iexceed.di.component

import com.iexceed.di.module.AppzillonTestModule
import dagger.Component
import javax.inject.Singleton


@Singleton
@Component(modules = [AppzillonTestModule::class])
interface AppzillonTestComponent : AppzillonComponent {
}