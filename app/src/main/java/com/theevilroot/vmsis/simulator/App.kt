package com.theevilroot.vmsis.simulator

import android.app.Application
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.theevilroot.vmsis.simulator.model.db.ISimulatorDatabase
import com.theevilroot.vmsis.simulator.model.db.CoreDatabase
import com.theevilroot.vmsis.simulator.model.db.FileDatabase
import org.kodein.di.Kodein
import org.kodein.di.KodeinAware
import org.kodein.di.android.x.androidXModule
import org.kodein.di.generic.bind
import org.kodein.di.generic.instance
import org.kodein.di.generic.provider
import org.kodein.di.generic.singleton

class App : Application(), KodeinAware {
    override val kodein: Kodein = Kodein {
        import(androidXModule(this@App))

        bind<Gson>() with provider { GsonBuilder().setPrettyPrinting().create() }
//        bind<ISimulatorDatabase>() with singleton { FileDatabase(instance(), filesDir, "saves") }
        bind<ISimulatorDatabase>() with singleton { CoreDatabase() }
    }
}