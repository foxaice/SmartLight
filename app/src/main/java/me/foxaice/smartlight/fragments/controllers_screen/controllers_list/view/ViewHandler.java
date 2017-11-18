package me.foxaice.smartlight.fragments.controllers_screen.controllers_list.view;

import android.os.Handler;
import android.os.Message;

import java.lang.ref.WeakReference;

import me.foxaice.smartlight.R;

class ViewHandler extends Handler {
    private static final int START_SEARCH = 0x0001;
    private static final int STOP_SEARCH = 0x0002;
    private static final int UPDATE_LIST_VIEW = 0x0003;
    private static final int WORKING_SEARCH = 0x0004;
    private static final int IDLE_SEARCH = 0x0005;
    private static final int MAX_MILLISECONDS_FOR_IDLE = 5000;
    private static final int MAX_MILLISECONDS_FOR_SEARCHING = 10000;
    private static final int DELAY = 1000;
    private final WeakReference<ControllerListFragment> wrFragment;
    private boolean isSearching;
    private ControllerListFragment mFragment;

    ViewHandler(ControllerListFragment wrFragment) {
        this.wrFragment = new WeakReference<>(wrFragment);
    }

    @Override
    public void handleMessage(Message msg) {
        mFragment = wrFragment.get();
        if (mFragment != null) {
            if (msg.what == UPDATE_LIST_VIEW) {
                onReceivedMessageUpdateListView(msg);
            } else if (msg.what == START_SEARCH) {
                onReceivedMessageStartSearch();
            } else if (msg.what == STOP_SEARCH) {
                onReceivedMessageStopSearch();
            } else if (msg.what == WORKING_SEARCH) {
                onReceivedMessageWorkingSearch(msg);
            } else if (msg.what == IDLE_SEARCH) {
                onReceivedMessageIdleSearch(msg);
            }
        }
    }

    void sendStartSearchMessage() {
        sendEmptyMessage(START_SEARCH);
    }

    void sendStopSearchMessage() {
        sendEmptyMessage(STOP_SEARCH);
    }

    void sendUpdateListViewMessage(String item) {
        sendMessage(obtainMessage(UPDATE_LIST_VIEW, item));
    }

    void removeAllCallbacksAndMessages() {
        mFragment = wrFragment.get();
        if (mFragment != null) mFragment.stopSearch();
        this.removeCallbacksAndMessages(null);
    }

    private void onReceivedMessageIdleSearch(Message msg) {
        int millisecondsCount = msg.arg1;
        if (millisecondsCount >= MAX_MILLISECONDS_FOR_IDLE) {
            this.sendEmptyMessage(START_SEARCH);
        } else {
            String text = mFragment.getString(R.string.no_controllers) +
                    mFragment.getString(R.string.countdown_until_refresh, (MAX_MILLISECONDS_FOR_IDLE - millisecondsCount) / 1000);
            mFragment.setBodyText(text);
            millisecondsCount += msg.arg2;
            this.sendMessageDelayed(this.obtainMessage(IDLE_SEARCH, millisecondsCount, msg.arg2), msg.arg2);
        }
    }

    private void onReceivedMessageStartSearch() {
        if (!isSearching) {
            isSearching = true;
            this.removeMessages(IDLE_SEARCH);
            mFragment.showWifiIsConnected();
            mFragment.startSearch();
            this.sendMessageDelayed(this.obtainMessage(WORKING_SEARCH, 0, DELAY), DELAY);
        }
    }

    private void onReceivedMessageStopSearch() {
        if (isSearching) {
            isSearching = false;
            this.removeMessages(WORKING_SEARCH);
            mFragment.stopSwipeRefresh();
            mFragment.stopSearch();
            if (mFragment.isControllersListEmpty()) {
                mFragment.showNoFindingControllersContent();
                this.sendMessage(this.obtainMessage(IDLE_SEARCH, 0, DELAY));
            }
        }
    }

    private void onReceivedMessageUpdateListView(Message msg) {
        if (msg.obj instanceof String) {
            String controllerResponse = (String) msg.obj;
            if (!mFragment.controllersListContains(controllerResponse)) {
                mFragment.addItemToControllersList(controllerResponse);
            }
        }
    }

    private void onReceivedMessageWorkingSearch(Message msg) {
        int millisecondsCount = msg.arg1 + msg.arg2;
        if (millisecondsCount >= MAX_MILLISECONDS_FOR_SEARCHING) {
            this.sendEmptyMessage(STOP_SEARCH);
        } else {
            this.sendMessageDelayed(this.obtainMessage(WORKING_SEARCH, millisecondsCount, DELAY), DELAY);
        }
    }
}
