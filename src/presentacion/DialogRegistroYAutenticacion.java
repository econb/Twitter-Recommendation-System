package presentacion;

import java.awt.EventQueue;
import java.awt.FlowLayout;
import java.awt.GridLayout;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JEditorPane;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPasswordField;
import javax.swing.JScrollPane;
import javax.swing.JTextField;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;

import javax.swing.JPanel;
import javax.swing.border.EtchedBorder;

import logica.TweetFilter;
import logica.excepciones.ExcepcionTweetFilter;
import logica.excepciones.ExcepcionTwitterAPI;

/**
 * Dialogo para registrar y autenticar usuarios
 * @author Eddie Contreras
 */
public class DialogRegistroYAutenticacion extends JDialog {
	private JEditorPane descripcionApp;
	private JScrollPane scrollDescripcionApp;
	private JPanel panelRegYAut;

	private JPanel panelAutenticacion;
	private JLabel labelAut;
	private JPanel panelCamposAut;
	private JLabel labelUsuarioAut;
	private JTextField usuarioAut;
	private JLabel labelPasswordAut;
	private JPasswordField passwordAut;
	private JPanel panelBotonAut;
	private JButton botonAut;

	private JPanel panelRegistro;
	private JLabel labelReg;
	private JPanel panelCamposReg;
	private JLabel labelUsuarioReg;
	private JTextField usuarioReg;
	private JLabel labelPasswordReg;
	private JPasswordField passwordReg;
	private JLabel labelPassword2Reg;
	private JPasswordField password2Reg;
	private JPanel panelBotonReg;
	private JButton botonReg;

