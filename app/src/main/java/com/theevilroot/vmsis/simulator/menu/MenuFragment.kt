package com.theevilroot.vmsis.simulator.menu

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.lifecycle.*
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.theevilroot.vmsis.simulator.R
import com.theevilroot.vmsis.simulator.core.CoreFragment
import com.theevilroot.vmsis.simulator.core.CoreViewModelFactory
import com.theevilroot.vmsis.simulator.model.GameInfo
import com.theevilroot.vmsis.simulator.model.Player
import com.theevilroot.vmsis.simulator.model.db.ISimulatorDatabase
import kotlinx.android.synthetic.main.f_menu.*
import kotlinx.android.synthetic.main.f_menu.menu_list
import kotlinx.android.synthetic.main.f_menu.view.*
import kotlinx.android.synthetic.main.i_menu_item.view.*
import kotlinx.android.synthetic.main.i_menu_new.view.*
import org.kodein.di.generic.instance

sealed class AbstractMenuItemHolder(itemView: View): RecyclerView.ViewHolder(itemView)

class MenuItemHolder (itemView: View): AbstractMenuItemHolder(itemView) {
    fun bind(item: GameInfo, listener: (GameInfo) -> Unit) = with(itemView) {
        setOnClickListener { listener(item) }
        player_name.text = item.player.name
        difficulty.text = "Difficulty: ${item.player.difficulty}"
    }
}

class NewMenuItemHolder (itemView: View) : AbstractMenuItemHolder(itemView) {

    fun bind(listener: View.OnClickListener) = with(itemView) {
        new_game.setOnClickListener(listener)
        setOnClickListener(null)
    }

}

class MenuItemAdapter (
    private val newListener: View.OnClickListener,
    private val itemListener: (GameInfo) -> Unit
) : RecyclerView.Adapter<AbstractMenuItemHolder>() {

    private val data: MutableList<GameInfo> = mutableListOf()

    fun updateData(d: List<GameInfo>) {
        data.clear()
        data.addAll(d)
        notifyDataSetChanged()
    }

    override fun getItemViewType(position: Int): Int =
        if (position == data.size) 1 else 2

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AbstractMenuItemHolder =
        LayoutInflater.from(parent.context)
            .inflate(if (viewType == 1) R.layout.i_menu_new else R.layout.i_menu_item,
                parent, false)
            .let {
                if (viewType == 1) NewMenuItemHolder(it) else MenuItemHolder(it)
            }

    override fun onBindViewHolder(holder: AbstractMenuItemHolder, position: Int) =
        when (holder) {
            is NewMenuItemHolder -> holder.bind(newListener)
            is MenuItemHolder -> holder.bind(data[position], itemListener)
        }

    override fun getItemCount(): Int =
        data.size + 1
}


class MenuViewModel (private val database: ISimulatorDatabase) : ViewModel() {

    private val playersData: MutableLiveData<List<Player>?> = MutableLiveData()
    val gamesData = playersData.switchMap {
        liveData {
            if (it == null)
                emit(null)
            else emit(it.mapNotNull { database.getGameInfo(it) })
        }
    }

    val startGamePlayer: MutableLiveData<Player> = MutableLiveData()
    val stateData = startGamePlayer.switchMap {
        liveData { emit(database.getGameInfo(it)) }
    }

    fun updatePlayersList() {
        playersData.postValue(database.getPlayers())
    }
}

class MenuFragment : CoreFragment(R.layout.f_menu), Observer<List<GameInfo>?> {

    private val database by instance<ISimulatorDatabase>()
    private val viewModel by createViewModel<MenuViewModel> { database }
    private val adapter by createMenuAdapter()

    override fun View.onView() {
        menu_list.let {
            it.layoutManager = LinearLayoutManager(context)
            it.adapter = adapter
        }

        viewModel.gamesData.observe(this@MenuFragment, this@MenuFragment)
        viewModel.stateData.observe(this@MenuFragment, Observer {
            val state = it ?: return@Observer

           findNavController().navigate(if (state.isFinished)
               R.id.fragment_game_finish
           else R.id.fragment_session, bundleOf("player" to state.player))
        })
        viewModel.updatePlayersList()
    }

    private fun onItemClicked(info: GameInfo) {
        viewModel.startGamePlayer.postValue(info.player)
    }

    private fun onNewClicked(v: View) {
        findNavController().navigate(R.id.fragment_game_create)
    }

    override fun onChanged(t: List<GameInfo>?) {
        if (t == null) {
            menu_progress.visibility = View.VISIBLE
        } else {
            menu_progress.visibility = View.GONE
            adapter.updateData(t)
        }
    }

    private fun createMenuAdapter() = lazy {
        MenuItemAdapter(::onNewClicked, ::onItemClicked)
    }
}