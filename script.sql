-- =====================================================================
-- Sistema de Gestión Hospitalaria — Esquema y datos iniciales
-- Motor: MySQL 8.x / MariaDB
-- ADVERTENCIA: este script ELIMINA y recrea la base de datos "hospital".
-- =====================================================================

DROP DATABASE IF EXISTS hospital;

CREATE DATABASE hospital
    CHARACTER SET utf8mb4
    COLLATE utf8mb4_unicode_ci;

USE hospital;

-- =========================================================
-- SEGURIDAD: perfiles, usuarios y funcionalidades
-- =========================================================

CREATE TABLE perfil (
    id_perfil INT AUTO_INCREMENT PRIMARY KEY,
    nombre VARCHAR(50) NOT NULL UNIQUE,
    descripcion VARCHAR(150),
    activo BOOLEAN DEFAULT TRUE
);

CREATE TABLE usuario (
    id_usuario INT AUTO_INCREMENT PRIMARY KEY,
    nombre VARCHAR(80) NOT NULL,
    apellido VARCHAR(80) NOT NULL,
    cedula VARCHAR(20) UNIQUE,
    telefono VARCHAR(30),
    correo VARCHAR(120),            -- renombrada desde "email" para coincidir con el modelo Java
    usuario VARCHAR(40) UNIQUE NOT NULL,
    contrasena VARCHAR(255) NOT NULL,  -- renombrada desde "password" para coincidir con el modelo Java
    fecha_alta DATE,                -- nueva, alineada con Usuario.fechaAlta
    ultimo_acceso DATE,             -- nueva, alineada con Usuario.ultimoAcceso
    id_perfil INT NOT NULL,
    activo BOOLEAN DEFAULT TRUE,
    CONSTRAINT fk_usuario_perfil
        FOREIGN KEY (id_perfil) REFERENCES perfil (id_perfil)
);

CREATE TABLE funcionalidad (
    id_funcionalidad INT AUTO_INCREMENT PRIMARY KEY,
    nombre VARCHAR(80) NOT NULL,
    descripcion VARCHAR(200)
);

CREATE TABLE perfil_funcionalidad (
    id_perfil INT,
    id_funcionalidad INT,
    PRIMARY KEY (id_perfil, id_funcionalidad),
    FOREIGN KEY (id_perfil) REFERENCES perfil (id_perfil),
    FOREIGN KEY (id_funcionalidad) REFERENCES funcionalidad (id_funcionalidad)
);

-- =========================================================
-- ORGANIZACIÓN: países, instituciones y ubicaciones
-- =========================================================

CREATE TABLE pais (
    id_pais INT AUTO_INCREMENT PRIMARY KEY,
    nombre VARCHAR(80) UNIQUE NOT NULL
);

CREATE TABLE institucion (
    id_institucion INT AUTO_INCREMENT PRIMARY KEY,
    nombre VARCHAR(150),
    direccion VARCHAR(200),
    telefono VARCHAR(30),
    id_pais INT NOT NULL,
    activo BOOLEAN DEFAULT TRUE,
    FOREIGN KEY (id_pais) REFERENCES pais (id_pais)
);

CREATE TABLE ubicacion (
    id_ubicacion INT AUTO_INCREMENT PRIMARY KEY,
    nombre VARCHAR(100),
    descripcion VARCHAR(200),
    id_institucion INT,
    FOREIGN KEY (id_institucion) REFERENCES institucion (id_institucion)
);

-- =========================================================
-- INVENTARIO: marcas, modelos, tipos, proveedores y equipos
-- =========================================================

CREATE TABLE marca (
    id_marca INT AUTO_INCREMENT PRIMARY KEY,
    nombre VARCHAR(80) UNIQUE NOT NULL,
    descripcion VARCHAR(200),          -- nueva, alineada con el modelo Marca
    activo BOOLEAN DEFAULT TRUE
);

CREATE TABLE modelo (
    id_modelo INT AUTO_INCREMENT PRIMARY KEY,
    nombre VARCHAR(100),
    id_marca INT,
    activo BOOLEAN DEFAULT TRUE,
    FOREIGN KEY (id_marca) REFERENCES marca (id_marca)
);

CREATE TABLE tipo_equipo (
    id_tipo INT AUTO_INCREMENT PRIMARY KEY,
    nombre VARCHAR(80),
    descripcion VARCHAR(150),
    activo BOOLEAN DEFAULT TRUE
);

