package magicofconch.sora.util;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;
import magicofconch.sora.security.os_id.OsIdAuthenticationToken;
import magicofconch.sora.user.entity.UserInfo;
import magicofconch.sora.user.repository.UserInfoRepository;
import magicofconch.sora.util.exception.BusinessException;

@Component
@RequiredArgsConstructor
public class SecurityUtil {

	private final UserInfoRepository userInfoRepository;

	/**
	 * @return current UserInfo entity
	 */
	public UserInfo getCurrentUsersEntity(){
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

		if (authentication != null && authentication.getPrincipal() instanceof OsIdAuthenticationToken userDetails) {
			String uuid = userDetails.getUserDetails().getUuid();
			return userInfoRepository.findUserInfoByUuid(uuid)
				.orElseThrow(() -> new BusinessException(ResponseCode.USER_NOT_FOUND));
		}
		throw new BusinessException(ResponseCode.USER_NOT_FOUND);
	}

}
