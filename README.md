# loginUtc2 — Selenium kiểm thử đăng nhập UTC

Dự án Java dùng Selenium để kiểm thử trang [Văn phòng điện tử UTC](https://vanphongdientu.utc.edu.vn/Login). Bộ test có **29 ca Selenium** và **1 test Spring Boot**. Các ca gửi form dùng dữ liệu giả để kiểm tra đăng nhập thất bại; không cần tài khoản hoặc mật khẩu thật.

## 1. Chuẩn bị môi trường

- **JDK 17**: đặt `JAVA_HOME` tới thư mục JDK và thêm thư mục `bin` của JDK vào `PATH`.
- **Google Chrome** đã cài trên máy.
- **Git** để clone dự án.
- Kết nối Internet để tải dependency, ChromeDriver và truy cập trang UTC.

Dự án có Gradle Wrapper **9.7.1**, không cần cài Gradle riêng. Selenium Manager tự quản lý ChromeDriver phù hợp với Chrome. Các thư viện chính gồm Spring Boot **4.1.1**, Selenium **4.50.0**, JUnit Jupiter và AssertJ.

Kiểm tra Java, clone và mở thư mục dự án trong PowerShell:

```powershell
java -version
git clone https://github.com/PhamDucAnh6451071001/loginUtc2.git
cd loginUtc2
.\gradlew.bat --version
```

Chạy các lệnh bên dưới tại thư mục chứa `gradlew.bat` và `build.gradle`.

## 2. Chạy Selenium và xem cửa sổ Chrome

```powershell
.\gradlew.bat e2eTest
```

Mặc định Chrome **hiện cửa sổ**. Selenium tự mở trang, nhập dữ liệu, thao tác form và kiểm tra kết quả. Mỗi test có phiên Chrome riêng; cửa sổ tự đóng sau khi test kết thúc. Vì vậy cửa sổ có thể mở và đóng liên tục khi chạy nhiều ca.

Task `e2eTest` chạy cả 29 ca và luôn thực thi lại. Để kết luận lượt chạy thành công, kiểm tra cuối log có `BUILD SUCCESSFUL` và không có test thất bại hoặc bị bỏ qua.

### Chạy không hiện cửa sổ (headless)

```powershell
.\gradlew.bat e2eTest -Pheadless=true
```

### Chạy một test case

```powershell
# TC05: thông tin đăng nhập sai bị từ chối
.\gradlew.bat e2eTest --tests '*UtcLoginE2ETest.tc05_*'

# TC10: username có khoảng trắng ở giữa
.\gradlew.bat e2eTest --tests '*UtcNegativeLoginE2ETest.tc10_*'
```
## 3. Build và chạy test Spring Boot

```powershell
# Chạy test Spring Boot, không mở Chrome hoặc truy cập UTC
.\gradlew.bat test

# Build JAR và chạy test Spring Boot
.\gradlew.bat build

# Build và chạy toàn bộ test, gồm cả Selenium
.\gradlew.bat build e2eTest
```

`test` và `build` không tự chạy các ca Selenium. Muốn kiểm thử bằng trình duyệt, cần gọi task `e2eTest`. Chạy class `LoginUtc2Application` hoặc `bootRun` chỉ khởi động ứng dụng Spring Boot, không khởi động bộ test Selenium.

File JAR được tạo trong `build/libs/`.

### Linux/macOS và môi trường CI

```bash
bash ./gradlew build --no-daemon
bash ./gradlew e2eTest -Pheadless=true -PtimeoutSeconds=30 --no-daemon
```

## 4. Xem báo cáo và ảnh lỗi

| Nội dung | Đường dẫn sau khi chạy |
| --- | --- |
| Báo cáo HTML Selenium | `build/reports/tests/e2eTest/index.html` |
| Báo cáo HTML test Spring Boot | `build/reports/tests/test/index.html` |
| Kết quả XML Selenium | `build/test-results/e2eTest/TEST-*.xml` |
| Kết quả XML test Spring Boot | `build/test-results/test/TEST-*.xml` |
| Ảnh chụp khi test lỗi | `build/screenshots/` |

Mở báo cáo Selenium trên Windows:

```powershell
Start-Process .\build\reports\tests\e2eTest\index.html
```

Ảnh lỗi được chụp trước khi đóng Chrome. Khi xem lại kết quả, đối chiếu thời gian tạo báo cáo với lượt chạy vừa thực hiện; một lượt chạy bị ngắt có thể chưa tạo báo cáo mới. Thư mục `build/` không được commit.

## 5. GitHub Actions

Workflow: [`.github/workflows/ci.yml`](.github/workflows/ci.yml).

Pipeline tự chạy khi **push**, khi có **pull request**, hoặc khi chọn **Run workflow** trong tab [Actions](https://github.com/PhamDucAnh6451071001/loginUtc2/actions).

1. **Build and unit tests**: dùng Java 17 và Gradle Wrapper trên Ubuntu 24.04 để build, chạy test Spring Boot và lưu JAR/báo cáo.
2. **Selenium against UTC**: chạy sau khi build thành công, dùng Chrome headless để chạy 29 ca trên trang UTC.

Lệnh Selenium trong CI:

```bash
bash ./gradlew e2eTest -Pheadless=true -PtimeoutSeconds=30 --no-daemon --console=plain
```

Mở một lượt chạy trong tab Actions để xem log từng job. Phần **Artifacts** cung cấp:

| Artifact | Nội dung |
| --- | --- |
| `loginUtc2-jar` | File JAR của ứng dụng |
| `unit-test-reports` | Báo cáo HTML/XML test Spring Boot |
| `selenium-reports` | Báo cáo HTML/XML Selenium và ảnh lỗi nếu có |

Artifact được lưu **14 ngày**. Báo cáo được upload cả khi bước test thất bại. Pipeline hiện build, test và lưu artifact; chưa cấu hình triển khai lên server.

## 6. Cấu trúc bộ test

```text
src/test/java/com/example/loginutc2/
├── LoginUtc2ApplicationTests.java
└── e2e/
    ├── base/
    │   ├── BaseTest.java
    │   ├── ScreenshotWatcher.java
    │   └── BrowserNetwork.java
    ├── pages/
    │   ├── BasePage.java
    │   ├── LoginPage.java
    │   └── PasswordRecoveryPage.java
    └── tests/
        ├── UtcLoginE2ETest.java          # TC01–TC09
        └── UtcNegativeLoginE2ETest.java  # TC10–TC29
```

- `BaseTest` tạo và đóng Chrome cho từng test.
- Page Object giữ locator và thao tác; assertion nằm trong test class.
- `ScreenshotWatcher` lưu ảnh khi test thất bại.
- `BrowserNetwork` quan sát request/response cho TC27–TC29 trong bộ nhớ.

TC01–TC09 kiểm tra form, dữ liệu thiếu/sai, checkbox, trang quên mật khẩu và thao tác tab. TC10–TC29 bổ sung biến thể nhập liệu sai, gửi form bằng Enter, ghi nhớ đăng nhập, HTTPS và một số dấu hiệu lộ dữ liệu trong phản hồi/URL.

Các ca chuỗi dài kiểm tra cách xử lý dữ liệu dài, không khẳng định giới hạn tài khoản. Các ca kiểm tra lỗi/URL chỉ bao phủ dấu hiệu được định nghĩa trong test, không thay thế kiểm toán bảo mật toàn bộ. CAPTCHA, đăng nhập thành công, API cần quyền truy cập và giới hạn số lần thử chưa nằm trong bộ test này.

## 7. Xử lý lỗi thường gặp

| Hiện tượng | Cách kiểm tra |
| --- | --- |
| Không thấy Chrome | Dùng `e2eTest`; kiểm tra có đang truyền `-Pheadless=true` hay không. Task `test`, `build` hoặc chạy ứng dụng không tự mở Chrome. |
| Không tìm thấy Java/toolchain 17 | Kiểm tra `java -version`, `JAVA_HOME` và JDK 17 đã cài. Mở lại terminal sau khi sửa biến môi trường. |
| Không tạo được phiên Chrome | Kiểm tra Chrome đã cài và mạng cho phép Selenium Manager tải ChromeDriver. |
| `ERR_NAME_NOT_RESOLVED`, `ERR_NETWORK_CHANGED` hoặc timeout khi mở trang | Mở trang UTC thủ công để kiểm tra DNS/kết nối. Đọc log và ảnh lỗi trước khi kết luận lỗi do dữ liệu kiểm thử. |
| Timeout chờ phần tử | Kiểm tra trang đã tải đúng form; có thể dùng `-PtimeoutSeconds=30` nếu phần tử tải chậm. Tham số này không sửa lỗi mạng hay locator đã thay đổi. |

## 8. Quy ước commit

Mỗi test case được thêm bằng một commit riêng, sau khi chạy và đọc kết quả của chính ca đó. Commit cấu hình và tài liệu dùng commit riêng.

`README.md` được theo dõi trong Git; các ghi chú Markdown khác, `.env`, cache và kết quả build vẫn được ignore.
