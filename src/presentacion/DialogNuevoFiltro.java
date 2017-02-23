package presentacion;

import java.awt.BorderLayout;
import java.awt.EventQueue;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;

import logica.TweetFilter;
import logica.excepciones.ExcepcionTweetFilter;

/**
 * Dialogo para crear un nuevo filtro
 * @author Eddie Contreras
 */
public class DialogNuevoFiltro extends JDialog {
	private JPanel panelCampos;
	private JLabel labelNombre;
	private JTextField textoNombre;
	private JLabel labelDescripcion;
	private JTextField textoDescripcion;
	private JLabel labelCronologia;
	private JComboBox<String> listaCronologias;

	private JPanel panelBotones;
	private JButton botonOK;
	private JButton botonCancelar;

	/**
	 * Create the dialog.
	 */
	public DialogNuevoFiltro() {
		this.getContentPane().setLayout(new BorderLayout());

		//Panel botones
		panelBotones = new JPanel();
		this.getContentPane().add(panelBotones, BorderLayout.SOUTH);
		botonOK = new JButton("OK");
		botonCancelar = new JButton("Cancelar");

		panelBotones.setLayout(new FlowLayout(FlowLayout.RIGHT));
		panelBotones.add(botonOK);
		panelBotones.add(botonCancelar);

		//Panel campos
		panelCampos = new JPanel();
		this.getContentPane().add(panelCampos, BorderLayout.CENTER);
		labelNombre = new JLabel("Nombre");
		textoNombre = new JTextField();
		labelDescripcion = new JLabel("Descripción");
		textoDescripcion = new JTextField();
		labelCronologia = new JLabel("Cronologia");
		listaCronologias = new JComboBox<String>();

		listaCronologias.setEditable(false);
		listaCronologias.addItem("Home");		

		panelCampos.setLayout(new GridLayout(6, 1));
		panelCampos.add(labelNombre);
		panelCampos.add(textoNombre);
		panelCampos.add(labelDescripcion);
		panelCampos.add(textoDescripcion);
		panelCampos.add(labelCronologia);
		panelCampos.add(listaCronologias);

		//Eventos botones
		botonOK.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				eventoBotonOK(e);	
			}
		});

		botonCancelar.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				eventoBotonCancelar(e);		
			}
		});	

		//Operaciones sobre la ventana
		this.setTitle("Nuevo filtro");
		this.setSize(400, 200);
		this.setResizable(false);
		this.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE); //Operación al cerrar
		this.setLocationRelativeTo(null); //Centrar
		this.setVisible(true); //Hacer visible
	}

	/*
	 * Evento botón OK
	 */
	private void eventoBotonOK(ActionEvent e) {
		String nombre = textoNombre.getText();
		String descripcion = textoDescripcion.getText();
		String tipoCronologia;

		//Validacion longitud nombre
		if (nombre.length() < 4 || nombre.length() > 12) {
			textoNombre.setText("");
			JOptionPane.showMessageDialog(this, "El nombre debe tener entre 4 y 12 caracteres.", "error", JOptionPane.ERROR_MESSAGE);
		//Validación nombre alfanumérico
		} else if (!nombre.matches("[\\w\\s]*")) {
			textoNombre.setText("");
			JOptionPane.showMessageDialog(this, "El nombre debe contener solo caracteres alfanuméricos.", "error", JOptionPane.ERROR_MESSAGE);
		//Validación descripción
		} else if (descripcion.isEmpty()) {
			JOptionPane.showMessageDialog(this, "La descripción no puede estar vacía.", "error", JOptionPane.ERROR_MESSAGE);
		//Validación item seleccionado en comboBox	
		} else if (listaCronologias.getSelectedIndex() == -1) {
			JOptionPane.showMessageDialog(this, "Debe seleccionar un tipo de cronología.", "error", JOptionPane.ERROR_MESSAGE);
		} else {
			//Creación nuevo filtro
			try {
				tipoCronologia = (String) listaCronologias.getSelectedItem();
				TweetFilter.getInstancia().crearFiltro(nombre, descripcion, tipoCronologia, null);
				this.dispose();
			} catch (ExcepcionTweetFilter e1) {
				JOptionPane.showMessageDialog(this, e1.getMessage(), "error", JOptionPane.ERROR_MESSAGE);
			}	
		}	
	}

	/*
	 * Evento botón Cancelar
	 */
	private void eventoBotonCancelar(ActionEvent e) {
		this.dispose();
	}
}
