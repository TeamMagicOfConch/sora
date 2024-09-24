package magicofconch.sora.security.os_id;

import java.util.Collection;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import magicofconch.sora.security.CustomUserDetails;

public class OsIdAuthenticationToken extends AbstractAuthenticationToken {

	private final String osId;
	private CustomUserDetails userDetails;  // 인증 후 사용자 정보 저장

	// 인증 전 사용: osId만으로 초기화
	public OsIdAuthenticationToken(String osId) {
		super(null);
		this.osId = osId;
	}

	// 인증 후 사용: 사용자 정보와 권한으로 초기화
	public OsIdAuthenticationToken(CustomUserDetails userDetails, Collection<? extends GrantedAuthority> authorities) {
		super(authorities);
		setAuthenticated(true);
		setDetails(userDetails);
		this.osId = userDetails.getUserDto().getOsId();
		this.userDetails = userDetails;
	}

	@Override
	public Object getCredentials() {
		return null;  // 자격 증명 없음
	}

	@Override
	public Object getPrincipal() {
		// 인증 후 사용자 정보 반환
		return this.userDetails != null ? this.userDetails : this.osId;
	}

	public CustomUserDetails getUserDetails() {
		return this.userDetails;
	}
}
