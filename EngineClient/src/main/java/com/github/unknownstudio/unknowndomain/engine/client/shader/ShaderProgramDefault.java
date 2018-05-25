package com.github.unknownstudio.unknowndomain.engine.client.shader;

import com.github.unknownstudio.unknowndomain.engine.client.util.GLHelper;
import org.joml.Matrix4f;
import org.joml.Vector3f;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL30;
import org.lwjgl.system.MemoryStack;

import java.nio.FloatBuffer;

public class ShaderProgramDefault extends ShaderProgram {

    private int shaderId = -1;
    private int vertexShaderId = -1;
    private int fragmentShaderId = -1;

    @Override
    public void createShader() {

        vertexShaderId = GL20.glCreateShader(GL20.GL_VERTEX_SHADER);

        //TODO: maybe we should extract shader into a separated class?
        GL20.glShaderSource(vertexShaderId, GLHelper.readText("/assets/unknowndomain/shader/default.vert"));
        GL20.glCompileShader(vertexShaderId);
        if (GL20.glGetShaderi(vertexShaderId, GL20.GL_COMPILE_STATUS) == 0) {
            System.out.println("Error compiling Shader code: " + GL20.glGetShaderInfoLog(vertexShaderId, 1024));
        }

        fragmentShaderId = GL20.glCreateShader(GL20.GL_FRAGMENT_SHADER);

        GL20.glShaderSource(fragmentShaderId, GLHelper.readText("/assets/unknowndomain/shader/default.frag"));
        GL20.glCompileShader(fragmentShaderId);
        if (GL20.glGetShaderi(fragmentShaderId, GL20.GL_COMPILE_STATUS) == 0) {
            System.out.println("Error compiling Shader code: " + GL20.glGetShaderInfoLog(fragmentShaderId, 1024));
        }

        shaderId = GL20.glCreateProgram();
        GL20.glAttachShader(shaderId, vertexShaderId);
        GL20.glAttachShader(shaderId, fragmentShaderId);
        GL30.glBindFragDataLocation(shaderId, 0, "fragColor");
        GL20.glLinkProgram(shaderId);
        useShader();

        GL20.glValidateProgram(shaderId);

        //We can deleter the shader after linking to the shader program
        GL20.glDeleteShader(vertexShaderId);
        GL20.glDeleteShader(fragmentShaderId);

        int uniTex = getUniformLocation("texImage");
        GL20.glUniform1i(uniTex, 0);

        Matrix4f model = new Matrix4f();
        model.identity();
        int uniModel = getUniformLocation("model");
        setUniform(uniModel, model);

        Matrix4f view = new Matrix4f();
        view.identity();
        view = view.lookAt(new Vector3f(0,0,-5f),new Vector3f(0,0,0f),new Vector3f(0,1,0)); //TODO: Controlled by Camera
        int uniView = getUniformLocation("view");
        setUniform(uniView, view);


        Matrix4f projection = new Matrix4f();
        projection = projection.perspective((float)Math.toRadians(60), 16.0f/9.0f, 0.01f,1000f); //TODO: Controlled by Camera
        int uniProjection = getUniformLocation("projection");
        setUniform(uniProjection, projection);
    }

    @Override
    public void deleteShader() {

        GL20.glDeleteShader(vertexShaderId);
        vertexShaderId = -1;
        GL20.glDeleteShader(fragmentShaderId);
        fragmentShaderId = -1;
        GL20.glDeleteProgram(shaderId);
        shaderId = -1;
    }

    @Override
    public void linkShader() {
        GL20.glLinkProgram(shaderId);
    }

    @Override
    public void useShader() {
        GL20.glUseProgram(shaderId);
    }

    @Override
    public int getUniformLocation(String name) {
        return GL20.glGetUniformLocation(shaderId, name);
    }

    @Override
    public int getAttributeLocation(String name){
        return GL20.glGetAttribLocation(shaderId, name);
    }

    @Override
    public void enableVertexAttrib(int location){
        GL20.glEnableVertexAttribArray(location);
    }

    @Override
    public void pointVertexAttribute(int location, int size, int stride, int offset) {
        GL20.glVertexAttribPointer(location, size, GL11.GL_FLOAT, false, stride, offset);
    }

    @Override
    public void setUniform(int location, Matrix4f value) {
        try (MemoryStack stack = MemoryStack.stackPush()) {
            FloatBuffer buffer = stack.mallocFloat(4 * 4);
            value.get(buffer);
            GL20.glUniformMatrix4fv(location, false, buffer);
        }
    }

    @Override
    public void setUniform(String location, Matrix4f value) {
        setUniform(getUniformLocation(location), value);
    }
}
