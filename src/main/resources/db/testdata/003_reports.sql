INSERT INTO report (title, description, date_added, due_date, category, status, reporting_user_id, assigned_user_id) VALUES
    ('Problem z wyświetlaniem obrazu', 'Obraz na stronie głównej nie wyświetla się prawidłowo.', '2024-11-10 10:00:00', '2024-11-12 18:00:00', 'CRITICAL', 'COMPLETED', 1, 2),
    ('Błąd wysyłki wiadomości', 'Formularz nie wysyła wiadomości do bazy danych.', '2024-11-09 14:00:00', '2024-11-11 17:30:00', 'MAJOR', 'COMPLETED', 1, 5),
    ('Nieprawidłowe dane w raportach', 'Dane w raportach są niezgodne z rzeczywistością.', '2024-11-08 09:30:00', '2024-11-13 15:45:00', 'MINOR', 'COMPLETED', 4, 2),
    ('Problem z autoryzacją użytkownika', 'Użytkownicy mają problem z autoryzacją przy logowaniu.', '2024-11-07 11:15:00', '2024-11-10 23:59:59', 'CRITICAL', 'COMPLETED', 1, 5),
    ('Błąd renderowania grafiki', 'Grafika na stronie wyświetla się nieprawidłowo po zmianie rozdzielczości.', '2024-11-12 13:30:00', '2024-11-15 19:00:00', 'MAJOR', 'COMPLETED', 4, 2),
    ('Nie można usunąć konta użytkownika', 'System nie pozwala na usunięcie konta użytkownika.', '2024-11-13 16:45:00', '2024-11-17 22:30:00', 'CRITICAL', 'COMPLETED', 1, 2),
    ('Błąd przy generowaniu PDF', 'PDF generuje się z błędami, brak danych.', '2024-11-14 15:20:00', '2024-11-18 14:45:00', 'MAJOR', 'COMPLETED', 1, 5),
    ('Zbyt długi czas odpowiedzi serwera', 'Serwer potrzebuje zbyt wiele czasu na odpowiedź.', '2024-11-15 08:55:00', '2024-11-18 21:15:00', 'MINOR', 'COMPLETED', 4, 2),
    ('Problem z certyfikatem SSL', 'Certyfikat SSL wygasł i strona nie jest bezpieczna.', '2024-11-02 12:00:00', '2024-11-05 20:00:00', 'CRITICAL', 'COMPLETED', 1, 5),
    ('Nie można załadować skryptu', 'Skrypt JavaScript nie ładuje się prawidłowo.', '2024-11-01 10:30:00', '2024-11-04 16:30:00', 'CRITICAL', 'COMPLETED', 1, 2),
    ('Problemy z wyszukiwarką', 'Wyszukiwarka nie zwraca oczekiwanych wyników.', '2024-11-16 14:40:00', '2024-11-19 12:30:00', 'MAJOR', 'COMPLETED', 1, 5),
    ('Brak możliwości zmiany hasła', 'Użytkownicy nie mogą zmienić hasła przez panel użytkownika.', '2024-11-17 09:00:00', '2024-11-21 18:15:00', 'MINOR', 'COMPLETED', 4, 2),
    ('Problemy z migracją danych', 'Dane są niekompletne po migracji.', '2024-11-18 11:50:00', '2024-11-22 22:45:00', 'CRITICAL', 'COMPLETED', 1, 5),
    ('Brak dostępu do serwera', 'Serwer nie odpowiada, brak dostępu przez kilka godzin.', '2024-11-21 10:00:00', '2024-11-27 20:45:00', 'CRITICAL', 'UNDER_REVIEW', 1, 2),
    ('Nie działa moduł płatności', 'Płatności online nie są realizowane poprawnie.', '2024-11-24 15:30:00', '2024-11-28 23:59:59', 'MAJOR', 'UNDER_REVIEW', 1, 5),
    ('Problemy z bezpieczeństwem danych', 'Brak odpowiedniego szyfrowania przy przesyłaniu danych.', '2024-11-21 14:20:00', '2024-11-30 19:30:00', 'MINOR', 'UNDER_REVIEW', 4, 2),
    ('Problem z interfejsem API', 'Interfejs API nie zwraca poprawnych odpowiedzi.', '2024-11-25 12:10:00', '2024-11-29 17:15:00', 'CRITICAL', 'UNDER_REVIEW', 1, 5);
