package top.mnsx.job.domain.vo;

import lombok.Data;

@Data
public class JobInfoPageVo {

    private Integer id;
    private String jobAppName;
    private String jobName;
    private String jobDesc;
    private String creator;
    private String createTime;
    private String runCron;
    private Integer runStrategy;
    private String runParam;
    private String triggerNextTime;
    private Integer enabled;
}
