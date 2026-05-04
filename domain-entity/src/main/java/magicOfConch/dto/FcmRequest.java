package magicOfConch.dto;

import magicOfConch.enums.OsType;

public record FcmRequest(OsType osType, String title, String body, String token) {
}
