package com.erwandano.fxcomponents.control;

import javafx.beans.value.ChangeListener;
import javafx.geometry.Side;
import javafx.scene.control.SplitPane;
import javafx.scene.control.TabPane;

/**
 * Save the position of a SplitTab
 */
public class SplitTabPosition {


    private static final double DEFAULT_WIDTH_THRESHOLD = 75;
    private static final double DEFAULT_WIDTH = 200;


    public SplitTabPosition(SplitPane splitPane, TabPane tabPane){
        this.splitPane = splitPane;
        this.tabPane = tabPane;
        this.savedPosition = 0;
        initPositionListener();
        initSizeListener();
        splitPane.widthProperty().addListener(sizeListener);
        splitPane.heightProperty().addListener(sizeListener);
        setSide(tabPane.getSide());
        setDefaultWidth(DEFAULT_WIDTH);
        setWidthThreshold(DEFAULT_WIDTH_THRESHOLD);
    }

    /**
     * The splitPane in  the SplitTabPane
     */
    private SplitPane splitPane;

    public SplitPane getSplitPane() {
        return splitPane;
    }

    /**
     * The TabPane in the SplitTabPane
     */
    private TabPane tabPane;

    public TabPane getTabPane() {
        return tabPane;
    }

    /**
     * The side of the tab
     */
    private Side side;

    public Side getSide() {
        return side;
    }

    /**
     * The saved width (or height) of the tab
     */
    private double savedWidth;

    public double getSavedWidth() {
        return savedWidth;
    }

    /**
     * The saved delta.
     * The delta being the distance between the tabPane and the divider, measured in divider position unit.
     */
    private double savedDelta;

    public double getSavedDelta(){
        return savedDelta;
    }

    /**
     * The saved divider position
     */
    private double savedPosition;

    public double getSavedPosition() {
        if(savedPosition==0){
            return defaultPosition;
        } else {
            return savedPosition;
        }
    }

    /**
     * The width below which the SplitTab is considered hidden
     */
    private double widthThreshold;

    public double getWidthThreshold() {
        return widthThreshold;
    }

    /**
     * The divider delta below which the SplitTab is considered hidden
     * The divider delta is defined as the distance between the divider and the SplitTab in terms of divider position.
     *   |              |         |
     *   |              |<-delta->|   delta=0.2
     *   |              |         |
     *   0             0.8        1  (Dividers positions)
     */
    private double deltaThreshold;

    public double getDeltaThreshold() {
        return deltaThreshold;
    }

    /**
     * The divider position below which the SplitTab is considered hidden
     */
    private double positionThreshold;

    public double getPositionThreshold() {
        return positionThreshold;
    }

    /**
     * The divider value to use to hide the SplitTab
     */
    private double hiddenPosition;

    public double getHiddenPosition() {
        return hiddenPosition;
    }

    /**
     * The default width to use when displaying the SplitTab
     */
    private double defaultWidth;

    public double getDefaultWidth() {
        return defaultWidth;
    }

    /**
     * The default delta to use when displaying the SplitTab
     */
    private double defaultDelta;

    public double getDefaultDelta() {
        return defaultDelta;
    }

    /**
     * The default divider position to use when displaying the SplitTab
     */
    private double defaultPosition;

    public double getDefaultPosition() {
        return defaultPosition;
    }

    /**
     * Indicates if the tabPane should be considered hidden
     */
    public boolean isHidden(){
        double currentPosition = splitPane.getDividerPositions()[0];
        double currentDelta = deltaFromPosition(currentPosition);
        return currentDelta <= deltaThreshold;
    }

    /*******************************************************************************************************************
     *                                                                                                                 *
     * SETTERS                                                                                                         *
     *                                                                                                                 *
     ******************************************************************************************************************/

    /**
     * Set the splitPane
     */
    public void setSplitPane(SplitPane splitPane){
        this.splitPane = splitPane;
        /* Refresh the deltas to take into account the new splitpane width */
        refreshDeltas();
    }

    /**
     * Set the tabPane
     */
    public void setTabPane(TabPane tabPane){
        this.tabPane = tabPane;
        setSide(tabPane.getSide());
    }

    /**
     * Set the deltaThreshold
     */
    public void setDeltaThreshold(double delta){
        if(delta>1 || delta <0){
            setWidthThreshold(DEFAULT_WIDTH_THRESHOLD);
        } else {
            this.deltaThreshold = delta;
            this.widthThreshold = widthFromDelta(delta);
            refreshThresholds();
        }
    }

    /**
     * Set the width threshold
     */
    public void setWidthThreshold(double width){
        this.widthThreshold = width;
        this.deltaThreshold = deltaFromWidth(width);
        refreshThresholds();
    }

    /**
     * Set the default width
     */
    public void setDefaultWidth(double width){
        this.defaultWidth = width;
        this.defaultDelta = deltaFromWidth(width);
    }

    /**
     * Set the default delta
     */
    public void setDefaultDelta(double delta){
        if(delta>1 || delta<0){
            setDefaultWidth(DEFAULT_WIDTH);
        } else {
            this.defaultDelta = delta;
            this.defaultWidth = widthFromDelta(delta);
        }
    }

