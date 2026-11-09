# Sistema de Gestión Hospitalaria

`Codigo Asistido por IA generativa (con un tumor cerebral)`

Programa de escritorio en Java para llevar el inventario de equipos médicos de
un hospital: equipos, marcas, modelos, proveedores, instituciones,
ubicaciones, intervenciones, usuarios y perfiles. Cada usuario inicia sesión
y el sistema guarda un registro de sus acciones

## Cómo iniciarlo

Necesitás Java 21, Maven y MySQL o MariaDB instalados (sirve el MySQL de XAMPP).
La aplicación se conecta a `localhost:3306`, base `hospital`, usuario `root`
sin contraseña

1. Crear la base de datos (borra la base `hospital` si ya existe):

   ```bash
   mysql -u root -p < script.sql
   ```

   Opcional: cargar más datos de demostración (equipos, intervenciones y
   los usuarios `operador` / `operador123` y `tecnico` / `tecnico123`).
   Se corre una sola vez, justo después de `script.sql`:

   ```bash
   mysql -u root hospital < datos-demo.sql
   ```

2. Ejecutar el programa:

   ```bash
   mvn exec:java
   ```

3. Entrar con el usuario `admin` y la contraseña `admin123`

El usuario admin tiene acceso a todo. El perfil Operador solo puede ver
equipos e intervenciones. Las funcionalidades de cada perfil se eligen en
Seguridad > Perfiles y se aplican al volver a iniciar sesión

Si ya tenías la base creada con una versión anterior, volvé a correr
`script.sql` o agregá la funcionalidad de países a mano:

```sql
INSERT INTO funcionalidad (nombre, descripcion) VALUES ('Países', 'ABM de países');
INSERT INTO perfil_funcionalidad (id_perfil, id_funcionalidad)
    SELECT 1, id_funcionalidad FROM funcionalidad WHERE nombre = 'Países';
```

## Problemas comunes

- **"Usuario o contraseña incorrectos" con los datos correctos**: la app se
  está conectando a otra base. Si tenés MariaDB del sistema y XAMPP
  instalados, los dos usan el puerto 3306 y solo puede andar uno a la vez.
  Revisá cuál está prendido y que su base `hospital` sea la de `script.sql`
- **Las ventanas se ven en blanco** (sway, Hyprland, river, niri): Java
  necesita la variable `_JAVA_AWT_WM_NONREPARENTING=1`. Para dejarla fija:

  ```bash
  echo 'export _JAVA_AWT_WM_NONREPARENTING=1' >> ~/.zshrc
  ```

- **Usar otra base**: `mvn exec:java -Dhospital.db.url=jdbc:mysql://localhost:3306/otra_base`
  (también `-Dhospital.db.user` y `-Dhospital.db.password`)

## Cómo está organizado

- `dao/`: acceso a la base. `BaseDAO` tiene el código común (consultas,
  altas con auditoría, manejo de errores) y cada DAO solo define su SQL y
  cómo se arma el objeto
- `vista/`: pantallas Swing. `FrmBase` arma el formulario, la tabla, el
  buscador y los botones Nuevo/Guardar/Modificar/Eliminar. Cada pantalla
  declara sus campos y sus validaciones
- `modelo/`: clases de datos
- `script.sql` crea la base; `datos-demo.sql` carga datos de ejemplo

Para agregar un ABM nuevo: crear el DAO extendiendo `BaseDAO`, la pantalla
extendiendo `FrmBase` (ver `FrmMarca` como ejemplo), agregarla en
`Principal.crearFormulario` y cargar la funcionalidad en `script.sql`

## Cómo contribuir

1. Creá una rama con tu cambio: `git checkout -b mi-cambio`
2. Hacé los cambios y probá que todo funcione con `mvn test`
3. Hacé commit y abrí un pull request
