-- V1__init_schema.sql
BEGIN;

-- Drop existing table if exists
DROP TABLE IF EXISTS public.users CASCADE;

-- Create users table with correct structure
CREATE TABLE public.users (
    id BIGSERIAL NOT NULL,
    document_id UUID DEFAULT gen_random_uuid() UNIQUE NOT NULL,
    username VARCHAR(255) NOT NULL,
    email VARCHAR(255) NOT NULL,
    first_name VARCHAR(100) NULL,
    last_name VARCHAR(100) NULL,
    full_name VARCHAR(255) NULL,
    gender INT4 DEFAULT 1 NULL,
    avatar VARCHAR(255) DEFAULT 'avatar.png'::character varying NULL,
    cover VARCHAR(255) DEFAULT 'no-image.png'::character varying NULL,
    address TEXT NULL,
    phone VARCHAR(20) NULL,
    dob DATE NULL,
    is_activated BOOLEAN DEFAULT false NULL,
    is_deleted BOOLEAN DEFAULT false NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP NULL,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP NULL,
    CONSTRAINT users_email_key UNIQUE (email),
    CONSTRAINT users_pkey PRIMARY KEY (id),
    CONSTRAINT users_username_key UNIQUE (username)
);

-- Create indexes
CREATE INDEX idx_users_created_at ON public.users USING btree (created_at);
CREATE INDEX idx_users_email ON public.users USING btree (email);
CREATE INDEX idx_users_is_activated ON public.users USING btree (is_activated);
CREATE INDEX idx_users_is_deleted ON public.users USING btree (is_deleted);
CREATE INDEX idx_users_phone ON public.users USING btree (phone);
CREATE INDEX idx_users_username ON public.users USING btree (username);
CREATE INDEX idx_users_document_id ON public.users USING btree (document_id);

COMMIT; 
