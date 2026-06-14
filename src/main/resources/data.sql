-- Artists
INSERT INTO artist (id, name, nationality, genre, debut_year) VALUES
  (1, 'Taylor Swift',   'American', 'Pop/Country', 2006),
  (2, 'Ed Sheeran',     'British',  'Pop',         2011),
  (3, 'The Weeknd',     'Canadian', 'R&B',         2010);

-- Songs (each belongs to one artist — one-to-many)
INSERT INTO song (id, title, release_year, artist_id) VALUES
  (1,  'Love Story',       2008, 1),
  (2,  'Shake It Off',     2014, 1),
  (3,  'Anti-Hero',        2022, 1),
  (4,  'Shape of You',     2017, 2),
  (5,  'Perfect',          2017, 2),
  (6,  'Blinding Lights',  2019, 3),
  (7,  'Starboy',          2016, 3);

-- Song details (one-to-one with song)
INSERT INTO song_detail (id, song_id, album, duration_seconds, genre, lyrics_snippet) VALUES
  (1, 1,  'Fearless',           235, 'Country Pop', 'We were both young when I first saw you'),
  (2, 2,  '1989',               219, 'Synth-pop',   'I stay out too late, got nothing in my brain'),
  (3, 3,  'Midnights',          200, 'Indie Pop',   'I have this thing where I get older but just never wiser'),
  (4, 4,  'Divide',             234, 'Pop',         'The club isn''t the best place to find a lover'),
  (5, 5,  'Divide',             263, 'Pop Ballad',  'I found a love for me, darling just dive right in'),
  (6, 6,  'After Hours',        200, 'Synth-pop',   'I said, ooh, I''m blinded by the lights'),
  (7, 7,  'Starboy',            230, 'R&B',         'I''m tryna put you in the worst mood, ah');

-- Advance identity sequences past the seed data so auto-generated IDs don't collide
ALTER TABLE artist    ALTER COLUMN id RESTART WITH 100;
ALTER TABLE song      ALTER COLUMN id RESTART WITH 100;
ALTER TABLE song_detail ALTER COLUMN id RESTART WITH 100;
