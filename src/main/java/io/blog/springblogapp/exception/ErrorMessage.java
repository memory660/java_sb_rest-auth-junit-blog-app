package io.blog.springblogapp.exception;

import lombok.*;

import java.time.OffsetDateTime;

@Builder
@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class ErrorMessage {

    private OffsetDateTime timestamp;
    private Integer status;
    private String message;

}
