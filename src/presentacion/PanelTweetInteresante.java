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
 * Panel para mostrar un tweet interesante
 * @author Eddie Contreras
 */
public class PanelTweetInteresante extends PanelTweet {
	private JButton botonDislike;

	public PanelTweetInteresante(Tweet tweet, String IDfiltro, JPanel panelPadre) {
		super(tweet, IDfiltro, panelPadre);

		//Botones de retroalimentación
		ImageIcon imgDislike = new ImageIcon(getClass().getResource("/presentacion/dislike.png"));
		botonDislike = new JButton(imgDislike);

		panelBotones.add(botonDislike);
		if (!tweet.getMedia().isEmpty()) {
			panelBotones.add(botonMedia);
		}

		//Eventos botones
		botonDislike.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				eventoBotonDislike(e);
			}
		});
	}

	/*
	 * Evento boton dislike
	 * Entrena el filtro con el caso (tweet, etiqueta)
	 * Remueve el tweet de la vista del usuario 
	 * @param e
	 */
	protected void eventoBotonDislike(ActionEvent e) {
		try {
			TweetFilter.getInstancia().retroalimentarFiltro(IDfiltro, tweet, false);
			super.panelPadre.remove(this);
			
			/* No permite que se deformen los tweets cuando no son suficientes
			 * para llenar el panel */	
			int numTweets = panelPadre.getComponentCount(); 
			if (numTweets < 5) {
				for (int i=1; i <= 5 - numTweets; i++) {
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
