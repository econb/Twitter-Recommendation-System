package presentacion;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.net.MalformedURLException;
import java.net.URL;

import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextArea;


import logica.Tweet;

/**
 * Panel para mostrar un tweet
 * @author Eddie Contreras
 */
public abstract class PanelTweet extends JPanel {
	protected Tweet tweet;
	protected String IDfiltro;
	protected JPanel panelPadre;

	private JPanel panelImagen;
	private JLabel imagenPerfil;

	private JPanel panelInterno;
	private JTextArea textoTweet;

	private JPanel panelInternoSup;
	private JLabel labelAutor;
	private JLabel labelAutorSN;

	protected JPanel panelBotones;
	protected JButton botonMedia;

	/**
	 * Creador
	 */
	public PanelTweet(Tweet tweet, String IDfiltro, JPanel panelPadre) {	
		this.tweet = tweet;
		this.IDfiltro = IDfiltro;
		this.panelPadre = panelPadre;

		this.setPreferredSize(new Dimension(530, 90));
		this.setBackground(Color.white);
		this.setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY));
		this.setLayout(new BorderLayout());

		//Panel imagen
		panelImagen = new JPanel();
		this.add(panelImagen, BorderLayout.WEST);
		panelImagen.setPreferredSize(new Dimension(60, 60));
		panelImagen.setBackground(Color.WHITE);

		try {
			URL url = new URL(tweet.getAutorImagenUrl());
			ImageIcon imagen = new ImageIcon(url);
			imagenPerfil = new JLabel(imagen, JLabel.CENTER);
			panelImagen.add(imagenPerfil);		
		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		//Panel Interno
		panelInterno = new JPanel();
		this.add(panelInterno, BorderLayout.CENTER);
		panelInternoSup = new JPanel();
		panelInterno.setBackground(Color.WHITE);
		panelInternoSup.setBackground(Color.WHITE);

		panelInterno.setLayout(new BorderLayout());
		panelInterno.add(panelInternoSup, BorderLayout.NORTH);

		//Panel interno superior: autor
		labelAutor = new JLabel(tweet.getAutorNombre());
		labelAutorSN = new JLabel("@" + tweet.getAutor());
		labelAutorSN.setForeground(Color.GRAY);

		panelInternoSup.setLayout(new FlowLayout(FlowLayout.LEFT));
		panelInternoSup.add(labelAutor);
		panelInternoSup.add(labelAutorSN);

		//Panel interno: texto tweet
		textoTweet = new JTextArea();
		textoTweet.setText(tweet.getTexto());
		textoTweet.setEditable(false);
		textoTweet.setLineWrap(true);
		textoTweet.setWrapStyleWord(true);
		panelInterno.add(textoTweet, BorderLayout.CENTER);

		//Panel botones
		panelBotones = new JPanel();
		this.add(panelBotones, BorderLayout.EAST);
		panelBotones.setPreferredSize(new Dimension(30, 90));
		panelBotones.setBackground(Color.WHITE);

		ImageIcon imgMedia = new ImageIcon(getClass().getResource("/presentacion/media.jpg"));
		botonMedia = new JButton(imgMedia);

		/* Los botones (con sus imagenes) se agregan a panelBotones en los creadores
		 * de las implementaciones de panelTweet */
		panelBotones.setLayout(new GridLayout(3, 1));
		
		//Eventos botones
		botonMedia.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				eventoBotonMedia(e);
			}
		});
	}

	/*
	 * Evento botón media
	 */
	private void eventoBotonMedia(ActionEvent e) {
		if (!tweet.getMedia().isEmpty()) {
			JDialog dialogoMedia = new JDialog();
			JLabel labelMedia;
			String UrlMedia = tweet.getMedia().getFirst();

			try {
				URL url = new URL(UrlMedia);
				ImageIcon imagen = new ImageIcon(url);
				labelMedia = new JLabel(imagen);	

				dialogoMedia.setLayout(new BorderLayout());
				dialogoMedia.add(labelMedia, BorderLayout.CENTER);
				
				//Operaciones sobre la ventana Media
				dialogoMedia.setTitle("Media");
				dialogoMedia.setSize(500, 500);
				dialogoMedia.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE); //Operación al cerrar
				dialogoMedia.setLocationRelativeTo(null); //Centrar
				dialogoMedia.setVisible(true); //Hacer visible
			} catch (MalformedURLException e1) {
				JOptionPane.showMessageDialog(this, "Ha ocurrido un error al cargar la imagen.", "error", JOptionPane.ERROR_MESSAGE);
				e1.printStackTrace();
			}
		}
	}
}
