package br.univel.cliente.view;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;

import br.univel.common.Arquivo;
import br.univel.common.Cliente;
import br.univel.common.IServer;
import br.univel.common.TipoFiltro;

public class MainCliente extends JFrame implements IServer {

	private static final long serialVersionUID = 1L;
	private List<Cliente> listaClientes = new ArrayList<Cliente>();
	private Map<Cliente, List<Arquivo>> mapaArquivos = new HashMap<Cliente, List<Arquivo>>();
	private JTextField tfIPServidor;
	private JTable table;
	private IServer servidor;
	private Registry registry;
	private Cliente cliente;

	public MainCliente() {

		this.setSize(600, 400);

		setResizable(false);
		GridBagLayout gridBagLayout = new GridBagLayout();
		gridBagLayout.columnWidths = new int[] { 0, 0 };
		gridBagLayout.rowHeights = new int[] { 70, 0, 0 };
		gridBagLayout.columnWeights = new double[] { 1.0, Double.MIN_VALUE };
		gridBagLayout.rowWeights = new double[] { 0.0, 1.0, Double.MIN_VALUE };
		getContentPane().setLayout(gridBagLayout);

		JPanel panel = new JPanel();
		GridBagConstraints gbc_panel = new GridBagConstraints();
		gbc_panel.insets = new Insets(10, 10, 10, 10);
		gbc_panel.fill = GridBagConstraints.BOTH;
		gbc_panel.gridx = 0;
		gbc_panel.gridy = 0;
		getContentPane().add(panel, gbc_panel);
		GridBagLayout gbl_panel = new GridBagLayout();
		gbl_panel.columnWidths = new int[] { 0, 330, 0, 0 };
		gbl_panel.rowHeights = new int[] { 0, 0, 0 };
		gbl_panel.columnWeights = new double[] { 0.0, 0.0, 1.0, Double.MIN_VALUE };
		gbl_panel.rowWeights = new double[] { 0.0, 0.0, Double.MIN_VALUE };
		panel.setLayout(gbl_panel);

		JLabel lblIp = new JLabel("IP:");
		GridBagConstraints gbc_lblIp = new GridBagConstraints();
		gbc_lblIp.insets = new Insets(0, 0, 5, 5);
		gbc_lblIp.anchor = GridBagConstraints.EAST;
		gbc_lblIp.gridx = 0;
		gbc_lblIp.gridy = 0;
		panel.add(lblIp, gbc_lblIp);

		tfIPServidor = new JTextField();
		GridBagConstraints gbc_tfIPServidor = new GridBagConstraints();
		gbc_tfIPServidor.insets = new Insets(0, 0, 5, 5);
		gbc_tfIPServidor.anchor = GridBagConstraints.NORTH;
		gbc_tfIPServidor.fill = GridBagConstraints.HORIZONTAL;
		gbc_tfIPServidor.gridx = 1;
		gbc_tfIPServidor.gridy = 0;
		panel.add(tfIPServidor, gbc_tfIPServidor);
		tfIPServidor.setColumns(10);

		JButton btnConectar = new JButton("Conectar");
		btnConectar.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				if (tfIPServidor.getText().equals("")) {
					InetAddress IP;
					String IPString = null;
					try {
						IP = InetAddress.getLocalHost();
						IPString = IP.getHostAddress();
					} catch (UnknownHostException ex) {
						ex.printStackTrace();
					}

					if (IPString != null) {
						tfIPServidor.setText(IPString);
					} else {
						JOptionPane.showMessageDialog(null, "Erro ao inicializar Servidor!");
					}

					btnConectar.setEnabled(false);
					try {
						startarServidor();
					} catch (RemoteException e1) {
						e1.printStackTrace();
					}

				} else {
					try {
						conectarServidor();
					} catch (RemoteException | NotBoundException e1) {
						e1.printStackTrace();
					}
				}
			}
		});
		GridBagConstraints gbc_btnConectar = new GridBagConstraints();
		gbc_btnConectar.anchor = GridBagConstraints.NORTH;
		gbc_btnConectar.fill = GridBagConstraints.HORIZONTAL;
		gbc_btnConectar.insets = new Insets(0, 0, 5, 0);
		gbc_btnConectar.gridx = 2;
		gbc_btnConectar.gridy = 0;
		panel.add(btnConectar, gbc_btnConectar);

		JButton btnDesconectar = new JButton("Desconectar");
		btnDesconectar.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				
			}
		});
		GridBagConstraints gbc_btnDesconectar = new GridBagConstraints();
		gbc_btnDesconectar.anchor = GridBagConstraints.NORTH;
		gbc_btnDesconectar.fill = GridBagConstraints.HORIZONTAL;
		gbc_btnDesconectar.gridx = 2;
		gbc_btnDesconectar.gridy = 1;
		panel.add(btnDesconectar, gbc_btnDesconectar);

		JPanel panel_1 = new JPanel();
		GridBagConstraints gbc_panel_1 = new GridBagConstraints();
		gbc_panel_1.fill = GridBagConstraints.BOTH;
		gbc_panel_1.gridx = 0;
		gbc_panel_1.gridy = 1;
		getContentPane().add(panel_1, gbc_panel_1);
		GridBagLayout gbl_panel_1 = new GridBagLayout();
		gbl_panel_1.columnWidths = new int[] { 0, 0 };
		gbl_panel_1.rowHeights = new int[] { 0, 0 };
		gbl_panel_1.columnWeights = new double[] { 1.0, Double.MIN_VALUE };
		gbl_panel_1.rowWeights = new double[] { 1.0, Double.MIN_VALUE };
		panel_1.setLayout(gbl_panel_1);

		JScrollPane scrollPane = new JScrollPane();
		GridBagConstraints gbc_scrollPane = new GridBagConstraints();
		gbc_scrollPane.fill = GridBagConstraints.BOTH;
		gbc_scrollPane.gridx = 0;
		gbc_scrollPane.gridy = 0;
		panel_1.add(scrollPane, gbc_scrollPane);

		table = new JTable();
		scrollPane.setViewportView(table);

		this.setVisible(true);
	}

	protected void conectarServidor() throws RemoteException, NotBoundException {
		registry = null;
		registry = LocateRegistry.getRegistry(tfIPServidor.getText(), 1818);

		servidor = (IServer) registry.lookup(IServer.NOME_SERVICO);
		cliente = (Cliente) UnicastRemoteObject.exportObject(this, 0);

	}

	protected void startarServidor() throws RemoteException {
		registry = null;
		servidor = (IServer) UnicastRemoteObject.exportObject(this, 0);
		registry = LocateRegistry.createRegistry(1818);
		registry.rebind(IServer.NOME_SERVICO, servidor);
	}

	public void registrarCliente(Cliente c) throws RemoteException {
		if (!listaClientes.contains(c)) {
			listaClientes.add(c);
		}
	}

	public void publicarListaArquivos(Cliente c, List<Arquivo> lista) throws RemoteException {
		mapaArquivos.put(c, lista);
	}

	public Map<Cliente, List<Arquivo>> procurarArquivo(String query, TipoFiltro tipoFiltro, String filtro)
			throws RemoteException {
		Map<Cliente, List<Arquivo>> mapaEncontrados = new HashMap<Cliente, List<Arquivo>>();

		for (Map.Entry<Cliente, List<Arquivo>> mapa : mapaArquivos.entrySet()) {

			ArrayList<Arquivo> arquivosEncotrados = new ArrayList<>();

			mapa.getValue().forEach(arquivo -> {
				if (arquivo.getNome().endsWith(filtro) || arquivo.getNome().contains(query)) {
					arquivosEncotrados.add(arquivo);
				}
			});

			if (arquivosEncotrados != null) {
				mapaEncontrados.put(mapa.getKey(), arquivosEncotrados);
			}
		}

		return null;
	}

	public byte[] baixarArquivo(Cliente cli, Arquivo arq) throws RemoteException {
		return null;
	}

	public void desconectar(Cliente c) throws RemoteException {
		if (listaClientes.contains(c)) {
			listaClientes.remove(c);
		}
	}

	public static void main(String[] args) {
		new MainCliente();
	}
}
