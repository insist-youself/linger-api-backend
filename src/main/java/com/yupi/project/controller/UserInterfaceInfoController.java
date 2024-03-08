package com.yupi.project.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.yupi.lingerapicommon.common.BaseResponse;
import com.yupi.lingerapicommon.common.DeleteRequest;
import com.yupi.lingerapicommon.common.ErrorCode;
import com.yupi.lingerapicommon.common.ResultUtils;
import com.yupi.lingerapicommon.constant.UserConstant;
import com.yupi.lingerapicommon.model.dto.userinterfaceinfo.UserInterfaceInfoAddRequest;
import com.yupi.lingerapicommon.model.dto.userinterfaceinfo.UserInterfaceInfoQueryRequest;
import com.yupi.lingerapicommon.model.dto.userinterfaceinfo.UserInterfaceInfoUpdateRequest;
import com.yupi.lingerapicommon.model.entity.User;
import com.yupi.lingerapicommon.model.entity.UserInterfaceInfo;
import com.yupi.project.annotation.AuthCheck;
import com.yupi.project.exception.BusinessException;
import com.yupi.project.exception.ThrowUtils;
import com.yupi.project.service.UserInterfaceInfoService;
import com.yupi.project.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.List;

/**
 * 接口管理
 *
 * @author yupi
 */
@RestController
@RequestMapping("/userinterfaceinfo")
@Slf4j
public class UserInterfaceInfoController {

    @Resource
    private UserInterfaceInfoService userinterfaceinfoService;

    @Resource
    private UserService userService;

    // region 增删改查

