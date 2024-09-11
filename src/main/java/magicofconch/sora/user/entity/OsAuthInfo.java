package magicofconch.sora.user.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import magicofconch.sora.user.enums.OsType;

@Entity
public class OsAuthInfo {

	@Id
	@Column(name = "os_auth_info_id")
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	private String identifier;
	private OsType osType;
}
