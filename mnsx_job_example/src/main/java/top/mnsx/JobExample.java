package top.mnsx;

import org.springframework.stereotype.Component;
import top.mnsx.handler.IJobHandler;
import top.mnsx.message.JobInvokeRequest;
import top.mnsx.message.JobResponse;

/**
 * @Author Mnsx_x xx1527030652@gmail.com
 */
@Component
public class JobExample implements IJobHandler {
    @Override
    public JobResponse execute(String s) throws Exception {
        return JobResponse.success("接收参数=" + s);
    }
}
