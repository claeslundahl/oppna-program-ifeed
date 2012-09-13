package se.vgregion.ifeed.service.exceptions;


public class IFeedServiceException extends RuntimeException {
    private static final long serialVersionUID = 1L;
    private String key;

    public IFeedServiceException(String key, String defaultMessage) {
        super(defaultMessage);
    }

    public IFeedServiceException(String key, String defaultMessage, Throwable cause) {
        super(defaultMessage, cause);
    }

    public String getKey() {
        return key;
    }
}
