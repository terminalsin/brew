package org.imanity.brew.scene.impl;

import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.WorldCreator;
import org.bukkit.WorldType;
import org.bukkit.entity.Player;
import org.fairy.bukkit.util.CustomLocation;
import org.imanity.brew.game.Game;
import org.imanity.brew.scene.SceneBase;
import org.imanity.brew.util.EmptyChunkGenerator;

import java.io.Serializable;
import java.util.function.Function;

@Getter
public class WorldScene extends SceneBase implements Serializable {

    private final String worldName;
    private final CustomLocation spawnLocation;
    private final Function<WorldCreator, WorldCreator> worldConfigurer;
    private final boolean save;

    public WorldScene(String worldName, CustomLocation spawnLocation, Function<WorldCreator, WorldCreator> worldConfigurer, boolean save) {
        this.worldName = worldName;
        this.spawnLocation = spawnLocation;
        this.worldConfigurer = worldConfigurer;
        this.save = save;
    }

    public WorldScene(String worldName, CustomLocation spawnLocation) {
        this(worldName, spawnLocation, worldCreator -> worldCreator
                .environment(World.Environment.NORMAL)
                .type(WorldType.FLAT)
                .generator(new EmptyChunkGenerator()),
                false);
    }

    @Override
    public void init(Game game) {
        if (Bukkit.getWorld(this.worldName) != null) {
            return;
        }

        final World world = this.worldConfigurer
                .apply(new WorldCreator(this.worldName))
                .createWorld();

        // configurable?
        world.setAutoSave(false);
    }

    @Override
    public void teleport(Player player, Game game) {
        this.spawnLocation.teleport(player, 3f, true);
    }

    @Override
    public void close() {
        final World world = Bukkit.getWorld(this.worldName);
        if (world == null) {
            return;
        }

        Bukkit.unloadWorld(world, this.save);
    }

}