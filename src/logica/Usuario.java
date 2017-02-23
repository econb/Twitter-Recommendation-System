package logica;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Properties;

import logica.excepciones.ExcepcionTweetFilter;
import logica.excepciones.ExcepcionTwitterAPI;
import logica.excepciones.ExcepcionTwitterRateLimit;
import logica.filtro.Filtro;

/**
 * Usuario de la aplicación
 * @author Eddie Contreras
 */
public class Usuario {
	private String ACCESS_TOKEN; //ACCESS TOKEN: identificador del usuario ante Twitter
	private String ACCESS_TOKEN_SECRET; //ACCESS_TOKEN_SECRET: clave del usuario ante Twitter
	private String nombre;
	private String password;
	private LinkedList<Filtro> filtros;

	/**
	 * Creador
	 * @param nombre nombre del usuario
	 * @param password password del usuario
	 */
	public Usuario(String nombre, String password) {
		this.nombre = nombre;
		this.password = password;
		filtros = new LinkedList<Filtro>();
	}
	
	public String getNombre() { 
		return nombre;
	}
	
	public String getPassword() {
		return password;
	}
	
	public String getAccessToken() {
		return ACCESS_TOKEN;
	}
	
	public String getAccessTokenSecret() {
		return ACCESS_TOKEN_SECRET;
	}
	
	public LinkedList<Filtro> getListaFiltros() {
		return filtros;
	}
	
	public void setNombre(String nombre) {
		this.nombre = nombre;
	}
	
	public void setPassword(String password) {
		this.password = password;
	}
	
	public void setAccessToken(String accessToken) {
		this.ACCESS_TOKEN = accessToken;
	}
	
	public void setAccessTokenSecret(String accessTokenSecret) {
		this.ACCESS_TOKEN_SECRET = accessTokenSecret;
	}
	
	/**
	 * Retrona una versión 'liviana' de los filtros del usuario
	 * @return filtros
	 */
	public LinkedList<HashMap<String, String>> getFiltros() {
		LinkedList<HashMap<String, String>> filtrosTexto;
		filtrosTexto = new LinkedList<HashMap<String, String>>();
		
		for (Filtro filtro : filtros) {
			filtrosTexto.add(filtro.toText());
		}
		
		return filtrosTexto;
	}

	/**
	 * Valida el password del usuario
	 * @param password el password a validar
	 * @return true si el password es correcto
	 */
	public boolean passwordValido(String password) {
		return this.password.equals(password);
	}

	/**
	 * Crea un nuevo filtro
	 * @param CONSUMER_KEY identificador de la aplicación ante Twitter
	 * @param CONSUMER_SECRET clave de la aplicación ante Twitter
	 * @param nombre nombre del filtro
	 * @param descripcion descripción del filtro
	 * @param tipoCronologia tipo de cronologia, puede ser 'Home', 'Lista', etc
	 * @param otrosParams otros parametros que pueden ser incluidos o no dependiendo del tipo de filtro y modelo 
	 * @throws ExcepcionTweetFilter si ya existe un filtro con el nombre especificado
	 * @throws ExcepcionTweetFilter si se trata de crear más de un filtro (excepción temporal).
	 * @throws ExcepcionTweetFilter si no se pudo cargar el archivo de propiedades
	 * @throws ExcepcionTweetFilter si no se pudo crear el filtro por reflexión
	 * @throws ExcepcionTweetFilter si no se pudo crear el clasificador por reflexión
	 */
	public void crearFiltro(String CONSUMER_KEY, String CONSUMER_SECRET, String nombre, String descripcion, String tipoCronologia, String[] otrosParams) throws ExcepcionTweetFilter {
		if (buscarFiltro(nombre) != null) {
			throw new ExcepcionTweetFilter("Ya existe un filtro con el nombre " + nombre);
		}
		
		/*==============================================================================/
		 * LIMITACIÓN TEMPORAL: solo se permite crear un filtro por usuario.
		 * Actualmente solo se pueden crear filtros para la cronología Home, los cuales
		 * hacen filtrado en tiempo real estableciendo conexiones y escuchando 
		 * el stream de tweets que le llegan al usuario en Twitter.
		 * Twitter es bastante estricto con sus políticas de rate limiting y es posible
		 * que bloquee esta aplicación debido a que dos o mas conexiones escuchando el mismo
		 * stream es un uso bastante ineficiente de recursos y Twitter lo puede tomar
		 * como un abuso a su Streaming API
		 */
		if (filtros.size() >= 1) {
			throw new ExcepcionTweetFilter("En esta versión de la aplicación solo se permite " +
					"crear\nmáximo un filtro para la cronología 'home'. Estra restricción será " +
					"eliminada\nen futuras versiones cuando se soporten otros tipos de cronologías.");
		}
		/*==============================================================================*/
		
		//Lee archivo de configuración y obtiene nombre de clase para tipo de cronología
		Properties configuracion = new Properties();
		InputStream in = getClass().getResourceAsStream("/filtro.properties");
		
		try {
			configuracion.load(in);
		} catch (IOException e) {
			throw new ExcepcionTweetFilter("Hubo un problema al crear el filtro.", e);
		}
		
		String claseFiltro = configuracion.getProperty(tipoCronologia);
		
		//Crea el filtro mediante reflexión
		try {
			Class clase = Class.forName(claseFiltro);
			Constructor constructor  = clase.getConstructor(String.class, String.class, String.class, String.class, String.class, String.class, String[].class);
			Filtro filtro = (Filtro) constructor.newInstance(CONSUMER_KEY, CONSUMER_SECRET, ACCESS_TOKEN, ACCESS_TOKEN_SECRET, nombre, descripcion, otrosParams);
			filtros.add(filtro);
		} catch (ClassNotFoundException | NoSuchMethodException | SecurityException | InstantiationException | IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
			throw new ExcepcionTweetFilter("Hubo un problema al crear el filtro.", e);
		} 
	}

