package me.foxaice.smartlight.fragments.controllers_screen.controllers_list.view;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import me.foxaice.smartlight.R;
import me.foxaice.smartlight.activities.controllers_screen.view.ControllersScreenActivity;
import me.foxaice.smartlight.fragments.controllers_screen.controller_management.view.ControllerManagementFragment;
import me.foxaice.smartlight.fragments.controllers_screen.controllers_list.presenter.ControllerListPresenter;
import me.foxaice.smartlight.fragments.controllers_screen.controllers_list.presenter.IControllerListPresenter;


public class ControllerListFragment extends Fragment implements IControllerListView {
    public static final String TAG = "CONTROLLERS_LIST_FRAGMENT";

    private IControllerListPresenter mPresenter = new ControllerListPresenter();
    private List<String> mControllersList = new ArrayList<>();
    private ListView mControllersListView;
    private ControllerListAdapter mControllerListAdapter;
    private TextView mBodyText;
    private TextView mIntentSettingsButtonText;
    private SwipeRefreshLayout mSwipeRefreshLayout;
    private ViewHandler mHandler = new ViewHandler(this);

    private BroadcastReceiver mLocalBroadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            boolean isWifiEnabled = intent.getBooleanExtra(ControllersScreenActivity.EXTRA_IS_WIFI_ENABLED, false);
            boolean isConnected = intent.getBooleanExtra(ControllersScreenActivity.EXTRA_IS_CONNECTED, false);
            mPresenter.onUpdateWifiState(isWifiEnabled, isConnected);
        }
    };

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_controllers_list, container, false);
        mPresenter.attach(this);

        initViews(view);
        initListView();
        initSwipeRefreshLayout();

        mIntentSettingsButtonText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(WifiManager.ACTION_PICK_WIFI_NETWORK));
            }
        });

        boolean isWifiEnabled = false;
        boolean isConnected = false;

        Bundle args = getArguments();
        if (args != null
                && args.containsKey(ControllersScreenActivity.EXTRA_IS_WIFI_ENABLED)
                && args.containsKey(ControllersScreenActivity.EXTRA_IS_CONNECTED)) {
            isWifiEnabled = args.getBoolean(ControllersScreenActivity.EXTRA_IS_WIFI_ENABLED);
            isConnected = args.getBoolean(ControllersScreenActivity.EXTRA_IS_CONNECTED);
        }
        mPresenter.onUpdateWifiState(isWifiEnabled, isConnected);
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        mPresenter.attach(this);
        IntentFilter filter = new IntentFilter(ControllersScreenActivity.NOTIFY_NETWORK_CHANGE);
        LocalBroadcastManager.getInstance(this.getContext()).registerReceiver(mLocalBroadcastReceiver, filter);
    }

    @Override
    public void onStop() {
        super.onStop();
        LocalBroadcastManager.getInstance(this.getContext()).unregisterReceiver(mLocalBroadcastReceiver);
        sendMessageStopSearch();
        mPresenter.detach();
        mHandler.removeAllCallbacksAndMessages();
    }

    @Override
    public void sendMessageStartSearch() {
        mHandler.sendEmptyMessage(ViewHandler.START_SEARCH);
    }

    @Override
    public void sendMessageStopSearch() {
        mHandler.sendEmptyMessage(ViewHandler.STOP_SEARCH);
    }

    @Override
    public void stopSwipeRefreshing() {
        if (mSwipeRefreshLayout.isRefreshing()) {
            mHandler.post(new Runnable() {
                @Override
                public void run() {
                    mSwipeRefreshLayout.setRefreshing(false);
                }
            });
        }
    }

    @Override
    public void addToControllersList(final String controllerResponse) {
        if (!mControllersList.contains(controllerResponse)) {
            mHandler.sendMessage(mHandler.obtainMessage(ViewHandler.UPDATE_LIST_VIEW, controllerResponse));
        }
    }

    @Override
    public String getControllerNameByMACAddress(String macAddress) {
        return mPresenter.getControllerNameByMACAddress(macAddress);
    }

    @Override
    public void showContent(boolean isEnabled, boolean isConnected) {
        setVisibility(View.VISIBLE, mIntentSettingsButtonText, mBodyText);
        setVisibility(View.GONE, mSwipeRefreshLayout, mControllersListView);
        mSwipeRefreshLayout.setRefreshing(false);
        if (isEnabled) {
            if (isConnected) {
                setVisibility(View.GONE, mIntentSettingsButtonText, mBodyText);
                setVisibility(View.VISIBLE, mSwipeRefreshLayout, mControllersListView);
                mSwipeRefreshLayout.setRefreshing(true);
            } else {
                mBodyText.setText(R.string.wifi_not_connected);
            }
        } else {
            mBodyText.setText(R.string.wifi_turn_off);
        }
        if (!isEnabled || !isConnected) {
            mHandler.removeAllCallbacksAndMessages();
        }
    }

    void showWifiIsConnected() {
        showContent(true, true);
    }


    void showNoFindingControllersContent() {
        mBodyText.setText(null);
        setVisibility(View.VISIBLE, mBodyText, mSwipeRefreshLayout);
        setVisibility(View.GONE, mControllersListView, mIntentSettingsButtonText);
    }

    IControllerListPresenter getPresenter() {
        return mPresenter;
    }

    void setBodyText(String text) {
        mBodyText.setText(text);
    }

    void addItemToControllersList(String item) {
        mControllersList.add(item);
        mControllerListAdapter.notifyDataSetChanged();
    }

    boolean isControllersListEmpty() {
        return mControllersList.isEmpty();
    }

    boolean controllersListContains(String item) {
        return mControllersList.contains(item);
    }

    void stopSwipeRefresh() {
        mSwipeRefreshLayout.setRefreshing(false);
    }

    private void initViews(View root) {
        mControllersListView = (ListView) root.findViewById(R.id.fragment_controllers_list_list_controllers);
        mBodyText = (TextView) root.findViewById(R.id.fragment_controllers_list_text_body);
        mIntentSettingsButtonText = (TextView) root.findViewById(R.id.fragment_controllers_list_text_intent_settings);
        mSwipeRefreshLayout = (SwipeRefreshLayout) root.findViewById(R.id.fragment_controllers_list_swipe_refresh);
    }

    private void initListView() {
        mControllerListAdapter = new ControllerListAdapter(this, R.layout.item_controllers_list, mControllersList);
        mControllersListView.setAdapter(mControllerListAdapter);
        mControllersListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (view != null) {
                    ControllerListAdapter.ControllerViewHolder holder = (ControllerListAdapter.ControllerViewHolder) view.getTag();
                    Fragment fragment = new ControllerManagementFragment();
                    Bundle args = new Bundle();
                    args.putString(ControllerManagementFragment.EXTRA_NAME, holder.nameController);
                    args.putString(ControllerManagementFragment.EXTRA_MAC_WITH_COLONS, holder.macAddressWithColons);
                    args.putString(ControllerManagementFragment.EXTRA_MAC, holder.macAddress);
                    args.putString(ControllerManagementFragment.EXTRA_IP, holder.ipAddress);
                    fragment.setArguments(args);
                    getActivity().getSupportFragmentManager()
                            .beginTransaction()
                            .setCustomAnimations(R.anim.slide_in_right, R.anim.no_trasition)
                            .add(R.id.activity_controllers_screen_frame_layout, fragment)
                            .commit();
                    mHandler.sendEmptyMessage(ViewHandler.STOP_SEARCH);
                }
            }
        });
    }

    private void initSwipeRefreshLayout() {
        mSwipeRefreshLayout.setColorSchemeColors(Color.BLUE, Color.CYAN, Color.MAGENTA, Color.YELLOW, Color.RED);
        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                ControllerListFragment.this.sendMessageStopSearch();
                mControllersList.clear();
                mControllerListAdapter.notifyDataSetChanged();
                ControllerListFragment.this.sendMessageStartSearch();
            }
        });
    }

    private void setVisibility(int visibility, View... views) {
        for (View item : views) {
            item.setVisibility(visibility);
        }
    }
}
