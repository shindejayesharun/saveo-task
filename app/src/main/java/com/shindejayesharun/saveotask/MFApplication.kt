package com.shindejayesharun.saveotask

import android.app.Application
import com.shindejayesharun.saveotask.data.network.ApiInterface
import com.shindejayesharun.saveotask.data.network.NetworkConnectionInterceptor
import com.shindejayesharun.saveotask.data.repositories.HomeRepository
import com.shindejayesharun.saveotask.data.repositories.MovieDetailRepository
import com.shindejayesharun.saveotask.ui.home.HomeViewModelFactory
import com.shindejayesharun.saveotask.ui.moviedetail.MovieDetailViewModelFactory
import org.kodein.di.Kodein
import org.kodein.di.KodeinAware
import org.kodein.di.android.x.androidXModule
import org.kodein.di.generic.bind
import org.kodein.di.generic.instance
import org.kodein.di.generic.provider
import org.kodein.di.generic.singleton

class MFApplication : Application(), KodeinAware {

    override val kodein = Kodein.lazy {
        import(androidXModule(this@MFApplication))

        bind() from singleton { NetworkConnectionInterceptor(instance()) }
        bind() from singleton { ApiInterface(instance()) }
        bind() from singleton { HomeRepository(instance()) }
        bind() from provider { HomeViewModelFactory(instance()) }
        bind() from singleton { MovieDetailRepository(instance()) }
        bind() from provider { MovieDetailViewModelFactory(instance()) }


    }

}