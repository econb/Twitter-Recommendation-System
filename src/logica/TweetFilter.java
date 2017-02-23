package logica;

import java.util.HashMap;
import java.util.LinkedList;

import persistencia.PersistenciaTexto;


import logica.excepciones.ExcepcionTweetFilter;
import logica.excepciones.ExcepcionTwitterAPI;
import logica.excepciones.ExcepcionTwitterRateLimit;

/**
 * Controlador. 
 * Gestiona todos los casos de uso de la aplicación
 * @author Eddie Contreras
 */
public class TweetFilter {
	//Única instancia de la clase
	private static TweetFilter instancia = null;
	//CONSUMER_KEY: identificador de la aplicación ante Twitter
	private final String CONSUMER_KEY = "9gULGqYJJj3E1OAkb4WQAA";
	//CONSUMER_SECRET: clave de la aplicación ante Twitter
	private final String CONSUMER_SECRET = "BfVbkX5hrDowxSiHTQp1tqeUhnAscmpbReigCsPQc";
	//Lista de usuarios del sistema
	private HashMap<String, Usuario> usuarios = new HashMap<String, Usuario>();
	//usuario autenticado
	private Usuario usuarioAutenticado;
	//Objeto temporal para autorización ante Twitter
	private TwitterPINBasedAuth twitterPINBasedAuth;
	//Usuario temporal para completar registro y autorización 
	private Usuario usuarioTempRegistro;

	private TweetFilter() {}

	public static TweetFilter getInstancia() {
		if (instancia == null) {
			instancia = new TweetFilter();
		}
		return instancia;
	}

	/**
	 * Retorna el nombre del usuario autenticado
	 * @return nombre
	 */
	public String getUsuarioAutenticado() {
		return usuarioAutenticado.getNombre();
	}
	
	public String getConsumerKey() {
		return CONSUMER_KEY;
	}
	
	public String getConsumerSecret() {
		return CONSUMER_SECRET;
	}
	
	public HashMap<String, Usuario> getUsuarios() {
		return usuarios;
	}

	/**
	 * Registra un nuevo usuario. 
	 * @param nombre nombre del nuevo usuario
	 * @param password password del nuevo usuario
	 * @return url url para que el usuario obtenga un PIN
	 * @throws ExcepcionTweetFilter si el usuario ya está regsitrado
	 * @throws ExcepcionTwitterAPI si hay un problema en la conexión con Twitter
	 * @pre el usuario no esta registrado
	 */
	public String registrarUsuario(String nombre, String password) throws ExcepcionTweetFilter, ExcepcionTwitterAPI {
		String url;

		//RECUPERA DATOS
		this.usuarios = PersistenciaTexto.getInstancia().recuperar();
		
		if (usuarios.containsKey(nombre)) {
			throw new ExcepcionTweetFilter("El usuario de nombre: " + nombre + " ya está registrado.");
		}

		twitterPINBasedAuth = new TwitterPINBasedAuth();
		url = twitterPINBasedAuth.getURLAutorizacion(CONSUMER_KEY, CONSUMER_SECRET);
		usuarioTempRegistro = new Usuario(nombre, password);
		
		return url;
	}

	/**
	 * Autoriza la aplicación para hacer peticiones a Twitter en nombre de un usuario
	 * @param PIN el PIN que obtuvo el usuario con la url
	 * @throws ExcepcionTweetFilter si se perdió la información del usuario a registrar
	 * @throws ExcepcionTwitterAPI si hay un problema en la conexión con Twitter
	 */
	public void autorizarAplicacion(String PIN) throws ExcepcionTweetFilter, ExcepcionTwitterAPI { 
		String[] accessTokenStr;

		if (usuarioTempRegistro == null) {
			throw new ExcepcionTweetFilter("Ha ocurrido un error, por favor vuelva a iniciar el proceso de registro.");
		}

		accessTokenStr = twitterPINBasedAuth.autorizarAplicacion(PIN);
		usuarioTempRegistro.setAccessToken(accessTokenStr[0]);
		usuarioTempRegistro.setAccessTokenSecret(accessTokenStr[1]);
		usuarios.put(usuarioTempRegistro.getNombre(), usuarioTempRegistro);
		usuarioTempRegistro = null;
		twitterPINBasedAuth = null;
		
		//GUARDA DATOS
		PersistenciaTexto.getInstancia().guardar();
	}

