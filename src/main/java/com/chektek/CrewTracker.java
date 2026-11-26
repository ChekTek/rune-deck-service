package com.chektek;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.chektek.payload.CrewPayload;

import net.runelite.api.Actor;
import net.runelite.api.Client;
import net.runelite.api.NPC;
import net.runelite.api.NPCComposition;
import net.runelite.api.Player;
import net.runelite.api.WorldEntity;
import net.runelite.api.WorldView;
import net.runelite.api.gameval.DBTableID;
import net.runelite.api.gameval.VarbitID;
import net.runelite.client.util.Text;

/**
 * Tracks the local player's Sailing crew from the game's own varbits
 * and builds the CREW payload snapshots.
 *
 * <ul>
 *   <li>Which crewmate sits in each of the 5 crew slots, and which job
 *       they are assigned to, are plain varbits
 *       (SAILING_CREW_SLOT_n / SAILING_CREW_SLOT_n_POSITION).</li>
 *   <li>Position values: 0 = unassigned, 4 = sails, 5 = repairs,
 *       7 = wind catcher, 10 = the hook on a skiff, 13 / 14 = the two
 *       hooks on a sloop.</li>
 *   <li>A crewmate's name comes from the game's crew table
 *       (DBTableID.SailingCrew), resolving the boat NPC id to a name.</li>
 *   <li>Assignment alone does not prove a crewmate is producing
 *       salvage (the wreck may have sunk, the hold may be full), so
 *       "WORKING" additionally requires evidence: the crewmate's
 *       overhead catch line ("... hook some salvage ..." / "... put
 *       it in the cargo hold") heard within the last
 *       {@link #HEARD_WINDOW_TICKS} ticks — the same window the AFK
 *       Shipwrecks hub plugin uses. Assigned but unheard for longer
 *       than the window is "IDLE"; assigned and not yet observed for
 *       a full window is "UNKNOWN", never a guess.</li>
 * </ul>
 *
 * All methods must be called on the client thread. The tracker is a
 * singleton (like {@link PayloadCache}) and is deliberately not reset
 * when the payload cache is cleared, so a client reconnecting does
 * not lose the evidence window; it is reset on logout.
 */
public class CrewTracker {

	private static final Logger LOGGER = LoggerFactory.getLogger(CrewTracker.class);

	private static final CrewTracker instance = new CrewTracker();

	static final int SLOTS = 5;

	/** Crew slot varbits (RuneLite API gameval): the crewmate unique id in each slot. */
	private static final int[] CREW_VARBITS = {
		VarbitID.SAILING_CREW_SLOT_1, VarbitID.SAILING_CREW_SLOT_2, VarbitID.SAILING_CREW_SLOT_3,
		VarbitID.SAILING_CREW_SLOT_4, VarbitID.SAILING_CREW_SLOT_5
	};

	/** Crew position varbits: the job assigned to the crewmate in each slot. */
	private static final int[] POSITION_VARBITS = {
		VarbitID.SAILING_CREW_SLOT_1_POSITION, VarbitID.SAILING_CREW_SLOT_2_POSITION,
		VarbitID.SAILING_CREW_SLOT_3_POSITION, VarbitID.SAILING_CREW_SLOT_4_POSITION,
		VarbitID.SAILING_CREW_SLOT_5_POSITION
	};

	// Position values, from the AFK Shipwrecks hub plugin's in-game
	// observation (CrewAssignment.java), cross-checked against the
	// core Sailing plugin's notes.
	static final int POS_NONE = 0;
	static final int POS_SAILS = 4;
	static final int POS_REPAIRS = 5;
	static final int POS_WIND_CATCHER = 7;
	static final int POS_HOOK_SKIFF = 10;
	static final int POS_HOOK_SLOOP_1 = 13;
	static final int POS_HOOK_SLOOP_2 = 14;

	/** World entity config ids of the player's own boats (read from the client in game; the gameval classes have no constants for them). */
	private static final int ENTITY_RAFT = 1;
	private static final int ENTITY_SKIFF = 2;
	private static final int ENTITY_SLOOP = 3;

