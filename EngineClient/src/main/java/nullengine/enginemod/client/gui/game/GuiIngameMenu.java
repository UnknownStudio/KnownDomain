package nullengine.enginemod.client.gui.game;

import nullengine.Platform;
import nullengine.client.gui.GUIManager;
import nullengine.client.gui.Scene;
import nullengine.client.gui.control.Button;
import nullengine.client.gui.control.Label;
import nullengine.client.gui.layout.BorderPane;
import nullengine.client.gui.layout.VBox;
import nullengine.client.gui.misc.Background;
import nullengine.client.gui.misc.Insets;
import nullengine.client.gui.misc.Pos;
import nullengine.client.rendering.font.Font;
import nullengine.util.Color;

public class GuiIngameMenu extends BorderPane {
    public GuiIngameMenu(){
        VBox vBox = new VBox();
        vBox.spacing().set(5);
        vBox.alignment().setValue(Pos.HPos.CENTER);
        center().setValue(vBox);
        this.background().setValue(new Background(Color.fromARGB(0x7FAAAAAA)));
        vBox.padding().setValue(new Insets(100, 350, 0, 350));

        Label text = new Label();
        text.text().setValue("Game Menu");
        text.font().setValue(new Font(Font.getDefaultFont(), 20));
        vBox.getChildren().add(text);

        Button terminateGame = new Button("Terminate");
        terminateGame.setOnMouseClicked(mouseClickEvent -> {
            var engine = Platform.getEngineClient();
            engine.getCurrentGame().terminate();
            GUIManager guiManager = engine.getRenderManager().getGUIManager();
            guiManager.show(new Scene(new GUIGameCreation()));
        });
        vBox.getChildren().add(terminateGame);
    }
}
