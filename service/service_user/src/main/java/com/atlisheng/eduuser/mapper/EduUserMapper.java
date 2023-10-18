package com.atlisheng.eduuser.mapper;

import com.atlisheng.eduuser.entity.EduUser;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;

/**
 * <p>
 * 用户表 Mapper 接口
 * </p>
 *
 * @author Earl
 * @since 2023-10-05
 */
public interface EduUserMapper extends BaseMapper<EduUser> {

    public Integer queryRegisterCountDaily(String day);

}
