package magicofconch.sora.security;

import lombok.Builder;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import magicOfConch.user.UserInfo;

@Slf4j
@Data
@Builder
public class UserDto {
	private String uuid;
	private String osId;
	private String role;
	private String username;

	public static UserDto fromUserInfo(UserInfo userInfo, String osId){
		return UserDto.builder()
			.uuid(userInfo.getUuid())
			.role(userInfo.getRole())
			.osId(osId)
			.username(userInfo.getUsername())
			.build();
	}
}
