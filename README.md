# Neon Visuals (Fabric 1.21.11)

Клиентский мод-визуалы в стиле Pulse. Меню: **Right Shift**. HUD-блоки (Target / Potion) двигаются ЛКМ в меню.

Модули: Target HUD (имя, анимированное HP, броня + прочность), Potion HUD, Item Swap (регулятор анимации смены предмета),
Hit Particles (6 типов), Fog (цветная дымка), Sword Adjust (X/Y/Z, scale, rotation), White Enchant (белый глинт, всегда включён
через ресурсы мода: assets/minecraft/textures/misc/enchanted_glint_*.png).

## Сборка
1. Сверь версии в gradle.properties с https://fabricmc.net/develop (1.21.11).
2. `gradle build` -> build/libs/neonvisuals-2.0.0.jar -> в папку mods вместе с Fabric API.
