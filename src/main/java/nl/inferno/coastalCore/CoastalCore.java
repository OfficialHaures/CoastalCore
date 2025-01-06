package nl.inferno.coastalCore;

import org.bukkit.plugin.java.JavaPlugin;
import nl.inferno.coastalCore.commands.*;
import nl.inferno.coastalCore.listeners.*;
import nl.inferno.coastalCore.rewards.RewardManager;

public final class CoastalCore extends JavaPlugin {
    private RewardManager rewardManager;

    @Override
    public void onEnable() {
        // Initialize managers
        rewardManager = new RewardManager(this);

        // Register commands
        getCommand("smeltwand").setExecutor(new SmeltWandCommand());
        getCommand("convert").setExecutor(new ConvertCommand(true));
        getCommand("unconvert").setExecutor(new ConvertCommand(false));
        getCommand("tempfly").setExecutor(new TempFlyCommand(this));

        // Register listeners
        getServer().getPluginManager().registerEvents(new SmeltWandListener(), this);
        getServer().getPluginManager().registerEvents(new TempFlyListener(this), this);
        getServer().getPluginManager().registerEvents(new ShoulderRideListener(), this);
        getServer().getPluginManager().registerEvents(new DualHorseListener(), this);
        getServer().getPluginManager().registerEvents(new BetterSleepListener(), this);
    }

    @Override
    public void onDisable() {
        // Save any necessary data
    }

    public RewardManager getRewardManager() {
        return rewardManager;
    }
}
