package me.foxaice.smartlight.fragments.controllers_screen.controller_management.presenter;

import java.io.IOException;
import java.util.concurrent.atomic.AtomicInteger;

import me.foxaice.controller_api.IAdminControllerApi;
import me.foxaice.smartlight.utils.Validator;

class CommandSender implements Runnable {
    static final int TASK_SEARCH_DEVICE = 0x0001;
    static final int TASK_DISCONNECT = 0x0002;
    static final int TASK_CHANGE_PASSWORD = 0x0003;
    static final int TASK_CONNECT_TO_NETWORK = 0x0004;
    static final int TASK_CONNECT_TO_SAVED_NETWORK = 0x0005;
    static final int TASK_SCAN_NETWORKS = 0x0006;
    static final int TASK_FORGET_NETWORK = 0x0007;
    private static final String WRONG_SUB_TASK = "WRONG SUB TASK!";
    private static final String FORGET_STA_SSID = "ssidIsNull";
    private static final String FORGET_STA_KEY = "OPEN,NONE";
    private final IAdminControllerApi mAdminControllerApi;
    private final ControllerManagementPresenter mPresenter;
    private final AtomicInteger mRemainingTasks;
    private final int mTask;
    private boolean isPortFound;
    private boolean isModeFound;
    private boolean isPassAPSet;
    private boolean isControllerFound;
    private boolean isModeSet;
    private boolean isScanDone;
    private boolean isRestarted;
    private boolean isSSIDSTAset;
    private boolean isPassSTASet;
    private boolean isSSIDFound;
    private CharSequence mPass;
    private String mSSIDSTA;

    CommandSender(ControllerManagementPresenter presenter, int task) {
        this.mPresenter = presenter;
        this.mTask = task;
        this.mRemainingTasks = new AtomicInteger(3);
        this.mAdminControllerApi = mPresenter.getAdminControllerApi();
    }

    CommandSender(ControllerManagementPresenter presenter, int task, CharSequence params) {
        this(presenter, task);
        switch (task) {
            case TASK_CHANGE_PASSWORD: {
                mPass = params;
                break;
            }
            case TASK_CONNECT_TO_NETWORK: {
                String[] array = params.toString().split("\\|\\|\\|");
                if (array.length == 2) {
                    mSSIDSTA = array[0];
                    mPass = array[1];
                } else {
                    throw new IllegalArgumentException("Wrong Params!");
                }
            }
        }
    }

    @Override
    public void run() {
        try {
            switch (mTask) {
                case TASK_SEARCH_DEVICE:
                    searchDeviceRun();
                    break;
                case TASK_DISCONNECT:
                    disconnectFromNetworkRun();
                    break;
                case TASK_CHANGE_PASSWORD:
                    changePasswordAPRun();
                    break;
                case TASK_CONNECT_TO_NETWORK:
                    connectToNetworkRun();
                    break;
                case TASK_SCAN_NETWORKS:
                    scanNetworksRun();
                    break;
                case TASK_CONNECT_TO_SAVED_NETWORK:
                    connectToSavedNetworkRun();
                    break;
                case TASK_FORGET_NETWORK:
                    forgetNetworkRun();
                    break;
            }
        } catch (InterruptedException | IOException e) {
            e.printStackTrace();
        }
        mPresenter.stopExecutorService();
    }

    void startNextSubTask() {
        this.mRemainingTasks.decrementAndGet();
    }

