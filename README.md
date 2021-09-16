# tpe1-g3 - Airport Management
Trabajo práctico especial para la materia Programación de Objetos Distribuidos, utilizando RMI y Concurrencia.

Sistema remoto thread-safe para registrar los despegues de
un aeropuerto, a partir de la existencia de una o más pistas de despegue (a partir de ahora pistas),
permitiendo notificar a las aerolíneas de los eventos y ofreciendo reportes de los despegues hasta
el momento.

## Instrucciones de instalacion

Tener instalado Maven y correr parado en la carpeta raiz

```shell
$> mvn clean install
```

### Server y Registry

Para descomprimir los scripts del servidor y registry, ejecutar en la carpeta raiz:
```shell
$> ./server-untar.sh
```
En caso de conflicto con los permisos sobre el bash script, asignarle permisos de ejecucion con:
```shell
$> chmod +x server-untar.sh
```
Esto disponibilizara los script para correr al registry y server en server/target/tpe1-g3-server-1.0-SNAPSHOT

Cambiar a dicho directorio y correr en distintas terminales los scripts: 
```shell
$> ./run-registry.sh
```
o
```shell
$> ./run-server.sh
```

### Clientes
Para descomprimir los scripts de los clientes, ejecutar en la carpeta raiz:
```shell
$> ./client-untar.sh
```
En caso de conflicto con los permisos sobre el bash script, asignarle permisos de ejecucion con:
```shell
$> chmod +x client-untar.sh
```
Esto disponibilizara los script para correr los clientes en client/target/tpe1-g3-client-1.0-SNAPSHOT

Cambiar a dicho directorio y correr en distintas terminales los clientes
#### Cliente de administracion
```shell
$> ./run-management.sh -DserverAddress=xx.xx.xx.xx:yyyy -Daction=actionName
[ -Drunway=runwayName | -Dcategory=minCategory ]
```
donde 
- serverAddress = 127.0.0.1:0
- actionName = add | open | close | status | takeOff | reorder
- runwayName = nombre de la pista
- minCategory = categoria de la pista

#### Cliente de solictud de pista
```shell
$> ./run-runway.sh -DserverAddress=xx.xx.xx.xx:yyyy -DinPath=fileName
```
donde
- serverAddress = 127.0.0.1:0
- fileName = path del archivo CSV de entrada con las solicitudes de pista

#### Cliente de seguimiento de vuelo
```shell
$> ./run-airline.sh -DserverAddress=xx.xx.xx.xx:yyyy -Dairline=airlineName
-DflightCode=flightCode
```
donde
- serverAddress = 127.0.0.1:0
- airlineName = nombre de la aerolinea (usar comillas simples '' en caso de ser mas de una palabra)
- flightCode = codigo identificador de vuelo

#### Cliente de solictud de pista
```shell
$> ./run-query.sh -DserverAddress=xx.xx.xx.xx:yyyy [ -Dairline=airlineName |
-Drunway=runwayName ] -DoutPath=fileName
```
donde
- serverAddress = 127.0.0.1:0
- fileName = path del archivo CSV de salida con los resultados de la consulta
- airlineName = nombre de la aerolinea para la consulta
- runwayName = nombre de la pista para la consulta


## Autores

- **Lautaro Galende** <<lgalende@itba.edu.ar>>
- **Valentín Ratti** <<vratti@itba.edu.ar>>
- **Tommy Rosenblatt** <<trosenblatt@itba.edu.ar>>
- **Sebastián Itokazu** <<sitokazu@itba.edu.ar>>