CREATE TABLE proveedor (
    id_proveedor INT AUTO_INCREMENT PRIMARY KEY,
    nombre VARCHAR(150),
    direccion VARCHAR(200),
    telefono VARCHAR(30),
    email VARCHAR(100),
    activo BOOLEAN DEFAULT TRUE
);

CREATE TABLE equipo (
    id_equipo INT AUTO_INCREMENT PRIMARY KEY,
    codigo VARCHAR(30) UNIQUE,
    nombre VARCHAR(120),
    serie VARCHAR(120),
    fecha_compra DATE,
    fecha_garantia DATE,
    estado ENUM('ACTIVO','INACTIVO','MANTENIMIENTO'),
    observaciones TEXT,
    id_modelo INT,
    id_tipo INT,
    id_proveedor INT,
    id_ubicacion INT,
    FOREIGN KEY (id_modelo) REFERENCES modelo (id_modelo),
    FOREIGN KEY (id_tipo) REFERENCES tipo_equipo (id_tipo),
    FOREIGN KEY (id_proveedor) REFERENCES proveedor (id_proveedor),
    FOREIGN KEY (id_ubicacion) REFERENCES ubicacion (id_ubicacion)
);

-- =========================================================
-- PROCESOS: tipos de intervención, intervenciones y auditoría
-- =========================================================

CREATE TABLE tipo_intervencion (
    id_tipo_intervencion INT AUTO_INCREMENT PRIMARY KEY,
    nombre VARCHAR(80),
    descripcion VARCHAR(200)
);

CREATE TABLE intervencion (
    id_intervencion INT AUTO_INCREMENT PRIMARY KEY,
    fecha DATE,
    descripcion TEXT,
    costo DECIMAL(10,2),
    id_equipo INT,
    id_tipo_intervencion INT,
    id_usuario INT,
    FOREIGN KEY (id_equipo) REFERENCES equipo (id_equipo),
    FOREIGN KEY (id_tipo_intervencion) REFERENCES tipo_intervencion (id_tipo_intervencion),
    FOREIGN KEY (id_usuario) REFERENCES usuario (id_usuario)
);

CREATE TABLE auditoria (
    id_auditoria INT AUTO_INCREMENT PRIMARY KEY,
    fecha TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    tabla_afectada VARCHAR(80),
    operacion VARCHAR(30),
    descripcion TEXT,
    id_usuario INT,
    FOREIGN KEY (id_usuario) REFERENCES usuario (id_usuario)
);

-- =========================================================
-- DATOS INICIALES (seeds)
-- =========================================================

-- Perfiles
INSERT INTO perfil (nombre, descripcion, activo) VALUES
    ('Administrador', 'Acceso total al sistema', TRUE),
    ('Operador', 'Acceso a procesos y consultas', TRUE);

-- Usuario administrador por defecto:  admin / admin123
INSERT INTO usuario
    (nombre, apellido, cedula, telefono, correo, usuario, contrasena, fecha_alta, activo, id_perfil)
VALUES
    ('Admin', 'Sistema', '50000000', '099000000', 'admin@hospital.com', 'admin',
     '$2a$12$Ngqd.6UcD2uKz1itqURoJe24qHNYYtGanCr4mkDHVn9p7ncT6wkTC', CURDATE(), TRUE, 1);

-- Funcionalidades
INSERT INTO funcionalidad (nombre, descripcion) VALUES
    ('Usuarios', 'ABM de usuarios del sistema'),
    ('Perfiles', 'ABM de perfiles de acceso'),
    ('Auditoría', 'Consulta de auditoría'),
    ('Marcas', 'ABM de marcas de equipos'),
    ('Modelos', 'ABM de modelos de equipos'),
    ('Proveedores', 'ABM de proveedores'),
    ('Equipos', 'ABM de equipos biomédicos'),
    ('Instituciones', 'ABM de instituciones'),
    ('Ubicaciones', 'ABM de ubicaciones'),
    ('Tipos de Equipo', 'ABM de tipos de equipo'),
    ('Tipos de Intervención', 'ABM de tipos de intervención'),
    ('Intervenciones', 'Registro de intervenciones');

-- Perfil Administrador (1): todas las funcionalidades; Operador (2): Equipos e Intervenciones
INSERT INTO perfil_funcionalidad (id_perfil, id_funcionalidad) VALUES
    (1,1),(1,2),(1,3),(1,4),(1,5),(1,6),(1,7),(1,8),(1,9),(1,10),(1,11),(1,12),
    (2,7),(2,12);

