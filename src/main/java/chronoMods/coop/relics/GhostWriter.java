package chronoMods.coop.relics;

import com.evacipated.cardcrawl.modthespire.lib.*;

import com.badlogic.gdx.graphics.*;
import com.badlogic.gdx.graphics.g2d.*;

import com.megacrit.cardcrawl.localization.*;
import com.megacrit.cardcrawl.core.*;
import com.megacrit.cardcrawl.cards.*;
import com.megacrit.cardcrawl.dungeons.*;
import com.megacrit.cardcrawl.blights.*;
import com.megacrit.cardcrawl.rooms.*;
import com.megacrit.cardcrawl.helpers.*;

import basemod.*;
import basemod.abstracts.*;
import basemod.interfaces.*;

import java.util.*;

import chronoMods.*;
import chronoMods.steam.*;
import chronoMods.coop.*;
import chronoMods.ui.deathScreen.*;
import chronoMods.ui.hud.*;
import chronoMods.ui.lobby.*;
import chronoMods.ui.mainMenu.*;

public class GhostWriter extends AbstractBlight {
    public static final String ID = "GhostWriter";
    public static AbstractCard sendCard;

  private static final BlightStrings blightStrings = CardCrawlGame.languagePack.getBlightString(ID);
  public static final String NAME = blightStrings.NAME;
  public static final String[] DESCRIPTIONS = blightStrings.DESCRIPTION;

    public GhostWriter() {
        super(ID, NAME, "", "spear.png", true);
        this.blightID = ID;
        this.name = NAME;
        updateDescription();
        this.unique = true;
        this.img = ImageMaster.loadImage("chrono/images/blights/" + ID + ".png");
        this.outlineImg = ImageMaster.loadImage("chrono/images/blights/outline/" + ID + ".png");
        this.increment = 0;
        this.tips.add(new PowerTip(name, description));
    }

    @Override
    public void updateDescription() {
        this.description = this.DESCRIPTIONS[0];
        // Specify teammate?
    }

    @SpirePatch(clz = CardGroup.class, method="moveToExhaustPile")
    public static class onExhaust {
        public static void Postfix(CardGroup __instance, AbstractCard c) {
            if (TogetherManager.gameMode != TogetherManager.mode.Coop) { return; }
            if (AbstractDungeon.player.hasBlight("SiphonPump")) {
                // Remove from Exhaust Pile
                AbstractDungeon.player.exhaustPile.removeCard(c);
                // Remove from Master Deck
                for (AbstractCard card : AbstractDungeon.player.masterDeck.group) {
                  if (card.uuid.equals(c.uuid))
                    AbstractDungeon.player.masterDeck.removeCard(c); 
                } 
                
                // Send to other player. Next? Random?
                GhostWriter.sendCard = c;
                NetworkHelper.sendData(NetworkHelper.dataType.SendCard);
            }
        }
    }
}