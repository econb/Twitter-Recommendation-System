package presentacion;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.EventQueue;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.HashMap;
import java.util.LinkedList;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.Timer;
import javax.swing.border.EtchedBorder;

import logica.Tweet;
import logica.TweetFilter;
import logica.excepciones.ExcepcionTweetFilter;
import logica.excepciones.ExcepcionTwitterAPI;
import logica.excepciones.ExcepcionTwitterRateLimit;

/**
 * Ventana principal de la aplicación desde donde se pueden gestionar
 * filtros y ver tweets interesantes.
 * @author Eddie Contreras
 */
public class FramePrincipal extends JFrame implements ActionListener {
	private JPanel panelSuperior;
	private JPanel panelUsuario;
	private JPanel panelNuevoFiltro;
	private JLabel labelUsuario;
	private JButton botonNuevoFiltro;

	private JPanel panelIzquierdo;
	private JPanel panelFiltro;
	private JTextArea textoFiltro;
	private JScrollPane scrollTextoFiltro;
	private JPanel panelSelecFiltro;
	private JButton botonSigFiltro;
	private JButton botonPrevFiltro;
	private JPanel panelOpsFiltro;
	private JButton botonEntrenar;
	private JButton botonEncender;
	private JButton botonApagar;
	private JButton botonEliminar;

	private JPanel panelTweetsInteresantes;
	private JPanel panelOpsTweetsInt;
	private JButton botonVerTweetsDescartados;
	private JButton botonActualizarTweets;
	private JPanel panelTweets;
	private JScrollPane scrollPanelTweets;

	private LinkedList<HashMap<String, String>> filtros;
	private int indiceFiltroActual;
	private LinkedList<Tweet> cacheCronologiaEntrenar;

	private final Timer timer = new Timer(3000, this); //reloj que refresca automáticamente la ventana
	private int numTweetsMostrados;
	
