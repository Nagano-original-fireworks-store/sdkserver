package org.nofs;

import org.nofs.utils.Utils;
import java.util.Arrays;

/* loaded from: sdkserver.jar:emu/grasscutter/GameConstants.class */
public final class GameConstants {
    public static final int DEFAULT_TEAMS = 4;
    public static final int MAX_TEAMS = 10;
    public static final int MAIN_CHARACTER_MALE = 10000005;
    public static final int MAIN_CHARACTER_FEMALE = 10000007;
    public static final int MAX_FRIENDS = 45;
    public static final int MAX_FRIEND_REQUESTS = 50;
    public static final int SERVER_CONSOLE_UID = 99;
    public static final int BATTLE_PASS_MAX_LEVEL = 50;
    public static final int BATTLE_PASS_POINT_PER_LEVEL = 1000;
    public static final int BATTLE_PASS_POINT_PER_WEEK = 10000;
    public static final int BATTLE_PASS_LEVEL_PRICE = 150;
    public static final int BATTLE_PASS_CURRENT_INDEX = 2;
    public static String VERSION = "3.2.0";
    public static final String[] DEFAULT_ABILITY_STRINGS = {"Avatar_DefaultAbility_VisionReplaceDieInvincible", "Avatar_DefaultAbility_AvartarInShaderChange", "Avatar_SprintBS_Invincible", "Avatar_Freeze_Duration_Reducer", "Avatar_Attack_ReviveEnergy", "Avatar_Component_Initializer", "Avatar_FallAnthem_Achievement_Listener"};
    public static final int[] DEFAULT_ABILITY_HASHES = Arrays.stream(DEFAULT_ABILITY_STRINGS).mapToInt(Utils::abilityHash).toArray();
    public static final int DEFAULT_ABILITY_NAME = Utils.abilityHash("Default");
}
