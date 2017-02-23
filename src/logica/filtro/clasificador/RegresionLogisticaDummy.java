package logica.filtro.clasificador;

import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;


import logica.Tweet;
import logica.filtro.preprocesador.PreprocesadorDummy;

/**
 * Clasificador Regresión Logistica
 * @author Eddie Contreras
 */
public class RegresionLogisticaDummy implements Clasificador {
	private HashMap<String, Double> paramsBasicos; //parámetros de las características básicas del modelo
	private HashMap<String, Double> paramsVocabulario; //parámetros de las características de vocabulario del modelo
	private PreprocesadorDummy preprocesador; //preprocesador del texto de los tweets

	public RegresionLogisticaDummy() {
		preprocesador = new PreprocesadorDummy();	
		paramsBasicos = new HashMap<String, Double>();
		paramsVocabulario = new HashMap<String, Double>();

		//Término bias
		paramsBasicos.put("bias", 0.0); 		
		//Caracteristicas básicas de un tweet
		paramsBasicos.put("numRetweets", 0.0);	//número de retweets
		paramsBasicos.put("esRetweet", 0.0);	//es retweet?
		paramsBasicos.put("esRetweetPorUsuario", 0.0); //ha sido retwitteado por el usuario?
		paramsBasicos.put("esFavorito", 0.0);	//es favorito por usuario?
		paramsBasicos.put("esRespuesta", 0.0); //es un tweet de respuesta?
		paramsBasicos.put("esInapropiado", 0.0);	//tiene contenido inapropiado?
		paramsBasicos.put("numHashTags", 0.0);	//número de hashtags
		paramsBasicos.put("numUrls", 0.0);		//número de url's
		paramsBasicos.put("numMenciones", 0.0);	//número de menciones
		paramsBasicos.put("numMedia", 0.0);	//número de media (fotos)
	}

	/**
	 * Entrena el modelo con un tweet	
	 * @param tweet tweet
	 * @param etiqueta etiqueta del tweet
	 */
	@Override
	public void entrenar(Tweet tweet, boolean etiqueta) {
		HashSet<String> palabrasTweet;
		double hipotesis;
		double tasaAprendizaje;
		int y;
		double termino;

		//Preprocesamiento del tweet
		palabrasTweet = preprocesador.preprocesar(tweet.getTexto());

		//Adición de nuevas características al modelo
		for (String palabra : palabrasTweet) {
			if (!paramsVocabulario.containsKey(palabra)) {
				paramsVocabulario.put(palabra, 0.0);
			}
		}

		/* Aprendizaje en línea: cada parámetro se actualiza
		 * de acuerdo al método de optimización de descenso gradiente */
		hipotesis = hipotesis(tweet, palabrasTweet);
		tasaAprendizaje = 1;
		y = (etiqueta == true)?1:0;
		termino = tasaAprendizaje * (hipotesis - y);

		paramsBasicos.put("bias", paramsBasicos.get("bias") - (termino) );
		paramsBasicos.put("numRetweets", paramsBasicos.get("numRetweets") - (termino * tweet.getNumRetweets()) );
		paramsBasicos.put("esRetweet", 	paramsBasicos.get("esRetweet") - (termino * (tweet.esRetweet()?1:0)) );
		paramsBasicos.put("esRetweetPorUsuario", paramsBasicos.get("esRetweetPorUsuario") - (termino * (tweet.esRetweetPorUsuario()?1:0)) );
		paramsBasicos.put("esFavorito", paramsBasicos.get("esFavorito") - (termino * (tweet.esFavorito()?1:0)) );
		paramsBasicos.put("esRespuesta", paramsBasicos.get("esRespuesta") - (termino * (tweet.esRespuesta()?1:0)) );
		paramsBasicos.put("esInapropiado", paramsBasicos.get("esInapropiado") - (termino * (tweet.esInapropiado()?1:0)) );
		paramsBasicos.put("numHashTags", paramsBasicos.get("numHashTags") - (termino * tweet.getHashTags().size()) );
		paramsBasicos.put("numUrls", paramsBasicos.get("numUrls") - (termino * tweet.getUrls().size()) );
		paramsBasicos.put("numMenciones", paramsBasicos.get("numMenciones") - (termino * tweet.getMenciones().size()) );
		paramsBasicos.put("numMedia", paramsBasicos.get("numMedia") - (termino * tweet.getMedia().size()) );

		for (String palabra : palabrasTweet) { 
			paramsVocabulario.put(palabra, paramsVocabulario.get(palabra) - (termino));
		}
	}

