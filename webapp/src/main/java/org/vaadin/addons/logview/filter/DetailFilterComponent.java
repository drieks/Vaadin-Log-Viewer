package org.vaadin.addons.logview.filter;

import com.vaadin.event.Action;
import com.vaadin.event.DataBoundTransferable;
import com.vaadin.event.Transferable;
import com.vaadin.event.dd.DragAndDropEvent;
import com.vaadin.event.dd.DropHandler;
import com.vaadin.event.dd.acceptcriteria.AcceptAll;
import com.vaadin.event.dd.acceptcriteria.AcceptCriterion;
import com.vaadin.terminal.gwt.client.ui.dd.VerticalDropLocation;
import com.vaadin.ui.AbstractSelect.AbstractSelectTargetDetails;
import com.vaadin.ui.Table.TableDragMode;

public class DetailFilterComponent extends FilterComponent {
	private static final Action ADD_ITEM_ACTION = new Action("Add Item");

	private static final Action ADD_GROUP_ACTION = new Action("Add Group");

	private static final Action REMOVE_ACTION = new Action("Remove");

	private static final Action EDIT_ACTION = new Action("Edit");

	public DetailFilterComponent(FilterSettingContainer containerArg) {
		super(containerArg);

		treetable.setDragMode(TableDragMode.ROW);
		treetable.setDropHandler(new DropHandler() {
			@Override
			public AcceptCriterion getAcceptCriterion() {
				return AcceptAll.get();
			}

			@Override
			public void drop(DragAndDropEvent dropEvent) {
				// Called whenever a drop occurs on the component

				// Make sure the drag source is the same tree
				Transferable t = dropEvent.getTransferable();

				// see the comment in getAcceptCriterion()
				if(t.getSourceComponent() != treetable || !(t instanceof DataBoundTransferable)) {
					return;
				}

				AbstractSelectTargetDetails dropData = ((AbstractSelectTargetDetails)dropEvent.getTargetDetails());

				Object sourceItemId = ((DataBoundTransferable)t).getItemId();
				// FIXME: Why "over", should be "targetItemId" or just
				// "getItemId"
				Object targetItemId = dropData.getItemIdOver();

				// Location describes on which part of the node the drop took
				// place
				VerticalDropLocation location = dropData.getDropLocation();

				moveNode(sourceItemId, targetItemId, location);
			}
		});

		treetable.addActionHandler(new Action.Handler() {
			@Override
			public void handleAction(Action action, Object sender, Object target) {
				FilterSettingData data = getData(target);
				if(action == ADD_ITEM_ACTION || action == ADD_GROUP_ACTION) {
					container.createItem(action == ADD_GROUP_ACTION, getData(target));
					update();
				} else if(action == REMOVE_ACTION) {
					container.removeItem(target);
					update();
				} else if(action == EDIT_ACTION) {
					itemClicked(data);
				} else {
					getRoot().showNotification("Action " + action.toString());
				}
			}

			@Override
			public Action[] getActions(Object target, Object sender) {
				if(target == null) {
					// Context menu in an empty space -> add a new main category
					return new Action[] {
						ADD_ITEM_ACTION, ADD_GROUP_ACTION
					};
				} else if(getData(target).isGroup()) {
					// Context menu for an group
					return new Action[] {
						ADD_ITEM_ACTION, ADD_GROUP_ACTION, EDIT_ACTION, REMOVE_ACTION
					};
				} else {
					// Context menu for an item
					return new Action[] {
						EDIT_ACTION, REMOVE_ACTION
					};
				}
			}
		});
	}

	private void moveNode(Object sourceItemId, Object targetItemId, VerticalDropLocation location) {
		if(sourceItemId == targetItemId) {
			System.err.println("skip drop!");
			return;
		}
		FilterSettingData source = getData(sourceItemId);
		FilterSettingData target = getData(targetItemId);
		if(container.isParentOf(source.getId(), target)) {
			return;
		}
		if(location == VerticalDropLocation.MIDDLE) {
			if(target.isGroup()) {
				source.setParent(target.getId());
				// move first in the container
				container.moveAfterSibling(source, null);
			}
		} else if(location == VerticalDropLocation.TOP) {
			source.setParent(target.getParent());
			container.moveAfterSibling(source, target);
			container.moveAfterSibling(target, source);
		} else if(location == VerticalDropLocation.BOTTOM) {
			source.setParent(target.getParent());
			container.moveAfterSibling(source, target);
		}
		update();
	}
}
