INSERT INTO report_category(name) VALUES
                                      ( 'Inne'),
                                      ( 'Problemy z logowaniem i rejestracją'),
                                      ( 'Problemy z wydajnością i ładowaniem'),
                                      ( 'Problemy z interfejsem użytkownika'),
                                      ( 'Problemy z funkcjonalnością'),
                                      ( 'Problemy z danymi'),
                                      ( 'Problemy z notyfikacjami i e-mailami'),
                                      ('Problemy z integracjami i API'),
                                      ('Problemy z plikami i załącznikami');

INSERT INTO report (title, description, date_added, due_date, time_to_respond, category_id, status, reporting_user_id, assigned_user_id) VALUES
('Błąd logowania użytkownika', 'Użytkownicy zgłaszają problemy z logowaniem.', '2025-01-10 08:00:00', '2025-01-15 23:59:59', '2025-01-11 08:00:00', 2, 'COMPLETED', 1, 2),
('Problem z wysyłką e-mail', 'Powiadomienia e-mail nie są wysyłane poprawnie.', '2025-01-12 09:30:00', '2025-01-17 23:59:59', '2025-01-14 09:30:00', 7, 'COMPLETED', 2, 5),
('Powolne ładowanie strony', 'Strona ładuje się wolniej niż oczekiwano.', '2025-01-14 11:00:00', '2025-01-19 23:59:59', '2025-01-17 11:00:00', 3, 'COMPLETED', 3, 2),
('Błąd w module rejestracji', 'Nowi użytkownicy nie mogą się zarejestrować.', '2025-01-16 13:15:00', '2025-01-21 23:59:59', '2025-01-17 13:15:00', 2, 'COMPLETED', 4, 5),
('Problem z grafikami', 'Grafiki wczytują się niepoprawnie.', '2025-01-18 15:45:00', '2025-01-23 23:59:59', '2025-01-20 15:45:00', 4, 'COMPLETED', 5, 2),
('Zawieszanie się aplikacji', 'Aplikacja zawiesza się po dłuższym użytkowaniu.', '2025-01-20 18:00:00', '2025-01-25 23:59:59', '2025-01-22 18:00:00', 3, 'COMPLETED', 1, 5),
('Nie działa wyszukiwanie', 'Funkcja wyszukiwania nie zwraca wyników.', '2025-01-22 08:30:00', '2025-01-27 23:59:59', '2025-01-24 08:30:00', 5, 'COMPLETED', 2, 2),
('Problem z eksportem raportów', 'Eksport raportów kończy się błędem.', '2025-01-24 10:15:00', '2025-01-29 23:59:59', '2025-01-26 10:15:00', 5, 'COMPLETED', 3, 5),
('Znikające dane formularza', 'Dane z formularza znikają po przeładowaniu strony.', '2025-01-10 12:00:00', '2025-01-15 23:59:59', '2025-01-13 12:00:00', 6, 'COMPLETED', 4, 2),
('Problem z notyfikacjami', 'Nie działa wysyłka powiadomień do użytkowników.', '2025-01-12 14:30:00', '2025-01-17 23:59:59', '2025-01-15 14:30:00', 7, 'COMPLETED', 5, 5),
('Nie działa integracja z API', 'Zewnętrzne API zwraca błąd przy zapytaniach.', '2025-01-14 08:45:00', '2025-01-19 23:59:59', '2025-01-17 08:45:00', 8, 'COMPLETED', 1, 2),
('Problemy z załącznikami', 'Załączniki nie są poprawnie przesyłane.', '2025-01-16 11:30:00', '2025-01-21 23:59:59', '2025-01-19 11:30:00', 9, 'COMPLETED', 2, 5),
('Nieprawidłowe dane w raporcie', 'Raporty pokazują błędne dane.', '2025-01-29 13:15:00', '2025-02-03 23:59:59', '2025-02-01 13:15:00', 6, 'COMPLETED', 3, 2),
('Nie działa rejestracja użytkowników', 'Użytkownicy zgłaszają problemy z zakładaniem kont.', '2025-01-30 16:00:00', '2025-02-04 23:59:59', '2025-02-02 16:00:00', 2, 'COMPLETED', 4, 5),
('Problem z ładowaniem strony głównej', 'Strona główna wczytuje się bardzo długo.', '2025-01-31 18:30:00', '2025-02-05 23:59:59', '2025-02-03 18:30:00', 3, 'COMPLETED', 5, 2),
('Nieprawidłowe formatowanie dat', 'Daty w systemie nie są poprawnie sformatowane.', '2025-02-01 08:00:00', '2025-02-05 23:59:59', '2025-02-04 08:00:00', 6, 'PENDING', 1, 2),
('Nie działa wgrywanie plików', 'Pliki przesyłane przez użytkowników nie zapisują się.', '2025-02-22 09:45:00', '2025-02-03 23:59:59', '2025-01-28 09:45:00', 9, 'UNDER_REVIEW', 2, 3),
('Problemy z grafiką w dashboardzie', 'Grafiki na dashboardzie nie ładują się poprawnie.', '2025-02-01 12:15:00', '2025-02-06 23:59:59', '2025-02-04 12:15:00', 4, 'UNDER_REVIEW', 3, 4),
('Nie działa panel administratora', 'Administratorzy zgłaszają problemy z dostępem do panelu.', '2025-01-27 14:00:00', '2025-02-05 23:59:59', '2025-01-30 14:00:00', 4, 'PENDING', 4, NULL),
('Niepoprawne obliczenia', 'System zwraca błędne wyniki w module statystyk.', '2025-02-01 16:45:00', '2025-02-04 08:00:00', '2025-01-31 16:45:00', 5, 'UNDER_REVIEW', 5, 1),
('Błąd przy rejestracji produktu', 'Rejestracja nowego produktu kończy się błędem.', '2025-02-02 08:30:00', '2024-02-10 23:59:59', '2025-02-07 08:30:00', 5, 'PENDING', 1, 2),
('Nie działa usuwanie kont', 'Administratorzy nie mogą usuwać kont użytkowników.', '2025-02-01 11:00:00', '2024-12-15 23:59:59', '2025-02-02 11:00:00', 2, 'PENDING', 2, NULL),
('Problem z wyświetlaniem danych', 'Tabela danych w dashboardzie nie wyświetla pełnych wyników.', '2024-12-07 13:30:00', '2024-02-07 23:59:59', '2024-12-10 13:30:00', 4, 'UNDER_REVIEW', 3, 4),
('Problemy z cache', 'Zmiany w systemie nie są widoczne od razu z powodu problemów z cache.', '2025-02-01 15:15:00', '2024-12-14 23:59:59', '2025-02-05 23:59:59', 3, 'PENDING', 4, 5),
('Nie działa import danych', 'Import plików CSV kończy się błędem.', '2025-01-31 17:00:00', '2024-12-15 23:59:59', '2025-02-04 08:00:00', 3, 'PENDING', 5, 1),
('Problem z grafikami w module profilu', 'Zdjęcia profilowe użytkowników nie ładują się poprawnie.', '2024-12-08 09:45:00', '2024-12-13 23:59:59', '2024-12-08 09:45:00', 4, 'UNDER_REVIEW', 1, NULL),
('Nie działa zmiana hasła', 'Użytkownicy zgłaszają problemy z resetowaniem haseł.', '2024-12-08 11:30:00', '2024-12-14 23:59:59', '2024-12-11 11:30:00', 2, 'PENDING', 2, 3),
('Nie działa logowanie przez Google', 'Logowanie za pomocą konta Google kończy się błędem.', '2024-12-08 14:15:00', '2024-12-15 23:59:59', '2024-12-12 14:15:00', 1, 'PENDING', 3, NULL),
('Problemy z edycją danych', 'Użytkownicy nie mogą edytować swoich danych w profilu.', '2024-12-08 16:30:00', '2024-12-14 23:59:59', '2024-12-13 16:30:00', 5, 'UNDER_REVIEW', 4, 5),
('Znikające załączniki', 'Załączniki w formularzach znikają po zapisaniu.', '2024-12-08 18:00:00', '2024-12-15 23:59:59', '2024-12-14 18:00:00', 9, 'PENDING', 5, 1),
('Problem z wylogowywaniem', 'Użytkownicy zgłaszają problemy z wylogowaniem z systemu.', '2024-12-09 08:00:00', '2024-12-12 23:59:59', '2024-12-09 08:00:00', 2, 'UNDER_REVIEW', 1, NULL),
('Nie działa reset hasła', 'System resetu haseł zgłasza błąd przy weryfikacji konta.', '2024-12-09 10:00:00', '2024-12-13 23:59:59', '2024-12-10 10:00:00', 2, 'UNDER_REVIEW', 2, 4),
('Problem z integracją z systemem zewnętrznym', 'Dane z systemu zewnętrznego nie są importowane.', '2024-12-09 12:30:00', '2024-12-14 23:59:59', '2024-12-11 12:30:00', 8, 'PENDING', 3, NULL),
('Nie działa zmiana danych kontaktowych', 'Zmiana danych kontaktowych w profilu kończy się błędem.', '2024-12-09 15:15:00', '2024-12-15 23:59:59', '2024-12-13 15:15:00', 6, 'PENDING', 4, 5),
('Problemy z ładowaniem modułu', 'Moduł statystyk nie ładuje się poprawnie.', '2024-12-12 18:00:00', '2024-12-31 14:25:59', '2024-12-12 18:00:00', 3, 'UNDER_REVIEW', 5, 1),
('Nie działa synchronizacja danych', 'Dane nie synchronizują się pomiędzy modułami.', '2024-12-10 08:45:00', '2024-12-21 23:59:59', '2024-12-13 08:45:00', 5, 'UNDER_REVIEW', 1, NULL),
('Nie działa integracja z płatnościami', 'Płatności nie są autoryzowane przez system.', '2024-12-10 10:30:00', '2024-12-20 23:59:59', '2024-12-18 10:30:00', 5, 'PENDING', 2, 4),
('Nie działa import z Excel', 'Pliki Excel nie są poprawnie importowane.', '2024-12-10 12:15:00', '2024-12-21 23:59:59', '2024-12-19 12:15:00', 5, 'PENDING', 3, 5),
('Błąd w module powiadomień', 'Powiadomienia SMS nie są wysyłane poprawnie.', '2024-12-10 14:30:00', '2024-12-17 13:59:59', '2024-12-14 14:30:00', 7, 'UNDER_REVIEW', 4, NULL),
('Nie działa pobieranie danych historycznych', 'Dane historyczne nie są dostępne.', '2024-12-10 16:15:00', '2024-12-15 23:59:59', '2024-12-14 16:15:00', 6, 'PENDING', 5, 1);
