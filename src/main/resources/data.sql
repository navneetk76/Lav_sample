-- ================================================================
-- SINGLE_TABLE: all subtypes share one table.
-- The item_type column is the discriminator — it identifies
-- which Java subclass each row maps to.
--
-- TODO: Replace with seed data matching your domain.
-- ================================================================

INSERT INTO item (id, item_type, name, description, entry_year,
                  extra_field_a,
                  extra_field_b, numeric_field) VALUES
  -- TYPE_A rows: extra_field_b and numeric_field are NULL
  (1, 'STUDENT', 'Student-1', 'First Address',  2020, Y, 'Class 1',  NULL, NULL),
  (2, 'STUDENT', 'Student-2', 'Second Address', 2021, N, 'Class 1',   NULL, NULL),
  (3, 'STUDENT', 'Student-3', 'Third Address',  2022, Y, 'Class 2',  NULL, NULL),
  -- TYPE_B rows: extra_field_a is NULL
  (4, 'TEACHER', 'Teacher-1', 'First TypeB Address',  2020, Y, NULL, 'Qualification-b-1', 10),
  (5, 'TEACHER', 'Teacher-2', 'Second TypeB Address', 2021, Y,  NULL, 'Qualification-b-2', 12),
  (6, 'TEACHER', 'Teacher-3', 'Third TypeB Address',  2022, N, NULL, 'Qualification-b-3', 13);

ALTER TABLE item ALTER COLUMN id RESTART WITH 100;
