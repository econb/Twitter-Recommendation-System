package presentacion;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.EventQueue;
import java.util.LinkedList;

import javax.swing.BoxLayout;
import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

import logica.Tweet;

/**
 * Dialogo que muestra los tweets descartados
 * @author Eddie Contreras
 */
public class DialogDescartados extends JDialog {
	private JPanel panelTweets;
	private JScrollPane scrollPanelTweets;

	public DialogDescartados(LinkedList<Tweet> descartados, String IDfiltro) {
		this.getContentPane().setLayout(new BorderLayout());
		
		//Panel cronologia
		panelTweets = new JPanel();
		scrollPanelTweets = new JScrollPane(panelTweets);
		this.getContentPane().add(scrollPanelTweets, BorderLayout.CENTER);
		scrollPanelTweets.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
		scrollPanelTweets.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
		scrollPanelTweets.getVerticalScrollBar().setUnitIncrement(10);
		
		//Despliegue de la cronología
		panelTweets.setLayout(new BoxLayout(panelTweets, BoxLayout.Y_AXIS));	
		
		for (Tweet tweet : descartados) {
			JPanel panelTweet = new PanelTweetDescartado(tweet, IDfiltro, panelTweets);
			panelTweets.add(panelTweet);
		}
		
		/* No permite que se deformen los tweets cuando no son suficientes
		 * para llenar el panel */	
		int numTweets = panelTweets.getComponentCount(); 
		if (numTweets < 6) {
			for (int i=1; i <= 6 - numTweets; i++) {
				JPanel panelTweetVacio = new JPanel();
				panelTweetVacio.setPreferredSize(new Dimension(530, 90));
				panelTweets.add(panelTweetVacio);
			}
		}
		
		//Operaciones sobre la ventana
		this.setTitle("Tweets descartados");
		this.setSize(550, 500);
		this.setResizable(false);
		this.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE); //Operación al cerrar
		this.setLocationRelativeTo(null); //Centrar
		this.setVisible(true); //Hacer visible
	}
}
