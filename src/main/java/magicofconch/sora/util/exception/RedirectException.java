package magicofconch.sora.util.exception;

import lombok.Builder;
import lombok.Getter;
import magicofconch.sora.util.ResponseCode;

@Getter
public class RedirectException extends RuntimeException{
	private String redirectURI;

	public RedirectException(String redirectURI) {
		this.redirectURI = redirectURI;
	}
}
