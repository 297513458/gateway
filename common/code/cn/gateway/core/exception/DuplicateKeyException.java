package cn.gateway.core.exception;

@SuppressWarnings("serial")
public class DuplicateKeyException extends RuntimeException {

	public DuplicateKeyException() {
		super("重复键");
	}

	public DuplicateKeyException(String msg) {
		super(msg);
	}

	public DuplicateKeyException(String msg, Throwable cause) {
		super(msg, cause);
	}

	public DuplicateKeyException(Throwable cause) {
		super("重复键", cause);
	}
}