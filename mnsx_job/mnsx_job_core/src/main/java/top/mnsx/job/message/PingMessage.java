package top.mnsx.job.message;

import java.io.Serializable;

public class PingMessage extends AbstractMessage implements Serializable {

    public PingMessage(int sequenceId) {
        setSequenceId(sequenceId);
    }

    @Override
    public int getMessageType() {
        return PING_MESSAGE_REQUEST;
    }
}
