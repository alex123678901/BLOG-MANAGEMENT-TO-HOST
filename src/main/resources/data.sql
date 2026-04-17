-- Initial Roles
INSERT IGNORE INTO roles (id, name) VALUES (1, 'ROLE_ADMIN');
INSERT IGNORE INTO roles (id, name) VALUES (2, 'ROLE_AUTHOR');
INSERT IGNORE INTO roles (id, name) VALUES (3, 'ROLE_READER');

-- Initial Categories
INSERT IGNORE INTO categories (id, name, description) VALUES (1, 'Technology', 'Everything about tech and gadgets');
INSERT IGNORE INTO categories (id, name, description) VALUES (2, 'Lifestyle', 'Daily life, health, and wellness');
INSERT IGNORE INTO categories (id, name, description) VALUES (3, 'Travel', 'Stories from around the world');
INSERT IGNORE INTO categories (id, name, description) VALUES (4, 'Education', 'Learning materials and tutorials');
