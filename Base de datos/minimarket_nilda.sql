CREATE DATABASE IF NOT EXISTS bd_minimarket_nilda
CHARACTER SET utf8mb4
COLLATE utf8mb4_unicode_ci;

USE bd_minimarket_nilda;

CREATE TABLE categoria (
    id_categoria BIGINT AUTO_INCREMENT PRIMARY KEY,
    nombre VARCHAR(60) NOT NULL UNIQUE
) ENGINE=InnoDB;

CREATE TABLE usuario (
    id_usuario BIGINT AUTO_INCREMENT PRIMARY KEY,
    nombre VARCHAR(80) NOT NULL,
    username VARCHAR(40) NOT NULL UNIQUE,
    password_hash VARCHAR(100) NOT NULL,
    rol ENUM('ADMIN', 'VENDEDOR') NOT NULL,
    activo BOOLEAN NOT NULL DEFAULT TRUE
) ENGINE=InnoDB;

CREATE TABLE producto (
    id_producto BIGINT AUTO_INCREMENT PRIMARY KEY,
    codigo VARCHAR(20) NOT NULL UNIQUE,
    nombre VARCHAR(100) NOT NULL,
    id_categoria BIGINT NOT NULL,
    precio_compra DECIMAL(10, 2) NOT NULL,
    precio_venta DECIMAL(10, 2) NOT NULL,
    stock INT NOT NULL DEFAULT 0,
    stock_minimo INT NOT NULL DEFAULT 5,
    activo BOOLEAN NOT NULL DEFAULT TRUE,
    CONSTRAINT fk_producto_categoria FOREIGN KEY (id_categoria) REFERENCES categoria(id_categoria),
    INDEX idx_producto_codigo (codigo)
) ENGINE=InnoDB;

CREATE TABLE venta (
    id_venta BIGINT AUTO_INCREMENT PRIMARY KEY,
    nro_comprobante VARCHAR(15) NOT NULL UNIQUE,
    fecha DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    cliente VARCHAR(100) NOT NULL DEFAULT 'Público general',
    total DECIMAL(10, 2) NOT NULL,
    id_usuario BIGINT NOT NULL,
    CONSTRAINT fk_venta_usuario FOREIGN KEY (id_usuario) REFERENCES usuario(id_usuario),
    INDEX idx_venta_fecha (fecha)
) ENGINE=InnoDB;

CREATE TABLE detalle_venta (
    id_detalle BIGINT AUTO_INCREMENT PRIMARY KEY,
    id_venta BIGINT NOT NULL,
    id_producto BIGINT NOT NULL,
    cantidad INT NOT NULL,
    precio_unitario DECIMAL(10, 2) NOT NULL,
    subtotal DECIMAL(10, 2) NOT NULL,
    CONSTRAINT fk_detalle_venta FOREIGN KEY (id_venta) REFERENCES venta(id_venta) ON DELETE CASCADE,
    CONSTRAINT fk_detalle_producto FOREIGN KEY (id_producto) REFERENCES producto(id_producto)
) ENGINE=InnoDB;

CREATE TABLE movimiento_inventario (
    id_movimiento BIGINT AUTO_INCREMENT PRIMARY KEY,
    id_producto BIGINT NOT NULL,
    id_usuario BIGINT NOT NULL,
    tipo ENUM('ENTRADA', 'SALIDA') NOT NULL,
    cantidad INT NOT NULL,
    fecha DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    motivo VARCHAR(150),
    CONSTRAINT fk_movimiento_producto FOREIGN KEY (id_producto) REFERENCES producto(id_producto),
    CONSTRAINT fk_movimiento_usuario FOREIGN KEY (id_usuario) REFERENCES usuario(id_usuario)
) ENGINE=InnoDB;



INSERT INTO categoria (nombre) VALUES 
('Abarrotes'), 
('Bebidas'), 
('Limpieza'), 
('Frutas y Verduras');


INSERT INTO usuario (nombre, username, password_hash, rol, activo) VALUES 
('Administrador Nilda', 'admin', '$2a$10$$2a$10$dXJ3SW6G7P50lGmMkkmwe.20cQQubK3.HZWzG3YB1tlRy.fqvM/BG', 'ADMIN', TRUE),
('Vendedor Turno 1', 'vendedor', '$2a$10$dXJ3SW6G7P50lGmMkkmwe.20cQQubK3.HZWzG3YB1tlRy.fqvM/BG', 'VENDEDOR', TRUE);

INSERT INTO producto (codigo, nombre, id_categoria, precio_compra, precio_venta, stock, stock_minimo, activo) VALUES 
('PD-01', 'Aceite Primor 1L', 1, 7.50, 9.50, 10, 5, TRUE),
('PD-02', 'Leche Gloria Evaporada 400g', 1, 3.20, 4.20, 20, 6, TRUE),
('PD-03', 'Arroz Costeño 1kg', 1, 3.50, 4.50, 3, 5, TRUE),
('PD-04', 'Azúcar Rubia 1kg', 1, 3.00, 4.00, 18, 5, TRUE);