    private void changeSTAInfoRun(boolean isSwitchToSTA) throws InterruptedException, IOException {
        ResponseReceiver responseReceiver = new ResponseReceiver(mPresenter.getExecutorService(), mAdminControllerApi);
        while (mRemainingTasks.get() != 0 && isRun()) {
            switch (mRemainingTasks.get()) {
                case 3:
                    if (!isConditionDone(SubTasks.CONNECT_TO_DEVICE)) {
                        startSubTask(SubTasks.CONNECT_TO_DEVICE, responseReceiver);
                        mPresenter.subTaskCompleted(3);
                    }
                    break;
                case 2:
                    if (!isConditionDone(SubTasks.CHANGE_STA_SSID) || !isConditionDone(SubTasks.CHANGE_STA_KEY)) {
                        if (isSwitchToSTA && !isConditionDone(SubTasks.SWITCH_MODE_TO_STA)) {
                            startSubTask(SubTasks.SWITCH_MODE_TO_STA, responseReceiver);
                        }
                        if (!isConditionDone(SubTasks.CHANGE_STA_SSID)) {
                            startSubTask(SubTasks.CHANGE_STA_SSID, responseReceiver);
                        }
                        if (!isConditionDone(SubTasks.CHANGE_STA_KEY)) {
                            startSubTask(SubTasks.CHANGE_STA_KEY, responseReceiver);
                        }
                        if (isConditionDone(SubTasks.CHANGE_STA_SSID) && isConditionDone(SubTasks.CHANGE_STA_KEY)) {
                            mPresenter.subTaskCompleted(2);
                        }
                    }
                    break;
                case 1:
                    if (!isConditionDone(SubTasks.RESTART)) {
                        startSubTask(SubTasks.RESTART, responseReceiver);
                        mPresenter.subTaskCompleted(1);
                    }
                    break;
            }
        }
    }

    private void forgetNetworkRun() throws InterruptedException, IOException {
        mSSIDSTA = FORGET_STA_SSID;
        mPass = FORGET_STA_KEY;
        changeSTAInfoRun(false);
    }

    private void connectToSavedNetworkRun() throws InterruptedException, IOException {
        ResponseReceiver responseReceiver = new ResponseReceiver(mPresenter.getExecutorService(), mAdminControllerApi);
        while (mRemainingTasks.get() != 0 && isRun()) {
            switch (mRemainingTasks.get()) {
                case 3:
                    if (!isConditionDone(SubTasks.CONNECT_TO_DEVICE)) {
                        startSubTask(SubTasks.CONNECT_TO_DEVICE, responseReceiver);
                        mPresenter.subTaskCompleted(3);
                    }
                    break;
                case 2:
                    if (!isConditionDone(SubTasks.SWITCH_MODE_TO_STA)) {
                        startSubTask(SubTasks.SWITCH_MODE_TO_STA, responseReceiver);
                        mPresenter.subTaskCompleted(2);
                    }
                    break;
                case 1:
                    if (!isConditionDone(SubTasks.RESTART)) {
                        startSubTask(SubTasks.RESTART, responseReceiver);
                        mPresenter.subTaskCompleted(1);
                    }
                    break;
            }
        }
    }

    private void connectToNetworkRun() throws InterruptedException, IOException {
        changeSTAInfoRun(true);
    }

    private void searchDeviceRun() throws InterruptedException, IOException {
        ResponseReceiver responseReceiver = new ResponseReceiver(mPresenter.getExecutorService(), mAdminControllerApi);
        startSubTask(SubTasks.CONNECT_TO_DEVICE, responseReceiver);
        startSubTask(SubTasks.GET_PORT, responseReceiver);
        startSubTask(SubTasks.GET_MODE, responseReceiver);
        mPresenter.searchDeviceTaskIsDone();
    }

    private void scanNetworksRun() throws InterruptedException, IOException {
        mAdminControllerApi.initSocket(48899);
        ResponseReceiver responseReceiver = new ResponseReceiver(mPresenter.getExecutorService(), mAdminControllerApi);
        startSubTask(SubTasks.CONNECT_TO_DEVICE, responseReceiver);
        startSubTask(SubTasks.GET_SSID, responseReceiver);
        startSubTask(SubTasks.SCAN_NETWORKS, responseReceiver);
    }

    private void disconnectFromNetworkRun() throws InterruptedException, IOException {
        ResponseReceiver responseReceiver = new ResponseReceiver(mPresenter.getExecutorService(), mAdminControllerApi);
        while (mRemainingTasks.get() != 0 && isRun()) {
            switch (mRemainingTasks.get()) {
                case 3:
                    if (!isConditionDone(SubTasks.CONNECT_TO_DEVICE)) {
                        startSubTask(SubTasks.CONNECT_TO_DEVICE, responseReceiver);
                        mPresenter.subTaskCompleted(3);
                    }
                    break;
                case 2:
                    if (!isConditionDone(SubTasks.SWITCH_MODE_TO_AP)) {
                        startSubTask(SubTasks.SWITCH_MODE_TO_AP, responseReceiver);
                        mPresenter.subTaskCompleted(2);
                    }
                    break;
                case 1:
                    if (!isConditionDone(SubTasks.RESTART)) {
                        startSubTask(SubTasks.RESTART, responseReceiver);
                        mPresenter.subTaskCompleted(1);
                    }
                    break;
            }
        }
    }

