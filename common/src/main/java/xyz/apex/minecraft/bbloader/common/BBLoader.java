package xyz.apex.minecraft.bbloader.common;

import com.mojang.datafixers.util.Pair;
import net.minecraft.core.Direction;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.HorizontalDirectionalBlock;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.Validate;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Optional;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Stream;

public interface BBLoader
{
    String ID = "bbloader";
    Logger LOGGER = LogManager.getLogger();

    String DEBUG_DATA_JVM_PROPERTY_NAME = "apex.%s.test_data.enabled".formatted(ID);
    boolean IS_DEBUG_DATA_ENABLED = Boolean.parseBoolean(System.getProperty(DEBUG_DATA_JVM_PROPERTY_NAME, "false"));

    static Optional<Pair<Supplier<Block>, Function<Block, Item>>> bootstrap()
    {
        if(!IS_DEBUG_DATA_ENABLED) return Optional.empty();

        var lines = new String[] {
                "Debug Data Enabled!!",
                "",
                "No support will be provided while debug data is enabled.",
                "Please try to reproduce any issues with debug data disabled!",
                "",
                "To disable debug data remove or set the following JVM property to false",
                DEBUG_DATA_JVM_PROPERTY_NAME
        };

        var max = Stream.of(lines).mapToInt(String::length).max().orElse(0) + 4;
        var header = "*".repeat(max);
        LOGGER.warn(header);
        Stream.of(lines).map("* %s"::formatted).map(s -> StringUtils.rightPad(s, max - 2)).map("%s *"::formatted).forEach(LOGGER::warn);
        LOGGER.warn(header);

        return Optional.of(Pair.of(
                () -> new DebugTestBlock(BlockBehaviour.Properties.copy(Blocks.WHITE_WOOL).noOcclusion()),
                block -> new BlockItem(block, new Item.Properties())
        ));
    }

    final class DebugTestBlock extends HorizontalDirectionalBlock
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
