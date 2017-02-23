package logica.filtro.clasificador.evaluacion;

import java.io.Serializable;

import logica.Tweet;

/**
 * Clase que encapsula un tweet y su etiqueta.
 * Solo es usada para la validación cruzada.
 * @author Eddie Contreras.
 *
 */
public class Entreno implements Serializable{
	
	private static final long serialVersionUID = -2158250512074889624L;
	private Tweet tweet;
	private boolean etiqueta;
	
	public Entreno(Tweet tweet, boolean etiqueta) {
		this.tweet = tweet;
		this.etiqueta = etiqueta;
	}
	
	public Tweet getTweet() {
		return tweet;
	}
	
	public boolean getEtiqueta() {
		return etiqueta;
	}
}
