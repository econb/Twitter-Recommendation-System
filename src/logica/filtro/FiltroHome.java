package logica.filtro;

import java.util.LinkedList;
import java.util.Map;

import logica.Tweet;
import logica.excepciones.ExcepcionTweetFilter;
import logica.excepciones.ExcepcionTwitterAPI;
import logica.excepciones.ExcepcionTwitterRateLimit;

import twitter4j.Paging;
import twitter4j.RateLimitStatus;
import twitter4j.ResponseList;
import twitter4j.Status;
import twitter4j.TwitterException;
import twitter4j.TwitterStream;
import twitter4j.TwitterStreamFactory;
import twitter4j.UserStreamListener;
import twitter4j.conf.ConfigurationBuilder;

/**
 * Una implementación de filtro para la cronologia "Home" con filtrado en tiempo real.
 * Cada vez que hay un nuevo tweet en la cronologia Home en Twitter,
 * el filtro lo toma y lo clasifica inmediatamente.
 * @author Eddie Contreras
 */
public class FiltroHome extends Filtro {
	private TwitterStream twitterStream;
	
	/**
	 * Creador
	 * @param CONSUMER_KEY identificador de la aplicación ante Twitter
	 * @param CONSUMER_SECRET clave de la aplicación ante Twitter
	 * @param ACCESS_TOKEN identificador del usuario ante Twitter
	 * @param ACCESS_TOKEN_SECRET clave del usuario ante Twitter
	 * @param nombre nombre del filtro
	 * @param descripcion descripción del filtro
	 * @param otrosParams otros parametros que pueden ser incluidos o no dependiendo del tipo de filtro 
	 * @throws ExcepcionTweetFilter si no se pudo cargar el archivo de propiedades
	 * @throws ExcepcionTweetFilter si no se pudo crear el clasificador por reflexión
	 */
	public FiltroHome(String CONSUMER_KEY, String CONSUMER_SECRET, String ACCESS_TOKEN, String ACCESS_TOKEN_SECRET, String nombre, String descripcion, String[] otrosParams) throws ExcepcionTweetFilter {	
		super(CONSUMER_KEY, CONSUMER_SECRET, ACCESS_TOKEN, ACCESS_TOKEN_SECRET, nombre, descripcion, "Home");
		twitterStream = nuevoTwitterStream(CONSUMER_KEY, CONSUMER_SECRET, ACCESS_TOKEN, ACCESS_TOKEN_SECRET);
	}
	
