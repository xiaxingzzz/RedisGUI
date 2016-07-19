package xing.ui;

import java.awt.Component;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JRadioButton;
import javax.swing.JSpinner;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;

public class GridManager {
	static final long serialVersionUID = 5476944632465683614L;
	Map<Component, GridProp> gridProps = new LinkedHashMap<Component, GridProp>();
	List<List<Component>> data = new ArrayList<List<Component>>();
	private GridProp defaultGridProp = new GridProp();
	private JPanel panel;
	private Row row;

	public Row getCurrentRow() {
		return row;
	}

	public void setPanel(JPanel panel) {
		this.panel = panel;
	}

	public GridManager(JPanel panel) {
		this.panel = panel;
	}

	public GridManager() {
		this.panel = new JPanel();
	}

	public static class GridProp {
		public int span = 1;
		public int align = GridBagConstraints.CENTER;
		public Integer fill;
	}

	public class Row {
		List<Component> list;

		protected Row() {
			list = new ArrayList<Component>();
			data.add(list);
		}

		public void add(Component comp, GridProp prop) {
			if (comp == null) {
				comp = new JLabel();
			}

			list.add(comp);
			gridProps.put(comp, prop);
		}

		public void add(Component comp) {
			add(comp, defaultGridProp);
		}
	}

	public Row newRow() {
		row = new Row();
		return row;
	}

	public GridProp getDefaultGridProp() {
		return defaultGridProp;
	}

	public GridBagConstraints getGridBagConstraints(Integer fill, Component comp) {
		GridBagConstraints gbc = new GridBagConstraints();
		if (fill != null && GridBagConstraints.HORIZONTAL == fill) {
			gbc.fill = GridBagConstraints.HORIZONTAL;
			gbc.weightx = 1;
			gbc.weighty = 0;
		} else if (comp instanceof JButton || comp instanceof JCheckBox || comp instanceof JLabel
				|| comp instanceof JSpinner || comp instanceof JRadioButton || comp instanceof JProgressBar) {
			gbc.fill = GridBagConstraints.NONE;
			gbc.weightx = 0;
			gbc.weighty = 0;
		} else if (comp instanceof JTextField || comp instanceof JPanel || comp instanceof JComboBox) {
			gbc.fill = GridBagConstraints.HORIZONTAL;
			gbc.weightx = 1;
			gbc.weighty = 0;
		} else {
			gbc.fill = GridBagConstraints.BOTH;
			gbc.weightx = 1;
			gbc.weighty = 1;
			gbc.gridwidth = 2;
		}
		return gbc;
	}

	void layoutRow(List<Component> row, JPanel panel, GridBagLayout layout, int gridy, int top, int buttom) {
		int gridx = 0;
		int left = 10;
		int right = 3;
		for (int i = 0; i < row.size(); i++) {
			Component comp = row.get(i);

			GridProp gp = gridProps.get(comp);
			GridBagConstraints gbc = getGridBagConstraints(gp.fill, comp);
			right = (i == row.size() - 1) ? 10 : right;
			gbc.insets = new Insets(top, left, buttom, right);
			gbc.gridy = gridy;
			gbc.gridx = gridx;
			gbc.gridwidth = gp.span;
			gbc.anchor = gp.align;
			layout.setConstraints(comp, gbc);
			panel.add(comp);

			gridx = gp.span + gridx;
			left = 3;
		}
	}

	public JPanel layoutPanel() {

		GridBagLayout layout = new GridBagLayout();
		panel.setLayout(layout);
		int gridy = 0;
		int top = 0;
		int buttom = 3;
		for (int i = 0; i < data.size(); i++) {
			List<Component> row = data.get(i);

			if (row != null && row.size() > 0) {

				top = (i == 0) ? 10 : 3;
				buttom = (i == data.size() - 1) ? 10 : 3;
			}

			layoutRow(row, panel, layout, gridy, top, buttom);

			gridy += 1;
		}
		return panel;
	}

	public static void main(String[] args) {
		try {
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
			SwingUtilities.invokeLater(new Runnable() {
				@Override
				public void run() {

					GridManager gm = new GridManager();
					Row r = gm.newRow();
					r.add(new JTextField(""));
					r.add(new JButton("测试"));
					r.add(new JLabel());

					r = gm.newRow();
					r.add(new JTextField(""));
					r.add(new JButton("测试"));
					r.add(new JButton("测试"));

					JFrame frame = new JFrame();
					frame.setTitle("test");
					frame.add(gm.layoutPanel());
					frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

					frame.pack();
					frame.setLocationRelativeTo(null);

					frame.setSize(800, 600);
					frame.setVisible(true);

				}
			});
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
