package com.me.board.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class PageRequestDto {

    @Builder.Default
    private int page = 1;

    @Builder.Default
    private int size = 10;
}
