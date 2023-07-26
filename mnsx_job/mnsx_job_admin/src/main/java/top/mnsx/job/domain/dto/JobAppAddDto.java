package top.mnsx.job.domain.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class JobAppAddDto {

    // 应用名称
    private String appName;

    // 应用介绍
    private String appDesc;

    // 地址列表
    private String addressList;

    // 是否禁用
    private Integer enabled;
}
