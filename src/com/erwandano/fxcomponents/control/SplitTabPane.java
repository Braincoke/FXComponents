package com.erwandano.fxcomponents.control;

import javafx.beans.property.DoubleProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.ObjectPropertyBase;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.css.PseudoClass;
import javafx.geometry.Orientation;
import javafx.geometry.Side;
import javafx.scene.Node;
import javafx.scene.control.SplitPane;
import javafx.scene.control.TabPane;
import javafx.scene.layout.AnchorPane;

/**
 * A TabPane inside a SplitPane
 * Whenever the user clicks on a SplitTab, if the SplitTab is visible it will hide the TabPane by moving the
 * SplitPane divider.
 */
public class SplitTabPane extends AnchorPane {

    private static final double DEFAULT_TABPANE_MIN_HEIGHT = 0;

    private static final PseudoClass TOP_PSEUDOCLASS_STATE = PseudoClass.getPseudoClass("top");
    private static final PseudoClass BOTTOM_PSEUDOCLASS_STATE = PseudoClass.getPseudoClass("bottom");
    private static final PseudoClass LEFT_PSEUDOCLASS_STATE = PseudoClass.getPseudoClass("left");
    private static final PseudoClass RIGHT_PSEUDOCLASS_STATE = PseudoClass.getPseudoClass("right");

    /*******************************************************************************************************************
     *                                                                                                                 *
     * CONSTRUCTORS                                                                                                    *
     *                                                                                                                 *
     ******************************************************************************************************************/

    public SplitTabPane(){
        tabPane = new TabPane();
        tabPane.setRotateGraphic(true);
        tabPane.sideProperty().bindBidirectional(this.sideProperty());
        tabs.addListener(new ListChangeListener<SplitTab>() {
            @Override
            public void onChanged(Change<? extends SplitTab> c) {
                c.next();
                c.getAddedSubList().forEach(SplitTabPane.this::addTab);
                c.getRemoved().forEach(SplitTabPane.this::removeTab);
            }
        });
        tabPane.minHeightProperty().bind(this.tabPaneMinHeightProperty());
        tabPane.minWidthProperty().bind(this.tabPaneMinWidthProperty());
        content = new AnchorPane();
        splitPane = new SplitPane(new AnchorPane(), new AnchorPane());
        position = new SplitTabPosition(splitPane, tabPane);
        setSide(tabPane.getSide());
        this.getChildren().add(splitPane);
        AnchorPane.setTopAnchor(splitPane, 0d);
        AnchorPane.setBottomAnchor(splitPane, 0d);
        AnchorPane.setLeftAnchor(splitPane, 0d);
        AnchorPane.setRightAnchor(splitPane, 0d);
        collapseTabPane();
    }




    /*******************************************************************************************************************
     *                                                                                                                 *
     * ATTRIBUTES                                                                                                      *
     *                                                                                                                 *
     ******************************************************************************************************************/

    /**
     * The SplitPane
     */
    private SplitPane splitPane;

    public SplitPane getSplitPane() {
        return splitPane;
    }

    /**
     * The content of the SplitPane
     */
    private Node content;

    public Node getContent() {
        return content;
    }

    public void setContent(Node content) {
        this.content = content;
        switch (tabPane.getSide()){
            case TOP:
            case LEFT:
                splitPane.getItems().setAll(tabPane, content);
                break;
            case RIGHT:
            case BOTTOM:
                splitPane.getItems().setAll(content, tabPane);
                break;
        }
    }

    /**
     * The TabPane inside the SplitPane
     */
    private TabPane tabPane;

    public TabPane getTabPane() {
        return tabPane;
    }

    /**
     * The currently selected tab
     */
    private SplitTab selectedTab;

    /**
     * The saved position of the SplitTab
     */
    private SplitTabPosition position;

    /*******************************************************************************************************************
     *                                                                                                                 *
     * GETTERS AND SETTERS                                                                                             *
     *                                                                                                                 *
     ******************************************************************************************************************/

