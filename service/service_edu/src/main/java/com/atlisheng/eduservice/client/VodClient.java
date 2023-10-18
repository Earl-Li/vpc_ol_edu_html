package com.atlisheng.eduservice.client;

import com.atlisheng.commonutils.response.ResponseData;
import com.atlisheng.eduservice.client.impl.VodClientDegrade;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

/**
 * @author Earl
 * @version 1.0.0
 * @描述 远程调用vod服务中的方法
 * @创建日期 2023/09/29
 * @since 1.0.0
 */
@FeignClient(name="service-vod",fallback = VodClientDegrade.class)
@Component
public interface VodClient {
    /**
     * @param videoId
     * @描述 edu通过Feign远程调用vod服务中的删除小节视频方法
     * @author Earl
     * @version 1.0.0
     * @创建日期 2023/09/29
     * @since 1.0.0
     */
    @DeleteMapping("/eduvod/filevod/removeVodVideo/{videoId}")
    public ResponseData removeVodVideo(@PathVariable("videoId") String videoId);

    /**
     * @param videoIds
     * @描述 根据多个视频id批量删除视频
     * @author Earl
     * @version 1.0.0
     * @创建日期 2023/09/30
     * @since 1.0.0
     */
    @DeleteMapping("/eduvod/filevod/removeVodVideoByIds")
    public ResponseData removeVodVideoByIds(@RequestParam("videoIdList") List<String> videoIdList);
}
