package com.ly.common.enums;

import com.ly.common.base.IBaseEnum;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum GenderEnum implements IBaseEnum<Integer> {

    MALE(1, "男"),
    FEMALE (2, "女");

    private final Integer value;

    private final String label;

}
