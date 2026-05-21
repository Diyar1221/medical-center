-- ============================
-- Медицинский центр — SQL Schema
-- ============================

CREATE TABLE IF NOT EXISTS permissions (
    id BIGSERIAL PRIMARY KEY,
    permission VARCHAR(100) NOT NULL,
    operation VARCHAR(100) NOT NULL
);

CREATE TABLE IF NOT EXISTS roles (
    id BIGSERIAL PRIMARY KEY,
    title VARCHAR(100) NOT NULL UNIQUE
);

CREATE TABLE IF NOT EXISTS role_permissions (
    role_id BIGINT REFERENCES roles(id) ON DELETE CASCADE,
    permission_id BIGINT REFERENCES permissions(id) ON DELETE CASCADE,
    PRIMARY KEY (role_id, permission_id)
);

CREATE TABLE IF NOT EXISTS users (
    id BIGSERIAL PRIMARY KEY,
    username VARCHAR(100) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,
    enabled BOOLEAN NOT NULL DEFAULT TRUE
);

CREATE TABLE IF NOT EXISTS user_roles (
    user_id BIGINT REFERENCES users(id) ON DELETE CASCADE,
    role_id BIGINT REFERENCES roles(id) ON DELETE CASCADE,
    PRIMARY KEY (user_id, role_id)
);

CREATE TABLE IF NOT EXISTS specializations (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(100) NOT NULL UNIQUE,
    description TEXT
);

CREATE TABLE IF NOT EXISTS doctors (
    id BIGSERIAL PRIMARY KEY,
    first_name VARCHAR(100) NOT NULL,
    last_name VARCHAR(100) NOT NULL,
    middle_name VARCHAR(100),
    specialization_id BIGINT REFERENCES specializations(id),
    phone VARCHAR(20),
    email VARCHAR(100),
    experience_years INT,
    created_at TIMESTAMP DEFAULT NOW()
);

CREATE TABLE IF NOT EXISTS patients (
    id BIGSERIAL PRIMARY KEY,
    first_name VARCHAR(100) NOT NULL,
    last_name VARCHAR(100) NOT NULL,
    middle_name VARCHAR(100),
    birth_date DATE NOT NULL,
    phone VARCHAR(20),
    email VARCHAR(100),
    address TEXT,
    created_at TIMESTAMP DEFAULT NOW()
);

CREATE TABLE IF NOT EXISTS appointments (
    id BIGSERIAL PRIMARY KEY,
    patient_id BIGINT NOT NULL REFERENCES patients(id) ON DELETE CASCADE,
    doctor_id BIGINT NOT NULL REFERENCES doctors(id) ON DELETE CASCADE,
    date_time TIMESTAMP NOT NULL,
    status VARCHAR(30) NOT NULL DEFAULT 'SCHEDULED',
    notes TEXT,
    created_at TIMESTAMP DEFAULT NOW()
);

CREATE TABLE IF NOT EXISTS medical_records (
    id BIGSERIAL PRIMARY KEY,
    patient_id BIGINT NOT NULL REFERENCES patients(id) ON DELETE CASCADE,
    doctor_id BIGINT NOT NULL REFERENCES doctors(id),
    appointment_id BIGINT REFERENCES appointments(id),
    visit_date TIMESTAMP NOT NULL,
    diagnosis TEXT NOT NULL,
    treatment TEXT,
    notes TEXT,
    created_at TIMESTAMP DEFAULT NOW()
);

-- Начальные данные
INSERT INTO permissions (permission, operation) VALUES
    ('PATIENT', 'READ'),
    ('PATIENT', 'WRITE'),
    ('DOCTOR', 'READ'),
    ('DOCTOR', 'WRITE'),
    ('APPOINTMENT', 'READ'),
    ('APPOINTMENT', 'WRITE'),
    ('RECORD', 'READ'),
    ('RECORD', 'WRITE'),
    ('REPORT', 'READ')
ON CONFLICT DO NOTHING;

INSERT INTO roles (title) VALUES ('ROLE_ADMIN'), ('ROLE_DOCTOR'), ('ROLE_RECEPTIONIST')
ON CONFLICT DO NOTHING;

INSERT INTO role_permissions (role_id, permission_id)
SELECT r.id, p.id FROM roles r, permissions p WHERE r.title = 'ROLE_ADMIN'
ON CONFLICT DO NOTHING;

-- Пароль: admin123 (bcrypt)
INSERT INTO users (username, password, enabled) VALUES
    ('admin', '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy', TRUE)
ON CONFLICT DO NOTHING;

INSERT INTO user_roles (user_id, role_id)
SELECT u.id, r.id FROM users u, roles r WHERE u.username = 'admin' AND r.title = 'ROLE_ADMIN'
ON CONFLICT DO NOTHING;

INSERT INTO specializations (name, description) VALUES
    ('Терапевт', 'Общая терапия'),
    ('Кардиолог', 'Заболевания сердца и сосудов'),
    ('Невролог', 'Заболевания нервной системы'),
    ('Хирург', 'Хирургические вмешательства'),
    ('Педиатр', 'Лечение детей')
ON CONFLICT DO NOTHING;
