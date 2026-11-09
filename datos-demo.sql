USE hospital;

INSERT INTO perfil (nombre, descripcion, activo) VALUES
    ('Técnico', 'Mantenimiento de equipos biomédicos', TRUE);

INSERT INTO perfil_funcionalidad (id_perfil, id_funcionalidad)
SELECT p.id_perfil, f.id_funcionalidad
FROM perfil p
JOIN funcionalidad f ON f.nombre IN ('Equipos', 'Intervenciones', 'Tipos de Intervención')
WHERE p.nombre = 'Técnico';

INSERT INTO usuario
    (nombre, apellido, cedula, telefono, correo, usuario, contrasena, fecha_alta, activo, id_perfil)
SELECT 'Lucía', 'Fernández', '45123987', '099123456', 'lucia.fernandez@hospital.com', 'operador',
       '$2a$12$R17wDEWGQn9O2tSnbEV8QuoIycoauDj0EnIH4.FkS.VSaU4VYFZxe', '2025-03-01', TRUE, id_perfil
FROM perfil WHERE nombre = 'Operador';

INSERT INTO usuario
    (nombre, apellido, cedula, telefono, correo, usuario, contrasena, fecha_alta, activo, id_perfil)
SELECT 'Martín', 'Rodríguez', '38765432', '098765432', 'martin.rodriguez@hospital.com', 'tecnico',
       '$2a$12$gAW92x1C5ZwM7IDdaG0NC.3c4Tzw.d6fbFSHQX/oon9CEeOKhQ4k.', '2024-08-15', TRUE, id_perfil
FROM perfil WHERE nombre = 'Técnico';

INSERT INTO pais (nombre) VALUES ('Bolivia'), ('Perú');

INSERT INTO institucion (nombre, direccion, telefono, id_pais, activo)
SELECT 'Hospital Pereira Rossell', 'Bulevar Artigas 1550, Montevideo', '27087741', id_pais, TRUE
FROM pais WHERE nombre = 'Uruguay';

INSERT INTO institucion (nombre, direccion, telefono, id_pais, activo)
SELECT 'Hospital Italiano de Buenos Aires', 'Tte. Gral. Juan D. Perón 4190, CABA', '+54 11 4959-0200', id_pais, TRUE
FROM pais WHERE nombre = 'Argentina';

INSERT INTO ubicacion (nombre, descripcion, id_institucion)
SELECT u.nombre, u.descripcion, i.id_institucion
FROM (
    SELECT 'Quirófano 2' AS nombre, 'Bloque quirúrgico central' AS descripcion, 'Hospital de Clínicas' AS inst
    UNION ALL SELECT 'Emergencia', 'Puerta de emergencia adultos', 'Hospital de Clínicas'
    UNION ALL SELECT 'Neonatología', 'Cuidados intensivos neonatales', 'Hospital Pereira Rossell'
    UNION ALL SELECT 'Pediatría', 'Internación pediátrica, piso 3', 'Hospital Pereira Rossell'
    UNION ALL SELECT 'Imagenología', 'Ecografía y radiología', 'Sanatorio Americano'
    UNION ALL SELECT 'Unidad Coronaria', 'Cuidados coronarios', 'Hospital Italiano de Buenos Aires'
) u
JOIN institucion i ON i.nombre = u.inst;

INSERT INTO marca (nombre, descripcion, activo) VALUES
    ('Mindray', 'Monitoreo, ultrasonido y anestesia', TRUE),
    ('Zoll', 'Desfibriladores y reanimación', TRUE),
    ('B. Braun', 'Bombas de infusión', TRUE);

INSERT INTO modelo (nombre, id_marca, activo)
SELECT m.nombre, ma.id_marca, TRUE
FROM (
    SELECT 'BeneVision N12' AS nombre, 'Mindray' AS marca
    UNION ALL SELECT 'Resona I9', 'Mindray'
    UNION ALL SELECT 'R Series Plus', 'Zoll'
    UNION ALL SELECT 'Infusomat Space', 'B. Braun'
    UNION ALL SELECT 'Savina 300', 'Dräger'
    UNION ALL SELECT 'IntelliVue MX450', 'Philips'
) m
JOIN marca ma ON ma.nombre = m.marca;

INSERT INTO tipo_equipo (nombre, descripcion, activo) VALUES
    ('Electrocardiógrafo', 'Registro de actividad eléctrica cardíaca', TRUE),
    ('Incubadora', 'Soporte térmico neonatal', TRUE);

INSERT INTO proveedor (nombre, direccion, telefono, email, activo) VALUES
    ('MedTech Uruguay', 'Av. 18 de Julio 1820, Montevideo', '29001122', 'ventas@medtech.com.uy', TRUE),
    ('Insumos Hospitalarios SA', 'Ruta 8 km 17, Canelones', '22205566', 'contacto@insumoshosp.com.uy', TRUE);

INSERT INTO equipo
    (codigo, nombre, serie, fecha_compra, fecha_garantia, estado, observaciones,
     id_modelo, id_tipo, id_proveedor, id_ubicacion)
SELECT e.codigo, e.nombre, e.serie, e.compra, e.garantia, e.estado, e.obs,
       mo.id_modelo, t.id_tipo, pr.id_proveedor, ub.id_ubicacion
