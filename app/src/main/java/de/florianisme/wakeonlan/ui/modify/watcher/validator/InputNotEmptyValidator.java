package de.florianisme.wakeonlan.ui.modify.watcher.validator;

import android.widget.EditText;

public class InputNotEmptyValidator extends Validator {

    private final int errorMessageStringId;

    public InputNotEmptyValidator(EditText editTextView, int errorMessageStringId) {
        super(editTextView);
        this.errorMessageStringId = errorMessageStringId;
    }

    @Override
    public ValidationResult validate(String text) {
        return null == text || text.isEmpty() ? ValidationResult.INVALID : ValidationResult.VALID;
    }

    @Override
    int getErrorMessageStringId() {
        return errorMessageStringId;
    }

}
