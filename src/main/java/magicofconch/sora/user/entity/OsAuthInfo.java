package magicofconch.sora.user.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import magicofconch.sora.util.BaseEntity;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "os_auth_info", uniqueConstraints = {
	@UniqueConstraint(
		name = "os_id_unique",
		columnNames = "os_auth_info_id"
	)
})
public class OsAuthInfo extends BaseEntity {

	@Id
	@Column(name = "os_auth_info_id")
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "user_info_id", insertable = false, updatable = false)
	private UserInfo userInfo;

	private String osId;
	private String osType;

	@Builder
	public OsAuthInfo(String osId, String osType){
		this.osId = osId;
		this.osType = osType;
	}
}
