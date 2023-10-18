package com.atlisheng.eduacl.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.atlisheng.eduacl.entity.Permission;
import com.atlisheng.eduacl.entity.RolePermission;
import com.atlisheng.eduacl.entity.User;
import com.atlisheng.eduacl.helper.MemuHelper;
import com.atlisheng.eduacl.helper.PermissionHelper;
import com.atlisheng.eduacl.mapper.PermissionMapper;
import com.atlisheng.eduacl.service.PermissionService;
import com.atlisheng.eduacl.service.RolePermissionService;
import com.atlisheng.eduacl.service.UserService;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author Earl
 * @version 1.0.0
 * @描述 <p>
 * 权限 服务实现类
 * </p>
 * @创建日期 2023/10/17
 * @since 1.0.0
 */
@Service
public class PermissionServiceImpl extends ServiceImpl<PermissionMapper, Permission> implements PermissionService {

    @Autowired
    private RolePermissionService rolePermissionService;
    
    @Autowired
    private UserService userService;

    /**
     * @return {@link List }<{@link Permission }>
     * @描述 获取全部菜单，先查询菜单中的所有数据，注意这里面写了两套方法，第二套是课程演示，实际运行也是第二套，这里笔记做在第一套上面
     * @author Earl
     * @version 1.0.0
     * @创建日期 2023/10/16
     * @since 1.0.0
     */
    @Override
    public List<Permission> queryAllMenu() {

        //1.查出所有的菜单
        QueryWrapper<Permission> wrapper = new QueryWrapper<>();
        wrapper.orderByDesc("id");
        List<Permission> permissionList = baseMapper.selectList(wrapper);

        //2.将查出的菜单封装成list集合返回给前端
        List<Permission> result = bulid(permissionList);

        return result;
    }

    /**
     * @param roleId
     * @return {@link List }<{@link Permission }>
     * @描述 根据角色id查询出所有的菜单，将和角色有关系的菜单的isSelect属性设置为true
     * @author Earl
     * @version 1.0.0
     * @创建日期 2023/10/17
     * @since 1.0.0
     */
    @Override
    public List<Permission> selectAllMenu(String roleId) {
        List<Permission> allPermissionList = baseMapper.selectList(new QueryWrapper<Permission>().orderByAsc("CAST(id AS SIGNED)"));

        //根据角色id获取角色权限
        List<RolePermission> rolePermissionList = rolePermissionService.list(new QueryWrapper<RolePermission>().eq("role_id",roleId));

        for (int i = 0; i < allPermissionList.size(); i++) {
            //根据每个菜单对象对每个查询出的角色菜单遍历，如果菜单的id和角色菜单记录的菜单id相等，将菜单的select属性设置为true
            Permission permission = allPermissionList.get(i);
            for (int m = 0; m < rolePermissionList.size(); m++) {
                RolePermission rolePermission = rolePermissionList.get(m);
                if(rolePermission.getPermissionId().equals(permission.getId())) {
                    //lombok的isSelect属性，@Data生成的是setSelect方法，会去掉is
                    //这个属性是应该是用来给前端展示菜单用的？
                    permission.setSelect(true);
                }
            }
        }

        List<Permission> permissionList = bulid(allPermissionList);
        return permissionList;
    }

    /**
     * @param roleId
     * @param permissionIds
     * @描述 给角色分配权限
     * @author Earl
     * @version 1.0.0
     * @创建日期 2023/10/17
     * @since 1.0.0
     */
    @Override
    public void saveRolePermissionRelationShip(String roleId, String[] permissionIds) {

        rolePermissionService.remove(new QueryWrapper<RolePermission>().eq("role_id", roleId));
        //1.对应role_permission表，根据输入的角色id和菜单id集合，生成多条角色菜单关系表，加入list集合，最后批次添加记录
        List<RolePermission> rolePermissionList = new ArrayList<>();
        //遍历菜单id集合，因为角色和菜单关系表，需要填写的只有角色id和菜单id，所以根本用不着查表，直接设置对应的id即可
        for(String permissionId : permissionIds) {
            if(StringUtils.isEmpty(permissionId)) continue;
      
            RolePermission rolePermission = new RolePermission();
            rolePermission.setRoleId(roleId);
            rolePermission.setPermissionId(permissionId);
            rolePermissionList.add(rolePermission);
        }
        //批次添加记录
        rolePermissionService.saveBatch(rolePermissionList);
    }

    /**
     * @param id
     * @描述 递归删除菜单
     * @author Earl
     * @version 1.0.0
     * @创建日期 2023/10/17
     * @since 1.0.0
     */
    @Override
    public void removeChildById(String id) {

        //将id传入list集合，用于封装所有删除菜单的id值
        List<String> idList = new ArrayList<>();
        //根据id递归查询出当前菜单所有的子菜单id并存入List集合
        this.selectChildListById(id, idList);

        //将当前菜单的id加入list集合
        idList.add(id);
        //根据id批量删除集合中id对应的记录
        baseMapper.deleteBatchIds(idList);
    }

