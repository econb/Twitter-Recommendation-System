package logica;

import logica.excepciones.ExcepcionTwitterAPI;
import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.TwitterFactory;
import twitter4j.auth.AccessToken;
import twitter4j.auth.RequestToken;
import twitter4j.conf.ConfigurationBuilder;

/**
 * PIN-based authorization 
 * https://dev.twitter.com/docs/auth/pin-based-authorization
 * @author Eddie Contreras
 */
public class TwitterPINBasedAuth {
	private Twitter twitter;
	private RequestToken requestToken;
	
	public TwitterPINBasedAuth() {}

	/**
	 * Obtiene una URL necesaria para la autorización basada en PIN
	 * @param CONSUMER_KEY identificador de la aplicación ante Twitter
	 * @param CONSUMER_SECRET clave de la aplicación ante Twitter
	 * @return url url necesaria para que el usuario obtenga un PIN
	 * @throws ExcepcionTwitterAPI si hay un problema en la conexión con Twitter
	 */
	public String getURLAutorizacion(String CONSUMER_KEY, String CONSUMER_SECRET) throws ExcepcionTwitterAPI {
		String url;
		TwitterFactory twitterFactory;
		ConfigurationBuilder confBuilder;
		
		twitter = null;
		requestToken = null;
		confBuilder = new ConfigurationBuilder();
		confBuilder.setDebugEnabled(true)
				.setOAuthConsumerKey(CONSUMER_KEY)
				.setOAuthConsumerSecret(CONSUMER_SECRET);
		twitterFactory = new TwitterFactory(confBuilder.build());
		twitter = twitterFactory.getInstance();
		
		/* The Request Token's sole purpose is to receive User approval and can only be used to obtain an Access Token 
		 * http://oauth.net/core/1.0a/#auth_step1 */
		try {
			requestToken = twitter.getOAuthRequestToken();
		} catch (TwitterException e) {
			throw new ExcepcionTwitterAPI("Hubo un problema en la conexión con Twitter.", e);
		}
		url = requestToken.getAuthorizationURL();
		
		return url;
	}
	
	/**
	 * Autoriza la aplicación para hacer peticiones a Twitter en nombre de un usuario
	 * @param PIN el PIN que obtuvo el usuario con la URL
	 * @throws ExcepcionTwitterAPI si hay un problema en la conexión con Twitter
	 * @return accessToken token y clave del usuario
	 */
	public String[] autorizarAplicacion(String PIN) throws ExcepcionTwitterAPI { 
		AccessToken accessToken;
		String[] accessTokenStr = new String[2];
		
		try {
			accessToken = twitter.getOAuthAccessToken(requestToken, PIN);
		} catch (TwitterException e) {
			throw new ExcepcionTwitterAPI("Hubo un problema en la conexión con Twitter.", e);
		}

		accessTokenStr[0] = accessToken.getToken();
		accessTokenStr[1] = accessToken.getTokenSecret();
		twitter = null;
		requestToken = null;
		
		return accessTokenStr;
	}
}
