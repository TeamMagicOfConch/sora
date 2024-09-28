package magicofconch.sora.security.os_id;

import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import magicofconch.sora.security.CustomUserDetails;

@Slf4j
@Component
@RequiredArgsConstructor
public class OsIdAuthenticationProvider implements AuthenticationProvider {

	private final OsIdUserDetailService osIdUserDetailService;
	@Override
	public Authentication authenticate(Authentication authentication) throws AuthenticationException {
		String osId = (String) authentication.getPrincipal();

		CustomUserDetails userDetails =  osIdUserDetailService.loadUserByUsername(osId);

		OsIdAuthenticationToken osIdAuthenticationToken = new OsIdAuthenticationToken(userDetails, userDetails.getAuthorities());
		osIdAuthenticationToken.setDetails(userDetails);
		osIdAuthenticationToken.setAuthenticated(true);
		return osIdAuthenticationToken;
	}

	@Override
	public boolean supports(Class<?> authentication) {
		return authentication.isAssignableFrom(OsIdAuthenticationToken.class);
	}
}
