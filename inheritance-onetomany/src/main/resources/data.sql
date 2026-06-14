-- ================================================================
-- ONE-TO-MANY + INHERITANCE combined:
--   - owner table  (one owner has many items)
--   - item table   (SINGLE_TABLE: all subtypes in one table)
--                  owner_id FK links each item back to its owner
-- TODO: Replace with seed data that matches your domain.
-- ================================================================

-- Owners (one side of one-to-many)
INSERT INTO owner (id, name, category, entry_year) VALUES
  (1, 'Owner One',   'Cat A', 2010),
  (2, 'Owner Two',   'Cat B', 2015),
  (3, 'Owner Three', 'Cat A', 2020);

-- Items (many side — mix of TYPE_A and TYPE_B subtypes)
INSERT INTO item (id, item_type, name, description, entry_year,
                  extra_field_a,
                  extra_field_b, numeric_field,
                  owner_id) VALUES
  -- Owner 1 has 2 TYPE_A + 1 TYPE_B
  (1, 'TYPE_A', 'Item 1-A1', 'TypeA under Owner 1', 2020, 'alpha-1', NULL, NULL, 1),
  (2, 'TYPE_A', 'Item 1-A2', 'TypeA under Owner 1', 2021, 'alpha-2', NULL, NULL, 1),
  (3, 'TYPE_B', 'Item 1-B1', 'TypeB under Owner 1', 2022, NULL, 'beta-1', 100,  1),
  -- Owner 2 has 1 TYPE_A + 2 TYPE_B
  (4, 'TYPE_A', 'Item 2-A1', 'TypeA under Owner 2', 2020, 'alpha-3', NULL, NULL, 2),
  (5, 'TYPE_B', 'Item 2-B1', 'TypeB under Owner 2', 2021, NULL, 'beta-2', 200,  2),
  (6, 'TYPE_B', 'Item 2-B2', 'TypeB under Owner 2', 2022, NULL, 'beta-3', 300,  2),
  -- Owner 3 has 2 TYPE_B
  (7, 'TYPE_B', 'Item 3-B1', 'TypeB under Owner 3', 2021, NULL, 'beta-4', 400,  3),
  (8, 'TYPE_B', 'Item 3-B2', 'TypeB under Owner 3', 2022, NULL, 'beta-5', 500,  3);

ALTER TABLE owner ALTER COLUMN id RESTART WITH 100;
ALTER TABLE item  ALTER COLUMN id RESTART WITH 100;
