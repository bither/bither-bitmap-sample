package net.bither.image.exception;

public class Http400Exception extends HttpException {
	private static final long serialVersionUID = 1L;

	public Http400Exception(Exception cause) {
		super(cause);
	}

	public Http400Exception(String msg, int statusCode) {
		super(msg, statusCode);
	}

	public Http400Exception(String msg, Exception cause, int statusCode) {
		super(msg, cause, statusCode);

	}

	public Http400Exception(String msg, Exception cause) {
		super(msg, cause);
	}

	public Http400Exception(String msg) {
		super(msg);
	}
}
