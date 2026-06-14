-- ================================================================
-- TODO (Step 3): Replace with seed data that matches your domain.
-- Table names match the @Table(name=...) annotations in the entities.
-- Column names match entity field names (Hibernate snake_case conversion):
--   entryYear  -> entry_year
--   dataValue  -> data_value
--   category   -> category
-- ================================================================

-- Parent records (one Parent has many Children)
INSERT INTO parent (id, name, category, description, entry_year) VALUES
  (1, 'Parent One',   'Type A', 'First parent record',  2010),
  (2, 'Parent Two',   'Type B', 'Second parent record', 2015),
  (3, 'Parent Three', 'Type A', 'Third parent record',  2020);

-- Child records (each belongs to one Parent — one-to-many)
INSERT INTO child (id, name, data_value, entry_year, parent_id) VALUES
  (1, 'Child 1-A', 'value-1a', 2011, 1),
  (2, 'Child 1-B', 'value-1b', 2012, 1),
  (3, 'Child 1-C', 'value-1c', 2013, 1),
  (4, 'Child 2-A', 'value-2a', 2016, 2),
  (5, 'Child 2-B', 'value-2b', 2017, 2),
  (6, 'Child 3-A', 'value-3a', 2021, 3),
  (7, 'Child 3-B', 'value-3b', 2022, 3);

-- ChildDetail records (one-to-one with Child)
INSERT INTO child_detail (id, child_id, description, additional_info, numeric_value, notes) VALUES
  (1, 1, 'Detail for Child 1-A', 'Extra info A', 100, 'Note A'),
  (2, 2, 'Detail for Child 1-B', 'Extra info B', 200, 'Note B'),
  (3, 3, 'Detail for Child 1-C', 'Extra info C', 300, 'Note C'),
  (4, 4, 'Detail for Child 2-A', 'Extra info D', 400, 'Note D'),
  (5, 5, 'Detail for Child 2-B', 'Extra info E', 500, 'Note E'),
  (6, 6, 'Detail for Child 3-A', 'Extra info F', 600, 'Note F'),
  (7, 7, 'Detail for Child 3-B', 'Extra info G', 700, 'Note G');

-- Advance identity sequences past the seed data so auto-generated IDs don't collide
ALTER TABLE parent       ALTER COLUMN id RESTART WITH 100;
ALTER TABLE child        ALTER COLUMN id RESTART WITH 100;
ALTER TABLE child_detail ALTER COLUMN id RESTART WITH 100;
