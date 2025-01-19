package healeat.server.apiPayload.exception.handler;

import healeat.server.apiPayload.code.BaseErrorCode;
import healeat.server.apiPayload.exception.GeneralException;

public class FoodFeatureHandler extends GeneralException {
    public FoodFeatureHandler(BaseErrorCode code) {
        super(code);
    }
}
