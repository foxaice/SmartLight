package me.foxaice.smartlight.fragments.settings.view.dialogs;

import android.content.DialogInterface;
import android.support.v4.app.Fragment;
import android.widget.Toast;

import me.foxaice.smartlight.R;
import me.foxaice.smartlight.fragments.modes.IModeBaseView;
import me.foxaice.smartlight.utils.Validator;

class CustomDialogListener {
    private static final String INVALID_IP = "Invalid IP-Address";
    private static final String INVALID_PORT = "Invalid Port";
    private static final String DEFAULT_IP = "255.255.255.255";
    private static final int DEFAULT_PORT = 8899;

    static DialogInterface.OnClickListener getIPOnClickListener(final ConnectionSettingsDialog dialog) {
        return new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int which) {
                switch (which) {
                    case DialogInterface.BUTTON_NEGATIVE:
                        break;
                    case DialogInterface.BUTTON_NEUTRAL:
                        dialog.editText.setText(DEFAULT_IP);
                        dialog.alertDialog.getButton(DialogInterface.BUTTON_POSITIVE).performClick();
                        break;
                    case DialogInterface.BUTTON_POSITIVE:
                        String ip = dialog.editText.getText().toString();
                        if (Validator.isIpAddress(ip)) {
                            dialog.changeControllerIP(ip);
                            Fragment fragment = dialog.getActivity().getSupportFragmentManager().findFragmentById(R.id.activity_main_screen_frame_layout_mode_content);
                            if (fragment instanceof IModeBaseView) {
                                ((IModeBaseView) fragment).onChangedControllerSettings();
                            }
                        } else {
                            Toast.makeText(dialog.getContext(), INVALID_IP, Toast.LENGTH_SHORT).show();
                        }
                        break;
                }
            }
        };
    }

    static DialogInterface.OnClickListener getPortOnClickListener(final ConnectionSettingsDialog dialog) {
        return new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int which) {
                switch (which) {
                    case DialogInterface.BUTTON_NEGATIVE:
                        break;
                    case DialogInterface.BUTTON_NEUTRAL:
                        dialog.editText.setText(String.valueOf(DEFAULT_PORT));
                        dialog.alertDialog.getButton(DialogInterface.BUTTON_POSITIVE).performClick();
                        break;
                    case DialogInterface.BUTTON_POSITIVE:
                        String temp = dialog.editText.getText().toString();
                        Integer port = !temp.equals("") ? Integer.valueOf(temp) : null;
                        if (port != null && Validator.isPort(port)) {
                            dialog.changeControllerPort(port);
                            Fragment fragment = dialog.getActivity().getSupportFragmentManager().findFragmentById(R.id.activity_main_screen_frame_layout_mode_content);
                            if (fragment instanceof IModeBaseView) {
                                ((IModeBaseView) fragment).onChangedControllerSettings();
                            }
                        } else {
                            Toast.makeText(dialog.getContext(), INVALID_PORT, Toast.LENGTH_SHORT).show();
                        }
                        break;
                }
            }
        };
    }
}
