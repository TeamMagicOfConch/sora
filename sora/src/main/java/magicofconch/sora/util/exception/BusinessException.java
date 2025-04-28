package magicofconch.sora.util.exception;


import lombok.Builder;
import lombok.Getter;
import magicofconch.sora.util.ResponseCode;

public class BusinessException extends RuntimeException{
    @Getter
    private final ResponseCode responseCode;

    @Builder
    public BusinessException(ResponseCode responseCode) {
        super(responseCode.getMessage());
        this.responseCode= responseCode;
    }

}
