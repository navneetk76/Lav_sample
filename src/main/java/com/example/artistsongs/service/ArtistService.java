package com.example.artistsongs.service;

import com.example.artistsongs.dto.ArtistDto;
import com.example.artistsongs.dto.CreateArtistRequest;
import com.example.artistsongs.dto.SongDetailDto;
import com.example.artistsongs.dto.SongDto;
import com.example.artistsongs.entity.Artist;
import com.example.artistsongs.entity.Song;
import com.example.artistsongs.entity.SongDetail;
import com.example.artistsongs.exception.ResourceNotFoundException;
import com.example.artistsongs.repository.ArtistRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ArtistService {

    private final ArtistRepository artistRepository;

    public List<ArtistDto> findAll() {
        return artistRepository.findAllWithSongs().stream()
                .map(this::toDto)
                .toList();
    }

    public ArtistDto findById(Long id) {
        Artist artist = artistRepository.findWithSongsById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Artist not found: " + id));
        return toDto(artist);
    }

    @Transactional
    public ArtistDto create(CreateArtistRequest req) {
        Artist artist = Artist.builder()
                .name(req.getName())
                .nationality(req.getNationality())
                .genre(req.getGenre())
                .debutYear(req.getDebutYear())
                .build();
        return toDto(artistRepository.save(artist));
    }

    @Transactional
    public ArtistDto update(Long id, CreateArtistRequest req) {
        Artist artist = artistRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Artist not found: " + id));
        artist.setName(req.getName());
        artist.setNationality(req.getNationality());
        artist.setGenre(req.getGenre());
        artist.setDebutYear(req.getDebutYear());
        return toDto(artistRepository.save(artist));
    }

    @Transactional
    public void delete(Long id) {
        // Load songs eagerly so Hibernate's cascade delete fires for each child
        Artist artist = artistRepository.findWithSongsById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Artist not found: " + id));
        artistRepository.delete(artist);
    }

    // ---- Mapping helpers ----

    public ArtistDto toDto(Artist artist) {
        List<SongDto> songDtos = artist.getSongs().stream()
                .map(this::toSongDto)
                .toList();
        return ArtistDto.builder()
                .id(artist.getId())
                .name(artist.getName())
                .nationality(artist.getNationality())
                .genre(artist.getGenre())
                .debutYear(artist.getDebutYear())
                .songs(songDtos)
                .build();
    }

    private SongDto toSongDto(Song song) {
        SongDetailDto detailDto = null;
        if (song.getDetail() != null) {
            detailDto = toDetailDto(song.getDetail());
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

    private SongDetailDto toDetailDto(SongDetail d) {
        return SongDetailDto.builder()
                .id(d.getId())
                .album(d.getAlbum())
                .durationSeconds(d.getDurationSeconds())
                .genre(d.getGenre())
                .lyricsSnippet(d.getLyricsSnippet())
                .build();
    }
}
