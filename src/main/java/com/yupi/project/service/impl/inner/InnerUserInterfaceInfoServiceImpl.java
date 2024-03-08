package com.yupi.project.service.impl.inner;

import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.yupi.lingerapicommon.common.ErrorCode;
import com.yupi.lingerapicommon.model.entity.UserInterfaceInfo;
import com.yupi.lingerapicommon.service.InnerUserInterfaceInfoService;
import com.yupi.project.exception.BusinessException;
import com.yupi.project.exception.ThrowUtils;
import com.yupi.project.service.UserInterfaceInfoService;
import org.apache.dubbo.config.annotation.DubboService;

import javax.annotation.Resource;

/**
 * @author linger
 * @date 2024/2/21 21:56
 */
@DubboService
public class InnerUserInterfaceInfoServiceImpl implements InnerUserInterfaceInfoService {

    @Resource
    private UserInterfaceInfoService userInterfaceInfoService;

    @Override
    public boolean invokeCount(long interfaceInfoId, long userId) {
        if (interfaceInfoId <= 0 || userId <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        UserInterfaceInfo userInterfaceInfo = userInterfaceInfoService.lambdaQuery()
                .eq(UserInterfaceInfo::getInterfaceInfoId, interfaceInfoId)
                .eq(UserInterfaceInfo::getUserId, userId)
                .one();
        ThrowUtils.throwIf(userInterfaceInfo == null, ErrorCode.SYSTEM_ERROR, "接口不存在");
//        // 使用 UpdateWrapper 对象来构建更新条件
//        UpdateWrapper<UserInterfaceInfo> updateWrapper = new UpdateWrapper<>();
//        updateWrapper.eq("interfaceInfoId", interfaceInfoId);
//        updateWrapper.eq("userId", userId);
//        //修复leftNum < 0还会继续递减的bug，使用乐观锁解决数据多线程情况下的剩余调用接口数量异常问题
//        updateWrapper.gt("leftNum", 0);
//        updateWrapper.setSql("leftNum = leftNum - 1 , totalNum = totalNum + 1");
//        return userInterfaceInfoService.update(updateWrapper);
        // 修改调用次数
        return userInterfaceInfoService.lambdaUpdate()
                .eq(UserInterfaceInfo::getInterfaceInfoId, interfaceInfoId)
                .eq(UserInterfaceInfo::getUserId, userId)
                .set(UserInterfaceInfo::getTotalNum, userInterfaceInfo.getTotalNum() + 1)
                .set(UserInterfaceInfo::getLeftNum, userInterfaceInfo.getLeftNum() - 1)
                .update();
    }

    public UserInterfaceInfo hasLeftNum(Long interfaceId, Long userId) {
        return userInterfaceInfoService.lambdaQuery()
                .eq(UserInterfaceInfo::getInterfaceInfoId, interfaceId)
                .eq(UserInterfaceInfo::getUserId, userId)
                .one();
    }

    @Override
    public Boolean addDefaultUserInterfaceInfo(Long interfaceId, Long userId) {
        UserInterfaceInfo userInterfaceInfo = new UserInterfaceInfo();
        userInterfaceInfo.setUserId(userId);
        userInterfaceInfo.setInterfaceInfoId(interfaceId);
        userInterfaceInfo.setLeftNum(9999);
        return userInterfaceInfoService.save(userInterfaceInfo);
    }

    @Override
    public UserInterfaceInfo checkUserHasInterface(long interfaceId, long userId) {
        ThrowUtils.throwIf(interfaceId <= 0 || userId <= 0, ErrorCode.PARAMS_ERROR);
        return userInterfaceInfoService.lambdaQuery()
                .eq(UserInterfaceInfo::getInterfaceInfoId, interfaceId)
                .eq(UserInterfaceInfo::getUserId, userId)
                .one();
    }
}
