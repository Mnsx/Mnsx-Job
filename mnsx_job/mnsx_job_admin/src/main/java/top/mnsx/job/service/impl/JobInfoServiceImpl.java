package top.mnsx.job.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.quartz.*;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import top.mnsx.job.domain.po.JobApp;
import top.mnsx.job.domain.po.JobInfo;
import top.mnsx.job.domain.vo.JobInfoPageVo;
import top.mnsx.job.enums.EnabledEnum;
import top.mnsx.job.enums.IsDeletedEnum;
import top.mnsx.job.job.JobInvoker;
import top.mnsx.job.job.QuartZJob;
import top.mnsx.job.mapper.JobInfoMapper;
import top.mnsx.job.message.JobResponse;
import top.mnsx.job.service.JobAppService;
import top.mnsx.job.service.JobInfoService;
import top.mnsx.job.utils.Result;
import top.mnsx.job.utils.ThrowableUtil;

import javax.annotation.Resource;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Objects;
import java.util.Random;
import java.util.stream.Collectors;

/**
* @author Mnsx_x
* @description 针对表【job_info】的数据库操作Service实现
* @createDate 2023-07-24 16:13:54
*/
@Service
public class JobInfoServiceImpl extends ServiceImpl<JobInfoMapper, JobInfo>
    implements JobInfoService {

    @Autowired
    private JobAppService jobAppService;
    @Resource
    private JobInfoMapper jobInfoMapper;
    @Autowired
    private Scheduler scheduler;
    @Autowired
    private JobInvoker jobInvoker;

    @Override
    public Result register(JobInfo jobInfo) {
        JobApp jobApp = jobAppService.getById(jobInfo.getAppId());
        if (Objects.isNull(jobApp)) {
            return Result.err("应用不存在");
        }

        // 创建jobDetail实例
        JobDetail jobDetail = JobBuilder.newJob(QuartZJob.class)
                .withIdentity(jobInfo.getJobName(), jobApp.getAppName())
                .withDescription(jobInfo.getJobDesc())
                .build();

        // 定义调度触发规则corn
        Trigger trigger = TriggerBuilder.newTrigger()
                .withIdentity(jobInfo.getJobName(), jobApp.getAppName())
                .startAt(DateBuilder.futureDate(1, DateBuilder.IntervalUnit.SECOND))
                .withSchedule(CronScheduleBuilder.cronSchedule(jobInfo.getRunCron()))
                .startNow()
                .build();

        // 传递一些数据到任务里面
        JobDataMap jobDataMap = jobDetail.getJobDataMap();
        jobDataMap.put("JobApp", jobApp);
        jobDataMap.put("JobInfo", jobInfo);

        // 把作业和触发器注册到任务调度中
        try {
            scheduler.scheduleJob(jobDetail, trigger);
        } catch (SchedulerException e) {
            return Result.err("注册任务异常！");
        }

        // 启动
        try {
            if (!scheduler.isShutdown()) {
                scheduler.start();
            }
        } catch (SchedulerException e) {
            return Result.err("启动scheduler异常！");
        }

        return Result.ok();
    }

    @Override
    @Transactional
    public Result<JobInfo> insert(JobInfo jobInfo) {
        if (Objects.nonNull(jobInfo.getId())) {
            jobInfo.setId(null);
        }

        // 相同应用下名称不能重复
        LambdaQueryWrapper<JobInfo> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(jobInfo.getAppId() != null, JobInfo::getAppId, jobInfo.getAppId());
        wrapper.eq(jobInfo.getJobName() != null, JobInfo::getJobName, jobInfo.getJobName());
        wrapper.eq(jobInfo.getIsDeleted() != null, JobInfo::getIsDeleted, IsDeletedEnum.NO.getCode());
        List<JobInfo> jobInfos = jobInfoMapper.selectList(wrapper);

        if (!jobInfos.isEmpty()) {
            return Result.err("该应用已存在相同名称的任务！");
        }

        boolean flag = save(jobInfo);

        // 注册
        if (Objects.equals(jobInfo.getEnabled(), EnabledEnum.YES.getCode())) {
            Result registerResult = register(jobInfo);
            return registerResult.isOk() ? Result.ok() : registerResult;
        }

        return Result.ok();
    }

    @Override
    public Result<Page<JobInfoPageVo>> queryByPage(Integer pageNum, Integer pageSize) {
        // 分页查询数据
        LambdaQueryWrapper<JobInfo> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(JobInfo::getIsDeleted, IsDeletedEnum.NO.getCode());
        wrapper.orderByAsc(JobInfo::getAppId);
        Page<JobInfo> page = new Page<>(pageNum, pageSize);
        Page<JobInfo> result = page(page, wrapper);
        // 数据处理
        List<JobInfoPageVo> record = result.getRecords().stream().map(r -> {
            JobInfoPageVo vo = new JobInfoPageVo();
            BeanUtils.copyProperties(r, vo);
            JobApp jobApp = jobAppService.getById(r.getAppId());
            if (jobApp != null) {
                vo.setJobAppName(jobApp.getAppName());
            }
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            vo.setCreateTime(sdf.format(r.getCreateTime()));
            String triggerNextTime = r.getTriggerNextTime() != null ? sdf.format(r.getTriggerNextTime()) : "";
            vo.setTriggerNextTime(triggerNextTime);
            return vo;
        }).collect(Collectors.toList());
        Page<JobInfoPageVo> ans = new Page<>();
        BeanUtils.copyProperties(result, ans);
        ans.setRecords(record);
        return Result.ok(ans);
    }

    @Override
    public Result<JobInfo> update(JobInfo jobInfo) {
        JobInfo byId = getById(jobInfo.getId());
        if (byId == null) {
            return Result.err("任务不存在");
        }
        JobApp jobApp = jobAppService.getById(byId.getAppId());

        JobKey jobKey = JobKey.jobKey(byId.getJobName(), jobApp.getAppName());

        try {
            boolean flag = scheduler.deleteJob(jobKey);
            if (!flag) {
                return Result.err("停用定时任务失败");
            }
        } catch (SchedulerException e) {
            log.error("[Mnsx-Job] 停用定时任务产生异常={}", e);
            return Result.err("停用任务产生异常");
        }

        updateById(jobInfo);

        byId = getById(jobInfo.getId());
        Result registerResult = register(byId);
        if (registerResult.isErr()) {
            return registerResult;
        }

        return Result.ok(jobInfo);
    }

    @Override
    public Result<Boolean> delete(Integer id) {
        JobInfo byId = getById(id);

        if (byId != null) {
            removeById(id);
        }

        return Result.ok(true);
    }

    @Override
    public Result<Boolean> enable(Integer id) {
        JobInfo byId = getById(id);
        if (byId == null) {
            return Result.err("任务不存在");
        }

        if (Objects.equals(byId.getEnabled(), EnabledEnum.YES.getCode())) {
            return Result.err("任务已经处于启动状态");
        }

        Result registerResult = register(byId);
        if (registerResult.isErr()) {
            return registerResult;
        }

        byId.setEnabled(EnabledEnum.YES.getCode());
        updateById(byId);

        return Result.ok("启动定时任务成功");    }

    @Override
    public Result<Boolean> disable(Integer id) {
        JobInfo byId = getById(id);
        if (byId == null) {
            return Result.err("任务不存在");
        }

        if (Objects.equals(byId.getEnabled(), EnabledEnum.NO.getCode())) {
            return Result.err("任务已经处于停用状态");
        }

        JobApp jobApp = jobAppService.getById(byId.getAppId());

        JobKey jobKey = JobKey.jobKey(byId.getJobName(), jobApp.getAppName());

        try {
            boolean flag = scheduler.deleteJob(jobKey);
            if (!flag) {
                return Result.err("停用定时任务失败");
            }
        } catch (SchedulerException e) {
            log.error("[Mnsx-Job] 停用定时任务产生异常={}", e);
            return Result.err("停用任务产生异常");
        }

        byId.setEnabled(EnabledEnum.NO.getCode());
        updateById(byId);

        return Result.ok(true);
    }

    @Override
    public Result<Boolean> run(Integer id) {
        JobInfo byId = getById(id);
        if (byId == null) {
            return Result.err("任务不存在");
        }

        JobApp jobApp = jobAppService.getById(byId.getAppId());

        String addressList = jobApp.getAddressList();
        if (!StringUtils.hasText(addressList)) {
            return Result.err("任务对应的应用没有地址信息");
        }

        String[] split = addressList.split(",");
        Random random = new Random();
        int index = random.nextInt(split.length);
        String invokeAddress = split[index];

        JobResponse result = null;
        try {
            result = jobInvoker.invoke(invokeAddress, byId.getJobName(), byId.getRunParam());
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }

        return result.isOk() ? Result.ok(result.getMsg()) : Result.err(result.getMsg());
    }
}




