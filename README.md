# 🛒 Proyecto de Cátedra – NOVA-e  
*Desarrollo de Software para Móviles (DSM441)*  
Universidad Don Bosco – Facultad de Ingeniería

---

## 📌 Descripción del Proyecto

*NOVA-e* es un ecosistema de comercio electrónico móvil compuesto por dos aplicaciones complementarias:

- *Aplicación Cliente*: Permite a los usuarios navegar productos, gestionar su carrito, realizar compras, ver su historial de pedidos y administrar su perfil.
- *Aplicación Administrador*: Proporciona a los gestores herramientas para administrar productos, categorías, pedidos, métricas de negocio y usuarios.

Ambas aplicaciones se comunican con un *Backend REST monolítico* que actúa como fuente única de verdad, garantizando consistencia en los datos, seguridad mediante JWT y una arquitectura limpia basada en MVVM, Jetpack Compose, Retrofit, Hilt y Kotlin Coroutines.

🔗 [Backend REST Monolítico](https://github.com/EduardoRamirez86/ProyectoCatedraDWF)
---

## 👥 Integrantes del Equipo

| Nombre | Carné |
|--------|-------|
| Roberto Arturo Duarte Mejía | DM240115 |
| Eduardo Alfredo Ramírez Torres | RT240549 |
| José Fernando Rodríguez Escamilla | RE240134 |
| Dylan Alfonso Quintanilla Rivera | QR240095 |

---

## 📚 Documentación

Todos los manuales están disponibles en la siguiente carpeta de Google Drive:

🔗 [*Manuales del Proyecto NOVA-e*](https://drive.google.com/drive/folders/1rcdFGqt1BttXm7NBmDOcykLH7cHsk4bc?usp=drive_link)

Incluye:
- Manual Técnico Integral
- Manual de Usuario – Cliente
- Manual de Usuario – Administrador

---

## 🎨 Diseño de Interfaz (Mockups)

Los diseños de la interfaz de usuario fueron creados en Figma:

🎨 [*Prototipos en Figma*](https://www.figma.com/design/olHf6hKrmEJdNL5iTe3TWd/Proyecto-Catedra-DSM441)

---

## 🛠 Tecnologías Utilizadas

- *Lenguaje*: Kotlin
- *Arquitectura*: MVVM + Repository Pattern
- *UI*: Jetpack Compose
- *Red*: Retrofit + OkHttp + Gson
- *Inyección de Dependencias*: Hilt
- *Asincronía*: Kotlin Coroutines & Flow
- *Persistencia Local*: SharedPreferences (Cliente), memoria (Admin)
- *Autenticación*: JWT + AuthInterceptor
- *Backend*: Spring Boot (REST API)

---

## ✅ Funcionalidades Clave

### Cliente
- Autenticación segura (login/registro)
- Catálogo de productos con búsqueda
- Gestión de carrito y direcciones
- Checkout con geolocalización
- Pago atómico (Tarjeta, PayPal, Efectivo)
- Historial de pedidos y notificaciones

### Administrador
- CRUD completo de productos y categorías
- Gestión del ciclo de vida de pedidos
- Dashboard de métricas (ventas, productos más vendidos)
- Administración de usuarios
- Control de inventario

---

## 📦 Cómo Ejecutar el Proyecto

1. Clona este repositorio:
   ```bash

   git clone https://github.com/joseesca431/nova-e.git
1. Clona este repositorio(API):
   ```bash
   
   git clone https://github.com/EduardoRamirez86/ProyectoCatedraDWF.git




