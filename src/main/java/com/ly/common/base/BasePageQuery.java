package com.ly.common.base;

import lombok.Data;

@Data
public class BasePageQuery {

    private int pageNum = 1;

    private int pageSize = 10;
}
