# Chi Tiêu Tự Do — Android

Ứng dụng thu chi độc lập, **không phải bản sửa đổi 1Money**. Tạo giao dịch thu/chi, danh mục, ngân sách tháng, biểu đồ, lọc giao dịch, xuất CSV, sao lưu và nhập JSON.

## Tạo APK trong Android Studio

1. Cài Android Studio (JDK 17, Android SDK Platform 35).
2. Mở thư mục `ChiTieuAndroid`, chấp nhận Gradle Sync (cần mạng để tải Android Gradle Plugin).
3. Vào **Build → Build Bundle(s) / APK(s) → Build APK(s)**.
4. Lấy file: `app/build/outputs/apk/debug/app-debug.apk` và chuyển vào điện thoại để cài.

Dữ liệu lưu trong WebView localStorage của ứng dụng, không dùng internet. Sao lưu JSON thường xuyên trước khi gỡ ứng dụng hoặc xóa dữ liệu. Bản debug APK có thể cài để thử nghiệm; bản phát hành cần ký khóa riêng.

## Giới hạn

Không giống toàn bộ chức năng và giao diện 1Money. Chưa có nhiều ví, chuyển khoản nội bộ, lịch định kỳ, quản lý nợ, đa tiền tệ, widget hoặc đồng bộ cloud. Chưa kiểm thử trên thiết bị Android thực tế.

## Tự động build APK bằng GitHub Actions (không cần Android Studio)

1. Tạo repository **private** mới trên GitHub.
2. Giải nén toàn bộ dự án này và đưa **các nội dung bên trong thư mục ChiTieuAndroid** lên thư mục gốc repository (bao gồm `.github/workflows/build-apk.yml`, lưu ý file/thư mục ẩn).
3. Chọn **Actions → Build Android APK → Run workflow**. Workflow cũng chạy khi push lên nhánh `main` hoặc `master`.
4. Khi workflow hoàn tất, mở lượt chạy, tải artifact **ChiTieuTuDo-debug-apk**, giải nén lấy `app-debug.apk`.
5. Chép `app-debug.apk` sang Android và mở để cài; nếu cần, cho phép trình quản lý tệp được **Cài ứng dụng không rõ nguồn gốc**.

**Chú ý:** Bản này là APK debug để thử nghiệm; chưa được build/kiểm thử trong môi trường hiện tại. Không upload dữ liệu chi tiêu thật lên repository. Bản ứng dụng hiện chưa có toàn bộ chức năng của 1Money. Để phát hành lâu dài cần APK release được ký bằng keystore riêng và bổ sung kiểm thử.
