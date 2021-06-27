package com.zelyder.todoapp.presentation.tasks_list

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.appbar.AppBarLayout
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.zelyder.todoapp.R
import com.zelyder.todoapp.domain.enums.EditScreenExitStatus
import com.zelyder.todoapp.domain.models.Task
import com.zelyder.todoapp.viewModelFactoryProvider

class TasksListFragment : Fragment(), TasksListItemClickListener {
    private val viewModel: TasksListViewModel by viewModels { viewModelFactoryProvider().viewModelFactory() }

    var recyclerView: RecyclerView? = null
    var visibilityImg: ImageView? = null

    private val args: TasksListFragmentArgs by navArgs()

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

        view.findViewById<FloatingActionButton>(R.id.fab).setOnClickListener {
            findNavController().navigate(
                TasksListFragmentDirections.actionTasksListFragmentToEditTaskFragment(
                    true
                )
            )
        }

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
        if (savedInstanceState == null) {
            viewModel.updateList()
        }

        visibilityImg = view.findViewById(R.id.visibilityImg)

        visibilityImg?.setOnClickListener {
            viewModel.toggleVisibility()
        }

        view.findViewById<AppBarLayout>(R.id.tasks_list_appbar).setOnClickListener {
            recyclerView?.scrollToPosition(0)
        }

        if(args.editScreenExitStatus != EditScreenExitStatus.NONE){
            when(args.editScreenExitStatus) {
                EditScreenExitStatus.EDIT -> args.taskFromEditScreen?.let {
                    viewModel.editTask(it)
                }
                EditScreenExitStatus.ADD -> args.taskFromEditScreen?.let {
                    viewModel.addTask(it)
                }
                EditScreenExitStatus.DELETE -> args.taskFromEditScreen?.let {
                    viewModel.deleteTask(it)
                }
                else -> throw IllegalArgumentException()
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        recyclerView = null
        visibilityImg = null
    }

    override fun onCheck(task: Task) {
        viewModel.checkTask(task)
    }

    override fun onItemClick(task: Task) {
        findNavController().navigate(
            TasksListFragmentDirections.actionTasksListFragmentToEditTaskFragment(
                false, task
            )
        )
    }

    override fun onDelete(task: Task) {
        viewModel.deleteTask(task)
    }
}