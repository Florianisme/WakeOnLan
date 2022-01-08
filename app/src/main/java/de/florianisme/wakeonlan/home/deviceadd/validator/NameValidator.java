package de.florianisme.wakeonlan.home.deviceadd.validator;

import android.widget.EditText;

import de.florianisme.wakeonlan.R;

public class NameValidator extends Validator {

    public NameValidator(EditText editTextView) {
        super(editTextView);
    }

    @Override
    public ValidationResult validate(String text) {
        return null == text || text.isEmpty() ? ValidationResult.INVALID : ValidationResult.VALID;
    }

    @Override
    int getErrorMessageStringId() {
        return R.string.add_device_error_name_empty;
    }

}
