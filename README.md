# E-commerce

Projekt edukacyjny backendu platformy e-commerce w Javie i Spring Boot.

## Lokalne przechowywanie zdjęć

Aplikacja zapisuje zdjęcia jako pliki PNG w katalogu ustawionym przez `app.images.upload-dir`. Domyślna ścieżka to `~/ecommerce-data/uploads`. PostgreSQL przechowuje w tabeli `images` adres zdjęcia, jego pozycję i powiązanie z ogłoszeniem.

### Sprzątanie po błędach

- **Błąd zapisu PNG.** Aplikacja zamyka strumień i próbuje usunąć niedokończony plik. Jeśli próba usunięcia również się nie uda, wyjątek z usuwania zostaje dołączony do błędu zapisu jako `Suppressed`.
- **Wycofanie transakcji tworzenia ogłoszenia.** Po potwierdzonym rollbacku aplikacja automatycznie próbuje usunąć wszystkie pliki poprawnie zapisane w ramach tego żądania. Jeśli któregoś pliku nie uda się usunąć, aplikacja zapisuje ostrzeżenie `Could not delete image … after rollback` z nazwą pliku i szczegółami błędu. Aplikacja nadal próbuje usunąć pozostałe pliki.

Nagłe zatrzymanie aplikacji może przerwać sprzątanie przed zapisaniem ostrzeżenia. W takim przypadku albo po nieudanym usunięciu na dysku może zostać plik bez powiązanego rekordu w bazie. Dlatego przy sprawdzaniu spójności porównuj nazwy plików z odpowiadającymi im adresami w `images.url`. Sama liczba plików i rekordów może się różnić.

Plik bez rekordu w bazie nie pojawi się na liście zdjęć ogłoszenia. Po zakończeniu obsługi żądania i potwierdzeniu, że żaden rekord nie wskazuje na ten plik, można go usunąć ręcznie. To przyjęte ograniczenie lokalnego środowiska edukacyjnego.

## Odpowiedzi po zapisie produktu

`POST /api/v1/products` zwraca `201 Created`, a `PUT /api/v1/products/{id}` zwraca `200 OK`. Oba endpointy zwracają pełny `ProductResponse`, w tym adresy zdjęć i liczbę polubień.

To świadomy kontrakt z przyszłym frontendem. Po zapisie klient może odświeżyć widok na podstawie odpowiedzi, bez dodatkowego `GET`. Przy edycji samych danych produktu zbudowanie odpowiedzi wymaga odczytania metadanych galerii i policzenia polubień. Zdjęcia są reprezentowane w JSON-ie przez identyfikatory i adresy URL.
