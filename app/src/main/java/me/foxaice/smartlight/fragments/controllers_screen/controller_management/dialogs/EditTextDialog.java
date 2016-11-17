package me.foxaice.smartlight.fragments.controllers_screen.controller_management.dialogs;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.StringDef;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.text.method.PasswordTransformationMethod;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

import me.foxaice.smartlight.R;
import me.foxaice.smartlight.utils.Validator;

public class EditTextDialog extends DialogFragment {
    public static final String TAG_NAME = "CHANGE_NAME_DIALOG";
    public static final String TAG_PASS_AP = "CHANGE_PASSWORD_AP_DIALOG";
    public static final String TAG_PASS_STA = "CHANGE_PASSWORD_STA_DIALOG";
    public static final String EXTRA_STA_SSID = "EXTRA_STA_SSID";
    public static final String EXTRA_STA_SECURITY = "EXTRA_STA_SECURITY";
    private String mSTASSID;
    private String mSTAEncryptionMethod;
    @SecurityTypes
    private String mSTAEncryptionType;
    private TextWatcher mTextWatcher;
    private TextView mTextNameDialog;
    private TextView mTextPasswordLength;
    private EditText mEditText;
    private Spinner mSpinnerEncryptionMethod;
    private Spinner mSpinnerEncryptionType;

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        if (savedInstanceState != null) dismiss();
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        View view = View.inflate(getActivity(), R.layout.fragment_controller_management_edit_text_dialog, null);
        mTextNameDialog = (TextView) view.findViewById(R.id.fragment_controller_management_dialog_text_name);
        mTextPasswordLength = (TextView) view.findViewById(R.id.fragment_controller_management_dialog_text_password_quantity_chars);
        mEditText = (EditText) view.findViewById(R.id.fragment_controller_management_dialog_edit_name);
        mSpinnerEncryptionMethod = (Spinner) view.findViewById(R.id.fragment_controller_management_dialog_spinner_encryption_method);
        mSpinnerEncryptionType = (Spinner) view.findViewById(R.id.fragment_controller_management_dialog_spinner_encryption_type);