    private void changePasswordAPRun() throws InterruptedException, IOException {
        ResponseReceiver responseReceiver = new ResponseReceiver(mPresenter.getExecutorService(), mAdminControllerApi);
        while (mRemainingTasks.get() != 0 && isRun()) {
            switch (mRemainingTasks.get()) {
                case 3:
                    if (!isConditionDone(SubTasks.CONNECT_TO_DEVICE)) {
                        startSubTask(SubTasks.CONNECT_TO_DEVICE, responseReceiver);
                        mPresenter.subTaskCompleted(3);
                    }
                    break;
                case 2:
                    if (!isConditionDone(SubTasks.CHANGE_AP_KEY)) {
                        startSubTask(SubTasks.CHANGE_AP_KEY, responseReceiver);
                        mPresenter.subTaskCompleted(2);
                    }
                    break;
                case 1:
                    if (!isConditionDone(SubTasks.RESTART)) {
                        startSubTask(SubTasks.RESTART, responseReceiver);
                        mPresenter.subTaskCompleted(1);
                    }
                    break;
            }
        }
    }

    private void startSubTask(int subTask, ResponseReceiver responseReceiver) throws InterruptedException, IOException {
        long delay = 10L;
        int maxMilliseconds = 200;
        int currentTime = 0;
        int tries = 0;
        responseReceiver.clearAllResponses();
        while (isRun() && !isConditionDone(subTask)) {
            if (tries == 3) {
                tries = 0;
                sendSubTaskCommand(subTask, true);
            }
            tries++;
            sendSubTaskCommand(subTask, false);
            do {
                if (isSubTaskResponseReceived(subTask, responseReceiver)) {
                    setConditionDone(subTask, responseReceiver);
                    break;
                } else {
                    Thread.sleep(delay);
                    currentTime += delay;
                    if (currentTime >= maxMilliseconds) {
                        currentTime = 0;
                        break;
                    }
                }
            } while (!isConditionDone(subTask));
        }
    }

    private boolean isSubTaskResponseReceived(int subTask, ResponseReceiver responseReceiver) {
        switch (subTask) {
            case SubTasks.CONNECT_TO_DEVICE:
                return responseReceiver.isLinkWiFiReceived();
            case SubTasks.GET_PORT:
                return responseReceiver.isNETPReceived();
            case SubTasks.GET_MODE:
                return responseReceiver.isModeReceived();
            case SubTasks.SWITCH_MODE_TO_AP:
                return responseReceiver.isModeReceived();
            case SubTasks.SWITCH_MODE_TO_STA:
                return responseReceiver.isModeReceived();
            case SubTasks.CHANGE_STA_SSID:
                return responseReceiver.isSSIDReceived();
            case SubTasks.CHANGE_STA_KEY:
                if (responseReceiver.isError4Received()) {
                    mPresenter.subTaskFailed();
                }
                return responseReceiver.isSTAKeyReceived();
            case SubTasks.RESTART:
                return responseReceiver.isLinkWiFiReceived();
            case SubTasks.SCAN_NETWORKS:
                return responseReceiver.isScanNetworksReceived();
            case SubTasks.CHANGE_AP_KEY:
                if (responseReceiver.isError4Received()) {
                    mPresenter.subTaskFailed();
                }
                return responseReceiver.isAPKeyReceived();
            case SubTasks.GET_SSID:
                return responseReceiver.isSSIDReceived();
            default:
                throw new IllegalArgumentException(WRONG_SUB_TASK);
        }
    }

