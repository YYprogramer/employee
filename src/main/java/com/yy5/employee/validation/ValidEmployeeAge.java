package com.yy5.employee.validation;

import jakarta.validation.Payload;
import jakarta.validation.ReportAsSingleViolation;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Deprecated
@NotNull
@NotBlank
@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
@ReportAsSingleViolation

public @interface ValidEmployeeAge {
    String message() default "無効な年齢です";

    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}
