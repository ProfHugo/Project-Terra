# Project-Terra
My first mod. This aims to improve some aspects of combat, such as adding a stamina system and removing the ever-so obnoxious damage immunity system.

## Current Features:
### Removes damage immunity granted after taking damage
  - If an entity takes any damage in Minecraft, that entity would be immune to damage for 0.5 seconds (10 ticks) unless the damage dealt during immunity is higher than the hit that granted it
  - This system was in place so things won't die instantly to spam clicking pre 1.9
  - However, in 1.9, since a proper attack speed system got implemeted, landing hit matters a lot more, and the damage immunity system made it very clunky for multiple player (or zombies) to gang up on something
    - Boss fights became very clunky, as one player would then a hit, only for another's to be nullified if he/she went for it at the same time
    - The original purpose of the damage immunity is now fulfilled by this new attack speed system, so now it's just "in the way" of things
    - This mod completely removes this old system by setting the damage immunity timer to 0 on update, and to compensate for any potential one-shots due to DOTs that relied on the old system to be balanced (such as lava and cactus, but there are more), the damage is cut down by 20 times per tick to match the old values.

### Adds a stamina system (VERY WIP)
  - Currently, players have a max base amount of 100 stamina, which goes down based on hunger levels on a linear scale
  - Players regenerates stamina passively, and that amount is modified based on how much armor that player has on, and what the player is doing.
    - The base regen rate is 2.5% of the player's max stamina per tick (50% max stamina per second), and is reduced to 1.5% maxStam/tick with 20 armor values
    - The player also don't regen any stamina if he/she is running or if he/she is airborne (excluding elytra flight)
  - As of now, only attacking and blocking cost stamina
    - The stamina consuption per hit is based on the attack speed of the weapon, and the higher the attack speed, the lower the stamina cost
      - The current formula is: y = log(x/14.8)/log(0.91)
        - x is the attack speed value of held weapon
        - y is the stamina cost per swing
    - Blocking also consumes stamina (current values are very broken and unbalanced)
      - Currently, it's post mitigation amount multiplied by 40, so it cost approx. 20 stamina per block
        - This is broken, because if the blocker gets backstabed, he/she could lose well over 250 stamina easily
        - If the blocker runs out of stamina after blocking, he/she gets guardbroken, in which their shield would be disabled for 2 seconds (WIP, similar to how axes could guardbreak in vanilla Minecraft
