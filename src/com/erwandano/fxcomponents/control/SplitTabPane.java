package com.erwandano.fxcomponents.control;

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

    /*******************************************************************************************************************
     *                                                                                                                 *
     * CONSTRUCTORS                                                                                                    *
     *                                                                                                                 *
     ******************************************************************************************************************/

    public SplitTabPane(){
        tabPane = new TabPane();
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
        tabPane.setSide(side);
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
     * The the side of the tabPane
     */
    public Side getSide(){
        return tabPane.getSide();
    }

    /**
     * Add a tab
     */
    public boolean addTab(SplitTab splitTab){
        splitTab.setSide(tabPane.getSide());
        boolean result = tabPane.getTabs().add(splitTab);
        splitTab.getTab().setOnMouseClicked(event -> tabSelection(splitTab));
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
        splitTab.getTab().setOnMouseClicked(event -> tabSelection(splitTab));
    }

    /**
     * Remove the specified tab
     * @param splitTab  The specified tab
     * @return
     */
    public boolean removeTab(SplitTab splitTab){
        boolean result = tabPane.getTabs().remove(splitTab);
        splitTab.getTab().setOnMouseClicked(null);
        return result;
    }

    /**
     * Remove the tab at the specified index
     * @param index The index
     * @return      The SplitTab removed
     */
    public SplitTab removeTab(int index){
        SplitTab splitTab = (SplitTab) tabPane.getTabs().remove(index);
        splitTab.getTab().setOnMouseClicked(null);
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


}
