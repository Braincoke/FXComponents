import com.erwandano.fxcomponents.control.SplitTab;
import com.erwandano.fxcomponents.control.SplitTabPane;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Side;
import javafx.scene.layout.AnchorPane;

import java.net.URL;
import java.util.ResourceBundle;

public class Controller implements Initializable {

    @FXML
    private AnchorPane anchorPane;
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        SplitTabPane splitTabPane = new SplitTabPane();
        SplitTab tab1 = new SplitTab();
        tab1.setTabText("Hello");
        SplitTab tab2 = new SplitTab();
        tab2.setTabText("World");
        splitTabPane.addTab(tab1);
        splitTabPane.addTab(tab2);
        anchorPane.getChildren().add(splitTabPane);
        AnchorPane.setRightAnchor(splitTabPane, 0d);
        AnchorPane.setLeftAnchor(splitTabPane, 0d);
        AnchorPane.setBottomAnchor(splitTabPane, 0d);
        AnchorPane.setTopAnchor(splitTabPane, 0d);
        splitTabPane.getTabPane().setMinHeight(35);
        splitTabPane.getTabPane().setMinWidth(35);
        splitTabPane.setSide(Side.BOTTOM);
    }

}
