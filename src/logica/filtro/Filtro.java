package logica.filtro;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.Properties;


import twitter4j.Paging;
import twitter4j.RateLimitStatus;
import twitter4j.ResponseList;
import twitter4j.Status;
import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.TwitterFactory;
import twitter4j.conf.ConfigurationBuilder;

import logica.Tweet;
import logica.excepciones.ExcepcionTweetFilter;
import logica.excepciones.ExcepcionTwitterAPI;
import logica.excepciones.ExcepcionTwitterRateLimit;
import logica.filtro.clasificador.Clasificador;
import logica.filtro.clasificador.RegresionLogisticaDummy;
import logica.filtro.clasificador.evaluacion.SerializadorTweets;

/**
 * Filtro abstracto
 * @author Eddie Contreras
 */
public abstract class Filtro {
	private String nombre; 
	private String descripcion;
	protected boolean estado; //ON/OFF indica si está filtrando o no.
	private Clasificador clasificador; 
	private String tipoCronologia; //tipo de cronologia asociada
	protected Twitter twitter; //Objeto para interactuar con el REST API de Twitter
	private LinkedList<Tweet> tweetsInteresantes; //Tweets interesantes
	private LinkedList<Tweet> tweetsDescartados; //Tweets descartados
	private int numCasosEntrenoPositivos; //contador casos de entrenamiento positivos
	private int numCasosEntrenoNegativos; //contador casos de entrenamiento negativos

	/**
	 * Creador
	 * @param CONSUMER_KEY identificador de la aplicación ante Twitter
	 * @param CONSUMER_SECRET clave de la aplicación ante Twitter
	 * @param ACCESS_TOKEN identificador del usuario ante Twitter
	 * @param ACCESS_TOKEN_SECRET clave del usuario ante Twitter
	 * @param nombre nombre del filtro
	 * @param descripcion descripción del filtro 
	 * @param tCronologia tipo de cronologia
	 * @throws ExcepcionTweetFilter si no se pudo cargar el archivo de propiedades
	 * @throws ExcepcionTweetFilter si no se pudo crear el clasificador por reflexión
	 */
	protected Filtro(String CONSUMER_KEY, String CONSUMER_SECRET, String ACCESS_TOKEN, String ACCESS_TOKEN_SECRET, String nombre, String descripcion, String tCronologia) throws ExcepcionTweetFilter {
		this.nombre = nombre;
		this.descripcion = descripcion;
		this.estado = false;
		this.clasificador = nuevoClasificador();
		this.tipoCronologia = tCronologia;
		this.twitter = nuevoTwitter(CONSUMER_KEY, CONSUMER_SECRET, ACCESS_TOKEN, ACCESS_TOKEN_SECRET);
		this.tweetsInteresantes = new LinkedList<Tweet>();
		this.tweetsDescartados = new LinkedList<Tweet>();
		this.numCasosEntrenoPositivos = 0;
		this.numCasosEntrenoNegativos = 0;
	}

	public String getNombre() {
		return nombre;
	}
	
	public String getDescripcion() {
		return descripcion;
	}
	
	public String getTipoCronologia() {
		return tipoCronologia;
	}
	
	public int getNumCasosEntrenoPositivos() {
		return numCasosEntrenoPositivos;
	}
	
	public int getNumCasosEntrenoNegativos() {
		return numCasosEntrenoNegativos;
	}
	
	public Clasificador getClasificador() {
		return clasificador;
	}
	
	public void setNumCasosEntrenoPositivos(int numCasos) {
		this.numCasosEntrenoPositivos = numCasos;
	}
	
	public void setNumCasosEntrenoNegativos(int numCasos) {
		this.numCasosEntrenoNegativos = numCasos;
	}

	/**
	 * Retorna una descripcion 'liviana' del filtro
	 * @return filtro
	 */
	public HashMap<String, String> toText() {
		HashMap<String, String> filtroTexto;
		filtroTexto = new HashMap<String, String>();

		filtroTexto.put("nombre", nombre);
		filtroTexto.put("descripcion", descripcion);
		filtroTexto.put("cronologia", tipoCronologia);
		filtroTexto.put("estado", estado?"ON":"OFF");
		filtroTexto.put("positivos", String.valueOf(numCasosEntrenoPositivos));
		filtroTexto.put("negativos", String.valueOf(numCasosEntrenoNegativos));
		
		return filtroTexto;
	}

	/**
	 * Retorna la cronología asociada al filtro
	 * @return cronología
	 * @throws ExcepcionTwitterAPI si hay un problema en la conexión con Twitter
	 * @throws ExcepcionTwitterRateLimit si no se permite hacer más llamados al REST API de Twitter
	 */
	public abstract LinkedList<Tweet> getCronologia() throws ExcepcionTwitterAPI, ExcepcionTwitterRateLimit;

	/**
	 * Enciende el filtro para que comience a filtrar su cronología asociada
	 * @throws ExcepcionTweetFilter si el filtro ya está encendido
	 */
	public abstract void encender() throws ExcepcionTweetFilter;

