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
  (1, 'TYPE_A', 'Item A-1', 'First TypeA item',  2020, 'alpha-value',  NULL, NULL),
  (2, 'TYPE_A', 'Item A-2', 'Second TypeA item', 2021, 'beta-value',   NULL, NULL),
  (3, 'TYPE_A', 'Item A-3', 'Third TypeA item',  2022, 'gamma-value',  NULL, NULL),
  -- TYPE_B rows: extra_field_a is NULL
  (4, 'TYPE_B', 'Item B-1', 'First TypeB item',  2020, NULL, 'extra-b-1', 100),
  (5, 'TYPE_B', 'Item B-2', 'Second TypeB item', 2021, NULL, 'extra-b-2', 200),
  (6, 'TYPE_B', 'Item B-3', 'Third TypeB item',  2022, NULL, 'extra-b-3', 300);

ALTER TABLE item ALTER COLUMN id RESTART WITH 100;
