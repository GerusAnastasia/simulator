package com.theevilroot.vmsis.simulator.game.create

import android.view.View
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.theevilroot.vmsis.simulator.R
import com.theevilroot.vmsis.simulator.core.CoreFragment
import com.theevilroot.vmsis.simulator.core.CoreViewModelFactory
import com.theevilroot.vmsis.simulator.model.Player
import com.theevilroot.vmsis.simulator.model.db.ISimulatorDatabase
import kotlinx.android.synthetic.main.f_game_create.*
import kotlinx.android.synthetic.main.f_game_create.view.*
import org.kodein.di.generic.instance
import kotlin.random.Random

class CreateGameViewModel(private val database: ISimulatorDatabase) : ViewModel() {

    fun addPlayer(playerName: String, difficulty: Int): Int {
        val id = Random.nextInt()
        database.addPlayer(Player(id, playerName, difficulty))
        return id
    }

}

class CreateGameFragment : CoreFragment(R.layout.f_game_create) {

    private val database by instance<ISimulatorDatabase>()
    private val viewModel by createViewMode()

    override fun View.onView() {
        player_create.setOnClickListener(::onPlayerCreate)
        player_create.isEnabled = false
        player_difficulty.setOnSegmentSelectRequestListener {
            player_create.isEnabled = true
            true
        }
    }

    private fun onPlayerCreate(v: View) {
        if (!player_difficulty.hasSelectedSegment())
            return

        if (player_name.text.isNullOrBlank())
            return

        val id = viewModel.addPlayer(player_name.text.toString(),
            player_difficulty.lastSelectedAbsolutePosition)
    }

    private fun createViewMode() = lazy {
        ViewModelProvider(this@CreateGameFragment, CoreViewModelFactory(database))
            .get(CreateGameViewModel::class.java)
    }

}
