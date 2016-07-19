package xing.ui;

import java.awt.Component;
import java.awt.GridBagConstraints;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.swing.AbstractButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import javax.swing.text.JTextComponent;

import xing.ui.GridManager.GridProp;
import xing.ui.GridManager.Row;

public class SwingUtils {

	public static void startUI(final JFrame frame) {
		SwingUtilities.invokeLater(new Runnable() {
			@Override
			public void run() {
				frame.setVisible(true);
			}
		});
	}

	@SuppressWarnings("rawtypes")
	public static Object getComponentValue(Component c) {
		if (c == null) {
			return null;
		} else if (c instanceof JComboBox) {
			return ((JComboBox) c).getSelectedItem();
		} else if (c instanceof JTextComponent) {
			String text = ((JTextComponent) c).getText();
			if (text == null || text.trim().length() == 0) {
				return null;
			} else {
				return text;
			}
		} else if (c instanceof AbstractButton) {
			return Boolean.valueOf(((AbstractButton) c).isSelected());
		} else {
			return null;
		}
	}

	@SuppressWarnings({ "rawtypes" })
	public static List<JComponent> createComponent(Field field, String title, String tip, Object obj) {
		Object value = null;
		try {
			value = field.get(obj);
		} catch (Exception e) {
			e.printStackTrace();
		}

		ArrayList<JComponent> list = new ArrayList<JComponent>();

		JLabel label = new JLabel(title);
		if (notEmpty(tip)) {
			label.setToolTipText(tip);
		}

		list.add(label);

		Class<?> type = field.getType();

		if (String.class.equals(type)) {
			JTextField c = new JTextField();
			c.setColumns(16);
			if (value != null) {
				c.setText(value.toString());
			}
			c.setName(field.getName());
			list.add(c);
		} else if (int.class.equals(type)) {
			JTextField c = new JTextField();
			c.setColumns(16);
			if (value != null) {
				c.setText(value.toString());
			}
			c.setName(field.getName());
			list.add(c);
		} else if (type.isEnum()) {
			JComboBox c = enumComboBox(type, value);
			c.setName(field.getName());
			list.add(c);
		} else if (type.equals(boolean.class)) {
			JCheckBox cb = new JCheckBox();
			cb.setName(field.getName());

			try {
				cb.setSelected(field.getBoolean(obj));
			} catch (Exception e) {
				e.printStackTrace();
			}

			list.add(cb);
		}

		return list;
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	public static JComboBox enumComboBox(Class enumClass, Object value) {
		JComboBox c = new JComboBox();
		for (Object e : enumClass.getEnumConstants()) {
			c.addItem(e);
		}
		if (value != null) {
			c.setSelectedItem(value);
		}
		return c;
	}

	public static JComboBox<Boolean> booleanComboBox(Object value) {
		JComboBox<Boolean> c = new JComboBox<>();
		c.addItem(Boolean.TRUE);
		c.addItem(Boolean.FALSE);
		if (value != null) {
			c.setSelectedItem(value);
		}
		return c;
	}

	public static void createObjectPanel(GridManager gm, Field[] fields, Object obj, int columnSize) {
		GridProp gpLeft = new GridProp();
		gpLeft.align = GridBagConstraints.EAST;
		try {
			Row r = gm.newRow();
			int column = 0;
			for (Field field : fields) {
				Title aTitle = field.getAnnotation(Title.class);

				if (field.getAnnotation(Title.class) != null) {
					ComponentLayout layout = field.getAnnotation(ComponentLayout.class);
					List<JComponent> components = createComponent(field, aTitle.value(), aTitle.tip(), obj);
					JComponent title = components.get(0);
					r.add(title, gpLeft);

					JComponent comp = components.get(1);
					if (layout == null) {
						r.add(comp);
						column += 2;
					} else {
						GridProp gp = new GridProp();
						gp.span = layout.span();

						switch (layout.expand()) {
						case HORIZONTAL:
							gp.fill = GridBagConstraints.HORIZONTAL;
							break;
						case NONE:
							gp.fill = GridBagConstraints.NONE;
							break;
						case BOTH:
							gp.fill = GridBagConstraints.BOTH;
							break;
						default:
							gp.fill = null;
							break;
						}

						r.add(comp, gp);
						column += (1 + layout.span());
					}

					if (column > (columnSize - 2)) {
						r = gm.newRow();
						column = 0;
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static void savePanelToModel(JPanel panel, Object model) {
		Map<String, Component> map = getPanelNameMap(panel);
		saveModelFromMap(model, map);
	}

	public static void saveModelFromMap(Object model, Map<String, Component> map) {
		try {
			Field[] fields = model.getClass().getFields();
			for (Field field : fields) {
				Component component = map.get(field.getName());
				if (component != null) {
					Object value = getComponentValue(component);
					if (field.getType().equals(String.class)) {
						field.set(model, value != null ? value.toString() : null);
					} else if (field.getType().equals(int.class)) {
						try {
							if (value == null) {
								field.setInt(model, 0);
							} else {
								field.setInt(model, Integer.parseInt(value.toString()));
							}
						} catch (Exception e) {
							e.printStackTrace();
						}
					} else if (field.getType().isEnum()) {
						field.set(model, value);
					} else if (field.getType().equals(boolean.class)) {
						field.setBoolean(model, ((Boolean) value).booleanValue());
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@SuppressWarnings("rawtypes")
	public static void readModelToPanel(Object model, JPanel panel) {
		Map<String, Component> map = getPanelNameMap(panel);

		Field[] fields = model.getClass().getFields();
		for (Field field : fields) {

			try {
				Component component = map.get(field.getName());
				if (component != null) {
					Object value = field.get(model);
					if (field.getType().equals(String.class)) {
						((JTextComponent) component).setText(value != null ? value.toString() : null);
					} else if (field.getType().isEnum()) {
						try {
							if (value != null) {
								((JComboBox) component).setSelectedItem(value);
							}
						} catch (Exception e) {
							e.printStackTrace();
						}
					} else if (field.getType().equals(boolean.class)) {
						try {
							if (value != null) {
								((JCheckBox) component).setSelected((boolean) value);
							}
						} catch (Exception e) {
							e.printStackTrace();
						}
					}
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

	}

	public static Map<String, Component> getPanelNameMap(JPanel... panels) {
		Map<String, Component> map = new HashMap<>();
		for (JPanel panel : panels) {
			Component[] components = panel.getComponents();
			for (Component component : components) {
				String name = component.getName();
				if (name != null) {
					map.put(component.getName(), component);
				}
			}
		}
		return map;
	}
	
	private static boolean notEmpty(String str) {
		return str != null && str.trim().length() > 0;
	}

}
