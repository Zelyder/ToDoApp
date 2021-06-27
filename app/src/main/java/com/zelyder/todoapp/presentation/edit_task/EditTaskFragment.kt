package com.zelyder.todoapp.presentation.edit_task

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.google.android.material.switchmaterial.SwitchMaterial
import com.zelyder.todoapp.R
import com.zelyder.todoapp.domain.enums.EditScreenExitStatus
import com.zelyder.todoapp.domain.enums.Importance
import com.zelyder.todoapp.domain.models.Task
import com.zelyder.todoapp.presentation.core.Dialogs
import com.zelyder.todoapp.presentation.core.formatDate
import com.zelyder.todoapp.viewModelFactoryProvider

class EditTaskFragment : Fragment() {

    private var imgClose: ImageView? = null
    private var tvSave: TextView? = null
    private var editText: EditText? = null
    private var importanceText: TextView? = null
    private var deadlineText: TextView? = null
    private var importanceLayout: LinearLayout? = null
    private var deadlineLayout: LinearLayout? = null
    private var deadlineSwitch: SwitchMaterial? = null
    private var tvDelete: TextView? = null

    lateinit var task: Task

    private val args: EditTaskFragmentArgs by navArgs()
    private val viewModel: EditTaskViewModel by viewModels { viewModelFactoryProvider().viewModelFactory() }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_edit_task, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        imgClose = view.findViewById(R.id.edit_task_img_close)
        tvSave = view.findViewById(R.id.edit_task_tv_save)
        editText = view.findViewById(R.id.edit_task_et_text)
        importanceText = view.findViewById(R.id.edit_task_importance_text)
        deadlineText = view.findViewById(R.id.edit_task_deadline_text)
        importanceLayout = view.findViewById(R.id.edit_task_importance)
        deadlineLayout = view.findViewById(R.id.edit_task_deadline)
        deadlineSwitch = view.findViewById(R.id.edit_task_deadline_switch)
        tvDelete = view.findViewById(R.id.edit_task_tv_delete)

        viewModel.importance.observe(viewLifecycleOwner) {
            task.importance = it
            when (it) {
                Importance.NONE -> {
                    importanceText?.text =
                        resources.getString(R.string.importance_no)
                    importanceText?.setTextColor(
                        ContextCompat.getColor(
                            requireContext(),
                            R.color.gray
                        )
                    )
                }
                Importance.LOW -> {
                    importanceText?.text =
                        resources.getString(R.string.importance_low)
                    importanceText?.setTextColor(
                        ContextCompat.getColor(
                            requireContext(),
                            R.color.gray
                        )
                    )
                }
                Importance.HIGH -> {
                    importanceText?.text = resources.getString(R.string.importance_high)
                    importanceText?.setTextColor(
                        ContextCompat.getColor(
                            requireContext(),
                            R.color.red_light
                        )
                    )
                }
            }
        }

        viewModel.deadline.observe(viewLifecycleOwner) {
            if (it != null) {
                task.date = it
                deadlineText?.text = it
                deadlineSwitch?.isChecked = true
            } else {
                task.date = null
                deadlineText?.text = ""
                deadlineSwitch?.isChecked = false
            }
        }

        if (!args.isNewTask && args.task != null) {
            task = args.task!!
            editText?.setText(task.text)
            viewModel.setImportance(task.importance)
            if (task.date != null) {
                viewModel.setDeadline(task.date)
            }
        } else {
            task = Task(text = "")
            tvDelete?.isActivated = false
            tvDelete?.setTextColor(
                ContextCompat.getColor(
                    requireContext(),
                    R.color.gray
                )
            )
        }

        importanceLayout?.setOnClickListener {
            val popupView = LayoutInflater.from(requireContext()).inflate(
                R.layout.popup_importance,
                null,
                false
            )

            val popupWindow = PopupWindow(
                popupView,
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT,
                true
            )
            popupWindow.showAsDropDown(importanceLayout)

            popupView.findViewById<TextView>(R.id.popup_imp_no).setOnClickListener {
                viewModel.setImportance(Importance.NONE)
                popupWindow.dismiss()
            }

            popupView.findViewById<TextView>(R.id.popup_imp_low).setOnClickListener {
                viewModel.setImportance(Importance.LOW)
                popupWindow.dismiss()
            }

            popupView.findViewById<TextView>(R.id.popup_imp_high).setOnClickListener {
                viewModel.setImportance(Importance.HIGH)
                popupWindow.dismiss()
            }
        }

        imgClose?.setOnClickListener {
            findNavController().navigateUp()
        }

        deadlineLayout?.setOnClickListener {
            updateDeadline()
        }

        deadlineSwitch?.setOnClickListener {
            if (deadlineSwitch?.isChecked == false) {
                viewModel.setDeadline(null)
            } else {
                updateDeadline()
            }
        }
        tvDelete?.setOnClickListener {
            findNavController().navigate(
                EditTaskFragmentDirections.actionEditTaskFragmentToTasksListFragment(
                    EditScreenExitStatus.DELETE,
                    task
                )
            )
        }
        tvSave?.setOnClickListener {
            if (!editText?.text.isNullOrEmpty()) {
                task.text = editText?.text.toString()
            }

            findNavController().navigate(
                EditTaskFragmentDirections.actionEditTaskFragmentToTasksListFragment(
                    if (args.isNewTask) EditScreenExitStatus.ADD else EditScreenExitStatus.EDIT,
                    task
                )
            )
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        imgClose = null
        tvSave = null
        editText = null
        importanceText = null
        deadlineText = null
        importanceLayout = null
        deadlineLayout = null
        deadlineSwitch = null
        tvDelete = null
    }

    private fun updateDeadline() {
        val dialogs = Dialogs()
        dialogs.openDatePicker(requireContext()) {
            viewModel.setDeadline(
                formatDate(
                    dialogs.savedDay,
                    dialogs.savedMonth + 1,
                    dialogs.savedYear
                )
            )
        }
    }
}