        if (getTag() != null) {
            switch (getTag()) {
                case TAG_NAME:
                    prepareChangeNameContent();
                    builder.setNegativeButton(R.string.dialog_negative_button_text, null)
                            .setPositiveButton(R.string.dialog_positive_button_text, new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    changeName(mEditText.getText().toString());
                                }
                            });
                    break;
                case TAG_PASS_AP:
                    prepareChangePassAPContent();
                    builder.setNegativeButton(R.string.dialog_negative_button_text, null)
                            .setPositiveButton(R.string.dialog_positive_button_text, new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    InputMethodManager im = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                                    im.hideSoftInputFromWindow(mEditText.getWindowToken(), InputMethodManager.HIDE_IMPLICIT_ONLY);
                                    changePassword(mEditText.getText());
                                }
                            });
                    break;
                case TAG_PASS_STA:
                    prepareChangePassSTAContent(view);
                    builder.setNegativeButton(R.string.dialog_negative_button_text, null)
                            .setPositiveButton(R.string.dialog_positive_button_text, new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    InputMethodManager im = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                                    im.hideSoftInputFromWindow(mEditText.getWindowToken(), InputMethodManager.HIDE_IMPLICIT_ONLY);
                                    String params = String.format("%s|||%s,%s,%s", mSTASSID, mSTAEncryptionMethod, mSTAEncryptionType, mEditText.getText());
                                    changePassword(params);
                                }
                            });
                    break;
            }
            builder.setView(view);
        }

        Dialog dialog = builder.create();
        if (dialog.getWindow() != null) {
            dialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);
        }
        return dialog;
    }

    private void prepareChangeNameContent() {
        mTextNameDialog.setText(R.string.controller_name);
        mTextPasswordLength.setVisibility(View.GONE);
        mEditText.setHint(R.string.hint_edit_name);
        mEditText.setInputType(InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS);
    }

    private void prepareChangePassAPContent() {
        mTextNameDialog.setText(R.string.change_password_header);
        mTextPasswordLength.setVisibility(View.VISIBLE);
        mTextPasswordLength.setText(getString(R.string.password_length, "NONE"));
        mEditText.setHint(R.string.hint_edit_password);
        mEditText.setInputType(InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
        mEditText.setTransformationMethod(new PasswordTransformationMethod());
        mTextWatcher = new CustomTextWatcher(SecurityTypes.TKIP_OR_AES, this, mTextPasswordLength, mEditText);
        mEditText.addTextChangedListener(mTextWatcher);
    }

    private void prepareChangePassSTAContent(View view) {
        Bundle args = getArguments();
        if (args != null) {
            mSTASSID = args.getString(EXTRA_STA_SSID);
            String security = args.getString(EXTRA_STA_SECURITY);
            if (security != null) {
                String[] array = security.split("/");
                mSTAEncryptionMethod = array[0];
                if (array.length > 1) {
                    @SecurityTypes
                    String temp = array[1];
                    mSTAEncryptionType = temp;
                } else {
                    mSTAEncryptionType = SecurityTypes.ASCII;
                }
            }
            mTextNameDialog.setText(mSTASSID);
            mTextPasswordLength.setVisibility(View.VISIBLE);
            mTextPasswordLength.setText(getString(R.string.password_length, "NONE"));
            mEditText.setHint(R.string.hint_edit_password);
            mEditText.setInputType(InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
            mEditText.setTransformationMethod(new PasswordTransformationMethod());
        }

        if (mSTAEncryptionMethod != null) {
            if (mSTAEncryptionMethod.matches("^(WPA)1(PSK)(\\1)2(\\2)$")) {
                mSTAEncryptionMethod = "WPA2PSK";
            } else if (mSTAEncryptionMethod.matches("^WPA1PSK$")) {
                mSTAEncryptionMethod = "WPAPSK";
            } else if (mSTAEncryptionMethod.matches("^WEP$")) {
                view.findViewById(R.id.fragment_controller_management_dialog_linear_wep_encryption).setVisibility(View.VISIBLE);
                mSpinnerEncryptionMethod.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                        mSTAEncryptionMethod = parent.getItemAtPosition(position).toString();
                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> parent) {

                    }
                });
                mSpinnerEncryptionType.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                        mSTAEncryptionType = "ASCII".equals(parent.getItemAtPosition(position)) ? SecurityTypes.ASCII : SecurityTypes.HEX;
                        ((CustomTextWatcher) mTextWatcher).changeType(mSTAEncryptionType);
                        mEditText.setText(null);
                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> parent) {

                    }
                });
            }
            if (mSTAEncryptionType.equals(SecurityTypes.TKIP_OR_AES)) {
                mSTAEncryptionType = SecurityTypes.AES;
            }
            mTextWatcher = new CustomTextWatcher(mSTAEncryptionType, this, mTextPasswordLength, mEditText);
        }
        mEditText.addTextChangedListener(mTextWatcher);
    }

    private void changeName(String name) {
        Fragment fragment = getTargetFragment();
        if (fragment instanceof DialogListener) {
            DialogListener listener = (DialogListener) fragment;
            listener.onFinishChangeNameDialog(name);
        }
        dismiss();
    }

    private void changePassword(CharSequence sequence) {
        Fragment fragment = getTargetFragment();
        if (fragment instanceof DialogListener) {
            DialogListener listener = (DialogListener) fragment;
            listener.onFinishChangePasswordDialog(sequence, getTag());
        }
        dismiss();
    }

    public interface DialogListener {
        void onFinishChangeNameDialog(String name);
        void onFinishChangePasswordDialog(CharSequence sequence, String tag);
    }

    @StringDef({SecurityTypes.HEX, SecurityTypes.ASCII, SecurityTypes.TKIP_OR_AES, SecurityTypes.AES, SecurityTypes.TKIP})
    @Retention(RetentionPolicy.SOURCE)
    @interface SecurityTypes {
        String HEX = "WEP-H";
        String ASCII = "WEP-A";
        String TKIP_OR_AES = "TKIPAES";
        String AES = "AES";
        String TKIP = "TKIP";
    }

    private static class CustomTextWatcher implements TextWatcher {

        private final TextView mTextPasswordLength;
        private final EditText mEditText;
        private final EditTextDialog mDialog;
        @SecurityTypes
        private String mType;

        private CustomTextWatcher(@SecurityTypes String type, EditTextDialog dialog, TextView textPasswordLength, EditText editText) {
            mTextPasswordLength = textPasswordLength;
            mEditText = editText;
            mDialog = dialog;
            mType = type;
        }

        private static String getStringError(@SecurityTypes String type, CharSequence s) {
            String error = null;
            switch (type) {
                case SecurityTypes.HEX:
                    error = s.length() != 0 ? Errors.HEX : null;
                    break;
                case SecurityTypes.ASCII:
                    error = s.length() != 0 ? Errors.ASCII : null;
                    break;
                case SecurityTypes.TKIP_OR_AES:
                    error = Errors.AES_OR_TKIP;
                    break;
                case SecurityTypes.AES:
                    error = getStringError(SecurityTypes.TKIP_OR_AES, s);
                    break;
                case SecurityTypes.TKIP:
                    error = getStringError(SecurityTypes.TKIP_OR_AES, s);
                    break;
            }
            return !isValidByType(type, s) ? error : null;
        }

        private static boolean isValidByType(@SecurityTypes String type, CharSequence s) {
            switch (type) {
                case SecurityTypes.HEX:
                    return Validator.isHexPasskey(s.toString());
                case SecurityTypes.ASCII:
                    return Validator.isASCIIPasseky(s.toString());
                case SecurityTypes.TKIP_OR_AES:
                    int count = s.length();
                    return (count == 0 || (count >= 8 && count <= 63));
                case SecurityTypes.AES:
                    return isValidByType(SecurityTypes.TKIP_OR_AES, s);
                case SecurityTypes.TKIP:
                    return isValidByType(SecurityTypes.TKIP_OR_AES, s);
            }
            throw new IllegalArgumentException("Wrong type!");
        }

        private void changeType(@SecurityTypes String type) {
            mType = type;
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
}
