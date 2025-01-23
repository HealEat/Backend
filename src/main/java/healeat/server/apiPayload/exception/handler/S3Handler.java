package healeat.server.apiPayload.exception.handler;

import healeat.server.apiPayload.code.BaseErrorCode;
import healeat.server.apiPayload.exception.GeneralException;

public class S3Handler extends GeneralException {
  public S3Handler(BaseErrorCode code) {
    super(code);
  }
}
