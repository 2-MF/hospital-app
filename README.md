# Sistema de Gestión Hospitalaria

`Codigo Asistido por IA generativa (con un tumor cerebral)`

Programa de escritorio en Java para llevar el inventario de equipos médicos de
un hospital: equipos, marcas, modelos, proveedores, instituciones,
ubicaciones, intervenciones, usuarios y perfiles. Cada usuario inicia sesión
y el sistema guarda un registro de sus acciones

## Cómo iniciarlo

Necesitás Java 21, Maven y MySQL o MariaDB instalados

1. Crear la base de datos (borra la base `hospital` si ya existe):

   ```bash
   mysql -u root -p < script.sql
   ```

2. Ejecutar el programa:

   ```bash
   mvn exec:java
   ```

3. Entrar con el usuario `admin` y la contraseña `admin123`

El usuario admin tiene acceso a todo. El perfil Operador solo puede ver
equipos e intervenciones

## Cómo contribuir

1. Creá una rama con tu cambio: `git checkout -b mi-cambio`
2. Hacé los cambios y probá que todo funcione con `mvn test`
3. Hacé commit y abrí un pull request
