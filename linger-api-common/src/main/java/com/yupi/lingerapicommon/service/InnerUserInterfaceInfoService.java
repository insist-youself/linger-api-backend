package com.yupi.lingerapicommon.service;

import com.yupi.lingerapicommon.model.entity.UserInterfaceInfo;

/**
* @author 86136
* @description 针对表【user_interface_info(用户调用接口关系)】的数据库操作Service
* @createDate 2024-02-16 15:54:26
*/
public interface InnerUserInterfaceInfoService {
    /**
     * 调用接口统计
     *
     * @param interfaceInfoId 接口ID
     * @param userId          用户ID
     * @return boolean 是否执行成功
     */
    boolean invokeCount(long interfaceInfoId, long userId);

    /**
     * 是否还有调用次数
     *
     * @param interfaceId 接口id
     * @param userId      用户id
     * @return UserInterfaceInfo 用户接口信息
     */
    UserInterfaceInfo hasLeftNum(Long interfaceId, Long userId);


    /**
     * 添加默认的用户接口信息
     *
     * @param interfaceId 接口id
     * @param userId      用户id
     * @return Boolean 是否添加成功
     */
    Boolean addDefaultUserInterfaceInfo(Long interfaceId, Long userId);

    /**
     * 检查用户是否有接口
     *
     * @param interfaceId 接口id
     * @param userId     用户id
     * @return UserInterfaceInfo 用户接口信息
     */
    UserInterfaceInfo checkUserHasInterface(long interfaceId, long userId);
}