    /**
     * Set the disposition of the tabPane inside the splitPane
     * @param side
     */
    public void setSide(Side side){
        sideProperty().set(side);
        switch(side){
            case LEFT:
                splitPane.getItems().setAll(tabPane, content);
                splitPane.setOrientation(Orientation.HORIZONTAL);
                break;
            case RIGHT:
                splitPane.getItems().setAll(content, tabPane);
                splitPane.setOrientation(Orientation.HORIZONTAL);
                break;
            case TOP:
                splitPane.getItems().setAll(tabPane, content);
                splitPane.setOrientation(Orientation.VERTICAL);
                break;
            case BOTTOM:
                splitPane.getItems().setAll(content, tabPane);
                splitPane.setOrientation(Orientation.VERTICAL);
                break;
        }
        tabPane.getTabs().forEach(tab -> {
            SplitTab splitTab = (SplitTab) tab;
            splitTab.setSide(side);
        });
        position.setSide(side);
        collapseTabPane();
    }

    /**
     * Add a tab
     */
    public boolean addTab(SplitTab splitTab){
        splitTab.setSide(tabPane.getSide());
        boolean result = tabPane.getTabs().add(splitTab);
        splitTab.getLabel().setOnMouseClicked(event -> tabSelection(splitTab));
        return  result;
    }

    /**
     * Add a tab at the specified index
     * @param index     Index
     * @param splitTab  SplitTab
     */
    public void addTab(int index, SplitTab splitTab){
        splitTab.setSide(tabPane.getSide());
        tabPane.getTabs().add(index, splitTab);
        splitTab.getLabel().setOnMouseClicked(event -> tabSelection(splitTab));
    }

    /**
     * Remove the specified tab
     * @param splitTab  The specified tab
     * @return
     */
    public boolean removeTab(SplitTab splitTab){
        boolean result = tabPane.getTabs().remove(splitTab);
        splitTab.getLabel().setOnMouseClicked(null);
        return result;
    }

    /**
     * Remove the tab at the specified index
     * @param index The index
     * @return      The SplitTab removed
     */
    public SplitTab removeTab(int index){
        SplitTab splitTab = (SplitTab) tabPane.getTabs().remove(index);
        splitTab.getLabel().setOnMouseClicked(null);
        return splitTab;
    }

    /**
     * Remove every tab specified
     * @param elements  The tabs to remove
     * @return          If the removal was successful or not
     */
    public boolean removeAllTabs(SplitTab...elements){
        return tabPane.getTabs().removeAll(elements);
    }


    /*******************************************************************************************************************
     *                                                                                                                 *
     * TAB BEHAVIOUR                                                                                                   *
     *                                                                                                                 *
     ******************************************************************************************************************/


    /**
     * Handle the tab selection
     * The tab is either hidden or shown
     * @param clickedTab The tab selected
     */
    private void tabSelection(SplitTab clickedTab) {
        //The user clicked on an already selected tab to hide or show the left menu
        if(selectedTab == clickedTab){
            if(position.isHidden()) {
                tabPane.getSelectionModel().getSelectedItem().getStyleClass().remove("hidden");
                showTabPane();
            } else {
                tabPane.getSelectionModel().getSelectedItem().getStyleClass().add("hidden");
                collapseTabPane();
            }
        } else {
            //The user wants to change tabs
            tabPane.getSelectionModel().getSelectedItem().getStyleClass().remove("hidden");
            tabPane.getSelectionModel().select(clickedTab);
            selectedTab = clickedTab;
            showTabPane();
        }
    }



    /**
     * Hide the tabPane
     */
    public void collapseTabPane() {
        splitPane.setDividerPositions(position.getHiddenPosition());
    }

    /**
     * Show the tabPane
     */
    public void showTabPane() {
        splitPane.setDividerPositions(position.getSavedPosition());
    }


    /*******************************************************************************************************************
     *                                                                                                                 *
     * USED FOR FXML                                                                                                   *
     *                                                                                                                 *
     ******************************************************************************************************************/

