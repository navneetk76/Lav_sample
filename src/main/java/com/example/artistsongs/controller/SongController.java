package com.example.artistsongs.controller;

import com.example.artistsongs.dto.CreateSongRequest;
import com.example.artistsongs.dto.SongDetailDto;
import com.example.artistsongs.dto.SongDto;
import com.example.artistsongs.service.SongService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Exposes CRUD endpoints for Songs.
 * Each song response includes its one-to-one SongDetail.
 */
@RestController
@RequestMapping("/api/songs")
@RequiredArgsConstructor
public class SongController {

    private final SongService songService;

    // GET /api/songs
    @GetMapping
    public List<SongDto> getAll() {
        return songService.findAll();
    }

    // GET /api/songs/{id}  — includes the one-to-one detail
    @GetMapping("/{id}")
    public SongDto getById(@PathVariable Long id) {
        return songService.findById(id);
    }

    // GET /api/songs?artistId=1  — all songs for an artist (one-to-many query)
    @GetMapping(params = "artistId")
    public List<SongDto> getByArtist(@RequestParam Long artistId) {
        return songService.findByArtist(artistId);
    }

    // POST /api/songs
    @PostMapping
    public ResponseEntity<SongDto> create(@Valid @RequestBody CreateSongRequest req) {
        SongDto created = songService.create(req);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    // PUT /api/songs/{id}/detail  — add or replace the one-to-one SongDetail
    @PutMapping("/{id}/detail")
    public SongDto upsertDetail(@PathVariable Long id, @RequestBody SongDetailDto detailDto) {
        return songService.upsertDetail(id, detailDto);
    }

    // DELETE /api/songs/{id}
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long id) {
        songService.delete(id);
    }
}
