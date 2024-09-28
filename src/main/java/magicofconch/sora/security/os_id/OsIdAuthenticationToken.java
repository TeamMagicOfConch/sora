package magicofconch.sora.security.os_id;

import java.util.Collection;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;

import jakarta.servlet.http.HttpServletRequest;
import magicofconch.sora.security.CustomUserDetails;

public class OsIdAuthenticationToken extends AbstractAuthenticationToken {

	private final String osId;
	private CustomUserDetails userDetails;  // 인증 후 사용자 정보 저장

	// 인증 전 사용: osId만으로 초기화
	public OsIdAuthenticationToken(String osId) {
		super(null);
		this.osId = osId;
	}

	public OsIdAuthenticationToken(CustomUserDetails userDetails, Collection<? extends GrantedAuthority> authorities) {
		super(authorities);
		this.osId = userDetails.getUserDto().getOsId();
		this.userDetails = userDetails;
		super.setAuthenticated(true);
	}

	// 자격 증명 없음
	@Override
	public Object getCredentials() {

		return null;
	}

	// 인증 후 사용자 정보 반환
	@Override
	public Object getPrincipal() {

		return this.userDetails != null ? this.userDetails : this.osId;
	}

	public CustomUserDetails getUserDetails() {
		return this.userDetails;
	}
}
