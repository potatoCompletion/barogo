package com.barogo.api.auth.request;

import com.barogo.api.user.domain.User;
import jakarta.validation.constraints.NotBlank;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.security.crypto.password.PasswordEncoder;

@Getter
@Setter
@NoArgsConstructor
public class SignUpRequest {

    @NotBlank(message = "id를 입력해주세요.")
    private String userId;

    @NotBlank(message = "비밀번호를 입력해주세요.")
    private String password;

    @NotBlank(message = "이름을 입력해주세요.")
    private String name;

    @Builder
    public SignUpRequest(String userId, String password, String name) {
        this.userId = userId;
        this.password = password;
        this.name = name;
    }

    public User toUser(PasswordEncoder passwordEncoder) {
        return User.builder()
                .userId(this.userId)
                .password(passwordEncoder.encode(this.password))
                .name(this.name)
                .build();
    }
}
