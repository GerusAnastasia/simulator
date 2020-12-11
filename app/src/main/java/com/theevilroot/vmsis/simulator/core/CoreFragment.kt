package com.theevilroot.vmsis.simulator.core

import android.app.Activity
import android.os.Bundle
import android.os.Parcelable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.LayoutRes
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.theevilroot.vmsis.simulator.main.MainActivity
import com.theevilroot.vmsis.simulator.menu.MenuViewModel
import com.theevilroot.vmsis.simulator.model.Player
import com.theevilroot.vmsis.simulator.model.db.ISimulatorDatabase
import org.kodein.di.Kodein
import org.kodein.di.KodeinAware
import org.kodein.di.android.x.kodein

abstract class CoreFragment (@LayoutRes private val layoutRes: Int) : Fragment(), KodeinAware {

    override val kodein: Kodein by kodein()
    protected val mainActivity by activity<MainActivity>()

    override fun onCreateView(inflater: LayoutInflater, parent: ViewGroup?, state: Bundle?): View? =
        inflater.inflate(layoutRes, parent, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) =
        view.onView()

    protected abstract fun View.onView()

    private inline fun <reified T: Activity> activity() = lazy {
        if (activity !is T)
            throw RuntimeException("${javaClass.simpleName} cannot be created" +
                    " from ${activity?.javaClass?.simpleName} because ${T::class.java.simpleName}" +
                    "is required")
        activity as T
    }

    fun <T: Parcelable> fromArgs(name: String) = lazy {
        arguments?.getParcelable<T>(name)
                ?: throw RuntimeException("${this.javaClass.simpleName} should have argument `$name`")
    }

    inline fun <reified T: ViewModel> createViewModel(crossinline database: () -> ISimulatorDatabase) = lazy {
        ViewModelProvider(this, CoreViewModelFactory(database()))
                .get(T::class.java)
    }

}