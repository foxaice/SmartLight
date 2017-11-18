package me.foxaice.smartlight.fragments.controllers_screen.controller_management.view.dialogs.execution_task;

import android.animation.LayoutTransition;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Queue;

import me.foxaice.smartlight.R;
import me.foxaice.smartlight.utils.AnimationUtils;
import me.foxaice.smartlight.utils.TextUtils;

import static me.foxaice.smartlight.fragments.controllers_screen.controller_management.view.IControllerManagementView.Tasks;

public class ExecutionTaskDialog extends DialogFragment {
    private static final String EXTRA_TASK_ID = "EXTRA_TASK_ID";
    private static final String TAG = "TAG_EXECUTION_DIALOG";
    private static final int REQUEST_CODE = 300;
    private Queue<String> mTasks;
    private int mExecutionID;
    private ArrayList<TaskHolder> mTaskViewList = new ArrayList<>();
    private DialogListener mDialogListener;
    private LinearLayout mRoot;
    private ViewHandler mViewHandler;

    public interface DialogListener {
        void onTaskExecute(ExecutionTaskDialog dialog);
        void onExecutionTaskCompleted(boolean successful);
        void onStartNextSubTask();
        void onDialogDismissed();
    }

    public static void showDialog(Fragment fragment, int taskId) {
        ExecutionTaskDialog dialog = new ExecutionTaskDialog();
        dialog.setArguments(new Intent().putExtra(EXTRA_TASK_ID, taskId).getExtras());
        dialog.setTargetFragment(fragment, REQUEST_CODE);
        dialog.show(fragment.getFragmentManager(), ExecutionTaskDialog.TAG);
    }

    private static String[] getStringsByTaskId(Context context, int taskID) {
        int firstTaskId = R.string.task_connect_to_device;
        int secondTaskId;
        int lastTaskId = R.string.task_restart_device;
        switch (taskID) {
            case Tasks.DISCONNECT_TASK:
                secondTaskId = R.string.disconnect_from_network;
                break;
            case Tasks.CHANGE_PASSWORD_TASK:
                secondTaskId = R.string.task_change_password;
                break;
            case Tasks.CONNECT_TO_NETWORK_TASK:
                secondTaskId = R.string.task_connect_to_network;
                break;
            case Tasks.CONNECT_TO_SAVED_NETWORK_TASK:
                return getStringsByTaskId(context, Tasks.CONNECT_TO_NETWORK_TASK);
            case Tasks.FORGET_NETWORK:
                return getStringsByTaskId(context, Tasks.CONNECT_TO_NETWORK_TASK);
            default:
                throw new IllegalArgumentException("Wrong the Task Name");
        }
        return TextUtils
                .getStringArrayFromResources(context, firstTaskId, secondTaskId, lastTaskId);
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        if (savedInstanceState != null) dismiss();
        loadArguments(getArguments());
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
        mViewHandler.removeAllCallbacksAndMessages();
        mDialogListener.onDialogDismissed();
    }

    public int getExecutionTaskID() {
        return mExecutionID;
    }

    public void setSubTaskComplete(int position) {
        mViewHandler.sendUpdateListMessage(position);
    }

    public void removeCallbacksAndMessages() {
        if (mViewHandler != null) mViewHandler.removeAllCallbacksAndMessages();
    }

    TaskHolder getLastTaskHolder() {
        return mTaskViewList.get(mTaskViewList.size() - 1);
    }

    boolean isTaskQueueEmpty() {
        return mTasks.isEmpty();
    }

    void executionTaskCompleted(boolean successful) {
        mDialogListener.onExecutionTaskCompleted(successful);
    }

    Runnable getNextTaskRunnable() {
        return new Runnable() {
            @Override
            public void run() {
                runNextTask(mRoot);
            }
        };
    }

    private void loadArguments(Bundle args) {
        if (args != null) {
            mExecutionID = args.getInt(EXTRA_TASK_ID);
            mRoot = (LinearLayout) getContentView();
            if (getTargetFragment() instanceof DialogListener) {
                mDialogListener = (DialogListener) getTargetFragment();
            }
            mViewHandler = new ViewHandler(this);
        }
    }

    private View getContentView() {
        LinearLayout root = (LinearLayout) View.inflate(getContext(), R.layout.fragment_controller_management_execution_task_dialog, null);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            root.getLayoutTransition().enableTransitionType(LayoutTransition.CHANGING);
        }
        mTasks = new ArrayDeque<>(Arrays.asList(ExecutionTaskDialog.getStringsByTaskId(this.getContext(), mExecutionID)));
        return root;
    }

    private void runNextTask(final ViewGroup viewGroup) {
        if (!mTasks.isEmpty()) {
            final View view = View.inflate(getContext(), R.layout.item_execution_task_list, null);
            final TaskHolder taskHolder = new TaskHolder(
                    (CheckMarkProgressView) view.findViewById(R.id.fragment_controller_management_dialog_view_checkmark),
                    (TextView) view.findViewById(R.id.fragment_controller_management_dialog_text_task_name));
            taskHolder.setText(mTasks.poll());
            mTaskViewList.add(taskHolder);
            final LinearLayout.LayoutParams footerLayoutParams = (LinearLayout.LayoutParams) viewGroup.getChildAt(viewGroup.getChildCount() - 1).getLayoutParams();
            float temp = footerLayoutParams.topMargin != 0 ? 0 : getResources().getDimension(R.dimen.check_mark_progress_size);
            Animation a = AnimationUtils.getChangeViewGroupHeightAnimation(viewGroup, temp);
            a.setDuration(200);
            a.setAnimationListener(new AnimationUtils.AnimationListenerAdapter() {
                @Override
                public void onAnimationStart(Animation animation) {
                    footerLayoutParams.topMargin = 0;
                }

                @Override
                public void onAnimationEnd(Animation animation) {
                    viewGroup.addView(view);
                    viewGroup.post(new Runnable() {
                        @Override
                        public void run() {
                            taskHolder.startAnimation();
                            if (mTaskViewList.size() == 1) {
                                mDialogListener.onTaskExecute(ExecutionTaskDialog.this);
                            } else {
                                mDialogListener.onStartNextSubTask();
                            }
                        }
                    });
                }
            });
            viewGroup.startAnimation(a);
        }
    }
}

