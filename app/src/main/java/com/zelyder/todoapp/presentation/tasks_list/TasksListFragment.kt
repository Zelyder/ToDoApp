package com.zelyder.todoapp.presentation.tasks_list

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.zelyder.todoapp.R
import com.zelyder.todoapp.data.DataSource
import com.zelyder.todoapp.domain.models.Task

class TasksListFragment : Fragment(), TasksListItemClickListener {

    var recyclerView: RecyclerView? = null
    var visibilityImg: ImageView? = null
    var isHided: Boolean = false

    private val removedTasks = mutableListOf<Task>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_tasks_list, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val adapter = TasksListAdapter(this)
        recyclerView = view.findViewById<RecyclerView>(R.id.rvTasksList).apply {
            layoutManager = LinearLayoutManager(view.context)
            setAdapter(adapter)
        }
        val data = DataSource.getData()
        data.forEach { if(it.isDone) removedTasks.add(it) }
        adapter.submitList(data)

        visibilityImg = view.findViewById(R.id.visibilityImg)

        visibilityImg?.setOnClickListener {
            if (isHided) {
                visibilityImg?.setImageResource(R.drawable.ic_visibility_off)
                adapter.submitList(adapter.currentList + removedTasks)
            } else {
                visibilityImg?.setImageResource(R.drawable.ic_visibility_on)
                adapter.submitList(adapter.currentList.filter { !it.isDone })
            }
            isHided = !isHided
        }
    }

    override fun onCheck(task: Task) {
        if (task.isDone) {
            removedTasks.add(task)
        } else {
            if (task in removedTasks) {
                removedTasks.remove(task)
            }
        }

    }

    override fun onItemClick(task: Task) {
        findNavController().navigate(R.id.action_TasksListFragment_to_EditTaskFragment)
    }
}