    private ObjectProperty<Side> side;

    /**
     * The current position of the tabs in the TabPane.  The default position
     * for the tabs is Side.Top.
     *
     * @return The current position of the tabs in the TabPane.
     */
    public final Side getSide() {
        return side == null ? Side.TOP : side.get();
    }

    /**
     * The position of the tabs in the SplitTabPane.
     */
    public final ObjectProperty<Side> sideProperty() {
        if (side == null) {
            side = new ObjectPropertyBase<Side>(Side.TOP) {
                private Side oldSide;
                @Override protected void invalidated() {

                    oldSide = get();

                    pseudoClassStateChanged(TOP_PSEUDOCLASS_STATE, (oldSide == Side.TOP || oldSide == null));
                    pseudoClassStateChanged(RIGHT_PSEUDOCLASS_STATE, (oldSide == Side.RIGHT));
                    pseudoClassStateChanged(BOTTOM_PSEUDOCLASS_STATE, (oldSide == Side.BOTTOM));
                    pseudoClassStateChanged(LEFT_PSEUDOCLASS_STATE, (oldSide == Side.LEFT));
                }

                @Override
                public Object getBean() {
                    return SplitTabPane.this;
                }

                @Override
                public String getName() {
                    return "side";
                }
            };
        }
        return side;
    }


    private ObservableList<SplitTab> tabs = FXCollections.observableArrayList();


    /**
     * <p>The tabs to display in this TabPane. Changing this ObservableList will
     * immediately result in the TabPane updating to display the new contents
     * of this ObservableList.</p>
     *
     * <p>If the tabs ObservableList changes, the selected tab will remain the previously
     * selected tab, if it remains within this ObservableList. If the previously
     * selected tab is no longer in the tabs ObservableList, the selected tab will
     * become the first tab in the ObservableList.</p>
     */
    public final ObservableList<SplitTab> getTabs() {
        return tabs;
    }

    private DoubleProperty tabPaneMinWidth;


    private DoubleProperty tabPaneMinHeight;

    /**
     * The minimum height of the TabPane.
     */
    public final void setTabPaneMinHeight(double value) {
        tabPaneMinHeightProperty().setValue(value);
    }

    /**
     * The minimum height of the TabPane.
     *
     * @return The minimum height of the TabPane.
     */
    public final double getTabPaneMinHeight() {
        return tabPaneMinHeight == null ? DEFAULT_TABPANE_MIN_HEIGHT : tabPaneMinHeight.getValue();
    }

    /**
     * The minimum height of the TabPane.
     */
    public final DoubleProperty tabPaneMinHeightProperty() {
        if (tabPaneMinHeight == null) {
            tabPaneMinHeight = new SimpleDoubleProperty(DEFAULT_TABPANE_MIN_HEIGHT) {

                @Override
                public Object getBean() {
                    return SplitTabPane.this;
                }

                @Override
                public String getName() {
                    return "tabPaneMinHeight";
                }
            };
        }
        return tabPaneMinHeight;
    }

    /**
     * <p>The minimum width of the TabPane.
     * </p>
     */
    public final void setTabPaneMinWidth(double value) {
        tabPaneMinWidthProperty().setValue(value);
    }

    /**
     * The minimum width of the TabPane.
     *
     * @return The minimum width of the TabPane.
     */
    public final double getTabPaneMinWidth() {
        return tabPaneMinWidth == null ? DEFAULT_TABPANE_MIN_HEIGHT : tabPaneMinWidth.getValue();
    }

    /**
     * The minimum width of the TabPane.
     */
    public final DoubleProperty tabPaneMinWidthProperty() {
        if (tabPaneMinWidth == null) {
            tabPaneMinWidth = new SimpleDoubleProperty(DEFAULT_TABPANE_MIN_HEIGHT) {

                @Override
                public Object getBean() {
                    return SplitTabPane.this;
                }

                @Override
                public String getName() {
                    return "tabPaneMinWidth";
                }
            };
        }
        return tabPaneMinWidth;
    }


}
