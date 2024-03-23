package de.florianisme.wakeonlan.ui.modify.watcher.validator;

import android.widget.EditText;

import com.google.common.base.Strings;

import de.florianisme.wakeonlan.R;

public class PortValidator extends Validator {

    public PortValidator(EditText editTextView) {
        super(editTextView);
    }

    @Override
    public ValidationResult validate(String wakePort) {
        try {
            if (Strings.nullToEmpty(wakePort).isEmpty()) {
                return ValidationResult.VALID;
            }
            int parsedPort = Integer.parseInt(wakePort);

            return parsedPort > 0 && parsedPort <= 65535 ? ValidationResult.VALID : ValidationResult.INVALID;
        } catch (NumberFormatException e) {
            return ValidationResult.INVALID;
        }
    }

    @Override
    int getErrorMessageStringId() {
        return R.string.add_device_error_port_invalid;
    }

}
