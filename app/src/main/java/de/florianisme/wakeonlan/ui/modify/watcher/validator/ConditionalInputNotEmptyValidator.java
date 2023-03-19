package de.florianisme.wakeonlan.ui.modify.watcher.validator;

import android.widget.EditText;

import java.util.List;
import java.util.function.Supplier;

import de.florianisme.wakeonlan.R;

public class ConditionalInputNotEmptyValidator extends Validator {

    private final List<Supplier<Boolean>> conditionalSupplierList;

    public ConditionalInputNotEmptyValidator(EditText editTextView, List<Supplier<Boolean>> conditionalSupplierList) {
        super(editTextView);
        this.conditionalSupplierList = conditionalSupplierList;
    }

    @Override
    public ValidationResult validate(String text) {
        if (inputShouldBeValidated()) {
            return null == text || text.isEmpty() ? ValidationResult.INVALID : ValidationResult.VALID;
        } else {
            return ValidationResult.VALID;
        }
    }

    private boolean inputShouldBeValidated() {
        return conditionalSupplierList.stream().allMatch(Supplier::get);
    }

    @Override
    int getErrorMessageStringId() {
        return R.string.add_device_error_name_empty;
    }

}
