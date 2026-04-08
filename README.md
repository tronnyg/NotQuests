# NotQuests
Download: https://modrinth.com/plugin/notquests/versions

[![GitHub issues](https://img.shields.io/github/issues/AlessioGr/NotQuests)](https://github.com/AlessioGr/NotQuests/issues)
[![GitHub stars](https://img.shields.io/github/stars/AlessioGr/NotQuests)](https://github.com/AlessioGr/NotQuests/stargazers)
[![GitHub license](https://img.shields.io/github/license/AlessioGr/NotQuests)](https://github.com/AlessioGr/NotQuests/blob/main/LICENSE)
<a href="https://alessiogr.github.io/NotQuests/"><img src="https://img.shields.io/badge/JavaDocs-Read%20now-yellow" alt="javadocs"></a>
[![CodeFactor](https://www.codefactor.io/repository/github/alessiogr/notquests/badge)](https://www.codefactor.io/repository/github/alessiogr/notquests)
[![Crowdin](https://badges.crowdin.net/e/c753a7d24d44ac550e857d2b521d9ecb/localized.svg)](https://notquests.crowdin.com/notquests)

![Banner](https://user-images.githubusercontent.com/70709113/133943253-af271d49-441b-473e-8b95-6053fe5d09cb.png)

NotQuests is a flexible, open-source Quest plugin for Paper, featuring custom YAML-based GUIs, a powerful variable system, and extensive integrations.
---

You can find the releases here: https://github.com/AlessioGr/NotQuests/releases. A MySQL database connection is recommended and can be specified in the general.yml. Otherwise, SQLite will be used.

## Requirements
- Paper 26.1 (Minecraft 26.1.1)
- Java 25

## Translations
Do not commit your translations on GitHub. Instead, use translate.notquests.com. Translations are always welcome :) Check https://translate.notquests.com/notquests#readme before translating.

## How to build
Git clone this project and build it with these Gradle commands: `clean build`

The output file should be in the folder `plugin/build/libs/plugin-version.jar`


![Banner_Features](https://user-images.githubusercontent.com/70709113/133997198-bbc020b6-69c5-454b-b5b5-5f1bec0bab0d.png)
- Proxy support (like Velocity, Waterfall & BungeeCord)
- **Custom YAML-based GUIs** - the entire GUI system is configurable via YAML files, supporting tabbed interfaces, paged item lists, action buttons, custom skull textures, and dynamic placeholders
- Conversation system
- Great API
- Quest Profiles - players can create multiple profiles with separate quest progress
- Sub-objectives with unlimited nesting
- Variable system for flexible Actions & Conditions
- Tag System (save data per-player)
- Modern coloring using MiniMessage
- Player data is saved in MySQL or SQLite, quest configuration data in a configuration file
- Quest Points (you can create your own currency via tags)
- Smart Command Tab Completions for all User and Admin commands
- Translation system (https://translate.notquests.com/)
- Re-usable Actions for Triggers
- Multiple Quest Objectives:
  - Break Blocks
  - Place Blocks
  - Collect Items
  - Consume Items
  - Craft Items
  - Deliver Items to NPC
  - Escort NPC
  - Kill Mobs
  - Breed Mobs
  - Complete Other Quest
  - Talk to NPC
  - Command Triggers (to finish the objective)
  - KillEliteMobs (if you're using EliteMobs)
  - ReachLocation (WorldEdit required for objective creation)
  - SlimefunResearch (requires Slimefun)
  - Run Command
  - Interact (right- or left-click block (or both))
  - Jump
  - Sneak
  - Smelt Items
  - Shear Sheep
  - Open Buried Treasure
  - Enchant
  - NumberVariable
  - And more
- Multiple Conditions (Requirements) and Actions (Rewards) based on the variable system
- Multiple Triggers: (Triggers run Actions when activated, set per-quest)
  - Begin Quest or Objective
  - Complete Quest or Objective
  - Death
  - Disconnect
  - Fail Quest
  - NPC dies (good for Escort NPC Quests)
  - Enter World
  - Leave World


**Optional integrations:**
- Citizens - needed for Citizens NPC stuff to work. You can also use Armor Stands without Citizens
- Vault - for Economy stuff
- MySQL Database (strongly recommended)
- PlaceholderAPI - needed for placeholders
- MythicMobs, EliteMobs, Slimefun, LuckPerms, Towny, Jobs Reborn, EcoBosses, UltimateJobs
- Countless other plugins through placeholder and command objectives/conditions/actions


![Banner_Placeholders](https://user-images.githubusercontent.com/70709113/133997368-44c2bdb3-4ad9-483b-b2db-a5221d3d8a5a.png)

Player Placeholders:

%notquests_player_has_completed_quest_QUESTNAME%
%notquests_player_has_current_active_quest_QUESTNAME%
%notquests_player_is_objective_unlocked_and_active_OBJECTIVEID_from_active_quest_QUESTNAME%
%notquests_player_is_objective_unlocked_OBJECTIVEID_from_active_quest_QUESTNAME%
%notquests_player_is_objective_completed_OBJECTIVEID_from_active_quest_QUESTNAME%
%notquests_player_questpoints%
%notquests_player_active_quests_list_horizontal%
%notquests_player_active_quests_list_vertical%
%notquests_player_completed_quests_amount%
%notquests_player_active_quests_amount%
%notquests_player_tag_TAGNAME%
%notquests_player_variable_VARIABLENAME%
%notquests_player_expression_EXPRESSION%
%notquests_player_rounded_expression_EXPRESSION%
%notquests_player_quest_cooldown_left_formatted_QUESTNAME%

You are welcome to join https://discord.gg/7br638S5Ex for community support and announcements.

Please don't hesitate to contribute any features you need on GitHub: https://github.com/AlessioGr/NotQuests
