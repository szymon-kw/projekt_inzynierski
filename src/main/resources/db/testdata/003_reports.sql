INSERT INTO report (title, description, date_added, due_date, category, status, reporting_user_id) VALUES
        ('Błąd ładowania strony', 'Strona nie ładuje się poprawnie po kliknięciu przycisku.', '2024-10-17 09:00:00', '2024-10-20 23:59:59', 'CRITICAL', 'OPEN', 1),
        ('Nie działa formularz kontaktowy', 'Formularz nie wysyła wiadomości po wypełnieniu i kliknięciu "Wyślij".', '2024-10-16 11:30:00', '2024-10-19 23:59:59', 'MAJOR', 'IN_PROGRESS', 2),
        ('Powolne ładowanie strony', 'Strona ładuje się bardzo wolno w przeglądarkach mobilnych.', '2024-10-15 08:45:00', '2024-10-18 23:59:59', 'MINOR', 'RESOLVED', 5),
        ('Błąd podczas logowania', 'Użytkownicy nie mogą zalogować się na swoje konta.', '2024-10-14 12:00:00', '2024-10-17 23:59:59', 'CRITICAL', 'OPEN', 1),
        ('Zawieszanie się aplikacji', 'Aplikacja zawiesza się przy próbie otwarcia dużego pliku.', '2024-10-13 10:30:00', '2024-10-16 23:59:59', 'MAJOR', 'CLOSED', 4);
