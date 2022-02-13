package de.florianisme.wakeonlan.ui.home.details.watcher.validator;

import android.widget.EditText;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import de.florianisme.wakeonlan.R;

public class IpAddressValidator extends Validator {

    private final Pattern IP_PATTERN = Pattern.compile("^(?:[0-9]{1,3}\\.){3}[0-9]{1,3}$");
    private final boolean inputIsOptional;

    public IpAddressValidator(EditText editTextView) {
        this(editTextView, false);
    }

    public IpAddressValidator(EditText editTextView, boolean inputIsOptional) {
        super(editTextView);
        this.inputIsOptional = inputIsOptional;
    }

    @Override
    public ValidationResult validate(String text) {
        if (inputIsOptional && (text == null || text.trim().isEmpty())) {
            return ValidationResult.VALID;
        }

        Matcher matcher = IP_PATTERN.matcher(text.trim());
        if (matcher.matches()) {
            return ValidationResult.VALID;
        } else {
            return ValidationResult.INVALID;
        }
    }

    @Override
    int getErrorMessageStringId() {
        return R.string.add_device_error_ip_invalid;
    }

}
