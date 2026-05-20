package de.vw.paso.client.base.exception;

import de.vw.paso.delegate.base.ErrorCode;
import de.vw.paso.delegate.base.ErrorCodeParameter;
import de.vw.paso.delegate.base.ErrorCodeUtils;

public abstract class AbstractClientException extends Exception {

  static final ErrorCode UNKNOWN_ERROR = new ErrorCode("default.exception.text", "Unknown error");

  public static AbstractClientException wrap(Throwable t) {
    return new AbstractClientException(t, UNKNOWN_ERROR) {};
  }

	private final ErrorCode errorCode;

	private final Throwable[] nestedThrowables;

  protected AbstractClientException(ErrorCode errorCode) {
		this(null, errorCode);
	}

	protected AbstractClientException(ErrorCode errorCode, String... errorCodeParameters) {
		this(null, errorCode, ErrorCodeUtils.createParam(errorCodeParameters));
	}

  protected AbstractClientException(ErrorCode errorCode, ErrorCodeParameter... parameters) {
		this(null, errorCode, parameters);
	}

  protected AbstractClientException(Throwable cause, ErrorCode errorCode) {
		this(cause, errorCode, new ErrorCodeParameter[] {});
	}

  protected AbstractClientException(Throwable cause, ErrorCode errorCode, String... errorCodeParameters) {
		this(cause, errorCode, ErrorCodeUtils.createParam(errorCodeParameters));
	}

  protected AbstractClientException(Throwable cause, ErrorCode errorCode, ErrorCodeParameter... parameters) {
		super(cause);
		this.errorCode = errorCode;
    this.errorCode.setErrorCodeParameters(parameters);
    this.nestedThrowables = cause.getSuppressed();
  }

	@Override
	public String getMessage() {
		StringBuilder builder = new StringBuilder();
		builder.append("\n\t-------------------------------------------------------------------------------------------------");
		builder.append("\n\tErrorCode:    ").append(errorCode != null ? errorCode.getKey() : "");
		builder.append("\n\tErrorMessage: ").append(errorCode != null ? errorCode.getMessage() : "");
		builder.append("\n\tCause:        ").append(getCause() != null ? getCause().getClass().getName() : "");
		builder.append("\n\tCauseMessage: ").append(getCause() != null ? getCause().getMessage() : "");
		builder.append("\n\t-------------------------------------------------------------------------------------------------");
		return builder.toString();
	}

	public ErrorCode getErrorCode() {
		return errorCode;
	}

	public Throwable[] getNestedThrowables() {
		return nestedThrowables;
	}

}
