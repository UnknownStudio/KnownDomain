package nullengine.client.rendering.gui;

import com.github.mouse0w0.observable.value.ObservableValue;
import nullengine.client.asset.AssetURL;
import nullengine.client.gui.GuiManager;
import nullengine.client.gui.Parent;
import nullengine.client.gui.Scene;
import nullengine.client.gui.rendering.Graphics;
import nullengine.client.rendering.display.Window;
import nullengine.client.rendering.font.FontHelper;
import nullengine.client.rendering.gl.shader.ShaderProgram;
import nullengine.client.rendering.gl.shader.ShaderType;
import nullengine.client.rendering.gl.texture.GLFrameBuffer;
import nullengine.client.rendering.shader.ShaderManager;
import nullengine.client.rendering.shader.ShaderProgramBuilder;
import nullengine.client.rendering.texture.TextureManager;
import org.joml.Matrix4f;
import org.joml.Vector2f;
import org.joml.Vector4f;
import org.joml.Vector4fc;

import static org.lwjgl.opengl.GL11.*;

/**
 * render for any gui
 */
public class GuiRenderer {

    private Window window;
    private GuiManager guiManager;

    private ObservableValue<ShaderProgram> shader;

    private Graphics graphics;

    public GuiRenderer(Window window, GuiManager guiManager) {
        this.guiManager = guiManager;
        this.window = window;

        shader = ShaderManager.instance().registerShader("gui_shader",
                new ShaderProgramBuilder().addShader(ShaderType.VERTEX_SHADER, AssetURL.of("engine", "shader/gui.vert"))
                        .addShader(ShaderType.FRAGMENT_SHADER, AssetURL.of("engine", "shader/gui.frag")));

        this.graphics = new GLGraphics(this);
        graphics.setFont(FontHelper.instance().getDefaultFont());
    }

    public void render(float partial) {
        startRender();

        // render scene
        if (guiManager.isHudVisible() && !guiManager.isDisplayingScreen()) {
            for (Scene scene : guiManager.getDisplayingHuds().values()) {
                startRenderFlag();
                renderScene(scene);
            }
        }

        if (guiManager.isDisplayingScreen()) {
            startRenderFlag();
            renderScene(guiManager.getDisplayingScreen());
        }

        endRender();
    }

    public void setClipRect(Vector4fc clipRect) {
        ShaderManager.instance().setUniform("u_ClipRect", clipRect);
    }

    private void startRender() {
        GLFrameBuffer.bindScreenFrameBuffer();

        if (window.isResized()) {
            glViewport(0, 0, window.getWidth(), window.getHeight());
        }

        ShaderProgram shader = this.shader.getValue();
        ShaderManager.instance().bindShader(shader);

        startRenderFlag();

        int width = window.getWidth(), height = window.getHeight();
        shader.setUniform("u_ProjMatrix", new Matrix4f().setOrtho(0, width, height, 0, 1000, -1000));
        shader.setUniform("u_ModelMatrix", new Matrix4f());
        shader.setUniform("u_WindowSize", new Vector2f(width, height));
        shader.setUniform("u_ClipRect", new Vector4f(0, 0, width / window.getContentScaleX(), height / window.getContentScaleY())); // Shader will scale this back

        TextureManager.instance().getWhiteTexture();
    }

    private void startRenderFlag() {
        glEnable(GL_BLEND);
        glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);
        glEnable(GL_LINE_SMOOTH);
        glHint(GL_LINE_SMOOTH_HINT, GL_NICEST);
        glEnable(GL_POINT_SMOOTH);
        glHint(GL_POINT_SMOOTH_HINT, GL_NICEST);
        // GL_POLYGON_SMOOTH will cause transparent lines on objects
//        glEnable(GL_POLYGON_SMOOTH);
//        glHint(GL_POLYGON_SMOOTH_HINT, GL_NICEST);
    }

    private void endRender() {
        glDisable(GL_BLEND);
        glDisable(GL_LINE_SMOOTH);
        glDisable(GL_POINT_SMOOTH);
//        glDisable(GL_POLYGON_SMOOTH);
    }

    private void renderScene(Scene scene) {
        var widthScaleless = window.getWidth() / window.getContentScaleX();
        var heightScaleless = window.getHeight() / window.getContentScaleY();
        if (window.isResized() || widthScaleless != scene.getWidth() || heightScaleless != scene.getHeight()) {
            scene.setSize(widthScaleless, heightScaleless);
        }

        scene.update();

        Parent root = scene.getRoot();
        if (!root.visible().get()) {
            return;
        }
        shader.getValue().setUniform("u_ModelMatrix", new Matrix4f().scale(window.getContentScaleX(), window.getContentScaleY(), 1));
        graphics.pushClipRect(0, 0, scene.width().get(), scene.height().get());
        root.getRenderer().render(root, graphics);
        graphics.popClipRect();
        shader.getValue().setUniform("u_ModelMatrix", new Matrix4f());
    }

//    private void debug(RenderContext context) {
//        graphics.setColor(Color.WHITE);
//
//        // TODO: CrossHair, move it.
//        int middleX = context.getWindow().getWidth() / 2, middleY = context.getWindow().getHeight() / 2;
//        graphics.drawLine(middleX, middleY - 10, middleX, middleY + 10);
//        graphics.drawLine(middleX - 10, middleY, middleX + 10, middleY);
//    }

    public void dispose() {
        ShaderManager.instance().unregisterShader("gui_shader");
    }
}
