package com.zaori.baofu.service.impl;

import com.zaori.baofu.common.BusinessException;
import com.zaori.baofu.config.JwtUtil;
import com.zaori.baofu.config.WxProperties;
import com.zaori.baofu.dto.request.WxLoginRequest;
import com.zaori.baofu.dto.response.LoginResponse;
import com.zaori.baofu.dto.response.UserInfoResponse;
import com.zaori.baofu.entity.SysUser;
import com.zaori.baofu.mapper.SysUserMapper;
import com.zaori.baofu.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.Map;

/**
 * 用户服务实现
 * 登录流程：开发模式直接使用固定用户；生产模式调用微信 code2session 换 openid
 */
@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final SysUserMapper sysUserMapper;
    private final RestTemplate restTemplate;
    private final WxProperties wxProperties;
    private final JwtUtil jwtUtil;
    private final StatisticsAggregator statisticsAggregator;

    /** 开发模式固定用户，避免每次登录都新建用户 */
    private static final String MOCK_OPENID = "dev_mock_openid";

    @Override
    public LoginResponse login(WxLoginRequest request) {
        String openid = resolveOpenid(request.getCode());

        // 按 openid 查询用户，不存在则创建
        SysUser user = sysUserMapper.selectByOpenid(openid);
        if (user == null) {
            user = new SysUser();
            user.setOpenid(openid);
            user.setNickname("暴富用户");
            user.setStatus(1);
            sysUserMapper.insert(user);
        }

        // 签发 JWT
        String token = jwtUtil.generateToken(user.getId());

        LoginResponse resp = new LoginResponse();
        resp.setToken(token);
        UserInfoResponse info = new UserInfoResponse();
        info.setId(user.getId());
        info.setNickname(user.getNickname());
        info.setAvatar(user.getAvatarUrl());
        resp.setUser(info);
        return resp;
    }

    @Override
    public UserInfoResponse getProfile(Long userId) {
        SysUser user = sysUserMapper.selectById(userId);
        if (user == null) {
            throw new BusinessException("用户不存在");
        }

        UserInfoResponse resp = new UserInfoResponse();
        resp.setId(user.getId());
        resp.setNickname(user.getNickname());
        resp.setAvatar(user.getAvatarUrl());

        // 聚合查询累计投入 / 盈利 / 亏损
        StatisticsAggregator.Totals totals = statisticsAggregator.aggregate(
                statisticsAggregator.queryRange(userId, null, null));
        resp.setTotalInvestment(totals.getInvestAmount());
        resp.setTotalProfit(totals.getProfitAmount());
        resp.setTotalLoss(totals.getLossAmount());
        return resp;
    }

    /**
     * 解析 openid：
     * - 开发模式（未配置真实微信凭据）返回固定 mock 用户
     * - 生产模式调用微信 jscode2session
     */
    private String resolveOpenid(String code) {
        boolean useMock = wxProperties.isMockLogin()
                || wxProperties.getAppid() == null
                || "your-appid".equals(wxProperties.getAppid());
        if (useMock) {
            return MOCK_OPENID;
        }

        String url = "https://api.weixin.qq.com/sns/jscode2session"
                + "?appid={appid}&secret={secret}&js_code={code}&grant_type=authorization_code";
        Map<String, Object> wxResponse = restTemplate.getForObject(
                url,
                Map.class,
                wxProperties.getAppid(),
                wxProperties.getSecret(),
                code);

        String openid = wxResponse == null ? null : (String) wxResponse.get("openid");
        if (openid == null || openid.isEmpty()) {
            String errMsg = wxResponse == null ? "微信无响应" : String.valueOf(wxResponse.get("errmsg"));
            throw new BusinessException("微信登录失败：" + errMsg);
        }
        return openid;
    }
}
