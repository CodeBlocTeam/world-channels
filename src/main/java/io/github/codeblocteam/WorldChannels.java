package io.github.codeblocteam;

import org.slf4j.Logger;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.entity.MoveEntityEvent;
import org.spongepowered.api.event.game.state.GameInitializationEvent;
import org.spongepowered.api.event.network.ClientConnectionEvent;
import org.spongepowered.api.event.world.LoadWorldEvent;
import org.spongepowered.api.event.world.UnloadWorldEvent;
import org.spongepowered.api.plugin.Plugin;
import org.spongepowered.api.text.channel.MessageChannel;
import org.spongepowered.api.world.World;

import javax.inject.Inject;
import java.util.HashMap;
import java.util.Map;

/*
TODO: Ajouter un fichier de config où on peut modifier le format des messages
TODO: Faire en sorte qu'on puisse modifier le format par monde
TODO: Pouvoir grouper des mondes qui partagent le même channel (overworld, nether, end)
TODO: Ajouter une commande pour envoyer un message à tout le serveur
TODO: Ajouter une commande pour envoyer un message à des mondes spécifiques
TODO: Ajouter un callback au /reload
TODO: Créer un service pour gérer les channels
 */

@Plugin(
        id = "world-channels",
        name = "World Channels",
        version = "0.0.1"
)
public class WorldChannels {

    /** Keeping track of all the channels created. */
    private Map<String, MessageChannel> channels;

    /** A SFL4J logger. */
    @Inject
    private Logger logger;

    /**
     * Create a channel for the given world.
     * @param world
     */
    private void loadWorldChannel(World world) {
        String name = world.getName();
        MessageChannel channel = MessageChannel.world(world);

        logger.info("Loading world channel for " + name);
        channels.putIfAbsent(name, channel);
    }

    /**
     * Delete the channel associated with the given world.
     * @param world
     */
    private void unloadWorldChannel(World world) {
        logger.info("Unloading world channel of " + world.getName());
        channels.remove(world.getName());
    }

    /**
     * Change the channel the player is talking into.
     *
     * @param player The player whom channel will change.
     * @param world The world associated with the channel the player
     *              will change to.'
     */
    private void updatePlayerChannel(Player player, World world) {
        MessageChannel channel = channels.get(world.getName());

        logger.info("Changing " + player.getName() + "'s channel to " + world.getName());
        player.setMessageChannel(channel);
    }

    @Listener
    public void onServerStart(GameInitializationEvent event) {
        channels = new HashMap<>();
    }

    @Listener
    public void onLoadWorld(LoadWorldEvent event) {
        loadWorldChannel(event.getTargetWorld());
    }

    @Listener
    public void onUnloadWorld(UnloadWorldEvent event) {
        unloadWorldChannel(event.getTargetWorld());
    }

    @Listener
    public void onChangeWorld(MoveEntityEvent.Teleport event) {
        if (event.getTargetEntity() instanceof Player) {
            updatePlayerChannel((Player) event.getTargetEntity(), event.getToTransform().getExtent());
        }
    }

    @Listener
    public void onPlayerJoin(ClientConnectionEvent.Join event) {
        updatePlayerChannel(event.getTargetEntity(), event.getTargetEntity().getWorld());
    }
}
