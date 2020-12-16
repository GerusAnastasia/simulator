package com.theevilroot.vmsis.simulator.game.finish

import android.view.View
import androidx.core.os.bundleOf
import androidx.lifecycle.*
import androidx.navigation.fragment.findNavController
import androidx.navigation.navOptions
import com.google.android.material.snackbar.Snackbar
import com.theevilroot.vmsis.simulator.R
import com.theevilroot.vmsis.simulator.core.CoreFragment
import com.theevilroot.vmsis.simulator.core.createStatsAdapter
import com.theevilroot.vmsis.simulator.core.setupAsStats
import com.theevilroot.vmsis.simulator.model.GameInfo
import com.theevilroot.vmsis.simulator.model.Player
import com.theevilroot.vmsis.simulator.model.db.ISimulatorDatabase
import kotlinx.android.synthetic.main.f_game_finish.*
import kotlinx.android.synthetic.main.f_game_finish.view.*
import org.kodein.di.generic.instance

class GameFinishViewModel(private val database: ISimulatorDatabase) : ViewModel() {

    val playerData: MutableLiveData<Player> = MutableLiveData()
    val resultData = playerData.switchMap<Player, GameInfo?> {
        liveData { emit(database.getGameInfo(it)) }
    }

}

class GameFinishFragment : CoreFragment(R.layout.f_game_finish), Observer<GameInfo?> {

    private val database by instance<ISimulatorDatabase>()
    private val viewModel by createViewModel<GameFinishViewModel> { database }
    private val player by fromArgs<Player>("player")

    private val statsAdapter by createStatsAdapter()

    override fun View.onView() {
        viewModel.resultData.observe(this@GameFinishFragment, this@GameFinishFragment)
        viewModel.playerData.postValue(player)

        back_to_menu.setOnClickListener {
            findNavController().navigate(R.id.fragment_menu, null,
                    navOptions { launchSingleTop = true })
        }

        stats.setupAsStats(statsAdapter)
    }

    override fun onChanged(t: GameInfo?) {
        if (t == null || !t.isFinished) {
            findNavController().navigate(R.id.fragment_session, bundleOf(
                    "player" to player))
        } else {
            player_name.text = t.player.name
            cause.text = t.result.description
            progress_title.text = "Last semester: ${t.semester.index}"
            statsAdapter.updateStats(t.stats.data)
        }
    }
}