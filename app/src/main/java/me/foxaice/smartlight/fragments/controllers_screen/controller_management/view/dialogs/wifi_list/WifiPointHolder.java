package me.foxaice.smartlight.fragments.controllers_screen.controller_management.view.dialogs.wifi_list;

import android.graphics.drawable.Drawable;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import me.foxaice.smartlight.R;

class WifiPointHolder extends RecyclerView.ViewHolder {
    private final ImageView mSignalImage;
    private final TextView mSSIDText;
    private final TextView mBSSIDText;

    WifiPointHolder(View view) {
        super(view);
        mSignalImage = (ImageView) view.findViewById(R.id.fragment_controller_management_dialog_image_wifi_signal);
        mSSIDText = (TextView) view.findViewById(R.id.fragment_controller_management_dialog_text_wifi_SSID);
        mBSSIDText = (TextView) view.findViewById(R.id.fragment_controller_management_dialog_text_wifi_BSSID);
    }

    void setVisibility(int visibility) {
        itemView.setVisibility(visibility);
        mSignalImage.setVisibility(visibility);
        mSSIDText.setVisibility(visibility);
        mBSSIDText.setVisibility(visibility);
    }

    void setOnClickListener(View.OnClickListener listener) {
        itemView.setOnClickListener(listener);
    }

    void setOnLongClickListener(View.OnLongClickListener listener) {
        itemView.setOnLongClickListener(listener);
    }

    void setSSID(String ssid) {
        mSSIDText.setText(ssid);
    }

    void setBSSID(String bssid) {
        mBSSIDText.setText(bssid);
    }

    void setImageDrawable(Drawable drawable) {
        mSignalImage.setImageDrawable(drawable);
    }
}