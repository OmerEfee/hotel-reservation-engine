# 🏨 Hotel Reservation Engine (Concurrency-Safe)

Spring Boot 3 ve MS SQL Server kullanılarak geliştirilmiş, eşzamanlı rezervasyon isteklerinde çakışmayı (race condition / overbooking) veritabanı seviyesinde engelleyen ve Docker üzerinde çalışan yüksek tutarlılıklı rezervasyon servisi.

---

## 🎯 Mimari ve Çözülen Problem (Mülakat Notları)

### 1. Overbooking & Race Condition Engelleme
Bir otel odası için aynı milisaniyede gelen rezervasyon isteklerinde çift satış riskini önlemek amacıyla **Pessimistic Write Lock (`@Lock(LockModeType.PESSIMISTIC_WRITE)`)** uygulanmıştır.
* `findByIdWithLock(roomId)` çağrıldığında ilgili kayıt veritabanında satır bazlı kilitlenir (`SELECT ... WITH (UPDLOCK, ROWLOCK)`).
* Eşzamanlı gelen ikinci işlem, ilk transaction tamamlanana kadar bekletilir.
* Tarih çakışması matematiksel `existsOverlappingReservation` sorgusu ile kontrol edilir; çakışma durumunda istemciye anlamlı `409 Conflict` fırlatılır.

### 2. Dinamik Fiyatlandırma
Rezervasyon yapılan gün sayısı (`ChronoUnit.DAYS.between`) tespit edilerek taban oda fiyatı üzerinden otomatik toplam tutar hesabı yapılır.

---

## 🛠 Kullanılan Teknolojiler
* **Java 17 / Spring Boot 3**
* **Spring Data JPA & Hibernate**
* **MS SQL Server**
* **Docker & Dockerfile**
* **Lombok & Jakarta Validation**

---

## 🚀 Docker ile Çalıştırma

### 1. Maven ile .jar Üretimi:
```bash
mvn clean package -DskipTests