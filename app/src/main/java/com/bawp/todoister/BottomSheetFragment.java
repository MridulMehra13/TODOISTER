package com.bawp.todoister;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CalendarView;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import com.bawp.todoister.model.Priority;
import com.bawp.todoister.model.SharedViewModel;
import com.bawp.todoister.model.Task;
import com.bawp.todoister.model.TaskViewModel;
import com.bawp.todoister.util.Utils;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.google.android.material.chip.Chip;
import com.google.android.material.snackbar.Snackbar;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.Group;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.fragment.NavHostFragment;

import java.util.Calendar;
import java.util.Date;

public class BottomSheetFragment extends BottomSheetDialogFragment implements View.OnClickListener{
    private EditText enterTodo;
    private ImageButton calenderButton;
    private ImageButton priorityButton;
    private RadioGroup priorityRadioGroup;
    private RadioButton selectRadioButton;
    private int selectButtonId;
    private ImageButton saveButton;
    private CalendarView calendarView;
    private Group calenderGroup;
    private Date dueData;
    Calendar calendar = Calendar.getInstance();
    private SharedViewModel sharedViewModel;
    private boolean isEdit;
    private Priority priority;

    public BottomSheetFragment() {

    }

    @Override
    public View onCreateView(
            LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.bottom_sheet, container, false);
        calenderGroup = view.findViewById(R.id.calendar_group);
        calendarView = view.findViewById(R.id.calendar_view);
        calenderButton = view.findViewById(R.id.today_calendar_button);
        enterTodo = view.findViewById(R.id.enter_todo_et);
        saveButton = view.findViewById(R.id.save_todo_button);
        priorityButton = view.findViewById(R.id.priority_todo_button);
        priorityRadioGroup = view.findViewById(R.id.radioGroup_priority);

        Chip todayChip = view.findViewById(R.id.today_chip);
        todayChip.setOnClickListener(this);
        Chip tomorrowChip = view.findViewById(R.id.tomorrow_chip);
        tomorrowChip.setOnClickListener(this);
        Chip nextWeekChip = view.findViewById(R.id.next_week_chip);
        return view;
    }
//Your bottomfrag. will not show same thing
    @Override
    public void onResume() {
        super.onResume();
        if(sharedViewModel.getSelectedItem().getValue() != null)
        {
            isEdit = sharedViewModel.getIsEdit();
            Task task = sharedViewModel.getSelectedItem().getValue();
            enterTodo.setText(task.getTask());
        }
    }

    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        sharedViewModel = new ViewModelProvider(requireActivity())
                .get(SharedViewModel.class);



        calenderButton.setOnClickListener(view12 -> {
            calenderGroup.setVisibility(calenderGroup.getVisibility() == View.GONE ? View.VISIBLE : View.GONE);
            Utils.hideSoftKeyboard(view12);

        });
        calendarView.setOnDateChangeListener((calendarView, year, month, dayOfMonth) -> {
            calendar.clear();
            calendar.set(year,month,dayOfMonth);
            dueData = calendar.getTime();

        });

        priorityButton.setOnClickListener(view13 -> {

            Utils.hideSoftKeyboard(view13);
            priorityRadioGroup.setVisibility(priorityRadioGroup.getVisibility() == View.GONE ? View.VISIBLE : View.GONE);
            priorityRadioGroup.setOnCheckedChangeListener((radioGroup, checkedId) -> {
                if(priorityRadioGroup.getVisibility() == View.VISIBLE) {
                    selectButtonId = checkedId;
                    selectRadioButton = view.findViewById(selectButtonId);
                    if (selectRadioButton.getId() == R.id.radioButton_high) {
                        priority = Priority.HIGH;
                    } else if (selectRadioButton.getId() == R.id.radioButton_med) {
                        priority = Priority.MEDIUM;
                    } else if (selectRadioButton.getId() == R.id.radioButton_low) {
                        priority = Priority.LOW;
                    }else {
                        priority = Priority.LOW;
                    }
                }else {
                    priority = Priority.LOW;
                }
            });
        });


//condition is that we can create new task
        //
        saveButton.setOnClickListener(view1 -> {
            String task = enterTodo.getText().toString().trim();
            if(!TextUtils.isEmpty(task) && dueData!= null && priority != null)
            {
                Task myTask = new Task(task, priority,
                        dueData, Calendar.getInstance().getTime(), false);

                //update on prev task
                if(isEdit)
                {
                    Task updateTask = sharedViewModel.getSelectedItem().getValue();
                    updateTask.setTask(task);
                    updateTask.setDateCreated(Calendar.getInstance().getTime());
                    updateTask.setPriority(priority);
                    updateTask.setDueDate(dueData);
                    TaskViewModel.update(updateTask);
                    sharedViewModel.setIsEdit(false);


                }else {
                    TaskViewModel.insert(myTask);
                }
                enterTodo.setText("");

                if(this.isVisible())
                {
                    this.dismiss();
                }
            }else {
                Snackbar.make(saveButton,R.string.empty_field, Snackbar.LENGTH_SHORT).show();
            }
        });

    }

    @Override
    public void onClick(View view) {
        int id = view.getId();
        if(id == R.id.today_chip)
        {
            calendar.add(Calendar.DAY_OF_YEAR,0);
            dueData = calendar.getTime();
        } else if (id == R.id.tomorrow_chip)
        {
            calendar.add(Calendar.DAY_OF_YEAR,1);
            dueData = calendar.getTime();

        } else if (id == R.id.next_week_chip) {
            calendar.add(Calendar.DAY_OF_YEAR,7);
            dueData = calendar.getTime();

        }
    }
}