	/**
	 * Apaga el filtro para que se detenga de filtrar su cronología asociada
	 * @throws ExcepcionTweetFilter si el filtro ya está apagado
	 */
	public abstract void apagar() throws ExcepcionTweetFilter;

	/**
	 * Retorna los tweets interesantes asociados al filtro
	 * @return tweets interesantes
	 */
	public LinkedList<Tweet> getTweetsInteresantes() {
		return (LinkedList<Tweet>) tweetsInteresantes.clone();
	}

	/**
	 * Retorna los tweets descartados asociados al filtro
	 * @return tweets descartados
	 */
	public LinkedList<Tweet> getTweetsDescartados() {
		return (LinkedList<Tweet>) tweetsDescartados.clone();
	}

	/**
	 * Entrena el filtro con un caso (tweet, etiqueta)
	 * @param tweet tweet
	 * @param etiqueta etiqueta del tweet
	 */
	public void entrenar(Tweet tweet, boolean etiqueta) {
		clasificador.entrenar(tweet, etiqueta);

		if (etiqueta == true) {
			numCasosEntrenoPositivos++;
		} else {
			numCasosEntrenoNegativos++;
		}
	}
	
	/**
	 * Clasifica un tweet
	 * @param tweet tweet
	 * @return etiqueta etiqueta dada al tweet
	 */
	public boolean clasificar(Tweet tweet) {
		boolean etiqueta; 
		
		etiqueta = clasificador.clasificar(tweet);
		
		if (etiqueta == true) {
			tweetsInteresantes.addFirst(tweet);
		} else {
			tweetsDescartados.addFirst(tweet);
		}
		
		return etiqueta;
	}

	/**
	 * Retroalimenta el filtro con un caso (tweet, etiqueta)
	 * Si etiqueta es false: tweet antes era intereante y ahora no lo es.
	 * Si etiqueta es true: tweet antes no era interesante y ahora lo es.
	 * @param tweet tweet
	 * @param etiqueta etiqueta del tweet
	 * @throws ExcepcionTweetFilter si el tweet no existe
	 */
	public void retroalimentar(Tweet tweet, boolean etiqueta) throws ExcepcionTweetFilter {
		if (etiqueta == false) {
			if (!tweetsInteresantes.remove(tweet)) {
				throw new ExcepcionTweetFilter("No se pudo remover el tweet.");
			}
			tweetsDescartados.addFirst(tweet);
		} else {
			if (!tweetsDescartados.remove(tweet)) {
				throw new ExcepcionTweetFilter("No se pudo remover el tweet.");
			}
			tweetsInteresantes.addFirst(tweet);
		}
		
		this.entrenar(tweet, etiqueta);	
	}

	/**
	 * Crea un clasificador
	 * @param tipoClasificador tipo del clasificador
	 * @return clasificador
	 * @throws ExcepcionTweetFilter si no se pudo cargar el archivo de propiedades
	 * @throws ExcepcionTweetFilter si no se pudo crear el clasificador por reflexión
	 */
	private Clasificador nuevoClasificador() throws ExcepcionTweetFilter {
		Clasificador clasificador = null;

		//Lee archivo de configuración y obtiene nombre de clase para tipo de cronología
		Properties configuracion = new Properties();
		InputStream in = getClass().getResourceAsStream("/filtro.properties");

		try {
			configuracion.load(in);
		} catch (IOException e) {
			throw new ExcepcionTweetFilter("Hubo un problema al crear el filtro.", e);
		}

		String claseClasificador = configuracion.getProperty("clasificador");

		//Crea el clasificador mediante reflexión
		try {
			Class clase = Class.forName(claseClasificador);
			Constructor constructor = clase.getConstructor();
			clasificador = (Clasificador) constructor.newInstance();
		} catch (ClassNotFoundException | NoSuchMethodException | SecurityException | InstantiationException | IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
			throw new ExcepcionTweetFilter("Hubo un problema al crear el filtro.", e);
		}

		return clasificador; 
	}

	/**
	 * Construye un objeto Twitter para consumir recursos del REST API de Twitter
	 * @param CONSUMER_KEY identificador de la aplicación ante Twitter
	 * @param CONSUMER_SECRET clave de la aplicación ante Twitter
	 * @param ACCESS_TOKEN identificador del usuario ante Twitter
	 * @param ACCESS_TOKEN_SECRET clave del usuario ante Twitter
	 * @return twitter
	 */
	private Twitter nuevoTwitter(String CONSUMER_KEY, String CONSUMER_SECRET, String ACCESS_TOKEN, String ACCESS_TOKEN_SECRET) {
		Twitter twitter;
		TwitterFactory twitterFactory;
		ConfigurationBuilder confBuilder;

		confBuilder = new ConfigurationBuilder();
		confBuilder.setDebugEnabled(true)
		.setOAuthConsumerKey(CONSUMER_KEY)
		.setOAuthConsumerSecret(CONSUMER_SECRET)
		.setOAuthAccessToken(ACCESS_TOKEN)
		.setOAuthAccessTokenSecret(ACCESS_TOKEN_SECRET); 	
		twitterFactory = new TwitterFactory(confBuilder.build());
		twitter = twitterFactory.getInstance();

		return twitter;
	}
}
