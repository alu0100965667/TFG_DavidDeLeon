## TFG David De León Rodríguez
# Optimización de la planificación académica con Inteligencia Artificial

El código de este repositorio implementa la metaheurística GRASP para llevar a cabo la planificación académica del Grado en Ingeniería Informática de la Universidad de La Laguna. Recibe como archivos de entrada ficheros .subject con la información de las asignaturas que el usuario quiere que el algoritmo tenga en cuenta. Estos archivos se encuentran en la carpeta ```subjects```. El algoritmo busca configuraciones de asignaturas y sus grupos en las que se optimicen los conflictos en los horarios considerados en el trabajo de Fin de Grado en el que se desarrolló este códgio.

## Funcionamiento de la herramienta

En primer lugar, para utilizar el código, se deberá de inicializar la clase GRASP pasándole al constructor la ruta absoluta del directorio donde se encuentren los ficheros .subject que quiera tener en cuenta el usuario. El constructor leerá los archivos del directorio y creará los objetos pertinentes para el funcionamiento del algoritmo.

La metaheurística GRASP consta de dos fases, una de construcción y otra de mejora.
Los métodos de la fase de construcción en la que se crea una solución semi aleatoria son los siguientes:
* ```semiGreedy()```: implementa la versión que tiene en cuenta la suma de todos las funciones objetivo.
* ```semiGreedyMO()```: implementa la versión multiobjetivo que tiene en cuenta cada función objetivo por separado.

Los parámetros a tener en cuenta para la modificación de estas funciones son:
* ```SEMIGREEDY_FLAG```: booleano que si es ```true``` las funciones ejecutan la versión de creación de la lista de candidatos basada en calidad, y si es ```false``` ejectuan la versión basada en cardinalidad.
* ```RCL_PARAM```: double que va de 0 a 1 representando la aleatoriedad en la versión basada en calidad.
* ```RCL_SIZE```: entero que determina el tamaño máximo de la lista de candidatos en la versión basada en cardinalidad.

Los métodos que implementan la fase de mejora o búsqueda local son los siguientes:
* ```localSearchFirst()```: implementa la búsqueda local de primer vecino.
* ```localSearchBest()```: implementa la búsqueda local de mejor vecino.
* ```localSearchMultiObjectiveFirst2()```: implementa la búsqueda local multiobjetivo de primer vecino 

Las soluciones a las que llega la ejecución de la fase de construcción seguida por la de mejora se almacenan en el atributo ```allSolutions```. La mejor solución encontrada se almacena en ```bestSolution```.
