package logica.filtro.clasificador.evaluacion;

import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;

import logica.Tweet;
import logica.filtro.clasificador.Clasificador;
import logica.filtro.clasificador.RegresionLogisticaDummy;

/**
 * Implementación Validación Cruzada
 * @author Eddie Contreras
 */
public class XValidator {
	private HashMap<String, Entreno> casos;

	public XValidator() {
		// TODO Auto-generated constructor stub
	}

	public void validar() {
		casos = SerializadorTweets.getInstancia().deserializar();

		//Separación positivos y negativos
		HashSet<String> casosPositivos = new HashSet<String>();
		HashSet<String> casosNegativos = new HashSet<String>();

		for (String key : casos.keySet()) {
			Entreno entreno = casos.get(key);
			boolean etiqueta = entreno.getEtiqueta();			
			
			if (etiqueta == true) {
				casosPositivos.add(key);
			} else if (etiqueta == false) {
				casosNegativos.add(key);
			}
		}

		System.out.println("total: " + casos.size());
		System.out.println("positivos: " + casosPositivos.size());
		System.out.println("negativos: " + casosNegativos.size());

		//Particiones
		final int NUM_PARTES = 9;
		LinkedList<HashSet<String>> partesPositivos = crearParticiones(casosPositivos);
		LinkedList<HashSet<String>> partesNegativos = crearParticiones(casosNegativos);
		LinkedList<HashSet<String>> listaPartes = new LinkedList<HashSet<String>>();

		for (int i = 0; i < NUM_PARTES; i++) {
			HashSet<String> parte = new HashSet<String>();
			parte.addAll(partesPositivos.get(i));
			parte.addAll(partesNegativos.get(i));

			listaPartes.add(parte);
		}

		System.out.println("partes: " + listaPartes.size());
		System.out.println("partesPositivas: " + partesPositivos.size());
		System.out.println("partesNegativas: " + partesNegativos.size());

		//Validación cruzada	
		LinkedList<Medicion> listaMediciones = new LinkedList<Medicion>();
		RegresionLogisticaDummy clasificador = null;
		
		for (int i = 0; i < NUM_PARTES; i++) {
			//División subconjuntos entrenamiento y prueba
			HashSet<String> partePrueba = listaPartes.get(i);

			HashSet<String> partesEntrenamiento = new HashSet<String>();
			for (int j = 0; j < i; j++) {
				partesEntrenamiento.addAll(listaPartes.get(j));
			}
			for (int j = i+1; j < NUM_PARTES; j++) {
				partesEntrenamiento.addAll(listaPartes.get(j));
			}

			//Entrenamiento
			clasificador = new RegresionLogisticaDummy();
					
			for (String key: partesEntrenamiento) {
				Tweet tweet = casos.get(key).getTweet();
				boolean etiqueta = casos.get(key).getEtiqueta();
				clasificador.entrenar(tweet, etiqueta);
			}

			//Prueba
			Medicion medicion = new Medicion();
			for (String key: partePrueba) {
				Tweet tweet = casos.get(key).getTweet();
				boolean etiquetaReal = casos.get(key).getEtiqueta();	
				boolean etiquetaPrediccion = clasificador.clasificar(tweet);			
				medicion.medir(etiquetaReal, etiquetaPrediccion);	
			}
			
			listaMediciones.add(medicion);	
		}

		//Resultados
		double precisionAvg = 0;
		double recallAvg = 0;
		
		for (Medicion medicion : listaMediciones) {	
			System.out.println("\n---------------------Parte------------------------");
			System.out.print(" VP: " + medicion.getVerdaderosPositivos());
			System.out.print(" VN: " + medicion.getVerdaderosNegativos());
			System.out.print(" FP: " + medicion.getFalsosPositivos());
			System.out.print(" FN: " + medicion.getFalsosNegativos());
			System.out.println("\nprecision: " + medicion.precision());
			System.out.println("recall: " + medicion.recall());	
			
			precisionAvg += medicion.precision();
			recallAvg += medicion.recall();
		}
		
		precisionAvg = precisionAvg / listaMediciones.size();
		recallAvg = recallAvg / listaMediciones.size();
		
		System.out.println("\n--------------------Mediciones-----------------------");
		System.out.println("precisionAvg: " + precisionAvg);
		System.out.println("recallAvg: " + recallAvg);	
		
		System.out.println("\n---------------------ParametrosBasicos----------------------");
		for (String param : clasificador.getParamsBasicos().keySet()) {
			System.out.println(clasificador.getParamsBasicos().get(param) + "::" + param);
		}
		System.out.println("\n---------------------ParametrosVocabulario----------------------");
		for (String param : clasificador.getParamsVocabulario().keySet()) {
			System.out.println(clasificador.getParamsVocabulario().get(param) + "::" + param);
		}
	}

	/**
	 * Parte un conjunto en varias partes
	 * @param casosDeUnaClase
	 * @return partes
	 */
	private LinkedList<HashSet<String>> crearParticiones(HashSet<String> casosDeUnaClase) {
		int i = 1;
		final int CASOS_X_PARTE = 100;
		HashSet<String> parte = new HashSet<String>();
		LinkedList<HashSet<String>> partesDeUnaClase = new LinkedList<HashSet<String>>();

		for (String id : casosDeUnaClase) {
			if (i == CASOS_X_PARTE) {
				parte.add(id);
				partesDeUnaClase.add(parte);
				parte = new HashSet<String>();
				i = 1;
			} else {
				parte.add(id);
				i++;
			}
		}

		return partesDeUnaClase;
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		XValidator validator = new XValidator();
		validator.validar();

	}

}
