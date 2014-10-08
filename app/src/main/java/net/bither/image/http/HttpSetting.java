package net.bither.image.http;

public class HttpSetting {
    /**
     * HTTP_CONNECTION_TIMEOUT: Set the timeout in milliseconds until a
     * connection is established. The default value is zero, that means the
     * timeout is not used.
     */
    public static final int HTTP_CONNECTION_TIMEOUT = 5 * 1000;
    /**
     * HTTP_SO_TIMEOUT: Set the default socket timeout (SO_TIMEOUT). in
     * milliseconds which is the timeout for waiting for data.
     */
    public static final int HTTP_SO_TIMEOUT = 7 * 1000;

    // session
    public static final String SESSION_ID = "sessionid";
    public static final String CSRF_TOKEN = "csrftoken";
    public static final String TOKEN = "token";
    public static final String CSRF_MIDDLE_WARE_TOKEN = "csrfmiddlewaretoken";


}