	/** How long a crewmate heard salvaging is still assumed to be working a hook, in game ticks (~60 s). Mirrors AFK Shipwrecks' HEARD_SALVAGING_TICKS. */
	static final int HEARD_WINDOW_TICKS = 100;

	private static final int NEVER = Integer.MIN_VALUE;

	/** The ghost crewmate only ever says variations of "Wooo wooo." */
	private static final Pattern GHOST_SPEECH = Pattern.compile("(?i)^(?:w+o+[ ,]*)+[.!]*$");

	private final Map<Integer, String> nameByUniqueId = new HashMap<>();

	// Roster snapshot, refreshed every game tick.
	private boolean aboard;
	private String boatKind;
	private int boatCapacity;
	private final int[] slotUniqueId = new int[SLOTS];
	private final int[] slotPosition = new int[SLOTS];
	private final boolean[] slotAboard = new boolean[SLOTS];
	private int lastRosterTick = NEVER;

	// Evidence bookkeeping, per slot.
	private final int[] lastSalvageTick = new int[SLOTS];
	private final int[] hookSinceTick = new int[SLOTS];
	private int lastSailingXpTick = NEVER;

	private CrewTracker() {
		resetEvidence();
	}

	public static CrewTracker getInstance() {
		return instance;
	}

	private void resetEvidence() {
		for (int i = 0; i < SLOTS; i++) {
			lastSalvageTick[i] = NEVER;
			hookSinceTick[i] = NEVER;
			slotUniqueId[i] = 0;
			slotPosition[i] = -1;
		}
	}

	/** Called when the player logs out: assignments and evidence no longer apply. */
	public void reset() {
		aboard = false;
		boatKind = null;
		boatCapacity = 0;
		lastRosterTick = NEVER;
		resetEvidence();
		lastSailingXpTick = NEVER;
	}

	/** Records that Sailing XP increased this tick (used only for the ghost crewmate's evidence). */
	public void noteSailingXp(int tick) {
		lastSailingXpTick = tick;
	}

	/**
	 * Refreshes the roster snapshot from the crew varbits and builds
	 * the current CREW payload. The roster is re-read at most once per
	 * game tick, so calling this more than once in the same tick is
	 * cheap and returns the same state.
	 */
	public CrewPayload buildPayload(Client client) {
		updateRoster(client);

		int tick = client.getTickCount();

		List<CrewPayload.Hook> hooks = new ArrayList<>();
		for (int[] hookPosition : hookPositionsForBoat()) {
			int hookNumber = hookPosition[0];
			int slot = slotWithPosition(hookPosition[1]);
			if (slot < 0) {
				hooks.add(new CrewPayload.Hook(hookNumber, null, "EMPTY"));
			} else {
				hooks.add(new CrewPayload.Hook(hookNumber, nameOf(slotUniqueId[slot]), hookState(slot, tick)));
			}
		}

		List<CrewPayload.CrewMember> crew = new ArrayList<>();
		for (int slot = 0; slot < SLOTS; slot++) {
			if (!slotAboard[slot]) {
				continue;
			}
			crew.add(new CrewPayload.CrewMember(slot + 1, nameOf(slotUniqueId[slot]),
				assignmentName(slotPosition[slot]), slotPosition[slot], crewState(slot, tick)));
		}

		return new CrewPayload(aboard, boatKind,
			hooks.toArray(new CrewPayload.Hook[0]), crew.toArray(new CrewPayload.CrewMember[0]));
	}

