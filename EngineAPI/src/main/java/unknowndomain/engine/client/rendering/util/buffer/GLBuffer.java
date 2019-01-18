package unknowndomain.engine.client.rendering.util.buffer;

import org.joml.Vector3fc;
import org.lwjgl.BufferUtils;
import unknowndomain.engine.util.Color;
import unknowndomain.engine.util.Math2;

import java.nio.ByteBuffer;

public class GLBuffer {

    private ByteBuffer backingBuffer;

    public GLBuffer(int size) {
        backingBuffer = BufferUtils.createByteBuffer(size);
    }

    private float posOffsetX;
    private float posOffsetY;
    private float posOffsetZ;

    private boolean isDrawing;
    private int drawMode;
    private GLBufferFormat format;

    public boolean isDrawing() {
        return isDrawing;
    }

    public int getDrawMode() {
        return drawMode;
    }

    public GLBufferFormat getFormat() {
        return format;
    }

    public void begin(int mode, GLBufferFormat format) {
        if (isDrawing) {
            throw new IllegalStateException("Already drawing!");
        } else {
            isDrawing = true;
            reset();
            drawMode = mode;
            this.format = format;
            backingBuffer.limit(backingBuffer.capacity());
        }
    }

    public void finish() {
        if (!isDrawing) {
            throw new IllegalStateException("Not yet drawn!");
        } else {
            isDrawing = false;
            backingBuffer.position(0);
            backingBuffer.limit(vertexCount * format.getStride());
        }
    }

    public void reset() {
        drawMode = 0;
        format = null;
        vertexCount = 0;
        posOffset(0, 0, 0);
    }

    public void grow(int needLength) {
        if (needLength > backingBuffer.remaining()) {
            int oldSize = this.backingBuffer.capacity();
            int newSize = oldSize + Math2.roundUp(needLength, 0x200000);
            int oldPosition = backingBuffer.position();
            ByteBuffer newBuffer = BufferUtils.createByteBuffer(newSize);
            this.backingBuffer.position(0);
            newBuffer.put(this.backingBuffer);
            newBuffer.rewind();
            this.backingBuffer = newBuffer;
            newBuffer.position(oldPosition);
        }
    }

    private int vertexCount;

    public int getVertexCount() {
        return vertexCount;
    }

    public ByteBuffer getBackingBuffer() {
        return backingBuffer;
    }

    public GLBuffer put(int[] ints) {
        int bits = ints.length * Integer.BYTES;
        if (bits % format.getStride() != 0) {
            throw new IllegalArgumentException();
        }
        for (int i = 0; i < ints.length; i++) {
            backingBuffer.putFloat(ints[i]);
        }
        vertexCount += bits / format.getStride();
        return this;
    }

    public GLBuffer put(float[] floats) {
        int bits = floats.length * Float.BYTES;
        if (bits % format.getStride() != 0) {
            throw new IllegalArgumentException();
        }
        for (int i = 0; i < floats.length; i++) {
            backingBuffer.putFloat(floats[i]);
        }
        vertexCount += bits / format.getStride();
        return this;
    }

    public GLBuffer pos(float x, float y, float z) {
        if (format.isUsingPosition()) {
            backingBuffer.putFloat(x + posOffsetX);
            backingBuffer.putFloat(y + posOffsetY);
            backingBuffer.putFloat(z + posOffsetZ);
        }
        return this;
    }

    public GLBuffer posOffset(float x, float y, float z) {
        posOffsetX = x;
        posOffsetY = y;
        posOffsetZ = z;
        return this;
    }

    public GLBuffer color(Color color) {
        if (format.isUsingColor()) {
            color(color.getRed(), color.getGreen(), color.getBlue(), color.getAlpha());
        }
        return this;
    }

    public GLBuffer color(int color) {
        if (format.isUsingColor()) {
            color(((color >> 16) & 255) / 255f, ((color >> 8) & 255) / 255f, (color & 255) / 255f, ((color >> 24) & 255) / 255f);
        }
        return this;
    }

    public GLBuffer color(float r, float g, float b) {
        return color(r, g, b, 1);
    }

    public GLBuffer color(float r, float g, float b, float a) {
        if (format.isUsingColor()) {
            backingBuffer.putFloat(r);
            backingBuffer.putFloat(g);
            backingBuffer.putFloat(b);
            backingBuffer.putFloat(a);
        }
        return this;
    }

    public GLBuffer uv(float u, float v) {
        if (format.isUsingTextureUV()) {
            backingBuffer.putFloat(u);
            backingBuffer.putFloat(v);
        }
        return this;
    }

    public GLBuffer normal(Vector3fc vec) {
        return normal(vec.x(), vec.y(), vec.z());
    }

    public GLBuffer normal(float nx, float ny, float nz) {
        if (format.isUsingNormal()) {
            backingBuffer.putFloat(nx);
            backingBuffer.putFloat(ny);
            backingBuffer.putFloat(nz);
        }
        return this;
    }

    public void endVertex() {
        ++vertexCount;
        grow(format.getStride());
    }
}