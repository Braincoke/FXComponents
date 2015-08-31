package com.erwandano.fxcomponents.control;

import javafx.geometry.Insets;
import javafx.geometry.Side;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.control.Tab;

/**
 * A Tab inside a SplitTabPane
 */
public class SplitTab extends Tab{

    public SplitTab(){
        label = new Label();
        label.setPadding(new Insets(5));
        setGraphic(label);
        setClosable(false);
        setStyle("-fx-padding: 0px");
        textProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue.trim().compareTo("") != 0) {
                setTabText(newValue);
            }
        });
        graphicProperty().addListener((observable, oldValue, newValue) -> {
            if(newValue!=label && newValue!=null) {
                setTabGraphic(newValue);
            }
        });
    }

    /**
     * Init a SplitTab from an existing Tab
     */
    public SplitTab(Tab pTab){
        super();
        setContent(pTab.getContent());
        setTabText(pTab.getText());
        setTabGraphic(pTab.getGraphic());
    }

    /**
     * The Label used to display the tab and its graphics
     */
    private Label label;

    public Label getLabel() {
        return label;
    }

    /**
     * The developers of JavaFX decided to make the setText method final
     * so now I have to create a new method for that
     */
    public void setTabText(String text){
        this.setText("");
        label.setText(text);
    }

    public String getTabText(){
        return label.getText();
    }

    /**
     * Choose the graphics
     */
    public void setTabGraphic(Node graphics){
        setGraphic(null);
        label.setGraphic(graphics);
    }

    public Node getTabGraphic(){
        return label.getGraphic();
    }

    /**
     * Set the side of the tab
     * @param side  Either left, right, top or bottom
     */
    public void setSide(Side side){
        switch (side){
            case LEFT:
                label.setRotate(-90);
                break;
            case RIGHT:
                label.setRotate(90);
                break;
        }
    }


}
