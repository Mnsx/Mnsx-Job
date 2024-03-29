package top.mnsx.job.enums;

import lombok.Getter;

public enum IsDeletedEnum {

    YES(1, "已删除"),
    NO(0, "未删除");

    @Getter
    private Integer code;

    @Getter
    private String description;

    private IsDeletedEnum(Integer code, String description) {
        this.code = code;
        this.description = description;
    }

    public static String getDescription(Integer code) {
        IsDeletedEnum[] values = IsDeletedEnum.values();
        for (IsDeletedEnum value : values) {
            if (value.getCode().equals(code)) {
                return value.getDescription();
            }
        }
        return null;
    }
}