	/**
	 * Entrena un filtro con un caso (tweet, etiqueta)
	 * @param IDfiltro identificador del filtro
	 * @param tweet tweet
	 * @param etiqueta etiqueta del tweet
	 * @throws ExcepcionTweetFilter si el filtro no existe
	 */
	public void entrenarFiltro(String IDfiltro, Tweet tweet, boolean etiqueta) throws ExcepcionTweetFilter {
		Filtro filtro = buscarFiltro(IDfiltro);

		if (filtro == null) {
			throw new ExcepcionTweetFilter("No existe el filtro " + IDfiltro);
		}
		
		filtro.entrenar(tweet, etiqueta);
	}
	
	/**
	 * Retroalimenta un filtro con un caso (tweet, etiqueta)
	 * @param IDfiltro identificador del filtro
	 * @param tweet tweet
	 * @param etiqueta etiqueta del tweet
	 * @throws ExcepcionTweetFilter si el filtro no existe
	 * @throws ExcepcionTweetFilter si el tweet no existe
	 */
	public void retroalimentarFiltro(String IDfiltro, Tweet tweet, boolean etiqueta) throws ExcepcionTweetFilter {
		Filtro filtro = buscarFiltro(IDfiltro);

		if (filtro == null) {
			throw new ExcepcionTweetFilter("No existe el filtro " + IDfiltro);
		}
		
		filtro.retroalimentar(tweet, etiqueta);
	}
	
	/**
	 * Enciende un filtro para que comience a filtrar su cronología asociada
	 * @param IDfiltro identificador del filtro
	 * @throws ExcepcionTweetFilter si el filtro no existe
	 * @throws ExcepcionTweetFilter si el filtro ya está encendido
	 */
	public void encenderFiltro(String IDfiltro) throws ExcepcionTweetFilter {
		Filtro filtro = buscarFiltro(IDfiltro);

		if (filtro == null) {
			throw new ExcepcionTweetFilter("No existe el filtro " + IDfiltro);
		}
		
		filtro.encender();
	}
	
	/**
	 * Apaga un filtro para que se detenga de filtrar su cronología asociada
	 * @param IDfiltro identificador del filtro
	 * @throws ExcepcionTweetFilter si el filtro no existe
	 * @throws ExcepcionTweetFilter si el filtro ya está apagado
	 */
	public void apagarFiltro(String IDfiltro) throws ExcepcionTweetFilter {
		Filtro filtro = buscarFiltro(IDfiltro);

		if (filtro == null) {
			throw new ExcepcionTweetFilter("No existe el filtro " + IDfiltro);
		}
		
		filtro.apagar();
	}
	
	/**
	 * Retorna la cronologia asociada al filtro especificado
	 * @param IDfiltro identificador del filtro
	 * @return cronología conología asociada al filtro
	 * @throws ExcepcionTweetFilter si el filtro no existe
	 * @throws ExcepcionTwitterRateLimit si no se permite hacer más llamados al REST API de Twitter
	 * @throws ExcepcionTwitterAPI si hay un problema en la conexión con Twitter
	 */
	public LinkedList<Tweet> consultarCronologia(String IDfiltro) throws ExcepcionTweetFilter, ExcepcionTwitterAPI, ExcepcionTwitterRateLimit {
		LinkedList<Tweet> cronologia;
		Filtro filtro = buscarFiltro(IDfiltro);

		if (filtro == null) {
			throw new ExcepcionTweetFilter("No existe el filtro " + IDfiltro);
		}	
		cronologia = filtro.getCronologia();
		
		return cronologia;
	}
	
	/**
	 * Retorna los tweets interesantes asociados al filtro especificado
	 * @param IDfiltro identificador del filtro
	 * @return tweets interesantes
	 * @throws ExcepcionTweetFilter si el filtro no existe
	 */
	public LinkedList<Tweet> consultarTweetsInteresantes(String IDfiltro) throws ExcepcionTweetFilter {
		LinkedList<Tweet> interesantes;
		Filtro filtro = buscarFiltro(IDfiltro);
		
		if (filtro == null) {
			throw new ExcepcionTweetFilter("No existe el filtro " + IDfiltro);
		}
		interesantes = filtro.getTweetsInteresantes();
		
		return interesantes;
	}
	
	/**
	 * Retorna los tweets descartados asociados al filtro especificado
	 * @param IDfiltro identificador del filtro
	 * @return tweets descartados
	 * @throws ExcepcionTweetFilter si el filtro no existe
	 */
	public LinkedList<Tweet> consultarTweetsDescartados(String IDfiltro) throws ExcepcionTweetFilter {
		LinkedList<Tweet> descartados;
		Filtro filtro = buscarFiltro(IDfiltro);
		
		if (filtro == null) {
			throw new ExcepcionTweetFilter("No existe el filtro " + IDfiltro);
		}
		descartados = filtro.getTweetsDescartados();
		
		return descartados;
	}
	
	/**
	 * Busca un filtro en la lista de filtros
	 * (NOTA: IDfiltro es el nombre del filtro)
	 * @param IDfiltro identificador del filtro
	 * @return filtro null si no lo encuentra
	 */
	public Filtro buscarFiltro(String IDfiltro) {
		Filtro filtro = null;
		
		for (Filtro filtr : filtros) {
			if (filtr.getNombre().equals(IDfiltro)) {
				filtro = filtr;
			}
		}
		
		return filtro;
	}
}
