package magicofconch.sora.security;

import lombok.Builder;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import magicofconch.sora.user.entity.UserInfo;

@Slf4j
@Data
@Builder
public class UserDto {
	private String uuid;
	private String osId;
	private String role;

	public static UserDto fromUserInfo(UserInfo userInfo, String osId){
		log.info("[UserDto fromUserInfo] - userInfo.getRole ={}, userInfo.getUuid={} ", userInfo.getRole(), userInfo.getUuid());
		return UserDto.builder()
			.uuid(userInfo.getUuid())
			.role(userInfo.getRole())
			.osId(osId)
			.build();
	}
}