	/**
	 * Clasifica un tweet
	 * @param tweet tweet
	 * @return etiqueta etiqueta asignada al tweet
	 */
	@Override
	public boolean clasificar(Tweet tweet) {
		HashSet<String> palabrasTweet;
		double hipotesis;
		boolean etiqueta;

		//Preprocesamiento del tweet
		palabrasTweet = preprocesador.preprocesar(tweet.getTexto());

		hipotesis = hipotesis(tweet, palabrasTweet);
		etiqueta = hipotesis >= 0.5;		

		return etiqueta;
	}

	/**
	 * Función sigmoide
	 * @param z 
	 * @return resultado
	 */
	private double sigmoide(double z) {
		return 1 / (1 + StrictMath.pow(StrictMath.E, -z)); 
	}

	/**
	 * Calcula la hipótiesis para un caso
	 * @param tweet 
	 * @param palabras
	 * @return sigmoide
	 */
	private double hipotesis(Tweet tweet, HashSet<String> palabrasTweet) {
		double z;

		z = paramsBasicos.get("bias") 
				+ ( paramsBasicos.get("numRetweets") * tweet.getNumRetweets() )   //NORMALIZAR?
				+ ( paramsBasicos.get("esRetweet") * (tweet.esRetweet()?1:0) ) 
				+ ( paramsBasicos.get("esRetweetPorUsuario") * (tweet.esRetweetPorUsuario()?1:0) )
				+ ( paramsBasicos.get("esFavorito") * (tweet.esFavorito()?1:0) )
				+ ( paramsBasicos.get("esRespuesta") * (tweet.esRespuesta()?1:0) )
				+ ( paramsBasicos.get("esInapropiado") * (tweet.esInapropiado()?1:0) )
				+ ( paramsBasicos.get("numHashTags") * tweet.getHashTags().size() )
				+ ( paramsBasicos.get("numUrls") * tweet.getUrls().size() )
				+ ( paramsBasicos.get("numMenciones") * tweet.getMenciones().size() )
				+ ( paramsBasicos.get("numMedia") * tweet.getMedia().size() );

		for (String palabra : palabrasTweet) {
			if (paramsVocabulario.containsKey(palabra)) {
				z += paramsVocabulario.get(palabra);
			}
		}	
		return sigmoide(z);
	}

	public HashMap<String, Double> getParamsBasicos() {
		return paramsBasicos;
	}

	public HashMap<String, Double> getParamsVocabulario() {
		return paramsVocabulario;
	}

	@Override
	public LinkedList<String> getDatos() {
		LinkedList<String> datos = new LinkedList<String>();

		HashMap<String, Double> paramsBasicosCopia = (HashMap<String, Double>) paramsBasicos.clone();
		datos.add("**PARAMSBASICOS**");

		for (String caracteristica : paramsBasicosCopia.keySet()) {
			datos.add(caracteristica);
			datos.add(String.valueOf(paramsBasicosCopia.get(caracteristica)));
		}

		HashMap<String, Double> paramsVocabularioCopia = (HashMap<String, Double>) paramsVocabulario.clone();
		datos.add("**PARAMSVOCABULARIO**");

		for (String caracteristica : paramsVocabularioCopia.keySet()) {
			datos.add(caracteristica);
			datos.add(String.valueOf(paramsVocabularioCopia.get(caracteristica)));
		}

		return datos;
	}

	@Override
	public void setDatos(LinkedList<String> datos) {
		int i = 0;

		if (i < datos.size() && datos.get(0).equals("**PARAMSBASICOS**")) {
			for (i = 1; i < datos.size()-1 && 
					!datos.get(i).equals("**PARAMSVOCABULARIO**"); i = i+2) {
				String caracteristica = datos.get(i);
				Double parametro = Double.parseDouble(datos.get(i+1));
				this.paramsBasicos.put(caracteristica, parametro);
			}	
		} 
		if (i < datos.size() && datos.get(i).equals("**PARAMSVOCABULARIO**")) {
			for (i = i + 1; i < datos.size()-1 ; i = i+2) {
				String caracteristica = datos.get(i);
				Double parametro = Double.parseDouble(datos.get(i+1));
				this.paramsVocabulario.put(caracteristica, parametro);
			}
		}
	}
}
