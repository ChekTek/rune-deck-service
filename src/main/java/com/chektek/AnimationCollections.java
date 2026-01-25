package com.chektek;

import com.google.common.collect.ImmutableSet;
import net.runelite.api.gameval.AnimationID;

import java.util.Set;

abstract public class AnimationCollections {
	public static boolean isFishingAnimation(int animation) {
		return FISHING_ANIMATIONS.contains(animation);
	}

	private static final Set<Integer> FISHING_ANIMATIONS = ImmutableSet.of(
			AnimationID.HUMAN_HARPOON_BARBED,
			AnimationID.BRUT_PLAYER_HAND_FISHING_END_BLANK,
			AnimationID.BRUT_PLAYER_HAND_FISHING_END_SHARK_1,
			AnimationID.BRUT_PLAYER_HAND_FISHING_END_SHARK_2,
			AnimationID.BRUT_PLAYER_HAND_FISHING_END_SWORDFISH_1,
			AnimationID.BRUT_PLAYER_HAND_FISHING_END_SWORDFISH_2,
			AnimationID.BRUT_PLAYER_HAND_FISHING_END_TUNA_1,
			AnimationID.BRUT_PLAYER_HAND_FISHING_END_TUNA_2,
			AnimationID.BRUT_PLAYER_HAND_FISHING_START,
			AnimationID.BRUT_PLAYER_HAND_FISHING_READY,
			AnimationID.HUMAN_LARGENET,
			AnimationID.HUMAN_LOBSTER,
			AnimationID.HUMAN_HARPOON_CRYSTAL,
			AnimationID.HUMAN_HARPOON_DRAGON,
			AnimationID.HUMAN_HARPOON,
			AnimationID.HUMAN_HARPOON_INFERNAL,
			AnimationID.HUMAN_HARPOON_TRAILBLAZER,
			AnimationID.HUMAN_OCTOPUS_POT,
			AnimationID.HUMAN_SMALLNET,
			AnimationID.HUMAN_FISHING_CASTING,
			AnimationID.HUMAN_FISHING_CASTING_PEARL,
			AnimationID.HUMAN_FISHING_CASTING_PEARL_FLY,
			AnimationID.HUMAN_FISHING_CASTING_PEARL_BRUT,
			AnimationID.HUMAN_FISH_ONSPOT_PEARL,
			AnimationID.HUMAN_FISH_ONSPOT_PEARL_FLY,
			AnimationID.HUMAN_FISH_ONSPOT_PEARL_BRUT,
			AnimationID.HUMAN_FISHING_CASTING_PEARL_OILY,
			AnimationID.HUMAN_FISHING_ONSPOT_BRUT);
}
