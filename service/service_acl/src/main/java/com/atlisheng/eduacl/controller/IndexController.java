package com.atlisheng.eduacl.controller;

import com.alibaba.fastjson.JSONObject;
import com.atlisheng.commonutils.response.ResponseData;
import com.atlisheng.eduacl.entity.Permission;
import com.atlisheng.eduacl.service.IndexService;
import com.atlisheng.eduacl.service.PermissionService;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/admin/acl/index")
//@CrossOrigin
public class IndexController {

    @Autowired
    private IndexService indexService;

    /**
     * 根据token获取用户信息
     */
    @GetMapping("info")
    public ResponseData info(){
        //获取当前登录用户用户名
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        Map<String, Object> userInfo = indexService.getUserInfo(username);
        return ResponseData.responseCall().data(userInfo);
    }

    /**
     * 获取菜单
     * @return
     */
    @GetMapping("menu")
    public ResponseData getMenu(){
        //获取当前登录用户用户名
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        List<JSONObject> permissionList = indexService.getMenu(username);
        return ResponseData.responseCall().data("permissionList", permissionList);
    }

    @PostMapping("logout")
    public ResponseData logout(){
        return ResponseData.responseCall();
    }

}
