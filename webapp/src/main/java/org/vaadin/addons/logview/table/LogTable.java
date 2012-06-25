package org.vaadin.addons.logview.table;

import com.google.common.collect.ImmutableMap;
import com.jensjansson.pagedtable.PagedTable;
import com.vaadin.data.Container;
import com.vaadin.ui.CustomComponent;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Root;
import com.vaadin.ui.Root.BrowserWindowResizeEvent;
import com.vaadin.ui.Root.BrowserWindowResizeListener;
import com.vaadin.ui.Table;
import com.vaadin.ui.Table.CellStyleGenerator;
import com.vaadin.ui.Table.ColumnReorderEvent;
import com.vaadin.ui.VerticalLayout;

public class LogTable extends CustomComponent {
	private transient BrowserWindowResizeListener resize;
	private transient VerticalLayout mainLayout;
	private transient PagedTable table;

	private final transient static ImmutableMap<String, String> levels = new ImmutableMap.Builder<String, String>()
			.put("TRACE", "b666 ffff") //
			.put("DEBUG", "b036 ffff") //
			.put("INFO", "b360 ffff") //
			.put("WARN", "bc60 ffff") //
			.put("ERROR", "b900 ffff") //
			.put("FATAL", "bf00 ffff") //
			.build();

	public LogTable() {
		// common part: create layout
		mainLayout = new VerticalLayout();
		mainLayout.setSizeFull();

		// top-level component properties
		setSizeFull();

		// table
		table = new PagedTable();
		table.setImmediate(true);
		table.setSizeFull();
		table.setSelectable(true);
		table.setSortDisabled(true);
		table.setFooterVisible(true);
		table.setColumnReorderingAllowed(true);
		table.setColumnCollapsingAllowed(true);
		table.addStyleName("no_y_scroll");

		HorizontalLayout c1 = table.createControls();
		mainLayout.addComponent(c1);

		mainLayout.addComponent(table);
		mainLayout.setExpandRatio(table, 1.0f);

		HorizontalLayout c2 = table.createControls();
		mainLayout.addComponent(c2);

		setCompositionRoot(mainLayout);
		// final BeanItemContainer<DetailLogEntry> container2 = new
// BeanItemContainer<DetailLogEntry>(DetailLogEntry.class);
		final Container container2 = new LogContainer();
		final Container container = new DebugContainer(container2).getProxy();
		/*
		int i = 0;
		for(LogEntry entry : Loader.entrys) {
			if(i > 1000) {
				break;
			}
			i++;
			container2.addItem(new DetailLogEntry(entry));
		}
		*/

		// NDC Classname date id level message
		// table.setColumnCollapsed(ExampleUtil.iso3166_PROPERTY_FLAG, true);

		table.setCellStyleGenerator(new CellStyleGenerator() {
			@Override
			public String getStyle(Object itemId, Object propertyId) {
				if(propertyId == null) {
					if(itemId instanceof DetailLogEntry) {
						DetailLogEntry entry = (DetailLogEntry)itemId;
						String ret = levels.get(entry.getLevel());
						if(ret == null) {
							return ret;
						}
						return "cell " + ret;
					}
				}
				return null;
			}
		});
		table.addListener(new Table.ColumnReorderListener() {
			@Override
			public void columnReorder(ColumnReorderEvent event) {
				System.err.println("test: " + event);
			}
		});
		table.setContainerDataSource(container);

		/*
		TextField messageFilterField = new TextField();
		messageFilterField.addListener(new Property.ValueChangeListener() {
			@Override
			public void valueChange(ValueChangeEvent event) {
				String text = (String)event.getProperty().getValue();
				container2.removeContainerFilters(DetailLogEntry.MESSAGE);
				if(text != null) {
					container2.addContainerFilter(DetailLogEntry.MESSAGE, text, true, true);
				}
			}
		});
		*/
		table.setColumnHeader(DetailLogEntry.ID, "ID");
		table.setColumnHeader(DetailLogEntry.CLASS, "Class");
		table.setColumnHeader(DetailLogEntry.MESSAGE, "Message");
		table.setColumnHeader(DetailLogEntry.DATE, "Date");
		table.setColumnHeader(DetailLogEntry.LEVEL, "Level");
		table.setColumnCollapsed(DetailLogEntry.ID, true);
		// table.setColumnCollapsed(DetailLogEntry.LEVEL, true);
		table.setVisibleColumns(new Object[] {
			DetailLogEntry.ID,
			DetailLogEntry.DATE,
			DetailLogEntry.LEVEL,
			DetailLogEntry.NDC,
			DetailLogEntry.CLASS,
			DetailLogEntry.MESSAGE
		});

		resize = new BrowserWindowResizeListener() {
			@Override
			public void browserWindowResized(BrowserWindowResizeEvent event) {
				updateHeight(event.getHeight());
			}
		};
	}

	private void updateHeight(int height) {
		if(height == -1) {
			table.setPageLength(100);
			return;
		}
		int h = height - 155;
		int h2 = Math.max(0, h / 21 + 1);
		System.err.printf("h:%d / %d\n", h, h2);
		table.setPageLength(h2);
	}

	@Override
	public void attach() {
		super.attach();
		Root root = getRoot();
		root.addListener(resize);
		root.setImmediate(true);
		updateHeight(root.getBrowserWindowHeight());
	}
}
