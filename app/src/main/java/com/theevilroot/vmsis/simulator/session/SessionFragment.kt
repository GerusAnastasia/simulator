package com.theevilroot.vmsis.simulator.session

import android.content.res.ColorStateList
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.os.bundleOf
import androidx.lifecycle.*
import androidx.navigation.findNavController
import androidx.navigation.navOptions
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.snackbar.Snackbar
import com.theevilroot.vmsis.simulator.R
import com.theevilroot.vmsis.simulator.core.CoreFragment
import com.theevilroot.vmsis.simulator.core.StatsAdapter
import com.theevilroot.vmsis.simulator.core.createStatsAdapter
import com.theevilroot.vmsis.simulator.core.setupAsStats
import com.theevilroot.vmsis.simulator.model.*
import com.theevilroot.vmsis.simulator.model.db.ISimulatorDatabase
import kotlinx.android.synthetic.main.f_session.*
import kotlinx.android.synthetic.main.i_action.view.*
import kotlinx.android.synthetic.main.i_stat.view.*
import org.kodein.di.generic.instance

class ActionHolder (itemView: View): RecyclerView.ViewHolder(itemView) {
    fun bind(action: Action, denyReason: String?, listener: (Action)->Unit) = with(itemView) {
        name.text = action.description
        val allow = denyReason == null
        if (allow) {
            image_view.setImageResource(R.drawable.ic_round_check_24)
            image_view.imageTintList = ColorStateList.valueOf(Color.GREEN)
            setOnClickListener{ listener(action) }
        } else {
            image_view.setImageResource(R.drawable.ic_round_close_24)
            image_view.imageTintList = ColorStateList.valueOf(Color.RED)
            image_view.setOnClickListener {
                Toast.makeText(context, "$denyReason", Toast.LENGTH_SHORT).show()
            }
            setOnClickListener {
                Snackbar.make(rootView, "Нельзя выполнить это действие", Snackbar.LENGTH_SHORT)
                    .show()
            }
        }
    }
}

class ActionsAdapter (private val listener: (Action)->Unit): RecyclerView.Adapter<ActionHolder>() {

    private val data: MutableList<Action> = mutableListOf()
    var stats: Stats? = null
        set(value) {
            field = value
            notifyDataSetChanged()
        }

    fun updateData(list: List<Action>) {
        data.clear()
        data.addAll(list.toTypedArray())
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ActionHolder =
        LayoutInflater.from(parent.context)
            .inflate(R.layout.i_action, parent, false)
            .let(::ActionHolder)

    override fun onBindViewHolder(holder: ActionHolder, position: Int) {
        data[position].let {
            holder.bind(it, stats?.denyReason(it), listener)
        }
    }

    override fun getItemCount(): Int {
        return data.size
    }

}

class SessionViewModel (private val database: ISimulatorDatabase) : ViewModel() {
    val playerData: MutableLiveData<Player> = MutableLiveData()
    val sessionData = playerData.map {
        database.getSession(it)
    }

    val updateData: MutableLiveData<Pair<Session, SessionResult>> = MutableLiveData()
    val nextData = updateData.map { (session, result) ->
        database.updatePersistentState(session, result)
        result
    }
}

class SessionFragment : CoreFragment(R.layout.f_session), Observer<Session> {

    private val database by instance<ISimulatorDatabase>()
    private val viewModel by createViewModel<SessionViewModel> { database }
    private val player by fromArgs<Player>("player")

    private val statsAdapter: StatsAdapter by createStatsAdapter()

    private val actionsAdapter: ActionsAdapter by createActionsAdapter()
    private var session: Session? = null

    override fun View.onView() {
        with(actions_list) {
            layoutManager = GridLayoutManager(context, 2,
                    GridLayoutManager.HORIZONTAL, false)
            adapter = actionsAdapter
        }

        stats_list.setupAsStats(statsAdapter)
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

        actionsAdapter.updateData(t.actions)
        actionsAdapter.stats = t.stats
        statsAdapter.updateStats(t.stats.data)

        required_performance.visibility = if (t.sessionIndex >= t.semester.sessionsCount - 3) {
            View.VISIBLE
        } else {
            View.GONE
        }
        required_performance.text = "Необходимая успеваемость -- ${t.semester.requiredPerformance}"
    }

    private fun onActionClicked(action: Action) {
        session?.let {
            viewModel.updateData.postValue(it to it + action)
        }
    }

    private fun createActionsAdapter() = lazy { ActionsAdapter(::onActionClicked) }
}