    /**
     * 创建
     *
     * @param userinterfaceinfoAddRequest
     * @param request
     * @return
     */
    @PostMapping("/add")
//    @AuthCheck(mustRole = UserConstant.ADMIN_ROLE)
    public BaseResponse<Long> addUserInterfaceInfo(@RequestBody UserInterfaceInfoAddRequest userinterfaceinfoAddRequest, HttpServletRequest request) {
        if (userinterfaceinfoAddRequest == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        UserInterfaceInfo userinterfaceinfo = new UserInterfaceInfo();
        BeanUtils.copyProperties(userinterfaceinfoAddRequest, userinterfaceinfo);
        userinterfaceinfo.setLeftNum(9999);
        // 校验
        User loginUser = userService.getLoginUser(request);
        userinterfaceinfo.setUserId(loginUser.getId());
        userinterfaceinfoService.validUserInterfaceInfo(userinterfaceinfo, true);
        boolean result = userinterfaceinfoService.save(userinterfaceinfo);
        if (!result) {
            throw new BusinessException(ErrorCode.OPERATION_ERROR);
        }
        long newUserInterfaceInfoId = userinterfaceinfo.getId();
        return ResultUtils.success(newUserInterfaceInfoId);
    }

    /**
     * 删除
     *
     * @param deleteRequest
     * @param request
     * @return
     */
    @PostMapping("/delete")
    @AuthCheck(mustRole = UserConstant.ADMIN_ROLE)
    public BaseResponse<Boolean> deleteUserInterfaceInfo(@RequestBody DeleteRequest deleteRequest, HttpServletRequest request) {
        if (deleteRequest == null || deleteRequest.getId() <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        User user = userService.getLoginUser(request);
        long id = deleteRequest.getId();
        // 判断是否存在
        UserInterfaceInfo oldUserInterfaceInfo = userinterfaceinfoService.getById(id);
        if (oldUserInterfaceInfo == null) {
            throw new BusinessException(ErrorCode.NOT_FOUND_ERROR);
        }
        // 仅本人或管理员可删除
        if (!oldUserInterfaceInfo.getUserId().equals(user.getId()) && !userService.isAdmin(request)) {
            throw new BusinessException(ErrorCode.NO_AUTH_ERROR);
        }
        boolean b = userinterfaceinfoService.removeById(id);
        return ResultUtils.success(b);
    }

    /**
     * 更新
     *
     * @param userinterfaceinfoUpdateRequest
     * @param request
     * @return
     */
    @PostMapping("/update")
    @AuthCheck(mustRole = UserConstant.ADMIN_ROLE)
    public BaseResponse<Boolean> updateUserInterfaceInfo(@RequestBody UserInterfaceInfoUpdateRequest userinterfaceinfoUpdateRequest,
                                            HttpServletRequest request) {
        if (userinterfaceinfoUpdateRequest == null || userinterfaceinfoUpdateRequest.getId() <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        UserInterfaceInfo userinterfaceinfo = new UserInterfaceInfo();
        BeanUtils.copyProperties(userinterfaceinfoUpdateRequest, userinterfaceinfo);
        // 参数校验
        userinterfaceinfoService.validUserInterfaceInfo(userinterfaceinfo, false);
        User user = userService.getLoginUser(request);
        long id = userinterfaceinfoUpdateRequest.getId();
        // 判断是否存在
        UserInterfaceInfo oldUserInterfaceInfo = userinterfaceinfoService.getById(id);
        if (oldUserInterfaceInfo == null) {
            throw new BusinessException(ErrorCode.NOT_FOUND_ERROR);
        }
        // 仅本人或管理员可修改
//        if (!oldUserInterfaceInfo.getUserId().equals(user.getId()) && !userService.isAdmin(request)) {
//            throw new BusinessException(ErrorCode.NO_AUTH_ERROR);
//        }
        boolean result = userinterfaceinfoService.updateById(userinterfaceinfo);
        return ResultUtils.success(result);
    }

    /**
     * 根据 id 获取
     *
     * @param id
     * @return
     */
    @GetMapping("/get/vo")
    @AuthCheck(mustRole = UserConstant.ADMIN_ROLE)
    public BaseResponse<UserInterfaceInfo> getUserInterfaceInfoById(long id) {
        if (id <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        UserInterfaceInfo userinterfaceinfo = userinterfaceinfoService.getById(id);
        ThrowUtils.throwIf(userinterfaceinfo == null, ErrorCode.NOT_FOUND_ERROR);
        return ResultUtils.success(userinterfaceinfo);
    }

    /**
     * 获取列表（仅管理员可使用）
     *
     * @param userinterfaceinfoQueryRequest
     * @return
     */
    @AuthCheck(mustRole = UserConstant.ADMIN_ROLE)
    @GetMapping("/list")
    public BaseResponse<List<UserInterfaceInfo>> listUserInterfaceInfo(UserInterfaceInfoQueryRequest userinterfaceinfoQueryRequest) {
        UserInterfaceInfo userinterfaceinfoQuery = new UserInterfaceInfo();
        if (userinterfaceinfoQueryRequest != null) {
            BeanUtils.copyProperties(userinterfaceinfoQueryRequest, userinterfaceinfoQuery);
        }
        QueryWrapper<UserInterfaceInfo> queryWrapper = new QueryWrapper<>(userinterfaceinfoQuery);
        List<UserInterfaceInfo> userinterfaceinfoList = userinterfaceinfoService.list(queryWrapper);
        return ResultUtils.success(userinterfaceinfoList);
    }

    /**
     * 分页获取列表（封装类）
     *
     * @param userInterfaceInfoQueryRequest
     * @param request
     * @return
     */
    @PostMapping("/list/page/vo")
    @AuthCheck(mustRole = UserConstant.ADMIN_ROLE)
    public BaseResponse<Page<UserInterfaceInfo>> listUserInterfaceInfoVOByPage(@RequestBody UserInterfaceInfoQueryRequest userInterfaceInfoQueryRequest,
                                                                               HttpServletRequest request) {
        long current = userInterfaceInfoQueryRequest.getCurrent();
        long size = userInterfaceInfoQueryRequest.getPageSize();
        // 限制爬虫
        ThrowUtils.throwIf(size > 20, ErrorCode.PARAMS_ERROR);
        Page<UserInterfaceInfo> userInterfaceInfoPage = userinterfaceinfoService.page(new Page<>(current, size),
                userinterfaceinfoService.getQueryWrapper(userInterfaceInfoQueryRequest));
        return ResultUtils.success(userinterfaceinfoService.getUserInterfaceInfoVOPage(userInterfaceInfoPage, request));
    }

}
