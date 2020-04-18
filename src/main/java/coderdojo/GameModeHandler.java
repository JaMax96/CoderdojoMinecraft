package coderdojo;

import com.sk89q.worldedit.util.Location;
import com.sk89q.worldguard.LocalPlayer;
import com.sk89q.worldguard.protection.ApplicableRegionSet;
import com.sk89q.worldguard.protection.flags.Flags;
import com.sk89q.worldguard.protection.flags.StateFlag;
import com.sk89q.worldguard.session.MoveType;
import com.sk89q.worldguard.session.Session;
import com.sk89q.worldguard.session.handler.FlagValueChangeHandler;
import com.sk89q.worldguard.session.handler.Handler;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.entity.Player;

public class GameModeHandler extends FlagValueChangeHandler<StateFlag.State> {
    public static final Factory FACTORY = new Factory();

    public static class Factory extends Handler.Factory<GameModeHandler> {
        @Override
        public GameModeHandler create(Session session) {
            return new GameModeHandler(session);
        }
    }

    public GameModeHandler(Session session) {
        super(session, Flags.BUILD);
    }

    @Override
    protected void onInitialValue(LocalPlayer player, ApplicableRegionSet set, StateFlag.State value) {
        handleFlagValue(player, StateFlag.State.ALLOW.equals(value));
    }

    @Override
    protected boolean onSetValue(LocalPlayer player, Location from, Location to, ApplicableRegionSet toSet, StateFlag.State currentValue, StateFlag.State lastValue, MoveType moveType) {
        handleFlagValue(player, StateFlag.State.ALLOW.equals(currentValue));
        return true;
    }

    @Override
    protected boolean onAbsentValue(LocalPlayer player, Location from, Location to, ApplicableRegionSet toSet, StateFlag.State lastValue, MoveType moveType) {
        handleFlagValue(player, false);
        return true;
    }

    private void handleFlagValue(LocalPlayer localPlayer, boolean allowed) {
        Player player = Bukkit.getPlayer(localPlayer.getUniqueId());
        if (!player.isOp()) {
            if (allowed) {
                player.setGameMode(GameMode.CREATIVE);
            } else {
                player.setGameMode(GameMode.SPECTATOR);
            }
        }
    }
}