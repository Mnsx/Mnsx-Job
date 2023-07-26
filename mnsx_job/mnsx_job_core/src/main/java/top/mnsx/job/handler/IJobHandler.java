package top.mnsx.job.handler;

import top.mnsx.job.message.JobInvokeResponse;
import top.mnsx.job.message.JobResponse;

/**
 * Handler规范
 */
public interface IJobHandler {
    public JobInvokeResponse execute(String params) throws Exception;
}