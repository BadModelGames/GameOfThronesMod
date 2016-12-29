package me.superhb.got.util;

import me.superhb.got.blocks.GoTBlocks;
import net.minecraft.block.Block;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.DimensionType;
import net.minecraft.world.World;
import net.minecraft.world.chunk.IChunkGenerator;
import net.minecraft.world.chunk.IChunkProvider;
import net.minecraft.world.gen.feature.WorldGenMinable;
import net.minecraftforge.fml.common.IWorldGenerator;

import java.util.Random;

public class GenerationHandler implements IWorldGenerator {
    @Override
    public void generate (Random random, int chunkX, int chunkZ, World world, IChunkGenerator chunkGenerator, IChunkProvider chunkProvider) {
        if (world.provider.getDimensionType() == DimensionType.OVERWORLD) {
            generateOre(world, random, chunkX, chunkZ, GoTBlocks.valyrianOre, 10, 3);
        }
    }

    private void generateOre (World world, Random rand, int chunkX, int chunkZ, Block ore, int layer, int size) {
        for (int i = 0; i < 16; i++) {
            int blockX = chunkX + rand.nextInt(16);
            int blockZ = chunkZ + rand.nextInt(16);
            int valyrianY = rand.nextInt(layer); // Found between 0-10
            BlockPos valyrianPos = new BlockPos(blockX, valyrianY, blockZ);

            (new WorldGenMinable(ore.getDefaultState(), size)).generate(world, rand, valyrianPos);
        }
    }
}
