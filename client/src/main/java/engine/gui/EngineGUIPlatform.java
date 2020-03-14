package engine.gui;

import engine.Platform;
import engine.graphics.GraphicsEngine;
import engine.graphics.display.Window;
import engine.gui.internal.ClipboardHelper;
import engine.gui.internal.GUIPlatform;
import engine.gui.internal.SceneHelper;
import engine.gui.internal.StageHelper;
import engine.gui.internal.impl.InputHelperImpl;
import engine.gui.internal.impl.SceneHelperImpl;
import engine.gui.internal.impl.StageHelperImpl;
import engine.gui.internal.impl.glfw.GLFWClipboardHelper;
import engine.gui.stage.Stage;

public final class EngineGUIPlatform extends GUIPlatform {

    private final StageHelperImpl stageHelper = new StageHelperImpl();
    private final SceneHelper sceneHelper = new SceneHelperImpl();
    private final ClipboardHelper clipboardHelper = new GLFWClipboardHelper();

    private final Window primaryWindow = GraphicsEngine.getGraphicsBackend().getPrimaryWindow();

    private Stage guiStage;
    private Stage hudStage;

    public EngineGUIPlatform() {
        setInstance(this);
        InputHelperImpl.initialize();
        Stage.getStages().addChangeListener(change -> {
            if (change.getList().isEmpty()) Platform.getEngine().terminate();
        });
        initGUIStage();
        initHUDStage();
    }

    private void initGUIStage() {
        Stage stage = stageHelper.getPrimaryStage();
        StageHelper.setWindow(stage, primaryWindow);
        StageHelper.getShowingProperty(stage).set(true);
        StageHelper.doVisibleChanged(stage, true);
        stageHelper.enableInput(stage);
        guiStage = stage;
    }

    private void initHUDStage() {
        Stage stage = new Stage();
        StageHelper.setPrimary(stage, true);
        StageHelper.setWindow(stage, primaryWindow);
        StageHelper.getShowingProperty(stage).set(true);
        StageHelper.doVisibleChanged(stage, true);
        hudStage = stage;
    }

    public Stage getGUIStage() {
        return guiStage;
    }

    public Stage getHUDStage() {
        return hudStage;
    }

    @Override
    public StageHelper getStageHelper() {
        return stageHelper;
    }

    @Override
    public SceneHelper getSceneHelper() {
        return sceneHelper;
    }

    @Override
    public ClipboardHelper getClipboardHelper() {
        return clipboardHelper;
    }

    public void dispose() {
    }
}