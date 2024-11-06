INSERT INTO users (first_name, last_name, email, password, company_id) VALUES
    /* password123 */
 ('Jan', 'Kowalski', 'jan.kowalski@example.com', '$2a$10$w6B2r20Uu1KuHQN9/whOqOAjHBBSxzCjlK3Lih.2WHOgtLlHds36C', 1),
    /* qwerty1234 */
 ('Anna', 'Nowak', 'anna.nowak@example.com', '$2a$10$9HH2vpf3mCZUQkxw4mZ.UuD23ndJ39lmaovU4NqvOKZ7SlmK9hwJG', 2),
 /* strongPass789 */
 ('Piotr', 'Wiśniewski', 'admin@example.com', '$2a$10$ilPA7CzP.LBXMD0PHYc7cOY6r/WuQSP9oBz6u9C9yMvyhmzLB6YGm', 3),
    /* safePass456 */
 ('Katarzyna', 'Wójcik', 'katarzyna.wojcik@example.com', '$2a$10$L6D8jFwUZmXTIV/ifCmnu.KQ7pElFKLdpvVU/POV2o4mFj/B3x9bi', 4),
    /* passMZ890 */
 ('Michał', 'Zieliński', 'michal.zielinski@example.com', '$2a$10$.fJPS50Y98O8pIoSFHWxs.M7sAw7SOF4tcghhJi1awDVnr8A3pUci', 5);



INSERT INTO user_role (name, description) VALUES
    ('USER', 'Zwykły użytkownik z podstawowymi uprawnieniami'),
    ('ADMINISTRATOR', 'Użytkownik z uprawnieniami administracyjnymi, pełny dostęp do wszystkich funkcji i ustawień'),
    ('EMPLOYEE', 'Użytkownik z uprawnieniami pracowniczymi do obsługi zdarzeń');
INSERT INTO
    user_roles (user_id, role_id)
VALUES
    (1, 1),
    (2, 3),
    (3, 2),
    (4,1),
    (5,3);
