package com.theevilroot.vmsis.simulator

import android.app.Application
import com.theevilroot.vmsis.simulator.model.db.ISimulatorDatabase
import com.theevilroot.vmsis.simulator.model.db.MockDatabase
import org.kodein.di.Kodein
import org.kodein.di.KodeinAware
import org.kodein.di.android.x.androidXModule
import org.kodein.di.generic.bind
import org.kodein.di.generic.singleton

class App : Application(), KodeinAware {
    override val kodein: Kodein = Kodein {
        import(androidXModule(this@App))

        bind<ISimulatorDatabase>() with singleton { MockDatabase() }
    }
}