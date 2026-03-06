## Hướng dẫn chạy dự án Language Center Management System

File này dùng để nhắc lại cách chạy nhanh toàn bộ hệ thống (server REST API + client Swing) sau này.

---

## 1. Chuẩn bị môi trường

- **Yêu cầu chung**:
  - Đã cài **JDK 21** (hoặc tối thiểu 17, nhưng nên giữ đúng bản 21 nếu có thể).
  - Đã cài **Maven 3.6+** (có lệnh `mvn` trong terminal).
  - Đã cài và cấu hình **MySQL** (hoặc dùng đúng thông tin kết nối đã cấu hình sẵn trong `application.properties`).

- **Kiểm tra nhanh**:

```bash
java -version
mvn -version
```

---

## 2. Cấu hình database (chỉ cần làm/lặp lại khi đổi máy hoặc đổi DB)

1. Mở file cấu hình server:
   - `server/nhom2/src/main/resources/application.properties`

2. Kiểm tra/cập nhật các dòng:

```properties
spring.datasource.url=jdbc:mysql://.../LanguageCenterDB?useSSL=true&serverTimezone=UTC
spring.datasource.username=...
spring.datasource.password=...
```

3. Đảm bảo MySQL đang chạy và database/tables đã được tạo (bằng script `init_database.sql` hoặc DB online đã sẵn).

---

## 3. Chạy server (REST API Spring Boot)

1. Mở terminal và chuyển vào thư mục server:

```bash
cd /home/huy/Code/LTTT/MID_TERM_PROJECT/NNTT_GIUAKI/server/nhom2
```

2. Build project (chỉ cần làm lại khi mới clone, mới đổi code, hoặc sau khi đổi dependency):

```bash
mvn clean install
```

3. Chạy server:

```bash
mvn spring-boot:run
```

4. Kiểm tra server đã chạy thành công:
   - Trong log thấy dòng kiểu: `Started App in ... seconds`.
   - Không có lỗi đỏ dừng chương trình.
   - Mặc định server chạy ở: `http://localhost:8080`.

---

## 4. Kiểm tra nhanh API server

Khi server đang chạy, mở **terminal mới** (KHÔNG tắt terminal đang chạy server):

- Lấy danh sách học viên:

```bash
curl http://localhost:8080/api/students
```

- Nếu trả về JSON (danh sách hoặc `[]`) là server hoạt động bình thường.

Có thể thử thêm một số endpoint khác tương tự (xem `README.md` và `API_DOCUMENTATION.md` trong `server/nhom2` để xem đầy đủ).

---

## 5. Chạy ứng dụng client (Java Swing)

> Lưu ý: **Phải để server chạy trước**, rồi mới chạy client.

1. Mở một terminal mới, chuyển vào thư mục client:

```bash
cd /home/huy/Code/LTTT/MID_TERM_PROJECT/NNTT_GIUAKI/client/nhom2clientapp
```

2. Build client (làm lại khi mới clone hoặc đổi code):

```bash
mvn clean install
```

3. Chạy ứng dụng Swing:

- Cách 1 (khuyến khích): Chạy từ IDE (IntelliJ/Eclipse/NetBeans)
  - Mở project `nhom2clientapp` trong IDE.
  - Tìm class: `client_ttnn.hcmute.App`.
  - Chạy phương thức `main` của class này (`Run 'App.main()'`).

- Cách 2 (nếu `pom.xml` có cấu hình plugin exec):

```bash
mvn exec:java -Dexec.mainClass="client_ttnn.hcmute.App"
```

4. Khi cửa sổ ứng dụng mở:
   - Nhấn **“Làm mới”** để tải danh sách sinh viên từ server.
   - Dùng các nút **“Thêm mới”**, **“Cập nhật”**, **“Xóa”** để thao tác và kiểm tra API `students`.

---

## 6. Một số lỗi thường gặp & cách xử lý nhanh

- **Không kết nối được đến database**:
  - Kiểm tra MySQL đã chạy.
  - Đúng `spring.datasource.url`, `username`, `password` trong `application.properties`.

- **Port 8080 đã bị chiếm**:
  - Mở `server/nhom2/src/main/resources/application.properties`.
  - Đổi `server.port=8080` thành một port khác, ví dụ:

```properties
server.port=8081
```

  - Nếu đổi port, nhớ **đổi lại URL API trong client**:
    - Mở `client/nhom2clientapp/src/main/java/client_ttnn/hcmute/StudentApiService.java`.
    - Sửa hằng `BASE_URL` cho đúng port, ví dụ:

```java
private static final String BASE_URL = "http://localhost:8081/api/students";
```

- **Client báo lỗi khi tải danh sách sinh viên**:
  - Kiểm tra xem server có đang chạy không.
  - Kiểm tra log server để xem HTTP status code và lỗi cụ thể.

---

## 7. Tóm tắt siêu ngắn (để nhớ nhanh)

- **Bước 1**: Chạy server

```bash
cd server/nhom2
mvn clean install
mvn spring-boot:run
```

- **Bước 2**: Kiểm tra API

```bash
curl http://localhost:8080/api/students
```

- **Bước 3**: Chạy client

```bash
cd client/nhom2clientapp
mvn clean install
mvn exec:java -Dexec.mainClass="client_ttnn.hcmute.App"   # nếu đã cấu hình exec, hoặc chạy App.java trong IDE
```

Chỉ cần làm đúng 3 bước trên là có thể chạy và kiểm tra hệ thống bất cứ lúc nào sau này.

