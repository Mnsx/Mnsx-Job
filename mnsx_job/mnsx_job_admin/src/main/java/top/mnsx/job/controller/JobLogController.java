package top.mnsx.job.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import top.mnsx.job.service.JobLogService;
import top.mnsx.job.utils.Result;

/**
 * @Author Mnsx_x xx1527030652@gmail.com
 */
@RestController
@RequestMapping("/log")
public class JobLogController {
    @Autowired
    private JobLogService jobLogService;

    @GetMapping("/query/{jobId}")
    public Result<?> queryById(@PathVariable("jobId") Integer jobId) {
        return jobLogService.queryByJobId(jobId);
    }
}
