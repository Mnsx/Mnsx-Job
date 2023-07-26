package top.mnsx.job.message;

import lombok.Getter;
import top.mnsx.job.utils.IdUtil;

import java.util.Objects;

public class JobResponse extends AbstractMessage {

    public static final Integer SUCCESS_CODE = 1;

    public static final Integer ERROR_CODE = 0;

    @Getter
    private Integer code;

    @Getter
    private String msg;

    public JobResponse() {
        this.code = SUCCESS_CODE;
        this.setMessageType(JOB_RESPONSE);
        this.setSequenceId(IdUtil.generateAtomicId());
    }

    public JobResponse(Integer code, String msg) {
        this.code = code;
        this.msg = msg;
        this.setMessageType(JOB_RESPONSE);
        this.setSequenceId(IdUtil.generateAtomicId());
    }

    public static JobResponse success(String msg) {
        return new JobResponse(SUCCESS_CODE, msg);
    }

    public static JobResponse error(String msg) {
        return new JobResponse(ERROR_CODE, msg);
    }

    public boolean isOk() {
        return Objects.equals(this.code, SUCCESS_CODE);
    }

    @Override
    public int getMessageType() {
        return JOB_RESPONSE;
    }
}
