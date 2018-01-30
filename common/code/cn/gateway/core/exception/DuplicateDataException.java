package cn.gateway.core.exception;

@SuppressWarnings("serial")
public class DuplicateDataException extends RuntimeException {
	public DuplicateDataException() {
		super("重复数据");
	}

	public DuplicateDataException(String msg) {
		super(msg);
	}

	public DuplicateDataException(Throwable cause) {
		super("重复数据", cause);
	}

	public DuplicateDataException(String msg, Throwable cause) {
		super(msg, cause);
	}

}
