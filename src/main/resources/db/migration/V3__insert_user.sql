INSERT INTO user (
    id,
    created_at,
    active,
    role,
    login,
    password_hash
)
SELECT
    UUID(),
    NOW(),
    TRUE,
    'ADMIN',
    'admin',
    '$2a$10$aBwF.3JfmPuYMzu1n0lCEOvBPqzx09M9pIs9mbHcojeBeLlmobvkK'
FROM dual
WHERE NOT EXISTS (
    SELECT 1 FROM user WHERE login = 'admin'
);
