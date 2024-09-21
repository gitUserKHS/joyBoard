package com.me.board.dto;

import lombok.Data;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@Data
public class PageResponseDto<E> {

    private List<E> dtoList;

    private PageRequestDto pageRequestDTO;

    private boolean prev, next;

    private int totalCount, prevPage, nextPage, totalPage, current;

    public PageResponseDto(List<E> dtoList, PageRequestDto pageRequestDTO, long total){
        this.dtoList = dtoList;
        this.pageRequestDTO = pageRequestDTO;
        this.totalCount = (int)total;

        int pageSize = pageRequestDTO.getSize();
        int end = (int)(Math.ceil((double)pageRequestDTO.getPage() / pageSize)) * pageSize;
        int start = end - pageSize + 1;
        int last = (int)(Math.ceil(totalCount / (double)pageRequestDTO.getSize()));

        end = Math.min(end, last);

        this.prev = start > 1;
        this.next = totalCount > end * pageRequestDTO.getSize();
        this.prevPage = this.prev ? start - 1 : 0;
        this.nextPage = this.next ? end + 1 : 0;
        this.current = pageRequestDTO.getPage();
    }
}
