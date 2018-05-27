INSERT INTO roles (name) SELECT 'ROLE_BOSS'
WHERE
    NOT EXISTS (
        SELECT id FROM roles WHERE id = 1
    );

INSERT INTO roles (name) SELECT 'ROLE_WORKER'
WHERE
    NOT EXISTS (
        SELECT id FROM roles WHERE id = 2
    );