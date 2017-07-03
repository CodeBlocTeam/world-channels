package io.github.codeblocteam;

import org.slf4j.Logger;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.entity.MoveEntityEvent;
import org.spongepowered.api.event.game.state.GameInitializationEvent;
import org.spongepowered.api.event.message.MessageChannelEvent;
import org.spongepowered.api.event.network.ClientConnectionEvent;
import org.spongepowered.api.event.world.LoadWorldEvent;
import org.spongepowered.api.event.world.UnloadWorldEvent;
import org.spongepowered.api.plugin.Plugin;
import org.spongepowered.api.text.channel.MessageChannel;

import javax.inject.Inject;
import java.util.HashMap;
import java.util.Map;

@Plugin(
        id = "world-channels",
        name = "World Channels",
        version = "0.0.1"
)
public class WorldChannels {

    private Map<String, MessageChannel> channels;

    @Inject
    private Logger logger;

    @Listener
    public void onServerStart(GameInitializationEvent event) {
        channels = new HashMap<>();
    }

    @Listener
    public void onLoadWorld(LoadWorldEvent event) {
        String worldName = event.getTargetWorld().getName();
        MessageChannel channel = MessageChannel.world(event.getTargetWorld());

        channels.put(worldName, channel);
    }

    @Listener
    public void onUnloadWorld(UnloadWorldEvent event) {
        String worldName = event.getTargetWorld().getName();

        channels.remove(worldName);
    }

    @Listener
    public void onChangeWorld(MoveEntityEvent.Teleport event) {
        Player target = (Player) event.getTargetEntity();
        MessageChannel channel = channels.get(target.getWorld().getName());

        target.setMessageChannel(channel);
    }

    @Listener
    public void onPlayerJoin(ClientConnectionEvent.Join event) {
        Player target = event.getTargetEntity();
        MessageChannel channel = channels.get(target.getWorld().getName());

        target.setMessageChannel(channel);
    }
}
