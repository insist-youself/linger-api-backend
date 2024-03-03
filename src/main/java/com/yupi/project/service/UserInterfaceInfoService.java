package com.yupi.project.service;


import com.baomidou.mybatisplus.extension.service.IService;
import com.yupi.lingerapicommon.model.entity.UserInterfaceInfo;

/**
* @author 86136
* @description 针对表【user_interface_info(用户调用接口关系)】的数据库操作Service
* @createDate 2024-02-16 15:54:26
*/
public interface UserInterfaceInfoService extends IService<UserInterfaceInfo> {
    void validUserInterfaceInfo(UserInterfaceInfo userInterfaceInfo, boolean add);

    /**
     * 调用接口统计
     * @param interfaceInfoId
     * @param userId
     * @return
     */
    boolean invokeCount(long interfaceInfoId, long userId);


    Integer getLeftNum(long interfaceInfoId, long userId);
}
