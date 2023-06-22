package com.tacitn.songservice.domain.vo;
import lombok.Data;

@Data
public class ListSongParams {
    private Integer page = 1;
    private Integer size = 6;
    private String sortBy;
    private Integer sortOrder = 1;
    private Long songListId;
    private Long singerId;
}
