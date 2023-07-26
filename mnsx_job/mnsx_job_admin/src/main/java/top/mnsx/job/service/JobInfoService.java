package top.mnsx.job.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import top.mnsx.job.domain.po.JobInfo;
import top.mnsx.job.domain.vo.JobInfoPageVo;
import top.mnsx.job.utils.Result;

/**
* @author Mnsx_x
* @description 针对表【job_info】的数据库操作Service
* @createDate 2023-07-24 16:13:54
*/
public interface JobInfoService extends IService<JobInfo> {
    public Result register(JobInfo jobInfo);

    Result<JobInfo> insert(JobInfo jobInfo);

    Result<Page<JobInfoPageVo>> queryByPage(Integer pageNum, Integer pageSize);

    Result<JobInfo> update(JobInfo jobInfo);

    Result<Boolean> delete(Integer id);

    Result<Boolean> enable(Integer id);

    Result<Boolean> disable(Integer id);

    Result<Boolean> run(Integer id);
}
