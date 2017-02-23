package persistencia;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.LinkedList;

import logica.TweetFilter;
import logica.Usuario;
import logica.excepciones.ExcepcionTweetFilter;
import logica.filtro.Filtro;
import logica.filtro.clasificador.Clasificador;

/**
 * Mecanismo de persistencia en archivos de texto plano.
 * Actualmente solo guarda usuarios y sus filtros con clasificadores.
 * No guarda tweets (interesantes o descartados). 
 * @author Eddie Contreras
 */
public class PersistenciaTexto {
	public static PersistenciaTexto instancia;
	private static final String nombreArchivo = "datos.txt";

	private PersistenciaTexto() {}

	public static PersistenciaTexto getInstancia() {
		if (instancia == null) {
			instancia = new PersistenciaTexto();
		}
		return instancia;
	}

	public void guardar() {
		TweetFilter tweetFilter = TweetFilter.getInstancia();
		File archivo;
		FileWriter fw;
		BufferedWriter bw;

		try {
			archivo = new File(nombreArchivo);

			if (!archivo.exists()) {
				archivo.createNewFile();
			}
			fw = new FileWriter(archivo.getAbsoluteFile());
			bw = new BufferedWriter(fw);

			//Guarda usuario por usuario
			bw.write(String.valueOf(tweetFilter.getUsuarios().size())); bw.newLine();
			for (String key: tweetFilter.getUsuarios().keySet()) {
				//USUARIO
				bw.write("**USUARIO**"); bw.newLine();
				Usuario usuario = tweetFilter.getUsuarios().get(key);
				bw.write(usuario.getNombre()); bw.newLine();
				bw.write(usuario.getPassword()); bw.newLine();
				bw.write(usuario.getAccessToken()); bw.newLine();
				bw.write(usuario.getAccessTokenSecret()); bw.newLine();		

				//Guarda filtro por filtro
				bw.write(String.valueOf(usuario.getListaFiltros().size())); bw.newLine();
				for (Filtro filtro : usuario.getListaFiltros()) {
					//FILTRO
					bw.write("**FILTRO**"); bw.newLine();
					bw.write(filtro.getNombre()); bw.newLine();
					bw.write(filtro.getDescripcion()); bw.newLine();
					bw.write(filtro.getTipoCronologia()); bw.newLine();
					bw.write(String.valueOf(filtro.getNumCasosEntrenoPositivos())); bw.newLine();
					bw.write(String.valueOf(filtro.getNumCasosEntrenoNegativos())); bw.newLine();
					
					//Guarda clasificador
					Clasificador clasificador = filtro.getClasificador();
					bw.write(String.valueOf(clasificador.getDatos().size())); bw.newLine();
					for (String cadena : clasificador.getDatos()) {
						bw.write(cadena); bw.newLine();
					}
				}
			}
			bw.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public HashMap<String, Usuario> recuperar() {
		HashMap<String, Usuario> usuarios = new HashMap<String, Usuario>();
		File archivo;
		FileReader fr;
		BufferedReader br;
		String line;

		try {
			archivo = new File(nombreArchivo);	

			if (archivo.exists()) {
				fr = new FileReader(archivo.getAbsoluteFile());
				br = new BufferedReader(fr);
				
				//Recupera usuario por usuario
				int numUsuarios = Integer.parseInt(br.readLine());
				for (int i = 1; i <= numUsuarios; i++) {
					if (br.readLine().equals("**USUARIO**")) {
						//Lee info usuario
						String nombre = br.readLine(); 
						String password = br.readLine(); 
						String accessToken = br.readLine(); 
						String accessTokenSecret = br.readLine(); 

						//Crea usuario
						Usuario usuario = new Usuario(nombre, password);
						usuario.setAccessToken(accessToken);
						usuario.setAccessTokenSecret(accessTokenSecret);
						usuarios.put(nombre, usuario);

						//Recupera filtro por filtro
						int numFiltros = Integer.parseInt(br.readLine());
						for (int j = 1; j <= numFiltros; j++) {
							if (br.readLine().equals("**FILTRO**")) {
								//Lee info filtro
								String nombreFiltro = br.readLine();
								String descripcion = br.readLine();
								String tipoCronologia = br.readLine();
								int numCasosEntrenoPositivos = Integer.parseInt(br.readLine());
								int numCasosEntrenoNegativos = Integer.parseInt(br.readLine());	
								String consumerKey = TweetFilter.getInstancia().getConsumerKey();
								String consumerSecret = TweetFilter.getInstancia().getConsumerSecret();
								
								//Crea filtro
								try {
									usuario.crearFiltro(consumerKey, consumerSecret, nombreFiltro, descripcion, tipoCronologia, null);
									Filtro filtro = usuario.buscarFiltro(nombreFiltro);
									filtro.setNumCasosEntrenoPositivos(numCasosEntrenoPositivos);
									filtro.setNumCasosEntrenoNegativos(numCasosEntrenoNegativos);
									
									//Recupera clasificador
									LinkedList<String> datosClasificador = new LinkedList<String>();
									int numDatosClasificador = Integer.parseInt(br.readLine()); 
									for (int k = 1; k <= numDatosClasificador; k++) {
										datosClasificador.add(br.readLine());
									}
									filtro.getClasificador().setDatos(datosClasificador);
								} catch (ExcepcionTweetFilter e) {
									e.printStackTrace();
								}	
							}
						}
					}
				}
				br.close();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		return usuarios;
	}

}
