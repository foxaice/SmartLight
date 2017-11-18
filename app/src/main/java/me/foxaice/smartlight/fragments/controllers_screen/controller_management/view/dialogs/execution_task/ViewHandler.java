package me.foxaice.smartlight.fragments.controllers_screen.controller_management.view.dialogs.execution_task;

import android.os.Handler;
import android.os.Message;

import java.lang.ref.WeakReference;

class ViewHandler extends Handler {
    private static final int UPDATE_LIST = 0x0000;
    private static final int EXECUTION_FAILED = 0x0001;
    private final WeakReference<ExecutionTaskDialog> wrDialog;

    ViewHandler(ExecutionTaskDialog taskDialog) {
        this.wrDialog = new WeakReference<>(taskDialog);
    }

    @Override
    public void handleMessage(final Message msg) {
        final ExecutionTaskDialog dialog = wrDialog.get();
        if (dialog != null) {
            if (msg.what == UPDATE_LIST) {
                final TaskHolder holder = dialog.getLastTaskHolder();
                if (dialog.isTaskQueueEmpty()) {
                    dialog.executionTaskCompleted(true);
                } else {
                    holder.setActionAfterCheck(dialog.getNextTaskRunnable());
                }
                holder.onCheck();
                if ((int) msg.obj == 3) {
                    sendEmptyMessageDelayed(EXECUTION_FAILED, 12345);
                }
            } else if (msg.what == EXECUTION_FAILED) {
                dialog.executionTaskCompleted(false);
            }
        }
    }

    void sendUpdateListMessage(int position) {
        sendMessage(obtainMessage(UPDATE_LIST, position));
    }

    void removeAllCallbacksAndMessages() {
        removeCallbacksAndMessages(null);
    }
}
