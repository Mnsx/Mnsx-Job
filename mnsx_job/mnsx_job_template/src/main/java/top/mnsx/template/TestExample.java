package top.mnsx.template;

import org.springframework.stereotype.Component;
import top.mnsx.job.handler.IJobHandler;
import top.mnsx.job.message.JobInvokeResponse;
import top.mnsx.job.message.JobResponse;

@Component
public class TestExample implements IJobHandler {


    public JobInvokeResponse execute(String params) throws Exception {
        return JobInvokeResponse.success("接收参数=" + params);
    }
}
