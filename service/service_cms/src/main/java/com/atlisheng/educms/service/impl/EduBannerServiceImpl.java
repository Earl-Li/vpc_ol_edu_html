package com.atlisheng.educms.service.impl;

import com.atlisheng.educms.entity.EduBanner;
import com.atlisheng.educms.mapper.EduBannerMapper;
import com.atlisheng.educms.service.EduBannerService;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * <p>
 * 首页banner表 服务实现类
 * </p>
 *
 * @author Earl
 * @since 2023-10-02
 */
@Service
public class EduBannerServiceImpl extends ServiceImpl<EduBannerMapper, EduBanner> implements EduBannerService {

    @Override
    @Cacheable(key="'bannerList'",value = "indexData")
    public List<EduBanner> queryBannerList() {
        List<EduBanner> bannerList = list(null);
        return bannerList;
    }

    @Override
    public void pageBanner(Page<EduBanner> pageParam, QueryWrapper queryWrapper) {
        page(pageParam, queryWrapper);
    }

    @Override
    public EduBanner getBannerById(String id) {
        EduBanner eduBanner = getById(id);
        return eduBanner;
    }

    @Override
    public boolean addBanner(EduBanner banner) {
        boolean status = save(banner);
        return status;
    }

    @Override
    public boolean updateBannerById(EduBanner banner) {
        boolean status = updateById(banner);
        return status;
    }

    @Override
    public boolean removeBannerById(String id) {
        boolean status = removeById(id);
        return status;
    }
}
