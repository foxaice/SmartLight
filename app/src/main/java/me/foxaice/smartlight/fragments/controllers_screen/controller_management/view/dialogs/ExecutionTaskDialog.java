package me.foxaice.smartlight.fragments.controllers_screen.controller_management.view.dialogs;

import android.animation.LayoutTransition;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.lang.ref.WeakReference;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Queue;

import me.foxaice.smartlight.R;
import me.foxaice.smartlight.fragments.controllers_screen.controller_management.view.custom_views.CheckMarkProgressView;
import me.foxaice.smartlight.fragments.controllers_screen.controller_management.view.IControllerManagementView;
import me.foxaice.smartlight.utils.AnimationUtils;

public class ExecutionTaskDialog extends DialogFragment {
    public static final String EXTRA_TASK_ID = "EXTRA_TASK_ID";
    public static final String TAG = "TAG_EXECUTION_DIALOG";
    private Queue<String> mTasks;
    private int mExecutionID;
    private ArrayList<TaskHolder> mTaskViewList = new ArrayList<>();
    private DialogListener mDialogListener;
    private LinearLayout mRoot;
    private Handler mHandler;

    public interface DialogListener {
        void onTaskExecute(ExecutionTaskDialog dialog);
        void onExecutionTaskCompleted(boolean successful);
        void onStartNextSubTask();
        void onDialogDismissed();
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        if (savedInstanceState != null) dismiss();
        Bundle args = getArguments();
        if (args != null) {
            mExecutionID = args.getInt(EXTRA_TASK_ID);
            mRoot = (LinearLayout) getContentView();
            if (getTargetFragment() instanceof DialogListener) {
                mDialogListener = (DialogListener) getTargetFragment();
            }
            mHandler = new CustomHandler(this);
        }
        return new AlertDialog.Builder(getContext()).setView(mRoot).create();
    }

    @Override
    public void onResume() {
        super.onResume();
        runNextTask(mRoot);
    }

    @Override
    public void onDismiss(DialogInterface dialog) {
        super.onDismiss(dialog);
        mHandler.removeCallbacksAndMessages(null);
        mDialogListener.onDialogDismissed();
    }

    public int getExecutionTaskID() {
        return mExecutionID;
    }

    public void setSubTaskComplete(int position) {
        mHandler.sendMessage(mHandler.obtainMessage(CustomHandler.UPDATE_LIST, position));
    }

    private View getContentView() {
        LinearLayout root = (LinearLayout) View.inflate(getContext(), R.layout.fragment_controller_management_execution_task_dialog, null);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            root.getLayoutTransition().enableTransitionType(LayoutTransition.CHANGING);
        }
        mTasks = new ArrayDeque<>(Arrays.asList(getResourceArrayId(mExecutionID)));
        return root;
    }

    private void runNextTask(final ViewGroup viewGroup) {
        if (!mTasks.isEmpty()) {
            final View view = View.inflate(getContext(), R.layout.item_execution_task_list, null);
            final TaskHolder holder = new TaskHolder();
            holder.checkMark = (CheckMarkProgressView) view.findViewById(R.id.fragment_controller_management_dialog_view_checkmark);
            holder.taskText = (TextView) view.findViewById(R.id.fragment_controller_management_dialog_text_task_name);
            holder.taskText.setText(mTasks.poll());
            mTaskViewList.add(holder);
            float temp = ((LinearLayout.LayoutParams) viewGroup.getChildAt(viewGroup.getChildCount() - 1).getLayoutParams()).topMargin != 0 ? 0 : getResources().getDimension(R.dimen.check_mark_progress_size);
            Animation a = AnimationUtils.getChangeViewGroupHeightAnimation(viewGroup, temp);
            a.setDuration(200);
            a.setAnimationListener(new Animation.AnimationListener() {
                @Override
                public void onAnimationStart(Animation animation) {
                    ((LinearLayout.LayoutParams) viewGroup.getChildAt(viewGroup.getChildCount() - 1).getLayoutParams()).topMargin = 0;
                }

                @Override
                public void onAnimationEnd(Animation animation) {
                    viewGroup.addView(view);
                    holder.checkMark.post(new Runnable() {
                        @Override
                        public void run() {
                            holder.checkMark.startAnimation();
                            if (mTaskViewList.size() == 1) {
                                mDialogListener.onTaskExecute(ExecutionTaskDialog.this);
                            } else {
                                mDialogListener.onStartNextSubTask();
                            }
                        }
                    });
                }

                @Override
                public void onAnimationRepeat(Animation animation) {

                }
            });
            viewGroup.startAnimation(a);
        }
    }

    public void removeCallbacksAndMessages() {
        if (mHandler != null) mHandler.removeCallbacksAndMessages(null);
    }

    private String[] getResourceArrayId(int taskID) {
        switch (taskID) {
            case IControllerManagementView.DISCONNECT_TASK:
                return new String[]{getString(R.string.task_connect_to_device), getString(R.string.task_disconnect_from_network), getString(R.string.task_restart_device)};
            case IControllerManagementView.CHANGE_PASSWORD_TASK:
                return new String[]{getString(R.string.task_connect_to_device), getString(R.string.task_change_password), getString(R.string.task_restart_device)};
            case IControllerManagementView.CONNECT_TO_NETWORK_TASK:
                return new String[]{getString(R.string.task_connect_to_device), getString(R.string.task_connect_to_network), getString(R.string.task_restart_device)};
            case IControllerManagementView.CONNECT_TO_SAVED_NETWORK_TASK:
                return getResourceArrayId(IControllerManagementView.CONNECT_TO_NETWORK_TASK);
            case IControllerManagementView.FORGET_NETWORK:
                return getResourceArrayId(IControllerManagementView.CONNECT_TO_NETWORK_TASK);
        }
        throw new IllegalArgumentException("Wrong the Task Name");
    }

    private static class TaskHolder {
        CheckMarkProgressView checkMark;
        TextView taskText;
    }

    private static class CustomHandler extends Handler {
        private static final int UPDATE_LIST = 0x0000;
        private static final int EXECUTION_FAILED = 0x0001;
        private final WeakReference<ExecutionTaskDialog> wrDialog;

        private CustomHandler(ExecutionTaskDialog taskDialog) {
            this.wrDialog = new WeakReference<>(taskDialog);
        }


        @Override
        public void handleMessage(final Message msg) {
            final ExecutionTaskDialog dialog = wrDialog.get();
            if (dialog != null) {
                if (msg.what == UPDATE_LIST) {
                    final TaskHolder holder = dialog.mTaskViewList.get(dialog.mTaskViewList.size() - 1);
                    if (dialog.mTasks.isEmpty()) {
                        dialog.mDialogListener.onExecutionTaskCompleted(true);
                    } else {
                        holder.checkMark.setActionAfterCheck(new Runnable() {
                            @Override
                            public void run() {
                                dialog.runNextTask(dialog.mRoot);
                            }
                        });
                    }
                    holder.checkMark.post(new Runnable() {
                        @Override
                        public void run() {
                            holder.checkMark.onCheck();
                        }
                    });
                    if ((int) msg.obj == 3) {
                        sendEmptyMessageDelayed(EXECUTION_FAILED, 12345);
                    }
                } else if (msg.what == EXECUTION_FAILED) {
                    dialog.mDialogListener.onExecutionTaskCompleted(false);
                }
            }
        }
    }
}

