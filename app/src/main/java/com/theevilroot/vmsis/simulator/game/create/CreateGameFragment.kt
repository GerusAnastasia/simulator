package com.theevilroot.vmsis.simulator.game.create

import android.view.View
import androidx.core.os.bundleOf
import androidx.lifecycle.*
import androidx.navigation.fragment.findNavController
import androidx.navigation.navOptions
import com.google.android.material.dialog.MaterialDialogs
import com.google.android.material.snackbar.Snackbar
import com.theevilroot.vmsis.simulator.R
import com.theevilroot.vmsis.simulator.core.CoreFragment
import com.theevilroot.vmsis.simulator.core.CoreViewModelFactory
import com.theevilroot.vmsis.simulator.model.Player
import com.theevilroot.vmsis.simulator.model.Session
import com.theevilroot.vmsis.simulator.model.db.ISimulatorDatabase
import kotlinx.android.synthetic.main.f_game_create.*
import kotlinx.android.synthetic.main.f_game_create.view.*
import org.kodein.di.generic.instance
import kotlin.random.Random

class CreateGameViewModel(private val database: ISimulatorDatabase) : ViewModel() {

    val inputData: MutableLiveData<Pair<String, Int>> = MutableLiveData()
    val playerIdData = inputData.map { (name, diff) ->
        val id = Random.nextInt()
        val player = Player(id, name, diff)
        database.addPlayer(player)
        player
    }
}

class CreateGameFragment : CoreFragment(R.layout.f_game_create), Observer<Player> {

    private val database by instance<ISimulatorDatabase>()
    private val viewModel by createViewModel<CreateGameViewModel> { database }

    override fun View.onView() {
        viewModel.playerIdData.observe(this@CreateGameFragment, this@CreateGameFragment)
        player_create.setOnClickListener(::onPlayerCreate)
        player_create.isEnabled = false
        player_difficulty.setOnSegmentSelectRequestListener {
            player_create.isEnabled = true
            true
        }
    }

    override fun onChanged(t: Player) {
        findNavController().navigate(R.id.fragment_session,
                bundleOf("player" to t),
                navOptions { launchSingleTop = true })
    }

    private fun onPlayerCreate(v: View) {
        if (!player_difficulty.hasSelectedSegment())
            return

        if (player_name.text.isNullOrBlank())
            return

        val diff = player_difficulty.lastSelectedAbsolutePosition
        val name = player_name.text.toString()
        viewModel.inputData.postValue(name to diff)
    }

}
