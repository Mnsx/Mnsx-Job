package top.mnsx.job.message;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import top.mnsx.job.utils.IdUtil;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class JobInvokeResponse extends AbstractMessage {

    private String returnValue;

    private Exception exceptionValue;

    public JobInvokeResponse(String msg) {
        this.returnValue = msg;
        this.setMessageType(JOB_INVOKE_RESPONSE);
        this.setSequenceId(IdUtil.generateAtomicId());
    }

    public JobInvokeResponse(Exception exceptionValue) {
        this.exceptionValue = exceptionValue;
        this.setMessageType(JOB_INVOKE_RESPONSE);
        this.setSequenceId(IdUtil.generateAtomicId());
    }

    public static JobInvokeResponse error(String msg) {
        return new JobInvokeResponse(new RuntimeException(msg));
    }

    public static JobInvokeResponse error(Exception e) {
        return new JobInvokeResponse(e);
    }

    public static JobInvokeResponse success(String msg) {
        return new JobInvokeResponse(msg);
    }

    @Override
    public int getMessageType() {
        return 0;
    }
}
