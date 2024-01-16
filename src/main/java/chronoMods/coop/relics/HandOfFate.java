package chronoMods.coop.relics;


import basemod.BaseMod;
import chronoMods.TogetherManager;
import chronoMods.network.NetworkHelper;
import chronoMods.network.RemotePlayer;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.evacipated.cardcrawl.modthespire.lib.SpireInsertPatch;
import com.evacipated.cardcrawl.modthespire.lib.SpirePatch;
import com.evacipated.cardcrawl.modthespire.lib.SpireReturn;
import com.megacrit.cardcrawl.blights.AbstractBlight;
import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.helpers.ImageMaster;
import com.megacrit.cardcrawl.helpers.PotionHelper;
import com.megacrit.cardcrawl.helpers.PowerTip;
import com.megacrit.cardcrawl.helpers.ShaderHelper;
import com.megacrit.cardcrawl.localization.BlightStrings;
import com.megacrit.cardcrawl.map.MapRoomNode;
import com.megacrit.cardcrawl.potions.AbstractPotion;
import com.megacrit.cardcrawl.potions.SmokeBomb;
import com.megacrit.cardcrawl.ui.campfire.RestOption;

public class HandOfFate extends AbstractBlight {

    public static final String ID = "HandOfFate";
    private static final BlightStrings blightStrings = CardCrawlGame.languagePack.getBlightString(ID);
    public static final String NAME = blightStrings.NAME;
    public static final String[] DESCRIPTIONS = blightStrings.DESCRIPTION;

    public HandOfFate() {
        super(ID, NAME, "", "spear.png", true);
        this.blightID = ID;
        this.name = NAME;
        updateDescription();
        this.unique = true;
        this.img = ImageMaster.loadImage("chrono/images/blights/" + ID + ".png");
        this.outlineImg = ImageMaster.loadImage("chrono/images/blights/outline/" + ID + ".png");


        this.increment = 0;
        this.tips.clear();
        this.tips.add(new PowerTip(name, description));
        this.counter = TogetherManager.players.size() - 1;
        if (this.counter >= 4) {
            this.increment = 1;
            this.counter--;
        }
    }

    @Override
    public void updateDescription() {
        if (TogetherManager.players.size() <= 4)
            this.description = this.DESCRIPTIONS[0];
        if (TogetherManager.players.size() > 4)
            this.description = this.DESCRIPTIONS[1];
    }

    @Override
    public void effect() {

        /*
        // if you have any lives, heal up
        if (counter > 0) {
            flash();
            AbstractDungeon.player.isDead = false;
            if (AbstractDungeon.player.hasRelic("Mark of the Bloom")) {
                AbstractDungeon.player.currentHealth = AbstractDungeon.player.maxHealth;
            } else {
                AbstractDungeon.player.heal(AbstractDungeon.player.maxHealth, true);
            }
        }

        // If you have an increment lose an increment. Otherwise, lose a counter.
        if (this.increment == 0)
            this.counter--;

        NetworkHelper.sendData(NetworkHelper.dataType.LoseLife);
        */

        AbstractDungeon.player.isDead = false;
        AbstractDungeon.player.heal(AbstractDungeon.player.maxHealth, true);
        NetworkHelper.sendData(NetworkHelper.dataType.Hp);


        // use a smoke bomb
        AbstractPotion p = PotionHelper.getPotion("SmokeBomb");
        p.use(AbstractDungeon.player);

        TogetherManager.currentUser.stuck = true;
        NetworkHelper.sendData(NetworkHelper.dataType.Stuck);
    }

    @Override
    public void renderInTopPanel(SpriteBatch sb) {
        /*
        if (this.counter <= 0)
            ShaderHelper.setShader(sb, ShaderHelper.Shader.GRAYSCALE);

        super.renderInTopPanel(sb);
        sb.setColor(Color.WHITE);

        int i = 0;
        for (; i < this.counter; i++) {
            sb.draw(ImageMaster.TP_HP, this.currentX - (56.0F * Settings.scale) + (12.0F * Settings.scale * i), this.currentY - 64.0F * Settings.scale, 32.0F, 32.0F, 64.0F, 64.0F, this.scale/1.5f, this.scale/1.5f, 0, 0, 0, 64, 64, false, false);
        }

        for (; i < this.counter + this.increment; i++) {
            sb.setColor(Color.GOLD);
            sb.draw(TogetherManager.TP_WhiteHeart, this.currentX - (56.0F * Settings.scale) + (12.0F * Settings.scale * i), this.currentY - 64.0F * Settings.scale, 32.0F, 32.0F, 64.0F, 64.0F, this.scale/1.5f, this.scale/1.5f, 0, 0, 0, 64, 64, false, false);
        }
        sb.setColor(Color.WHITE);

        if (this.counter <= 0)
            ShaderHelper.setShader(sb, ShaderHelper.Shader.DEFAULT);
        */
    }

    @Override
    public void renderCounter(SpriteBatch sb, boolean inTopPanel) {}

    // This patch prevents death
    @SpirePatch(clz = AbstractPlayer.class, method="damage")
    public static class RevivePlayer {
        @SpireInsertPatch(rloc=1875-1725, localvars={})
        public static SpireReturn Insert(AbstractPlayer player, DamageInfo info) {

            if (player.isDead) {
                if (player.hasBlight("HandOfFate")) {
                    player.currentHealth = 0;
                    player.getBlight("HandOfFate").effect();
                    return SpireReturn.Return(null);
                }
            }

            return SpireReturn.Continue();
        }
    }

    @SpirePatch(clz = RestOption.class, method="useOption")
    public static class ReviveRest {
        public static void Postfix(RestOption __instance) {
            boolean reviving = false;

            for(RemotePlayer p : TogetherManager.players){
                if(p.stuck){
                    reviving = true;
                    break;
                }
            }

            if(reviving){
                int damageAmount = AbstractDungeon.player.currentHealth / 2;
                DamageInfo info = new DamageInfo(AbstractDungeon.player, damageAmount, DamageInfo.DamageType.HP_LOSS);
                AbstractDungeon.player.damage(info);
            }

            if(AbstractDungeon.player.hasBlight("HandOfFate")){
                NetworkHelper.sendData(NetworkHelper.dataType.Revive);
            }
        }
    }


}