	/** Refreshes the roster snapshot from the crew varbits, at most once per game tick. */
	private void updateRoster(Client client) {
		int tick = client.getTickCount();
		if (tick == lastRosterTick) {
			return;
		}
		lastRosterTick = tick;

		Player local = client.getLocalPlayer();
		WorldEntity boat = findOwnBoat(client);
		aboard = false;
		boatKind = null;
		boatCapacity = 0;
		if (boat != null && local != null && boat.getWorldView() != null && local.getWorldView() != null
			&& boat.getWorldView().getId() == local.getWorldView().getId()) {
			aboard = true;
			switch (boat.getConfig().getId()) {
				case ENTITY_RAFT:
					boatKind = "RAFT";
					boatCapacity = 0;
					break;
				case ENTITY_SKIFF:
					boatKind = "SKIFF";
					boatCapacity = 2;
					break;
				case ENTITY_SLOOP:
					boatKind = "SLOOP";
					boatCapacity = 5;
					break;
				default:
					boatKind = "UNKNOWN";
					boatCapacity = SLOTS;
					break;
			}
		}

		int filledSeen = 0;
		for (int slot = 0; slot < SLOTS; slot++) {
			int uniqueId = aboard ? client.getVarbitValue(CREW_VARBITS[slot]) : 0;
			int position = aboard ? client.getVarbitValue(POSITION_VARBITS[slot]) : POS_NONE;

			if (uniqueId != slotUniqueId[slot]) {
				// A different crewmate (or nobody) now sits in this slot:
				// what the previous one did no longer counts.
				lastSalvageTick[slot] = NEVER;
				hookSinceTick[slot] = NEVER;
			}
			if (isHook(position) && position != slotPosition[slot]) {
				hookSinceTick[slot] = tick;
			}
			if (!isHook(position)) {
				// Moved off the hook: salvage heard earlier no longer
				// places them there.
				lastSalvageTick[slot] = NEVER;
				if (!isHook(slotPosition[slot])) {
					hookSinceTick[slot] = NEVER;
				}
			}
			slotUniqueId[slot] = uniqueId;
			slotPosition[slot] = position;

			// The game picks crew in slot order when more are assigned
			// than the boat can carry: only the first boatCapacity
			// filled slots are aboard.
			slotAboard[slot] = uniqueId != 0 && filledSeen < boatCapacity;
			if (uniqueId != 0) {
				filledSeen++;
			}

			if (slotAboard[slot] && uniqueId != 0 && !nameByUniqueId.containsKey(uniqueId)) {
				String name = lookupCrewmateName(client, uniqueId);
				if (name != null && !name.isEmpty()) {
					nameByUniqueId.put(uniqueId, name);
				}
			}
		}
	}

	/**
	 * Feeds an overhead-text change into the evidence tracker: a
	 * crewmate announcing a catch is proof they are working their hook.
	 */
	public void onOverheadText(Client client, Actor actor, String text) {
		if (!aboard || !(actor instanceof NPC) || text == null) {
			return;
		}
		String actorName = actor.getName();
		if (actorName == null) {
			return;
		}
		int slot = slotByName(actorName.trim());
		if (slot < 0) {
			return;
		}
		String plain = Text.removeTags(text).trim().toLowerCase();
		if (plain.contains("cargo hold is full")) {
			// A stop line, not a catch: the hold being full is one of
			// the ways a working crewmate stops producing.
			return;
		}
		boolean salvage = plain.contains("hook some salvage") || plain.contains("put it in the cargo hold");
		if (!salvage && GHOST_SPEECH.matcher(plain).matches()) {
			// The ghost crewmate cannot say what he did; a Sailing XP
			// gain in the same tick is the tell (AFK Shipwrecks does
			// the same). Allow one tick of slack either way.
			int tick = client.getTickCount();
			salvage = lastSailingXpTick != NEVER && Math.abs(tick - lastSailingXpTick) <= 1;
		}
		if (salvage) {
			lastSalvageTick[slot] = client.getTickCount();
		}
	}

	/** The hook positions the current boat type supports: {hook number, position varbit value}. */
	private int[][] hookPositionsForBoat() {
		if (!aboard || boatKind == null) {
			return new int[0][];
		}
		switch (boatKind) {
			case "SKIFF":
				return new int[][]{{1, POS_HOOK_SKIFF}};
			case "SLOOP":
				return new int[][]{{1, POS_HOOK_SLOOP_1}, {2, POS_HOOK_SLOOP_2}};
			default:
				return new int[0][];
		}
	}

	private int slotWithPosition(int positionValue) {
		for (int slot = 0; slot < SLOTS; slot++) {
			if (slotAboard[slot] && slotPosition[slot] == positionValue) {
				return slot;
			}
		}
		return -1;
	}

