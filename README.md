# REST Assured Regresyon Test Projesi

Yazılım Test Mühendisliği ödevi kapsamında hazırlanmış otomatik API regresyon test projesi.

## 🛠 Teknolojiler

| Araç | Versiyon | Amaç |
|------|----------|-------|
| Java | 11+ | Programlama dili |
| Maven | 3.8+ | Proje yönetimi & bağımlılıklar |
| Rest Assured | 5.4.0 | API test kütüphanesi |
| JUnit 5 | 5.10.2 | Test çatısı |
| Hamcrest | 2.2 | Assertion kütüphanesi |

## 🎯 Test Hedefi

[JSONPlaceholder](https://jsonplaceholder.typicode.com) — ücretsiz ve herkese açık sahte REST API.

## 📁 Proje Yapısı

```
main/
├── pom.xml
└── src/
    └── test/
        └── java/
            └── tests/
                ├── BaseTest.java      # Ortak yapılandırma
                ├── GetTests.java      # 3 GET testi
                └── PostTests.java     # 3 POST testi
```

## ✅ Test Listesi

### GET Testleri (`GetTests.java`)
| # | Endpoint | Ne test edilir? |
|---|----------|-----------------|
| 1 | `GET /posts/1` | Tekil gönderi id, userId, title, body kontrolü |
| 2 | `GET /posts` | Listenin 100 eleman içerdiği; ilk ve son id kontrolü |
| 3 | `GET /users/1/todos` | userId=1 olan todo'lar; tamamlananların varlığı |

### POST Testleri (`PostTests.java`)
| # | Endpoint | Ne test edilir? |
|---|----------|-----------------|
| 1 | `POST /posts` | 201 dönüşü; title, body, userId eşleşmesi |
| 2 | `POST /comments` | 201 dönüşü; postId, email, name kontrolü + id > 0 |
| 3 | `POST /todos` | 201 dönüşü; completed=false; userId eşleşmesi |

### Her testte uygulanan 3 temel kontrol
- ✔️ **Status code** doğrulaması
- ✔️ **Response body** değer doğrulaması  
- ✔️ **Yanıt süresi** < 3000 ms kontrolü

## 🚀 Çalıştırma

```bash
# Tüm testleri çalıştır
mvn test

# Belirli bir test sınıfını çalıştır
mvn test -Dtest=GetTests
mvn test -Dtest=PostTests
```

## 📊 Test Raporu

Testler çalıştırıldıktan sonra rapor şu konumda oluşur:
```
target/surefire-reports/
```
