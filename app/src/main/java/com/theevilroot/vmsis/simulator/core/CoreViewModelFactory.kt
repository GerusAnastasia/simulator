package com.theevilroot.vmsis.simulator.core

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.theevilroot.vmsis.simulator.model.db.ISimulatorDatabase

class CoreViewModelFactory (private val database: ISimulatorDatabase) : ViewModelProvider.Factory {
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return modelClass.getConstructor(ISimulatorDatabase::class.java)
            .newInstance(database)
    }
}