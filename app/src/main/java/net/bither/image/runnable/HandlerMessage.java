package net.bither.image.runnable;

public class HandlerMessage {
	public static final int MSG_PREPARE = 0;
	public static final int MSG_SUCCESS = 1;
	public static final int MSG_CANCEL = 2;

	// error
	public static final int MSG_FAILURE = 3;
	public static final int MSG_FAILURE_NETWORK = 4;
	public static final int MSG_AUTH_ERROR = 5;
	public static final int MSG_400 = 6;
	public static final int MSG_404 = 7;
	public static final int MSG_FILE_NOT_FOUND = 8;

	// finance
	public static final int MSG_EDIT_FINANCE = 9;
	public static final int MSG_DOWLOAD_FINANCE = 10;
	// other
	public static final int MSG_BEGIN_DELETE = 11;
	public static final int MSG_REGISTER_FLAG = 12;
	public static final int MSG_UPLOAD_AVATAR = 13;
	public static final int MSG_SAVE_FILE_FINISH = 14;
	public static final int MSG_ROM_RXCEPTION = 15;
	public static final int MSG_PIC_INSERT_DB = 16;
	public static final int MSG_SDCARD_EXCEPTION = 17;
	public static final int MSG_THIS_USER_BIND_OTHER_UID = 18;
	public static final int MSG_OTHER_USER_BIND_THIS_UID = 19;
	public static final int MSG_IS_DUPLICATE_REQUEST = 20;
	public static final int MSG_VALID_NO_PASS = 21;

	public static final int MSG_DOWLOAD = 30;

}
