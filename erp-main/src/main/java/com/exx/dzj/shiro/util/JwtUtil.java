package com.exx.dzj.shiro.util;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTDecodeException;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.exx.dzj.excepte.ErpException;
import com.exx.dzj.util.ConvertUtils;
import lombok.extern.slf4j.Slf4j;

import javax.servlet.http.HttpServletRequest;
import java.util.Date;

/**
 * @Author
 * @Date 2019/5/7 0007 10:07
 * @Description JWT工具类
 */
@Slf4j
public class JwtUtil {
    /**
     * 过期时间24小时
     */
    public static final long EXPIRE_TIME = 24 * 60 * 60 * 1000;

    /**
     * 校验token是否正确
     * @param token  密钥
     * @param secret 用户的密码
     * @return 是否正确
     */
    public static boolean verify(String token, String username, String secret) {
        try {
            // 根据密码生成JWT效验器
            Algorithm algorithm = Algorithm.HMAC256(secret);
            JWTVerifier verifier = JWT.require(algorithm).withClaim("userName", username).build();
            // 效验TOKEN
            DecodedJWT jwt = verifier.verify(token);
            return true;
        } catch (Exception ex) {
            log.error("调用方法{}错误信息{}", JwtUtil.class.getName()+".verify", ex.getMessage());
            return false;
        }
    }

    /**
     * 获得token中的信息无需secret解密也能获得
     * @return token中包含的用户名
     */
    public static String getUserName(String token) {
        try {
            DecodedJWT jwt = JWT.decode(token);
            return jwt.getClaim("userName").asString();
        } catch (JWTDecodeException e) {
            log.error("调用方法{}错误信息{}", JwtUtil.class.getName()+".getUserName", e.getMessage());
            return null;
        }
    }

    /**
     * 生成签名, 24小时后过期
     * @param username 用户名
     * @param secret   用户的密码
     * @return 加密的token
     */
    public static String sign(String username, String secret) {
        Date date = new Date(System.currentTimeMillis() + EXPIRE_TIME);
        Algorithm algorithm = Algorithm.HMAC256(secret);
        // 附带username信息
        return JWT.create().withClaim("userName", username).withExpiresAt(date).sign(algorithm);

    }

    /**
     * 根据request中的token获取用户账号
     * @param request
     * @return
     * @throws ErpException
     */
    public static String getUserNameByToken(HttpServletRequest request) throws ErpException {
        String accessToken = request.getHeader("user-token");
        String userName = getUserName(accessToken);
        if (ConvertUtils.isEmpty(userName)) {
            throw new ErpException(400, "未获取到用户");
        }
        return userName;
    }
}