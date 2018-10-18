import com.sun.istack.internal.NotNull;
import javafx.collections.ObservableList;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.layout.Pane;
import javafx.scene.shape.Shape;
import javafx.scene.text.Text;

import java.util.HashMap;

public class NodeMapper {
    private HashMap<String, Node> nodeMap;
    private HashMap<Node, Integer> visitedNodes;
    private int maxNodeVisitLimit = 1;

    public NodeMapper(@NotNull Node root) {
        nodeMap = new HashMap<>();
        visitedNodes = new HashMap<>();
    }

    public NodeMapper() {
        nodeMap = new HashMap<>();
        visitedNodes = new HashMap<>();
    }

    public void setRoot(@NotNull Node root) {
        nodeMap.clear();
        mapNode(root);
    }

    public HashMap<String, Node> getMap() {
        if (nodeMap.size() < 1) {
            throw new IllegalStateException("Not data on node map,make sure root is set.");
        }
        return nodeMap;
    }

    private void mapNode(@NotNull Node root) {
        if (isVisitLimit(root)) {
            if (root instanceof Pane) {
                mapPane((Pane) root);
            } else if (root instanceof Control) {
                mapControl((Control) root);
            } else if (root instanceof Shape) {
                mapShape((Shape) root);
            }
        }
    }

    private void mapPane(@NotNull Pane root) {
        ObservableList<Node> children = root.getChildren();
        for (Node node : children) {
            if (node != null) {
                mapNode(node);
            }
        }
    }

    private void mapShape(@NotNull Shape root) {
        if (root instanceof Text) {
            addNodeToMap(root);
        }
    }

    private void addNodeToMap(@NotNull Node root) {
        if (root.getId() != null) {
            nodeMap.put(root.getId(), root);
        }
    }

    private void mapControl(@NotNull Control root) {
        if (root instanceof ScrollPane | root instanceof ButtonBar | root instanceof TabPane
                | root instanceof SplitPane | root instanceof Accordion) {
            mapControlWithChildren(root);
        } else if (root instanceof Labeled) {
            mapLabeled((Labeled) root);
        } else if (root instanceof TextInputControl) {
            mapTextInputControl((TextInputControl) root);
        } else if (root instanceof ComboBoxBase) {
            mapComboBase((ComboBoxBase) root);
        } else if (root instanceof TreeTableView | root instanceof TableView) {
            mapTable(root);
        } else if (root instanceof ProgressIndicator) {
            mapProgressIndicator((ProgressIndicator) root);
        }
    }

    private void mapProgressIndicator(ProgressIndicator root) {
        addNodeToMap(root);
    }

    private void mapTable(Control root) {
        addNodeToMap(root);
    }

    private void mapComboBase(@NotNull ComboBoxBase root) {
        addNodeToMap(root);
    }

    private void mapTextInputControl(@NotNull TextInputControl root) {
        addNodeToMap(root);
    }

    private void mapControlWithChildren(@NotNull Control root) {
        if (root instanceof ButtonBar) {
            getNodesFromButtonBar((ButtonBar) root);
        } else if (root instanceof ScrollPane) {
            getNodesFromScrollPane((ScrollPane) root);
        } else if (root instanceof TabPane) {
            getNodesFromTabPane((TabPane) root);
        } else if (root instanceof SplitPane) {
            getNodesFromSplitPane((SplitPane) root);
        } else if (root instanceof Accordion) {
            getNodesFromAccordion((Accordion) root);
        }
    }

    private void getNodesFromAccordion(@NotNull Accordion root) {
        ObservableList<TitledPane> panes = root.getPanes();
        for (TitledPane pane : panes) {
            if (pane != null) {
                mapNode(pane);
            }
        }
    }

    private void getNodesFromSplitPane(@NotNull SplitPane root) {
        ObservableList<Node> nodes = root.getItems();
        for (Node node : nodes) {
            if (node != null) {
                mapNode(node);
            }
        }
    }

    private void getNodesFromTabPane(@NotNull TabPane root) {
        ObservableList<Tab> tabs = root.getTabs();
        for (Tab tab : tabs) {
            Node node = tab.getContent();
            if (node != null) {
                mapNode(node);
            }
        }
    }

    private void getNodesFromScrollPane(@NotNull ScrollPane root) {
        Node node = root.getContent();
        if (node != null) {
            mapNode(node);
        }
    }

    private void getNodesFromButtonBar(@NotNull ButtonBar buttonBar) {
        ObservableList<Node> nodes = buttonBar.getButtons();
        for (Node node : nodes) {
            mapNode(node);
        }
    }

    private void mapLabeled(@NotNull Labeled root) {
        if (root instanceof TitledPane) {
            getNodesFromTitledPane((TitledPane) root);
        } else {
            addNodeToMap(root);
        }
        Node graphic = root.getGraphic();
        if (graphic != null) {
            mapNode(graphic);
        }
    }

    private void getNodesFromTitledPane(@NotNull TitledPane root) {
        Node content = root.getContent();
        if (content != null) {
            mapNode(content);
        }
        addNodeToMap(root);
    }

    private boolean isVisitLimit(Node root) {
        if (visitedNodes.containsKey(root)) {
            if (visitedNodes.get(root) > maxNodeVisitLimit) {
                return true;
            }
        }
        return false;
    }

    public void setMaxNodeVisit(int count) {
        maxNodeVisitLimit = count;
    }
}
