-- Base de datos del proyecto de citas medicas
CREATE DATABASE IF NOT EXISTS citas_medicas CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
USE citas_medicas;

SET FOREIGN_KEY_CHECKS = 0;
DROP TABLE IF EXISTS cita;
DROP TABLE IF EXISTS medico_especialidad;
DROP TABLE IF EXISTS medico;
DROP TABLE IF EXISTS paciente;
DROP TABLE IF EXISTS especialidad;
DROP TABLE IF EXISTS usuario;
SET FOREIGN_KEY_CHECKS = 1;

CREATE TABLE usuario (
  id INT AUTO_INCREMENT PRIMARY KEY,
  username VARCHAR(50) NOT NULL UNIQUE,
  password VARCHAR(64) NOT NULL,            -- sha256 en hex
  nombre_completo VARCHAR(100) NOT NULL,
  rol VARCHAR(20) NOT NULL DEFAULT 'ADMIN',
  activo TINYINT(1) NOT NULL DEFAULT 1
) ENGINE=InnoDB;

CREATE TABLE especialidad (
  id INT AUTO_INCREMENT PRIMARY KEY,
  nombre VARCHAR(80) NOT NULL UNIQUE,
  descripcion VARCHAR(255)
) ENGINE=InnoDB;

CREATE TABLE paciente (
  id INT AUTO_INCREMENT PRIMARY KEY,
  nombre VARCHAR(60) NOT NULL,
  apellido VARCHAR(60) NOT NULL,
  dui CHAR(10) NOT NULL UNIQUE,             -- formato 00000000-0
  telefono VARCHAR(15),
  email VARCHAR(100),
  fecha_nacimiento DATE,
  direccion VARCHAR(200)
) ENGINE=InnoDB;

CREATE TABLE medico (
  id INT AUTO_INCREMENT PRIMARY KEY,
  nombre VARCHAR(60) NOT NULL,
  apellido VARCHAR(60) NOT NULL,
  jvpm VARCHAR(20) NOT NULL UNIQUE,         -- numero de inscripcion
  telefono VARCHAR(15),
  email VARCHAR(100),
  activo TINYINT(1) NOT NULL DEFAULT 1
) ENGINE=InnoDB;

-- relacion N:M entre medico y especialidad
CREATE TABLE medico_especialidad (
  id_medico INT NOT NULL,
  id_especialidad INT NOT NULL,
  PRIMARY KEY (id_medico, id_especialidad),
  CONSTRAINT fk_me_medico FOREIGN KEY (id_medico) REFERENCES medico(id) ON DELETE CASCADE,
  CONSTRAINT fk_me_esp FOREIGN KEY (id_especialidad) REFERENCES especialidad(id) ON DELETE RESTRICT
) ENGINE=InnoDB;

CREATE TABLE cita (
  id INT AUTO_INCREMENT PRIMARY KEY,
  id_paciente INT NOT NULL,
  id_medico INT NOT NULL,
  fecha DATE NOT NULL,
  hora TIME NOT NULL,
  estado ENUM('PENDIENTE','CONFIRMADA','ATENDIDA','CANCELADA') NOT NULL DEFAULT 'PENDIENTE',
  motivo VARCHAR(255),
  CONSTRAINT fk_cita_paciente FOREIGN KEY (id_paciente) REFERENCES paciente(id) ON DELETE RESTRICT,
  CONSTRAINT fk_cita_medico FOREIGN KEY (id_medico) REFERENCES medico(id) ON DELETE RESTRICT,
  -- un medico no puede tener dos citas a la misma hora
  CONSTRAINT uq_medico_fecha_hora UNIQUE (id_medico, fecha, hora)
) ENGINE=InnoDB;

-- ===== datos de prueba =====

-- admin / admin123  y  recepcion / recep123
INSERT INTO usuario (username, password, nombre_completo, rol) VALUES
('admin', SHA2('admin123', 256), 'Administrador del sistema', 'ADMIN'),
('recepcion', SHA2('recep123', 256), 'Recepción general', 'RECEPCION');

