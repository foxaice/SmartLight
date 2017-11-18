package me.foxaice.smartlight.fragments.controllers_screen.controller_management.view.dialogs.wifi_list;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.content.res.ResourcesCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.content.res.AppCompatResources;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import me.foxaice.smartlight.R;
import me.foxaice.smartlight.utils.ViewUtils;

public class WifiListDialog extends DialogFragment {
    private static final String TAG_WIFI_LIST_AP = "WIFI_LIST_AP";
    private static final String TAG_WIFI_LIST_STA = "WIFI_LIST_STA";
    private static final int REQUEST_CODE = 300;

    private DialogListener mDialogListener;
    private SwipeRefreshLayout mSwipeRefreshLayout;
    private TextView mSavedNetworkText;
    private TextView mAvailableNetworksText;
    private RecyclerView mWifiPointsRecycler;
    private WifiPointsAdapter mWifiPointsAdapter;
    private WifiPointHolder mSavedNetworkHolder;
    private Drawable[] mDrawables;
    private String mSavedNetworkSSID;
    private ViewHandler mViewHandler;

    public interface DialogListener {
        void onClickNetwork(String ssid, String security);
        void onClickForgetNetwork();
        void onClickConnectToSavedNetwork();
        void onDialogDismissed();
        void onStartScanNetworksTask();
    }

    public static WifiListDialog createAndShowStaDialog(Fragment fragment) {
        return createDialog(fragment, TAG_WIFI_LIST_STA);
    }

    public static WifiListDialog createAndShowApDialog(Fragment fragment) {
        return createDialog(fragment, TAG_WIFI_LIST_AP);
    }

    private static WifiListDialog createDialog(Fragment fragment, String tag) {
        WifiListDialog dialog = new WifiListDialog();
        dialog.setTargetFragment(fragment, REQUEST_CODE);
        dialog.show(fragment.getFragmentManager(), tag);
        return dialog;
    }