    /**
     * @param id
     * @return {@link List }<{@link String }>
     * @描述
     * @author Earl
     * @version 1.0.0
     * @创建日期 2023/10/17
     * @since 1.0.0
     *///
    @Override
    public List<String> selectPermissionValueByUserId(String id) {

        List<String> selectPermissionValueList = null;
        if(this.isSysAdmin(id)) {
            //如果是系统管理员，获取所有权限
            selectPermissionValueList = baseMapper.selectAllPermissionValue();
        } else {
            selectPermissionValueList = baseMapper.selectPermissionValueByUserId(id);
        }
        return selectPermissionValueList;
    }

    @Override
    public List<JSONObject> selectPermissionByUserId(String userId) {
        List<Permission> selectPermissionList = null;
        if(this.isSysAdmin(userId)) {
            //如果是超级管理员，获取所有菜单
            selectPermissionList = baseMapper.selectList(null);
        } else {
            selectPermissionList = baseMapper.selectPermissionByUserId(userId);
        }

        List<Permission> permissionList = PermissionHelper.bulid(selectPermissionList);
        List<JSONObject> result = MemuHelper.bulid(permissionList);
        return result;
    }

    /**
     * 判断用户是否系统管理员
     * @param userId
     * @return
     */
    private boolean isSysAdmin(String userId) {
        User user = userService.getById(userId);

        if(null != user && "admin".equals(user.getUsername())) {
            return true;
        }
        return false;
    }

    /**
     *	根据当前菜单的id，递归获取子菜单的id，将子菜单id封装到list集合中，相当于将所有pid的记录查出来，然后遍历所有的子菜单记录，将
     *其id放入list集合，并继续对这些记录查询子菜单记录，将id放入同一个list集合，并遍历每个菜单下的子菜单，直到获取不到子菜单向上返回，有点
     * 像深度优先遍历和宽度优先遍历的结合版
     * @param id
     * @param idList
     */
    private void selectChildListById(String id, List<String> idList) {
        List<Permission> childList = baseMapper.selectList(new QueryWrapper<Permission>().eq("pid", id).select("id"));
        childList.stream().forEach(item -> {
            idList.add(item.getId());
            this.selectChildListById(item.getId(), idList);
        });
    }

    /**
     * @param treeNodes
     * @return {@link List }<{@link Permission }>
     * @描述 使用递归方法封装菜单数据成树形结构，先从一级查询
     * @author Earl
     * @version 1.0.0
     * @创建日期 2023/10/16
     * @since 1.0.0
     */
    private static List<Permission> bulid(List<Permission> treeNodes) {
        List<Permission> trees = new ArrayList<>();
        //对每条菜单记录进行遍历，当遇到一级目录时，设置对应一级菜单的层级为1，查出对应一级菜单的全部子菜单封装成list集合存入一级菜单的children
        // 属性并将一级菜单加入封装数据的list集合
        for (Permission treeNode : treeNodes) {
            if ("0".equals(treeNode.getPid())) {
                treeNode.setLevel(1);
                //将子菜单的所有数据全部查出来放入一级菜单的children属性，将一级菜单加入list集合
                trees.add(findChildren(treeNode,treeNodes));
            }
        }
        return trees;
    }

    /**
     * @param treeNode 顶层一级菜单对象
     * @param treeNodes 所有菜单记录的list集合
     * @return {@link Permission }
     * @描述 递归查找一级菜单的所有子菜单，并封装成list集合传递给上级菜单的children属性
     * @author Earl
     * @version 1.0.0
     * @创建日期 2023/10/16
     * @since 1.0.0
     */
    private static Permission findChildren(Permission treeNode,List<Permission> treeNodes) {
        //由于是循环调用，这一步实际是在各层菜单的children属性创建一个新的list集合，准备接收子菜单数据
        treeNode.setChildren(new ArrayList<Permission>());

        for (Permission it : treeNodes) {
            //这个效率有点感人，每次遍历所有菜单，将pid和上层菜单的id进行比较，值相同则查出该菜单的下的所有子菜单，
            // 封装到其children属性，并将该菜单设置到上层菜单的children属性的list集合中，实际上是深度优先遍历
            if(treeNode.getId().equals(it.getPid())) {
                //定义菜单的层级
                int level = treeNode.getLevel() + 1;
                //设置当前菜单层级
                it.setLevel(level);
                //这特么会执行？逗我呢，上面不是执行了嘛，这里最后测试一下会不会进来,经过测试，压根就不会执行这行代码
                if (treeNode.getChildren() == null) {
                    treeNode.setChildren(new ArrayList<>());
                }
                //在这里面再次对所有菜单进行了遍历
                treeNode.getChildren().add(findChildren(it,treeNodes));
            }
        }
        return treeNode;
    }
}
