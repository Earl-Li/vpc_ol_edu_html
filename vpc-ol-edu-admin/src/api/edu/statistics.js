import request from '@/utils/request'
export default {
    createStatistics(day) {
        return request({
            url: `/edustatistics/statistics/generateDataByDate/${day}`,
            method: 'post'
        })
    },
    dataDisplayByChart(queryInfo){
        return request({
            url: `/edustatistics/statistics/dataDisplayByChart/${queryInfo.begin}/${queryInfo.end}/${queryInfo.type}`,
            method: 'get'
        })
    }
}