package top.mnsx.job.enums;

import lombok.Getter;

public enum EnabledEnum {

    YES(1, "启用"),
    NO(0, "停用");

    @Getter
    private Integer code;

    @Getter
    private String description;

    private EnabledEnum(Integer code, String description) {
        this.code = code;
        this.description = description;
    }

    public static String getDescription(Integer code) {
        EnabledEnum[] values = EnabledEnum.values();
        for (EnabledEnum value : values) {
            if (value.getCode().equals(code)) {
                return value.getDescription();
            }
        }
        return null;
    }
}
