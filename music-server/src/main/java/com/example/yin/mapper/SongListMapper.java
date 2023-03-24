package com.example.yin.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.yin.model.domain.SongList;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository

public interface SongListMapper extends BaseMapper<SongList> {
    Integer updateScore(int id);

    Integer countAll();

    List<SongList> rankAll();

}
