# Plan de Pruebas

## 1. Introducción

Este documento describe el plan de pruebas para el proyecto **End Of Line** desarrollado en el marco de la asignatura **Diseño y Pruebas 1** por el grupo **L4-02**. El objetivo del plan de pruebas es garantizar que el software desarrollado cumple con los requisitos especificados en las historias de usuario y que se han realizado las pruebas necesarias para validar su funcionamiento.

## 2. Alcance

El alcance de este plan de pruebas incluye:

- Pruebas unitarias.
  - Pruebas unitarias de backend incluyendo pruebas servicios o repositorios
  - Pruebas unitarias de frontend: pruebas de las funciones javascript creadas en frontend.
  - Pruebas unitarias de interfaz de usuario. Usan la interfaz de  usuario de nuestros componentes frontend.
- Pruebas de integración.  En nuestro caso principalmente son pruebas de controladores.

## 3. Estrategia de Pruebas

### 3.1 Tipos de Pruebas

#### 3.1.1 Pruebas Unitarias
Las pruebas unitarias se realizarán para verificar el correcto funcionamiento de los componentes individuales del software. Se utilizarán herramientas de automatización de pruebas como **JUnit** en background y .

#### 3.1.2 Pruebas de Integración
Las pruebas de integración se enfocarán en evaluar la interacción entre los distintos módulos o componentes del sistema, nosotros las realizaremos a nivel de API, probando nuestros controladores Spring.

## 4. Herramientas y Entorno de Pruebas

### 4.1 Herramientas
- **Maven**: Gestión de dependencias y ejecución de las pruebas.
- **JUnit**: Framework de pruebas unitarias.
- **Jacoco**: Generación de informes de cobertura de código.
- **Jest**: Framework para pruebas unitarias en javascript.
- **React-test**: Liberaría para la creación de pruebas unitarias de componentes React.

### 4.2 Entorno de Pruebas
Las pruebas se ejecutarán en el entorno de desarrollo y, eventualmente, en el entorno de pruebas del servidor de integración continua.

## 5. Planificación de Pruebas

### 5.1 Cobertura de Pruebas

El informe de cobertura de pruebas se puede consultar [aquí](/target/site/jacoco/index.html)

### 5.2 Matriz de Trazabilidad