	/**
	 * Autentica a un usuario
	 * @param nombre el nombre del usuario
	 * @param password el password del usuario
	 * @throws ExcepcionTweetFilter si el usuario no está registrado
	 * @throws ExcepcionTweetFilter si el password no es correcto
	 * @pre el usuario está registrado
	 * @pos se recupera al usuario en el atributo usuarioAutenticado
	 */
	public void autenticarUsuario(String nombre, String password) throws ExcepcionTweetFilter {	
		//RECUPERA DATOS
		this.usuarios = PersistenciaTexto.getInstancia().recuperar();
		
		if (!usuarios.containsKey(nombre)) {
			throw new ExcepcionTweetFilter("El usuario " + nombre + " no está registrado.");
		}
		
		if (!usuarios.get(nombre).passwordValido(password)) {
			throw new ExcepcionTweetFilter("El password no es correcto.");
		}

		usuarioAutenticado = usuarios.get(nombre);
	}

	/**
	 * Retorna los filtros del usuario autenticado
	 * @return lista de filtros del usuario autenticado
	 * @throws ExcepcionTweetFilter si no hay un usuario autenticado
	 * @pre hay un usuario autenticado
	 */
	public LinkedList<HashMap<String, String>> consultarFiltros() throws ExcepcionTweetFilter {
		verificarUsuarioAutenticado();

		return usuarioAutenticado.getFiltros();
	}

	/**
	 * Crea un nuevo filtro para el usuario autenticado
	 * @param nombre nombre del filtro
	 * @param descripcion descripción del filtro
	 * @param tipoCronologia tipo de cronologia, puede ser 'Home', 'Lista', etc
	 * @param otrosParams otros parametros que pueden ser incluidos o no dependiendo del tipo de filtro y modelo 
	 * @throws ExcepcionTweetFilter si no hay un usuario autenticado
	 * @throws ExcepcionTweetFilter si ya existe un filtro con el nombre especificado
	 * @throws ExcepcionTweetFilter si se trata de crear más de un filtro (excepción temporal).
	 * @throws ExcepcionTweetFilter si no se pudo cargar el archivo de propiedades
	 * @throws ExcepcionTweetFilter si no se pudo crear el filtro por reflexión
	 * @throws ExcepcionTweetFilter si no se pudo crear el clasificador por reflexión
	 * @pre hay un usuario autenticado
	 * @pos el usuario autentidado tiene un nuevo filtro
	 */
	public void crearFiltro(String nombre, String descripcion, String tipoCronologia, String[] otrosParams) throws ExcepcionTweetFilter {
		verificarUsuarioAutenticado();
		usuarioAutenticado.crearFiltro(CONSUMER_KEY, CONSUMER_SECRET, nombre, descripcion, tipoCronologia, otrosParams);	
			
		//GUARDA DATOS
		PersistenciaTexto.getInstancia().guardar();
	}

	/**
	 * Entrena un filtro con un caso (tweet, etiqueta)
	 * @param IDfiltro identificador del filtro
	 * @param tweet tweet
	 * @param etiqueta etiqueta del tweet
	 * @throws ExcepcionTweetFilter si no hay un usuario autenticado
	 * @throws ExcepcionTweetFilter si el filtro no existe
	 * @pre hay un usuario autenticado
	 * @pre el filtro existe
	 * @pos el filtro se entrena con el nuevo caso
	 */
	public void entrenarFiltro(String IDfiltro, Tweet tweet, boolean etiqueta) throws ExcepcionTweetFilter {
		verificarUsuarioAutenticado();
		usuarioAutenticado.entrenarFiltro(IDfiltro, tweet, etiqueta);
		
		//GUARDA DATOS
		PersistenciaTexto.getInstancia().guardar();
	}

	/**
	 * Retroalimenta un filtro con un caso (tweet, etiqueta)
	 * @param IDfiltro identificador del filtro
	 * @param tweet tweet
	 * @param etiqueta etiqueta del tweet
	 * @throws ExcepcionTweetFilter si no hay un usuario autenticado
	 * @throws ExcepcionTweetFilter si el filtro no existe
	 * @throws ExcepcionTweetFilter si el tweet no existe
	 * @pre hay un usuario autenticado
	 * @pre el filtro existe
	 * @pre el tweet existe
	 */
	public void retroalimentarFiltro(String IDfiltro, Tweet tweet, boolean etiqueta) throws ExcepcionTweetFilter {
		verificarUsuarioAutenticado();
		usuarioAutenticado.retroalimentarFiltro(IDfiltro, tweet, etiqueta);
		
		//GUARDA DATOS
		PersistenciaTexto.getInstancia().guardar();
	}

