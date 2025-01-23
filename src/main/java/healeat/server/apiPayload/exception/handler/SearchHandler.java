package healeat.server.apiPayload.exception.handler;

import healeat.server.apiPayload.code.BaseErrorCode;
import healeat.server.apiPayload.exception.GeneralException;

public class SearchHandler extends GeneralException {

    public SearchHandler(BaseErrorCode code) {
        super(code);
    }
}