| Historia de Usuario | Prueba | Descripción | Estado | Tipo |
|---------------------|--------|-------------|--------|------|
| #HU-1 | [GA: GameRestControllerTests](/src/test/java/es/us/dp1/lx_xy_24_25/your_game_name/game/GameRestControllerTests.java) |  | Implementada |  |
| #HU-1 | [GA: GameService1Tests](/src/test/java/es/us/dp1/lx_xy_24_25/your_game_name/game/GameService1Tests.java) |  | Implementada |  |
| #HU-1 | [GA: GameService2Tests](/src/test/java/es/us/dp1/lx_xy_24_25/your_game_name/game/GameService2Tests.java) |  | Implementada |  |
| #HU-2 | [GA: GameRestControllerTests](/src/test/java/es/us/dp1/lx_xy_24_25/your_game_name/game/GameRestControllerTests.java) |  | Implementada |  |
| #HU-2 | [GA: GameService1Tests](/src/test/java/es/us/dp1/lx_xy_24_25/your_game_name/game/GameService1Tests.java) |  | Implementada |  |
| #HU-2 | [GA: GameService2Tests](/src/test/java/es/us/dp1/lx_xy_24_25/your_game_name/game/GameService2Tests.java) |  | Implementada |  |
| #HU-3 | [GA: GameRestControllerTests](/src/test/java/es/us/dp1/lx_xy_24_25/your_game_name/game/GameRestControllerTests.java) |  | Implementada |  |
| #HU-3 | [GA: GameService1Tests](/src/test/java/es/us/dp1/lx_xy_24_25/your_game_name/game/GameService1Tests.java) |  | Implementada |  |
| #HU-3 | [GA: GameService2Tests](/src/test/java/es/us/dp1/lx_xy_24_25/your_game_name/game/GameService2Tests.java) |  | Implementada |  |
| #HU-3 | [US: UserControllerTests](/src/test/java/es/us/dp1/lx_xy_24_25/your_game_name/user/UserControllerTests.java) |  | Implementada |  |
| #HU-3 | [US: UserServiceTests](/src/test/java/es/us/dp1/lx_xy_24_25/your_game_name/user/UserServiceTests.java) |  | Implementada |  |
| #HU-4 | [AU: AuthControllerTests](/src/test/java/es/us/dp1/lx_xy_24_25/your_game_name/auth/AuthControllerTests.java) |  | Implementada |  |
| #HU-4 | [AU: AuthServiceTests](/src/test/java/es/us/dp1/lx_xy_24_25/your_game_name/auth/AuthServiceTests.java) |  | Implementada |  |
| #HU-4 | [US: AuthoritiesServiceTests](/src/test/java/es/us/dp1/lx_xy_24_25/your_game_name/user/AuthoritiesServiceTests.java) |  | Implementada |  |
| #HU-4 | [US: UserControllerTests](/src/test/java/es/us/dp1/lx_xy_24_25/your_game_name/user/UserControllerTests.java) |  | Implementada |  |
| #HU-4 | [US: UserServiceTests](/src/test/java/es/us/dp1/lx_xy_24_25/your_game_name/user/UserServiceTests.java) |  | Implementada |  |
| #HU-5 | [US: UserControllerTests](/src/test/java/es/us/dp1/lx_xy_24_25/your_game_name/user/UserControllerTests.java) |  | Implementada |  |
| #HU-5 | [US: UserServiceTests](/src/test/java/es/us/dp1/lx_xy_24_25/your_game_name/user/UserServiceTests.java) |  | Implementada |  |
| #HU-6 | [DE: DevelopersControllerTests](/src/test/java/es/us/dp1/lx_xy_24_25/your_game_name/developers/DevelopersControllerTests.java) |  | Implementada |  |
| #HU-6 | [US: UserControllerTests](/src/test/java/es/us/dp1/lx_xy_24_25/your_game_name/user/UserControllerTests.java) |  | Implementada |  |
| #HU-6 | [US: UserServiceTests](/src/test/java/es/us/dp1/lx_xy_24_25/your_game_name/user/UserServiceTests.java) |  | Implementada |  |
| #HU-7 | [US: UserControllerTests](/src/test/java/es/us/dp1/lx_xy_24_25/your_game_name/user/UserControllerTests.java) |  | Implementada |  |
| #HU-7 | [US: UserServiceTests](/src/test/java/es/us/dp1/lx_xy_24_25/your_game_name/user/UserServiceTests.java) |  | Implementada |  |
| #HU-8 | [GA: GameRestControllerTests](/src/test/java/es/us/dp1/lx_xy_24_25/your_game_name/game/GameRestControllerTests.java) |  | Implementada |  |
| #HU-8 | [GA: GameService1Tests](/src/test/java/es/us/dp1/lx_xy_24_25/your_game_name/game/GameService1Tests.java) |  | Implementada |  |
| #HU-8 | [GA: GameService2Tests](/src/test/java/es/us/dp1/lx_xy_24_25/your_game_name/game/GameService2Tests.java) |  | Implementada |  |
| #HU-8 | [NO: NotificationControllerTests](/src/test/java/es/us/dp1/lx_xy_24_25/your_game_name/notifications/NotificationControllerTests.java) |  | Implementada |  |
| #HU-8 | [NO: NotificationServiceTests](/src/test/java/es/us/dp1/lx_xy_24_25/your_game_name/notifications/NotificationServiceTests.java) |  | Implementada |  |
| #HU-8 | [NO: WSNotificationControllerTests](/src/test/java/es/us/dp1/lx_xy_24_25/your_game_name/notifications/WSNotificationControllerTests.java) |  | Implementada |  |
| #HU-9 | [NO: NotificationControllerTests](/src/test/java/es/us/dp1/lx_xy_24_25/your_game_name/notifications/NotificationControllerTests.java) |  | Implementada |  |
| #HU-9 | [NO: NotificationServiceTests](/src/test/java/es/us/dp1/lx_xy_24_25/your_game_name/notifications/NotificationServiceTests.java) |  | Implementada |  |
| #HU-9 | [NO: WSNotificationControllerTests](/src/test/java/es/us/dp1/lx_xy_24_25/your_game_name/notifications/WSNotificationControllerTests.java) |  | Implementada |  |
| #HU-10 | [GA: GameRestControllerTests](/src/test/java/es/us/dp1/lx_xy_24_25/your_game_name/game/GameRestControllerTests.java) |  | Implementada |  |
| #HU-10 | [GA: GameService1Tests](/src/test/java/es/us/dp1/lx_xy_24_25/your_game_name/game/GameService1Tests.java) |  | Implementada |  |
| #HU-10 | [GA: GameService2Tests](/src/test/java/es/us/dp1/lx_xy_24_25/your_game_name/game/GameService2Tests.java) |  | Implementada |  |
| #HU-10 | [GA: WSChatControllerTests](/src/test/java/es/us/dp1/lx_xy_24_25/your_game_name/game/WSChatControllerTests.java) |  | Implementada |  |
| #HU-11 | [NO: NotificationControllerTests](/src/test/java/es/us/dp1/lx_xy_24_25/your_game_name/notifications/NotificationControllerTests.java) |  | Implementada |  |
| #HU-11 | [NO: NotificationServiceTests](/src/test/java/es/us/dp1/lx_xy_24_25/your_game_name/notifications/NotificationServiceTests.java) |  | Implementada |  |
| #HU-11 | [NO: WSNotificationControllerTests](/src/test/java/es/us/dp1/lx_xy_24_25/your_game_name/notifications/WSNotificationControllerTests.java) |  | Implementada |  |
| #HU-12 | [ST: StatisticsServiceTests](/src/test/java/es/us/dp1/lx_xy_24_25/your_game_name/statistics/StatisticsServiceTests.java) |  | Implementada |  |
| #HU-13 | [ST: StatisticsServiceTests](/src/test/java/es/us/dp1/lx_xy_24_25/your_game_name/statistics/StatisticsServiceTests.java) |  | Implementada |  |
| #HU-14 | [ST: StatisticsServiceTests](/src/test/java/es/us/dp1/lx_xy_24_25/your_game_name/statistics/StatisticsServiceTests.java) |  | Implementada |  |
| #HU-15 | [ST: StatisticsServiceTests](/src/test/java/es/us/dp1/lx_xy_24_25/your_game_name/statistics/StatisticsServiceTests.java) |  | Implementada |  |
| #HU-16 | [ST: StatisticsServiceTests](/src/test/java/es/us/dp1/lx_xy_24_25/your_game_name/statistics/StatisticsServiceTests.java) |  | Implementada |  |
| #HU-17 | [ST: StatisticsServiceTests](/src/test/java/es/us/dp1/lx_xy_24_25/your_game_name/statistics/StatisticsServiceTests.java) |  | Implementada |  |
| #HU-18 | [AC: AchievementControllerTests](/src/test/java/es/us/dp1/lx_xy_24_25/your_game_name/achievements/AchievementControllerTests.java) |  | Implementada |  |
| #HU-18 | [AC: AchievementServiceTests](/src/test/java/es/us/dp1/lx_xy_24_25/your_game_name/achievements/AchievementServiceTests.java) |  | Implementada |  |
| #HU-19 | [AC: AchievementControllerTests](/src/test/java/es/us/dp1/lx_xy_24_25/your_game_name/achievements/AchievementControllerTests.java) |  | Implementada |  |
| #HU-19 | [AC: AchievementServiceTests](/src/test/java/es/us/dp1/lx_xy_24_25/your_game_name/achievements/AchievementServiceTests.java) |  | Implementada |  |
| #HU-20 | [GA: GameRestControllerTests](/src/test/java/es/us/dp1/lx_xy_24_25/your_game_name/game/GameRestControllerTests.java) |  | Implementada |  |
| #HU-20 | [GA: GameService1Tests](/src/test/java/es/us/dp1/lx_xy_24_25/your_game_name/game/GameService1Tests.java) |  | Implementada |  |
| #HU-20 | [GA: GameService2Tests](/src/test/java/es/us/dp1/lx_xy_24_25/your_game_name/game/GameService2Tests.java) |  | Implementada |  |
| #HU-21 | [CA: CardServiceTests](/src/test/java/es/us/dp1/lx_xy_24_25/your_game_name/card/CardServiceTests.java) |  | Implementada |  |
| #HU-21 | [GA: GameRestControllerTests](/src/test/java/es/us/dp1/lx_xy_24_25/your_game_name/game/GameRestControllerTests.java) |  | Implementada |  |
| #HU-21 | [GA: GameService1Tests](/src/test/java/es/us/dp1/lx_xy_24_25/your_game_name/game/GameService1Tests.java) |  | Implementada |  |
| #HU-21 | [GA: GameService2Tests](/src/test/java/es/us/dp1/lx_xy_24_25/your_game_name/game/GameService2Tests.java) |  | Implementada |  |
| #HU-21 | [HA: HandServiceTests](/src/test/java/es/us/dp1/lx_xy_24_25/your_game_name/hand/HandServiceTests.java) |  | Implementada |  |
| #HU-21 | [MO: ValidatorTests](/src/test/java/es/us/dp1/lx_xy_24_25/your_game_name/model/ValidatorTests.java) |  | Implementada |  |
| #HU-21 | [PA: PackCardServiceTests](/src/test/java/es/us/dp1/lx_xy_24_25/your_game_name/packCard/PackCardServiceTests.java) |  | Implementada |  |
| #HU-21 | [PL: PlayerServiceTests](/src/test/java/es/us/dp1/lx_xy_24_25/your_game_name/player/PlayerServiceTests.java) |  | Implementada |  |
| #HU-21 | [TA: CellServiceTests](/src/test/java/es/us/dp1/lx_xy_24_25/your_game_name/tableCard/CellServiceTests.java) |  | Implementada |  |
| #HU-21 | [TA: RowServiceTests](/src/test/java/es/us/dp1/lx_xy_24_25/your_game_name/tableCard/RowServiceTests.java) |  | Implementada |  |
| #HU-21 | [TA: TableCardServiceTests](/src/test/java/es/us/dp1/lx_xy_24_25/your_game_name/tableCard/TableCardServiceTests.java) |  | Implementada |  |
| #HU-21 | [TE: TeamServiceTests](/src/test/java/es/us/dp1/lx_xy_24_25/your_game_name/team/TeamServiceTests.java) |  | Implementada |  |
| #HU-22 | [CA: CardServiceTests](/src/test/java/es/us/dp1/lx_xy_24_25/your_game_name/card/CardServiceTests.java) |  | Implementada |  |
| #HU-22 | [GA: GameRestControllerTests](/src/test/java/es/us/dp1/lx_xy_24_25/your_game_name/game/GameRestControllerTests.java) |  | Implementada |  |
| #HU-22 | [GA: GameService1Tests](/src/test/java/es/us/dp1/lx_xy_24_25/your_game_name/game/GameService1Tests.java) |  | Implementada |  |
| #HU-22 | [GA: GameService2Tests](/src/test/java/es/us/dp1/lx_xy_24_25/your_game_name/game/GameService2Tests.java) |  | Implementada |  |
| #HU-22 | [HA: HandServiceTests](/src/test/java/es/us/dp1/lx_xy_24_25/your_game_name/hand/HandServiceTests.java) |  | Implementada |  |
| #HU-22 | [MO: ValidatorTests](/src/test/java/es/us/dp1/lx_xy_24_25/your_game_name/model/ValidatorTests.java) |  | Implementada |  |
| #HU-22 | [PA: PackCardServiceTests](/src/test/java/es/us/dp1/lx_xy_24_25/your_game_name/packCard/PackCardServiceTests.java) |  | Implementada |  |
| #HU-22 | [PL: PlayerServiceTests](/src/test/java/es/us/dp1/lx_xy_24_25/your_game_name/player/PlayerServiceTests.java) |  | Implementada |  |
| #HU-22 | [TA: CellServiceTests](/src/test/java/es/us/dp1/lx_xy_24_25/your_game_name/tableCard/CellServiceTests.java) |  | Implementada |  |
| #HU-22 | [TA: RowServiceTests](/src/test/java/es/us/dp1/lx_xy_24_25/your_game_name/tableCard/RowServiceTests.java) |  | Implementada |  |
| #HU-22 | [TA: TableCardServiceTests](/src/test/java/es/us/dp1/lx_xy_24_25/your_game_name/tableCard/TableCardServiceTests.java) |  | Implementada |  |
| #HU-22 | [TE: TeamServiceTests](/src/test/java/es/us/dp1/lx_xy_24_25/your_game_name/team/TeamServiceTests.java) |  | Implementada |  |

