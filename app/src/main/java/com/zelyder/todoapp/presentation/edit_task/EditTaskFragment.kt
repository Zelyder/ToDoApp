package com.zelyder.todoapp.presentation.edit_task

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.core.content.ContextCompat
import androidx.core.graphics.drawable.DrawableCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.google.android.material.switchmaterial.SwitchMaterial
import com.zelyder.todoapp.R
import com.zelyder.todoapp.appComponent
import com.zelyder.todoapp.domain.enums.EditScreenExitStatus
import com.zelyder.todoapp.domain.enums.Importance
import com.zelyder.todoapp.domain.models.Task
import com.zelyder.todoapp.presentation.core.Dialogs
import com.zelyder.todoapp.presentation.core.ViewModelFactory
import com.zelyder.todoapp.presentation.core.toDate
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.serialization.ExperimentalSerializationApi
import java.util.*
import javax.inject.Inject


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

    @Inject
    lateinit var viewModelFactory: ViewModelFactory
    private val viewModel: EditTaskViewModel by viewModels { viewModelFactory }

    @ExperimentalCoroutinesApi
    @ExperimentalSerializationApi
    override fun onAttach(context: Context) {
        context.appComponent.inject(this)
        super.onAttach(context)
    }

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

        var tintColor = ContextCompat.getColor(requireContext(), R.color.red_light)

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
                else -> throw IllegalArgumentException()
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
            tvDelete?.isEnabled = false

            tintColor = ContextCompat.getColor(requireContext(), R.color.gray)
            tvDelete?.setTextColor(
                ContextCompat.getColor(
                    requireContext(),
                    R.color.gray
                )
            )
        }

        var drawable = ContextCompat.getDrawable(requireContext(), R.drawable.ic_delete)
        drawable = DrawableCompat.wrap(drawable!!)
        DrawableCompat.setTint(drawable.mutate(), tintColor)
        drawable.setBounds( 0, 0, drawable.intrinsicWidth, drawable.intrinsicHeight)
        tvDelete?.setCompoundDrawables(drawable, null, null, null)

        importanceLayout?.setOnClickListener {
            val popupView = LayoutInflater.from(requireContext()).inflate(
                R.layout.popup_importance,
                importanceLayout,
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
                Calendar.getInstance().run {
                    set(dialogs.savedYear, dialogs.savedMonth, dialogs.savedDay)
                    timeInMillis
                }.toDate()
            )
        }
    }
}