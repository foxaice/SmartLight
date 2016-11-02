package me.foxaice.smartlight.fragments.settings.dialogs;

import android.content.DialogInterface;
import android.text.Editable;
import android.text.TextWatcher;

class CustomTextWatcher {
    static TextWatcher getIPTextWatcher(final ConnectionSettingsDialog dialog) {
        return new TextWatcher() {
            StringBuilder mModifyingText = new StringBuilder();

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (mModifyingText.toString().equals(s.toString())) return;

                dialog.editText.setError(null);
                dialog.alertDialog.getButton(DialogInterface.BUTTON_POSITIVE).setEnabled(true);

                int offset = 15;

                mModifyingText.replace(0, mModifyingText.length(), s.toString());

                for (int i = 1, countDots = 0, countDigitInRow = 0; i < mModifyingText.length(); ) {
                    char prevChar = mModifyingText.charAt(i - 1);
                    char curChar = mModifyingText.charAt(i);

                    if ((i == 1) && (prevChar == '.' || !Character.isDigit(prevChar))) {
                        mModifyingText.deleteCharAt(0);
                        continue;
                    }

                    if ((!Character.isDigit(curChar) && curChar != '.') || (curChar == '.' && (prevChar == curChar || countDots > 2))) {
                        mModifyingText.deleteCharAt(i);
                        continue;
                    } else {
                        if (Character.isDigit(prevChar) && Character.isDigit(curChar)) {
                            countDigitInRow++;
                            if (countDigitInRow == 1 && prevChar == '0') {
                                mModifyingText.deleteCharAt(i - 1);
                                countDigitInRow = 0;
                                continue;
                            } else if (countDigitInRow == 2) {
                                int quarterOfIP = Integer.valueOf(mModifyingText.substring(i - 2, i + 1));
                                if (quarterOfIP > 255) {
                                    mModifyingText.replace(i - 2, i + 1, "255");
                                }
                            } else if (countDigitInRow > 2) {
                                if (countDots > 2) {
                                    offset = i;
                                    break;
                                }
                                mModifyingText.insert(i, '.');
                                countDigitInRow = 0;
                                continue;
                            }
                        } else {
                            countDigitInRow = 0;
                        }
                        i++;
                    }
                    if (i > offset) break;
                    if (curChar == '.') countDots++;
                }

                if (mModifyingText.length() > offset) {
                    mModifyingText.delete(offset, mModifyingText.length());
                }

                dialog.editText.setText(mModifyingText);
                dialog.editText.setSelection(mModifyingText.length());
            }
        };
    }

    static TextWatcher getPortTextWatcher(final ConnectionSettingsDialog dialog) {
        return new TextWatcher() {
            StringBuilder mModifyingText = new StringBuilder();

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (mModifyingText.toString().equals(s.toString())) return;

                dialog.editText.setError(null);
                dialog.alertDialog.getButton(DialogInterface.BUTTON_POSITIVE).setEnabled(true);

                int offset = 5;

                mModifyingText.replace(0, mModifyingText.length(), s.toString());

                for (int i = 0; i < mModifyingText.length(); ) {
                    char curChar = mModifyingText.charAt(i);
                    if (!Character.isDigit(curChar)) {
                        mModifyingText.deleteCharAt(i);
                    } else {
                        i++;
                    }
                    if (i > offset) break;
                }

                if (mModifyingText.length() > offset) {
                    mModifyingText.delete(offset, mModifyingText.length());
                }

                if (mModifyingText.length() > 0) {
                    int port = Integer.valueOf(mModifyingText.toString());
                    if (port > 0xFFFF) {
                        port = 0xFFFF;
                    }
                    dialog.editText.setText(String.valueOf(port));
                } else {
                    dialog.editText.setText(mModifyingText);
                }
                dialog.editText.setSelection(mModifyingText.length());
            }
        };
    }
}
