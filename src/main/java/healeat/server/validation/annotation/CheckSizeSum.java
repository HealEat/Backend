package healeat.server.validation.annotation;

import healeat.server.validation.validator.CheckPageValidator;
import healeat.server.validation.validator.CheckSizeSumValidator;
import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.*;

@Documented
@Constraint(validatedBy = CheckSizeSumValidator.class)
@Target({ElementType.TYPE, ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
public @interface CheckSizeSum {
    String message() default "키워드 필터는 5개까지만 가능합니다.";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}