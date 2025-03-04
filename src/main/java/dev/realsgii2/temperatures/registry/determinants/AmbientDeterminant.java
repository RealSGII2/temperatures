package dev.realsgii2.temperatures.registry.determinants;

import dev.realsgii2.temperatures.Config;
import dev.realsgii2.temperatures.Util;
import dev.realsgii2.temperatures.api.registry.determinant.IDeterminant;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;

import java.util.Objects;
import java.util.Optional;

/**
 * Offsets the temperature by the warmth or cold emitted by surrounding blocks.
 */
public class AmbientDeterminant implements IDeterminant.INameableDeterminant {
    @Override
    public String getName() {
        return "ambient";
    }

    @Override
    public double getTemperature(Player player, double result) {
        double resultOffset = 0.0;

        for (BlockPos blockPos : Util.World.getNearbyPositionsWithY(player.blockPosition(), (int) Math.pow(16, 2), 1)) {
            BlockState state = player.level().getBlockState(blockPos);

            Optional<Registry<Block>> possibleBlockRegistry = player.level().registryAccess().registry(Registries.BLOCK);
            if (possibleBlockRegistry.isEmpty()) continue;

            String blockId = Objects.requireNonNull(possibleBlockRegistry.get().getKey(state.getBlock())).toString();

            double magnitude = Math.abs(player.blockPosition().distSqr(blockPos));
            if (Config.Common.isWarmBlock(blockId)) {
                double thisOffset = Math.max(0, 1 - magnitude / 16) * Config.Common.getWarmth(blockId);

                // If the numbers have the same sign, get the highest value
                if (Math.abs(thisOffset) > Math.abs(resultOffset)) resultOffset = thisOffset;

                // If the signs are different, get the sum
                if (Math.signum(thisOffset) != Math.signum(resultOffset)) resultOffset += thisOffset;
            }
        }

        return result + resultOffset;
    }

    @Override
    public int overridePriority(Player player, double oldResult, double newResult) {
        return NO_OVERRIDE;
    }

    @Override
    public int order() {
        return NO_ORDER;
    }
}
