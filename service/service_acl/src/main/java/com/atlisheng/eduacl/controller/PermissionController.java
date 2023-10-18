package com.atlisheng.eduacl.controller;


import com.atlisheng.eduacl.entity.Permission;
import com.atlisheng.eduacl.service.PermissionService;
import com.atlisheng.commonutils.response.ResponseData;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * @author Earl
 * @version 1.0.0
 * @描述 <p>
 * 权限 菜单管理,swagger把这个admin路径禁用调用了，使用swagger无法访问权限控制下的接口，必须修改路径不含admin或者把swagger的admin排除先注释掉
 * </p>
 * @创建日期 2023/10/16
 * @since 1.0.0
 */
@RestController
@RequestMapping("/admin/acl/permission")
//@CrossOrigin
public class PermissionController {

    @Autowired
    private PermissionService permissionService;

    /**
     * @return {@link ResponseData }
     * @描述 查询出所有的菜单，封装成Permission的List集合返回，woc这个Permission对应数据库的children字段竟然还是一个
     * List集合？而且还是Permission的List集合，这和数据库插入值不会冲突吗？查表知没有children这个字段，这个children属性
     * 单纯就是给前端用的
     * 这种形式可以封装成无限级树形结构，使用递归来实现查询所有菜单的功能：一级菜单下面查二级菜单、二级菜单下面查三级菜单，
     * 直到查不到下级菜单为止
     * @author Earl
     * @version 1.0.0
     * @创建日期 2023/10/16
     * @since 1.0.0
     *///获取全部菜单
    @ApiOperation(value = "查询所有菜单")
    @GetMapping("queryAllMenu")
    public ResponseData indexAllPermission() {
        List<Permission> list =  permissionService.queryAllMenu();
        return ResponseData.responseCall().data("children",list);
    }

    /**
     * @param id
     * @return {@link ResponseData }
     * @描述 根据菜单id递归删除菜单和子菜单
     * @author Earl
     * @version 1.0.0
     * @创建日期 2023/10/16
     * @since 1.0.0
     */
    @ApiOperation(value = "递归删除菜单")
    @DeleteMapping("remove/{id}")
    public ResponseData remove(@PathVariable String id) {
        permissionService.removeChildById(id);
        return ResponseData.responseCall();
    }

    /**
     * @param roleId 角色id
     * @param permissionId 菜单id，角色单次选择只能一个角色，菜单可以选择多个，swagger的数组形式的数据通过每个新元素起新行传递
     * @return {@link ResponseData }
     * @描述
     * @author Earl
     * @version 1.0.0
     * @创建日期 2023/10/16
     * @since 1.0.0
     */
    @ApiOperation(value = "给角色分配权限")
    @PostMapping("/doAssign")
    public ResponseData doAssign(String roleId,String[] permissionId) {
        permissionService.saveRolePermissionRelationShip(roleId,permissionId);
        return ResponseData.responseCall();
    }

    /**
     * @param roleId
     * @return {@link ResponseData }
     * @描述 根据角色id查询出对应的菜单id
     * @author Earl
     * @version 1.0.0
     * @创建日期 2023/10/16
     * @since 1.0.0
     */
    @ApiOperation(value = "根据角色获取菜单")
    @GetMapping("toAssign/{roleId}")
    public ResponseData toAssign(@PathVariable String roleId) {
        List<Permission> list = permissionService.selectAllMenu(roleId);
        return ResponseData.responseCall().data("children", list);
    }


    /**
     * @param permission
     * @return {@link ResponseData }
     * @描述
     * @author Earl
     * @version 1.0.0
     * @创建日期 2023/10/16
     * @since 1.0.0
     */
    @ApiOperation(value = "新增菜单")
    @PostMapping("save")
    public ResponseData save(@RequestBody Permission permission) {
        permissionService.save(permission);
        return ResponseData.responseCall();
    }

    /**
     * @param permission
     * @return {@link ResponseData }
     * @描述
     * @author Earl
     * @version 1.0.0
     * @创建日期 2023/10/16
     * @since 1.0.0
     */
    @ApiOperation(value = "修改菜单")
    @PutMapping("update")
    public ResponseData updateById(@RequestBody Permission permission) {
        permissionService.updateById(permission);
        return ResponseData.responseCall();
    }

}

