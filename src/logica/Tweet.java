package logica;

import java.io.Serializable;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.LinkedList;

import twitter4j.HashtagEntity;
import twitter4j.MediaEntity;
import twitter4j.Status;
import twitter4j.URLEntity;
import twitter4j.UserMentionEntity;

/**
 * Representación de un tweet propia de la aplicación
 * @author Eddie Contreras
 */
public class Tweet implements Serializable {
	private static final long serialVersionUID = -6535844367784416994L;
	private String autor; //ScreenName del autor sin '@'
	private String autorNombre; //nombre del autor
	private String autorImagenUrl; //url de la imagen de perfil del autor
	private String ID; //id del tweet
	private String texto; //texto, si es retweet agrega "RT @ScreenName:"
	private Calendar fecha; //fecha de creación
	private String lugar; //ubicación geográfica
	private long numRetweets; //Número de retweets
	private boolean esFavorito; //es favorito por el usuario?
	private boolean esInapropiado; //tiene un enlace identificado como inapropiado?
	private boolean esRetweet; //es retweet?
	private boolean esRetweetPorUsuario; //ha sido retwitteado por el usuario?
	private boolean esRespuesta; //es una respuesta a otro tweet?
	private LinkedList<String> hashTags; //hashtags sin '#'
	private LinkedList<String> media; //url's de fotos
	private LinkedList<String> urls; //url's no acortadas
	private LinkedList<String> menciones; //screenNames de menciones sin '@'
	
	/**
	 * Creador
	 * Transforma un tweet de la representación usada por la libreria Twitter4j 
	 * a la representación propia de la aplicación.
	 * @param estado
	 */
	public Tweet(Status estado) {
		autor = estado.getUser().getScreenName();
		autorNombre = estado.getUser().getName();
		autorImagenUrl = estado.getUser().getProfileImageURL();	
		ID = String.valueOf(estado.getId());	
		texto = estado.getText();	
		fecha = new GregorianCalendar();
		fecha.setTime(estado.getCreatedAt());	
		lugar = (estado.getPlace() != null) ? estado.getPlace().getFullName() : "";
		numRetweets = estado.getRetweetCount();
		esFavorito = estado.isFavorited();	//CUANDO ES RT NO MARCA COMO FAV
		esInapropiado = estado.isPossiblySensitive();
		esRetweet = estado.isRetweet();
		esRetweetPorUsuario = estado.isRetweetedByMe();
		esRespuesta = (estado.getInReplyToScreenName() != null);
		
		hashTags = new LinkedList<String>();
		for (HashtagEntity hashTagEnt : estado.getHashtagEntities()) {
			hashTags.add(hashTagEnt.getText());
		}		
		
		media = new LinkedList<String>();
		for (MediaEntity mediaEnt : estado.getMediaEntities()) {
			if (mediaEnt.getType().equals("photo")) {
				media.add(mediaEnt.getMediaURL());
			}		
		}		
		
		urls = new LinkedList<String>();
		for (URLEntity urlEnt : estado.getURLEntities()) {
			urls.add(urlEnt.getExpandedURL());
		}
		
		menciones = new LinkedList<String>();
		for (UserMentionEntity mentionEnt : estado.getUserMentionEntities()) {
			menciones.add(mentionEnt.getScreenName());
		}
		
		
	}
	
	public String getID() {
		return ID;
	}
	
	public String getTexto() {
		return texto;
	}
	
	public long getNumRetweets() {
		return numRetweets;
	}
	
	public boolean esRetweet() {
		return esRetweet;
	}
	
	public boolean esRetweetPorUsuario() {
		return esRetweetPorUsuario;
	}
	
	public boolean esFavorito() {
		return esFavorito;
	}
	
	public boolean esRespuesta() {
		return esRespuesta;
	}
	
	public boolean esInapropiado() {
		return esInapropiado;
	}
	
	public LinkedList<String> getHashTags() {
		return hashTags;
	}
	
	public LinkedList<String> getMedia() {
		return media;
	}
	
	public LinkedList<String> getUrls() {
		return urls;
	}
	
	public LinkedList<String> getMenciones() {
		return menciones;
	}
	
	public String getAutor() {
		return autor;
	}
	
	public String getAutorNombre() {
		return autorNombre;
	}
	
	public String getAutorImagenUrl() {
		return autorImagenUrl;
	}
	
	public Calendar getFecha() {
		return fecha;
	}
}
