package de.florianisme.wakeonlan.home.deviceadd.validator;

import android.widget.EditText;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import de.florianisme.wakeonlan.R;

public class MacValidator extends Validator {

    private final Pattern MAC_PATTERN = Pattern.compile("^([0-9A-Fa-f]{2}[:-]){5}([0-9A-Fa-f]{2})$");

    public MacValidator(EditText editTextView) {
        super(editTextView);
    }

    @Override
    public ValidationResult validate(String text) {
        Matcher matcher = MAC_PATTERN.matcher(text);
        if (matcher.matches()) {
            return ValidationResult.VALID;
        } else {
            return ValidationResult.INVALID;
        }
    }

    @Override
    int getErrorMessageStringId() {
        return R.string.add_machine_error_mac_invalid;
    }

}
