package me.foxaice.smartlight.fragments.controllers_screen.controller_management.view;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.graphics.Point;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.AnticipateOvershootInterpolator;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.Interpolator;
import android.view.animation.ScaleAnimation;
import android.view.animation.TranslateAnimation;
import android.widget.ImageView;
import android.widget.TextView;

import me.foxaice.smartlight.R;
import me.foxaice.smartlight.fragments.controllers_screen.controller_management.view.dialogs.EditTextDialog;
import me.foxaice.smartlight.fragments.controllers_screen.controller_management.view.dialogs.ExecutionTaskDialog;
import me.foxaice.smartlight.fragments.controllers_screen.controller_management.view.dialogs.wifilist.WifiListDialog;
import me.foxaice.smartlight.fragments.controllers_screen.controller_management.presenter.ControllerManagementPresenter;
import me.foxaice.smartlight.fragments.controllers_screen.controller_management.presenter.IControllerManagementPresenter;

import static me.foxaice.smartlight.utils.AnimationUtils.setDuration;
import static me.foxaice.smartlight.utils.AnimationUtils.setInterpolator;
import static me.foxaice.smartlight.utils.AnimationUtils.AnimationListenerAdapter;

public class ControllerManagementFragment extends Fragment implements IControllerManagementView, EditTextDialog.DialogListener, WifiListDialog.DialogListener, ExecutionTaskDialog.DialogListener {
    public static final String EXTRA_IP = "EXTRA_IP";
    public static final String EXTRA_MAC = "EXTRA_MAC";
    public static final String EXTRA_MAC_WITH_COLONS = "EXTRA_MAC_WITH_COLONS";
    public static final String EXTRA_NAME = "EXTRA_NAME";
    public static final String WRONG_TASK_NAME = "Wrong Task Name!";
    private IControllerManagementPresenter mPresenter = new ControllerManagementPresenter();
    private Handler mHandler = new ViewHandler(this);
    private TextView mPortText;
    private TextView mStateText;
    private TextView mFailedToConnectText;
    private TextView mNameControllerText;
    private ImageView mControllerImage;
    private TextView mIpAddressText;
    private TextView mMacAddressText;
    private TextView mChangeNameButtonText;
    private TextView mChangePasswordOrDisconnectButtonText;
    private TextView mConnectToAnotherNetworkButtonText;
    private ExecutionTaskDialog mTaskDialog;
    private WifiListDialog mWifiListDialog;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_controller_management, container, false);
        mPresenter.attachView(this);

        initViews(view);

        Bundle args = getArguments();
        if (args != null) {
            mPresenter.setDeviceName(args.getString(EXTRA_NAME));
            mPresenter.setDeviceIP(args.getString(EXTRA_IP));
            mPresenter.setDeviceMAC(args.getString(EXTRA_MAC));

            mNameControllerText.setText(mPresenter.getDeviceName());
            mIpAddressText.setText(getString(R.string.ip_address, mPresenter.getDeviceIP()));
            mMacAddressText.setText(getString(R.string.mac_address, args.getString(EXTRA_MAC_WITH_COLONS)));
        }
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        mPresenter.attachView(this);
        if (mPresenter.getDeviceMode() == null) {
            mHandler.sendEmptyMessage(ViewHandler.START_CONNECTION);
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        mPresenter.stopExecutorService();
        mHandler.removeCallbacksAndMessages(null);
        mPresenter.detachView();
    }

    @Override
    public void onSuccessfulConnect() {
        mHandler.sendEmptyMessage(ViewHandler.IS_CONNECTED);
    }

    @Override
    public void onUpdateDeviceName() {
        mNameControllerText.setText(mPresenter.getDeviceName());
    }

    @Override
    public void subTaskCompleted(int position) {
        mHandler.sendMessage(mHandler.obtainMessage(ViewHandler.SUB_TASK_COMPLETED, position, position));
    }

    @Override
    public void onTaskExecute(ExecutionTaskDialog dialog) {
        mTaskDialog = dialog;
        switch (dialog.getExecutionTaskID()) {
            case DISCONNECT_TASK:
                mPresenter.startDisconnectDeviceTask();
                break;
            case CHANGE_PASSWORD_TASK:
                mPresenter.startChangePasswordTask();
                break;
            case CONNECT_TO_NETWORK_TASK:
                mPresenter.startConnectToNetworkTask();
                break;
            case CONNECT_TO_SAVED_NETWORK_TASK:
                mPresenter.startConnectToSavedNetworkTask();
                break;
            case FORGET_NETWORK:
                mPresenter.startForgetNetworkTask();
                break;
            default:
                throw new IllegalArgumentException(WRONG_TASK_NAME);
        }
    }

    @Override
    public void onStartNextSubTask() {
        mPresenter.startNextSubTask();
    }

    @Override
    public void onExecutionTaskCompleted(boolean successful) {
        Message msg;
        if (successful) {
            msg = mHandler.obtainMessage(ViewHandler.EXECUTION_TASK_COMPLETED, 1, 1);
        } else {
            msg = mHandler.obtainMessage(ViewHandler.EXECUTION_TASK_COMPLETED, 0, 0);
        }
        mHandler.sendMessage(msg);
    }

    @Override
    public void onStartScanNetworksTask() {
        mPresenter.startScanNetworksTask();
    }

    @Override
    public void onFinishChangeNameDialog(String name) {
        mPresenter.setDeviceName(name);
    }

    @Override
    public void onFinishChangePasswordDialog(CharSequence sequence, String tag) {
        switch (tag) {
            case EditTextDialog.TAG_PASS_AP:
                mPresenter.setParamsForChangePasswordAPTask(sequence);
                showExecutionTaskDialog(CHANGE_PASSWORD_TASK);
                break;

            case EditTextDialog.TAG_PASS_STA:
                mPresenter.setParamsForConnectToNetworkTask(sequence);
                showExecutionTaskDialog(CONNECT_TO_NETWORK_TASK);
                break;
        }

    }

    @Override
    public void onDialogDismissed() {
        mPresenter.stopExecutorService();
        mTaskDialog = null;
        mWifiListDialog = null;
    }


    @Override
    public void updateWifiListDialog(String response, String savedNetworkSSID) {
        if (mWifiListDialog != null) {
            mWifiListDialog.updateWifiPointsList(response, savedNetworkSSID);
        }
    }

    @Override
    public void onClickNetwork(String ssid, String security) {
        EditTextDialog dialog = new EditTextDialog();
        Bundle args = new Bundle();
        if (security.equals("NONE")) {
            onFinishChangePasswordDialog(ssid + "|||OPEN,NONE", EditTextDialog.TAG_PASS_STA);
        } else {
            args.putString(EditTextDialog.EXTRA_STA_SSID, ssid);
            args.putString(EditTextDialog.EXTRA_STA_SECURITY, security);
            dialog.setArguments(args);
            dialog.setTargetFragment(ControllerManagementFragment.this, 300);
            dialog.show(getFragmentManager(), EditTextDialog.TAG_PASS_STA);
        }
    }

    @Override
    public void onClickForgetNetwork() {
        showExecutionTaskDialog(FORGET_NETWORK);
    }

    @Override
    public void onClickConnectToSavedNetwork() {
        showExecutionTaskDialog(CONNECT_TO_SAVED_NETWORK_TASK);
    }

    IControllerManagementPresenter getPresenter() {
        return mPresenter;
    }

    ExecutionTaskDialog getTaskDialog() {
        return mTaskDialog;
    }

    void showIsSearchingContent(StringBuilder dots) {
        mPortText.setText(getString(R.string.port_address, dots));
        mStateText.setText(getString(R.string.is_searching, dots));
    }

    void showSuccessfulConnectionContent() {
        int maxSymbols = 20;
        StringBuilder networkName = new StringBuilder(mPresenter.getDeviceNetwork());
        if (networkName.length() > maxSymbols) {
            networkName.delete(maxSymbols, networkName.length()).append("...");
        }
        String mode = mPresenter.getDeviceMode();
        String port = mPresenter.getDevicePort();
        mPortText.setText(getString(R.string.port_address, port));
        String connectionInfoState;

        if ("STA".equals(mode)) {
            connectionInfoState = getString(R.string.sta_state_controller, networkName);
            mChangePasswordOrDisconnectButtonText.setText(R.string.disconnect_from_network);
            mChangePasswordOrDisconnectButtonText.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    showExecutionTaskDialog(DISCONNECT_TASK);
                }
            });
        } else if ("AP".equals(mode)) {
            connectionInfoState = getString(R.string.ap_state_controller, networkName);
            mChangePasswordOrDisconnectButtonText.setText(R.string.change_password_wifi_network);
            mChangePasswordOrDisconnectButtonText.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    EditTextDialog dialog = new EditTextDialog();
                    dialog.setTargetFragment(ControllerManagementFragment.this, 300);
                    dialog.show(getFragmentManager(), EditTextDialog.TAG_PASS_AP);
                }
            });
        } else {
            mHandler.sendEmptyMessage(ViewHandler.FAILED_CONNECTION);
            return;
        }

        mConnectToAnotherNetworkButtonText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mWifiListDialog = new WifiListDialog();
                mWifiListDialog.setTargetFragment(ControllerManagementFragment.this, 300);
                mWifiListDialog.show(
                        getFragmentManager(),
                        mPresenter.getDeviceMode().equals("STA") ? WifiListDialog.TAG_WIFI_LIST_STA : WifiListDialog.TAG_WIFI_LIST_AP
                );
            }
        });
        mChangeNameButtonText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EditTextDialog dialog = new EditTextDialog();
                dialog.setTargetFragment(ControllerManagementFragment.this, 300);
                dialog.show(getFragmentManager(), EditTextDialog.TAG_NAME);
            }
        });
        mChangePasswordOrDisconnectButtonText.setVisibility(View.VISIBLE);
        mConnectToAnotherNetworkButtonText.setVisibility(View.VISIBLE);
        mChangeNameButtonText.setVisibility(View.VISIBLE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH) {
            animateSuccessfulConnection(connectionInfoState);
        } else {
            animateSuccessfulConnectionPreApi14(connectionInfoState);
        }
    }

    void showFailedConnectionContent() {
        mFailedToConnectText.setVisibility(View.VISIBLE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH) {
            animateFailedConnection();
        } else {
            animateFailedConnectionPreApi14();
        }
    }

    private void initViews(View view) {
        mPortText = (TextView) view.findViewById(R.id.fragment_controller_management_text_port);
        mStateText = (TextView) view.findViewById(R.id.fragment_controller_management_text_wifi_management_state);
        mFailedToConnectText = (TextView) view.findViewById(R.id.fragment_controller_management_text_failed_to_connect);
        mNameControllerText = (TextView) view.findViewById(R.id.fragment_controller_management_text_name);
        mControllerImage = (ImageView) view.findViewById(R.id.fragment_controller_management_image_icon);
        mIpAddressText = (TextView) view.findViewById(R.id.fragment_controller_management_text_ip);
        mMacAddressText = (TextView) view.findViewById(R.id.fragment_controller_management_text_mac);
        mChangeNameButtonText = (TextView) view.findViewById(R.id.fragment_controller_management_text_change_name_controller);
        mChangePasswordOrDisconnectButtonText = (TextView) view.findViewById(R.id.fragment_controller_management_text_change_network_password_ap);
        mConnectToAnotherNetworkButtonText = (TextView) view.findViewById(R.id.fragment_controller_management_text_connect_to_another_network);
    }

    private void showExecutionTaskDialog(int taskID) {
        if (taskID != CONNECT_TO_NETWORK_TASK
                && taskID != DISCONNECT_TASK
                && taskID != CHANGE_PASSWORD_TASK
                && taskID != CONNECT_TO_SAVED_NETWORK_TASK
                && taskID != FORGET_NETWORK) {
            throw new IllegalArgumentException(WRONG_TASK_NAME);
        }
        ExecutionTaskDialog dialog = new ExecutionTaskDialog();
        Bundle args = new Bundle();
        args.putInt(ExecutionTaskDialog.EXTRA_TASK_ID, taskID);
        dialog.setArguments(args);
        dialog.setTargetFragment(ControllerManagementFragment.this, 300);
        dialog.show(getFragmentManager(), ExecutionTaskDialog.TAG);
    }

    @RequiresApi(api = Build.VERSION_CODES.ICE_CREAM_SANDWICH)
    private void animateSuccessfulConnection(String connectionInfoState) {
        long animationTime = 1_000;
        Interpolator interpolator = new DecelerateInterpolator();
        animateButton(mChangeNameButtonText, interpolator, animationTime, 0);
        animateButton(mChangePasswordOrDisconnectButtonText, interpolator, animationTime, 100);
        animateButton(mConnectToAnotherNetworkButtonText, interpolator, animationTime, 200);

        animateStateInfo(connectionInfoState, new AnticipateOvershootInterpolator(), animationTime);
    }

    @RequiresApi(api = Build.VERSION_CODES.ICE_CREAM_SANDWICH)
    private void animateButton(View v, Interpolator interpolator, long duration, int delay) {
        Point p = new Point();
        getActivity().getWindowManager().getDefaultDisplay().getSize(p);
        float screenHeight = p.y / 2;
        v.setAlpha(0.0f);
        v.setY(screenHeight);
        v.animate()
                .translationY(0.0f)
                .alpha(1.0f)
                .setInterpolator(interpolator)
                .setDuration(duration)
                .setStartDelay(delay)
                .start();
    }


    @RequiresApi(api = Build.VERSION_CODES.ICE_CREAM_SANDWICH)
    private void animateStateInfo(final String connectionInfoState, final Interpolator interpolator, final long duration) {
        mStateText.animate()
                .alpha(0.0f)
                .setInterpolator(interpolator)
                .setDuration(duration / 2)
                .setListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        mStateText.setX(-mStateText.getMeasuredWidth());
                        mStateText.setText(connectionInfoState);
                        mStateText.setAlpha(0.0f);
                        mStateText.animate()
                                .alpha(1.0f)
                                .translationX(0)
                                .setInterpolator(interpolator)
                                .setDuration(duration / 2)
                                .setListener(null)
                                .start();
                    }
                }).start();
    }

    @TargetApi(10)
    private void animateSuccessfulConnectionPreApi14(final String connectionInfoState) {
        //noinspection deprecation
        float screenHeight = getActivity().getWindowManager().getDefaultDisplay().getHeight();
        final long animationTime = 1_000;
        int x = mChangeNameButtonText.getLeft();

        TranslateAnimation translate = new TranslateAnimation(-mStateText.getMeasuredWidth(), 0.0f, 0.0f, 0.0f);
        AlphaAnimation alpha = new AlphaAnimation(0.0f, 1.0f);
        final AnimationSet stateSetAnimation = new AnimationSet(true);
        stateSetAnimation.addAnimation(translate);
        stateSetAnimation.addAnimation(alpha);
        stateSetAnimation.setFillAfter(true);
        AlphaAnimation alphaReverse = new AlphaAnimation(1.0f, 0.0f);
        alphaReverse.setAnimationListener(new AnimationListenerAdapter() {
            @Override
            public void onAnimationEnd(Animation animation) {
                mStateText.setText(connectionInfoState);
                mStateText.startAnimation(stateSetAnimation);
            }
        });

        translate = new TranslateAnimation(x, x, screenHeight, 0.0f);
        AnimationSet buttonsSetAnimation = new AnimationSet(true);
        buttonsSetAnimation.addAnimation(alpha);
        buttonsSetAnimation.addAnimation(translate);
        buttonsSetAnimation.setFillAfter(true);

        setDuration(animationTime / 2, stateSetAnimation, alphaReverse);
        setDuration(animationTime, buttonsSetAnimation);
        setInterpolator(new AnticipateOvershootInterpolator(), stateSetAnimation, alphaReverse);
        setInterpolator(new DecelerateInterpolator(), buttonsSetAnimation);

        mChangeNameButtonText.startAnimation(buttonsSetAnimation);
        mChangePasswordOrDisconnectButtonText.startAnimation(buttonsSetAnimation);
        mConnectToAnotherNetworkButtonText.startAnimation(buttonsSetAnimation);
        mStateText.startAnimation(alphaReverse);
    }


    @RequiresApi(api = Build.VERSION_CODES.ICE_CREAM_SANDWICH)
    private void animateFailedConnection() {
        Point p = new Point();
        getActivity().getWindowManager().getDefaultDisplay().getSize(p);
        int screenWidth = p.x;
        mFailedToConnectText.setAlpha(0);
        mNameControllerText.animate()
                .scaleXBy(0.1f).scaleYBy(0.1f)
                .translationY(10)
                .setDuration(1400).start();
        mControllerImage.animate()
                .scaleX(1.35f).scaleY(1.35f)
                .translationX(screenWidth / 2.0f - mControllerImage.getMeasuredWidth() / 2.0f)
                .setDuration(1400).start();
        mPortText.animate()
                .translationXBy(200)
                .alpha(0)
                .setDuration(600).start();
        mIpAddressText.animate()
                .translationXBy(200)
                .alpha(0)
                .setDuration(600).start();
        mMacAddressText.animate()
                .translationXBy(200)
                .alpha(0)
                .setDuration(600).start();
        mStateText.animate()
                .translationYBy(300)
                .alpha(0)
                .setDuration(600).start();
        mFailedToConnectText.animate()
                .scaleXBy(0.1f).scaleYBy(0.1f)
                .alpha(1)
                .setStartDelay(400)
                .setDuration(1000).start();
    }

    @TargetApi(10)
    private void animateFailedConnectionPreApi14() {
        int screenWidth = getActivity().findViewById(R.id.toolbar_settings_image_back_arrow).getMeasuredWidth();
        float xName, yName,
                xPort, yPort,
                xIP, yIP,
                xMac, yMac,
                xImage, yImage,
                xState, yState;
        int sizeImage = mControllerImage.getMeasuredWidth();
        int widthName = mNameControllerText.getMeasuredWidth();
        int heightName = mNameControllerText.getMeasuredHeight();
        int widthFailed = mFailedToConnectText.getMeasuredWidth();
        int heightFailed = mFailedToConnectText.getMeasuredHeight();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
            mFailedToConnectText.setAlpha(0);
            xName = mNameControllerText.getX();
            yName = mNameControllerText.getY();
            xPort = mPortText.getX();
            yPort = mPortText.getY();
            xIP = mIpAddressText.getX();
            yIP = mIpAddressText.getY();
            xMac = mMacAddressText.getX();
            yMac = mMacAddressText.getY();
            xImage = mControllerImage.getX();
            yImage = mControllerImage.getY();
            xState = mStateText.getX();
            yState = mStateText.getY();
        } else {
            Animation alpha = new AlphaAnimation(0, 0);
            alpha.setDuration(0);
            alpha.setFillAfter(true);
            mFailedToConnectText.startAnimation(alpha);
            xName = mNameControllerText.getLeft();
            yName = mNameControllerText.getTop();
            xPort = mPortText.getLeft();
            yPort = mPortText.getTop();
            xIP = mIpAddressText.getLeft();
            yIP = mIpAddressText.getTop();
            xMac = mMacAddressText.getLeft();
            yMac = mMacAddressText.getTop();
            xImage = mControllerImage.getLeft();
            yImage = mControllerImage.getTop();
            xState = mStateText.getLeft();
            yState = mStateText.getTop();
        }
        AnimationSet set = new AnimationSet(true);
        Animation alpha = new AlphaAnimation(1.0f, 0.0f);
        Animation scale = new ScaleAnimation(1.0f, 1.1f, 1.0f, 1.1f, widthName / 2, heightName / 2);
        Animation translation = new TranslateAnimation(xName, xName, yName, yName + 10);
        set.addAnimation(scale);
        set.addAnimation(translation);
        set.setDuration(1400);
        set.setFillAfter(true);
        mNameControllerText.startAnimation(set);

        set = new AnimationSet(true);
        scale = new ScaleAnimation(1.0f, 1.35f, 1.0f, 1.35f, sizeImage / 2, sizeImage / 2);
        translation = new TranslateAnimation(xImage, screenWidth / 2.0f - mControllerImage.getMeasuredWidth() / 2.0f, yImage - sizeImage / 2, yImage - sizeImage / 2);
        set.addAnimation(scale);
        set.addAnimation(translation);
        set.setDuration(1400);
        set.setFillAfter(true);
        mControllerImage.startAnimation(set);

        set = new AnimationSet(true);
        translation = new TranslateAnimation(xPort, xPort + 200, yPort, yPort);
        set.addAnimation(alpha);
        set.addAnimation(translation);
        set.setDuration(600);
        set.setFillAfter(true);
        mPortText.startAnimation(set);

        set = new AnimationSet(true);
        translation = new TranslateAnimation(xIP, xIP + 200, yIP, yIP);
        set.addAnimation(alpha);
        set.addAnimation(translation);
        set.setDuration(600);
        set.setFillAfter(true);
        mIpAddressText.startAnimation(set);

        set = new AnimationSet(true);
        translation = new TranslateAnimation(xMac, xMac + 200, yMac, yMac);
        set.addAnimation(alpha);
        set.addAnimation(translation);
        set.setDuration(600);
        set.setFillAfter(true);
        mMacAddressText.startAnimation(set);

        set = new AnimationSet(true);
        translation = new TranslateAnimation(xState, xState + 300, yState, yState);
        set.addAnimation(alpha);
        set.addAnimation(translation);
        set.setDuration(600);
        set.setFillAfter(true);
        mStateText.startAnimation(set);

        set = new AnimationSet(true);
        alpha = new AlphaAnimation(0.0f, 1.0f);
        scale = new ScaleAnimation(1.0f, 1.03f, 1.0f, 1.03f, widthFailed / 2, heightFailed / 2);
        set.addAnimation(alpha);
        set.addAnimation(scale);
        set.setStartTime(400);
        set.setDuration(1000);
        set.setFillAfter(true);
        mFailedToConnectText.startAnimation(set);
    }

}
