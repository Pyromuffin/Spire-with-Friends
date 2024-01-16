package chronoMods.coop;

import chronoMods.TogetherManager;
import com.evacipated.cardcrawl.modthespire.lib.SpirePatch;
import com.evacipated.cardcrawl.modthespire.lib.SpireReturn;
import com.megacrit.cardcrawl.blights.AbstractBlight;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.neow.NeowEvent;
import com.megacrit.cardcrawl.rooms.AbstractRoom;
import com.megacrit.cardcrawl.screens.custom.CustomMod;

import java.util.ArrayList;

public class TeamworkCustom {
    public static boolean isActive = false;

	@SpirePatch(clz = NeowEvent.class, method = "buttonEffect")
    public static class ControlNeowEvent {
        public static SpireReturn Postfix(NeowEvent __instance, int buttonPressed, int ___screenNum) {
            if(!isActive) return SpireReturn.Continue();
            if(___screenNum != 99) return SpireReturn.Continue();

            AbstractDungeon.getCurrRoom().phase = AbstractRoom.RoomPhase.INCOMPLETE;
            AbstractDungeon.dungeonMapScreen.close();

			// add team relic selection
			ArrayList<AbstractBlight> blights = new ArrayList<>();
			for (int i = 0; i < 2; i++) {
				// skip message in a bottle.
				if (TogetherManager.teamBlights.get(0).blightID.equals("MessageInABottle")) {
					blights.add(TogetherManager.teamBlights.get(1));
					TogetherManager.teamBlights.remove(1);
				} else {
					blights.add(TogetherManager.teamBlights.get(0));
					TogetherManager.teamBlights.remove(0);
				}
			}

			TogetherManager.teamRelicScreen.open(blights);
			return SpireReturn.Return(null);
        }
    }
}