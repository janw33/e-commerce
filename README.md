# E-commerce

Projekt edukacyjny backendu platformy e-commerce w Javie i Spring Boot.

## Lokalne przechowywanie zdjęć

Aplikacja zapisuje zdjęcia jako pliki PNG w katalogu ustawionym przez `app.images.upload-dir`. Domyślna ścieżka to `~/ecommerce-data/uploads`. PostgreSQL przechowuje w tabeli `images` adres zdjęcia, jego pozycję i powiązanie z ogłoszeniem.

### Sprzątanie po błędach

- **Błąd zapisu PNG.** Aplikacja zamyka strumień i próbuje usunąć niedokończony plik. Jeśli próba usunięcia również się nie uda, wyjątek z usuwania zostaje dołączony do błędu zapisu jako `Suppressed`.
- **Wycofanie transakcji tworzenia ogłoszenia.** Po potwierdzonym rollbacku aplikacja automatycznie próbuje usunąć wszystkie pliki poprawnie zapisane w ramach tego żądania. Jeśli któregoś pliku nie uda się usunąć, aplikacja zapisuje ostrzeżenie `Could not delete image … after rollback` z nazwą pliku i szczegółami błędu. Aplikacja nadal próbuje usunąć pozostałe pliki.

Nagłe zatrzymanie aplikacji może przerwać sprzątanie przed zapisaniem ostrzeżenia. W takim przypadku albo po nieudanym usunięciu na dysku może zostać plik bez powiązanego rekordu w bazie. Dlatego przy sprawdzaniu spójności porównuj nazwy plików z odpowiadającymi im adresami w `images.url`. Sama liczba plików i rekordów może się różnić.

Plik bez rekordu w bazie nie pojawi się na liście zdjęć ogłoszenia. Po zakończeniu obsługi żądania i potwierdzeniu, że żaden rekord nie wskazuje na ten plik, można go usunąć ręcznie. To przyjęte ograniczenie lokalnego środowiska edukacyjnego.
