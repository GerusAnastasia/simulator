package com.theevilroot.vmsis.simulator.session

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.lifecycle.*
import androidx.navigation.findNavController
import androidx.navigation.navOptions
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.snackbar.Snackbar
import com.theevilroot.vmsis.simulator.R
import com.theevilroot.vmsis.simulator.core.CoreFragment
import com.theevilroot.vmsis.simulator.core.CoreViewModelFactory
import com.theevilroot.vmsis.simulator.model.IAction
import com.theevilroot.vmsis.simulator.model.Player
import com.theevilroot.vmsis.simulator.model.Session
import com.theevilroot.vmsis.simulator.model.SessionResult
import com.theevilroot.vmsis.simulator.model.db.ISimulatorDatabase
import kotlinx.android.synthetic.main.f_session.*
import kotlinx.android.synthetic.main.i_action.view.*
import org.kodein.di.generic.instance

class ActionHolder (itemView: View): RecyclerView.ViewHolder(itemView) {
    fun bind(action: IAction, listener: (IAction)->Unit) = with(itemView) {
        name.text = action.getDescription()
        setOnClickListener{ listener(action) }
    }
}

class ActionsAdapter (private val listener: (IAction)->Unit): RecyclerView.Adapter<ActionHolder>() {

    private val data: MutableList<IAction> = mutableListOf()

    fun updateData(list: List<IAction>) {
        data.clear()
        data.addAll(list.toTypedArray())
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ActionHolder =
        LayoutInflater.from(parent.context)
            .inflate(R.layout.i_action, parent, false)
            .let(::ActionHolder)

    override fun onBindViewHolder(holder: ActionHolder, position: Int) {
        data[position].let { holder.bind(it, listener) }
    }

    override fun getItemCount(): Int {
        return data.size
    }

}

class SessionViewModel (private val database: ISimulatorDatabase) : ViewModel() {
    val playerData: MutableLiveData<Player> = MutableLiveData()
    val sessionData = playerData.switchMap {
        liveData { emit(database.getSession(it)) }
    }

    val updateData: MutableLiveData<Pair<Session, SessionResult>> = MutableLiveData()
    val nextData = updateData.switchMap { (session, result) ->
        database.updatePersistentState(session, result)
        liveData { emit(result) }
    }
}

class SessionFragment : CoreFragment(R.layout.f_session), Observer<Session> {

    private val database by instance<ISimulatorDatabase>()
    private val viewModel by createViewModel<SessionViewModel> { database }
    private val player by fromArgs<Player>("player")

    private val adapter: ActionsAdapter by createAdapter()
    private var session: Session? = null

    override fun View.onView() {
        with(actions_list) {
            layoutManager = GridLayoutManager(context, 2,
                    GridLayoutManager.HORIZONTAL, false)
            adapter = this@SessionFragment.adapter
        }

        viewModel.sessionData.observe(this@SessionFragment, this@SessionFragment)
        viewModel.playerData.postValue(player)

        viewModel.nextData.observe(this@SessionFragment, Observer {
            if (it.nextSession != null) {
                viewModel.playerData.postValue(it.nextSession.player)
            } else {
                findNavController().navigate(R.id.fragment_game_finish,
                        bundleOf("player" to player),
                        navOptions { launchSingleTop = true })
            }
        })
    }

    override fun onChanged(t: Session) {
        session = t

        semester_title.text = "${t.semester.index + 1} семестр"
        session_title.text = "День ${t.sessionIndex + 1}/${t.semester.sessionsCount}"

        adapter.updateData(t.actions)
    }

    private fun onActionClicked(action: IAction) {
        session?.let {
            viewModel.updateData.postValue(it to it + action)
        }
    }

    private fun createAdapter() = lazy { ActionsAdapter(::onActionClicked) }
}