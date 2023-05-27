package com.bawp.todoister.adapter;

import com.bawp.todoister.model.Task;

public interface OnTodoClickListner {
    void onTodoClick( Task task);
    void onTodoRadioButtonClick(Task task);
}
