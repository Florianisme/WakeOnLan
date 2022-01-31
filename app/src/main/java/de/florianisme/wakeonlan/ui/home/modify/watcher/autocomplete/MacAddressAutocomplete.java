package de.florianisme.wakeonlan.ui.home.modify.watcher.autocomplete;

import android.text.Editable;
import android.text.TextWatcher;

public class MacAddressAutocomplete implements TextWatcher {

    boolean isDeleting = false;
    boolean shouldAppendColon = false;
    boolean shouldPrependColon = false;

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {
        // We are deleting when amount of characters (count) will have a length (after) of 0 afterwards
        isDeleting = after == 0 && count >= 0;
    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {
        // If maximum length was reached or last digit is a colon
        if (s.length() >= 17 || s.toString().endsWith(":")) {
            shouldAppendColon = false;
            return;
        }

        String[] byteSplit = s.toString().split(":");
        if (byteSplit.length == 0) {
            shouldAppendColon = false;
            return;
        }

        String lastByteSplit = byteSplit[byteSplit.length - 1];

        // if we were just deleting and entered a number without colon
        if (lastByteSplit != null && lastByteSplit.length() == 3) {
            shouldPrependColon = true;
            return;
        }

        shouldAppendColon = lastByteSplit != null && lastByteSplit.length() == 2;
    }

    @Override
    public void afterTextChanged(Editable s) {
        // Order of insert/append and setting booleans to false is very important!
        if (shouldPrependColon && !isDeleting) {
            shouldPrependColon = false;
            s.insert(s.length() - 1, ":");
        } else if (shouldAppendColon && !isDeleting) {
            shouldAppendColon = false;
            s.append(":");
        }
    }
}
