package com.theevilroot.vmsis.simulator.menu

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.*
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.theevilroot.vmsis.simulator.R
import com.theevilroot.vmsis.simulator.core.CoreFragment
import com.theevilroot.vmsis.simulator.core.CoreViewModelFactory
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
    fun bind(item: Player, listener: View.OnClickListener) = with(itemView) {
        setOnClickListener(listener)
        player_name.text = item.name
        difficulty.text = "Difficulty: ${item.difficulty}"
    }
}

class NewMenuItemHolder (itemView: View) : AbstractMenuItemHolder(itemView) {

    fun bind(listener: View.OnClickListener) = with(itemView) {
        new_game.setOnClickListener(listener)
        setOnClickListener {  }
    }

}

class MenuItemAdapter (
    private val newListener: View.OnClickListener,
    private val itemListener: View.OnClickListener
    ) : RecyclerView.Adapter<AbstractMenuItemHolder>() {

    private val data: MutableList<Player> = mutableListOf()

    fun updateData(d: List<Player>) {
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

    val playersData: MutableLiveData<List<Player>?> = MutableLiveData(null)

    init {
        playersData.postValue(null)
    }

    fun updatePlayersList() {
        playersData.postValue(database.getPlayers())
    }
}

class MenuFragment : CoreFragment(R.layout.f_menu), Observer<List<Player>?> {

    private val database by instance<ISimulatorDatabase>()
    private val viewModel by menuViewModel()
    private val adapter by createMenuAdapter()

    override fun View.onView() {
        menu_list.let {
            it.layoutManager = LinearLayoutManager(context)
            it.adapter = adapter
        }

        viewModel.playersData.observe(this@MenuFragment, this@MenuFragment)
        viewModel.updatePlayersList()
    }

    private fun onItemClicked(v: View) {

    }
    private fun onNewClicked(v: View) {
        findNavController().navigate(R.id.fragment_game_create)
    }

    override fun onChanged(t: List<Player>?) {
        if (t == null) {
            menu_progress.visibility = View.VISIBLE
        } else {
            menu_progress.visibility = View.GONE
            adapter.updateData(t)
        }
    }

    private fun menuViewModel() = lazy {
        ViewModelProvider(this@MenuFragment, CoreViewModelFactory(database))
            .get(MenuViewModel::class.java)
    }

    private fun createMenuAdapter() = lazy {
        MenuItemAdapter(::onNewClicked, ::onItemClicked)
    }
}