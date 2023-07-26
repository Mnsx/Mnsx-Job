package top.mnsx.job.enums;

import lombok.Getter;

public enum CreateWayEnum {

    AUTO(0, "自动"),
    MANUAL(1, "手动");

    @Getter
    private Integer code;
    @Getter
    private String description;

    private CreateWayEnum(Integer code, String description) {
        this.code = code;
        this.description = description;
    }

    public static String getDescription(Integer code) {
        CreateWayEnum[] values = CreateWayEnum.values();
        for (CreateWayEnum value : values) {
            if (value.getCode().equals(code)) {
                return value.getDescription();
            }
        }
        return null;
    }
}
