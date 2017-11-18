package me.foxaice.smartlight.fragments.controllers_screen.controllers_list.view;

import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import me.foxaice.smartlight.R;
import me.foxaice.smartlight.fragments.controllers_screen.controller_management.view.ControllerManagementFragment;
import me.foxaice.smartlight.fragments.controllers_screen.controllers_list.presenter.ControllerListPresenter;
import me.foxaice.smartlight.fragments.controllers_screen.controllers_list.presenter.IControllerListPresenter;
import me.foxaice.smartlight.utils.ViewUtils;


public class ControllerListFragment extends Fragment implements IControllerListView, AdapterView.OnItemClickListener {
    private static final String TAG = "CONTROLLERS_LIST_FRAGMENT";
    private static final String NOTIFY_NETWORK_CHANGE = "NOTIFY_NETWORK_CHANGE";
    private static final String EXTRA_IS_CONNECTED = "EXTRA_IS_CONNECTED";
    private static final String EXTRA_IS_WIFI_ENABLED = "EXTRA_IS_WIFI_ENABLED";
    private List<String> mControllersList = new ArrayList<>();
    private ControllerListAdapter mControllerListAdapter;
    private ListView mControllersListView;
    private TextView mBodyText;
    private TextView mIntentSettingsButtonText;
    private SwipeRefreshLayout mSwipeRefreshLayout;
    private IControllerListPresenter mPresenter = new ControllerListPresenter();
    private ViewHandler mHandler = new ViewHandler(this);

    private BroadcastReceiver mLocalBroadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            boolean isWifiEnabled = intent.getBooleanExtra(EXTRA_IS_WIFI_ENABLED, false);
            boolean isConnected = intent.getBooleanExtra(EXTRA_IS_CONNECTED, false);
            mPresenter.onUpdateWifiState(isWifiEnabled, isConnected);
        }
    };

    public static void attachFragment(AppCompatActivity activity, int containerId, boolean networkIsEnabled, boolean networkIsConnected) {
        Fragment fragment = ControllerListFragment.findFragment(activity);
        FragmentTransaction transaction = ControllerListFragment.beginTransaction(activity);
        if (fragment == null) {
            fragment = new ControllerListFragment();
            Bundle args = ControllerListFragment.formNetworkArgs(networkIsEnabled, networkIsConnected);
            fragment.setArguments(args);
            transaction.add(containerId, fragment, ControllerListFragment.TAG);
        } else if (!networkIsConnected && !(ControllerListFragment.isInContainer(activity, containerId))) {
            transaction.replace(containerId, fragment);
        }
        transaction.commit();
    }

    public static Intent getNetworkChangeIntent(boolean networkIsEnabled, boolean networkIsConnected) {
        return new Intent(NOTIFY_NETWORK_CHANGE).putExtras(
                ControllerListFragment.formNetworkArgs(networkIsEnabled, networkIsConnected)
        );
    }

    private static Bundle formNetworkArgs(boolean isEnabled, boolean isConnected) {
        return new Intent()
                .putExtra(EXTRA_IS_WIFI_ENABLED, isEnabled)
                .putExtra(EXTRA_IS_CONNECTED, isConnected)
                .getExtras();
    }

    private static Fragment findFragment(AppCompatActivity activity) {
        return activity.getSupportFragmentManager().findFragmentByTag(ControllerListFragment.TAG);
    }

    private static boolean isInContainer(AppCompatActivity activity, int containerId) {
        return activity.getSupportFragmentManager()
                .findFragmentById(containerId) instanceof ControllerListFragment;
    }

    @SuppressLint("CommitTransaction")
    private static FragmentTransaction beginTransaction(AppCompatActivity activity) {
        return activity.getSupportFragmentManager().beginTransaction();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_controllers_list, container, false);
        mPresenter.attach(this);
        initViews(view);
        initListView();
        initSwipeRefreshLayout();
        loadArguments(getArguments());
        mIntentSettingsButtonText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(WifiManager.ACTION_PICK_WIFI_NETWORK));
            }
        });
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        mPresenter.attach(this);
        IntentFilter filter = new IntentFilter(NOTIFY_NETWORK_CHANGE);
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
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        if (view != null) {
            ControllerListAdapter.ViewHolder holder = (ControllerListAdapter.ViewHolder) view.getTag();
            ControllerManagementFragment.attachFragment((AppCompatActivity) getActivity(),
                    getId(),
                    holder.nameController,
                    holder.ipAddress,
                    holder.macAddress,
                    holder.macAddressWithColons
            );
            mHandler.sendStopSearchMessage();
        }
    }

    @Override
    public void sendMessageStartSearch() {
        mHandler.sendStartSearchMessage();
    }

    @Override
    public void sendMessageStopSearch() {
        mHandler.sendStopSearchMessage();
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
            mHandler.sendUpdateListViewMessage(controllerResponse);
        }
    }

    @Override
    public void showContent(boolean isEnabled, boolean isConnected) {
        ViewUtils.setVisibility(View.VISIBLE, mIntentSettingsButtonText, mBodyText);
        ViewUtils.setVisibility(View.GONE, mSwipeRefreshLayout, mControllersListView);
        mSwipeRefreshLayout.setRefreshing(false);
        if (isEnabled) {
            if (isConnected) {
                ViewUtils.setVisibility(View.GONE, mIntentSettingsButtonText, mBodyText);
                ViewUtils.setVisibility(View.VISIBLE, mSwipeRefreshLayout, mControllersListView);
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

    @Override
    public String getControllerNameByMACAddress(String macAddress) {
        return mPresenter.getControllerNameByMACAddress(macAddress);
    }

    boolean isControllersListEmpty() {
        return mControllersList.isEmpty();
    }

    boolean controllersListContains(String item) {
        return mControllersList.contains(item);
    }

    void showWifiIsConnected() {
        showContent(true, true);
    }

    void showNoFindingControllersContent() {
        setBodyText(null);
        ViewUtils.setVisibility(View.VISIBLE, mBodyText, mSwipeRefreshLayout);
        ViewUtils.setVisibility(View.GONE, mControllersListView, mIntentSettingsButtonText);
    }

    void startSearch() {
        mPresenter.startSearch();
    }

    void stopSearch() {
        mPresenter.stopSearch();
    }

    void setBodyText(String text) {
        mBodyText.setText(text);
    }

    void addItemToControllersList(String item) {
        mControllersList.add(item);
        mControllerListAdapter.notifyDataSetChanged();
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
        mControllerListAdapter = new ControllerListAdapter(this, mControllersList);
        mControllersListView.setAdapter(mControllerListAdapter);
        mControllersListView.setOnItemClickListener(this);
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

    private void loadArguments(Bundle args) {
        boolean isWifiEnabled = false;
        boolean isConnected = false;
        if (args != null && args.containsKey(EXTRA_IS_WIFI_ENABLED) && args.containsKey(EXTRA_IS_CONNECTED)) {
            isWifiEnabled = args.getBoolean(EXTRA_IS_WIFI_ENABLED);
            isConnected = args.getBoolean(EXTRA_IS_CONNECTED);
        }
        mPresenter.onUpdateWifiState(isWifiEnabled, isConnected);
    }
}
