<div align="center">
  
# GestorDeArchivos 📂
  
Un gestor de archivos nativo de Android moderno, construido con Jetpack Compose y arquitectura MVVM para una práctica escolar.
  
<p align="center">
  <img alt="Kotlin" src="https://img.shields.io/badge/Kotlin-100%25-blue?style=for-the-badge&logo=kotlin"/>
  <img alt="Jetpack Compose" src="https://img.shields.io/badge/Jetpack-Compose-green?style=for-the-badge&logo=jetpackcompose"/>
  <img alt="Arquitectura" src="https://img.shields.io/badge/Arquitectura-MVVM-orange?style=for-the-badge"/>
</p>
  
</div>

---

## 📸 Capturas de Pantalla

| Navegación Principal | Visor de Imágenes (con Zoom) | Visor de Texto |
| :---: | :---: | :---: |
| ![Navegación](https://github.com/user-attachments/assets/efe8191b-6813-4fda-a78d-d7f0931a6efc) | ![Visor de Imagen](https://github.com/user-attachments/assets/c60d49e8-234b-4846-aa45-f554286aa499) | ![Visor de Texto](https://github.com/user-attachments/assets/f95405f7-540e-440d-a896-397b2576fdd8) |

| Menú de Opciones | Temas Personalizados | Modo Horizontal |
| :---: | :---: | :---: |
| ![Opciones](https://github.com/user-attachments/assets/8fd3adce-7553-4fd2-8021-e2f2eb0571e5) | ![Temas](https://github.com/user-attachments/assets/007fb07f-ff7d-4184-9a1c-f6b7fda806b4) | ![Horizontal](https://github.com/user-attachments/assets/d7c40e47-2b99-42b3-a522-271d04671af7) |

---

## ✨ Características (Features)

* **Navegación de archivos:** Explora directorios, sube de nivel y navega usando "migas de pan" (breadcrumbs).
* **Operaciones CRUD:**
    * **Crear** nuevas carpetas (No implementado en el reporte, pero es un CRUD estándar).
    * **Renombrar** archivos y carpetas.
    * **Borrar** archivos y carpetas (recursivamente).
* **Portapapeles:** Funcionalidad completa de **Copiar** y **Mover**.
* **Visores Integrados:**
    * **Visor de Imágenes:** Con soporte para zoom (pellizcar) y rotación.
    * **Visor de Texto:** Para leer archivos `.txt` y similares.
* **Integración con el Sistema:**
    * **Abrir con:** Lanza un intent `ACTION_VIEW` para tipos de archivo no soportados (PDF, etc.).
    * **Compartir:** Utiliza `FileProvider` para compartir archivos de forma segura.
* **Personalización:**
    * **Temas Dinámicos:** Cambia entre el tema "Guinda IPN" y "Azul ESCOM".
    * **Modo Oscuro:** Se adapta automáticamente al tema del sistema.
* **Manejo de Permisos:** Solicita y verifica el permiso `MANAGE_EXTERNAL_STORAGE` en Android 11+.

---

## 🛠️ Stack Técnico y Arquitectura

Esta aplicación está construida siguiendo las mejores prácticas modernas de desarrollo en Android.

* **UI:** 100% **Jetpack Compose** para una interfaz de usuario declarativa y reactiva.
* **Arquitectura:** **MVVM** (Model-View-ViewModel) para una clara separación de responsabilidades.
* **Gestión de Estado:** `ViewModels` (`FileViewModel`, `ThemeViewModel`) exponen el estado a la UI mediante `LiveData` y `StateFlow`.
* **Concurrencia:** **Corutinas de Kotlin** (`viewModelScope`, `Dispatchers.IO`) para todas las operaciones de disco, asegurando que la UI nunca se bloquee.
* **Navegación:** **Jetpack Navigation para Compose** para gestionar el flujo entre pantallas.
* **Persistencia:** **Jetpack DataStore** para guardar de forma asíncrona las preferencias de tema del usuario.
* **Carga de Imágenes:** **Coil** para cargar miniaturas y vistas previas de imágenes de forma eficiente.
* **Integración del Sistema:** **FileProvider** para el acceso seguro a URIs de contenido al compartir o abrir archivos.

---

## 🚀 Cómo Empezar (Getting Started)

Para compilar y ejecutar este proyecto localmente, sigue estos pasos:

### Prerrequisitos
* Android Studio (se recomienda la versión Iguana o superior)
* Emulador de Android o dispositivo físico con Android 11 (API 30) o superior.

### Instalación

1.  Clona el repositorio:
    ```sh
    git clone [https://github.com/tu-usuario/GestorDeArchivos.git](https://github.com/tu-usuario/GestorDeArchivos.git)
    ```
2.  Abre el proyecto en Android Studio.
3.  Espera a que Gradle sincronice las dependencias.
4.  Ejecuta la aplicación.

> **Importante:** Al ser un gestor de archivos, la app requiere el permiso de "Acceso a todos los archivos" (`MANAGE_EXTERNAL_STORAGE`). Deberás otorgar este permiso manualmente a través de los ajustes de la app en tu dispositivo o emulador la primera vez que la ejecutes.

---

## 📄 Documentación Detallada del Proyecto (Práctica Escolar)

El siguiente contenido es el reporte técnico detallado de la implementación, arquitectura y pruebas realizadas para la práctica escolar.

<details>
<summary>Haz clic para expandir la documentación técnica y las pruebas</summary>
  
### 🛠️ Stack Técnico y Arquitectura
La aplicación "GestorDeArchivos" está construida sobre un stack tecnológico moderno de Android, priorizando la separación de responsabilidades, la reactividad de la interfaz de usuario y la seguridad en el manejo de operaciones del sistema. La arquitectura central sigue el patrón MVVM (Model-View-ViewModel).

#### 1. Arquitectura y Componentes Centrales
La lógica de la aplicación se divide en las siguientes capas clave:

* **Capa de UI (Jetpack Compose):**
    * La interfaz de usuario es completamente declarativa, construida con Jetpack Compose. La actividad principal, `MainActivity`, actúa como el host.
    * **Gestión de Estado de UI:** El estado de la UI se gestiona de forma reactiva. Las pantallas (Composable functions) observan los datos expuestos por los ViewModels. Por ejemplo, `FileExplorerScreen` utiliza `observeAsState()` para reaccionar a los cambios en el `LiveData` del `FileViewModel`.
    * **Estado Efímero:** El estado local de la UI, como la visibilidad de un diálogo (`showOptionsSheet`) o el contenido de un campo de texto (`RenameFileDialog`), se maneja internamente en el Composable usando `rememberSaveable` y `remember { mutableStateOf(...) }`.

* **Capa de Lógica de UI (ViewModels):**
    * **FileViewModel:** Es el componente central de la lógica de negocio. Hereda de `AndroidViewModel` para obtener acceso al contexto de la aplicación y expone el estado a la UI a través de `LiveData` (`currentPath`, `filesList`). Contiene la lógica para todas las operaciones de archivos.
    * **ThemeViewModel:** Un ViewModel enfocado, responsable de gestionar el tema de la aplicación. Utiliza `StateFlow` para exponer el tema actual (`AppThemeType`) a la `MainActivity`.

* **Capa de Datos (Persistencia y Acceso a Archivos):**
    * **Sistema de Archivos:** La fuente de datos principal es el sistema de archivos del dispositivo, accedido vía `java.io.File`. Todas las llamadas se aíslan dentro del `FileViewModel`.
    * **Preferencias (DataStore):** Para la persistencia de los ajustes del usuario (tema), la aplicación utiliza Jetpack DataStore. El `ThemeViewModel` gestiona la lectura y escritura asíncrona de esta preferencia.

#### 2. Concurrencia y Manejo de Hilos
Para garantizar una UI fluida, se utiliza un modelo de concurrencia basado en Corutinas de Kotlin:

* **Operaciones I/O:** Todas las operaciones de disco (leer directorio, borrar, renombrar, etc.) se ejecutan en un contexto de `Dispatchers.IO`, lanzando una nueva corutina en el `viewModelScope` (`viewModelScope.launch(Dispatchers.IO)`).
* **Actualización de UI:** Tras finalizar la operación en segundo plano, la actualización de los `LiveData` se realiza de forma segura en el hilo principal usando `withContext(Dispatchers.Main)`.
* **Carga de Visores:** El `TextViewerScreen` utiliza un `LaunchedEffect` para disparar una corutina que lee el contenido del archivo en `Dispatchers.IO`, mostrando un indicador de carga mientras tanto.

#### 3. Navegación
La navegación es manejada por Jetpack Navigation para Compose.

* `AppNavigationHost.kt` define el `NavHost` y todas las rutas (`composable`).
* **Paso de Argumentos:** Para abrir un visor de texto o imagen, la ruta del archivo se pasa como un argumento de navegación. Para manejar de forma segura los caracteres especiales, la ruta se codifica en URL (`URLEncoder.encode`) antes de navegar y se decodifica (`URLDecoder.decode`) en la pantalla de destino.

#### 4. Integración con el Sistema Android
* **Permisos:** La aplicación solicita `MANAGE_EXTERNAL_STORAGE`, necesario para un gestor de archivos en Android 11+. La lógica para verificar (`Environment.isExternalStorageManager()`) y solicitar este permiso está encapsulada en `PermissionManager.kt`. `MainActivity` utiliza `rememberLauncherForActivityResult` para manejar el flujo.
* **Compartir y Abrir Archivos (FileProvider):**
    * Declarado en el `AndroidManifest.xml` y configurado en `provider_paths.xml`.
    * Las funciones `shareFile` y `openFileWithIntent` usan `FileProvider.getUriForFile(...)` para obtener una `content://` URI segura.
    * Se obtiene el tipo MIME (`MimeTypeHelper.kt`) y se lanza un `Intent` (`ACTION_SEND` o `ACTION_VIEW`) con la bandera `FLAG_GRANT_READ_URI_PERMISSION`.
* **Carga de Imágenes (Coil):**
    * La biblioteca Coil se utiliza para la carga eficiente de imágenes.
    * El `AsyncImage` se usa en `FileExplorerScreen` para miniaturas (`ContentScale.Crop`) y en `ImageViewerScreen` para la imagen completa (`ContentScale.Fit`).
* **Tematización:**
    * El archivo `Theme.kt` define esquemas de color personalizados (Guinda IPN y Azul ESCOM).
    * El composable `FileExplorerTheme` aplica el esquema de color correcto basándose en el `ThemeViewModel` y si el sistema está en modo oscuro (`isSystemInDarkTheme()`).

---

### 🚀 Implementación de Funcionalidades
La implementación de las funcionalidades clave se centra en el `FileViewModel`, asegurando que la lógica de I/O esté aislada de la UI y se ejecute de manera asíncrona.

* **Carga y Navegación de Directorios:**
    * `loadDirectory(path: String)`: Se invoca en `Dispatchers.IO`. Usa `directory.listFiles()` y luego ordena los resultados con una expresión `sortedWith` personalizada que prioriza carpetas y luego ordena alfabéticamente (`compareBy({ !it.isDirectory }, { it.name.lowercase() })`). El resultado se publica en `LiveData` usando `withContext(Dispatchers.Main)`.
    * `MapsUp()`: (Nombrada `MapsUp()` en el documento) Obtiene el `_currentPath.value`, encuentra su `parentFile` y llama a `loadDirectory` con esa nueva ruta.

* **Operaciones CRUD de Archivos:**
    * **Borrar (`deleteFile`):** En `Dispatchers.IO`, si `file.isDirectory` es verdadero, se invoca `file.deleteRecursively()` ; de lo contrario, se usa `file.delete()`. Luego se refresca el directorio.
    * **Renombrar (`renameFile`):** En `Dispatchers.IO`, valida que el nombre no esté vacío y comprueba si `newFile.exists()` para prevenir conflictos. Si es seguro, ejecuta `file.renameTo(newFile)` y refresca el directorio.

* **Leer (Visores de Archivos):**
    * **Texto:** `TextViewerScreen` lee el archivo con `file.readText()` en `Dispatchers.IO` (vía `LaunchedEffect`) y lo muestra en un `TextField` de solo lectura.
    * **Imagen:** `ImageViewerScreen` delega la carga a Coil (`AsyncImage`). Los gestos de zoom/paneo se manejan con `rememberTransformableState` y se aplican con el modificador `graphicsLayer`.
    * **Externo (Fallback):** Para otros tipos, `openFileWithIntent` usa un `Intent` de `ACTION_VIEW` con un `FileProvider`.

* **Implementación del Portapapeles (Copiar y Mover):**
    * Se utiliza un `LiveData` (`_clipboard`) que almacena un `Pair<File, ClipboardAction>`.
    * `copyFileToClipboard` / `moveFileToClipboard`: Simplemente actualizan el valor de `_clipboard`.
    * `pasteFile`: Se ejecuta en `Dispatchers.IO`. Lee el `_clipboard.value`, valida el destino y, basándose en la `action`:
        * `ClipboardAction.COPY`: Ejecuta `fileToPaste.copyRecursively(newFile)`.
        * `ClipboardAction.MOVE`: Ejecuta `fileToPaste.renameTo(newFile)`.
    * El portapapeles solo se limpia (`clearClipboard()`) si la acción fue MOVE, permitiendo múltiples pegados si se copió.

---

### 📋 Pruebas Realizadas
A continuación se documenta el plan de pruebas ejecutado en la aplicación, cubriendo la navegación, operaciones CRUD, visores y adaptación de la interfaz.

#### Pruebas de Navegación

| Caso de Prueba | Descripción | Resultado (Captura) |
| :--- | :--- | :--- |
| **1.1 Entrar** | En `FileExplorerScreen`, hacer clic en una carpeta (ej. "Download"). | Verificar que la lista se actualiza y las "migas de pan" (`FileBreadcrumbs`) también (ej. "Inicio > Download"). <br><br> ![image](https://github.com/user-attachments/assets/efe8191b-6813-4fda-a78d-d7f0931a6efc) |
| **1.2 Subir** | Desde "Download", presionar el icono de flecha "atrás" en la `TopAppBar`. | Verificar que se vuelve a la raíz ("Inicio"). <br><br> **Antes:** <br> ![image](https://github.com/user-attachments/assets/dc71a589-b6bd-4eaa-a3f4-9ade0d7960eb) <br> **Después:** <br> ![image](https://github.com/user-attachments/assets/116fb753-9223-478d-9140-c2a405f4e379) |
| **1.3 Migas de Pan** | Navegar a una subcarpeta (ej. "Inicio > Music > Recordings"). Hacer clic en "Inicio" en las migas de pan. | Verificar que se navega directamente a la raíz. <br><br> **Antes:** <br> ![image](https://github.com/user-attachments/assets/ead7b2b1-d798-4031-aca3-3ad862fcdcb3) <br> **Después:** <br> ![image](https://github.com/user-attachments/assets/8831cb0d-a4c6-499e-9fe2-f3c43ffcebd4) |

#### Pruebas de Operaciones (CRUD)

| Caso de Prueba | Descripción | Resultado (Captura) |
| :--- | :--- | :--- |
| **2.1 Renombrar** | Seleccionar un archivo, elegir "Renombrar", introducir un nombre válido y confirmar. | Verificar que el archivo aparece con el nuevo nombre. <br><br> **Paso 1:** <br> ![image](https://github.com/user-attachments/assets/8fd3adce-7553-4fd2-8021-e2f2eb0571e5) <br> **Paso 2:** <br> ![image](https://github.com/user-attachments/assets/9b9465ae-5f04-4fa1-8e9a-40487cf84c91) <br> **Paso 3:** <br> ![image](https://github.com/user-attachments/assets/ab9f2eb2-0217-4646-899e-43f083c57a55) |
| **2.2 Borrar Archivo** | Seleccionar un archivo, elegir "Borrar", y "Confirmar" en el diálogo. | Verificar que el archivo desaparece de la lista. <br><br> **Paso 1:** <br> ![image](https://github.com/user-attachments/assets/9e2edaff-0edf-4744-9b17-0ed174dc0110) <br> **Paso 2:** <br> ![image](https://github.com/user-attachments/assets/000de4e3-2132-44f3-b614-ebeef37a7a8e) <br> **Paso 3:** <br> ![image](https://github.com/user-attachments/assets/87d4d1dd-c79c-44f1-8f81-8089f7ae93d9) |
| **2.3 Borrar Carpeta** | Repetir Caso 2.2 con una carpeta que contenga archivos. | Verificar que la carpeta y todo su contenido se eliminan. <br><br> **Paso 1:** <br> ![image](https://github.com/user-attachments/assets/75f2625d-4633-49e5-ba7e-630eef080345) <br> **Paso 2:** <br> ![image](https://github.com/user-attachments/assets/e5a966f8-b336-4548-9285-73c480c191b9) <br> **Paso 3:** <br> ![image](https://github.com/user-attachments/assets/a4fde576-6939-4902-8ef7-dfe883300618) |
| **2.4 Copiar** | Seleccionar un archivo, elegir "Copiar", navegar a otra carpeta y presionar "Pegar". | Verificar que el archivo aparece en la nueva carpeta y también permanece en la original. <br><br> **Paso 1:** <br> ![image](https://github.com/user-attachments/assets/a396d1ea-f79f-45ac-92bb-1955e695b7bd) <br> **Paso 2:** <br> ![image](https://github.com/user-attachments/assets/2db5ff6a-4217-4556-996d-1b068a3b80f6) <br> **Paso 3:** <br> ![image](https://github.com/user-attachments/assets/f9d267ad-1290-4f45-b57e-24a30bac5165) |
| **2.5 Mover** | Seleccionar un archivo, elegir "Mover", navegar a otra carpeta y presionar "Pegar". | Verificar que el archivo aparece en la nueva carpeta y desaparece de la original. <br><br> **Paso 1:** <br> ![image](https://github.com/user-attachments/assets/2d0e8760-2f01-47c8-94d6-d47d675e6633) <br> **Paso 2:** <br> ![image](https://github.com/user-attachments/assets/6abe09bd-a45b-4fde-9294-301751960569) <br> **Paso 3:** <br> ![image](https://github.com/user-attachments/assets/e4253074-7c0f-4e02-9005-f4e58ed6ec7e) <br> **Paso 4:** <br> ![image](https://github.com/user-attachments/assets/70fcf2c7-9e5b-4391-b7fd-e978e0fafbb5) |

#### Pruebas de Visores

| Caso de Prueba | Descripción | Resultado (Captura) |
| :--- | :--- | :--- |
| **3.1 Texto** | Hacer clic en un archivo .txt. | Verificar que se abre `TextViewerScreen` y muestra el contenido. <br><br> **Antes:** <br> ![image](https://github.com/user-attachments/assets/fac3020b-dd61-4139-b7f0-bdf424652e65) <br> **Después:** <br> ![image](https://github.com/user-attachments/assets/f95405f7-540e-440d-a896-397b2576fdd8) |
| **3.2 Imagen** | Hacer clic en un archivo .jpg o .png. | Verificar que se abre `ImageViewerScreen`. <br><br> ![image](https://github.com/user-attachments/assets/11babf35-b525-4022-8bff-f66d25d2d90c) |
| **3.3 Zoom/Rotación** | En `ImageViewerScreen`, pellizcar para hacer zoom, arrastrar para mover y usar los botones de la `BottomAppBar` para rotar. | Verificar que la imagen responde correctamente. <br><br> **Zoom:** <br> ![image](https://github.com/user-attachments/assets/c60d49e8-234b-4846-aa45-f554286aa499) <br> **Rotar Izq:** <br> ![image](https://github.com/user-attachments/assets/0bcca61f-9c5d-436c-a48f-6988c928f677) <br> **Rotar Der:** <br> ![image](https://github.com/user-attachments/assets/efbe09c0-52ad-4cbf-bc56-a76396524e8a) |
| **3.4 Abrir con** | Hacer clic en un archivo no soportado (ej. .pdf). | Verificar que el sistema operativo muestra el diálogo "Abrir con...". <br><br> ![image](https.com/user-attachments/assets/b99aec0f-b0c3-47fe-87be-4c3e3c6e19b0) |

#### Pruebas de Interfaz (Multi-dispositivo)

| Caso de Prueba | Descripción | Resultado (Captura) |
| :--- | :--- | :--- |
| **4.1 Rotación** | Girar el dispositivo a modo horizontal. | Verificar que la UI se adapta y sigue siendo funcional. <br><br> ![image](https://github.com/user-attachments/assets/d7c40e47-2b99-42b3-a522-271d04671af7) |
| **4.2 Temas** | Ir a Ajustes, cambiar de "Tema Guinda" a "Tema Azul". | Verificar que los colores cambian y que la selección persiste al reiniciar la app. <br><br> **Paso 1:** <br> ![image](https://github.com/user-attachments/assets/06e05377-a248-4e9a-9094-2a8e7879b6c7) <br> **Paso 2:** <br> ![image](https://github.com/user-attachments/assets/28165729-1702-4b66-951a-6eba310649ba) <br> **Paso 3:** <br> ![image](https://github.com/user-attachments/assets/007fb07f-ff7d-4184-9a1c-f6b7fda806b4) |

---

### 💡 Conclusiones
La realización de la presente práctica ha representado una inmersión profunda y una aplicación integral de los conceptos que definen el desarrollo moderno de aplicaciones Android. Más allá de cumplir con el objetivo funcional de crear un gestor de archivos, esta práctica sirvió como un ejercicio cohesivo para ensamblar un stack tecnológico contemporáneo, enfrentando desafíos realistas del ecosistema de Android.

El pilar fundamental de la práctica fue la adopción de Jetpack Compose para la totalidad de la interfaz de usuario. Esta elección demostró ser un acierto , ya que la naturaleza declarativa de Compose simplificó drásticamente la gestión de estados complejos, como la visibilidad de diálogos y la actualización reactiva de la `LazyColumn` en `FileExplorerScreen`.

Arquitectónicamente, la adhesión estricta al patrón MVVM fue crucial. El `FileViewModel` se convirtió en el cerebro centralizado de toda la lógica de negocio. Al aislar las operaciones de I/O y la gestión del estado de la UI, la capa de Compose pudo mantenerse limpia, centrada únicamente en "renderizar" el estado que el ViewModel le proporcionaba.

El aprendizaje más significativo en el ámbito del rendimiento provino del uso de Corutinas de Kotlin. Cada operación de archivo, desde `listFiles()` hasta `deleteRecursively()`, se ejecutó dentro del `viewModelScope` en `Dispatchers.IO`. Esta práctica de sacar el trabajo pesado del hilo principal es mandatoria para evitar errores de "Aplicación no Responde" (ANR) y garantizar una experiencia de usuario fluida.

Los desafíos más complejos fueron de integración con las estrictas políticas de seguridad de Android. El manejo del permiso `MANAGE_EXTERNAL_STORAGE` y la implementación de las funciones "Compartir" y "Abrir con" forzaron una comprensión profunda del `FileProvider`, el uso de URIs `content://` y la gestión de banderas de permisos de URI (`FLAG_GRANT_READ_URI_PERMISSION`).

Finalmente, la práctica se enriqueció con la implementación de Jetpack DataStore en el `ThemeViewModel`. Esto no solo añadió una característica de personalización (temas Guinda y Azul), sino que también demostró la forma moderna y asíncrona de persistir datos simples, superando las limitaciones de las `SharedPreferences` tradicionales.

---

### 📚 Bibliografía
* Google. (2024a). Descripción general de ViewModel. Android Developers.
* Google. (2024b). Descripción general de los permisos de Android. Android Developers.
* Google. (2024c). Acceso a archivos desde el almacenamiento externo. Android Developers.
* Google. (2024d). Corutinas de Kotlin en Android. Android Developers.
* Google. (2024e). Jetpack DataStore. Android Developers.
  
</details>
