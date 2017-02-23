package logica.filtro.clasificador.evaluacion;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.HashMap;


import logica.Tweet;

/**
 * Serializa y deserializa tweets que son usados para la validación cruzada
 * @author Eddie Contreras
 */
public class SerializadorTweets {
	private final String nombreArchivo = "mistweets.tw";
	private static SerializadorTweets instancia = null;

	private SerializadorTweets() {
	}
	
	public static SerializadorTweets getInstancia() {
		if (instancia == null) {
			instancia = new SerializadorTweets();
		}
		return instancia;
	}
	
	public void nuevoTweet(Tweet tweet, boolean etiqueta) {
		Entreno entreno = new Entreno(tweet, etiqueta);
		
		HashMap<String, Entreno> data = deserializar();
		data.put(tweet.getID(), entreno);
		serializar(data);
		
		System.out.println("//========================data====================//");
		System.out.println("#tweets: " + data.size());
		String key = tweet.getID();
		String text = data.get(key).getTweet().getTexto();
		boolean label = data.get(key).getEtiqueta();
		System.out.println(key + " : " + label + " : " + text);
		System.out.println("//------------------------------------------------//");
	}
	
	public void serializar(HashMap<String, Entreno> data) {
		ObjectOutputStream oos;
		
		try {
			File file = new File(nombreArchivo);
			if (!file.exists()) {
				file.createNewFile();
			}
			
			oos = new ObjectOutputStream(new FileOutputStream(file));
			oos.writeObject(data);
			oos.close();
		} catch (IOException e) {
			System.out.println("EXCEPCION: "+e.getMessage());
			e.printStackTrace();
		}
	}
	
	public HashMap<String, Entreno> deserializar() {
		ObjectInputStream ois;
		HashMap<String, Entreno> dataGuardada = new HashMap<String, Entreno>();
		
		try {
			File file = new File(nombreArchivo);
			if (!file.exists()) {
				file.createNewFile();
			}
			
			ois = new ObjectInputStream(new FileInputStream(file));
			dataGuardada = (HashMap<String, Entreno>) ois.readObject();
			ois.close();
		} catch (IOException | ClassNotFoundException e) {
			System.out.println("EXCEPCION: "+e.getMessage());
			e.printStackTrace();
		}
		
		return dataGuardada;
	}

}
