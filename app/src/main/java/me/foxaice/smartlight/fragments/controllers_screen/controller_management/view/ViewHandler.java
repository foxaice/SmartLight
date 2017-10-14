package me.foxaice.smartlight.fragments.controllers_screen.controller_management.view;

import android.os.Handler;
import android.os.Message;
import android.widget.Toast;

import java.lang.ref.WeakReference;

import me.foxaice.smartlight.fragments.controllers_screen.controller_management.view.dialogs.ExecutionTaskDialog;

class ViewHandler extends Handler {
    static final int START_CONNECTION = 0x0001;
    static final int IS_CONNECTED = 0x0002;
    static final int FAILED_CONNECTION = 0x0004;
    static final int SUB_TASK_COMPLETED = 0x0005;
    static final int EXECUTION_TASK_COMPLETED = 0x0006;
    private static final int DELAY = 150;
    private static final int MAX_SEARCHING_SECONDS = 10000;
    private static final int IS_CONNECTING = 0x0003;
    private final WeakReference<ControllerManagementFragment> wrFragment;
    private ControllerManagementFragment mFragment;

    ViewHandler(ControllerManagementFragment fragment) {
        wrFragment = new WeakReference<>(fragment);
    }

    @Override
    public void handleMessage(Message msg) {
        mFragment = wrFragment.get();
        if (mFragment != null) {
            switch (msg.what) {
                case START_CONNECTION:
                    onReceivedMessageStartConnection();
                    break;
                case IS_CONNECTING:
                    onReceivedMessageIsConnecting(msg);
                    break;
                case IS_CONNECTED:
                    onReceivedMessageIsConnected();
                    break;
                case FAILED_CONNECTION:
                    onReceivedMessageFailedConnection();
                    break;
                case SUB_TASK_COMPLETED:
                    onReceivedMessageSubTaskComplete(msg);
                    break;
                case EXECUTION_TASK_COMPLETED:
                    onReceivedMessageExecutionTaskCompleted(msg);
            }
        }
    }

    private void onReceivedMessageExecutionTaskCompleted(Message msg) {
        ExecutionTaskDialog dialog = mFragment.getTaskDialog();
        if (dialog != null) {
            if (msg.arg1 == 1) {
                dialog.removeCallbacksAndMessages();
                Toast.makeText(dialog.getContext(), "SUCCESS", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(dialog.getContext(), "FAILED", Toast.LENGTH_SHORT).show();
            }
            this.postDelayed(new Runnable() {
                @Override
                public void run() {
                    if (mFragment.getActivity() != null) {
                        mFragment.getActivity().finish();
                    }
                }
            }, 1500);
        }
    }

    private void onReceivedMessageSubTaskComplete(Message msg) {
        if (mFragment.getTaskDialog() != null) {
            mFragment.getTaskDialog().setSubTaskComplete(msg.arg1);
        }
    }

    private void onReceivedMessageStartConnection() {
        this.sendMessageDelayed(this.obtainMessage(IS_CONNECTING, 0, 0, new StringBuilder()), DELAY);
        mFragment.getPresenter().startSearchDeviceTask();
    }

    private void onReceivedMessageIsConnecting(Message msg) {
        int milliseconds = msg.arg1;
        int dotsCounter = msg.arg2;
        StringBuilder sb = (StringBuilder) msg.obj;
        sb = getStringBuilderWithDots(sb, dotsCounter++, true);
        if (dotsCounter == 12) dotsCounter = 0;
        milliseconds += DELAY;
        mFragment.showIsSearchingContent(sb);
        if (milliseconds >= MAX_SEARCHING_SECONDS) {
            this.sendEmptyMessage(FAILED_CONNECTION);
        } else {
            this.sendMessageDelayed(this.obtainMessage(IS_CONNECTING, milliseconds, dotsCounter, sb), DELAY);
        }
    }

    private void onReceivedMessageIsConnected() {
        this.removeMessages(IS_CONNECTING);
        mFragment.showSuccessfulConnectionContent();
    }

    private void onReceivedMessageFailedConnection() {
        this.removeMessages(IS_CONNECTING);
        mFragment.showFailedConnectionContent();
    }

    private StringBuilder getStringBuilderWithDots(StringBuilder sb, int i, boolean firstEnterRecursive) {
        if (sb == null) return null;
        boolean reverse = false;
        if (firstEnterRecursive) sb.delete(0, sb.length());
        if (i == 0) {
            sb.append(' ').append('.');
        } else if (i >= 1 && i <= 3) {
            getStringBuilderWithDots(sb, --i, false).append(' ').append('.');
        } else if (i >= 4 && i <= 9) {
            getStringBuilderWithDots(sb, (6 - i) > 0 ? 6 - i : i - 6, false);
            reverse = true;
        } else if (i >= 10 && i <= 12) {
            getStringBuilderWithDots(sb, 12 - i, false);
        }
        if (firstEnterRecursive) while (sb.length() < 9) sb.append(' ');
        return reverse ? sb.reverse() : sb;
    }
}
