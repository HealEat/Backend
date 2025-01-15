package healeat.server.apiPayload.exception.handler;

import healeat.server.apiPayload.code.BaseErrorCode;
import healeat.server.apiPayload.exception.GeneralException;

public class StoreHandler extends GeneralException {
    public StoreHandler(BaseErrorCode code) {
        super(code);
    }
}
