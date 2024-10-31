package com.loenan.insurancepolicy.domain.core.validation;

import com.loenan.insurancepolicy.domain.contract.exception.InvalidInputException;
import com.loenan.insurancepolicy.domain.contract.model.error.FieldError;
import org.apache.commons.lang3.EnumUtils;
import org.apache.commons.lang3.StringUtils;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.function.BooleanSupplier;
import java.util.function.Supplier;
import java.util.stream.Collectors;

public class ValidationUtil {

    public static final String NO_VALUE_PROVIDED_ERROR_MESSAGE = "No value was provided for the required field %s";
    public static final String BLANK_STRING_PROVIDED_ERROR_MESSAGE = "The provided value was blank for the field %s";
    public static final String STRING_VALUE_TOO_LONG_ERROR_MESSAGE = "The field %s cannot contain more then %d characters";
    public static final String INVALID_ENUM_VALUE_ERROR_MESSAGE = "The value for the field %s can only one of: %s";
    public static final String INVALID_DATE_ORDER_ERROR_MESSAGE = "The date value for the field %s must be after the value of the field %s";

    @SafeVarargs
    public static void validateInput(Optional<FieldError>... fieldErrors) throws InvalidInputException {
        List<FieldError> errors = Arrays.stream(fieldErrors)
            .filter(Optional::isPresent)
            .map(Optional::get)
            .toList();
        if (!errors.isEmpty()) {
            throw new InvalidInputException(errors);
        }
    }

    public static Optional<FieldError> validateMandatoryValue(Object fieldValue, String fieldName) {
        return validate(
            fieldName,
            () -> Objects.nonNull(fieldValue),
            () -> NO_VALUE_PROVIDED_ERROR_MESSAGE.formatted(fieldName)
        );
    }

    public static Optional<FieldError> validateMandatoryNotBlankString(String fieldValue, String fieldName) {
        return validateMandatoryValue(fieldValue, fieldName)
            .or(() -> validate(
                fieldName,
                () -> StringUtils.isNotBlank(fieldValue),
                () -> BLANK_STRING_PROVIDED_ERROR_MESSAGE.formatted(fieldName)
            ));
    }

    public static Optional<FieldError> validateStringLength(String fieldValue, int maxLength, String fieldName) {
        return validate(
            fieldName,
            () -> Objects.isNull(fieldValue) || fieldValue.length() <= maxLength,
            () -> STRING_VALUE_TOO_LONG_ERROR_MESSAGE.formatted(fieldName, maxLength)
        );
    }

    public static <T extends Enum<T>> Optional<FieldError> validateEnumValue(String fieldValue, Class<T> enumType, String fieldName) {
        return validate(
            fieldName,
            () -> Objects.isNull(fieldValue) || EnumUtils.isValidEnum(enumType, fieldValue),
            () -> INVALID_ENUM_VALUE_ERROR_MESSAGE.formatted(fieldName, getValidEnumValues(enumType))
        );
    }

    private static <T extends Enum<T>> String getValidEnumValues(Class<T> enumType) {
        return EnumUtils.getEnumList(enumType).stream()
            .map(Enum::name)
            .collect(Collectors.joining(", "));
    }

    public static Optional<FieldError> validateDateOrder(
        LocalDate startDate,
        LocalDate endDate,
        String startDateFieldName,
        String endDateFieldName
    ) {
        return validate(
            endDateFieldName,
            () -> Objects.isNull(startDate) || Objects.isNull(endDate) || endDate.isAfter(startDate),
            () -> INVALID_DATE_ORDER_ERROR_MESSAGE.formatted(endDateFieldName, startDateFieldName)
        );
    }

    private static Optional<FieldError> validate(
        String fieldName,
        BooleanSupplier condition,
        Supplier<String> errorMessageSupplier
    ) {
        if (condition.getAsBoolean()) {
            return Optional.empty();
        } else {
            return Optional.of(new FieldError(fieldName, errorMessageSupplier.get()));
        }
    }
}
