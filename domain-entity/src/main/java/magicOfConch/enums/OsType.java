package magicOfConch.enums;

public enum OsType {
	WINDOW,
	IOS,
	ANDROID,
	MAC;

	public static OsType findOsType(String osType) {

		switch (osType) {
			case "WINDOW":
				return WINDOW;
			case "IOS":
				return IOS;
			case "ANDROID":
				return ANDROID;
			case "MAC":
				return MAC;

			default:
				return null;
		}
	}

}
