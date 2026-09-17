# 1. Aşama: Hafif Java Çalışma Ortamı (Alpine Linux)
FROM eclipse-temurin:17-jre-alpine

# Konteyner içindeki çalışma dizini
WORKDIR /app

# Maven ile ürettiğimiz jar dosyasını konteyner içine kopyala
COPY target/reservation-service-0.0.1-SNAPSHOT.jar app.jar

# Uygulamanın dinlediği port
EXPOSE 8080

# Konteyner başlatıldığında çalışacak komut
ENTRYPOINT ["java", "-jar", "app.jar"]