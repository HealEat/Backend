package healeat.server.validation.annotation;

import healeat.server.validation.validator.CheckPageValidator;
import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.*;

@Documented
@Constraint(validatedBy = CheckPageValidator.class)
@Target({ElementType.METHOD, ElementType.FIELD, ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
public @interface CheckPage {
  String message() default "페이지 번호는 1 이상이어야 합니다.";
  Class<?>[] groups() default {};
  Class<? extends Payload>[] payload() default {};
}
