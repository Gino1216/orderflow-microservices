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

