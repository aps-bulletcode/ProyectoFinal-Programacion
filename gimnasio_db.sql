-- ============================================================
--  gimnasio_db.sql  –  Script completo de inicialización
--  Ejecutar en MySQL Workbench o desde consola:
--    mysql -u root -p < gimnasio_db.sql
-- ============================================================

CREATE DATABASE IF NOT EXISTS gimnasio_db CHARACTER SET utf8mb4 COLLATE utf8mb4_spanish_ci;

USE gimnasio_db;

-- ------------------------------------------------------------
-- 1. Tabla raíz: Usuarios
-- ------------------------------------------------------------
CREATE TABLE IF NOT EXISTS usuarios (
    id INT AUTO_INCREMENT PRIMARY KEY,
    username VARCHAR(50) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,
    email VARCHAR(100) NOT NULL UNIQUE,
    nombre VARCHAR(100),
    apellidos VARCHAR(100),
    dni VARCHAR(20) UNIQUE,
    rol ENUM('CLIENTE', 'ADMIN') NOT NULL DEFAULT 'CLIENTE'
);

-- ------------------------------------------------------------
-- 2. Tabla hija: Clientes  (herencia 1-a-1 con usuarios)
-- ------------------------------------------------------------
CREATE TABLE IF NOT EXISTS clientes (
    id INT PRIMARY KEY, -- mismo id que usuarios
    objetivo_fitness VARCHAR(100),
    peso_inicial DECIMAL(5, 2),
    altura INT,
    fecha_nacimiento DATE,
    genero VARCHAR(20),
    CONSTRAINT fk_cliente_usuario FOREIGN KEY (id) REFERENCES usuarios (id) ON DELETE CASCADE
);

-- ------------------------------------------------------------
-- 3. Entidad: Clases
-- ------------------------------------------------------------
CREATE TABLE IF NOT EXISTS clases (
    id INT AUTO_INCREMENT PRIMARY KEY,
    nombre VARCHAR(100) NOT NULL,
    descripcion TEXT,
    aforo_max INT NOT NULL
);

-- ------------------------------------------------------------
-- 4. Relación N:M: Inscripciones
-- ------------------------------------------------------------
CREATE TABLE IF NOT EXISTS inscripciones (
    id INT AUTO_INCREMENT PRIMARY KEY,
    cliente_id INT NOT NULL,
    clase_id INT NOT NULL,
    fecha_inscripcion DATE NOT NULL,
    CONSTRAINT fk_ins_cliente FOREIGN KEY (cliente_id) REFERENCES clientes (id) ON DELETE CASCADE,
    CONSTRAINT fk_ins_clase FOREIGN KEY (clase_id) REFERENCES clases (id) ON DELETE CASCADE
);

INSERT INTO
    clases (
        nombre,
        descripcion,
        aforo_max
    )
VALUES (
        'Crossfit',
        'Alta intensidad',
        20
    ),
    (
        'Yoga',
        'Relajación y flexibilidad',
        15
    );