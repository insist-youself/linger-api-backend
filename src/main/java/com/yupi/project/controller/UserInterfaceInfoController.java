package com.yupi.project.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.yupi.lingerapicommon.common.BaseResponse;
import com.yupi.lingerapicommon.common.DeleteRequest;
import com.yupi.lingerapicommon.common.ErrorCode;
import com.yupi.lingerapicommon.common.ResultUtils;
import com.yupi.lingerapicommon.constant.CommonConstant;
import com.yupi.lingerapicommon.constant.UserConstant;
import com.yupi.lingerapicommon.model.dto.userinterfaceinfo.UserInterfaceInfoAddRequest;
import com.yupi.lingerapicommon.model.dto.userinterfaceinfo.UserInterfaceInfoQueryRequest;
import com.yupi.lingerapicommon.model.dto.userinterfaceinfo.UserInterfaceInfoUpdateRequest;
import com.yupi.lingerapicommon.model.entity.User;
import com.yupi.lingerapicommon.model.entity.UserInterfaceInfo;
import com.yupi.project.annotation.AuthCheck;
import com.yupi.project.exception.BusinessException;
import com.yupi.project.service.UserInterfaceInfoService;
import com.yupi.project.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
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
    @AuthCheck(mustRole = UserConstant.ADMIN_ROLE)
    public BaseResponse<Long> addUserInterfaceInfo(@RequestBody UserInterfaceInfoAddRequest userinterfaceinfoAddRequest, HttpServletRequest request) {
        if (userinterfaceinfoAddRequest == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        UserInterfaceInfo userinterfaceinfo = new UserInterfaceInfo();
        BeanUtils.copyProperties(userinterfaceinfoAddRequest, userinterfaceinfo);
        // 校验
        userinterfaceinfoService.validUserInterfaceInfo(userinterfaceinfo, true);
        User loginUser = userService.getLoginUser(request);
        userinterfaceinfo.setUserId(loginUser.getId());
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
        if (!oldUserInterfaceInfo.getUserId().equals(user.getId()) && !userService.isAdmin(request)) {
            throw new BusinessException(ErrorCode.NO_AUTH_ERROR);
        }
        boolean result = userinterfaceinfoService.updateById(userinterfaceinfo);
        return ResultUtils.success(result);
    }

    /**
     * 根据 id 获取
     *
     * @param id
     * @return
     */
    @GetMapping("/get")
    @AuthCheck(mustRole = UserConstant.ADMIN_ROLE)
    public BaseResponse<UserInterfaceInfo> getUserInterfaceInfoById(long id) {
        if (id <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        UserInterfaceInfo userinterfaceinfo = userinterfaceinfoService.getById(id);
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
     * 分页获取列表
     *
     * @param userinterfaceinfoQueryRequest
     * @param request
     * @return
     */
    @GetMapping("/list/page")
    @AuthCheck(mustRole = UserConstant.ADMIN_ROLE)
    public BaseResponse<Page<UserInterfaceInfo>> listUserInterfaceInfoByPage(UserInterfaceInfoQueryRequest userinterfaceinfoQueryRequest, HttpServletRequest request) {
        if (userinterfaceinfoQueryRequest == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        UserInterfaceInfo userinterfaceinfoQuery = new UserInterfaceInfo();
        BeanUtils.copyProperties(userinterfaceinfoQueryRequest, userinterfaceinfoQuery);
        long current = userinterfaceinfoQueryRequest.getCurrent();
        long size = userinterfaceinfoQueryRequest.getPageSize();
        String sortField = userinterfaceinfoQueryRequest.getSortField();
        String sortOrder = userinterfaceinfoQueryRequest.getSortOrder();
        // 限制爬虫
        if (size > 50) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        QueryWrapper<UserInterfaceInfo> queryWrapper = new QueryWrapper<>(userinterfaceinfoQuery);
        queryWrapper.orderBy(StringUtils.isNotBlank(sortField),
                sortOrder.equals(CommonConstant.SORT_ORDER_ASC), sortField);
        Page<UserInterfaceInfo> userinterfaceinfoPage = userinterfaceinfoService.page(new Page<>(current, size), queryWrapper);
        return ResultUtils.success(userinterfaceinfoPage);
    }

}
