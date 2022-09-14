package coderdojo;

import com.sk89q.worldedit.util.Location;
import com.sk89q.worldguard.LocalPlayer;
import com.sk89q.worldguard.protection.ApplicableRegionSet;
import com.sk89q.worldguard.protection.flags.Flags;
import com.sk89q.worldguard.protection.flags.StateFlag;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import com.sk89q.worldguard.session.MoveType;
import com.sk89q.worldguard.session.Session;
import com.sk89q.worldguard.session.handler.FlagValueChangeHandler;
import com.sk89q.worldguard.session.handler.Handler;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.entity.Player;

public class GameModeFlagValueChangedHandler extends FlagValueChangeHandler<StateFlag.State> {
    public static final Factory FACTORY = new Factory();

    public static class Factory extends Handler.Factory<GameModeFlagValueChangedHandler> {
        @Override
        public GameModeFlagValueChangedHandler create(Session session) {
            return new GameModeFlagValueChangedHandler(session);
        }
    }

    public GameModeFlagValueChangedHandler(Session session) {
        super(session, Flags.BUILD);
    }

    @Override
    protected void onInitialValue(LocalPlayer player, ApplicableRegionSet set, StateFlag.State value) {
        handleFlagValue(player, StateFlag.State.ALLOW.equals(value), set);
    }

    @Override
    protected boolean onSetValue(LocalPlayer player, Location from, Location to, ApplicableRegionSet toSet, StateFlag.State currentValue, StateFlag.State lastValue, MoveType moveType) {
        return handleFlagValue(player, StateFlag.State.ALLOW.equals(currentValue), toSet);
    }

    @Override
    protected boolean onAbsentValue(LocalPlayer player, Location from, Location to, ApplicableRegionSet toSet, StateFlag.State lastValue, MoveType moveType) {
        return handleFlagValue(player, false, toSet);
    }

    private boolean handleFlagValue(LocalPlayer localPlayer, boolean allowed, ApplicableRegionSet toSet) {
        Player player = Bukkit.getPlayer(localPlayer.getUniqueId());
        if (player == null) {
            return false;
        }
        if (!player.isOp()) {
            if (allowed || isLibrary(toSet)) {
                player.setGameMode(GameMode.CREATIVE);
            } else {
                player.setGameMode(GameMode.SPECTATOR);
            }
        }
        return true;
    }

    private boolean isLibrary(ApplicableRegionSet toSet) {
        for(ProtectedRegion region : toSet.getRegions()){
            if(region.getId().startsWith("library")){
                return true;
            }
        }
        return false;
    }
}