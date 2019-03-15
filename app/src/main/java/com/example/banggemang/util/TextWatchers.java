package com.example.banggemang.util;

import android.text.Editable;
import android.text.TextWatcher;

public class TextWatchers {
    public static class money implements TextWatcher {
        private String resetText;

        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            resetText = s.toString();
        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            String text = s.toString();
            if (text.isEmpty()) {
                resetText = null;
                return;
            }
            if (text.equals(".")) {
                text = "0.";
            }
            String[] texts = text.split("\\.");
            if (texts.length > 2 || texts.length <= 0) {
                return;
            }
            if (texts.length == 2 && texts[1].length() > 2) {
                return;
            }
            int nZero = 0;
            for (int i = 0; i < texts[0].length(); ++i) {
                if (texts[0].charAt(i) != '0') {
                    break;
                }
                ++nZero;
            }
            if (nZero != 0 && !texts[0].equals("0")) {
                return;
            }
            if (text.equals(s.toString())) {
                resetText = null;
            } else {
                resetText = text;
            }
        }

        @Override
        public void afterTextChanged(Editable s) {
            if (resetText != null) {
                s.replace(0, s.length(), resetText);
            }
        }
    }
}
