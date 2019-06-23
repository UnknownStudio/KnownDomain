package unknowndomain.engine.world.collision;

import com.google.common.collect.Sets;
import org.joml.AABBd;
import org.joml.Vector2d;
import org.joml.Vector3f;
import org.joml.Vector3fc;
import unknowndomain.engine.block.Block;
import unknowndomain.engine.math.BlockPos;
import unknowndomain.engine.registry.Registries;
import unknowndomain.engine.util.Facing;
import unknowndomain.engine.world.World;
import unknowndomain.engine.world.util.FastVoxelRayTrace;

import javax.annotation.Nonnull;
import java.util.Comparator;
import java.util.Objects;
import java.util.Set;

public class WorldCollisionManagerImpl implements WorldCollisionManager {

    public static final float CALC_ERROR_FIXING = 1e-6f;

    private final World world;

    public WorldCollisionManagerImpl(@Nonnull World world) {
        this.world = Objects.requireNonNull(world);
    }

    @Nonnull
    @Override
    public World getWorld() {
        return world;
    }

    @Nonnull
    @Override
    public RayTraceBlockHit raycastBlock(Vector3fc from, Vector3fc dir, float distance) {
        return raycastBlock(from, dir, distance, Sets.newHashSet(Registries.getBlockRegistry().air()));
    }

    @Nonnull
    @Override
    public RayTraceBlockHit raycastBlock(Vector3fc from, Vector3fc dir, float distance, Set<Block> ignore) {
        Vector3f rayOffset = dir.normalize(new Vector3f()).mul(distance);
        Vector3f dist = rayOffset.add(from, new Vector3f());

        var all = FastVoxelRayTrace.rayTrace(from, dist);

        all.sort(Comparator.comparingDouble(pos -> from.distanceSquared(pos.getX(), pos.getY(), pos.getZ())));

        for (BlockPos pos : all) {
            Block block = world.getBlock(pos);
            if (ignore.contains(block))
                continue;
            Vector3f local = from.sub(pos.getX(), pos.getY(), pos.getZ(), new Vector3f());
            AABBd[] boxes = block.getBoundingBoxes();
            Vector2d result = new Vector2d();
            for (AABBd box : boxes) {
                boolean hit = box.intersectRay(local.x, local.y, local.z, rayOffset.x, rayOffset.y, rayOffset.z,
                        result);
                if (hit) {
                    Vector3f hitPoint = local.add(rayOffset.mul((float) result.x, new Vector3f()));
                    Facing facing = null;
                    if (hitPoint.x <= 0f + CALC_ERROR_FIXING) {
                        facing = Facing.WEST;
                    } else if (hitPoint.x >= 1f - CALC_ERROR_FIXING) {
                        facing = Facing.EAST;
                    } else if (hitPoint.y <= 0f + CALC_ERROR_FIXING) {
                        facing = Facing.DOWN;
                    } else if (hitPoint.y >= 1f - CALC_ERROR_FIXING) {
                        facing = Facing.UP;
                    } else if (hitPoint.z <= 0f + CALC_ERROR_FIXING) {
                        facing = Facing.SOUTH;
                    } else if (hitPoint.z >= 1f - CALC_ERROR_FIXING) {
                        facing = Facing.NORTH;
                    }
                    if (facing != null) {
                        return new RayTraceBlockHit(world, pos, block, hitPoint, facing);
                    }
                }
            }
        }
        return RayTraceBlockHit.failure();
    }
}