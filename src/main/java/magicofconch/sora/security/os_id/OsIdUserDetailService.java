package magicofconch.sora.security.os_id;

import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.extern.slf4j.Slf4j;
import magicofconch.sora.security.CustomUserDetails;
import magicofconch.sora.security.UserDto;
import magicofconch.sora.user.entity.OsAuthInfo;
import magicofconch.sora.user.repository.OsAuthInfoRepository;

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
			.orElseThrow(() -> new UsernameNotFoundException("없는 os_id 입니다"));

		CustomUserDetails userDetails = new CustomUserDetails(UserDto.fromUserInfo(osAuthInfo.getUserInfo(), osAuthInfo.getOsId()));
		log.info("OsIdUserDetailService - loadUserByUsername] userDetails role = {}", userDetails.getAuthorities());

		return userDetails;
	}
}