package com.zelyder.todoapp.presentation.tasks_list

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.core.widget.NestedScrollView
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
    var tvDoneCount: TextView? = null
    var nestedScrollView: NestedScrollView? = null

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

        tvDoneCount = view.findViewById(R.id.tvDoneCount)
        nestedScrollView = view.findViewById(R.id.nestedScrollView)

        val rvLayoutManager = LinearLayoutManager(view.context)
        recyclerView = view.findViewById<RecyclerView>(R.id.rvTasksList).apply {
            layoutManager = rvLayoutManager
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

        viewModel.doneCount.observe(viewLifecycleOwner) {
            tvDoneCount?.text = resources.getString(R.string.done_n, it)
        }
        if (savedInstanceState == null) {
            viewModel.updateList()
        }

        visibilityImg = view.findViewById(R.id.visibilityImg)

        visibilityImg?.setOnClickListener {
            viewModel.toggleVisibility()
        }

        view.findViewById<AppBarLayout>(R.id.tasks_list_appbar).setOnClickListener {
            nestedScrollView?.fullScroll(View.FOCUS_UP)
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
        tvDoneCount = null
        nestedScrollView = null
    }

    override fun onCheck(task: Task) {
        viewModel.checkTask(task)
    }

    override fun onEdit(task: Task) {
        viewModel.editTask(task)
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