	/**
	 * Retorna la cronología asociada al filtro
	 * Obtiene aprox. 800 tweets (máximo permitido por Twitter)
	 * http://shkspr.mobi/blog/2010/06/twitter-api-pagination-and-ids/
	 * https://dev.twitter.com/docs/working-with-timelines
	 * https://dev.twitter.com/docs/rate-limiting/1.1
	 * http://twitter4j.org/javadoc/twitter4j/api/HelpResources.html#getRateLimitStatus%28%29
	 * @return cronología la cronologia
	 * @throws ExcepcionTwitterAPI si hay un problema en la conexión con Twitter
	 * @throws ExcepcionTwitterRateLimit si no se permite hacer más llamados al REST API de Twitter
	 */
	@Override
	public LinkedList<Tweet> getCronologia() throws ExcepcionTwitterAPI, ExcepcionTwitterRateLimit {
		Paging paginacion;
		ResponseList<Status> estados;
		Map<String, RateLimitStatus> rateLimitMap;
		RateLimitStatus rateLimitHome;		 	
		int MAX_TWEETS = 200;
		int MAX_PAGINAS = 4;
		long MAX_ID;
		LinkedList<Tweet> cronologia = new LinkedList<Tweet>();

		//Verifica rate limiting 
		try {
			rateLimitMap  = twitter.getRateLimitStatus("statuses");
			rateLimitHome = rateLimitMap.get("/statuses/home_timeline");

			if (rateLimitHome.getRemaining() < rateLimitHome.getLimit()) {
				throw new ExcepcionTwitterRateLimit("En " + rateLimitHome.getSecondsUntilReset() +
						" segundos se podrá consultar de nuevo la cronología Home.");
			}
		} catch (TwitterException e) {
			throw new ExcepcionTwitterAPI("Hubo un problema en la conexión con Twitter.", e);
		}

		//Primera página (max 200 tweets)
		try {
			paginacion = new Paging(1, MAX_TWEETS);
			estados = twitter.getHomeTimeline(paginacion);

			for (Status estado: estados) {
				Tweet tweet = new Tweet(estado);
				cronologia.add(tweet);
			}
		} catch (TwitterException e) {
			throw new ExcepcionTwitterAPI("Hubo un problema en la conexión con Twitter.", e);
		}

		//Otras páginas (max 200 tweets por página, hasta completar 800 tweets)
		try {
			rateLimitMap  = twitter.getRateLimitStatus("statuses");
			rateLimitHome = rateLimitMap.get("/statuses/home_timeline");

			while ((rateLimitHome.getLimit() - rateLimitHome.getRemaining() < MAX_PAGINAS) && estados.size() > 1) {
				MAX_ID = estados.get(estados.size() - 1).getId();
				paginacion = new Paging(1, MAX_TWEETS, 1, MAX_ID);
				estados = twitter.getHomeTimeline(paginacion);

				//Remueve tweet repetido
				if (!estados.isEmpty()) {
					estados.remove(0);
				}
				for (Status estado: estados) {
					Tweet tweet = new Tweet(estado);
					cronologia.add(tweet);
				}
				
				rateLimitMap  = twitter.getRateLimitStatus("statuses");
				rateLimitHome = rateLimitMap.get("/statuses/home_timeline");
			}
		} catch (TwitterException e) {
			/*Si hay un problema en la conexión con Twitter, se ignora
			 *y se retorna la lista de tweets tal como esta.*/
			e.printStackTrace();
		}
		
		return cronologia;
	}
	
	/**
	 * Enciende el filtro para que comience a filtrar su cronología asociada
	 * @throws ExcepcionTweetFilter si el filtro ya está encendido
	 */
	@Override
	public void encender() throws ExcepcionTweetFilter {
		if (estado == true) {
			throw new ExcepcionTweetFilter("El filtro ya está encendido.");
		}
		
		twitterStream.user();
		estado = true;
	}
	
	/**
	 * Apaga el filtro para que se detenga de filtrar su cronología asociada
	 * @throws ExcepcionTweetFilter si el filtro ya está apagado
	 */
	@Override
	public void apagar() throws ExcepcionTweetFilter {
		if (estado == false) {
			throw new ExcepcionTweetFilter("El filtro ya está apagado.");
		}
		
		twitterStream.cleanUp();
		estado = false;
	}

	/**
	 * Construye un objeto TwitterStream para interactuar con el Streaming API de Twitter
	 * @param CONSUMER_KEY identificador de la aplicación ante Twitter
	 * @param CONSUMER_SECRET clave de la aplicación ante Twitter
	 * @param ACCESS_TOKEN identificador del usuario ante Twitter
	 * @param ACCESS_TOKEN_SECRET clave del usuario ante Twitter
	 * @return twitterStream
	 */
	private TwitterStream nuevoTwitterStream(String CONSUMER_KEY, String CONSUMER_SECRET, String ACCESS_TOKEN, String ACCESS_TOKEN_SECRET) {
		TwitterStream twitterStream;
		TwitterStreamFactory twitterStreamFactory;
		ConfigurationBuilder confBuilder;
		UserStreamListener listener; 
		
		confBuilder = new ConfigurationBuilder();
		confBuilder.setDebugEnabled(true)
					.setOAuthConsumerKey(CONSUMER_KEY)
					.setOAuthConsumerSecret(CONSUMER_SECRET)
					.setOAuthAccessToken(ACCESS_TOKEN)
					.setOAuthAccessTokenSecret(ACCESS_TOKEN_SECRET);
		twitterStreamFactory = new TwitterStreamFactory(confBuilder.build());
		twitterStream = twitterStreamFactory.getInstance();
		
		listener = new FiltroHomeUserStreamListener(this);
		twitterStream.addListener(listener);
		
		return twitterStream;
	}

}
