package com.example.artistsongs;

import com.example.artistsongs.dto.ArtistDto;
import com.example.artistsongs.dto.CreateArtistRequest;
import com.example.artistsongs.dto.CreateSongRequest;
import com.example.artistsongs.dto.SongDetailDto;
import com.example.artistsongs.dto.SongDto;
import com.example.artistsongs.service.ArtistService;
import com.example.artistsongs.service.SongService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@Transactional
class ArtistSongsApplicationTests {

    @Autowired
    ArtistService artistService;

    @Autowired
    SongService songService;

    @Test
    void contextLoads() {
    }

    @Test
    void oneToMany_artistHasManySongs() {
        // Seed data has 3 songs for Taylor Swift (id=1)
        ArtistDto artist = artistService.findById(1L);
        assertThat(artist.getName()).isEqualTo("Taylor Swift");
        assertThat(artist.getSongs()).hasSize(3);
    }

    @Test
    void oneToMany_createArtistWithSongs() {
        // Create a new artist
        CreateArtistRequest artistReq = new CreateArtistRequest("Adele", "British", "Soul", 2008);
        ArtistDto artist = artistService.create(artistReq);
        assertThat(artist.getId()).isNotNull();

        // Add a song linked to this artist (one-to-many)
        CreateSongRequest songReq = new CreateSongRequest("Hello", 2015, artist.getId(), null);
        SongDto song = songService.create(songReq);
        assertThat(song.getArtistId()).isEqualTo(artist.getId());
        assertThat(song.getArtistName()).isEqualTo("Adele");

        // Verify artist now shows the song
        ArtistDto updated = artistService.findById(artist.getId());
        assertThat(updated.getSongs()).hasSize(1);
        assertThat(updated.getSongs().get(0).getTitle()).isEqualTo("Hello");
    }

    @Test
    void oneToOne_songHasSongDetail() {
        // Seed data: song id=1 "Love Story" has a SongDetail
        SongDto song = songService.findById(1L);
        assertThat(song.getTitle()).isEqualTo("Love Story");
        assertThat(song.getDetail()).isNotNull();
        assertThat(song.getDetail().getAlbum()).isEqualTo("Fearless");
        assertThat(song.getDetail().getDurationSeconds()).isEqualTo(235);
    }

    @Test
    void oneToOne_upsertSongDetail() {
        // Create a song without detail
        CreateSongRequest songReq = new CreateSongRequest("Someone Like You", 2011, 2L, null);
        SongDto song = songService.create(songReq);
        assertThat(song.getDetail()).isNull();

        // Add detail via upsert (demonstrates one-to-one)
        SongDetailDto detailDto = SongDetailDto.builder()
                .album("21")
                .durationSeconds(285)
                .genre("Soul")
                .lyricsSnippet("I hate to turn up out of the blue uninvited")
                .build();
        SongDto updated = songService.upsertDetail(song.getId(), detailDto);

        assertThat(updated.getDetail()).isNotNull();
        assertThat(updated.getDetail().getAlbum()).isEqualTo("21");
        assertThat(updated.getDetail().getDurationSeconds()).isEqualTo(285);

        // Call again to verify update (not duplicate insert)
        SongDetailDto updatedDetail = SongDetailDto.builder()
                .album("21 (Deluxe)")
                .durationSeconds(285)
                .genre("Soul")
                .lyricsSnippet("I hate to turn up out of the blue uninvited")
                .build();
        SongDto reupdated = songService.upsertDetail(song.getId(), updatedDetail);
        assertThat(reupdated.getDetail().getAlbum()).isEqualTo("21 (Deluxe)");
    }

    @Test
    void oneToMany_deleteCascadesToSongs() {
        // Create an artist with songs, then delete the artist
        CreateArtistRequest artistReq = new CreateArtistRequest("Temp Artist", "American", "Pop", 2020);
        ArtistDto artist = artistService.create(artistReq);

        songService.create(new CreateSongRequest("Temp Song 1", 2021, artist.getId(), null));
        songService.create(new CreateSongRequest("Temp Song 2", 2022, artist.getId(), null));

        List<SongDto> songsBeforeDelete = songService.findByArtist(artist.getId());
        assertThat(songsBeforeDelete).hasSize(2);

        // Delete artist — cascade should remove songs too
        artistService.delete(artist.getId());

        List<SongDto> songsAfterDelete = songService.findByArtist(artist.getId());
        assertThat(songsAfterDelete).isEmpty();
    }
}
