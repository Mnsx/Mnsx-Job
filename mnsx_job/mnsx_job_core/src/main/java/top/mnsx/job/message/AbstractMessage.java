package top.mnsx.job.message;

import lombok.Data;

import java.io.Serializable;
import java.util.concurrent.ConcurrentHashMap;

@Data
public abstract class AbstractMessage implements Serializable {
    // 序列号
    private Integer sequenceId;
    // 消息类型
    private Integer messageType;

    // 消息集合
    private static final ConcurrentHashMap<Integer, Class<? extends AbstractMessage>> ALL_MESSAGE = new ConcurrentHashMap<>();

    public static final int JOB_RESPONSE = 1000;
    public static final int APP_TO_ADMIN_REQUEST = 1001;
    public static final int PING_MESSAGE_REQUEST = 1002;
    public static final int JOB_INVOKE_REQUEST = 1003;
    public static final int JOB_INVOKE_RESPONSE = 1004;

    static {
        ALL_MESSAGE.put(JOB_RESPONSE, JobResponse.class);
        ALL_MESSAGE.put(APP_TO_ADMIN_REQUEST, AppToAdminRequest.class);
        ALL_MESSAGE.put(PING_MESSAGE_REQUEST, PingMessage.class);
        ALL_MESSAGE.put(JOB_INVOKE_REQUEST, JobInvokeRequest.class);
        ALL_MESSAGE.put(JOB_INVOKE_RESPONSE, JobInvokeResponse.class);
    }

    public abstract int getMessageType();

    public static Class<? extends AbstractMessage> getMessageClass(Integer messageType) {
        return ALL_MESSAGE.get(messageType);
    }
}