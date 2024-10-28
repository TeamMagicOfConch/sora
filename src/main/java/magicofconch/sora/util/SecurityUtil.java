package magicofconch.sora.util;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import magicofconch.sora.security.CustomUserDetails;
import magicofconch.sora.security.os_id.OsIdAuthenticationToken;
import magicofconch.sora.user.entity.UserInfo;
import magicofconch.sora.user.repository.UserInfoRepository;
import magicofconch.sora.util.exception.BusinessException;

@Slf4j
@Component
@RequiredArgsConstructor
public class SecurityUtil {

	private final UserInfoRepository userInfoRepository;

	/**
	 * @return current UserInfo entity
	 */
	public UserInfo getCurrentUsersEntity(){
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

		if (authentication != null && authentication.getPrincipal() instanceof CustomUserDetails userDetails) {
			String uuid = userDetails.getUuid();
			return userInfoRepository.findUserInfoByUuid(uuid)
				.orElseThrow(() -> new BusinessException(ResponseCode.USER_NOT_FOUND));
		}
		throw new BusinessException(ResponseCode.USER_NOT_FOUND);
	}

	public String getUserRole(){
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

		if (authentication != null && authentication.getPrincipal() instanceof CustomUserDetails userDetails) {

			String role = userDetails.getAuthorities().stream()
				.findFirst()
				.map(GrantedAuthority::getAuthority)
				.orElse("NONE");

			return role;
		}

		throw new BusinessException(ResponseCode.USER_NOT_FOUND);
	}

}
