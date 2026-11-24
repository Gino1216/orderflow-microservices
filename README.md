# OrderFlow Microservices

Mô phỏng luồng đặt hàng bằng hai dịch vụ Spring Boot độc lập:

```text
Client -> order-service (8082) -> catalog-service (8081)
              |                       |
           orders DB              catalog DB
```

- `catalog-service`: quản lý sản phẩm, giá và tồn kho; cơ chế giữ trước tồn kho sử dụng khóa lạc quan để tránh ghi đè khi có nhiều yêu cầu đồng thời.
- `order-service`: tạo và truy vấn đơn hàng; gọi Catalog qua REST và lưu bản chụp tên/giá sản phẩm tại thời điểm đặt hàng.
- Mỗi dịch vụ có cơ sở dữ liệu riêng, endpoint kiểm tra trạng thái, validation, ánh xạ ngoại lệ và kiểm thử đơn vị.

## Chạy nhanh với H2

Yêu cầu: JDK 21 và Maven 3.9 trở lên.

Terminal 1:

```bash
mvn -pl catalog-service spring-boot:run
```

Terminal 2:

```bash
mvn -pl order-service spring-boot:run
```

Dữ liệu mẫu được tạo tự động. Xem danh sách sản phẩm:

```bash
curl http://localhost:8081/api/products
```

Tạo đơn hàng bằng `productId` nhận được:

```bash
curl -X POST http://localhost:8082/api/orders \
  -H "Content-Type: application/json" \
  -d '{"productId":1,"quantity":2,"customerEmail":"buyer@example.com"}'

curl "http://localhost:8082/api/orders?customerEmail=buyer@example.com"
```

## Chạy toàn bộ bằng Docker

Build các file JAR trước, sau đó khởi động hai dịch vụ và hai cơ sở dữ liệu PostgreSQL:

```bash
mvn clean package
docker compose up --build
```

## Kiểm thử

```bash
mvn test
```

## Các quyết định kỹ thuật có thể trình bày khi phỏng vấn

- Không dùng chung entity hoặc cơ sở dữ liệu giữa các dịch vụ; giao tiếp bằng DTO qua HTTP.
- Lưu bản chụp `productName` và `unitPrice` trong đơn hàng để lịch sử đơn hàng không thay đổi khi Catalog cập nhật dữ liệu sản phẩm.
- `@Version` phát hiện hai giao dịch cùng cập nhật một sản phẩm, giúp ngăn bán vượt tồn kho ở cấp cơ sở dữ liệu.
- `CatalogGateway` là interface tại biên hệ thống, giúp `OrderService` dễ kiểm thử đơn vị và cho phép thay REST bằng message broker trong tương lai.
- Đây là phiên bản portfolio gọn. Hướng phát triển tiếp theo phù hợp là Saga/Outbox để thực hiện giao dịch bù tồn kho nếu việc lưu đơn hàng thất bại, cùng idempotency key để chống tạo đơn trùng.

## Trao đổi khi phỏng vấn

### Vì sao mỗi microservice có một database riêng?

Mỗi dịch vụ sở hữu dữ liệu và mô hình nghiệp vụ của mình, nhờ đó có thể triển khai, mở rộng và thay đổi độc lập. `order-service` không truy cập bảng sản phẩm mà gọi API của `catalog-service`, tránh phụ thuộc chặt vào schema nội bộ của dịch vụ khác.

### Vì sao đơn hàng lưu lại tên và giá sản phẩm?

Tên và giá được lưu dưới dạng bản chụp tại thời điểm đặt hàng. Nếu Catalog thay đổi giá hoặc tên sản phẩm sau đó, lịch sử và tổng tiền của đơn hàng cũ vẫn chính xác.

### `@Version` hỗ trợ xử lý tồn kho đồng thời như thế nào?

JPA đưa giá trị version vào điều kiện cập nhật. Nếu hai transaction cùng đọc một sản phẩm, transaction hoàn thành sau sẽ phát hiện version đã thay đổi và không ghi đè dữ liệu mới hơn. Cơ chế này giảm nguy cơ bán vượt tồn kho mà không phải khóa bản ghi trong toàn bộ thời gian xử lý.

### Điều gì xảy ra nếu giữ tồn kho thành công nhưng lưu đơn hàng thất bại?

Hai database không nằm trong cùng một transaction nên phiên bản hiện tại có thể phát sinh tồn kho đã trừ nhưng chưa có đơn hàng. Trong hệ thống thực tế, có thể dùng Saga với thao tác hoàn tồn kho, Transactional Outbox để phát sự kiện tin cậy và idempotency key để xử lý yêu cầu gửi lại.

### Vì sao sử dụng `CatalogGateway`?

`OrderService` phụ thuộc vào một interface nghiệp vụ thay vì phụ thuộc trực tiếp vào HTTP client. Thiết kế này giúp unit test không cần khởi động Catalog, đồng thời cho phép thay REST bằng message broker hoặc adapter khác mà không sửa logic tạo đơn.

### Nếu phát triển tiếp, dự án nên bổ sung gì?

- API Gateway, service discovery và centralized configuration khi số lượng dịch vụ tăng.
- Retry có kiểm soát, circuit breaker và timeout cho giao tiếp liên dịch vụ.
- Distributed tracing, metrics và correlation ID để theo dõi một request qua nhiều dịch vụ.
- Testcontainers và contract test để kiểm tra tương thích API giữa các service.

## Mô tả ngắn để đưa vào CV

**OrderFlow - Order Processing Microservices**  
Phát triển hệ thống đặt hàng gồm hai microservice Spring Boot giao tiếp qua REST, mỗi dịch vụ sở hữu một cơ sở dữ liệu PostgreSQL riêng. Triển khai cơ chế giữ trước tồn kho với khóa lạc quan, validation, xử lý ngoại lệ tập trung, kiểm tra trạng thái dịch vụ, kiểm thử đơn vị bằng JUnit/Mockito và môi trường Docker Compose.

