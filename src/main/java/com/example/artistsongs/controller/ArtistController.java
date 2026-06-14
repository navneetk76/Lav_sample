package com.example.artistsongs.controller;

import com.example.artistsongs.dto.ArtistDto;
import com.example.artistsongs.dto.CreateArtistRequest;
import com.example.artistsongs.service.ArtistService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Exposes CRUD endpoints for Artists.
 * Each artist response includes its list of songs (one-to-many).
 */
@RestController
@RequestMapping("/api/artists")
@RequiredArgsConstructor
public class ArtistController {

    private final ArtistService artistService;

    // GET /api/artists
    @GetMapping
    public List<ArtistDto> getAll() {
        return artistService.findAll();
    }

    // GET /api/artists/{id}
    @GetMapping("/{id}")
    public ArtistDto getById(@PathVariable Long id) {
        return artistService.findById(id);
    }

    // POST /api/artists
    @PostMapping
    public ResponseEntity<ArtistDto> create(@Valid @RequestBody CreateArtistRequest req) {
        ArtistDto created = artistService.create(req);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    // PUT /api/artists/{id}
    @PutMapping("/{id}")
    public ArtistDto update(@PathVariable Long id, @Valid @RequestBody CreateArtistRequest req) {
        return artistService.update(id, req);
    }

    // DELETE /api/artists/{id}
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long id) {
        artistService.delete(id);
    }
}
