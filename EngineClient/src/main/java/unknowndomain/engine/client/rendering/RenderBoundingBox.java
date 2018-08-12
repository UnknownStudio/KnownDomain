package unknowndomain.engine.client.rendering;

import org.joml.AABBd;
import org.joml.Matrix4f;
import unknowndomain.engine.client.EngineClient;
import unknowndomain.engine.client.UnknownDomain;
import unknowndomain.engine.client.model.Mesh;
import unknowndomain.engine.client.model.pipeline.BoundingBoxToMesh;
import unknowndomain.engine.client.shader.Shader;

import java.nio.FloatBuffer;
import java.nio.IntBuffer;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL15.*;
import static org.lwjgl.opengl.GL20.*;
import static org.lwjgl.opengl.GL30.glBindVertexArray;
import static org.lwjgl.opengl.GL30.glGenVertexArrays;

public class RenderBoundingBox extends RendererShaderProgramCommon {
    private int vao = -1;
    private int count = -1;

    public RenderBoundingBox(Shader vertexShader, Shader fragmentShader) {
        super(vertexShader, fragmentShader);
    }

    private void gen() {
        EngineClient engine = UnknownDomain.getEngine();
        AABBd box = engine.getPlayer().getBoundingBox();

        Mesh mesh = new BoundingBoxToMesh().bakeModel(box);
        count = mesh.getIndices().length;

        vao = glGenVertexArrays();
        glBindVertexArray(vao);

        int vbo = glGenBuffers();
        glBindBuffer(GL_ARRAY_BUFFER, vbo);
        glBufferData(GL_ARRAY_BUFFER, FloatBuffer.wrap(mesh.getVertices()), GL_DYNAMIC_DRAW);
        glVertexAttribPointer(0, 3, GL_FLOAT, false, 0, 0);
        glBindBuffer(GL_ARRAY_BUFFER, 0);

        vbo = glGenBuffers();
        glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, vbo);
        glBufferData(GL_ELEMENT_ARRAY_BUFFER, IntBuffer.wrap(mesh.getIndices()), GL_DYNAMIC_DRAW);

        glBindBuffer(GL_ARRAY_BUFFER, 0);

        glBindVertexArray(0);
    }

    @Override
    public void render(Context context) {
        super.render(context);

        if (vao == -1) gen();

        for (int i = 0; i < 10; i++) {
            Shader.setUniform(u_Model, new Matrix4f().setTranslation(
                    UnknownDomain.getEngine().getPlayer().getPosition())
                    .translate(i, 0, i));

            glBindVertexArray(vao);
            glEnableVertexAttribArray(0);
            glDrawElements(GL_TRIANGLES, count, GL_UNSIGNED_INT, 0);
            glDisableVertexAttribArray(0);
            glBindVertexArray(0);
        }
    }
}