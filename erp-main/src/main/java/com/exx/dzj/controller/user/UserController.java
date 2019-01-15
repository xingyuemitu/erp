package com.exx.dzj.controller.user;

import com.exx.dzj.facade.user.UserFacade;
import com.exx.dzj.result.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * @Author
 * @Date 2019/1/8 0008 17:18
 * @Description 获取用户信息
 */
@RestController
@RequestMapping("user/")
public class UserController {

    @Autowired
    private UserFacade userFacade;

    /**
     * 当用户登录成功后,进入主页获取用户信息
     * @return
     */
    @GetMapping("getUserInfo")
    public Result getUserInfo(HttpServletRequest request, HttpServletResponse response, String token){
        Result result = Result.responseSuccess();
        result.setData(userFacade.getUserInfo(token));
        return result;
    }
}
