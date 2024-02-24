package com.yupi.project.service;
import com.baomidou.mybatisplus.extension.service.IService;
import com.yupi.lingerapicommon.model.entity.InterfaceInfo;

/**
* @author 86136
* @description 针对表【interface_info(接口信息)】的数据库操作Service
* @createDate 2024-02-09 14:49:25
*/
public interface InterfaceInfoService extends IService<InterfaceInfo> {

    void validInterfaceInfo(InterfaceInfo interfaceInfo, boolean add);
}
