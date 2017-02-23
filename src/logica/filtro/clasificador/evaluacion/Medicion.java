package logica.filtro.clasificador.evaluacion;

/**
 * Lleva el conteo de verdaderos positivos, verdaderos negativos, falsos positivos, falsos negativos
 * y calcula la precisión y el recall
 * Es usado en la validación cruzada
 * @author Eddie Contreras
 *
 */
public class Medicion {
	private int verdaderosPositivos;
	private int verdaderosNegativos;
	private int falsosPositivos;
	private int falsosNegativos;
	
	public Medicion() {
		verdaderosPositivos = 0;
		verdaderosNegativos = 0;
		falsosPositivos = 0;
		falsosNegativos = 0;
	}
	
	/**
	 * Aumenta los contadores de acuerdo a la comparación entre 
	 * la etiqueta real y la predicha.
	 * True corresponde a la clase positiva y False a la negativa.
	 * @param real etiqueta real
	 * @param prediccion etiqueta predicha
	 */
	public void medir(boolean real, boolean prediccion) {
		
		if (real == prediccion) {		//Verdadero	
			if (prediccion == true) {	//Verdadero positivo		
				verdaderosPositivos++;	
			} else {					//Verdadero negativo
				verdaderosNegativos++;	
			}
		} else { 						//Falso 
			if (prediccion == true) {	//Falso positivo
				falsosPositivos++;
			} else {					//Falso negativo
				falsosNegativos++;
			}
		}
	}
	
	/**
	 * De todos los que se predijeron como positivos, ¿cuantos realmente lo eran?
	 * @return
	 */
	public double precision() {
		return (double) verdaderosPositivos / (double) (verdaderosPositivos + falsosPositivos);
	}
	
	/** 
	 * De todos los que son positivos, ¿cuantos se predijeron correctamente?
	 * @return
	 */
	public double recall() {
		return (double) verdaderosPositivos / (double) (verdaderosPositivos + falsosNegativos);
	}

	public int getVerdaderosPositivos() {
		return verdaderosPositivos;
	}

	public int getVerdaderosNegativos() {
		return verdaderosNegativos;
	}

	public int getFalsosPositivos() {
		return falsosPositivos;
	}

	public int getFalsosNegativos() {
		return falsosNegativos;
	}
}
