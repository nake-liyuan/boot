package com.ly.controller;

import com.ly.common.result.Result;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("tset")
public class TestController {


    @GetMapping("text")
    public Result<String> test(String text){
        return Result.success(text);
    }
}
