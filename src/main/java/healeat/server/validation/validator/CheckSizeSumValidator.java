package healeat.server.validation.validator;

import healeat.server.apiPayload.code.status.ErrorStatus;
import healeat.server.validation.annotation.CheckSizeSum;
import healeat.server.web.dto.StoreRequestDto;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class CheckSizeSumValidator implements ConstraintValidator<CheckSizeSum, StoreRequestDto.SearchFilterDto> {

    @Override
    public void initialize(CheckSizeSum constraintAnnotation) {
        ConstraintValidator.super.initialize(constraintAnnotation);
    }

    @Override
    public boolean isValid(StoreRequestDto.SearchFilterDto dto, ConstraintValidatorContext context) {

        if (dto == null) {
            return true; // 기본적으로 null은 유효한 값으로 간주
        }

        boolean isValid = dto.getTotalFilterSize() < 6;

        if (!isValid) {
            context.disableDefaultConstraintViolation();
            context.buildConstraintViolationWithTemplate(ErrorStatus.FILTERS_LESS_EQUAL_THAN_5.toString())
                    .addConstraintViolation();
        }

        return isValid;
    }
}
