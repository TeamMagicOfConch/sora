package magicofconch.sora.user.controller;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;
import magicofconch.sora.user.dto.req.RegisterReq;
import magicofconch.sora.user.dto.res.RegisterRes;
import magicofconch.sora.user.service.UserService;
import magicofconch.sora.util.Response;

@RestController
@RequiredArgsConstructor
public class UserController {

	private final UserService userService;


	@PostMapping("/register")
	public Response<RegisterRes> registerUser(@RequestBody  RegisterReq registerReq){
		RegisterRes res = userService.registerUser(registerReq);

		return Response.ok(res);

	}

}