	/**
	 * Create the frame.
	 */
	public FramePrincipal() {
		this.getContentPane().setLayout(new BorderLayout());	

		//Panel superior: usuario y nuevo filtro
		panelSuperior = new JPanel();
		this.getContentPane().add(panelSuperior, BorderLayout.NORTH);
		panelUsuario = new JPanel();
		panelNuevoFiltro = new JPanel();
		panelSuperior.setLayout(new GridLayout(1, 2));
		panelSuperior.add(panelUsuario);
		panelSuperior.add(panelNuevoFiltro);

		labelUsuario = new JLabel("", JLabel.LEFT);
		panelUsuario.setLayout(new FlowLayout(FlowLayout.LEFT));
		panelUsuario.add(labelUsuario);

		botonNuevoFiltro = new JButton("Nuevo filtro");
		panelNuevoFiltro.setLayout(new FlowLayout(FlowLayout.RIGHT));
		panelNuevoFiltro.add(botonNuevoFiltro);

		//Panel Izquierdo: filtro y opciones filtro
		panelIzquierdo = new JPanel();
		this.getContentPane().add(panelIzquierdo, BorderLayout.WEST);
		panelFiltro = new JPanel();
		panelFiltro.setPreferredSize(new Dimension(250, 200));
		panelOpsFiltro = new JPanel();

		panelIzquierdo.setLayout(new GridLayout(2, 1));
		panelIzquierdo.add(panelFiltro);
		panelIzquierdo.add(panelOpsFiltro);

		//Panel filtro
		panelFiltro.setBorder(BorderFactory.createTitledBorder(BorderFactory.createEtchedBorder(EtchedBorder.LOWERED), "Filtro"));		
		textoFiltro = new JTextArea();
		textoFiltro.setText("Aun no ha creado filtros");
		textoFiltro.setEditable(false);
		textoFiltro.setLineWrap(true);
		textoFiltro.setWrapStyleWord(true);
		scrollTextoFiltro = new JScrollPane(textoFiltro);	

		panelSelecFiltro = new JPanel();
		botonSigFiltro = new JButton(">");
		botonPrevFiltro = new JButton("<");
		panelSelecFiltro.setLayout(new GridLayout(1, 2));
		panelSelecFiltro.add(botonPrevFiltro);
		panelSelecFiltro.add(botonSigFiltro);

		panelFiltro.setLayout(new BorderLayout());
		panelFiltro.add(scrollTextoFiltro, BorderLayout.CENTER);
		panelFiltro.add(panelSelecFiltro, BorderLayout.SOUTH);

		//Panel opciones filtro
		botonEntrenar = new JButton("Entrenar");
		botonEncender = new JButton("Encender");
		botonApagar = new JButton("Apagar");
		botonEliminar = new JButton("Eliminar");

		panelOpsFiltro.setLayout(new GridLayout(6, 1));
		panelOpsFiltro.add(botonEntrenar);
		panelOpsFiltro.add(botonEncender);
		panelOpsFiltro.add(botonApagar);
		panelOpsFiltro.add(botonEliminar);

		//Panel tweets interesantes
		panelTweetsInteresantes = new JPanel();
		this.getContentPane().add(panelTweetsInteresantes, BorderLayout.CENTER);
		panelTweetsInteresantes.setBorder(BorderFactory.createTitledBorder(BorderFactory.createEtchedBorder(EtchedBorder.LOWERED), "Tweets interesantes"));		
		panelTweetsInteresantes.setLayout(new BorderLayout());

		//panel actualizar tweets interesantes
		panelOpsTweetsInt = new JPanel();
		panelTweetsInteresantes.add(panelOpsTweetsInt, BorderLayout.SOUTH);
		botonVerTweetsDescartados = new JButton("Ver descartados");
		botonActualizarTweets = new JButton("Actualizar (0)");
		panelOpsTweetsInt.setLayout(new FlowLayout(FlowLayout.RIGHT));
		panelOpsTweetsInt.add(botonActualizarTweets);
		panelOpsTweetsInt.add(botonVerTweetsDescartados);

		//Panel cronologia
		panelTweets = new JPanel();
		scrollPanelTweets = new JScrollPane(panelTweets);	
		panelTweetsInteresantes.add(scrollPanelTweets, BorderLayout.CENTER);
		scrollPanelTweets.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
		scrollPanelTweets.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
		scrollPanelTweets.getVerticalScrollBar().setUnitIncrement(10);
		panelTweets.setLayout(new BoxLayout(panelTweets, BoxLayout.Y_AXIS));	

		//Eventos botones
		botonNuevoFiltro.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				eventoBotonNuevoFiltro(e);	
			}
		});

		botonSigFiltro.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				eventoBotonSigFiltro(e);	
			}
		});

		botonPrevFiltro.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				eventoBotonPrevFiltro(e);	
			}
		});

		botonEntrenar.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				eventoBotonEntrenar(e);	
			}
		});

		botonEncender.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				eventoBotonEncender(e);	
			}
		});

		botonApagar.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				eventoBotonApagar(e);	
			}
		});

		botonEliminar.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				eventoBotonEliminar(e);	
			}
		});

		botonVerTweetsDescartados.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				eventoBotonVerTweetsDescartados(e);	
			}
		});

		botonActualizarTweets.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				eventoBotonActualizarTweets(e);
			}
		});

		//Carga información existente
		filtros = new LinkedList<HashMap<String, String>>();
		cacheCronologiaEntrenar = new LinkedList<Tweet>();
		String usuario = TweetFilter.getInstancia().getUsuarioAutenticado();
		labelUsuario.setText(usuario);
		indiceFiltroActual = 0;
		numTweetsMostrados = 0;
		this.actualizarPanelFiltro();
		this.eventoBotonActualizarTweets(null);

		//Operaciones sobre la ventana
		this.setTitle("TweetFilter");
		this.setSize(810, 500);
		this.setResizable(false);
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE); //Operación al cerrar
		this.setLocationRelativeTo(null); //Centrar
		this.setVisible(true); //Hacer visible

		timer.start();//timer que refresca automáticamente la ventana
	}

	/*
	 * Actualiza el panel de información de filtros con el filtro actual.
	 */
	private void actualizarPanelFiltro() {
		try {
			filtros = TweetFilter.getInstancia().consultarFiltros();

			if (!filtros.isEmpty()) {
				String texto = filtros.get(indiceFiltroActual).get("nombre").toUpperCase() + 
						"\n\n" + filtros.get(indiceFiltroActual).get("descripcion") +
						"\n\nCronologia: " + filtros.get(indiceFiltroActual).get("cronologia") +
						"\n\nEstado: " + filtros.get(indiceFiltroActual).get("estado") +
						"\n\n# tweets marcados como:" + 
						"\n    interesantes: " + filtros.get(indiceFiltroActual).get("positivos") + 
						"\n    no interesantes: " + filtros.get(indiceFiltroActual).get("negativos") +
						"\n\nNOTA: procure que el número de tweets marcados interesantes y no interesantes sean similares o iguales para obtener mejores resultados.";
				textoFiltro.setText(texto);	
			}
			this.refrescar();		
		} catch (ExcepcionTweetFilter e) {
			JOptionPane.showMessageDialog(this, e.getMessage(), "error", JOptionPane.ERROR_MESSAGE);
		}
	}

	/*
	 * Actualiza los tweets interesantes del filtro actual
	 */
	private void eventoBotonActualizarTweets(ActionEvent e) {
		String IDfiltro;
		LinkedList<Tweet> interesantes;

		try {
			filtros = TweetFilter.getInstancia().consultarFiltros();

			if (!filtros.isEmpty()) {
				IDfiltro = filtros.get(indiceFiltroActual).get("nombre");

				try {
					panelTweets.removeAll();
					interesantes = TweetFilter.getInstancia().consultarTweetsInteresantes(IDfiltro);
					this.numTweetsMostrados = interesantes.size();
					
					for (Tweet tweet : interesantes) {
						JPanel panelTweet = new PanelTweetInteresante(tweet, IDfiltro, panelTweets);
						panelTweets.add(panelTweet);
					}

					/* No permite que se deformen los tweets cuando no son suficientes
					 * para llenar el panel */	
					int numTweets = panelTweets.getComponentCount(); 
					if (numTweets < 5) {
						for (int i=1; i <= 5 - numTweets; i++) {
							JPanel panelTweetVacio = new JPanel();
							panelTweetVacio.setPreferredSize(new Dimension(530, 90));
							panelTweets.add(panelTweetVacio);
						}
					}		
				} catch (ExcepcionTweetFilter e1) {
					JOptionPane.showMessageDialog(this, e1.getMessage(), "error", JOptionPane.ERROR_MESSAGE);
				}
				this.refrescar();
			}	
		} catch (ExcepcionTweetFilter e2) {
			JOptionPane.showMessageDialog(this, e2.getMessage(), "error", JOptionPane.ERROR_MESSAGE);
		}
	}

	/*
	 * Evento botón nuevo filtro
	 */
	private void eventoBotonNuevoFiltro(ActionEvent e) {
		DialogNuevoFiltro dialogoNuevoFiltro = new DialogNuevoFiltro();
	}

	/*
	 * Evento botón siguiente filtro
	 */
	private void eventoBotonSigFiltro(ActionEvent e) {
		if (!filtros.isEmpty() && (indiceFiltroActual + 1) < filtros.size()) {
			indiceFiltroActual++;
			this.actualizarPanelFiltro();
			this.eventoBotonActualizarTweets(null);
		}
	}

	/*
	 * Evento botón anterior filtro
	 */
	private void eventoBotonPrevFiltro(ActionEvent e) {
		if (!filtros.isEmpty() && (indiceFiltroActual - 1) >= 0 ) {
			indiceFiltroActual--;
			this.actualizarPanelFiltro();
			this.eventoBotonActualizarTweets(null);
		}
	}

	/*
	 * Evento botón entrenar filtro
	 */
	private void eventoBotonEntrenar(ActionEvent e) {
		String IDfiltro;
		DialogEntrenar dialogoEntrenar;

		if (!filtros.isEmpty()) {
			IDfiltro = filtros.get(indiceFiltroActual).get("nombre");
			try {
				cacheCronologiaEntrenar = TweetFilter.getInstancia().consultarCronologia(IDfiltro);
				dialogoEntrenar = new DialogEntrenar(cacheCronologiaEntrenar, IDfiltro);
			} catch (ExcepcionTweetFilter e1) {
				JOptionPane.showMessageDialog(this, e1.getMessage(), "error", JOptionPane.ERROR_MESSAGE);
			} catch (ExcepcionTwitterAPI | ExcepcionTwitterRateLimit e1) {
				if (!cacheCronologiaEntrenar.isEmpty()) {
					dialogoEntrenar = new DialogEntrenar(cacheCronologiaEntrenar, IDfiltro);
				} else {
					JOptionPane.showMessageDialog(this, e1.getMessage(), "error", JOptionPane.ERROR_MESSAGE);
				}
			} 
		}
	}

	/*
	 * Evento botón encender filtro
	 */
	private void eventoBotonEncender(ActionEvent e) {
		String IDfiltro;

		if (!filtros.isEmpty()) {
			IDfiltro = filtros.get(indiceFiltroActual).get("nombre");
			try {
				TweetFilter.getInstancia().encenderFiltro(IDfiltro);
				actualizarPanelFiltro();
			} catch (ExcepcionTweetFilter e1) {
				JOptionPane.showMessageDialog(this, e1.getMessage(), "error", JOptionPane.ERROR_MESSAGE);
			}
			this.actualizarPanelFiltro();
		}	
	}

	/*
	 * Evento botón apagar filtro
	 */
	private void eventoBotonApagar(ActionEvent e) {
		String IDfiltro;

		if (!filtros.isEmpty()) {
			IDfiltro = filtros.get(indiceFiltroActual).get("nombre");
			try {
				TweetFilter.getInstancia().apagarFiltro(IDfiltro);
			} catch (ExcepcionTweetFilter e1) {
				JOptionPane.showMessageDialog(this, e1.getMessage(), "error", JOptionPane.ERROR_MESSAGE);
			}
			this.actualizarPanelFiltro();
		}
	}

	/*
	 * Evento botón eliminar filtro
	 */
	private void eventoBotonEliminar(ActionEvent e) {
		//Eliminar el filtro actual
	}

	/*
	 * Evento botón ver tweets descartados
	 */
	private void eventoBotonVerTweetsDescartados(ActionEvent e) {
		String IDfiltro;
		LinkedList<Tweet> descartados;
		DialogDescartados dialogoDescartados;

		if (!filtros.isEmpty()) {
			IDfiltro = filtros.get(indiceFiltroActual).get("nombre");
			try {
				descartados = TweetFilter.getInstancia().consultarTweetsDescartados(IDfiltro);
				dialogoDescartados = new DialogDescartados(descartados, IDfiltro);
			} catch (ExcepcionTweetFilter e1) {
				JOptionPane.showMessageDialog(this, e1.getMessage(), "error", JOptionPane.ERROR_MESSAGE);
			}
		}
	}

	/*
	 * Refresca la pantalla
	 */
	private void refrescar() {
		this.revalidate();
	}

	/*
	 * Evento del timer para refrescar la ventana
	 */ 
	@Override
	public void actionPerformed(ActionEvent e) {
		try {
			filtros = TweetFilter.getInstancia().consultarFiltros();

			if (!filtros.isEmpty()) {
				this.actualizarPanelFiltro();	

				String IDfiltro = filtros.get(indiceFiltroActual).get("nombre");
				LinkedList<Tweet> interesantes = TweetFilter.getInstancia().consultarTweetsInteresantes(IDfiltro);
				int numTweetsReales = interesantes.size();
				int numTweetsNuevos = numTweetsReales - this.numTweetsMostrados;

				if (numTweetsNuevos < 0) {
					numTweetsNuevos = 0;
				}
				
				botonActualizarTweets.setText("Actualizar (" + numTweetsNuevos + ")");
				this.refrescar();
			}
		} catch (ExcepcionTweetFilter e1) {
			JOptionPane.showMessageDialog(this, e1.getMessage(), "error", JOptionPane.ERROR_MESSAGE);
		}
	}
}
