package me.foxaice.smartlight.fragments.controllers_screen.controller_management.view.dialogs.execution_task;

import android.widget.TextView;

class TaskHolder {
    private CheckMarkProgressView mCheckMark;
    private TextView mTaskText;

    TaskHolder(CheckMarkProgressView checkMark, TextView taskText) {
        this.mCheckMark = checkMark;
        this.mTaskText = taskText;
    }

    void setText(String text) {
        mTaskText.setText(text);
    }

    void startAnimation() {
        mCheckMark.startAnimation();
    }

    void setActionAfterCheck(Runnable action) {
        mCheckMark.setActionAfterCheck(action);
    }

    void onCheck() {
        mCheckMark.post(new Runnable() {
            @Override
            public void run() {
                mCheckMark.onCheck();
            }
        });
    }
}