	private int slotByName(String name) {
		for (int slot = 0; slot < SLOTS; slot++) {
			if (!slotAboard[slot] || slotUniqueId[slot] == 0) {
				continue;
			}
			String known = nameByUniqueId.get(slotUniqueId[slot]);
			if (known != null && known.trim().equalsIgnoreCase(name)) {
				return slot;
			}
		}
		return -1;
	}

	/** WORKING / IDLE / UNKNOWN for a crewmate assigned to a hook. */
	private String hookState(int slot, int tick) {
		if (heardRecently(slot, tick)) {
			return "WORKING";
		}
		int since = hookSinceTick[slot];
		if (since != NEVER && tick - since <= HEARD_WINDOW_TICKS) {
			// On the hook, but not yet observed for a full window:
			// honestly unknown rather than a guess either way.
			return "UNKNOWN";
		}
		return "IDLE";
	}

	private String crewState(int slot, int tick) {
		int position = slotPosition[slot];
		if (isHook(position)) {
			return hookState(slot, tick);
		}
		if (position == POS_NONE) {
			return "IDLE";
		}
		return "ASSIGNED";
	}

	private boolean heardRecently(int slot, int tick) {
		return lastSalvageTick[slot] != NEVER && tick - lastSalvageTick[slot] <= HEARD_WINDOW_TICKS;
	}

	static boolean isHook(int position) {
		return position == POS_HOOK_SKIFF || position == POS_HOOK_SLOOP_1 || position == POS_HOOK_SLOOP_2;
	}

	private static String assignmentName(int position) {
		switch (position) {
			case POS_NONE:
				return "NONE";
			case POS_SAILS:
				return "SAILS";
			case POS_REPAIRS:
				return "REPAIRS";
			case POS_WIND_CATCHER:
				return "WIND_CATCHER";
			case POS_HOOK_SKIFF:
			case POS_HOOK_SLOOP_1:
			case POS_HOOK_SLOOP_2:
				return "HOOK";
			default:
				return "OTHER";
		}
	}

	private String nameOf(int uniqueId) {
		return uniqueId == 0 ? null : nameByUniqueId.get(uniqueId);
	}

	/** Finds the player's own boat among the top-level world entities, or null. */
	private static WorldEntity findOwnBoat(Client client) {
		WorldView top = client.getTopLevelWorldView();
		if (top == null) {
			return null;
		}
		for (WorldEntity entity : top.worldEntities()) {
			if (entity != null && entity.getOwnerType() == WorldEntity.OWNER_TYPE_SELF_PLAYER
				&& entity.getConfig() != null) {
				int id = entity.getConfig().getId();
				if (id == ENTITY_RAFT || id == ENTITY_SKIFF || id == ENTITY_SLOOP) {
					return entity;
				}
			}
		}
		return null;
	}

	/**
	 * Resolves a crewmate's display name from the game's crew table:
	 * the row for this unique id names the NPC that stands on the boat
	 * (falling back to the cargo NPC), whose definition carries the
	 * name. Returns null when the table cannot be read.
	 */
	private String lookupCrewmateName(Client client, int uniqueId) {
		try {
			List<Integer> rows = client.getDBRowsByValue(
				DBTableID.SailingCrew.ID, DBTableID.SailingCrew.COL_UNIQUE_ID, 0, uniqueId);
			if (rows == null || rows.isEmpty()) {
				return null;
			}
			int row = rows.get(0);
			String name = npcName(client, client.getDBTableField(row, DBTableID.SailingCrew.COL_BOAT_NPC, 0));
			if (name.isEmpty()) {
				name = npcName(client, client.getDBTableField(row, DBTableID.SailingCrew.COL_CARGO_NPC, 0));
			}
			return name;
		} catch (RuntimeException e) {
			LOGGER.debug("Could not read crewmate {} from the crew table", uniqueId, e);
			return null;
		}
	}

	private static String npcName(Client client, Object[] field) {
		if (field == null || field.length == 0 || !(field[0] instanceof Integer)) {
			return "";
		}
		NPCComposition definition = client.getNpcDefinition((Integer) field[0]);
		if (definition == null || definition.getName() == null) {
			return "";
		}
		return Text.removeTags(definition.getName());
	}
}