    private void sendSubTaskCommand(int subTask, boolean isPreSubTask) throws IOException, InterruptedException {
        if (isPreSubTask && subTask != SubTasks.CONNECT_TO_DEVICE && subTask != SubTasks.RESTART) {
            sendSubTaskCommand(SubTasks.CONNECT_TO_DEVICE, false);
        } else {
            switch (subTask) {
                case SubTasks.CONNECT_TO_DEVICE:
                    sendCommand(Commands.QUIT);
                    sendCommand(Commands.LINK_WIFI);
                    sendCommand(Commands.OK);
                    break;
                case SubTasks.GET_PORT:
                    sendCommand(Commands.GET_NETP);
                    break;
                case SubTasks.GET_MODE:
                    sendCommand(Commands.GET_MODE);
                    break;
                case SubTasks.SWITCH_MODE_TO_AP:
                    sendCommand(Commands.AP_MODE);
                    sendCommand(Commands.GET_MODE);
                    break;
                case SubTasks.SWITCH_MODE_TO_STA:
                    sendCommand(Commands.STA_MODE);
                    sendCommand(Commands.GET_MODE);
                    break;
                case SubTasks.CHANGE_AP_KEY:
                    sendCommand(Commands.SET_KEY_AP);
                    sendCommand(Commands.GET_KEY_AP);
                    break;
                case SubTasks.SCAN_NETWORKS:
                    sendCommand(Commands.SCAN_NETWORKS);
                    break;
                case SubTasks.CHANGE_STA_SSID:
                    sendCommand(Commands.SET_SSID_STA);
                    sendCommand(Commands.GET_SSID_STA);
                    break;
                case SubTasks.CHANGE_STA_KEY:
                    sendCommand(Commands.SET_KEY_STA);
                    sendCommand(Commands.GET_KEY_STA);
                    break;
                case SubTasks.GET_SSID:
                    sendCommand(Commands.GET_SSID_STA);
                    break;
                case SubTasks.RESTART:
                    sendCommand(Commands.QUIT);
                    sendCommand(Commands.LINK_WIFI);
                    break;
                default:
                    throw new IllegalArgumentException(WRONG_SUB_TASK);
            }
        }
    }

    private boolean isConditionDone(int subTask) {
        switch (subTask) {
            case SubTasks.CONNECT_TO_DEVICE:
                return isControllerFound;
            case SubTasks.GET_PORT:
                return isPortFound;
            case SubTasks.GET_MODE:
                return isModeFound;
            case SubTasks.SWITCH_MODE_TO_AP:
                return isModeSet;
            case SubTasks.SWITCH_MODE_TO_STA:
                return isModeSet;
            case SubTasks.CHANGE_AP_KEY:
                return isPassAPSet;
            case SubTasks.SCAN_NETWORKS:
                return isScanDone;
            case SubTasks.GET_SSID:
                return isSSIDFound;
            case SubTasks.RESTART:
                return isRestarted;
            case SubTasks.CHANGE_STA_SSID:
                return isSSIDSTAset;
            case SubTasks.CHANGE_STA_KEY:
                return isPassSTASet;
            default:
                throw new IllegalArgumentException(WRONG_SUB_TASK);
        }
    }

    private void setConditionDone(int subTask, ResponseReceiver responseReceiver) throws IOException, InterruptedException {
        switch (subTask) {
            case SubTasks.CONNECT_TO_DEVICE:
                isControllerFound = true;
                break;
            case SubTasks.GET_PORT:
                isPortFound = true;
                mPresenter.getDeviceInfo().setPort(responseReceiver.getResponsePort());
                break;
            case SubTasks.GET_MODE:
                isModeFound = true;
                mPresenter.getDeviceInfo().setMode(responseReceiver.getResponseMode());
                sendCommand(Commands.QUIT);
                break;
            case SubTasks.SWITCH_MODE_TO_AP:
                if (Validator.isModeAP(responseReceiver.getResponseMode())) {
                    isModeSet = true;
                }
                break;
            case SubTasks.SWITCH_MODE_TO_STA:
                if (Validator.isModeSTA(responseReceiver.getResponseMode())) {
                    isModeSet = true;
                }
                break;
            case SubTasks.CHANGE_AP_KEY:
                String[] tempArray = responseReceiver.getResponseAPKey().split(",");
                if ((tempArray[0].equals("OPEN") && mPass.length() == 0) || (tempArray.length > 2 && tempArray[2].equals(mPass.toString()))) {
                    isPassAPSet = true;
                }
                break;
            case SubTasks.CHANGE_STA_KEY:
                if (responseReceiver.getResponseSTAKey().equals(mPass)) {
                    isPassSTASet = true;
                }
                break;
            case SubTasks.CHANGE_STA_SSID:
                if (responseReceiver.getResponseSSID().equals(mSSIDSTA)) {
                    isSSIDSTAset = true;
                }
                break;
            case SubTasks.SCAN_NETWORKS:
                isScanDone = true;
                mPresenter.scanNetworkTaskIsDone(responseReceiver.getResponseScanNetworks(), mSSIDSTA);
                break;
            case SubTasks.GET_SSID:
                isSSIDFound = true;
                mSSIDSTA = responseReceiver.getResponseSSID();
                break;
            case SubTasks.RESTART:
                sendCommand(Commands.OK);
                sendCommand(Commands.RESTART);
                isRestarted = true;
                break;
            default:
                throw new IllegalArgumentException(WRONG_SUB_TASK);
        }
    }

