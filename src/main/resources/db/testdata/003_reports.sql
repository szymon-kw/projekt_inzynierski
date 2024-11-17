INSERT INTO report (title, description, date_added, due_date, time_to_respond, category, status, reporting_user_id, assigned_user_id) VALUES
        ('Błąd ładowania strony', 'Strona nie ładuje się poprawnie po kliknięciu przycisku.', '2024-10-17 09:00:00', '2024-11-12 23:45:59', '2024-10-17 11:00:00', 'CRITICAL', 'PENDING', 1, 2),
        ('Nie działa formularz kontaktowy', 'Formularz nie wysyła wiadomości po wypełnieniu i kliknięciu "Wyślij".', '2024-10-16 11:30:00', '2024-11-13 23:15:59', '2024-10-16 11:45:00', 'MAJOR', 'UNDER_REVIEW', 1, 5),
        ('Powolne ładowanie strony', 'Strona ładuje się bardzo wolno w przeglądarkach mobilnych.', '2024-10-15 08:45:00', '2024-11-10 23:35:59', '2024-10-15 13:00:00', 'MINOR', 'UNDER_REVIEW', 4, 2),
        ('Błąd podczas logowania', 'Użytkownicy nie mogą zalogować się na swoje konta.', '2024-10-14 12:00:00', '2024-11-14 23:25:59','2024-10-14 12:35:00', 'CRITICAL', 'COMPLETED', 1, 5),
        ('Zawieszanie się aplikacji', 'Aplikacja zawiesza się przy próbie otwarcia dużego pliku.', '2024-10-13 10:30:00', '2024-11-16 23:59:59','2024-10-13 12:30:00', 'MAJOR', 'PENDING', 4, 2);
