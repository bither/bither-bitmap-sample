package net.bither.image.exception;

public class Http404Exception extends HttpException {

	private static final long serialVersionUID = 1L;

	public Http404Exception(Exception cause) {
		super(cause);
	}

	public Http404Exception(String msg, int statusCode) {
		super(msg, statusCode);
	}

	public Http404Exception(String msg, Exception cause, int statusCode) {
		super(msg, cause, statusCode);

	}

	public Http404Exception(String msg, Exception cause) {
		super(msg, cause);
	}

	public Http404Exception(String msg) {
		super(msg);
	}

}
