package com.ly.common.enums;

import com.ly.common.base.IBaseEnum;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum StatusEnum implements IBaseEnum<Integer> {

    ENABLE(1,"启用"),
    DISABLE(0,"禁用");

    private final Integer value;

    private final String label;


}