	/**
	 * Enciende un filtro para que comience a filtrar su cronología asociada
	 * @param IDfiltro identificador del filtro
	 * @throws ExcepcionTweetFilter si no hay un usuario autenticado
	 * @throws ExcepcionTweetFilter si el filtro no existe
	 * @throws ExcepcionTweetFilter si el filtro ya está encendido
	 * @pre hay un usuario autenticado
	 * @pre el filtro existe
	 * @pre el filtro está apagado
	 * @pos el estado del filtro cambia a encendido
	 */
	public void encenderFiltro(String IDfiltro) throws ExcepcionTweetFilter {
		verificarUsuarioAutenticado();
		usuarioAutenticado.encenderFiltro(IDfiltro);
	}

	/**
	 * Apaga un filtro para que se detenga de filtrar su cronología asociada
	 * @param IDfiltro identificador del filtro
	 * @throws ExcepcionTweetFilter si no hay un usuario autenticado
	 * @throws ExcepcionTweetFilter si el filtro no existe
	 * @throws ExcepcionTweetFilter si el filtro ya está apagado
	 * @pre hay un usuario autenticado
	 * @pre el filtro existe
	 * @pre el filtro está encendido
	 * @pos el estado del filtro cambia a apagado
	 */
	public void apagarFiltro(String IDfiltro) throws ExcepcionTweetFilter {
		verificarUsuarioAutenticado();
		usuarioAutenticado.apagarFiltro(IDfiltro);
	}

	/**
	 * Consulta la cronología asociada a un filtro
	 * @param IDfiltro identificador del filtro
	 * @return cronologia la cronología asociada al filtro
	 * @throws ExcepcionTweetFilter si no hay un usuario autenticado
	 * @throws ExcepcionTweetFilter si el filtro no existe
	 * @throws ExcepcionTwitterRateLimit si no se permite hacer más llamados al REST API de Twitter
	 * @throws ExcepcionTwitterAPI si hay un problema en la conexión con Twitter
	 * @pre hay un usuario autenticado
	 * @pre el filtro existe
	 */
	public LinkedList<Tweet> consultarCronologia(String IDfiltro) throws ExcepcionTweetFilter, ExcepcionTwitterAPI, ExcepcionTwitterRateLimit {
		LinkedList<Tweet> cronologia;

		verificarUsuarioAutenticado();
		cronologia = usuarioAutenticado.consultarCronologia(IDfiltro);

		return cronologia;	
	}

	/**
	 * Consulta los tweets interesantes asociados a un filtro
	 * @param IDfiltro identificador del filtro
	 * @return tweets interesantes
	 * @throws ExcepcionTweetFilter si no hay un usuario autenticado
	 * @throws ExcepcionTweetFilter si el filtro no existe
	 * @pre hay un usuario autenticado
	 * @pre el filtro existe
	 */
	public LinkedList<Tweet> consultarTweetsInteresantes(String IDfiltro) throws ExcepcionTweetFilter {
		LinkedList<Tweet> interesantes;

		verificarUsuarioAutenticado();
		interesantes = usuarioAutenticado.consultarTweetsInteresantes(IDfiltro);

		return interesantes;
	}

	/**
	 * Consulta los tweets descartados asociados a un filtro
	 * @param IDfiltro identificador del filtro
	 * @return tweets descartados
	 * @throws ExcepcionTweetFilter si no hay un usuario autenticado
	 * @throws ExcepcionTweetFilter si el filtro no existe
	 * @pre hay un usuario autenticado
	 * @pre el filtro existe
	 */
	public LinkedList<Tweet> consultarTweetsDescartados(String IDfiltro) throws ExcepcionTweetFilter {
		LinkedList<Tweet> descartados;

		verificarUsuarioAutenticado();
		descartados = usuarioAutenticado.consultarTweetsDescartados(IDfiltro);

		return descartados;
	}

	/**
	 * Verifica si hay un usuario autenticado
	 * @throws TweetFilterException si no hay un usuario autenticado
	 */
	private void verificarUsuarioAutenticado() throws ExcepcionTweetFilter {
		if (usuarioAutenticado == null) {
			throw new ExcepcionTweetFilter("No hay un usuario autenticado"); 
		}
	}
}
