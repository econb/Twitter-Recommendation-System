package presentacion;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.EventQueue;
import java.awt.FlowLayout;
import java.util.LinkedList;

import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

import logica.Tweet;

/**
 * Dialogo que muestra una cronología para entrenamiento.
 * @author Eddie Contreras
 */
public class DialogEntrenar extends JDialog {
	private JPanel panelTweets;
	private JScrollPane scrollPanelTweets;

	public DialogEntrenar(LinkedList<Tweet> cronologia, String IDfiltro) {
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
		
		for (Tweet tweet : cronologia) {
			JPanel panelTweet = new PanelTweetEntreno(tweet, IDfiltro, panelTweets);
			panelTweets.add(panelTweet);
		}
		
		//Operaciones sobre la ventana
		this.setTitle("Entrenamiento filtro");
		this.setSize(550, 500);
		this.setResizable(false);
		this.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE); //Operación al cerrar
		this.setLocationRelativeTo(null); //Centrar
		this.setVisible(true); //Hacer visible
	}
}
