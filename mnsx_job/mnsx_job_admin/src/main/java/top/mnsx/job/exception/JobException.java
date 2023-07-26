package top.mnsx.job.exception;

public class JobException extends RuntimeException {
    private String errMessage;

    public JobException() {
        super();
    }

    public JobException(String message) {
        super(message);
        this.errMessage = message;
    }

    public String getErrMessage() {
        return errMessage;
    }

    public static void cast(String errMessage) {
        throw new JobException(errMessage);
    }
}