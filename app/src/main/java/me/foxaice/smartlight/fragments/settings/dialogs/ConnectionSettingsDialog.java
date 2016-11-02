package me.foxaice.smartlight.fragments.settings.dialogs;

import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import me.foxaice.smartlight.R;
import me.foxaice.smartlight.preferences.ISharedPreferencesController;
import me.foxaice.smartlight.preferences.SharedPreferencesController;

public class ConnectionSettingsDialog extends DialogFragment {
    public static final String TAG_IP = "TAG_IP";
    public static final String TAG_PORT = "TAG_PORT";
    EditText editText;
    AlertDialog alertDialog;
    private ISharedPreferencesController mSharedPreferencesController;

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        mSharedPreferencesController = SharedPreferencesController.getInstance(getContext());
        View view = View.inflate(getContext(), R.layout.fragment_settings_edit_text_dialog, null);
        alertDialog = new AlertDialog.Builder(getContext()).setView(view).create();
        editText = (EditText) view.findViewById(R.id.fragment_settings_dialog_edit_name);
        TextView textView = (TextView) view.findViewById(R.id.fragment_settings_dialog_text_name);

        DialogInterface.OnClickListener onClickListener = null;
        String savedValue = null;
        TextWatcher textWatcher = null;
        switch (getTag()) {
            case TAG_IP: {
                onClickListener = CustomDialogListener.getIPOnClickListener(this);
                textWatcher = CustomTextWatcher.getIPTextWatcher(this);
                textView.setText(R.string.setting_ip);
                savedValue = getControllerIP();
                editText.setHint(R.string.hint_enter_ip);
            }
            break;
            case TAG_PORT: {
                onClickListener = CustomDialogListener.getPortOnClickListener(this);
                textWatcher = CustomTextWatcher.getPortTextWatcher(this);
                textView.setText(R.string.setting_port);
                savedValue = String.valueOf(getControllerPort());
                editText.setHint(R.string.hint_enter_port);
            }
        }

        if (savedValue != null) {
            editText.setText(savedValue);
            editText.setSelection(savedValue.length());
        }

        editText.addTextChangedListener(textWatcher);

        if (onClickListener != null) {
            alertDialog.setButton(DialogInterface.BUTTON_NEGATIVE, getString(R.string.dialog_negative_button_text), onClickListener);
            alertDialog.setButton(DialogInterface.BUTTON_NEUTRAL, getString(R.string.dialog_neutral_button_text), onClickListener);
            alertDialog.setButton(DialogInterface.BUTTON_POSITIVE, getString(R.string.dialog_positive_button_text), onClickListener);
        }
        return alertDialog;
    }

    void changeControllerIP(String ip) {
        mSharedPreferencesController.setIpAddress(ip);
    }

    void changeControllerPort(int port) {
        mSharedPreferencesController.setPort(port);
    }

    private String getControllerIP() {
        return mSharedPreferencesController.getIpAddress();
    }

    private int getControllerPort() {
        return mSharedPreferencesController.getPort();
    }
}
