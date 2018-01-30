package cn.gateway.core.exception;

@SuppressWarnings("serial")
public class DuplicateSubmitException extends RuntimeException {
	public DuplicateSubmitException() {
		super("重复提交");
	}

	public DuplicateSubmitException(String msg) {
		super(msg);
	}

	public DuplicateSubmitException(Throwable cause) {
		super("重复提交", cause);
	}

	public DuplicateSubmitException(String msg, Throwable cause) {
		super(msg, cause);
	}

}
