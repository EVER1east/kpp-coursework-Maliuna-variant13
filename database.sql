DROP DATABASE IF EXISTS hotel;
CREATE DATABASE hotel;
USE hotel;

-- Таблиця номерів
CREATE TABLE rooms (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    r_number INTEGER NOT NULL,
    price INTEGER NOT NULL,
    status INTEGER NOT NULL,

    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP
);

-- Таблиця працівників
CREATE TABLE specialists (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name TEXT NOT NULL,
    surname TEXT NOT NULL,
    post TEXT NOT NULL,

    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP
);

-- Таблиця заявок
CREATE TABLE requests (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    room_id BIGINT NOT NULL,
    specialist_id BIGINT NULL,
    description TEXT NOT NULL,
    status ENUM('NEW', 'ASSIGNED', 'IN_PROGRESS', 'COMPLETED')
        NOT NULL DEFAULT 'NEW',

    guest_impact TINYINT UNSIGNED NOT NULL CHECK (guest_impact <= 100),
    fault_severity TINYINT UNSIGNED NOT NULL CHECK (fault_severity <= 100),
    room_availablity_impact TINYINT UNSIGNED NOT NULL
        CHECK (room_availablity_impact <=100),
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,

    FOREIGN KEY (room_id) REFERENCES rooms(id),
    FOREIGN KEY (specialist_id) REFERENCES specialists(id)
);

-- Таблиця звітів
CREATE TABLE execution_records (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    request_id BIGINT NOT NULL UNIQUE,
    work_done TEXT NOT NULL,
    completed_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,

    FOREIGN KEY (request_id) REFERENCES requests(id)
);

INSERT INTO rooms (r_number, price, status) VALUES
(1, 69, 0),
(2, 1, 1);

INSERT INTO specialists (name, surname, post) VALUES
('Vitaliy', 'Maliuna', 'electrician'),
('Maksim', 'Crystal', 'tech_support');

