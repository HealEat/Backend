package healeat.server.apiPayload.exception.handler;

import healeat.server.apiPayload.code.BaseErrorCode;
import healeat.server.apiPayload.exception.GeneralException;

public class ReviewHandler extends GeneralException {

    public ReviewHandler(BaseErrorCode code) {
        super(code);
    }
}
