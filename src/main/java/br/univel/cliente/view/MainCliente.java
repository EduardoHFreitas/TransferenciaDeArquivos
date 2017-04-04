package br.univel.cliente.view;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.rmi.NoSuchObjectException;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;

import br.univel.common.Arquivo;
import br.univel.common.Cliente;
import br.univel.common.IServer;
import br.univel.common.TipoFiltro;
import javax.swing.JScrollPane;
import javax.swing.JTable;

public class MainCliente extends JFrame implements Runnable, IServer {

	private static final long serialVersionUID = 1L;
	private List<Cliente> listaClientes = new ArrayList<Cliente>();
	private List<Arquivo> listaArquivos = new ArrayList<Arquivo>();
	private Map<Cliente, List<Arquivo>> mapaArquivos = new HashMap<Cliente, List<Arquivo>>();
	private JTextField tfIPServidor;
	private IServer servidor;
	private Registry registry;
	private Cliente cliente;
	private JTextField tfBuscar;
	private JTextField tfFiltro;

	private Thread threadMonitor;
	private String diretorio;
	private JNumberField nfPorta;
	private JButton btnDesconectar;
	private JButton btnConectar;
	private JTable tabelaArquivos;

	private Cliente mySelf;
	
	public MainCliente() {

		this.setSize(600, 400);
		this.setDefaultCloseOperation(EXIT_ON_CLOSE);
		this.setResizable(false);
		
		GridBagLayout gridBagLayout = new GridBagLayout();
		gridBagLayout.columnWidths = new int[] { 600, 0 };
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
		gbl_panel.columnWidths = new int[] { 0, 340, 0, 0 };
		gbl_panel.rowHeights = new int[] { 0, 0, 0 };
		gbl_panel.columnWeights = new double[] { 0.0, 1.0, 1.0, Double.MIN_VALUE };
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

		btnConectar = new JButton("Conectar");
		btnConectar.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				if (tfIPServidor.getText().equals("")) {
					if (nfPorta.getText().equals("")) {
						JOptionPane.showMessageDialog(null, "Porta Invalida!");
						return;
					}
					
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

		btnDesconectar = new JButton("Desconectar");
		btnDesconectar.setEnabled(false);
		btnDesconectar.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				pararServidor();
			}
		});

		JLabel lblPorta = new JLabel("Porta:");
		GridBagConstraints gbc_lblPorta = new GridBagConstraints();
		gbc_lblPorta.anchor = GridBagConstraints.EAST;
		gbc_lblPorta.insets = new Insets(0, 0, 0, 5);
		gbc_lblPorta.gridx = 0;
		gbc_lblPorta.gridy = 1;
		panel.add(lblPorta, gbc_lblPorta);

		nfPorta = new JNumberField();
		GridBagConstraints gbc_nfPorta = new GridBagConstraints();
		gbc_nfPorta.anchor = GridBagConstraints.NORTH;
		gbc_nfPorta.insets = new Insets(0, 0, 0, 5);
		gbc_nfPorta.fill = GridBagConstraints.HORIZONTAL;
		gbc_nfPorta.gridx = 1;
		gbc_nfPorta.gridy = 1;
		panel.add(nfPorta, gbc_nfPorta);
		nfPorta.setColumns(10);
		GridBagConstraints gbc_btnDesconectar = new GridBagConstraints();
		gbc_btnDesconectar.anchor = GridBagConstraints.NORTH;
		gbc_btnDesconectar.fill = GridBagConstraints.HORIZONTAL;
		gbc_btnDesconectar.gridx = 2;
		gbc_btnDesconectar.gridy = 1;
		panel.add(btnDesconectar, gbc_btnDesconectar);

		JPanel panel_2 = new JPanel();
		GridBagConstraints gbc_panel_2 = new GridBagConstraints();
		gbc_panel_2.insets = new Insets(10, 10, 10, 10);
		gbc_panel_2.fill = GridBagConstraints.BOTH;
		gbc_panel_2.gridx = 0;
		gbc_panel_2.gridy = 1;
		getContentPane().add(panel_2, gbc_panel_2);
		GridBagLayout gbl_panel_2 = new GridBagLayout();
		gbl_panel_2.columnWidths = new int[] { 600, 0 };
		gbl_panel_2.rowHeights = new int[] { 0, 0 };
		gbl_panel_2.columnWeights = new double[] { 1.0, Double.MIN_VALUE };
		gbl_panel_2.rowWeights = new double[] { 1.0, Double.MIN_VALUE };
		panel_2.setLayout(gbl_panel_2);

		JPanel panel_1 = new JPanel();
		GridBagConstraints gbc_panel_1 = new GridBagConstraints();
		gbc_panel_1.fill = GridBagConstraints.BOTH;
		gbc_panel_1.gridx = 0;
		gbc_panel_1.gridy = 0;
		panel_2.add(panel_1, gbc_panel_1);
		GridBagLayout gbl_panel_1 = new GridBagLayout();
		gbl_panel_1.columnWidths = new int[] { 57, 210, 0, 87, 215, 0 };
		gbl_panel_1.rowHeights = new int[] { 0, 0, 0, 0 };
		gbl_panel_1.columnWeights = new double[] { 1.0, 0.0, 0.0, 1.0, 0.0, Double.MIN_VALUE };
		gbl_panel_1.rowWeights = new double[] { 0.0, 0.0, 1.0, Double.MIN_VALUE };
		panel_1.setLayout(gbl_panel_1);

		JLabel lblNewLabel = new JLabel("Buscar");
		GridBagConstraints gbc_lblNewLabel = new GridBagConstraints();
		gbc_lblNewLabel.insets = new Insets(0, 0, 5, 5);
		gbc_lblNewLabel.anchor = GridBagConstraints.EAST;
		gbc_lblNewLabel.gridx = 0;
		gbc_lblNewLabel.gridy = 0;
		panel_1.add(lblNewLabel, gbc_lblNewLabel);

		tfBuscar = new JTextField();
		GridBagConstraints gbc_tfBuscar = new GridBagConstraints();
		gbc_tfBuscar.gridwidth = 3;
		gbc_tfBuscar.fill = GridBagConstraints.HORIZONTAL;
		gbc_tfBuscar.anchor = GridBagConstraints.NORTH;
		gbc_tfBuscar.insets = new Insets(0, 0, 5, 5);
		gbc_tfBuscar.gridx = 1;
		gbc_tfBuscar.gridy = 0;
		panel_1.add(tfBuscar, gbc_tfBuscar);
		tfBuscar.setColumns(10);

		JButton btnBuscar = new JButton("Buscar");
		GridBagConstraints gbc_btnBuscar = new GridBagConstraints();
		gbc_btnBuscar.anchor = GridBagConstraints.NORTH;
		gbc_btnBuscar.fill = GridBagConstraints.HORIZONTAL;
		gbc_btnBuscar.insets = new Insets(0, 0, 5, 0);
		gbc_btnBuscar.gridx = 4;
		gbc_btnBuscar.gridy = 0;
		panel_1.add(btnBuscar, gbc_btnBuscar);

		JLabel lblFiltro = new JLabel("Filtro");
		GridBagConstraints gbc_lblFiltro = new GridBagConstraints();
		gbc_lblFiltro.insets = new Insets(0, 0, 5, 5);
		gbc_lblFiltro.anchor = GridBagConstraints.NORTHEAST;
		gbc_lblFiltro.gridx = 0;
		gbc_lblFiltro.gridy = 1;
		panel_1.add(lblFiltro, gbc_lblFiltro);

		JComboBox<TipoFiltro> comboBox = new JComboBox(TipoFiltro.values());
		GridBagConstraints gbc_comboBox = new GridBagConstraints();
		gbc_comboBox.fill = GridBagConstraints.HORIZONTAL;
		gbc_comboBox.anchor = GridBagConstraints.NORTH;
		gbc_comboBox.insets = new Insets(0, 0, 5, 5);
		gbc_comboBox.gridx = 1;
		gbc_comboBox.gridy = 1;
		panel_1.add(comboBox, gbc_comboBox);

		JLabel lblValor = new JLabel("Valor");
		GridBagConstraints gbc_lblValor = new GridBagConstraints();
		gbc_lblValor.anchor = GridBagConstraints.EAST;
		gbc_lblValor.insets = new Insets(0, 0, 5, 5);
		gbc_lblValor.gridx = 2;
		gbc_lblValor.gridy = 1;
		panel_1.add(lblValor, gbc_lblValor);

		tfFiltro = new JTextField();
		GridBagConstraints gbc_tfFiltro = new GridBagConstraints();
		gbc_tfFiltro.insets = new Insets(0, 0, 5, 5);
		gbc_tfFiltro.fill = GridBagConstraints.HORIZONTAL;
		gbc_tfFiltro.gridx = 3;
		gbc_tfFiltro.gridy = 1;
		panel_1.add(tfFiltro, gbc_tfFiltro);
		tfFiltro.setColumns(10);

		JButton btnPublicarLista = new JButton("Publicar Lista");
		btnPublicarLista.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				publicarMinhaLista("C:/Shared/");
			}
		});
		GridBagConstraints gbc_btnPublicarLista = new GridBagConstraints();
		gbc_btnPublicarLista.anchor = GridBagConstraints.NORTH;
		gbc_btnPublicarLista.fill = GridBagConstraints.HORIZONTAL;
		gbc_btnPublicarLista.insets = new Insets(0, 0, 5, 0);
		gbc_btnPublicarLista.gridx = 4;
		gbc_btnPublicarLista.gridy = 1;
		panel_1.add(btnPublicarLista, gbc_btnPublicarLista);
		
		JScrollPane scrollPane = new JScrollPane();
		GridBagConstraints gbc_scrollPane = new GridBagConstraints();
		gbc_scrollPane.gridwidth = 5;
		gbc_scrollPane.insets = new Insets(0, 0, 0, 5);
		gbc_scrollPane.fill = GridBagConstraints.BOTH;
		gbc_scrollPane.gridx = 0;
		gbc_scrollPane.gridy = 2;
		panel_1.add(scrollPane, gbc_scrollPane);
		
		tabelaArquivos = new JTable();
		scrollPane.setViewportView(tabelaArquivos);

		this.setVisible(true);
	}

	protected void pararServidor() {
		try {
			UnicastRemoteObject.unexportObject(this, true);
			UnicastRemoteObject.unexportObject(registry, true);
			
			tfIPServidor.setEnabled(true);
			nfPorta.setEnabled(true);
			btnDesconectar.setEnabled(false);
			btnConectar.setEnabled(true);
		} catch (NoSuchObjectException e) {
			e.printStackTrace();
		}
	}

	protected void conectarServidor() throws RemoteException, NotBoundException {
		if (nfPorta.getText().equals("")){
			JOptionPane.showMessageDialog(null, "Porta Invalida!");
			return;
		}
		
		registry = LocateRegistry.getRegistry(tfIPServidor.getText(), nfPorta.getNumber());

		JOptionPane.showMessageDialog(null, registry);
		servidor = (IServer) registry.lookup(IServer.NOME_SERVICO);

		mySelf = new Cliente();
		mySelf.setNome("Dread");
		mySelf.setIp(tfIPServidor.getText());
		mySelf.setPorta(nfPorta.getNumber());

		servidor.registrarCliente(mySelf);

	}

	protected void startarServidor() throws RemoteException {
		this.run();
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

	private void publicarMinhaLista(final String dir) {
		File diretorio = new File(dir);
		File arquivos[] = diretorio.listFiles();

		for (int i = 0; i < arquivos.length; i++) {
			File file = arquivos[i];
			Arquivo arquivo = new Arquivo();
			arquivo.setNome(file.getName());
			arquivo.setPath(file.getPath());
			arquivo.setDataHoraModificacao(new Date());
			arquivo.setTamanho(file.length());
			arquivo.setMd5(Md5Util.getMD5Checksum(file.getAbsolutePath()));
			arquivo.setId(i);
			listaArquivos.add(arquivo);
		}
	}

	@Override
	public void run() {
		registry = null;
		try {
			servidor = (IServer) UnicastRemoteObject.exportObject(this, 0);
			registry = LocateRegistry.createRegistry(nfPorta.getNumber());
			registry.rebind(IServer.NOME_SERVICO, servidor);

			tfIPServidor.setEnabled(false);
			nfPorta.setEnabled(false);
			btnDesconectar.setEnabled(true);
			
			Thread currentThread = Thread.currentThread();

			// thread que fica verificando a pasta
			if (this.threadMonitor == currentThread) {

				try {
					Thread.sleep(1000);
				} catch (InterruptedException e) {
					e.printStackTrace();
				} catch (NumberFormatException ex) {
					ex.printStackTrace();
				}

			}
		} catch (RemoteException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
