CREATE DATABASE IF NOT EXISTS bd_minimarket_nilda
CHARACTER SET utf8mb4
COLLATE utf8mb4_unicode_ci;

USE bd_minimarket_nilda;

CREATE TABLE categoria (
    id_categoria BIGINT AUTO_INCREMENT PRIMARY KEY,
    nombre VARCHAR(60) NOT NULL UNIQUE
);

CREATE TABLE unidad_medida (
    id_unidad BIGINT AUTO_INCREMENT PRIMARY KEY,
    abreviatura VARCHAR(20) NOT NULL UNIQUE,
    nombre VARCHAR(40) NOT NULL,
    activo BOOLEAN NOT NULL DEFAULT TRUE
);

CREATE TABLE usuario (
    id_usuario BIGINT AUTO_INCREMENT PRIMARY KEY,
    nombre VARCHAR(80) NOT NULL,
    username VARCHAR(40) NOT NULL UNIQUE,
    password_hash VARCHAR(100) NOT NULL,
    rol ENUM('ADMIN', 'VENDEDOR') NOT NULL,
    activo BOOLEAN NOT NULL DEFAULT TRUE
);

CREATE TABLE producto (
    id_producto BIGINT AUTO_INCREMENT PRIMARY KEY,
    codigo VARCHAR(20) NOT NULL UNIQUE,
    nombre VARCHAR(100) NOT NULL,
    id_categoria BIGINT NOT NULL,
    id_unidad BIGINT NOT NULL DEFAULT 1,
    precio_compra DECIMAL(10, 2) NOT NULL,
    precio_venta DECIMAL(10, 2) NOT NULL,
    stock DECIMAL(10, 3) NOT NULL DEFAULT 0.000,
    stock_minimo DECIMAL(10, 3) NOT NULL DEFAULT 5.000,
    activo BOOLEAN NOT NULL DEFAULT TRUE,
    CONSTRAINT fk_producto_categoria FOREIGN KEY (id_categoria) REFERENCES categoria(id_categoria),
    CONSTRAINT fk_producto_unidad FOREIGN KEY (id_unidad) REFERENCES unidad_medida(id_unidad)
);

CREATE TABLE venta (
    id_venta BIGINT AUTO_INCREMENT PRIMARY KEY,
    nro_comprobante VARCHAR(15) NOT NULL UNIQUE,
    fecha DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    cliente VARCHAR(100) NOT NULL DEFAULT 'Público general',
    total DECIMAL(10, 2) NOT NULL,
    id_usuario BIGINT NOT NULL,
    CONSTRAINT fk_venta_usuario FOREIGN KEY (id_usuario) REFERENCES usuario(id_usuario)
);

CREATE TABLE detalle_venta (
    id_detalle BIGINT AUTO_INCREMENT PRIMARY KEY,
    id_venta BIGINT NOT NULL,
    id_producto BIGINT NOT NULL,
    cantidad DECIMAL(10, 3) NOT NULL,
    precio_unitario DECIMAL(10, 2) NOT NULL,
    subtotal DECIMAL(10, 2) NOT NULL,
    CONSTRAINT fk_detalle_venta FOREIGN KEY (id_venta) REFERENCES venta(id_venta) ON DELETE CASCADE,
    CONSTRAINT fk_detalle_producto FOREIGN KEY (id_producto) REFERENCES producto(id_producto)
);

CREATE TABLE movimiento_inventario (
    id_movimiento BIGINT AUTO_INCREMENT PRIMARY KEY,
    id_producto BIGINT NOT NULL,
    id_usuario BIGINT NOT NULL,
    tipo ENUM('ENTRADA', 'SALIDA') NOT NULL,
    cantidad DECIMAL(10, 3) NOT NULL,
    fecha DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    motivo VARCHAR(150),
    CONSTRAINT fk_movimiento_producto FOREIGN KEY (id_producto) REFERENCES producto(id_producto),
    CONSTRAINT fk_movimiento_usuario FOREIGN KEY (id_usuario) REFERENCES usuario(id_usuario)
);

-- Datos iniciales
INSERT INTO categoria (nombre) VALUES
('Abarrotes'),
('Bebidas'),
('Limpieza'),
('Frutas y Verduras'),
('Lácteos y Huevos'),
('Cuidado Personal'),
('Snacks y Golosinas'),
('Embutidos'),
('Conservas'),
('Bazar y Hogar');

INSERT INTO unidad_medida (abreviatura, nombre) VALUES
('ud', 'Unidad'),
('kg', 'Kilogramo'),
('lt', 'Litro'),
('pq', 'Paquete'),
('g', 'Gramo'),
('ml', 'Mililitro'),
('cja', 'Caja');


INSERT INTO usuario (nombre, username, password_hash, rol, activo) VALUES
('Administrador Nilda', 'admin', '$2a$10$dXJ3SW6G7P50lGmMkkmwe.20cQQubK3.HZWzG3YB1tlRy.fqvM/BG', 'ADMIN', TRUE),
('Vendedor Turno 1', 'vendedor', '$2a$10$dXJ3SW6G7P50lGmMkkmwe.20cQQubK3.HZWzG3YB1tlRy.fqvM/BG', 'VENDEDOR', TRUE);
