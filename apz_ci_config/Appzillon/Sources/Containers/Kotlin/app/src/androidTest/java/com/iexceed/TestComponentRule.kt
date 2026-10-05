package com.iexceed

import android.content.Context
import com.iexceed.di.component.AppzillonTestComponent
import com.iexceed.di.component.DaggerAppzillonTestComponent
import com.iexceed.di.module.AppzillonTestModule
import org.junit.rules.TestRule
import org.junit.runner.Description
import org.junit.runners.model.Statement

class TestComponentRule(private val context: Context) : TestRule {

    var testComponent: AppzillonTestComponent? = null

    fun getContext() = context

    private fun setupDaggerTestComponentInApplication() {
        val application = context.applicationContext as AppzillonApplication
        testComponent = DaggerAppzillonTestComponent.builder()
            .appzillonTestModule(AppzillonTestModule(application))
            .build()
        application.setComponent(testComponent!!)
    }

    override fun apply(base: Statement, description: Description?): Statement {
        return object : Statement() {
            @Throws(Throwable::class)
            override fun evaluate() {
                try {
                    setupDaggerTestComponentInApplication()
                    base.evaluate()
                } finally {
                    testComponent = null
                }
            }
        }
    }

}