package logica.excepciones;

/**
 * Excepción para problemas en la lógica de la aplicación.
 * @author Eddie Contreras
 */
public class ExcepcionTweetFilter extends Exception {
	
	public ExcepcionTweetFilter(String message, Throwable cause) {
		super(message, cause);
	}

	public ExcepcionTweetFilter(String message) {
		super(message);
	}

	public ExcepcionTweetFilter(Throwable cause) {
		super(cause);
	}
}
