package me.foxaice.smartlight.fragments.controllers_screen.controller_management.view.dialogs.wifilist;

import android.app.Dialog;
import android.content.DialogInterface;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
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

import java.lang.ref.WeakReference;

import me.foxaice.smartlight.R;

public class WifiListDialog extends DialogFragment {
    public static final String TAG_WIFI_LIST_AP = "WIFI_LIST_AP";
    public static final String TAG_WIFI_LIST_STA = "WIFI_LIST_STA";

    private DialogListener mDialogListener;
    private SwipeRefreshLayout mSwipeRefreshLayout;
    private TextView mSavedNetworkText;
    private TextView mAvailableNetworksText;
    private RecyclerView mWifiPointsRecycler;
    private WifiPointsAdapter mWifiPointsAdapter;
    private WifiPointHolder mSavedNetworkHolder;
    private Drawable[] mDrawables;
    private String mSavedNetworkSSID;
    private Handler mHandler;

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        if (savedInstanceState != null) dismiss();
        mDrawables = getWifiSignalLevelDrawables();
        View root = View.inflate(getContext(), R.layout.fragment_controller_management_wifi_list_dialog, null);
        LinearLayout savedNetworkView = (LinearLayout) root.findViewById(R.id.fragment_controller_management_dialog_layout_saved_network);
        mSavedNetworkHolder = new WifiPointHolder(savedNetworkView);
        mWifiPointsRecycler = (RecyclerView) root.findViewById(R.id.fragment_controller_management_dialog_recycler_wifi_points);
        mSwipeRefreshLayout = (SwipeRefreshLayout) root.findViewById(R.id.fragment_controller_management_dialog_swipe_refresh);
        mAvailableNetworksText = (TextView) root.findViewById(R.id.fragment_controller_management_dialog_text_available_networks);
        mSavedNetworkText = (TextView) root.findViewById(R.id.fragment_controller_management_dialog_text_saved_network);

        mWifiPointsAdapter = new WifiPointsAdapter(this);
        mWifiPointsRecycler.setLayoutManager(new LinearLayoutManager(getContext()));
        mWifiPointsRecycler.setItemAnimator(new DefaultItemAnimator());
        mWifiPointsRecycler.setAdapter(mWifiPointsAdapter);

        mHandler = new CustomHandler(this);

        if (getTargetFragment() instanceof DialogListener) {
            mDialogListener = (DialogListener) getTargetFragment();
        }

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
    public void onStop() {
        super.onStop();
    }

    @Override
    public void onDismiss(DialogInterface dialog) {
        super.onDismiss(dialog);
        mHandler.removeCallbacksAndMessages(null);
        mDialogListener.onDialogDismissed();
    }

    public void updateWifiPointsList(String response, String savedNetworkSSID) {
        mSavedNetworkSSID = savedNetworkSSID;
        mHandler.sendMessage(mHandler.obtainMessage(CustomHandler.UPDATE_WIFI_LIST, response));
    }

    void onWifiPointClick(WifiPoint wifiPoint) {
        this.dismiss();
        mDialogListener.onClickNetwork(wifiPoint.getSSID(), wifiPoint.getSecurity());
    }

