# REST API Otomatik Regresyon Test Projesi

> **JSONPlaceholder API** için **Rest Assured**, **JUnit 5** ve **Maven** ile hazırlanmış otomatik regresyon test projesi.

---

## 📋 İçindekiler

- [Proje Hakkında](#proje-hakkında)
- [Kullanılan Teknolojiler](#kullanılan-teknolojiler)
- [Proje Yapısı](#proje-yapısı)
- [Test Senaryoları](#test-senaryoları)
- [Kurulum ve Çalıştırma](#kurulum-ve-çalıştırma)
- [Test Tasarım Kararları](#test-tasarım-kararları)

---

## Proje Hakkında

Bu proje, **Yazılım Test Mühendisliği** dersi kapsamında hazırlanmış bir otomatik API regresyon test projesidir. Test edilen servis, ücretsiz ve herkese açık olan [JSONPlaceholder](https://jsonplaceholder.typicode.com/) REST API'sidir.

### Test Edilen API Endpoint'leri

| Endpoint        | Metot | Açıklama                          |
|-----------------|-------|-----------------------------------|
| `/posts`        | GET   | Tüm gönderileri listele           |
| `/posts/{id}`   | GET   | ID'ye göre gönderi getir          |
| `/posts`        | POST  | Yeni gönderi oluştur (JSON body)  |
| `/users`        | GET   | Tüm kullanıcıları listele         |
| `/users/{id}`   | GET   | ID'ye göre kullanıcı getir        |
| `/comments`     | GET   | Tüm yorumları listele             |
| `/comments`     | GET   | `?postId=X` ile filtreli yorumlar |

---

## Kullanılan Teknolojiler

| Teknoloji       | Versiyon | Amaç                             |
|-----------------|----------|----------------------------------|
| Java            | 11+      | Programlama dili                 |
| Maven           | 3.8+     | Bağımlılık yönetimi & build      |
| Rest Assured    | 5.4.0    | API test kütüphanesi             |
| JUnit 5         | 5.10.2   | Test çerçevesi                   |
| Jackson         | 2.17.0   | JSON serileştirme/deserileştirme |
| AssertJ         | 3.25.3   | Okunabilir assertion'lar         |

---

## Proje Yapısı

```
rest-assured-regression/
├── pom.xml                          # Maven bağımlılıkları
└── src/
    └── test/
        └── java/
            └── com/testproject/
                ├── base/
                │   └── BaseTest.java        # Ortak Rest Assured ayarları
                ├── config/
                │   └── TestConfig.java      # URL ve süre sabitleri
                ├── model/
                │   └── Post.java            # Request/Response POJO
                └── tests/
                    ├── PostApiTest.java      # /posts endpoint testleri
                    ├── UserApiTest.java      # /users endpoint testleri
                    └── CommentApiTest.java   # /comments endpoint testleri
```

---

## Test Senaryoları

### ✅ Her Test İçin Ortak Kontroller (BaseTest üzerinden)
- **Yanıt süresi**: 3000 ms altında olmalı
- **Content-Type**: `application/json` olmalı

### PostApiTest (6 test)
| # | Senaryo                            | Metot | Tip       |
|---|------------------------------------|-------|-----------|
| 1 | Tüm postlar gelir, liste dolu      | GET   | Pozitif   |
| 2 | ID=1 için doğru alanlar döner      | GET   | Pozitif   |
| 3 | Farklı ID'ler için 200 döner       | GET   | Parametrik|
| 4 | Var olmayan ID için 404 döner      | GET   | Negatif   |
| 5 | Yeni post oluşturur, 201 + ID alır | POST  | Pozitif   |
| 6 | POST yanıtındaki alanlar doğrudur  | POST  | Pozitif   |

### UserApiTest (5 test)
| # | Senaryo                               | Metot | Tip     |
|---|---------------------------------------|-------|---------|
| 1 | 10 kullanıcı döner                    | GET   | Pozitif |
| 2 | Email adresleri benzersizdir          | GET   | Pozitif |
| 3 | İç içe adres/geo nesnesi doğrulanır   | GET   | Pozitif |
| 4 | Zorunlu alanlar mevcuttur             | GET   | Pozitif |
| 5 | Var olmayan kullanıcı 404 döner       | GET   | Negatif |

### CommentApiTest (4 test)
| # | Senaryo                                  | Metot | Tip     |
|---|------------------------------------------|-------|---------|
| 1 | Yorum listesi boş değildir               | GET   | Pozitif |
| 2 | Query param ile filtreleme çalışır       | GET   | Pozitif |
| 3 | Tüm email'ler '@' içerir                 | GET   | Pozitif |
| 4 | Zorunlu alanlar (id, postId, body) mevcut| GET   | Pozitif |

---

## Kurulum ve Çalıştırma

### Gereksinimler
- Java 11 veya üzeri
- Maven 3.8 veya üzeri
- İnternet bağlantısı (JSONPlaceholder API'ye erişim için)

### Tüm testleri çalıştır
```bash
mvn test
```

### Belirli bir test sınıfını çalıştır
```bash
mvn test -Dtest=PostApiTest
mvn test -Dtest=UserApiTest
mvn test -Dtest=CommentApiTest
```

### Belirli bir test metodunu çalıştır
```bash
mvn test -Dtest="PostApiTest#createPost_shouldReturn201AndReturnCreatedPost"
```

---

## Test Tasarım Kararları

### 1. BaseTest ile Merkezi Konfigürasyon
`BaseTest` sınıfı, tüm test sınıflarının ortak ihtiyaçlarını (base URL, log filtreleri, yanıt süresi kontrolü) tek bir yerde yönetir. Bu sayede:
- Kod tekrarı önlenir (DRY prensibi)
- Base URL değiştiğinde tek bir yer güncellenir

### 2. TestConfig ile Sabit Yönetimi
URL ve zaman aşımı değerleri `TestConfig` sınıfında sabitler olarak tutulur. Magic string / magic number kullanımından kaçınılır.

### 3. POJO Modeli
`Post.java` sınıfı, Jackson ile serialize/deserialize işlemlerini kolaylaştırır. `@JsonIgnoreProperties(ignoreUnknown = true)` ile API'nin yeni alan eklemesi testleri bozmaz.

### 4. Pozitif + Negatif Test
Her endpoint için hem başarı (2xx) hem başarısızlık (4xx) senaryoları test edilir.

### 5. Okunabilirlik
- `@DisplayName` ile test isimleri Türkçe ve açıklayıcı yazılır
- `@MethodOrderer` ile testler mantıksal sırada çalışır
- AssertJ ile assertion'lar doğal dil gibi okunur

---

## 📊 Örnek Test Çıktısı

```
[INFO] Tests run: 15, Failures: 0, Errors: 0, Skipped: 0
[INFO]
[INFO] -------------------------------------------------------
[INFO]  T E S T S
[INFO] -------------------------------------------------------
[INFO] Running com.testproject.tests.CommentApiTest
[INFO] Tests run: 4, Failures: 0, Errors: 0, Skipped: 0
[INFO] Running com.testproject.tests.PostApiTest
[INFO] Tests run: 6, Failures: 0, Errors: 0, Skipped: 0  (includes 5 parametric runs)
[INFO] Running com.testproject.tests.UserApiTest
[INFO] Tests run: 5, Failures: 0, Errors: 0, Skipped: 0
[INFO] BUILD SUCCESS
```
