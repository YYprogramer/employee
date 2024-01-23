package com.yy5.employee.controller;

import com.yy5.employee.validation.CustomStringTrimmerEditor;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.InitBinder;

@ControllerAdvice
public class AgeControllerAdvice {
    @InitBinder
    public void  initBinder(WebDataBinder binder) {
        binder.registerCustomEditor(String.class, new CustomStringTrimmerEditor(true));
    }
}