    private void updateWifiList(String response) {
        mSwipeRefreshLayout.setRefreshing(false);
        mSwipeRefreshLayout.setVisibility(View.GONE);
        mAvailableNetworksText.setVisibility(View.VISIBLE);
        mWifiPointsRecycler.setVisibility(View.VISIBLE);

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

            mSavedNetworkHolder.signalImage.setImageDrawable(mDrawables[WifiPointsAdapter.getIndexDrawableBySignal(savedNetwork)]);
            mSavedNetworkHolder.SSIDText.setText(savedNetwork.getSSID());
            mSavedNetworkHolder.BSSIDText.setText(savedNetwork.getBSSID());

            final String ssid = savedNetwork.getSSID();
            final String security = savedNetwork.getSecurity();
            mSavedNetworkHolder.itemView.setOnClickListener(new View.OnClickListener() {
                final DialogListener mDialogListener = (DialogListener) WifiListDialog.this.mDialogListener;

                @Override
                public void onClick(View v) {
                    WifiListDialog.this.dismiss();
                    mDialogListener.onClickConnectToSavedNetwork();
                }
            });
            mSavedNetworkHolder.itemView.setOnLongClickListener(new View.OnLongClickListener() {

                @Override
                public boolean onLongClick(View v) {
                    ViewGroup viewGroup = (ViewGroup) View.inflate(getContext(), R.layout.fragment_controller_management_wifi_list_saved_network_subdialog, null);
                    final AlertDialog alertDialog = new AlertDialog.Builder(getContext()).setTitle(ssid).setView(viewGroup).create();

                    if (viewGroup.getChildCount() > 5) {
                        final DialogListener dialogListener = mDialogListener;
                        View view = viewGroup.getChildAt(1);
                        TextView textConnect = view instanceof TextView ? (TextView) view : null;
                        view = viewGroup.getChildAt(3);
                        TextView textChangePass = view instanceof TextView ? (TextView) view : null;
                        view = viewGroup.getChildAt(5);
                        TextView textForget = view instanceof TextView ? (TextView) view : null;

                        if (textConnect != null) {
                            textConnect.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    alertDialog.dismiss();
                                    WifiListDialog.this.dismiss();
                                    dialogListener.onClickConnectToSavedNetwork();
                                }
                            });
                        }
                        if (textChangePass != null) {
                            textChangePass.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    alertDialog.dismiss();
                                    WifiListDialog.this.dismiss();
                                    dialogListener.onClickNetwork(ssid, security);
                                }
                            });
                        }
                        if (textForget != null) {
                            textForget.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    alertDialog.dismiss();
                                    WifiListDialog.this.dismiss();
                                    dialogListener.onClickForgetNetwork();
                                }
                            });
                        }
                    }
                    alertDialog.show();
                    return true;
                }
            });
        }
    }

    Drawable[] getWifiSignalLevelDrawables() {
        if (mDrawables == null) {
            Drawable[] drawables = new Drawable[5];
            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
                int[] drawableIds = new int[]{
                        R.drawable.wifi_signal0, R.drawable.wifi_signal1, R.drawable.wifi_signal2,
                        R.drawable.wifi_signal3, R.drawable.wifi_signal4
                };
                for (int i = 0; i < drawables.length; i++) {
                    drawables[i] = AppCompatResources.getDrawable(getContext(), drawableIds[i]);
                }
            } else {
                int[] themeStyles = new int[]{
                        R.style.WifiSignalStrength0, R.style.WifiSignalStrength1, R.style.WifiSignalStrength2,
                        R.style.WifiSignalStrength3, R.style.WifiSignalStrength4,
                };
                Resources.Theme theme;
                for (int i = 0; i < drawables.length; i++) {
                    theme = getResources().newTheme();
                    drawables[i] = ResourcesCompat.getDrawable(getResources(), R.drawable.wifi_signal, theme);
                    theme.applyStyle(themeStyles[i], false);
                }
            }
            mDrawables = drawables;
        }
        return mDrawables;
    }

    public interface DialogListener {
        void onClickNetwork(String ssid, String security);
        void onClickForgetNetwork();
        void onClickConnectToSavedNetwork();
        void onDialogDismissed();
        void onStartScanNetworksTask();
    }

    private static class CustomHandler extends Handler {
        private static final int UPDATE_WIFI_LIST = 0x0000;
        private final WeakReference<WifiListDialog> wrDialog;

        private CustomHandler(WifiListDialog dialog) {
            this.wrDialog = new WeakReference<>(dialog);
        }

        @Override
        public void handleMessage(Message msg) {
            WifiListDialog dialog = wrDialog.get();
            if (dialog != null) {
                if (msg.what == UPDATE_WIFI_LIST) {
                    dialog.updateWifiList(String.valueOf(msg.obj));
                }
            }
        }
    }
}













