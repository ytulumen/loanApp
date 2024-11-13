INSERT INTO roles (id, authority) VALUES (1, 'ROLE_ADMIN');
INSERT INTO roles (id, authority) VALUES (2, 'ROLE_CUSTOMER');

INSERT INTO customer(id, name, surname, credit_limit, used_credit_limit) VALUES (1, 'Angela', 'Harmon', 110, 0);
INSERT INTO customer(id, name, surname, credit_limit, used_credit_limit) VALUES (2, 'Noemi', 'Nichols', 220, 200);
INSERT INTO customer(id, name, surname, credit_limit, used_credit_limit) VALUES (3, 'Navy', 'Acevedo', 50, 45);

INSERT INTO users_table (id, username, role_id, password) VALUES (4, 'admin', 1, '$2a$12$KKwnS2mCEGG6h84SP46/quKYJ6.oxims53WTN2e/7iZcL69BcgTTC');
-- password is 123
INSERT INTO users_table (id, username, role_id, customer_id, password) VALUES (1, 'Angela.Harmon', 2, 1, '$2a$12$KKwnS2mCEGG6h84SP46/quKYJ6.oxims53WTN2e/7iZcL69BcgTTC');
-- password is 123
INSERT INTO users_table (id, username, role_id, customer_id, password) VALUES (2, 'Noemi.Nichols', 2, 2, '$2a$12$KKwnS2mCEGG6h84SP46/quKYJ6.oxims53WTN2e/7iZcL69BcgTTC');
-- password is 123
INSERT INTO users_table (id, username, role_id, customer_id, password) VALUES (3, 'Navy.Acevedo', 2, 3, '$2a$12$KKwnS2mCEGG6h84SP46/quKYJ6.oxims53WTN2e/7iZcL69BcgTTC');
-- password is 123
