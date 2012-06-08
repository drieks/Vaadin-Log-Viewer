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
				// helper.save();
			}
		});

		treetable.addActionHandler(new Action.Handler() {
			@Override
			public void handleAction(Action action, Object sender, Object target) {
				FilterSettingData data = getItem(target);
				if(action == ADD_ITEM_ACTION || action == ADD_GROUP_ACTION) {
					/*
					FilterSettingData item = getContainer().addItem();
					getContainer().setParent(item, target);
					// item.setParent((FilterSettingData)target);
					if(action == ADD_ITEM_ACTION) {
						item.setName("Item");
					} else {
						item.setChildrenAllowed(true);
						item.setName("Group");
					}
					getContainer().update();
					*/
				} else if(action == REMOVE_ACTION) {
					/*
					getContainer().removeItem(target);
					getContainer().update();
					*/
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
				} else if(getItem(target).isGroup()) {
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

	/**
	 * Move a node within a tree onto, above or below another node depending
	 * on the drop location.
	 * 
	 * @param sourceItemId
	 *            id of the item to move
	 * @param targetItemId
	 *            id of the item onto which the source node should be moved
	 * @param location
	 *            VerticalDropLocation indicating where the source node was
	 *            dropped relative to the target node
	 */
	private void moveNode(Object sourceItemId, Object targetItemId, VerticalDropLocation location) {
		// Sorting goes as
		// - If dropped ON a node, we append it as a child
		// - If dropped on the TOP part of a node, we move/add it before
		// the node
		// - If dropped on the BOTTOM part of a node, we move/add it
		// after the node

		/*
		if(sourceItemId == targetItemId) {
			System.err.println("skip drop!");
			return;
		}
		if(location == VerticalDropLocation.MIDDLE) {
			if(getContainer().areChildrenAllowed(targetItemId) && getContainer().setParent(sourceItemId, targetItemId)
					&& getContainer().hasChildren(targetItemId)) {
				// move first in the container
				getContainer().moveAfterSibling(sourceItemId, null);
			}
		} else if(location == VerticalDropLocation.TOP) {
			Object parentId = getContainer().getParent(targetItemId);
			if(getContainer().setParent(sourceItemId, parentId)) {
				getContainer().moveAfterSibling(sourceItemId, targetItemId);
			}
		} else if(location == VerticalDropLocation.BOTTOM) {
			Object parentId = getContainer().getParent(targetItemId);
			if(getContainer().setParent(sourceItemId, parentId)) {
				// reorder only the two items, moving source above target
				getContainer().moveAfterSibling(sourceItemId, targetItemId);
				getContainer().moveAfterSibling(targetItemId, sourceItemId);
			}
		}
		getContainer().update();
		*/
	}
}
