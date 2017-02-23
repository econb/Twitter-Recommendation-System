package presentacion;

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
 * Panel para mostrar un tweet para entrenamiento
 * @author Eddie Contreras
 */
public class PanelTweetEntreno extends PanelTweet {
	private JButton botonLike;
	private JButton botonDislike;

	public PanelTweetEntreno(Tweet tweet, String IDfiltro, JPanel panelPadre) {
		super(tweet, IDfiltro, panelPadre);

		//Botones de entrenamiento
		ImageIcon imgLike = new ImageIcon(getClass().getResource("/presentacion/like.png"));
		ImageIcon imgDislike = new ImageIcon(getClass().getResource("/presentacion/dislike.png"));	
		botonLike = new JButton(imgLike);
		botonDislike = new JButton(imgDislike);

		panelBotones.add(botonLike);
		panelBotones.add(botonDislike);
		if (!tweet.getMedia().isEmpty()) {
			panelBotones.add(botonMedia);
		}

		//Eventos botones
		botonLike.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				eventoBotonLike(e);
			}
		});

		botonDislike.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				eventoBotonDislike(e);
			}
		});
	}

	/*
	 * Evento boton like
	 * Entrena el filtro con el caso (tweet, etiqueta)
	 * @param e
	 */
	private void eventoBotonLike(ActionEvent e) {
		try {
			TweetFilter.getInstancia().entrenarFiltro(IDfiltro, tweet, true);
		} catch (ExcepcionTweetFilter e1) {
			JOptionPane.showMessageDialog(this, e1.getMessage(), "error", JOptionPane.ERROR_MESSAGE);
		}
	}

	/*
	 * Evento boton dislike
	 * Entrena el filtro con el caso (tweet, etiqueta)
	 * @param e
	 */
	private void eventoBotonDislike(ActionEvent e) {
		try {
			TweetFilter.getInstancia().entrenarFiltro(IDfiltro, tweet, false);
		} catch (ExcepcionTweetFilter e1) {
			JOptionPane.showMessageDialog(this, e1.getMessage(), "error", JOptionPane.ERROR_MESSAGE);
		}
	}
}
