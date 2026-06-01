package com.creativespawners.creativespawners.block;

import com.creativespawners.creativespawners.block.entity.CreativeSpawnerBlockEntity;
import com.creativespawners.creativespawners.registry.ModBlockEntities;
import com.mojang.serialization.MapCodec;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.phys.BlockHitResult;
import org.jetbrains.annotations.Nullable;

public class CreativeSpawnerBlock extends BaseEntityBlock {
    public static final MapCodec<CreativeSpawnerBlock> CODEC = simpleCodec(CreativeSpawnerBlock::new);
    public static final BooleanProperty UPGRADED = BooleanProperty.create("upgraded");

    public CreativeSpawnerBlock(BlockBehaviour.Properties properties) {
        super(properties);
        this.registerDefaultState(this.stateDefinition.any().setValue(UPGRADED, false));
    }

    @Override
    protected MapCodec<? extends BaseEntityBlock> codec() {
        return CODEC;
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(UPGRADED);
    }

    @Override
    public @Nullable BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new CreativeSpawnerBlockEntity(pos, state);
    }

    @Override
    public @Nullable <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, BlockState state, BlockEntityType<T> type) {
        if (level.isClientSide()) return null;
        return createTickerHelper(type, ModBlockEntities.CREATIVE_SPAWNER.get(), CreativeSpawnerBlockEntity::serverTick);
    }

    @Override
    protected RenderShape getRenderShape(BlockState state) {
        return RenderShape.MODEL;
    }

    @Override
    protected InteractionResult useItemOn(ItemStack stack, BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hit) {
        if (stack.is(Items.DIAMOND) && !state.getValue(UPGRADED)) {
            if (!level.isClientSide()) {
                level.setBlock(pos, state.setValue(UPGRADED, true), 3);
                if (!player.getAbilities().instabuild) {
                    stack.shrink(1);
                }
                level.playSound(null, pos, SoundEvents.ANVIL_USE, SoundSource.BLOCKS, 0.6F, 1.2F);
                ServerLevel serverLevel = (ServerLevel) level;
                RandomSource rand = level.getRandom();
                for (int i = 0; i < 20; i++) {
                    double ox = pos.getX() + 0.5 + (rand.nextDouble() - 0.5) * 0.8;
                    double oy = pos.getY() + 0.5 + (rand.nextDouble() - 0.5) * 0.8;
                    double oz = pos.getZ() + 0.5 + (rand.nextDouble() - 0.5) * 0.8;
                    serverLevel.sendParticles(ParticleTypes.ENCHANT, ox, oy, oz, 1, 0, 0.1, 0, 0.05);
                }
            }
            return InteractionResult.SUCCESS_SERVER;
        }
        return InteractionResult.PASS;
    }

    @Override
    public void animateTick(BlockState state, Level level, BlockPos pos, RandomSource random) {
        double x = pos.getX() + 0.5 + (random.nextDouble() - 0.5) * 0.7;
        double y = pos.getY() + 0.6;
        double z = pos.getZ() + 0.5 + (random.nextDouble() - 0.5) * 0.7;
        level.addParticle(ParticleTypes.END_ROD, x, y, z, 0, 0.03, 0);
        if (state.getValue(UPGRADED)) {
            double ex = pos.getX() + 0.5 + (random.nextDouble() - 0.5) * 0.6;
            double ey = pos.getY() + 0.7;
            double ez = pos.getZ() + 0.5 + (random.nextDouble() - 0.5) * 0.6;
            level.addParticle(ParticleTypes.ENCHANT, ex, ey, ez, 0, 0.05, 0);
        }
    }
}
