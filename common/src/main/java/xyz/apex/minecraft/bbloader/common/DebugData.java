package xyz.apex.minecraft.bbloader.common;

import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.HorizontalDirectionalBlock;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.Validate;
import org.jetbrains.annotations.ApiStatus;

import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.function.Supplier;
import java.util.stream.Stream;

@ApiStatus.Internal
public final class DebugData
{
    private static final String DEBUG_DATA_JVM_PROPERTY_NAME = "apex.%s.test_data.enabled".formatted(BBLoader.ID);
    public static final boolean IS_DEBUG_DATA_ENABLED = Boolean.parseBoolean(System.getProperty(DEBUG_DATA_JVM_PROPERTY_NAME, "false"));
    public static final ResourceLocation PLUSH_BLOCK_NAME = new ResourceLocation(BBLoader.ID, "plush");
    public static final ResourceLocation MUSHROOM_BLOCK_NAME = new ResourceLocation(BBLoader.ID, "mushroom");

    public static void register(BiFunction<ResourceLocation, Supplier<Block>, Supplier<Block>> blockRegistrar, BiConsumer<ResourceLocation, Supplier<Block>> itemRegistrar)
    {
        if(!IS_DEBUG_DATA_ENABLED) return;

        var lines = new String[] {
                "Debug Data Enabled!!",
                "",
                "No support will be provided while debug data is enabled.",
                "Please try to reproduce any issues with debug data disabled!",
                "",
                "To disable debug data remove or set the following JVM property to false",
                DEBUG_DATA_JVM_PROPERTY_NAME,
                "",
                "Registering the following Blocks & Items: [ '%s', '%s' ]".formatted(PLUSH_BLOCK_NAME, MUSHROOM_BLOCK_NAME)
        };

        var max = Stream.of(lines).mapToInt(String::length).max().orElse(0) + 4;
        var header = "*".repeat(max);
        BBLoader.LOGGER.warn(header);
        Stream.of(lines).map("* %s"::formatted).map(s -> StringUtils.rightPad(s, max - 2)).map("%s *"::formatted).forEach(BBLoader.LOGGER::warn);
        BBLoader.LOGGER.warn(header);

        var plushBlock = blockRegistrar.apply(PLUSH_BLOCK_NAME, () -> new DebugTestBlock(BlockBehaviour.Properties.copy(Blocks.WHITE_WOOL).noOcclusion()));
        var mushroomBlock = blockRegistrar.apply(MUSHROOM_BLOCK_NAME, () -> new DebugTestBlock(BlockBehaviour.Properties.copy(Blocks.WHITE_WOOL).noOcclusion()));
        itemRegistrar.accept(PLUSH_BLOCK_NAME, plushBlock);
        itemRegistrar.accept(MUSHROOM_BLOCK_NAME, mushroomBlock);
    }

    private static final class DebugTestBlock extends HorizontalDirectionalBlock
    {
        private DebugTestBlock(Properties properties)
        {
            super(properties);
            Validate.isTrue(IS_DEBUG_DATA_ENABLED);
            registerDefaultState(defaultBlockState().setValue(FACING, Direction.NORTH));
        }

        @Override
        protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder)
        {
            super.createBlockStateDefinition(builder.add(FACING));
        }

        @Override
        public BlockState getStateForPlacement(BlockPlaceContext context)
        {
            return defaultBlockState().setValue(FACING, context.getHorizontalDirection().getOpposite());
        }
    }
}
