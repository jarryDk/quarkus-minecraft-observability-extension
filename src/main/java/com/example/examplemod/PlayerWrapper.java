package com.example.examplemod;

import net.minecraft.network.chat.TextComponent;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.animal.Chicken;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;

/**
 * We need a wrapper here because we need something with a simple enough
 * signature that everything in it is in a JVM library, rather than loaded
 * by one of the fragmented classloaders. That allows us to find and
 * invoke the method by reflection.
 */
public class PlayerWrapper {
    private final Player player;

    PlayerWrapper(Player player) {
        this.player = player;
    }

    public void say(String message) {
        player.displayClientMessage(new TextComponent(message), true);
        Level level = player.getCommandSenderWorld();
        Chicken chicken = EntityType.CHICKEN.create(level);
        chicken.setPos(player.getX(), player.getY(), player.getZ());
        level.addFreshEntity(chicken);
    }
}
