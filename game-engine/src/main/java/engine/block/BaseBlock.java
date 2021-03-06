package engine.block;

import engine.block.state.BlockState;
import engine.component.Component;
import engine.component.ComponentAgent;
import engine.registry.Registrable;
import engine.state.StateManager;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;

public class BaseBlock extends Registrable.Impl<Block> implements Block {

    private final ComponentAgent components = new ComponentAgent();
    private final StateManager<Block, BlockState> stateManager;

    private BlockShape shape = BlockShape.NORMAL_CUBE;
    private BlockState defaultState;

    public BaseBlock() {
        var builder = new StateManager.Builder<Block, BlockState>(this);
        initStateProperties(builder);
        this.stateManager = builder.build(BlockState::new);
        defaultState = stateManager.getDefaultState();
    }

    protected void initStateProperties(StateManager.Builder<Block, BlockState> builder) {

    }

    @Override
    public StateManager<Block, BlockState> getStateManager() {
        return stateManager;
    }

    @Override
    public BlockState getDefaultState() {
        return defaultState;
    }

    @Nonnull
    @Override
    public BlockShape getShape() {
        return shape;
    }

    @Nonnull
    public BaseBlock setShape(@Nonnull BlockShape shape) {
        this.shape = Objects.requireNonNull(shape);
        return this;
    }

    @Override
    public <C extends Component> Optional<C> getComponent(@Nonnull Class<C> type) {
        return components.getComponent(type);
    }

    @Override
    public <C extends Component> boolean hasComponent(@Nonnull Class<C> type) {
        return components.hasComponent(type);
    }

    @Override
    public <C extends Component> Block setComponent(@Nonnull Class<C> type, @Nullable C value) {
        components.setComponent(type, value);
        return this;
    }

    @Override
    public <C extends Component> Block removeComponent(@Nonnull Class<C> type) {
        components.removeComponent(type);
        return this;
    }

    @Override
    @Nonnull
    public Set<Class<?>> getComponents() {
        return components.getComponents();
    }
}
