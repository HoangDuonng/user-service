-- Tạo cơ sở dữ liệu và bảng cho ứng dụng quản lý người dùng
START TRANSACTION;

-- Đảm bảo cơ sở dữ liệu sử dụng múi giờ UTC
SET TIMEZONE = 'UTC';

-- Tạo bảng `users`
CREATE TABLE IF NOT EXISTS users (
    id SERIAL PRIMARY KEY,
    
    username VARCHAR(50) NOT NULL UNIQUE,
    email VARCHAR(100) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,
    
    first_name VARCHAR(100),
    last_name VARCHAR(100),
    gender SMALLINT DEFAULT 1,

    avatar VARCHAR(255) DEFAULT 'avatar.png',
    cover VARCHAR(255) DEFAULT 'no-image.png',
    address TEXT,
    phone VARCHAR(20),

    is_activated BOOLEAN DEFAULT FALSE,
    is_deleted BOOLEAN DEFAULT FALSE,
    
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Dữ liệu mẫu cho bảng `users`
INSERT INTO users (username, email, password, first_name, last_name, gender, avatar, cover, address, phone, is_activated, is_deleted)
VALUES 
('nguyen_van_anh', 'nguyen.anh@example.com', 'hashed_password_1', 'Nguyễn', 'Văn Anh', 1, 'avatar1.png', 'cover1.png', '123 Đường Lê Lợi, Thành phố Hồ Chí Minh', '0123-456-7890', TRUE, FALSE),
('le_thi_bich', 'le.bich@example.com', 'hashed_password_2', 'Lê', 'Thị Bích', 2, 'avatar2.png', 'cover2.png', '456 Đường Nguyễn Trãi, Hà Nội', '0123-555-7890', TRUE, FALSE),
('pham_minh_tu', 'pham.tu@example.com', 'hashed_password_3', 'Phạm', 'Minh Tú', 1, 'avatar3.png', 'cover3.png', '789 Đường Phan Đình Phùng, Đà Nẵng', '0123-666-7890', TRUE, FALSE),
('trinh_huynh_mai', 'trinh.mai@example.com', 'hashed_password_4', 'Trịnh', 'Huỳnh Mai', 2, 'avatar4.png', 'cover4.png', '321 Đường Lý Thường Kiệt, Huế', '0123-777-7890', FALSE, FALSE),
('hoang_thanh', 'hoang.thanh@example.com', 'hashed_password_5', 'Hoàng', 'Thành', 1, 'avatar5.png', 'cover5.png', '654 Đường Trường Chinh, Cần Thơ', '0123-888-7890', TRUE, FALSE);

-- Tạo bảng `roles`
CREATE TABLE IF NOT EXISTS roles (
    id SERIAL PRIMARY KEY,
    name VARCHAR(100) NOT NULL UNIQUE,
    display_name VARCHAR(100),
    description TEXT,
    sort SMALLINT DEFAULT 99,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Dữ liệu mẫu cho bảng `roles`
INSERT INTO roles (name, display_name, description, sort)
VALUES 
('admin', 'Quản trị viên', 'Toàn quyền truy cập hệ thống', 1),
('user', 'Người dùng', 'Người dùng thông thường với quyền hạn giới hạn', 2),
('hotel_owner', 'Chủ khách sạn', 'Có thể quản lý thông tin khách sạn và đặt phòng', 3),
('blogger', 'Nhà viết blog', 'Có thể tạo và quản lý bài viết blog', 4),
('guest', 'Khách', 'Khách với quyền truy cập đọc', 5);

-- Tạo bảng `user_roles`
CREATE TABLE IF NOT EXISTS user_roles (
    user_id INT NOT NULL,
    role_id INT NOT NULL,
    PRIMARY KEY (user_id, role_id),
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
    FOREIGN KEY (role_id) REFERENCES roles(id) ON DELETE CASCADE
);

-- Dữ liệu mẫu cho bảng `user_roles`
INSERT INTO user_roles (user_id, role_id)
VALUES 
(1, 1),  -- Nguyễn Văn Anh là quản trị viên
(2, 2),  -- Lê Thị Bích là người dùng
(3, 3),  -- Phạm Minh Tú là chủ khách sạn
(4, 4),  -- Trịnh Huỳnh Mai là nhà viết blog
(5, 5);  -- Hoàng Thành là khách

-- Tạo chỉ mục kép cho `user_id` và `role_id` trong bảng `user_roles`
CREATE INDEX idx_user_role ON user_roles(user_id, role_id);

-- Thêm chỉ mục phụ cho cột `role_id`
CREATE INDEX role_user_role_id_foreign ON user_roles(role_id);

-- Thêm các ràng buộc khóa ngoại cho `user_roles`
ALTER TABLE user_roles
    ADD CONSTRAINT role_user_role_id_foreign FOREIGN KEY (role_id) REFERENCES roles(id) ON DELETE CASCADE ON UPDATE CASCADE,
    ADD CONSTRAINT role_user_user_id_foreign FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE ON UPDATE CASCADE;

-- COMMIT các thay đổi
COMMIT;
