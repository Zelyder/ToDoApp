package com.zelyder.todoapp.presentation.tasks_list

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.zelyder.todoapp.MyApp
import com.zelyder.todoapp.R
import com.zelyder.todoapp.data.DataSource
import com.zelyder.todoapp.domain.models.Task
import com.zelyder.todoapp.viewModelFactoryProvider

class TasksListFragment : Fragment(), TasksListItemClickListener {
    private val viewModel: TasksListViewModel by viewModels { viewModelFactoryProvider().viewModelFactory() }

    var recyclerView: RecyclerView? = null
    var visibilityImg: ImageView? = null

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
        val itemTouchHelper = ItemTouchHelper(SwipeGesture(adapter, requireContext()))
        itemTouchHelper.attachToRecyclerView(recyclerView)

        viewModel.tasks.observe(viewLifecycleOwner) { tasks ->
            adapter.submitList(tasks)
        }

        viewModel.isHided.observe(viewLifecycleOwner) { isHided ->
            if (isHided) {
                visibilityImg?.setImageResource(R.drawable.ic_visibility_off)
            } else {
                visibilityImg?.setImageResource(R.drawable.ic_visibility_on)
            }
        }

        viewModel.updateList()

        visibilityImg = view.findViewById(R.id.visibilityImg)

        visibilityImg?.setOnClickListener {
            viewModel.toggleVisibility()
        }
    }

    override fun onCheck(task: Task) {
        viewModel.checkTask(task)
    }

    override fun onItemClick(task: Task) {
        findNavController().navigate(R.id.action_TasksListFragment_to_EditTaskFragment)
    }

    override fun onDelete(task: Task) {
        viewModel.deleteTask(task)
    }
}