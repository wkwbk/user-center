package me.lisir.usercenter.service;

import jakarta.annotation.Resource;
import me.lisir.usercenter.model.domain.User;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

/**
 * 用户服务测试
 */
@SpringBootTest
class UserServiceTest {

    @Resource
    private UserService userService;

    @Test
    public void testAddUser() {
        User user = new User();
        user.setUsername("LISIR");
        user.setUserAccount("778057151");
        user.setAvatarUrl("https://lisir.me/logo.png");
        user.setGender(0);
        user.setUserPassword("123456");
        user.setPhone("123");
        user.setEmail("123");

        boolean result = userService.save(user);

        System.out.println(user.getId());

        Assertions.assertTrue(result);
    }

    @Test
    void testUserRegister_Success() {
        // 清理测试数据
        // 正常注册
        String userAccount = "testuser";
        String userPassword = "12345678";
        String checkPassword = "12345678";
        long result = userService.userRegister(userAccount, userPassword, checkPassword);
        Assertions.assertTrue(result > 0);
    }

    @Test
    void testUserRegister_PasswordEmpty() {
        // 密码为空
        String userAccount = "testuser";
        String userPassword = "";
        String checkPassword = "12345678";
        long result = userService.userRegister(userAccount, userPassword, checkPassword);
        Assertions.assertEquals(-1, result);
    }

    @Test
    void testUserRegister_AccountTooShort() {
        // 账号过短
        String userAccount = "test";
        String userPassword = "12345678";
        String checkPassword = "12345678";
        long result = userService.userRegister(userAccount, userPassword, checkPassword);
        Assertions.assertEquals(-1, result);
    }

    @Test
    void testUserRegister_PasswordTooShort() {
        // 密码过短
        String userAccount = "testuser";
        String userPassword = "12345";
        String checkPassword = "12345";
        long result = userService.userRegister(userAccount, userPassword, checkPassword);
        Assertions.assertEquals(-1, result);
    }

    @Test
    void testUserRegister_AccountHasSpecialChar() {
        // 账号包含特殊字符
        String userAccount = "test@user";
        String userPassword = "12345678";
        String checkPassword = "12345678";
        long result = userService.userRegister(userAccount, userPassword, checkPassword);
        Assertions.assertEquals(-1, result);
    }

    @Test
    void testUserRegister_PasswordNotMatch() {
        // 密码和校验密码不匹配
        String userAccount = "testuser";
        String userPassword = "12345678";
        String checkPassword = "123456789";
        long result = userService.userRegister(userAccount, userPassword, checkPassword);
        Assertions.assertEquals(-1, result);
    }

    @Test
    void testUserRegister_AccountDuplicate() {
        // 账号重复
        String userAccount = "testuser";
        String userPassword = "12345678";
        String checkPassword = "12345678";
        // 先注册一次
        userService.userRegister(userAccount, userPassword, checkPassword);
        // 再次注册同一账号
        long result = userService.userRegister(userAccount, userPassword, checkPassword);
        Assertions.assertEquals(-1, result);
    }
}