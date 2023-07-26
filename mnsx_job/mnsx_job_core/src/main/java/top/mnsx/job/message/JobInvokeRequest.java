package top.mnsx.job.message;

import lombok.Data;
import top.mnsx.job.utils.IdUtil;

@Data
public class JobInvokeRequest extends AbstractMessage {

    private String jobHandler;
    private String params;

    public JobInvokeRequest(String jobHandler, String params) {
        this.jobHandler = jobHandler;
        this.params = params;
        this.setMessageType(JOB_INVOKE_REQUEST);
        this.setSequenceId(IdUtil.generateAtomicId());
    }

    @Override
    public int getMessageType() {
        return JOB_INVOKE_REQUEST;
    }
}
