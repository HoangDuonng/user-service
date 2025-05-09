-- V1__init_schema.sql
START TRANSACTION;

-- Đảm bảo cơ sở dữ liệu sử dụng múi giờ UTC
SET TIMEZONE = 'UTC';

-- Tạo bảng users (full version for user service)
CREATE TABLE IF NOT EXISTS users (
    id SERIAL PRIMARY KEY,
    
    username VARCHAR(50) NOT NULL UNIQUE,
    email VARCHAR(100) NOT NULL UNIQUE,
    
    first_name VARCHAR(100),
    last_name VARCHAR(100),
    gender SMALLINT DEFAULT 1,

    avatar VARCHAR(255) DEFAULT 'avatar.png',
    cover VARCHAR(255) DEFAULT 'no-image.png',
    address TEXT,
    phone VARCHAR(20),
    dob DATE DEFAULT NULL,

    is_activated BOOLEAN DEFAULT FALSE,
    is_deleted BOOLEAN DEFAULT FALSE,
    
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Tạo chỉ mục cho bảng users
CREATE INDEX IF NOT EXISTS idx_users_username ON users(username);
CREATE INDEX IF NOT EXISTS idx_users_email ON users(email);
CREATE INDEX IF NOT EXISTS idx_users_phone ON users(phone);
CREATE INDEX IF NOT EXISTS idx_users_created_at ON users(created_at);
CREATE INDEX IF NOT EXISTS idx_users_is_activated ON users(is_activated);
CREATE INDEX IF NOT EXISTS idx_users_is_deleted ON users(is_deleted);

COMMIT; 