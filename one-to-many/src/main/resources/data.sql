-- ================================================================
-- TODO: Replace with seed data that matches your domain.
-- Table names match the @Table(name=...) annotations in the entities.
-- Column names follow Hibernate's snake_case convention:
--   entryYear -> entry_year
--   dataValue -> data_value
-- ================================================================

-- Parent records (one Parent has many Children)
INSERT INTO parent (id, name, category, description, entry_year) VALUES
  (1, 'Parent One',   'Type A', 'First parent record',  2010),
  (2, 'Parent Two',   'Type B', 'Second parent record', 2015),
  (3, 'Parent Three', 'Type A', 'Third parent record',  2020);

-- Child records (each row belongs to one Parent)
INSERT INTO child (id, name, data_value, entry_year, parent_id) VALUES
  (1, 'Child 1-A', 'value-1a', 2011, 1),
  (2, 'Child 1-B', 'value-1b', 2012, 1),
  (3, 'Child 1-C', 'value-1c', 2013, 1),
  (4, 'Child 2-A', 'value-2a', 2016, 2),
  (5, 'Child 2-B', 'value-2b', 2017, 2),
  (6, 'Child 3-A', 'value-3a', 2021, 3),
  (7, 'Child 3-B', 'value-3b', 2022, 3);

-- Advance identity sequences past the seed data so auto-generated IDs don't collide
ALTER TABLE parent ALTER COLUMN id RESTART WITH 100;
ALTER TABLE child  ALTER COLUMN id RESTART WITH 100;