    private static Drawable[] getDrawables(Context context) {
        Drawable[] drawables = new Drawable[5];
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
            int[] drawableIds = new int[]{
                    R.drawable.wifi_signal0, R.drawable.wifi_signal1, R.drawable.wifi_signal2,
                    R.drawable.wifi_signal3, R.drawable.wifi_signal4
            };
            for (int i = 0; i < drawables.length; i++) {
                drawables[i] = AppCompatResources.getDrawable(context, drawableIds[i]);
            }
        } else {
            int[] themeStyles = new int[]{
                    R.style.WifiSignalStrength0, R.style.WifiSignalStrength1, R.style.WifiSignalStrength2,
                    R.style.WifiSignalStrength3, R.style.WifiSignalStrength4,
            };
            Resources.Theme theme;
            for (int i = 0; i < drawables.length; i++) {
                theme = context.getResources().newTheme();
                theme.applyStyle(themeStyles[i], false);
                drawables[i] = ResourcesCompat.getDrawable(context.getResources(), R.drawable.wifi_signal, theme);
            }
        }
        return drawables;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        if (savedInstanceState != null) dismiss();
        mDrawables = WifiListDialog.getDrawables(getContext());
        View root = View.inflate(getContext(), R.layout.fragment_controller_management_wifi_list_dialog, null);
        initViews(root);
        setupRecyclerView();
        mViewHandler = new ViewHandler(this);
        initDialogListener();
        mDialogListener.onStartScanNetworksTask();
        return new AlertDialog.Builder(getContext()).setView(root).create();
    }

    @Override
    public void onResume() {
        super.onResume();
        if (mSavedNetworkSSID == null) {
            mSavedNetworkHolder.setVisibility(View.GONE);
            mSavedNetworkText.setVisibility(View.GONE);
            mSwipeRefreshLayout.setRefreshing(true);
        }
    }

    @Override
    public void onDismiss(DialogInterface dialog) {
        super.onDismiss(dialog);
        mViewHandler.removeAllCallbacksAndMessages();
        mDialogListener.onDialogDismissed();
    }

    public void updateWifiPointsList(String response, String savedNetworkSSID) {
        mSavedNetworkSSID = savedNetworkSSID;
        mViewHandler.sendUpdateWifiListMessage(response);
    }

    void onWifiPointClick(WifiPoint wifiPoint) {
        this.dismiss();
        mDialogListener.onClickNetwork(wifiPoint.getSSID(), wifiPoint.getSecurity());
    }

    void updateWifiList(String response) {
        mSwipeRefreshLayout.setRefreshing(false);
        mSwipeRefreshLayout.setVisibility(View.GONE);
        ViewUtils.setVisibility(View.VISIBLE, mAvailableNetworksText, mWifiPointsRecycler);

        mWifiPointsAdapter.fillWifiPointsList(response);
        mWifiPointsAdapter.sortByDesc();

        WifiPoint savedNetwork = null;
        if (mSavedNetworkSSID != null) {
            savedNetwork = mWifiPointsAdapter.getWifiPointBySSID(mSavedNetworkSSID);
            mWifiPointsAdapter.remove(savedNetwork);
        }
        if (getTag().equals(TAG_WIFI_LIST_AP) && savedNetwork != null) {
            mSavedNetworkHolder.setVisibility(View.VISIBLE);
            mSavedNetworkText.setVisibility(View.VISIBLE);
            mSavedNetworkHolder.setImageDrawable(mDrawables[WifiPointsAdapter.getIndexDrawableByWifiPointSignal(savedNetwork)]);
            mSavedNetworkHolder.setSSID(savedNetwork.getSSID());
            mSavedNetworkHolder.setBSSID(savedNetwork.getBSSID());
            setOnClickListenerForSavedNetwork();
            setOnLongClickListenerForSavedNetwork(savedNetwork);
        }
    }

    private void initViews(View root) {
        LinearLayout savedNetworkView = (LinearLayout) root.findViewById(R.id.fragment_controller_management_dialog_layout_saved_network);
        mSavedNetworkHolder = new WifiPointHolder(savedNetworkView);
        mWifiPointsRecycler = (RecyclerView) root.findViewById(R.id.fragment_controller_management_dialog_recycler_wifi_points);
        mSwipeRefreshLayout = (SwipeRefreshLayout) root.findViewById(R.id.fragment_controller_management_dialog_swipe_refresh);
        mAvailableNetworksText = (TextView) root.findViewById(R.id.fragment_controller_management_dialog_text_available_networks);
        mSavedNetworkText = (TextView) root.findViewById(R.id.fragment_controller_management_dialog_text_saved_network);
    }

    private void setupRecyclerView() {
        mWifiPointsAdapter = new WifiPointsAdapter(this);
        mWifiPointsRecycler.setLayoutManager(new LinearLayoutManager(getContext()));
        mWifiPointsRecycler.setItemAnimator(new DefaultItemAnimator());
        mWifiPointsRecycler.setAdapter(mWifiPointsAdapter);
    }

    private void initDialogListener() {
        if (getTargetFragment() instanceof DialogListener) {
            mDialogListener = (DialogListener) getTargetFragment();
        } else {
            dismiss();
        }
    }

    private void setOnLongClickListenerForSavedNetwork(WifiPoint savedNetwork) {
        if (mSavedNetworkHolder != null) {
            final String ssid = savedNetwork.getSSID();
            final String security = savedNetwork.getSecurity();
            mSavedNetworkHolder.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    ViewGroup viewGroup = (ViewGroup) View.inflate(getContext(),
                            R.layout.fragment_controller_management_wifi_list_saved_network_subdialog,
                            null);
                    final AlertDialog alertDialog = new AlertDialog.Builder(getContext())
                            .setTitle(ssid).setView(viewGroup)
                            .create();
                    if (viewGroup.getChildCount() > 5) {
                        View[] views = new View[]{viewGroup.getChildAt(1), viewGroup.getChildAt(3), viewGroup.getChildAt(5)};
                        final TextView textConnect = views[0] instanceof TextView ? (TextView) views[0] : null;
                        final TextView textChangePass = views[1] instanceof TextView ? (TextView) views[1] : null;
                        final TextView textForget = views[2] instanceof TextView ? (TextView) views[2] : null;
                        for (View item : views) {
                            if (item != null) {
                                item.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        alertDialog.dismiss();
                                        WifiListDialog.this.dismiss();
                                        if (v == textConnect) {
                                            mDialogListener.onClickConnectToSavedNetwork();
                                        } else if (v == textChangePass) {
                                            mDialogListener.onClickNetwork(ssid, security);
                                        } else if (v == textForget) {
                                            mDialogListener.onClickForgetNetwork();
                                        }
                                    }
                                });
                            }
                        }
                    }
                    alertDialog.show();
                    return true;
                }
            });
        }
    }

    private void setOnClickListenerForSavedNetwork() {
        if (mSavedNetworkHolder != null) {
            mSavedNetworkHolder.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    WifiListDialog.this.dismiss();
                    mDialogListener.onClickConnectToSavedNetwork();
                }
            });
        }
    }

    Drawable getWifiSignalLevelDrawable(int position) {
        return mDrawables != null ? mDrawables[position] : null;
    }
}













