package de.florianisme.wakeonlan.ui.modify.watcher.validator;

import android.widget.EditText;

import java.util.List;
import java.util.function.Supplier;

public class ConditionalInputNotEmptyValidator extends InputNotEmptyValidator {

    private final List<Supplier<Boolean>> conditionalSupplierList;

    public ConditionalInputNotEmptyValidator(EditText editTextView, int errorMessageStringId, List<Supplier<Boolean>> conditionalSupplierList) {
        super(editTextView, errorMessageStringId);
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

}
