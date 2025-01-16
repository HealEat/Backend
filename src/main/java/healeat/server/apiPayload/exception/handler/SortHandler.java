package healeat.server.apiPayload.exception.handler;

import healeat.server.apiPayload.code.BaseErrorCode;
import healeat.server.apiPayload.exception.GeneralException;

public class SortHandler extends GeneralException {

  public SortHandler(BaseErrorCode code) {
    super(code);
  }
}