-- Países
INSERT INTO pais (nombre) VALUES
    ('Uruguay'), ('Argentina'), ('Brasil'), ('Chile'), ('Paraguay');

-- Instituciones
INSERT INTO institucion (nombre, direccion, telefono, id_pais, activo) VALUES
    ('Hospital de Clínicas', 'Av. Italia s/n, Montevideo', '26004141', 1, TRUE),
    ('Hospital Maciel', '25 de Mayo 155, Montevideo', '29155500', 1, TRUE),
    ('Sanatorio Americano', 'Bulevar Artigas 1462', '24877777', 1, TRUE);

-- Ubicaciones
INSERT INTO ubicacion (nombre, descripcion, id_institucion) VALUES
    ('Quirófano 1', 'Bloque quirúrgico central', 1),
    ('Cuidados Intensivos', 'Unidad de cuidados intensivos adultos', 1),
    ('Hemodinámica', 'Sala de hemodinámica', 2),
    ('Almacén central', 'Depósito de equipos', 3);

-- Marcas
INSERT INTO marca (nombre, descripcion, activo) VALUES
    ('Siemens', 'Tecnología médica alemana', TRUE),
    ('General Electric', 'Equipos de imagen y monitoreo', TRUE),
    ('Philips', 'Imagenología y signos vitales', TRUE),
    ('Dräger', 'Ventilación y anestesia', TRUE),
    ('Medtronic', 'Dispositivos y bombas de infusión', TRUE);

-- Modelos
INSERT INTO modelo (nombre, id_marca, activo) VALUES
    ('Acuson X300', 1, TRUE),
    ('Vivid E95', 2, TRUE),
    ('MX550', 3, TRUE),
    ('Evita V300', 4, TRUE),
    ('SynchroMed II', 5, TRUE);

-- Tipos de equipo
INSERT INTO tipo_equipo (nombre, descripcion, activo) VALUES
    ('Monitor de signos vitales', 'Monitoreo de paciente', TRUE),
    ('Ventilador', 'Soporte respiratorio', TRUE),
    ('Ecógrafo', 'Diagnóstico por imagen', TRUE),
    ('Bomba de infusión', 'Administración de fármacos', TRUE),
    ('Desfibrilador', 'Emergencias cardiológicas', TRUE);

-- Proveedores
INSERT INTO proveedor (nombre, direccion, telefono, email, activo) VALUES
    ('Bio-Médica SRL', 'Zabala 1234, Montevideo', '29088877', 'ventas@biomedica.com.uy', TRUE),
    ('Equipos Médicos del Sur', 'Bulevar España 2109', '24101234', 'info@ems.com.uy', TRUE),
    ('TecnoSalud', 'Millán 4500', '23122233', 'contacto@tecnosalud.com.uy', TRUE);

-- Equipos
INSERT INTO equipo
    (codigo, nombre, serie, fecha_compra, fecha_garantia, estado, observaciones, id_modelo, id_tipo, id_proveedor, id_ubicacion)
VALUES
    ('MSV-001', 'Monitor UCI 01', 'SN-MSV-0001', '2023-03-15', '2026-03-15', 'ACTIVO', 'Monitor multiparamétrico de UCI', 1, 1, 1, 2),
    ('VENT-002', 'Ventilador UCI 02', 'SN-VENT-0002', '2022-11-02', '2025-11-02', 'MANTENIMIENTO', 'En revisión de calibración', 4, 2, 3, 2),
    ('ECO-003', 'Ecógrafo portátil', 'SN-ECO-0003', '2024-01-20', '2027-01-20', 'ACTIVO', 'Ecógrafo para guardia', 2, 3, 2, 1);

-- Tipos de intervención
INSERT INTO tipo_intervencion (nombre, descripcion) VALUES
    ('Mantenimiento preventivo', 'Revisión programada según plan'),
    ('Mantenimiento correctivo', 'Reparación por falla'),
    ('Calibración', 'Ajuste y verificación metrológica'),
    ('Reparación mayor', 'Intervención con repuestos importantes');

-- Intervenciones
INSERT INTO intervencion (fecha, descripcion, costo, id_equipo, id_tipo_intervencion, id_usuario) VALUES
    ('2026-02-10', 'Revisión anual y limpieza interna', 150.00, 1, 1, 1),
    ('2026-09-28', 'Cambio de sensor de O2 y calibración', 480.50, 2, 3, 1);

-- Nota: el módulo Pacientes NO forma parte del alcance de este sistema.
