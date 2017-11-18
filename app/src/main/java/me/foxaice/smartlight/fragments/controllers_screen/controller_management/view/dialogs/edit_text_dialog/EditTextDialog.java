package me.foxaice.smartlight.fragments.controllers_screen.controller_management.view.dialogs.edit_text_dialog;

import android.accessibilityservice.AccessibilityService;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.inputmethodservice.Keyboard;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.StringDef;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.text.InputType;
import android.text.TextWatcher;
import android.text.method.PasswordTransformationMethod;
import android.view.View;
import android.view.ViewParent;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

import me.foxaice.smartlight.R;

public class EditTextDialog extends DialogFragment {
    private static final String TAG_NAME = "CHANGE_NAME_DIALOG";
    private static final String TAG_PASS_AP = "CHANGE_PASSWORD_AP_DIALOG";
    private static final String TAG_PASS_STA = "CHANGE_PASSWORD_STA_DIALOG";
    private static final String EXTRA_STA_SSID = "EXTRA_STA_SSID";
    private static final String EXTRA_STA_SECURITY = "EXTRA_STA_SECURITY";
    private static final String ERROR_TAG = "WRONG_TAG!";
    private static final int REQUEST_CODE = 300;
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

    public interface DialogListener {
        void onFinishChangeNameDialog(String name);
        void onFinishChangeStaPasswordDialog(CharSequence sequence);
        void onFinishChangeApPasswordDialog(CharSequence sequence);
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

    public static void showChangeStaPassword(Fragment fragment, String ssid, String security) {
        EditTextDialog.showDialog(fragment,
                TAG_PASS_STA,
                new Intent()
                        .putExtra(EXTRA_STA_SSID, ssid)
                        .putExtra(EXTRA_STA_SECURITY, security)
                        .getExtras()
        );
    }

    public static void showChangeApPassword(Fragment fragment) {
        EditTextDialog.showDialog(fragment, TAG_PASS_AP, null);
    }

    public static void showChangeName(Fragment fragment) {
        EditTextDialog.showDialog(fragment, TAG_NAME, null);
    }

    private static void showDialog(Fragment fragment, String tag, Bundle args) {
        EditTextDialog dialog = new EditTextDialog();
        dialog.setTargetFragment(fragment, REQUEST_CODE);
        if (tag.equals(TAG_PASS_STA)) {
            dialog.setArguments(args);
        }
        dialog.show(fragment.getFragmentManager(), tag);
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        if (savedInstanceState != null) dismiss();
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        View view = View.inflate(getActivity(), R.layout.fragment_controller_management_edit_text_dialog, null);
        initViews(view);
        if (getTag() != null) {
            prepareContent(getTag());
            builder.setView(view)
                    .setNegativeButton(R.string.dialog_negative_button_text, null)
                    .setPositiveButton(R.string.dialog_positive_button_text, getPositiveButtonClickListener(getTag()));
        }
        showKeyboard();
        return builder.create();
    }

    private void initViews(View view) {
        mTextNameDialog = (TextView) view.findViewById(R.id.fragment_controller_management_dialog_text_name);
        mTextPasswordLength = (TextView) view.findViewById(R.id.fragment_controller_management_dialog_text_password_quantity_chars);
        mEditText = (EditText) view.findViewById(R.id.fragment_controller_management_dialog_edit_name);
        mSpinnerEncryptionMethod = (Spinner) view.findViewById(R.id.fragment_controller_management_dialog_spinner_encryption_method);
        mSpinnerEncryptionType = (Spinner) view.findViewById(R.id.fragment_controller_management_dialog_spinner_encryption_type);
    }

    private DialogInterface.OnClickListener getPositiveButtonClickListener(@NonNull String tag) {
        switch (tag) {
            case TAG_NAME:
                return new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        hideKeyboard();
                        changeName(mEditText.getText().toString());
                    }
                };
            case TAG_PASS_AP:
                return new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        hideKeyboard();
                        changePassword(mEditText.getText());
                    }
                };
            case TAG_PASS_STA:
                return new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        hideKeyboard();
                        String params = String.format("%s|||%s,%s,%s", mSTASSID, mSTAEncryptionMethod, mSTAEncryptionType, mEditText.getText());
                        changePassword(params);
                    }
                };
            default:
                throw new IllegalArgumentException(ERROR_TAG);
        }
    }

    private void prepareContent(@NonNull String tag) {
        switch (tag) {
            case TAG_NAME:
                prepareChangeNameContent();
                return;
            case TAG_PASS_AP:
                prepareChangePassAPContent();
                return;
            case TAG_PASS_STA:
                prepareChangePassSTAContent();
        }
    }

    private void hideKeyboard() {
        InputMethodManager im = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
        if (im != null) {
            im.hideSoftInputFromWindow(mEditText.getWindowToken(), 0);
        }
    }

    private void showKeyboard() {
        InputMethodManager im = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
        if (im != null) {
            im.showSoftInput(mEditText, InputMethodManager.SHOW_IMPLICIT);
        }
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

    private void prepareChangePassSTAContent() {
        Bundle args = getArguments();
        if (args != null) {
            mSTASSID = args.getString(EXTRA_STA_SSID);
            String security = args.getString(EXTRA_STA_SECURITY);
            if (security != null) {
                String[] array = security.split("/");
                mSTAEncryptionMethod = array[0];
                mSTAEncryptionType = array.length > 1 ? array[1] : SecurityTypes.ASCII;
            }
            mTextNameDialog.setText(mSTASSID);
            mTextPasswordLength.setText(getString(R.string.password_length, "NONE"));
            mTextPasswordLength.setVisibility(View.VISIBLE);
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
                ViewParent parent = mSpinnerEncryptionMethod.getParent();
                if (parent instanceof View) {
                    ((View) parent).setVisibility(View.VISIBLE);
                }

                mSpinnerEncryptionMethod.setOnItemSelectedListener(new OnItemSelectedListenerAdapter() {
                    @Override
                    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                        mSTAEncryptionMethod = parent.getItemAtPosition(position).toString();
                    }
                });
                mSpinnerEncryptionType.setOnItemSelectedListener(new OnItemSelectedListenerAdapter() {
                    @Override
                    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                        mSTAEncryptionType = "ASCII".equals(parent.getItemAtPosition(position)) ?
                                SecurityTypes.ASCII : SecurityTypes.HEX;
                        ((CustomTextWatcher) mTextWatcher).changeType(mSTAEncryptionType);
                        mEditText.setText(null);
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
            ((DialogListener) fragment).onFinishChangeNameDialog(name);
        }
        dismiss();
    }

    private void changePassword(CharSequence sequence) {
        Fragment fragment = getTargetFragment();
        if (fragment instanceof DialogListener) {
            switch (getTag()) {
                case TAG_PASS_STA:
                    ((DialogListener) fragment).onFinishChangeStaPasswordDialog(sequence);
                    break;
                case TAG_PASS_AP:
                    ((DialogListener) fragment).onFinishChangeApPasswordDialog(sequence);
            }
        }
        dismiss();
    }

    private static class OnItemSelectedListenerAdapter implements AdapterView.OnItemSelectedListener {
        @Override
        public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {}

        @Override
        public void onNothingSelected(AdapterView<?> parent) {}
    }
}
