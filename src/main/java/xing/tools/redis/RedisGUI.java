package xing.tools.redis;

import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.UIManager;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;
import xing.ui.GridManager;
import xing.ui.GridManager.GridProp;
import xing.ui.GridManager.Row;
import xing.ui.SwingUtils;

public class RedisGUI extends JFrame {

	private static final long serialVersionUID = -555354718321270894L;
	private final JDialog dialogPut = new JDialog(this);
	private JButton btnPut = new JButton("Put");
	private JButton btnGet = new JButton("Get");
	private JButton btnDel = new JButton("Del");
	private JButton btnCon = new JButton("Con");
	private JButton btnQuery = new JButton("Query");

	private JTextField textIP = new JTextField();
	private JComboBox<Integer> comboDatabase = new JComboBox<>();
	private JTextField textPassword = new JTextField();
	private JTextField textPort = new JTextField();
	private JTextField textParam = new JTextField();
	private JTextArea console = new JTextArea(6, 10);

	private Jedis jedis;
	private JedisPool pool;

	public static void main(String[] args) {
		try {
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
			final RedisGUI gui = new RedisGUI();
			gui.setSize(1024, 600);
			gui.setLocationRelativeTo(null);

			gui.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

			SwingUtils.startUI(gui);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public RedisGUI() {
		initGUI();
		setTitle("RedisGUI");

		addWindowListener(new WindowAdapter() {

			@Override
			public void windowClosing(WindowEvent e) {
				if (pool != null) {
					pool.close();
				}
			}
		});
	}

	private void initGUI() {
		GridManager gm = new GridManager();
		Row row = gm.newRow();
		row.add(new JLabel("IP"));
		row.add(textIP);
		row.add(new JLabel(""));
		row.add(new JLabel(""));
		row.add(new JLabel(""));

		row = gm.newRow();
		row.add(new JLabel("Port"));
		row.add(textPort);
		row.add(new JLabel(""));
		row.add(new JLabel(""));
		row.add(new JLabel(""));

		row = gm.newRow();
		row.add(new JLabel("Database"));

		for (int i = 0; i < 5; i++) {
			comboDatabase.addItem(i);
		}

		row.add(comboDatabase);
		row.add(new JLabel(""));
		row.add(new JLabel(""));
		row.add(new JLabel(""));

		row = gm.newRow();
		row.add(new JLabel("Password"));
		row.add(textPassword);

		row.add(btnCon);
		row.add(new JLabel(""));
		row.add(new JLabel(""));

		row = gm.newRow();
		row.add(new JLabel("Param"));
		row.add(textParam);
		row.add(btnGet);
		row.add(btnDel);
		row.add(btnPut);

		row = gm.newRow();
		GridProp gp = new GridProp();
		gp.span = 5;
		row.add(btnQuery, gp);

		row = gm.newRow();
		console.setTabSize(2);
		console.setLineWrap(true);
		JScrollPane areaPane = new JScrollPane(console);
		gp = new GridProp();
		gp.span = 5;
		row.add(areaPane, gp);

		btnGet.setEnabled(false);
		btnDel.setEnabled(false);
		btnPut.setEnabled(false);
		btnQuery.setEnabled(false);

		add(gm.layoutPanel());

		buildDialog();

		btnPut.addActionListener((e) -> {
			dialogPut.setVisible(true);
		});

		btnCon.addActionListener((e) -> {
			conn();
		});

		btnQuery.addActionListener((e) -> {
			StringBuilder sb = new StringBuilder();
			jedis.keys(textParam.getText()).forEach((key) -> {
				sb.append(key).append("\n");
			});
			console.setText(sb.toString());
		});

		btnGet.addActionListener((e) -> {
			console.setText(jedis.get(textParam.getText()));
		});

		btnDel.addActionListener((evt) -> {
			jedis.del(textParam.getText());
			console.setText(textParam.getText() + " is deleted");
		});

	}

	private void buildDialog() {
		GridManager gm = new GridManager();
		Row row = gm.newRow();
		row.add(new JLabel("Key"));

		JTextField textKey = new JTextField();
		row.add(textKey);

		row = gm.newRow();
		row.add(new JLabel("Value"));

		JTextArea textareaValue = new JTextArea(6, 10);
		JScrollPane areaPane = new JScrollPane(textareaValue);
		row.add(areaPane);

		row = gm.newRow();
		GridProp gp = new GridProp();
		gp.span = 2;

		JButton btnPut = new JButton("Put");
		row.add(btnPut, gp);

		dialogPut.add(gm.layoutPanel());
		dialogPut.setSize(600, 180);
		dialogPut.setLocationRelativeTo(null);

		btnPut.addActionListener((evt) -> {
			jedis.set(textKey.getText(), textareaValue.getText());
			console.setText("key:" + textKey.getText() + " value:" + textareaValue.getText() + " is set");
		});
	}

	private void conn() {
		JedisPoolConfig config = new JedisPoolConfig();
		pool = new JedisPool(config, textIP.getText(), Integer.parseInt(textPort.getText()), 5000,
				textPassword.getText(), comboDatabase.getSelectedIndex());
		jedis = pool.getResource();

		btnCon.setEnabled(false);
		btnGet.setEnabled(true);
		btnDel.setEnabled(true);
		btnPut.setEnabled(true);
		btnQuery.setEnabled(true);
	}

}
