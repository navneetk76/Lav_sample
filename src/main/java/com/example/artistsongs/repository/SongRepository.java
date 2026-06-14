package com.example.artistsongs.repository;

import com.example.artistsongs.entity.Song;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface SongRepository extends JpaRepository<Song, Long> {

    // Fetch song with its one-to-one detail (avoids extra query)
    @EntityGraph(attributePaths = "detail")
    Optional<Song> findWithDetailById(Long id);

    // All songs for a specific artist, including their details
    @EntityGraph(attributePaths = "detail")
    @Query("SELECT s FROM Song s WHERE s.artist.id = :artistId")
    List<Song> findByArtistIdWithDetail(Long artistId);

    List<Song> findByArtistId(Long artistId);
}
