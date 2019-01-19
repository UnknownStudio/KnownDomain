package unknowndomain.engine.client.rendering.block.model;

import unknowndomain.engine.client.block.ClientBlock;
import unknowndomain.engine.client.rendering.block.BlockMeshGenerator;
import unknowndomain.engine.client.rendering.util.BufferBuilder;
import unknowndomain.engine.math.BlockPos;
import unknowndomain.engine.util.Facing;
import unknowndomain.engine.world.BlockAccessor;

import java.util.List;

public class BakedBlockModel implements BlockMeshGenerator {

    private final List<int[]> meshes;

    public BakedBlockModel(List<int[]> meshes) {
        if (meshes.size() != 7) {
            throw new IllegalArgumentException();
        }
        this.meshes = meshes;
    }

    @Override
    public void generate(ClientBlock block, BlockAccessor world, BlockPos pos, BufferBuilder buffer) {
        buffer.posOffset(pos.getX(), pos.getY(), pos.getZ());
        BlockPos.Mutable mutablePos = new BlockPos.Mutable(pos);
        for (Facing facing : Facing.values()) {
            mutablePos.set(pos);
            if (!block.canRenderFace(world, mutablePos, facing)) {
                continue;
            }

            buffer.put(meshes.get(facing.getIndex()));
        }

        buffer.put(meshes.get(6));
    }

    @Override
    public void generate(ClientBlock block, BufferBuilder buffer) {
        for (int[] mesh : meshes) {
            buffer.put(mesh);
        }
    }

    @Override
    public void dispose() {

    }
}
