/**
 * 
 */
package logica.filtro.clasificador;


import java.util.LinkedList;

import logica.Tweet;

/**
 * Interfaz de un clasificador
 * Implementaciones deben gestionar un modelo de aprendizaje de máquina 
 * junto con su lista de características y el conocimiento aprendido 
 * con los casos de entrenamiento (tweet, etiqueta).
 * @author Eddie Contreras
 */
public interface Clasificador {

	/**
	 * Entrena el modelo con un tweet	
	 * @param tweet tweet
	 * @param etiqueta etiqueta del tweet
	 */
	void entrenar(Tweet tweet, boolean etiqueta);
		
	/**
	 * Clasifica un tweet
	 * @param tweet tweet
	 * @return etiqueta etiqueta asignada al tweet
	 */
	boolean clasificar(Tweet tweet);
	
	/**
	 * Retorna una representación del clasificador para hacerlo persistente.
	 * No se impone una estructura de datos específica más que un arreglo de cadenas
	 * ya que no se sabe que estructuras de datos usarán las implementaciones 
	 * de la interfaz Clasificador
	 * @return
	 */
	LinkedList<String> getDatos();
	
	/**
	 * Reconstruye el clasificador tomando la representación persistente 
	 * @param representacion representación persistente del clasificador
	 */
	void setDatos(LinkedList<String> datos);
}
