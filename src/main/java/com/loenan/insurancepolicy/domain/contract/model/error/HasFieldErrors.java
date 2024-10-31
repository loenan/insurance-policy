package com.loenan.insurancepolicy.domain.contract.model.error;

import java.util.List;

public interface HasFieldErrors {
    List<FieldError> getFieldErrors();
}
