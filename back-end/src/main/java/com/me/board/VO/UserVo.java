package com.me.board.VO;

import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserVo {
    private String name;
    private String password;

    @Builder.Default
    private String role = "USER";
}