### 5.3 Matriz de Trazabilidad entre Pruebas e Historias de Usuario

| Prueba                                | HU-01 | HU-02 | HU-03 | HU-04 | HU-05 | HU-06 | HU-07 | HU-08 | HU-09 | HU-10 | HU-11 | HU-12 | HU-13 | HU-14 | HU-15 | HU-16 | HU-17 | HU-18 | HU-19 | HU-20 | HU-21 | HU-22 |
|---------------------------------------|-------|-------|-------|-------|-------|-------|-------|-------|-------|-------|-------|-------|-------|-------|-------|-------|-------|-------|-------|-------|-------|-------|
| GA: GameRestControllerTests           |   X   |   X   |   X   |       |       |       |       |   X   |       |   X   |       |       |       |       |       |       |       |       |   X   |   X   |   X   |       |
| GA: GameService1Tests                 |   X   |   X   |   X   |       |       |       |       |   X   |       |   X   |       |       |       |       |       |       |       |       |   X   |   X   |   X   |       |
| GA: GameService2Tests                 |   X   |   X   |   X   |       |       |       |       |   X   |       |   X   |       |       |       |       |       |       |       |       |   X   |   X   |   X   |       |
| US: UserControllerTests               |       |       |   X   |   X   |   X   |   X   |   X   |       |       |       |       |       |       |       |       |       |       |       |       |   X   |       |       |
| US: UserServiceTests                  |       |       |   X   |   X   |   X   |   X   |   X   |       |       |       |       |       |       |       |       |       |       |       |       |   X   |       |       |
| AU: AuthControllerTests               |       |       |       |   X   |       |       |       |       |       |       |       |       |       |       |       |       |       |       |       |       |       |       |
| AU: AuthServiceTests                  |       |       |       |   X   |       |       |       |       |       |       |       |       |       |       |       |       |       |       |       |       |       |       |
| US: AuthoritiesServiceTests           |       |       |       |   X   |       |       |       |       |       |       |       |       |       |       |       |       |       |       |       |       |       |       |
| DE: DevelopersControllerTests         |       |       |       |       |       |   X   |       |       |       |       |       |       |       |       |       |       |       |       |       |       |       |       |
| NO: NotificationControllerTests       |       |       |       |       |       |       |       |   X   |   X   |       |   X   |       |       |       |       |       |       |       |       |       |       |       |
| NO: NotificationServiceTests          |       |       |       |       |       |       |       |   X   |   X   |       |   X   |       |       |       |       |       |       |       |       |       |       |       |
| NO: WSNotificationControllerTests     |       |       |       |       |       |       |       |   X   |   X   |       |   X   |       |       |       |       |       |       |       |       |       |       |       |
| GA: WSChatControllerTests             |       |       |       |       |       |       |       |       |       |   X   |       |       |       |       |       |       |       |       |       |       |       |       |
| ST: StatisticsServiceTests            |       |       |       |       |       |       |       |       |       |       |       |   X   |   X   |   X   |   X   |   X   |   X   |       |       |       |       |       |
| AC: AchievementControllerTests        |       |       |       |       |       |       |       |       |       |       |       |       |       |       |       |       |       |   X   |   X   |       |       |       |
| AC: AchievementServiceTests           |       |       |       |       |       |       |       |       |       |       |       |       |       |       |       |       |       |   X   |   X   |       |       |       |
| CA: CardServiceTests                  |       |       |       |       |       |       |       |       |       |       |       |       |       |       |       |       |       |       |       |       |   X   |   X   |
| HA: HandServiceTests                  |       |       |       |       |       |       |       |       |       |       |       |       |       |       |       |       |       |       |       |       |   X   |   X   |
| MO: ValidatorTests                    |       |       |       |       |       |       |       |       |       |       |       |       |       |       |       |       |       |       |       |       |   X   |   X   |
| PA: PackCardServiceTests              |       |       |       |       |       |       |       |       |       |       |       |       |       |       |       |       |       |       |       |       |   X   |   X   |
| PL: PlayerServiceTests                |       |       |       |       |       |       |       |       |       |       |       |       |       |       |       |       |       |       |       |       |   X   |   X   |
| TA: CellServiceTests                  |       |       |       |       |       |       |       |       |       |       |       |       |       |       |       |       |       |       |       |       |   X   |   X   |
| TA: RowServiceTests                   |       |       |       |       |       |       |       |       |       |       |       |       |       |       |       |       |       |       |       |       |   X   |   X   |
| TA: TableCardServiceTests             |       |       |       |       |       |       |       |       |       |       |       |       |       |       |       |       |       |       |       |       |   X   |   X   |
| TE: TeamServiceTests                  |       |       |       |       |       |       |       |       |       |       |       |       |       |       |       |       |       |       |       |       |   X   |   X   |


## 6. Criterios de Aceptación

- Todas las pruebas unitarias deben pasar con éxito antes de la entrega final del proyecto.
- La cobertura de código debe ser al menos del 70%.
- No debe haber fallos críticos en las pruebas de integración y en la funcionalidad.

## 7. Conclusión

Este plan de pruebas establece la estructura y los criterios para asegurar la calidad del software desarrollado. Es responsabilidad del equipo de desarrollo y pruebas seguir este plan para garantizar la entrega de un producto funcional y libre de errores.
