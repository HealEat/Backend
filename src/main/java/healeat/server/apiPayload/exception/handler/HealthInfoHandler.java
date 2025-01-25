package healeat.server.apiPayload.exception.handler;

import healeat.server.apiPayload.code.BaseErrorCode;
import healeat.server.apiPayload.exception.GeneralException;

public class HealthInfoHandler extends GeneralException {

    public HealthInfoHandler(BaseErrorCode errorCode) {
        super(errorCode);
    }
}
