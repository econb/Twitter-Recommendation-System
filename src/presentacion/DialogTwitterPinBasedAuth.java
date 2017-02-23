package presentacion;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.border.EmptyBorder;
import javax.swing.border.EtchedBorder;

import logica.TweetFilter;
import logica.excepciones.ExcepcionTweetFilter;
import logica.excepciones.ExcepcionTwitterAPI;

/**
 * Dialogo de la autorización por PIN de Twitter
 * @author Eddie Contreras
 */
public class DialogTwitterPinBasedAuth extends JDialog {
	private JPanel panelBotones;
	private JButton botonOK;
	private JButton botonCancelar;

	private JPanel panelCampos;
	private JLabel labelDescURL;
	private JTextField textURL;
	private JLabel labelDescPIN;
	private JTextField textoPIN;

	/**
	 * Create the dialog.
	 */
	public DialogTwitterPinBasedAuth(String URL) {
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
		labelDescURL = new JLabel("Abra su navegador web e ingrese a la siguiente página:");	
		textURL = new JTextField(URL);
		textURL.setHorizontalAlignment(JTextField.CENTER);
		textURL.setBorder(BorderFactory.createEtchedBorder(EtchedBorder.LOWERED));
		textURL.setEditable(false);
		labelDescPIN = new JLabel("Ingrese el PIN que obtuvo en la página web:");
		textoPIN = new JTextField();

		panelCampos.setLayout(new GridLayout(4, 1));
		panelCampos.add(labelDescURL);
		panelCampos.add(textURL);
		panelCampos.add(labelDescPIN);
		panelCampos.add(textoPIN);

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
		this.setTitle("Autorización");
		this.setSize(700, 200);
		this.setResizable(false);
		this.setDefaultCloseOperation(JDialog.DO_NOTHING_ON_CLOSE); //Operación al cerrar
		this.setLocationRelativeTo(null); //Centrar
		this.setVisible(true); //Hacer visible
	}

	/*
	 * Evento botón OK
	 */
	private void eventoBotonOK(ActionEvent e) {
		String PIN = textoPIN.getText();

		//Validacion PIN vacío
		if (PIN.isEmpty()) {
			JOptionPane.showMessageDialog(this, "Debe ingresar el PIN", "error", JOptionPane.ERROR_MESSAGE);
		} else {
			//Autorización basada en PIN
			try {
				TweetFilter.getInstancia().autorizarAplicacion(PIN);
				JOptionPane.showMessageDialog(this, "Se ha registrado exitosamente, ahora puede iniciar sesión.", "", JOptionPane.INFORMATION_MESSAGE);
				this.dispose();
			} catch (ExcepcionTweetFilter | ExcepcionTwitterAPI e1) {
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
