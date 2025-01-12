package healeat.server.apiPayload.exception.handler;

import healeat.server.apiPayload.code.BaseErrorCode;
import healeat.server.apiPayload.exception.GeneralException;

public class HealthPlanHandler extends GeneralException {

    public HealthPlanHandler(BaseErrorCode code) {
        super(code);
    }
}