	/**
	 * Crea el dialogo
	 */
	public DialogRegistroYAutenticacion() {	
		this.getContentPane().setLayout(new BorderLayout(0, 0));

		//Descripción de la aplicación
		descripcionApp = new JEditorPane();
		descripcionApp.setEditable(false);
		descripcionApp.setContentType("text/html");

		try {
			descripcionApp.setPage(getClass().getResource("/presentacion/tweetFilter.html"));
		} catch (IOException e1) {
			e1.printStackTrace();
		}

		scrollDescripcionApp = new JScrollPane(descripcionApp);
		scrollDescripcionApp.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
		scrollDescripcionApp.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
		this.getContentPane().add(scrollDescripcionApp, BorderLayout.CENTER);

		//Panel doble Registro y Autenticación
		panelRegYAut = new JPanel();
		this.getContentPane().add(panelRegYAut, BorderLayout.SOUTH);
		panelRegistro = new JPanel();
		panelRegistro.setBorder(BorderFactory.createEtchedBorder(EtchedBorder.LOWERED));
		panelAutenticacion = new JPanel();
		panelAutenticacion.setBorder(BorderFactory.createEtchedBorder(EtchedBorder.LOWERED));

		panelRegYAut.setLayout(new GridLayout(1, 2, 5, 0));
		panelRegYAut.add(panelAutenticacion);
		panelRegYAut.add(panelRegistro);

		//Panel Autenticación
		labelAut = new JLabel("Iniciar sesión", JLabel.CENTER);

		panelCamposAut = new JPanel();
		labelUsuarioAut = new JLabel("Nombre de usuario");
		usuarioAut = new JTextField();
		labelPasswordAut = new JLabel("Contraseña");
		passwordAut = new JPasswordField();
		panelCamposAut.setLayout(new GridLayout(4, 1));
		panelCamposAut.add(labelUsuarioAut);
		panelCamposAut.add(usuarioAut);
		panelCamposAut.add(labelPasswordAut);
		panelCamposAut.add(passwordAut);	

		panelBotonAut = new JPanel();
		botonAut = new JButton("OK");
		panelBotonAut.setLayout(new FlowLayout(FlowLayout.RIGHT));
		panelBotonAut.add(botonAut);

		panelAutenticacion.setLayout(new BorderLayout());
		panelAutenticacion.add(labelAut, BorderLayout.NORTH);
		panelAutenticacion.add(panelCamposAut, BorderLayout.CENTER);
		panelAutenticacion.add(panelBotonAut, BorderLayout.SOUTH);

		//Panel Registro
		labelReg = new JLabel("¿Nuevo usuario?", JLabel.CENTER);

		panelCamposReg = new JPanel();
		labelUsuarioReg = new JLabel("Nombre de usuario");
		usuarioReg = new JTextField();
		labelPasswordReg = new JLabel("Contraseña");
		passwordReg = new JPasswordField();
		labelPassword2Reg = new JLabel("Digite de nuevo la contraseña");
		password2Reg = new JPasswordField();
		panelCamposReg.setLayout(new GridLayout(6, 1));
		panelCamposReg.add(labelUsuarioReg);
		panelCamposReg.add(usuarioReg);
		panelCamposReg.add(labelPasswordReg);
		panelCamposReg.add(passwordReg);
		panelCamposReg.add(labelPassword2Reg);
		panelCamposReg.add(password2Reg);

		panelBotonReg = new JPanel();
		botonReg = new JButton("Registrarse");
		panelBotonReg.setLayout(new FlowLayout(FlowLayout.RIGHT));
		panelBotonReg.add(botonReg);

		panelRegistro.setLayout(new BorderLayout());
		panelRegistro.add(labelReg, BorderLayout.NORTH);
		panelRegistro.add(panelCamposReg, BorderLayout.CENTER);
		panelRegistro.add(panelBotonReg, BorderLayout.SOUTH);	

		//Eventos botones
		botonAut.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				eventoBotonAut(e);		
			}
		});

		botonReg.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				eventoBotonReg(e);		
			}
		});

		//Operaciones sobre la ventana
		this.setTitle("TweetFilter");
		this.setSize(450, 350);
		this.setResizable(false);
		this.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE); //Operación al cerrar
		this.setLocationRelativeTo(null); //Centrar
		this.setVisible(true); //Hacer visible
	}

	/*
	 * Evento botón para autenticar
	 */
	private void eventoBotonAut(ActionEvent e) {
		String nombre = usuarioAut.getText();
		String password = String.valueOf(passwordAut.getPassword());
		FramePrincipal framePrincipal;

		//Validación nombre
		if (nombre.isEmpty()) {
			JOptionPane.showMessageDialog(this, "Debe ingresar el nombre", "error", JOptionPane.ERROR_MESSAGE);
		//Validación contraseña
		} else if (password.isEmpty()) {
			JOptionPane.showMessageDialog(this, "Debe ingresar la contraseña", "error", JOptionPane.ERROR_MESSAGE);
		} else {
			//Autenticación usuario
			try {
				nombre = nombre.toLowerCase();
				TweetFilter.getInstancia().autenticarUsuario(nombre, password);
				framePrincipal = new FramePrincipal();
				this.dispose();
			} catch (ExcepcionTweetFilter e1) {
				JOptionPane.showMessageDialog(this, e1.getMessage(), "error", JOptionPane.ERROR_MESSAGE);
			} 
		}	
	}

	/*
	 * Evento botón para registrar
	 */
	private void eventoBotonReg(ActionEvent e) {
		String nombre = usuarioReg.getText();
		String password = String.valueOf(passwordReg.getPassword());
		String password2 = String.valueOf(password2Reg.getPassword());
		String URL;
		DialogTwitterPinBasedAuth dialogAuth;

		//Validacion longitud nombre
		if (nombre.length() < 4 || nombre.length() > 8) {
			usuarioReg.setText("");
			JOptionPane.showMessageDialog(this, "El nombre debe tener entre 4 y 8 caracteres.", "error", JOptionPane.ERROR_MESSAGE);
		//Validación nombre alfanumérico
		} else if (!nombre.matches("[\\w]*")) {
			usuarioReg.setText("");
			JOptionPane.showMessageDialog(this, "El nombre debe contener solo caracteres alfanuméricos.", "error", JOptionPane.ERROR_MESSAGE);
		//Validacion longitud contraseña
		} else if (password.length() < 4 || password.length() > 8) {
			passwordReg.setText("");
			password2Reg.setText("");
			JOptionPane.showMessageDialog(this, "La contraseña debe tener entre 4 y 8 caracteres.", "error", JOptionPane.ERROR_MESSAGE);
		//Validación contraseña alfanumérica
		} else if (!password.matches("[\\w]*")) {
			passwordReg.setText("");
			password2Reg.setText("");
			JOptionPane.showMessageDialog(this, "La contraseña debe contener solo caracteres alfanuméricos.", "error", JOptionPane.ERROR_MESSAGE);
		//Validación contraseña no digitada por segunda vez
		} else if (password2.isEmpty()) {		
			JOptionPane.showMessageDialog(this, "Digite la contraseña por segunda vez.", "error", JOptionPane.ERROR_MESSAGE);
		//Validación contraseña correctamente escrita la segunda vez
		} else if (!password.equals(password2)) {
			password2Reg.setText("");
			JOptionPane.showMessageDialog(this, "La contraseña no fue digitada correctamente la segunda vez.", "error", JOptionPane.ERROR_MESSAGE);
		} else {
			//Registro usuario
			try {
				nombre = nombre.toLowerCase();
				URL = TweetFilter.getInstancia().registrarUsuario(nombre, password);
				dialogAuth = new DialogTwitterPinBasedAuth(URL);

				//Limpieza campos
				usuarioReg.setText("");
				passwordReg.setText("");
				password2Reg.setText("");
			} catch (ExcepcionTweetFilter | ExcepcionTwitterAPI e1) {
				JOptionPane.showMessageDialog(this, e1.getMessage(), "error", JOptionPane.ERROR_MESSAGE);
			} 
		}
	}
}
