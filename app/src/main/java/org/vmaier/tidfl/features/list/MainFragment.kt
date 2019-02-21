package org.vmaier.tidfl.features.list

import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.LinearLayoutManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import kotlinx.android.synthetic.main.fragment_main.*
import org.vmaier.tidfl.R
import org.vmaier.tidfl.data.Task

class MainFragment : Fragment() {

    private val tasks = listOf(
        Task(goal = "Do Dishes", duration = 15),
        Task(goal = "Make an Android App", details = "Things I Do For Loot", duration = 3600)
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        retainInstance = true
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?)
            : View? = inflater.inflate(R.layout.fragment_main, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        list_recycler_view.apply {
            layoutManager = LinearLayoutManager(activity)
            adapter = TaskListAdapter(tasks)
        }
    }
}