package io.blog.springblogapp.model.request;

import lombok.*;

@Builder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ResetPasswordUpdateRequest {

    private String newPassword;

    private String confirmNewPassword;

}
