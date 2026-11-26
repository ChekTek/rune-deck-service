package com.chektek.payload;

import com.chektek.CrewTracker;

import net.runelite.api.Client;

import java.util.Arrays;
import java.util.Objects;

/**
 * Sailing crew state: whether the player is aboard their own boat, the
 * state of each salvaging hook, and the full crew roster.
 *
 * <p>The snapshot is computed by {@link CrewTracker}, which reads the
 * crew assignment varbits and corroborates "working" hooks with the
 * crewmates' overhead catch lines. A hook's state is one of WORKING,
 * IDLE, UNKNOWN (assigned but not yet observed for a full window) or
 * EMPTY (nobody assigned to that hook).
 *
 * <p>Example:
 * <pre>
 * { "type": "CREW", "aboard": true, "boat": "SLOOP",
 *   "hooks": [ { "hook": 1, "crewmate": "Jobless Jim", "state": "WORKING" },
 *              { "hook": 2, "crewmate": null, "state": "EMPTY" } ],
 *   "crew": [ { "slot": 1, "crewmate": "Jobless Jim", "assignment": "HOOK",
 *               "position": 13, "state": "WORKING" } ] }
 * </pre>
 */
public class CrewPayload extends Payload {
    private boolean aboard;
    private String boat;
    private Hook[] hooks;
    private CrewMember[] crew;

    public CrewPayload() {
        super(PayloadType.CREW);
        this.hooks = new Hook[0];
        this.crew = new CrewMember[0];
    }

    public CrewPayload(Client client) {
        super(PayloadType.CREW);

        CrewPayload snapshot = CrewTracker.getInstance().buildPayload(client);
        this.aboard = snapshot.aboard;
        this.boat = snapshot.boat;
        this.hooks = snapshot.hooks;
        this.crew = snapshot.crew;
    }

    public CrewPayload(boolean aboard, String boat, Hook[] hooks, CrewMember[] crew) {
        super(PayloadType.CREW);
        this.aboard = aboard;
        this.boat = boat;
        this.hooks = hooks;
        this.crew = crew;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        CrewPayload that = (CrewPayload) o;
        return aboard == that.aboard && Objects.equals(boat, that.boat)
                && Arrays.equals(hooks, that.hooks) && Arrays.equals(crew, that.crew);
    }

    @Override
    public int hashCode() {
        int result = Objects.hash(aboard, boat);
        result = 31 * result + Arrays.hashCode(hooks);
        result = 31 * result + Arrays.hashCode(crew);
        return result;
    }

    public boolean isAboard() {
        return aboard;
    }

    public String getBoat() {
        return boat;
    }

    public Hook[] getHooks() {
        return hooks;
    }

    public CrewMember[] getCrew() {
        return crew;
    }

    @Override
    public boolean isNewPayload(Client client) {
        return !this.equals(new CrewPayload(client));
    }

    public static class Hook {
        private final int hook;
        private final String crewmate;
        private final String state;

        public Hook(int hook, String crewmate, String state) {
            this.hook = hook;
            this.crewmate = crewmate;
            this.state = state;
        }

        public int getHook() {
            return hook;
        }

        public String getCrewmate() {
            return crewmate;
        }

        public String getState() {
            return state;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            Hook that = (Hook) o;
            return hook == that.hook && Objects.equals(crewmate, that.crewmate) && Objects.equals(state, that.state);
        }

        @Override
        public int hashCode() {
            return Objects.hash(hook, crewmate, state);
        }
    }

    public static class CrewMember {
        private final int slot;
        private final String crewmate;
        private final String assignment;
        private final int position;
        private final String state;

        public CrewMember(int slot, String crewmate, String assignment, int position, String state) {
            this.slot = slot;
            this.crewmate = crewmate;
            this.assignment = assignment;
            this.position = position;
            this.state = state;
        }

        public int getSlot() {
            return slot;
        }

        public String getCrewmate() {
            return crewmate;
        }

        public String getAssignment() {
            return assignment;
        }

        public int getPosition() {
            return position;
        }

        public String getState() {
            return state;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            CrewMember that = (CrewMember) o;
            return slot == that.slot && position == that.position && Objects.equals(crewmate, that.crewmate)
                    && Objects.equals(assignment, that.assignment) && Objects.equals(state, that.state);
        }

        @Override
        public int hashCode() {
            return Objects.hash(slot, crewmate, assignment, position, state);
        }
    }
}
