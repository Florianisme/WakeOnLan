package de.florianisme.wakeonlan.home.deviceadd.validator;

import android.widget.EditText;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import de.florianisme.wakeonlan.R;

public class BroadcastValidator extends Validator {

    private final Pattern IP_PATTERN = Pattern.compile("^(?:[0-9]{1,3}\\.){3}[0-9]{1,3}$");

    public BroadcastValidator(EditText editTextView) {
        super(editTextView);
    }

    @Override
    public ValidationResult validate(String text) {
        Matcher matcher = IP_PATTERN.matcher(text);
        if (matcher.matches()) {
            return ValidationResult.VALID;
        } else {
            return ValidationResult.INVALID;
        }
    }

    @Override
    int getErrorMessageStringId() {
        return R.string.add_machine_error_broadcast_invalid;
    }

}
