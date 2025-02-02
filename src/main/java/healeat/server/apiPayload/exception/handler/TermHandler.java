package healeat.server.apiPayload.exception.handler;

import healeat.server.apiPayload.code.BaseErrorCode;
import healeat.server.apiPayload.exception.GeneralException;

public class TermHandler extends GeneralException {
    public TermHandler(BaseErrorCode code) {
        super(code);
    }
}
