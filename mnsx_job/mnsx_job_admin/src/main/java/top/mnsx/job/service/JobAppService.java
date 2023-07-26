package top.mnsx.job.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import top.mnsx.job.domain.po.JobApp;
import top.mnsx.job.utils.Result;

import java.util.List;
import java.util.Map;

/**
* @author Mnsx_x
* @description 针对表【job_app】的数据库操作Service
* @createDate 2023-07-24 16:13:38
*/
public interface JobAppService extends IService<JobApp> {
    Result<Page<JobApp>> queryByPage(Integer pageNum, Integer pageSize);

    Result<JobApp> insert(JobApp jobApp);

    Result<JobApp> update(JobApp jobApp);

    Result<List<Map<String, String>>> queryAllName();
}
