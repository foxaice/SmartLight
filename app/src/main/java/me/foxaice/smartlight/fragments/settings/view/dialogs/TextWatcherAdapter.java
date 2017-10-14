package me.foxaice.smartlight.fragments.settings.view.dialogs;

import android.content.DialogInterface;
import android.text.Editable;
import android.text.TextWatcher;

abstract class TextWatcherAdapter implements TextWatcher {
    StringBuilder modifyingText;
    int offset;
    private final int mDefaultOffset;
    private final ConnectionSettingsDialog dialog;

    TextWatcherAdapter(ConnectionSettingsDialog dialog, int offset) {
        this.dialog = dialog;
        mDefaultOffset = offset;
        modifyingText = new StringBuilder();
    }

    static TextWatcher initializePortTextWatcher(ConnectionSettingsDialog dialog) {
        return new PortTextWatcher(dialog);
    }

    static TextWatcher initializeIpAddressTextWatcher(ConnectionSettingsDialog dialog) {
        return new IpAddressTextWatcher(dialog);
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {}

    @Override
    public void afterTextChanged(Editable s) {
        if (modifyingText.toString().equals(s.toString())) return;
        offset = mDefaultOffset;
        allowSaving();
        editModifyingText(s.toString());
        checkCorrectness(modifyingText);
        deleteModifyingTextItemsAfterOffset(offset);
        setDialogText(modifyingText);
        moveCursorToTextEnd();
    }

    abstract void checkCorrectness(StringBuilder s);

    void setDialogText(CharSequence s) {
        dialog.editText.setText(s);
    }

    private void allowSaving() {
        dialog.editText.setError(null);
        dialog.alertDialog.getButton(DialogInterface.BUTTON_POSITIVE).setEnabled(true);
    }

    private void editModifyingText(String s) {
        modifyingText.replace(0, modifyingText.length(), s);
    }

    private void deleteModifyingTextItemsAfterOffset(int offset) {
        if (modifyingText.length() > offset) {
            modifyingText.delete(offset, modifyingText.length());
        }
    }

    private void moveCursorToTextEnd() {
        dialog.editText.setSelection(modifyingText.length());
    }
}

class IpAddressTextWatcher extends TextWatcherAdapter {
    private final static int OFFSET = 15;

    IpAddressTextWatcher(ConnectionSettingsDialog dialog) {
        super(dialog, OFFSET);
    }

    @Override
    void checkCorrectness(StringBuilder s) {
        for (int i = 1, countDots = 0, countDigitInRow = 0; i < modifyingText.length(); ) {
            char prevChar = modifyingText.charAt(i - 1);
            char curChar = modifyingText.charAt(i);

            if ((i == 1) && (prevChar == '.' || !Character.isDigit(prevChar))) {
                modifyingText.deleteCharAt(0);
                continue;
            }

            if ((!Character.isDigit(curChar) && curChar != '.') || (curChar == '.' && (prevChar == curChar || countDots > 2))) {
                modifyingText.deleteCharAt(i);
                continue;
            } else {
                if (Character.isDigit(prevChar) && Character.isDigit(curChar)) {
                    countDigitInRow++;
                    if (countDigitInRow == 1 && prevChar == '0') {
                        modifyingText.deleteCharAt(i - 1);
                        countDigitInRow = 0;
                        continue;
                    } else if (countDigitInRow == 2) {
                        int quarterOfIP = Integer.valueOf(modifyingText.substring(i - 2, i + 1));
                        if (quarterOfIP > 255) {
                            modifyingText.replace(i - 2, i + 1, "255");
                        }
                    } else if (countDigitInRow > 2) {
                        if (countDots > 2) {
                            offset = i;
                            break;
                        }
                        modifyingText.insert(i, '.');
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
    }
}

class PortTextWatcher extends TextWatcherAdapter {
    private static final int OFFSET = 5;

    PortTextWatcher(ConnectionSettingsDialog dialog) {
        super(dialog, OFFSET);
    }

    @Override
    void checkCorrectness(StringBuilder s) {
        for (int i = 0; i < s.length(); ) {
            char curChar = s.charAt(i);
            if (!Character.isDigit(curChar)) {
                s.deleteCharAt(i);
            } else {
                i++;
            }
            if (i > offset) break;
        }
    }

    @Override
    void setDialogText(CharSequence s) {
        if (s.length() > 0) {
            int port = Integer.valueOf(s.toString());
            if (port > 0xFFFF) {
                port = 0xFFFF;
            }
            super.setDialogText(String.valueOf(port));
        } else {
            super.setDialogText(s);
        }
    }
}