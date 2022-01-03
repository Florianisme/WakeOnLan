package de.florianisme.wakeonlan.home.deviceadd.validator;

import android.text.Editable;
import android.text.TextWatcher;
import android.widget.EditText;

public abstract class Validator implements TextWatcher {

    private final EditText editTextView;

    protected Validator(EditText editTextView) {
        this.editTextView = editTextView;
    }

    abstract ValidationResult validate(String text);

    abstract int getErrorMessageStringId();

    protected String getErrorMessage() {
        return editTextView.getContext().getString(getErrorMessageStringId());
    }

    @Override
    public void afterTextChanged(Editable editable) {
        ValidationResult validate = validate(editable.toString());
        if (validate == ValidationResult.VALID) {
            editTextView.setError(null);
        } else {
            editTextView.setError(getErrorMessage());
        }
    }

    public boolean isValid() {
        return validate(editTextView.getText().toString()) == ValidationResult.VALID;
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {
        // Do nothing
    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {
        // Do nothing
    }
}
