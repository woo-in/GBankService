package bankapp.request.signup;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class SignUpRequest {

    //@NotBlank(message = "아이디를 입력해주세요.")
    @Size(min=4,max=20 , message = "아이디는 4자 이상 20자 이하로 입력해주세요.")
    @Pattern(
            regexp = "^(?=.*[a-z])(?=.*[0-9])[a-z0-9]+$",
            message = "아이디는 영문 소문자와 숫자를 최소 1자 이상 포함해야 합니다."
    )
    private String username;



    //@NotBlank(message = "비밀번호를 입력해주세요.")
    @Pattern(
            regexp = "^(?=.*[a-zA-Z])(?=.*[0-9])(?=.*[!@#$%^&*]).{8,16}$",
            message = "비밀번호는 영문, 숫자, 특수문자를 모두 포함하여 8~16자로 입력해주세요."
    )
    private String password;

    @NotBlank(message = "비밀번호 확인을 입력해주세요.")
    private String passwordConfirm;

    //@NotBlank(message = "이름을 입력해주세요.")
    @Size(min = 2, max = 50, message = "이름은 2자 이상 50자 이하로 입력해주세요.")
    @Pattern(regexp = "^[가-힣]+$", message = "이름은 한글만 입력해주세요.")
    private String name;

   //@NotBlank(message = "계좌 별칭을 입력해주세요.")
    @Size(min = 2, max = 20, message = "계좌 별칭은 2자 이상 20자 이하로 입력해주세요.")
    private String accountNickname;

    public SignUpRequest() { }

    public SignUpRequest(String username, String password, String passwordConfirm, String name, String accountNickname) {
        this.username = username;
        this.password = password;
        this.passwordConfirm = passwordConfirm;
        this.name = name;
        this.accountNickname = accountNickname;
    }

}
