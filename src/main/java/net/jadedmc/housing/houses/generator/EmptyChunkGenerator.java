package net.jadedmc.housing.houses.generator;

import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.generator.ChunkGenerator;
import org.jetbrains.annotations.NotNull;

import java.util.Random;

public class EmptyChunkGenerator extends ChunkGenerator {
    private final String spawn;

    public EmptyChunkGenerator() {
        spawn = "";
    }

    public EmptyChunkGenerator(String spawn) {
        this.spawn = spawn;
    }

    @Override
    public boolean shouldGenerateNoise() {
        return false;
    }

    @Override
    public boolean shouldGenerateSurface() {
        return false;
    }

    @Override
    public boolean shouldGenerateBedrock() {
        return false;
    }

    @Override
    public boolean shouldGenerateCaves() {
        return false;
    }

    @Override
    public boolean shouldGenerateDecorations() {
        return false;
    }

    @Override
    public boolean shouldGenerateMobs() {
        return false;
    }

    @Override
    public boolean shouldGenerateStructures() {
        return false;
    }

    @Override
    public Location getFixedSpawnLocation(@NotNull World world, @NotNull Random random) {
        if(spawn.isEmpty()) {
            return new Location(world, 256, 100, 256);
        }

        String[] location = spawn.split(",");
        double x = Double.parseDouble(location[0]);
        double y = Double.parseDouble(location[1]);
        double z = Double.parseDouble(location[2]);
        float yaw = (float) Double.parseDouble(location[3]);
        float pitch = (float) Double.parseDouble(location[4]);

        return new Location(world, x, y, z, yaw, pitch);
    }
}