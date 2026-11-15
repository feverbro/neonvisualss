package dev.neon.visuals;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import net.fabricmc.loader.api.FabricLoader;

import java.nio.file.Files;
import java.nio.file.Path;

/** Все настройки модулей. Сохраняются в config/neonvisuals.json */
public class Config {
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
    private static final Path FILE = FabricLoader.getInstance().getConfigDir().resolve("neonvisuals.json");
    public static Config I = new Config();

    // Target HUD
    public boolean targetHud = true;
    public int targetX = 260, targetY = 180;
    public boolean targetArmor = true, targetHeadOnly = false;
    public int accent = 0xFF00E5FF; // неон-циан

    // Potion HUD
    public boolean potionHud = true;
    public int potionX = 6, potionY = 60;

    // Item Swap (анимация смены предмета). 0 = без анимации, 1 = ваниль
    public boolean itemSwap = true;
    public float swapFactor = 0.0f;

    // Hit Particles
    public boolean hitParticles = true;
    public int hitParticleCount = 12;
    public int hitParticleType = 0;

    // Fog (цветной туман-оверлей по экрану)
    public boolean fog = false;
    public int fogColor = 0xFF8A2BE2;
    public int fogAlpha = 40; // 0..255

    // Sword Adjust (позиция/размер/поворот предмета в руке)
    public boolean swordAdjust = false;
    public float swX = 0f, swY = 0f, swZ = 0f;
    public float swScale = 1f;
    public float swRotX = 0f, swRotY = 0f, swRotZ = 0f;


    public static void load() {
        try {
            if (Files.exists(FILE)) I = GSON.fromJson(Files.readString(FILE), Config.class);
        } catch (Exception e) { I = new Config(); }
        if (I == null) I = new Config();
    }

    public static void save() {
        try { Files.writeString(FILE, GSON.toJson(I)); } catch (Exception ignored) {}
    }
}
