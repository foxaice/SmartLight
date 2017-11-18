package me.foxaice.smartlight.fragments.controllers_screen.controller_management.view;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import me.foxaice.smartlight.R;
import me.foxaice.smartlight.fragments.controllers_screen.controller_management.presenter.ControllerManagementPresenter;
import me.foxaice.smartlight.fragments.controllers_screen.controller_management.presenter.IControllerManagementPresenter;
import me.foxaice.smartlight.fragments.controllers_screen.controller_management.view.dialogs.edit_text_dialog.EditTextDialog;
import me.foxaice.smartlight.fragments.controllers_screen.controller_management.view.dialogs.execution_task.ExecutionTaskDialog;
import me.foxaice.smartlight.fragments.controllers_screen.controller_management.view.dialogs.wifi_list.WifiListDialog;
import me.foxaice.smartlight.utils.ViewUtils;

public class ControllerManagementFragment extends Fragment implements IControllerManagementView, EditTextDialog.DialogListener, WifiListDialog.DialogListener, ExecutionTaskDialog.DialogListener {
    private static final String WRONG_TASK_NAME = "Wrong Task Name!";
    private static final String EXTRA_IP = "EXTRA_IP";
    private static final String EXTRA_MAC = "EXTRA_MAC";
    private static final String EXTRA_MAC_WITH_COLONS = "EXTRA_MAC_WITH_COLONS";
    private static final String EXTRA_NAME = "EXTRA_NAME";
    private IControllerManagementPresenter mPresenter = new ControllerManagementPresenter();
    private ViewHandler mViewHandler = new ViewHandler(this);
    private TextView mPortText;
    private TextView mStateText;
    private TextView mFailedToConnectText;
    private TextView mNameControllerText;
    private TextView mIpAddressText;
    private TextView mMacAddressText;
    private TextView mChangeNameButtonText;
    private TextView mChangePasswordOrDisconnectButtonText;
    private TextView mConnectToAnotherNetworkButtonText;
    private ImageView mControllerImage;
    private ExecutionTaskDialog mTaskDialog;
    private WifiListDialog mWifiListDialog;

    public static void attachFragment(AppCompatActivity activity, int containerId, String nameDevice, String ip, String mac, String macWithColons) {
        Fragment fragment = new ControllerManagementFragment();
        Bundle args = formDeviceArgs(nameDevice, ip, mac, macWithColons);
        fragment.setArguments(args);
        activity.getSupportFragmentManager()
                .beginTransaction()
                .setCustomAnimations(R.anim.slide_in_right, R.anim.no_trasition)
                .add(R.id.activity_controllers_screen_frame_layout, fragment)
                .commit();
    }

