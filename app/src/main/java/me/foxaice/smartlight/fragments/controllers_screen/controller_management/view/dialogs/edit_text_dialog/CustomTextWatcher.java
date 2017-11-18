package me.foxaice.smartlight.fragments.controllers_screen.controller_management.view.dialogs.edit_text_dialog;

import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.EditText;
import android.widget.TextView;

import me.foxaice.smartlight.R;
import me.foxaice.smartlight.utils.Validator;

class CustomTextWatcher implements TextWatcher {

    private final TextView mTextPasswordLength;
    private final EditText mEditText;
    private final EditTextDialog mDialog;
    @EditTextDialog.SecurityTypes
    private String mType;

    CustomTextWatcher(@EditTextDialog.SecurityTypes String type, EditTextDialog dialog, TextView textPasswordLength, EditText editText) {
        mTextPasswordLength = textPasswordLength;
        mEditText = editText;
        mDialog = dialog;
        mType = type;
    }

    private static String getStringError(@EditTextDialog.SecurityTypes String type, CharSequence s) {
        String error = null;
        switch (type) {
            case EditTextDialog.SecurityTypes.HEX:
                error = s.length() != 0 ? Errors.HEX : null;
                break;
            case EditTextDialog.SecurityTypes.ASCII:
                error = s.length() != 0 ? Errors.ASCII : null;
                break;
            case EditTextDialog.SecurityTypes.TKIP_OR_AES:
                error = Errors.AES_OR_TKIP;
                break;
            case EditTextDialog.SecurityTypes.AES:
                error = getStringError(EditTextDialog.SecurityTypes.TKIP_OR_AES, s);
                break;
            case EditTextDialog.SecurityTypes.TKIP:
                error = getStringError(EditTextDialog.SecurityTypes.TKIP_OR_AES, s);
                break;
        }
        return !isValidByType(type, s) ? error : null;
    }

    private static boolean isValidByType(@EditTextDialog.SecurityTypes String type, CharSequence s) {
        switch (type) {
            case EditTextDialog.SecurityTypes.HEX:
                return Validator.isHexPasskey(s.toString());
            case EditTextDialog.SecurityTypes.ASCII:
                return Validator.isASCIIPasseky(s.toString());
            case EditTextDialog.SecurityTypes.TKIP_OR_AES:
                int count = s.length();
                return (count == 0 || (count >= 8 && count <= 63));
            case EditTextDialog.SecurityTypes.AES:
                return isValidByType(EditTextDialog.SecurityTypes.TKIP_OR_AES, s);
            case EditTextDialog.SecurityTypes.TKIP:
                return isValidByType(EditTextDialog.SecurityTypes.TKIP_OR_AES, s);
        }
        throw new IllegalArgumentException("Wrong type!");
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {
        setPasswordLength(count);
        setPositiveButtonEnable(s);
    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {
        setPasswordLength(count);
        setPositiveButtonEnable(s);
    }

    @Override
    public void afterTextChanged(Editable s) {
        setPasswordLength(s.length());
        setPositiveButtonEnable(s);
        mEditText.setError(getStringError(mType, s));
    }

    void changeType(@EditTextDialog.SecurityTypes String type) {
        mType = type;
    }

    private void setPasswordLength(int count) {
        String text = mDialog.getString(R.string.password_length, count == 0 ? "NONE" : count);
        mTextPasswordLength.setText(text);
    }

    private void setPositiveButtonEnable(CharSequence s) {
        boolean enabled = isValidByType(mType, s);
        ((AlertDialog) mDialog.getDialog()).getButton(DialogInterface.BUTTON_POSITIVE).setEnabled(enabled);
        mEditText.setError(null);
    }

    private interface Errors {
        String ASCII = "5 or 13 ASCII characters!";
        String HEX = "10 or 26 HEX characters!";
        String AES_OR_TKIP = "8-63 characters or none!";
    }
}
