package com.proxy.forge.config;

import com.proxy.forge.vo.ResponseApi;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * <p>ProjectName: wechat</p>
 * <p>PackageName: com.wechat.tools</p>
 * <p>Description: 实体类参数校验异常统一处理</p>
 * <p>Copyright: Copyright (c) 2023 by Ts</p>
 * <p>Contacts: Ts vx: Q_Q-1992</p>
 *
 * @Author: 85773
 * @Version: 1.0
 * @Date: 2024-05-05 08:03
 **/
@ControllerAdvice
public class ValidParamExceptionHandler {
    @ExceptionHandler(value = MethodArgumentNotValidException.class)
    @ResponseBody
    public ResponseApi handleMethodArgumentNoValidException(MethodArgumentNotValidException ex) {
        BindingResult bindingResult = ex.getBindingResult();
        StringBuilder errorMessage = new StringBuilder(bindingResult.getFieldErrors().size() * 16);
        errorMessage.append("invalidRequest: ");
        for (ObjectError error : bindingResult.getAllErrors()) {
            FieldError fieldError = (FieldError) error;
            errorMessage.append("[" + fieldError.getField());
            errorMessage.append(":");
            errorMessage.append(fieldError.getDefaultMessage());
            errorMessage.append("]");
            errorMessage.append(",");
        }
        String result = errorMessage.substring(0, errorMessage.length() - 1);
        return new ResponseApi(201,result,null);
    }
}
