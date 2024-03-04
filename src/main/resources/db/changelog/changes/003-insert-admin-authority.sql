INSERT INTO user_authorities (user_id, authority_name)
VALUES ((SELECT id FROM client WHERE username = 'admin'), 'ADMIN');