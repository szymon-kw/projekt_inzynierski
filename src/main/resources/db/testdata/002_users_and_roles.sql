INSERT INTO users (first_name, last_name, email, password, company_id) VALUES
 ('Jan', 'Kowalski', 'jan.kowalski@example.com', 'password123', 1),
 ('Anna', 'Nowak', 'anna.nowak@example.com', 'qwerty1234', 2),
 ('Piotr', 'Wiśniewski', 'admin@example.com', 'strongPass789', 3),  /*ADMIN*/
 ('Katarzyna', 'Wójcik', 'katarzyna.wojcik@example.com', 'safePass456', 4),
 ('Michał', 'Zieliński', 'michal.zielinski@example.com', 'passMZ890', 5);



INSERT INTO user_role (name, description) VALUES
    ('USER', 'Zwykły użytkownik z podstawowymi uprawnieniami'),
    ('ADMINISTRATOR', 'Użytkownik z uprawnieniami administracyjnymi, pełny dostęp do wszystkich funkcji i ustawień'),
    ('HANDLER', 'Użytkownik odpowiedzialny za obsługę zgłoszonych błędów (przypisanych przez administratora)');

INSERT INTO
    user_roles (user_id, role_id)
VALUES
    (1, 1),
    (2, 1),
    (3, 2),
    (4,1),
    (5,3);