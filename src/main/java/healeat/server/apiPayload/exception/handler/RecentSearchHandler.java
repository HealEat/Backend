package healeat.server.apiPayload.exception.handler;

import healeat.server.apiPayload.code.BaseErrorCode;
import healeat.server.apiPayload.exception.GeneralException;

public class RecentSearchHandler extends GeneralException {
    public RecentSearchHandler(BaseErrorCode code) {super(code);}
}
