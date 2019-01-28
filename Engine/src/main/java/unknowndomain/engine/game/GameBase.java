package unknowndomain.engine.game;

import unknowndomain.engine.Engine;
import unknowndomain.engine.component.Component;
import unknowndomain.engine.event.game.GameReadyEvent;
import unknowndomain.game.Blocks;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public abstract class GameBase implements Game {
    protected final Engine engine;

    protected GameContext context;

    public GameBase(Engine engine) {
        this.engine = engine;
        // this.option.getMods().add(this.meta);
    }

    /**
     * Construct stage, collect mod and resource according to it option
     */
    protected void constructStage() {
        // Mod construction moved to Engine
    }

    /**
     * Register stage, collect all registerable things from mod here.
     */
    protected void registerStage() {
        // Registry Moved to Engine
//        Map<Class<?>, Registry<?>> maps = Maps.newHashMap();
//        List<SimpleRegistry<?>> registries = Lists.newArrayList();
//        for (Registry.Type<?> tp : Arrays.asList(Registry.Type.of("block", Block.class), Registry.Type.of("item", Item.class), Registry.Type.of("entity", EntityType.class))) {
//            SimpleRegistry<?> registry = new SimpleRegistry<>(tp.type, tp.name);
//            maps.put(tp.type, registry);
//            registries.add(registry);
//        }
//        SimpleRegistryManager manager = new SimpleRegistryManager(maps);
//        eventBus.post(new RegisterEvent(manager));

        // GamePreInitializationEvent event = new GamePreInitializationEvent();
        // engine.getEventBus().post(event);

        // Hardcode set air for now
        this.context = new GameContext(engine.getRegistryManager(), engine.getEventBus(), Blocks.AIR);
    }

    /**
     * let mod and resource related module load resources.
     */
    protected void resourceStage() {

    }

    /**
     * final stage of the
     */
    protected void finishStage() {
        spawnWorld(null);
        engine.getEventBus().post(new GameReadyEvent(context));
    }

    @Override
    public GameContext getContext() {
        return context;
    }

    @Override
    public boolean isTerminated() {
        return false;
    }

    @Override
    public void run() {
        constructStage();
        registerStage();
        resourceStage();
        finishStage();

        // TODO: loop to check if we need to gc the world

        // for (WorldCommon worldCommon : internalWorlds) {
        // worldCommon.stop();
        // }
    }

    public void terminate() {
        // TODO: unload mod/resource here
    }

    @Nullable
    @Override
    public <T extends Component> T getComponent(@Nonnull Class<T> type) {
        return null;
    }

    @Override
    public <T extends Component> boolean hasComponent(@Nonnull Class<T> type) {
        return false;
    }
}
