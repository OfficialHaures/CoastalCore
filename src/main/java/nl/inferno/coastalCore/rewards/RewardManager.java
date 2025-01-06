package nl.inferno.coastalCore.rewards;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import nl.inferno.coastalCore.CoastalCore;

import java.io.File;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.UUID;

public class RewardManager {
    private final CoastalCore plugin;
    private final File dataFile;
    private FileConfiguration data;
    private final HashMap<UUID, LocalDateTime> dailyRewards = new HashMap<>();
    private final HashMap<UUID, LocalDateTime> weeklyRewards = new HashMap<>();
    private final HashMap<UUID, LocalDateTime> monthlyRewards = new HashMap<>();

    public RewardManager(CoastalCore plugin) {
        this.plugin = plugin;
        this.dataFile = new File(plugin.getDataFolder(), "rewards.yml");
        loadData();
    }

    private void loadData() {
        if (!dataFile.exists()) {
            plugin.saveResource("rewards.yml", false);
        }
        data = YamlConfiguration.loadConfiguration(dataFile);
    }

    public boolean canClaimDaily(Player player) {
        LocalDateTime lastClaim = dailyRewards.get(player.getUniqueId());
        if (lastClaim == null) return true;
        return LocalDateTime.now().isAfter(lastClaim.plusDays(1));
    }

    public boolean canClaimWeekly(Player player) {
        LocalDateTime lastClaim = weeklyRewards.get(player.getUniqueId());
        if (lastClaim == null) return true;
        return LocalDateTime.now().isAfter(lastClaim.plusWeeks(1));
    }

    public boolean canClaimMonthly(Player player) {
        LocalDateTime lastClaim = monthlyRewards.get(player.getUniqueId());
        if (lastClaim == null) return true;
        return LocalDateTime.now().isAfter(lastClaim.plusMonths(1));
    }

    public void claimReward(Player player, RewardType type) {
        switch (type) {
            case DAILY:
                dailyRewards.put(player.getUniqueId(), LocalDateTime.now());
                break;
            case WEEKLY:
                weeklyRewards.put(player.getUniqueId(), LocalDateTime.now());
                break;
            case MONTHLY:
                monthlyRewards.put(player.getUniqueId(), LocalDateTime.now());
                break;
        }
        saveData();
    }

    private void saveData() {
        try {
            data.save(dataFile);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public enum RewardType {
        DAILY,
        WEEKLY,
        MONTHLY
    }
}
