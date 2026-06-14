package com.example.artistsongs.repository;

import com.example.artistsongs.entity.SongDetail;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface SongDetailRepository extends JpaRepository<SongDetail, Long> {

    Optional<SongDetail> findBySongId(Long songId);
}