    private static Bundle formDeviceArgs(String name, String ip, String mac, String macWithColons) {
        return new Intent()
                .putExtra(EXTRA_NAME, name)
                .putExtra(EXTRA_IP, ip)
                .putExtra(EXTRA_MAC, mac)
                .putExtra(EXTRA_MAC_WITH_COLONS, macWithColons)
                .getExtras();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_controller_management, container, false);
        mPresenter.attachView(this);
        initViews(view);
        loadArguments();
        return view;
    }

    private void loadArguments() {
        Bundle args = getArguments();
        if (args != null) {
            mPresenter.setDeviceName(args.getString(EXTRA_NAME));
            mPresenter.setDeviceIP(args.getString(EXTRA_IP));
            mPresenter.setDeviceMAC(args.getString(EXTRA_MAC));

            mNameControllerText.setText(mPresenter.getDeviceName());
            mIpAddressText.setText(getString(R.string.ip_address, mPresenter.getDeviceIP()));
            mMacAddressText.setText(getString(R.string.mac_address, args.getString(EXTRA_MAC_WITH_COLONS)));
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        mPresenter.attachView(this);
        if (mPresenter.getDeviceMode() == null) {
            mViewHandler.sendStartConnectionMessage();
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        mPresenter.stopExecutorService();
        mViewHandler.removeAllCallbacksAndMessages();
        mPresenter.detachView();
    }

    @Override
    public void onSuccessfulConnect() {
        mViewHandler.sendIsConnectingMessage();
    }

    @Override
    public void onUpdateDeviceName() {
        mNameControllerText.setText(mPresenter.getDeviceName());
    }

    @Override
    public void subTaskCompleted(int position) {
        mViewHandler.sendSubTaskCompletedMessage(position);
    }

    @Override
    public void onTaskExecute(ExecutionTaskDialog dialog) {
        mTaskDialog = dialog;
        switch (dialog.getExecutionTaskID()) {
            case Tasks.DISCONNECT_TASK:
                mPresenter.startDisconnectDeviceTask();
                break;
            case Tasks.CHANGE_PASSWORD_TASK:
                mPresenter.startChangePasswordTask();
                break;
            case Tasks.CONNECT_TO_NETWORK_TASK:
                mPresenter.startConnectToNetworkTask();
                break;
            case Tasks.CONNECT_TO_SAVED_NETWORK_TASK:
                mPresenter.startConnectToSavedNetworkTask();
                break;
            case Tasks.FORGET_NETWORK:
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
        mViewHandler.sendExecutionTaskCompletedMessage(successful);
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
    public void onFinishChangeStaPasswordDialog(CharSequence sequence) {
        mPresenter.setParamsForConnectToNetworkTask(sequence);
        showExecutionTaskDialog(Tasks.CONNECT_TO_NETWORK_TASK);
    }

    @Override
    public void onFinishChangeApPasswordDialog(CharSequence sequence) {
        mPresenter.setParamsForChangePasswordAPTask(sequence);
        showExecutionTaskDialog(Tasks.CHANGE_PASSWORD_TASK);
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
        if (security.equals("NONE")) {
            onFinishChangeStaPasswordDialog(ssid + "|||OPEN,NONE");
        } else {
            EditTextDialog.showChangeStaPassword(this, ssid, security);
        }
    }

    @Override
    public void onClickForgetNetwork() {
        showExecutionTaskDialog(Tasks.FORGET_NETWORK);
    }

    @Override
    public void onClickConnectToSavedNetwork() {
        showExecutionTaskDialog(Tasks.CONNECT_TO_SAVED_NETWORK_TASK);
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
        String mode = mPresenter.getDeviceMode();
        String port = mPresenter.getDevicePort();
        mPortText.setText(getString(R.string.port_address, port));

        if (!mode.equals("STA") && !mode.equals("AP")) {
            mViewHandler.sendFailedConnectionMessage();
            return;
        } else {
            setModeContent(mode);
        }
        ViewUtils.setVisibility(View.VISIBLE, mChangePasswordOrDisconnectButtonText, mConnectToAnotherNetworkButtonText, mChangeNameButtonText);
        ButtonsAnimation.animateSuccessfulConnection(getActivity(),
                getConnectionInfoState(mode),
                mStateText,
                mChangeNameButtonText,
                mChangePasswordOrDisconnectButtonText,
                mConnectToAnotherNetworkButtonText);
    }

    void showFailedConnectionContent() {
        ViewUtils.setVisibility(View.VISIBLE, mFailedToConnectText);
        ButtonsAnimation.animateFailedConnection(getActivity(),
                mFailedToConnectText,
                mNameControllerText,
                mControllerImage,
                mPortText,
                mIpAddressText,
                mMacAddressText,
                mStateText
        );
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

    private void showExecutionTaskDialog(int taskId) {
        if (taskId != Tasks.CONNECT_TO_NETWORK_TASK
                && taskId != Tasks.DISCONNECT_TASK
                && taskId != Tasks.CHANGE_PASSWORD_TASK
                && taskId != Tasks.CONNECT_TO_SAVED_NETWORK_TASK
                && taskId != Tasks.FORGET_NETWORK) {
            throw new IllegalArgumentException(WRONG_TASK_NAME);
        }
        ExecutionTaskDialog.showDialog(this, taskId);
    }

    private void setModeContent(String mode) {
        int stringId = 0;
        View.OnClickListener listener = null;
        if ("STA".equals(mode)) {
            stringId = R.string.disconnect_from_network;
            listener = new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    showExecutionTaskDialog(Tasks.DISCONNECT_TASK);
                }
            };
        } else if ("AP".equals(mode)) {
            stringId = R.string.change_password_wifi_network;
            listener = new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    EditTextDialog.showChangeApPassword(ControllerManagementFragment.this);
                }
            };
        }
        if (listener != null) {
            mChangePasswordOrDisconnectButtonText.setText(stringId);
            mChangePasswordOrDisconnectButtonText.setOnClickListener(listener);
            mConnectToAnotherNetworkButtonText.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mWifiListDialog = mPresenter.getDeviceMode().equals("STA")
                            ? WifiListDialog.createAndShowStaDialog(ControllerManagementFragment.this)
                            : WifiListDialog.createAndShowApDialog(ControllerManagementFragment.this);
                }
            });
            mChangeNameButtonText.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    EditTextDialog.showChangeName(ControllerManagementFragment.this);
                }
            });
        }
    }

    private String getConnectionInfoState(String mode) {
        int stringId = 0;
        if ("STA".equals(mode)) {
            stringId = R.string.sta_state_controller;
        } else if ("AP".equals(mode)) {
            stringId = R.string.ap_state_controller;
        }

        if (stringId != 0) {
            String networkName = getNetworkName();
            return getString(stringId, networkName);
        } else {
            return null;
        }
    }

    private String getNetworkName() {
        int maxSymbols = 20;
        StringBuilder networkName = new StringBuilder(mPresenter.getDeviceNetwork());
        if (networkName.length() > maxSymbols) {
            networkName.delete(maxSymbols, networkName.length()).append("...");
        }
        return networkName.toString();
    }
}