FROM (
    SELECT 'MSV-004' AS codigo, 'Monitor Quirófano 2' AS nombre, 'SN-MDR-88412' AS serie,
           DATE '2024-05-10' AS compra, DATE '2027-05-10' AS garantia, 'ACTIVO' AS estado,
           'Monitor con módulo de capnografía' AS obs,
           'BeneVision N12' AS modelo, 'Monitor de signos vitales' AS tipo, 'MedTech Uruguay' AS prov, 'Quirófano 2' AS ubic
    UNION ALL SELECT 'MSV-005', 'Monitor Emergencia 01', 'SN-PHI-55120', DATE '2023-09-01', DATE '2026-09-01', 'ACTIVO',
           'Garantía vencida, renovar contrato', 'IntelliVue MX450', 'Monitor de signos vitales', 'Bio-Médica SRL', 'Emergencia'
    UNION ALL SELECT 'DEF-001', 'Desfibrilador Emergencia', 'SN-ZOL-10077', DATE '2022-02-14', DATE '2025-02-14', 'ACTIVO',
           'Control mensual de baterías', 'R Series Plus', 'Desfibrilador', 'Equipos Médicos del Sur', 'Emergencia'
    UNION ALL SELECT 'DEF-002', 'Desfibrilador Unidad Coronaria', 'SN-ZOL-10342', DATE '2025-01-20', DATE '2028-01-20', 'ACTIVO',
           NULL, 'R Series Plus', 'Desfibrilador', 'Equipos Médicos del Sur', 'Unidad Coronaria'
    UNION ALL SELECT 'BIN-001', 'Bomba de infusión UCI 01', 'SN-BBR-70011', DATE '2024-11-05', DATE '2026-11-05', 'ACTIVO',
           NULL, 'Infusomat Space', 'Bomba de infusión', 'Insumos Hospitalarios SA', 'Cuidados Intensivos'
    UNION ALL SELECT 'BIN-002', 'Bomba de infusión UCI 02', 'SN-BBR-70012', DATE '2024-11-05', DATE '2026-11-05', 'MANTENIMIENTO',
           'Alarma de oclusión intermitente', 'Infusomat Space', 'Bomba de infusión', 'Insumos Hospitalarios SA', 'Cuidados Intensivos'
    UNION ALL SELECT 'BIN-003', 'Bomba de infusión Pediatría', 'SN-BBR-70450', DATE '2025-06-18', DATE '2027-06-18', 'ACTIVO',
           NULL, 'Infusomat Space', 'Bomba de infusión', 'Insumos Hospitalarios SA', 'Pediatría'
    UNION ALL SELECT 'VENT-003', 'Ventilador Neonatología', 'SN-DRA-33019', DATE '2023-04-22', DATE '2026-04-22', 'ACTIVO',
           'Circuito neonatal', 'Savina 300', 'Ventilador', 'TecnoSalud', 'Neonatología'
    UNION ALL SELECT 'ECO-004', 'Ecógrafo Imagenología', 'SN-MDR-41200', DATE '2025-03-03', DATE '2028-03-03', 'ACTIVO',
           'Sonda convexa y lineal', 'Resona I9', 'Ecógrafo', 'MedTech Uruguay', 'Imagenología'
    UNION ALL SELECT 'ECO-005', 'Ecógrafo de reserva', 'SN-SIE-90087', DATE '2019-07-30', DATE '2022-07-30', 'INACTIVO',
           'Fuera de servicio, pendiente de baja', 'Acuson X300', 'Ecógrafo', 'Bio-Médica SRL', 'Almacén central'
) e
JOIN modelo mo ON mo.nombre = e.modelo
JOIN tipo_equipo t ON t.nombre = e.tipo
JOIN proveedor pr ON pr.nombre = e.prov
JOIN ubicacion ub ON ub.nombre = e.ubic;

INSERT INTO tipo_intervencion (nombre, descripcion) VALUES
    ('Inspección de seguridad eléctrica', 'Ensayo según IEC 62353');

INSERT INTO intervencion (fecha, descripcion, costo, id_equipo, id_tipo_intervencion, id_usuario)
SELECT i.fecha, i.descripcion, i.costo, eq.id_equipo, ti.id_tipo_intervencion, us.id_usuario
FROM (
    SELECT DATE '2026-01-15' AS fecha, 'Mantenimiento preventivo semestral' AS descripcion, 120.00 AS costo,
           'MSV-004' AS equipo, 'Mantenimiento preventivo' AS tipo, 'tecnico' AS usr
    UNION ALL SELECT DATE '2026-03-02', 'Reemplazo de batería y prueba de descarga', 310.00,
           'DEF-001', 'Mantenimiento correctivo', 'tecnico'
    UNION ALL SELECT DATE '2026-04-20', 'Ensayo de seguridad eléctrica anual', 85.00,
           'DEF-001', 'Inspección de seguridad eléctrica', 'tecnico'
    UNION ALL SELECT DATE '2026-05-11', 'Calibración de sensor de flujo', 260.00,
           'VENT-003', 'Calibración', 'tecnico'
    UNION ALL SELECT DATE '2026-07-08', 'Cambio de placa de alimentación', 1450.00,
           'ECO-005', 'Reparación mayor', 'admin'
    UNION ALL SELECT DATE '2026-08-25', 'Verificación de volumen infundido', 95.00,
           'BIN-001', 'Calibración', 'tecnico'
    UNION ALL SELECT DATE '2026-10-30', 'Diagnóstico por alarma de oclusión', 0.00,
           'BIN-002', 'Mantenimiento correctivo', 'operador'
    UNION ALL SELECT DATE '2026-11-03', 'Revisión de sondas y actualización de software', 180.00,
           'ECO-004', 'Mantenimiento preventivo', 'tecnico'
) i
JOIN equipo eq ON eq.codigo = i.equipo
JOIN tipo_intervencion ti ON ti.nombre = i.tipo
JOIN usuario us ON us.usuario = i.usr;
