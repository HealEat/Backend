package healeat.server.apiPayload.exception.handler;

import healeat.server.apiPayload.code.BaseErrorCode;
import healeat.server.apiPayload.exception.GeneralException;

public class MemberHealthInfoHandler extends GeneralException {

    public MemberHealthInfoHandler(BaseErrorCode errorCode) {
        super(errorCode);
    }
}
