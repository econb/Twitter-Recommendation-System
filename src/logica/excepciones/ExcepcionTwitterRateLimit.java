package logica.excepciones;

/**
 * Excepción usada cuando no se permite hacer mas peticiones de cronologías
 * al REST API de Twitter.
 * @author Eddie Contreras
 */
public class ExcepcionTwitterRateLimit extends Exception {

	public ExcepcionTwitterRateLimit(String message, Throwable cause) {
		super(message, cause);
	}

	public ExcepcionTwitterRateLimit(String message) {
		super(message);
	}

	public ExcepcionTwitterRateLimit(Throwable cause) {
		super(cause);
	}
}
