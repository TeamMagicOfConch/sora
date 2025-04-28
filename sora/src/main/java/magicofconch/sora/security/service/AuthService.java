package magicofconch.sora.security.service;

import java.util.UUID;
import lombok.RequiredArgsConstructor;
import magicofconch.sora.security.dto.req.LoginReq;
import magicofconch.sora.security.dto.req.RegisterReq;
import magicofconch.sora.security.dto.res.AuthRes;
import magicofconch.sora.security.dto.res.TokenDto;
import magicofconch.sora.security.jwt.JwtUtil;
import magicofconch.sora.security.os_id.OsIdAuthenticationToken;
import magicOfConch.user.OsAuthInfo;
import magicOfConch.user.UserInfo;
import magicOfConch.enums.UserRole;
import magicofconch.sora.user.repository.OsAuthInfoRepository;
import magicofconch.sora.user.repository.UserInfoRepository;
import magicofconch.sora.util.ResponseCode;
import magicofconch.sora.util.SecurityUtil;
import magicofconch.sora.util.exception.BusinessException;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final AuthenticationManagerBuilder authenticationManagerBuilder;
    private final UserInfoRepository userInfoRepository;
    private final OsAuthInfoRepository osAuthInfoRepository;
    private final JwtUtil jwtUtil;
    private final SecurityUtil securityUtil;

    @Transactional
    public AuthRes registerUser(RegisterReq registerReq) {

        if (osAuthInfoRepository.existsByOsId(registerReq.getOsId())) {
            throw new BusinessException(ResponseCode.USER_ALREADY_REGISTERED);
        }

        OsAuthInfo osAuthInfo = OsAuthInfo.builder()
                .osId(registerReq.getOsId())
                .osType(registerReq.getOsType())
                .build();

        UserInfo userInfo = UserInfo.builder()
                .osAuthInfo(osAuthInfo)
                .uuid(UUID.randomUUID().toString())
                .username(registerReq.getUsername())
                .initialReviewCount(registerReq.getInitialReviewCount())
                .role(UserRole.ROLE_USER.getRoleName())
                .build();

        String accessToken = jwtUtil.generateAccessToken(userInfo.getUuid(), UserRole.ROLE_USER.getRoleName());
        String refreshToken = jwtUtil.generateRefreshToken(userInfo.getUuid(), UserRole.ROLE_USER.getRoleName());

        userInfo.updateRefreshToken(refreshToken);

        osAuthInfoRepository.save(osAuthInfo);
        userInfoRepository.save(userInfo);

        AuthRes res = new AuthRes(accessToken, refreshToken, userInfo.getUsername());
        return res;
    }


    @Transactional
    public AuthRes login(LoginReq req) {

        OsIdAuthenticationToken authenticationToken = new OsIdAuthenticationToken(req.getOsId());

        OsIdAuthenticationToken osIdAuthenticationToken = (OsIdAuthenticationToken) authenticationManagerBuilder.getObject()
                .authenticate(authenticationToken);
        TokenDto tokenDto = jwtUtil.generateTokenDto(osIdAuthenticationToken);

        String uuid = osIdAuthenticationToken.getUserDetails().getUuid();

        UserInfo user = userInfoRepository.findUserInfoByUuid(uuid)
                .orElseThrow(() -> new BusinessException(ResponseCode.USER_NOT_FOUND));

        user.updateRefreshToken(tokenDto.getRefreshToken());

        return new AuthRes(tokenDto.getAccessToken(), tokenDto.getRefreshToken(),
                osIdAuthenticationToken.getUserDetails().getUsername());

    }

    @Transactional
    public void delete() {
        UserInfo user = securityUtil.getCurrentUsersEntity();
        userInfoRepository.delete(user);
    }
}
