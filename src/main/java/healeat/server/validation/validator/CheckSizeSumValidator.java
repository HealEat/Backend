package healeat.server.validation.validator;

import healeat.server.validation.annotation.CheckSizeSum;
import healeat.server.web.dto.StoreRequestDto;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class CheckSizeSumValidator implements ConstraintValidator<CheckSizeSum, StoreRequestDto.SearchKeywordDto> {

    @Override
    public void initialize(CheckSizeSum constraintAnnotation) {
        ConstraintValidator.super.initialize(constraintAnnotation);
    }

    @Override
    public boolean isValid(StoreRequestDto.SearchKeywordDto dto, ConstraintValidatorContext context) {

        if (dto == null) {
            return true; // 기본적으로 null은 유효한 값으로 간주
        }

        int categorySize = dto.getCategoryIdList() == null ? 0 : dto.getCategoryIdList().size();
        int featureSize = dto.getFeatureIdList() == null ? 0 : dto.getFeatureIdList().size();

        boolean isValid = (categorySize + featureSize) < 6;

        if (!isValid) {
            context.disableDefaultConstraintViolation();
            context.buildConstraintViolationWithTemplate(
                    "categoryIdList와 featureIdList의 크기 합이 6 이상일 수 없습니다."
            ).addConstraintViolation();
        }

        return isValid;
    }
}
