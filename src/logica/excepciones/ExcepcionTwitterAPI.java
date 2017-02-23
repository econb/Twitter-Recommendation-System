package logica.excepciones;

/**
 * Excepción para errores que ocurran cuando se interactúe con el API de Twitter
 * @author Eddie Contreras
 */
public class ExcepcionTwitterAPI extends Exception {

	public ExcepcionTwitterAPI(String message, Throwable cause) {
		super(message, cause);
	}

	public ExcepcionTwitterAPI(String message) {
		super(message);
	}

	public ExcepcionTwitterAPI(Throwable cause) {
		super(cause);
	}
}
