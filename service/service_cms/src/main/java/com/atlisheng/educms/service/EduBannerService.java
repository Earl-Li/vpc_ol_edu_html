package com.atlisheng.educms.service;

import com.atlisheng.educms.entity.EduBanner;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import java.util.List;

/**
 * <p>
 * 首页banner表 服务类
 * </p>
 *
 * @author Earl
 * @since 2023-10-02
 */
public interface EduBannerService extends IService<EduBanner> {

    /**
     * @return {@link List }<{@link EduBanner }>
     * @描述 查询所有的banner
     * @author Earl
     * @version 1.0.0
     * @创建日期 2023/10/02
     * @since 1.0.0
     */
    List<EduBanner> queryBannerList();

    /**
     * @param pageParam
     * @param queryWrapper
     * @描述 带条件分页查询banner
     * @author Earl
     * @version 1.0.0
     * @创建日期 2023/10/02
     * @since 1.0.0
     */
    void pageBanner(Page<EduBanner> pageParam, QueryWrapper queryWrapper);

    /**
     * @param id
     * @return {@link EduBanner }
     * @描述 根据bannerId获取所有banner
     * @author Earl
     * @version 1.0.0
     * @创建日期 2023/10/02
     * @since 1.0.0
     */
    EduBanner getBannerById(String id);

    /**
     * @param banner
     * @描述 新增banner
     * @author Earl
     * @version 1.0.0
     * @创建日期 2023/10/02
     * @since 1.0.0
     */
    boolean addBanner(EduBanner banner);

    /**
     * @param banner
     * @描述 根据bannerId更新banner
     * @author Earl
     * @version 1.0.0
     * @创建日期 2023/10/02
     * @since 1.0.0
     */
    boolean updateBannerById(EduBanner banner);

    /**
     * @param id
     * @描述 根据bannerId删除banner
     * @author Earl
     * @version 1.0.0
     * @创建日期 2023/10/02
     * @since 1.0.0
     */
    boolean removeBannerById(String id);
}
