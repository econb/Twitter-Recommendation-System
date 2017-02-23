package presentacion;

import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JOptionPane;
import javax.swing.JPanel;

import logica.Tweet;
import logica.TweetFilter;
import logica.excepciones.ExcepcionTweetFilter;

/**
 * Panel para mostrar un tweet descartado
 * @author Eddie Contreras
 */
public class PanelTweetDescartado extends PanelTweet {
	private JButton botonLike;

	public PanelTweetDescartado(Tweet tweet, String IDfiltro, JPanel panelPadre) {
		super(tweet, IDfiltro, panelPadre);

		//Botones de retroalimentación
		ImageIcon imgLike = new ImageIcon(getClass().getResource("/presentacion/like.png"));
		botonLike = new JButton(imgLike);
		
		panelBotones.add(botonLike);
		if (!tweet.getMedia().isEmpty()) {
			panelBotones.add(botonMedia);
		}

		//Eventos botones
		botonLike.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				eventoBotonLike(e);
			}
		});
	}
	
	/*
	 * Evento boton dislike
	 * Entrena el filtro con el caso (tweet, etiqueta)
	 * Remueve el tweet de la vista del usuario 
	 * @param e
	 */
	protected void eventoBotonLike(ActionEvent e) {
		try {
			TweetFilter.getInstancia().retroalimentarFiltro(IDfiltro, tweet, true);
			super.panelPadre.remove(this);
			
			/* No permite que se deformen los tweets cuando no son suficientes
			 * para llenar el panel */	
			int numTweets = panelPadre.getComponentCount(); 
			if (numTweets < 6) {
				for (int i=1; i <= 6 - numTweets; i++) {
					JPanel panelTweetVacio = new JPanel();
					panelTweetVacio.setPreferredSize(new Dimension(530, 90));
					panelPadre.add(panelTweetVacio);
				}
			}		
			super.panelPadre.revalidate();
		} catch (ExcepcionTweetFilter e1) {
			JOptionPane.showMessageDialog(this, e1.getMessage(), "error", JOptionPane.ERROR_MESSAGE);
		}
	}
}
