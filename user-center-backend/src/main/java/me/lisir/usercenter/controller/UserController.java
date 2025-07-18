package me.lisir.usercenter.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import me.lisir.usercenter.common.BaseResponse;
import me.lisir.usercenter.common.ErrorCode;
import me.lisir.usercenter.common.ResultUtils;
import me.lisir.usercenter.exception.BusinessException;
import me.lisir.usercenter.model.domain.User;
import me.lisir.usercenter.model.domain.request.UserLoginRequest;
import me.lisir.usercenter.model.domain.request.UserRegisterRequest;
import me.lisir.usercenter.service.UserService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

import static me.lisir.usercenter.constant.UserConstant.ADMIN_ROLE;
import static me.lisir.usercenter.constant.UserConstant.USER_LOGIN_STATE;

/**
 * 用户接口
 */
@RestController
@RequestMapping("/user")
@CrossOrigin(origins = {"http://localhost:5173"}, allowCredentials = "true")
public class UserController {

    @Resource
    private UserService userService;

    @PostMapping("/register")
    public BaseResponse<Long> userRegister(@RequestBody UserRegisterRequest userRegisterRequest) {
        if (userRegisterRequest == null) {
            throw new BusinessException(ErrorCode.NULL_ERROR, "请求参数为空");
        }

        String userAccount = userRegisterRequest.getUserAccount();
        String userPassword = userRegisterRequest.getUserPassword();
        String checkPassword = userRegisterRequest.getCheckPassword();

        if (StringUtils.isAnyBlank(userAccount, userPassword, checkPassword)) {
            throw new BusinessException(ErrorCode.NULL_ERROR, "请求参数为空");
        }

        long result = userService.userRegister(userAccount, userPassword, checkPassword);

        return ResultUtils.success(result);
    }

    @PostMapping("/login")
    public BaseResponse<User> userLogin(@RequestBody UserLoginRequest userLoginRequest, HttpServletRequest request) {
        if (userLoginRequest == null) {
            throw new BusinessException(ErrorCode.NULL_ERROR, "请求参数为空");
        }

        String userAccount = userLoginRequest.getUserAccount();
        String userPassword = userLoginRequest.getUserPassword();

        if (StringUtils.isAnyBlank(userAccount, userPassword)) {
            throw new BusinessException(ErrorCode.NULL_ERROR, "请求参数为空");
        }

        User user = userService.userLogin(userAccount, userPassword, request);

        return ResultUtils.success(user);
    }

    @PostMapping("/logout")
    public BaseResponse<Integer> userLogout(HttpServletRequest request) {
        if (request == null) {
            throw new BusinessException(ErrorCode.NULL_ERROR, "请求参数为空");
        }

        int result = userService.userLogout(request);
        return ResultUtils.success(result);
    }

    @GetMapping("/current")
    public BaseResponse<User> getCurrentUser(HttpServletRequest request) {
        Object userObj = request.getSession().getAttribute(USER_LOGIN_STATE);
        User currentUser = (User) userObj;
        if (currentUser == null) {
            throw new BusinessException(ErrorCode.NOT_LOGIN, "未登录");
        }

        long userId = currentUser.getId();
        User user = userService.getById(userId);
        if (user == null) {
            throw new BusinessException(ErrorCode.NULL_ERROR, "用户不存在");
        }
        User safetyUser = userService.getSafetyUser(user);

        return ResultUtils.success(safetyUser);
    }

    @GetMapping("/search")
    public BaseResponse<List<User>> searchUser(@RequestParam String username, HttpServletRequest request) {
        if (!isAdmin(request)) {
            throw new BusinessException(ErrorCode.NO_AUTH, "无权限");
        }
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        if (StringUtils.isNotBlank(username)) {
            queryWrapper.like("username", username);
        }
        List<User> userList = userService.list(queryWrapper);
        List<User> list = userList.stream().map(user -> userService.getSafetyUser(user)).collect(Collectors.toList());

        return ResultUtils.success(list);
    }

    @PostMapping("/delete")
    public BaseResponse<Boolean> deleteUser(@RequestBody long id, HttpServletRequest request) {
        if (!isAdmin(request)) {
            throw new BusinessException(ErrorCode.NO_AUTH, "无权限");
        }
        if (id <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "请求参数错误");
        }
        boolean b = userService.removeById(id);

        return ResultUtils.success(b);
    }

    /**
     * 是否为管理员
     *
     * @param request 请求
     * @return boolean
     */
    private boolean isAdmin(HttpServletRequest request) {
        // 鉴权，仅管理员可查询
        Object userObj = request.getSession().getAttribute(USER_LOGIN_STATE);
        User user = (User) userObj;
        if (user == null) {
            throw new BusinessException(ErrorCode.NOT_LOGIN, "未登录");
        }
        return user.getUserRole() == ADMIN_ROLE;
    }
}
