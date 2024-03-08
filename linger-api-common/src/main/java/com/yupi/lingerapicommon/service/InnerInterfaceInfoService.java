package com.yupi.lingerapicommon.service;


import com.yupi.lingerapicommon.model.entity.InterfaceInfo;

/**
* @author 86136
* @description 针对表【interface_info(接口信息)】的数据库操作Service
* @createDate 2024-02-09 14:49:25
*/
public interface InnerInterfaceInfoService{

    /**
     * 从数据库中查询接口是否存在 （请求路径、请求方法、请求参数）
     * @param path
     * @param method
     * @return
     */
    InterfaceInfo getInterfaceInfo(String path, String method);
}
