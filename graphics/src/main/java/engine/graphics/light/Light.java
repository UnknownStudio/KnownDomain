package engine.graphics.light;

import engine.graphics.shader.ShaderResource;
import org.joml.Vector3f;

public abstract class Light {
    protected Vector3f ambient = new Vector3f(0.1f);
    protected Vector3f diffuse = new Vector3f(1f);
    protected Vector3f specular = new Vector3f(1f);

    public Light() {
    }

    public abstract void bind(ShaderResource proxy, String fieldName);

    public Vector3f getAmbient() {
        return ambient;
    }

    public Light setAmbient(Vector3f ambient) {
        this.ambient = ambient;
        return this;
    }

    public Vector3f getDiffuse() {
        return diffuse;
    }

    public Light setDiffuse(Vector3f diffuse) {
        this.diffuse = diffuse;
        return this;
    }

    public Vector3f getSpecular() {
        return specular;
    }

    public Light setSpecular(Vector3f specular) {
        this.specular = specular;
        return this;
    }
}