INSERT INTO especialidad (nombre, descripcion) VALUES
('Medicina General', 'Consulta general y control de rutina'),
('Pediatría', 'Atención de niños y adolescentes'),
('Cardiología', 'Enfermedades del corazón y sistema circulatorio'),
('Dermatología', 'Enfermedades de la piel, cabello y uñas'),
('Ginecología', 'Salud de la mujer y control prenatal'),
('Traumatología', 'Lesiones de huesos, músculos y articulaciones'),
('Oftalmología', 'Salud visual y enfermedades de los ojos'),
('Nutrición', 'Planes alimenticios y control de peso');

INSERT INTO paciente (nombre, apellido, dui, telefono, email, fecha_nacimiento, direccion) VALUES
('Carlos', 'Martínez López', '04567891-2', '7123-4567', 'carlos.martinez@mail.com', '1990-03-14', 'Col. Escalón, San Salvador'),
('Ana', 'Gómez Rivas', '03456712-8', '7234-5678', 'ana.gomez@mail.com', '1985-07-22', 'Soyapango, San Salvador'),
('Luis', 'Hernández Cruz', '05123489-0', '7345-6789', 'luis.hernandez@mail.com', '2001-11-05', 'Santa Tecla, La Libertad'),
('María', 'Flores Peña', '02987654-3', '7456-7890', 'maria.flores@mail.com', '1978-01-30', 'Apopa, San Salvador'),
('José', 'Ramírez Ortiz', '06234578-9', '7567-8901', 'jose.ramirez@mail.com', '1995-09-18', 'Mejicanos, San Salvador'),
('Sofía', 'Castillo Mejía', '07345612-4', '7678-9012', 'sofia.castillo@mail.com', '2015-05-09', 'Ilopango, San Salvador'),
('Roberto', 'Alvarado Núñez', '01876543-5', '7789-0123', 'roberto.alvarado@mail.com', '1969-12-02', 'Antiguo Cuscatlán, La Libertad'),
('Daniela', 'Portillo Rauda', '08765432-1', '7890-1234', 'daniela.portillo@mail.com', '1999-04-27', 'San Miguel');

INSERT INTO medico (nombre, apellido, jvpm, telefono, email) VALUES
('Ricardo', 'Menjívar Salazar', 'JVPM-10234', '2222-1001', 'rmenjivar@clinica.com'),
('Patricia', 'Aguilar Funes', 'JVPM-10587', '2222-1002', 'paguilar@clinica.com'),
('Fernando', 'Quintanilla Rosales', 'JVPM-11023', '2222-1003', 'fquintanilla@clinica.com'),
('Lucía', 'Barrera Henríquez', 'JVPM-11456', '2222-1004', 'lbarrera@clinica.com'),
('Mauricio', 'Cáceres Linares', 'JVPM-12078', '2222-1005', 'mcaceres@clinica.com'),
('Elena', 'Valencia Mora', 'JVPM-12341', '2222-1006', 'evalencia@clinica.com');

-- algunos medicos tienen mas de una especialidad
INSERT INTO medico_especialidad (id_medico, id_especialidad) VALUES
(1, 1), (1, 3),
(2, 2),
(3, 3),
(4, 4), (4, 5),
(5, 6),
(6, 7), (6, 8);

INSERT INTO cita (id_paciente, id_medico, fecha, hora, estado, motivo) VALUES
(1, 1, '2026-10-26', '08:00:00', 'CONFIRMADA', 'Chequeo general'),
(2, 4, '2026-10-26', '09:30:00', 'PENDIENTE', 'Revisión de lunar en la espalda'),
(3, 5, '2026-10-27', '10:00:00', 'CONFIRMADA', 'Dolor de rodilla'),
(4, 3, '2026-10-27', '11:00:00', 'PENDIENTE', 'Control de presión arterial'),
(5, 1, '2026-10-28', '08:30:00', 'PENDIENTE', 'Gripe y fiebre'),
(6, 2, '2026-10-28', '14:00:00', 'CONFIRMADA', 'Control niño sano'),
(7, 3, '2026-10-29', '09:00:00', 'CANCELADA', 'Electrocardiograma'),
(8, 6, '2026-10-29', '15:30:00', 'PENDIENTE', 'Examen de la vista'),
(1, 3, '2026-10-20', '10:30:00', 'ATENDIDA', 'Palpitaciones'),
(2, 1, '2026-10-21', '16:00:00', 'ATENDIDA', 'Dolor de cabeza frecuente');
