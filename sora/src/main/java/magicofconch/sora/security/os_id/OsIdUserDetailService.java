package magicofconch.sora.security.os_id;

import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.extern.slf4j.Slf4j;
import magicofconch.sora.security.CustomUserDetails;
import magicofconch.sora.security.UserDto;
import magicOfConch.user.OsAuthInfo;
import magicofconch.sora.user.repository.OsAuthInfoRepository;
import magicofconch.sora.util.ResponseCode;
import magicofconch.sora.util.exception.BusinessException;

@Slf4j
@Service
public class OsIdUserDetailService implements UserDetailsService {

	private final OsAuthInfoRepository osAuthInfoRepository;
	public OsIdUserDetailService(OsAuthInfoRepository osAuthInfoRepository){
		this.osAuthInfoRepository = osAuthInfoRepository;
	}
	@Override
	@Transactional
	public CustomUserDetails loadUserByUsername(String osId) throws UsernameNotFoundException {

		OsAuthInfo osAuthInfo = osAuthInfoRepository.findOsAuthInfoByOsId(osId)
			.orElseThrow(() -> new BusinessException(ResponseCode.LOGIN_FAIL));

		CustomUserDetails userDetails = new CustomUserDetails(UserDto.fromUserInfo(osAuthInfo.getUserInfo(), osAuthInfo.getOsId()));

		return userDetails;
	}
}
