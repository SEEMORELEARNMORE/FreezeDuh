package com.example.yin.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.yin.model.domain.Song;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SongMapper extends BaseMapper<Song> {
    //更新歌曲的点击量
    Integer updateClickCount(int id);
    //统计并更新歌曲的收藏数
    Integer updateCollectionCount(int id);
    //统计并更新歌曲的评论数
    Integer updateCommentCount(int id);

    //排行根据歌曲点击量
    List<Song> rankSongByClick();
    //排行根据歌曲收藏量
    List<Song> rankSongByCollection();
    //排行根据歌曲评论量
    List<Song> rankSongByComment();

}
