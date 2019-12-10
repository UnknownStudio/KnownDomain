package nullengine.client.gui;

import com.github.mouse0w0.observable.collection.ObservableCollections;
import com.github.mouse0w0.observable.collection.ObservableList;
import com.github.mouse0w0.observable.value.ObservableValue;
import com.github.mouse0w0.observable.value.ValueChangeListener;
import nullengine.client.gui.component.Control;
import nullengine.client.gui.util.Utils;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public abstract class Parent extends Node {

    private final ObservableList<Node> children = ObservableCollections.observableList(new LinkedList<>());
    private final ObservableList<Node> unmodifiableChildren = ObservableCollections.unmodifiableObservableList(children);

    public Parent() {
        children.addChangeListener(change -> {
            for (Node node : change.getAdded()) {
                Parent oldParent = node.parent.get();
                if (oldParent != null) {
                    node.scene.unbindBidirectional(oldParent.scene);
                }
                node.parent.setValue(this);
                node.scene.bindBidirectional(Parent.this.scene);
                node.parent.addChangeListener(new ValueChangeListener<>() {
                    @Override
                    public void onChanged(ObservableValue<? extends Parent> observable, Parent oldValue, Parent newValue) {
                        children.remove(node);
                        observable.removeChangeListener(this);
                    }
                });
            }
            for (Node node : change.getRemoved()) {
                if (node.parent.get() == this) {
                    node.scene.unbindBidirectional(Parent.this.scene);
                    node.parent.set(null);
                }
            }
            needsLayout();
        });
    }

    public ObservableList<Node> getChildren() {
        return children;
    }

    public final ObservableList<Node> getUnmodifiableChildren() {
        return unmodifiableChildren;
    }

    public final List<Node> getChildrenRecursive() {
        var list = new ArrayList<Node>();
        for (Node child : children) {
            if (child instanceof Parent) {
                list.addAll(((Parent) child).getChildrenRecursive());
            }
            list.add(child);
        }
        return list;
    }

    @Override
    public float prefWidth() {
        float minX = 0, maxX = 0;
        for (Node child : getChildren()) {
            float childMinX = child.x().get();
            float childMaxX = childMinX + Math.max(Utils.prefWidth(child), child.width().get());
            if (minX > childMinX) {
                minX = childMinX;
            }
            if (maxX < childMaxX) {
                maxX = childMaxX;
            }
        }
        return maxX - minX;
    }

    @Override
    public float prefHeight() {
        float minY = 0, maxY = 0;
        for (Node child : getChildren()) {
            float childMinY = child.y().get();
            float childMaxY = childMinY + Math.max(Utils.prefHeight(child), child.height().get());
            if (minY > childMinY) {
                minY = childMinY;
            }
            if (maxY < childMaxY) {
                maxY = childMaxY;
            }
        }
        return maxY - minY;
    }

    private LayoutState layoutState = LayoutState.NEED_LAYOUT;
    private boolean performingLayout = false;

    public void needsLayout() {
        layoutState = LayoutState.NEED_LAYOUT;
        for (Node child : children) {
            if (child instanceof Parent) {
                ((Parent) child).needsLayout();
            }
        }
        Parent parent = parent().getValue();
        while (parent != null && parent.layoutState == LayoutState.CLEAN) {
            parent.layoutState = LayoutState.DIRTY_BRANCH;
            parent = parent.parent().getValue();
        }
    }

    public boolean isNeedsLayout() {
        return layoutState == LayoutState.NEED_LAYOUT;
    }

    public boolean isShouldUpdate() {
        return layoutState != LayoutState.CLEAN;
    }

    public final void layout() {
        switch (layoutState) {
            case CLEAN:
                break;
            case NEED_LAYOUT:
                if (performingLayout) {
                    break;
                }
                performingLayout = true;
                layoutChildren();
                // Intended fall-through
            case DIRTY_BRANCH:
                for (Node node : getChildren()) {
                    if (node instanceof Parent) {
                        ((Parent) node).layout();
                    }
                }
                layoutState = LayoutState.CLEAN;
                performingLayout = false;
                break;
        }
    }

    protected void layoutChildren() {
        for (Node node : getChildren()) {
            layoutInArea(node, node.x().get(), node.y().get(), Utils.prefWidth(node), Utils.prefHeight(node));
        }
    }

    protected final void layoutInArea(Node node, float x, float y, float width, float height) {
        node.x().set(x);
        node.y().set(y);
        node.width.set(width);
        node.height.set(height);
    }

    public List<Node> getPointingComponents(float posX, float posY) {
        var list = new ArrayList<Node>();
        for (Node node : getChildren()) {
            if (node.contains(posX, posY)) {
                if (node instanceof Parent) {
                    var container = (Parent) node;
                    if (!(node instanceof Control)) {
                        list.add(container);
                    }
                    list.addAll(container.getPointingComponents(posX - container.x().get(), posY - container.y().get()));

                } else {
                    list.add(node);
                }
            }
        }
        return list;
    }

    public List<Node> getPointingLastChildComponents(float posX, float posY) {
        var list = getPointingComponents(posX,posY);
        var toRemove = new ArrayList<Node>();
        for (var i:list){
            if (!i.parent().isEmpty())
                toRemove.add(i.parent().get());
        }
        list.removeAll(toRemove);
        return list;
    }

    private boolean closeRequired = false;

    public boolean closeRequired() {
        return closeRequired;
    }

    public void requireClose() {
        closeRequired = true;
    }

    public void doClosing(GuiManager manager) {
        manager.closeScreen();
    }
}
