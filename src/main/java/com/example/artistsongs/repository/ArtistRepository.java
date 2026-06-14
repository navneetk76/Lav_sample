package com.example.artistsongs.repository;

import com.example.artistsongs.entity.Artist;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface ArtistRepository extends JpaRepository<Artist, Long> {

    // Fetch artist together with its songs in a single query (avoids N+1)
    @EntityGraph(attributePaths = "songs")
    Optional<Artist> findWithSongsById(Long id);

    // Fetch all artists with their songs
    @EntityGraph(attributePaths = "songs")
    @Query("SELECT DISTINCT a FROM Artist a")
    List<Artist> findAllWithSongs();

    List<Artist> findByGenreIgnoreCase(String genre);
}