    private boolean isRun() {
        return !mPresenter.getExecutorService().isShutdown();
    }

    private void sendCommand(int command) throws InterruptedException, IOException {
        switch (command) {
            case Commands.OK:
                mAdminControllerApi.sendCommandOk();
                break;
            case Commands.LINK_WIFI:
                mAdminControllerApi.sendCommandLinkWiFi();
                break;
            case Commands.QUIT:
                mAdminControllerApi.sendCommandQuit();
                break;
            case Commands.GET_NETP:
                mAdminControllerApi.sendCommandGetNETP();
                break;
            case Commands.GET_MODE:
                mAdminControllerApi.sendCommandGetMode();
                break;
            case Commands.AP_MODE:
                mAdminControllerApi.sendCommandSetModeAP();
                break;
            case Commands.STA_MODE:
                mAdminControllerApi.sendCommandSetModeSTA();
                break;
            case Commands.GET_SSID_STA:
                mAdminControllerApi.sendCommandGetSSIDSTA();
                break;
            case Commands.SET_SSID_STA:
                mAdminControllerApi.sendCommandSetSSIDSTA(mSSIDSTA);
                break;
            case Commands.SET_KEY_STA:
                mAdminControllerApi.sendCommandSetPasswordSTA(mPass);
                break;
            case Commands.GET_KEY_STA:
                mAdminControllerApi.sendCommandGetPasswordSTA();
                break;
            case Commands.SET_KEY_AP:
                mAdminControllerApi.sendCommandSetPasswordAP(mPass);
                break;
            case Commands.GET_KEY_AP:
                mAdminControllerApi.sendCommandGetPasswordAp();
                break;
            case Commands.SCAN_NETWORKS:
                mAdminControllerApi.sendCommandScanNetworks();
                break;
            case Commands.RESTART:
                mAdminControllerApi.sendCommandRestart();
                break;
        }
        Thread.sleep(100L);
    }

    private interface Commands {
        int OK = 0x0001;
        int LINK_WIFI = 0x0002;
        int GET_NETP = 0x0003;
        int GET_MODE = 0x0004;
        int AP_MODE = 0x0005;
        int STA_MODE = 0x0006;
        int RESTART = 0x0007;
        int SET_KEY_AP = 0x0008;
        int GET_KEY_AP = 0x0009;
        int SET_KEY_STA = 0x000A;
        int GET_KEY_STA = 0x000B;
        int SET_SSID_STA = 0x000C;
        int GET_SSID_STA = 0x000D;
        int SCAN_NETWORKS = 0x000E;
        int QUIT = 0x000F;
    }

    private interface SubTasks {
        int CONNECT_TO_DEVICE = 0x0001;
        int GET_MODE = 0x0002;
        int GET_PORT = 0x0003;
        int CHANGE_AP_KEY = 0x0004;
        int CHANGE_STA_KEY = 0x0005;
        int CHANGE_STA_SSID = 0x0006;
        int SWITCH_MODE_TO_AP = 0x0007;
        int SWITCH_MODE_TO_STA = 0x0008;
        int SCAN_NETWORKS = 0x0009;
        int GET_SSID = 0x000A;
        int RESTART = 0x000B;
    }
}

