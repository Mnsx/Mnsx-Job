package top.mnsx.job.message;

import lombok.Data;

import java.io.Serializable;

@Data
public class AppToAdminRequest extends AbstractMessage implements Serializable {

    // 应用名称
    private String appName;
    // 应用简介
    private String appDesc;
    // 应用地址
    private String address;

    @Override
    public int getMessageType() {
        return APP_TO_ADMIN_REQUEST;
    }
}

