package top.mnsx.job.service;

import com.baomidou.mybatisplus.extension.service.IService;
import top.mnsx.job.domain.po.JobLog;
import top.mnsx.job.utils.Result;

/**
* @author Mnsx_x
* @description 针对表【job_log】的数据库操作Service
* @createDate 2023-07-24 16:13:57
*/
public interface JobLogService extends IService<JobLog> {

    Result<?> queryByJobId(Integer jobId);
}