    /**
     * Set the disposition of the tabPane inside the splitPane
     * @param side
     */
    public void setSide(Side side){
        this.side = side;
        switch(side){
            case LEFT:
                hiddenPosition = 0;
                positionThreshold = 0 + deltaThreshold;
                defaultPosition = defaultDelta;
                break;
            case RIGHT:
                hiddenPosition = 1;
                positionThreshold = 1 - deltaThreshold;
                defaultPosition = 1 - defaultDelta;
                break;
            case TOP:
                hiddenPosition = 0;
                positionThreshold = 0 + deltaThreshold;
                defaultPosition = defaultDelta;
                break;
            case BOTTOM:
                hiddenPosition = 1;
                positionThreshold = 1 - deltaThreshold;
                defaultPosition = 1 - defaultDelta;
                break;
        }
        splitPane.getDividers().get(0).positionProperty().addListener(positionListener);
    }

    /*******************************************************************************************************************
     *                                                                                                                 *
     * REFRESHING VALUES                                                                                               *
     *                                                                                                                 *
     ******************************************************************************************************************/



    private void refreshDeltas(){
        setDefaultDelta(this.defaultWidth);
        setWidthThreshold(this.widthThreshold);
    }

    private void refreshThresholds(){
        switch (side){
            case LEFT:
                positionThreshold = 0 + deltaThreshold;
                defaultPosition = defaultDelta;
                break;
            case RIGHT:
                positionThreshold = 1 - deltaThreshold;
                defaultPosition = 1 - defaultDelta;
                break;
            case TOP:
                positionThreshold = 0 + deltaThreshold;
                defaultPosition = defaultDelta;
                break;
            case BOTTOM:
                positionThreshold = 1 - deltaThreshold;
                defaultPosition = 1 - defaultDelta;
                break;
        }
    }

    /**
     * Save the given width
     * @param width Width of the tabPane to save
     */
    private void setSavedWidth(double width){
        /* We only save the width if it is compliant with the current threshold */
        if(width > widthThreshold) {
            this.savedWidth = width;
            this.savedDelta = deltaFromWidth(width);
            this.savedPosition = positionFromDelta(savedDelta);
        }
    }

    /**
     * Save the given position
     */
    private void setSavedPosition(double position){
        if(position<0 || position>1){
            setSavedWidth(DEFAULT_WIDTH);
        } else {
            /* We only save the position if it is compliant with the threshold */
            double delta = deltaFromPosition(position);
            if(delta > deltaThreshold) {
                this.savedPosition = position;
                this.savedDelta = deltaFromPosition(position);
                this.savedWidth = widthFromDelta(this.savedDelta);
            }
        }
    }

    /**
     * Listen to the divider position
     */
    private ChangeListener<Number> positionListener;

    private void initPositionListener(){
        this.positionListener = (observable, oldValue, newValue) ->{
            setSavedPosition(newValue.doubleValue());
        };
    }

    /**
     * Listen to the SplitPane width and height
     */
    private ChangeListener<Number> sizeListener;

    private void initSizeListener(){
        this.sizeListener = (observable, oldValue, newValue) -> {
            refreshDeltas();
            if(!isHidden()) {
                double newDelta = deltaFromWidth(savedWidth);
                double newPosition = positionFromDelta(newDelta);
                splitPane.getDividers().get(0).setPosition(newPosition);
            } else {
                splitPane.getDividers().get(0).setPosition(hiddenPosition);
                /* FIXME Strange UI bug when going from fullscreen hidden to small window */
            }
        };
    }

    /*******************************************************************************************************************
     *                                                                                                                 *
     * CONVERSION                                                                                                      *
     *                                                                                                                 *
     ******************************************************************************************************************/

    /**
     * Get the splitPane total width
     */
    public double getSplitPaneWidth(){
        switch (side){
            case LEFT:
            case RIGHT:
                return splitPane.getWidth();
            case TOP:
            case BOTTOM:
                return splitPane.getHeight();
        }
        return 0;
    }

    /**
     * Computes the width of the tabPane from a given delta
     * @param delta     The distance between the divider and the tabPane measured in divider position
     * @return          The width the tabPane should have for the delta to be complied with
     */
    public double widthFromDelta(double delta){
        double totalWidth = getSplitPaneWidth();
        /* The formula is:
         *    delta =  tabPaneWidth / totalWidth;
         */
        return delta*totalWidth;
    }

    /**
     * Computes the delta from the desired tabPane width
     * @param width     The width we would like the tabPane to have
     * @return          The value the delta should have so that the width is obtained
     */
    public double deltaFromWidth(double width){
        double totalWidth = getSplitPaneWidth();
        return width/totalWidth;
    }

    /**
     * Computes the divider's position from the desired delta
     * @param delta     The delta we wish to have
     * @return          The position the divider should have for the delta to be obtained
     */
    public double positionFromDelta(double delta){
        switch (side){
            case LEFT:
            case TOP:
                return delta;
            case BOTTOM:
            case RIGHT:
                return 1-delta;
        }
        return 0;
    }

    /**
     * Computes the delta from the desired position
     * @param position      The position we want the divider to have
     * @return              The delta related to that position
     */
    public double deltaFromPosition(double position){
        switch (side){
            case LEFT:
            case TOP:
                return position;
            case BOTTOM:
            case RIGHT:
                return 1-position;
        }
        return 0;
    }
}
