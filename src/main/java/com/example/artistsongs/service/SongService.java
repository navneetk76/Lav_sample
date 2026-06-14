package com.example.artistsongs.service;

import com.example.artistsongs.dto.CreateSongRequest;
import com.example.artistsongs.dto.SongDetailDto;
import com.example.artistsongs.dto.SongDto;
import com.example.artistsongs.entity.Artist;
import com.example.artistsongs.entity.Song;
import com.example.artistsongs.entity.SongDetail;
import com.example.artistsongs.exception.ResourceNotFoundException;
import com.example.artistsongs.repository.ArtistRepository;
import com.example.artistsongs.repository.SongRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class SongService {

    private final SongRepository songRepository;
    private final ArtistRepository artistRepository;

    public List<SongDto> findAll() {
        return songRepository.findAll().stream()
                .map(this::toDto)
                .toList();
    }

    public SongDto findById(Long id) {
        Song song = songRepository.findWithDetailById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Song not found: " + id));
        return toDto(song);
    }

    public List<SongDto> findByArtist(Long artistId) {
        return songRepository.findByArtistIdWithDetail(artistId).stream()
                .map(this::toDto)
                .toList();
    }

    @Transactional
    public SongDto create(CreateSongRequest req) {
        Artist artist = artistRepository.findById(req.getArtistId())
                .orElseThrow(() -> new ResourceNotFoundException("Artist not found: " + req.getArtistId()));

        Song song = Song.builder()
                .title(req.getTitle())
                .releaseYear(req.getReleaseYear())
                .build();

        // If detail was provided inline, attach it (one-to-one)
        if (req.getDetail() != null) {
            SongDetail detail = toDetailEntity(req.getDetail());
            song.setDetail(detail);
        }

        // addSong keeps both sides of the bidirectional relationship in sync
        artist.addSong(song);
        return toDto(songRepository.save(song));
    }

    /**
     * Add or replace the one-to-one SongDetail for a Song.
     * Demonstrates the one-to-one relationship update path.
     */
    @Transactional
    public SongDto upsertDetail(Long songId, SongDetailDto detailDto) {
        Song song = songRepository.findWithDetailById(songId)
                .orElseThrow(() -> new ResourceNotFoundException("Song not found: " + songId));

        SongDetail detail = song.getDetail();
        if (detail == null) {
            detail = new SongDetail();
        }
        detail.setAlbum(detailDto.getAlbum());
        detail.setDurationSeconds(detailDto.getDurationSeconds());
        detail.setGenre(detailDto.getGenre());
        detail.setLyricsSnippet(detailDto.getLyricsSnippet());

        song.setDetail(detail);
        return toDto(songRepository.save(song));
    }

    @Transactional
    public void delete(Long id) {
        Song song = songRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Song not found: " + id));
        songRepository.delete(song);
    }

    // ---- Mapping helpers ----

    public SongDto toDto(Song song) {
        SongDetailDto detailDto = null;
        if (song.getDetail() != null) {
            SongDetail d = song.getDetail();
            detailDto = SongDetailDto.builder()
                    .id(d.getId())
                    .album(d.getAlbum())
                    .durationSeconds(d.getDurationSeconds())
                    .genre(d.getGenre())
                    .lyricsSnippet(d.getLyricsSnippet())
                    .build();
        }
        return SongDto.builder()
                .id(song.getId())
                .title(song.getTitle())
                .releaseYear(song.getReleaseYear())
                .artistId(song.getArtist().getId())
                .artistName(song.getArtist().getName())
                .detail(detailDto)
                .build();
    }

    private SongDetail toDetailEntity(SongDetailDto dto) {
        return SongDetail.builder()
                .album(dto.getAlbum())
                .durationSeconds(dto.getDurationSeconds())
                .genre(dto.getGenre())
                .lyricsSnippet(dto.getLyricsSnippet())
                .build();
    }
}
