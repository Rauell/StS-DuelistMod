package duelistmod.powers;

import com.badlogic.gdx.graphics.Texture;
import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.core.*;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.localization.PowerStrings;
import com.megacrit.cardcrawl.powers.AbstractPower;

import duelistmod.DuelistMod;
import duelistmod.abstracts.DuelistCard;
import duelistmod.helpers.Util;

// Passive no-effect power, just lets Toon Monsters check for playability

public class CrystalTreePower extends AbstractPower 
{
    public AbstractCreature source;

    public static final String POWER_ID = duelistmod.DuelistMod.makeID("CrystalTreePower");
    private static final PowerStrings powerStrings = CardCrawlGame.languagePack.getPowerStrings(POWER_ID);
    public static final String NAME = powerStrings.NAME;
    public static final String[] DESCRIPTIONS = powerStrings.DESCRIPTIONS;
    public static final String IMG = DuelistMod.makePowerPath("CrystalTreePower.png");
    
    public CrystalTreePower(final AbstractCreature owner, final AbstractCreature source, int amt) 
    {
        this.name = NAME;
        this.ID = POWER_ID;
        this.owner = owner;        
        this.type = PowerType.BUFF;
        this.isTurnBased = false;
        this.img = new Texture(IMG);
        this.source = source;
        this.amount = amt;
        this.updateDescription();
    }
    
    @Override
    public int onAttacked(final DamageInfo info, int damageAmount) {
        if (damageAmount > 0 && !((AbstractPlayer)this.owner).stance.ID.equals("Neutral")) {
            this.flash();
            DuelistCard.gainTempHP(this.amount);
        }
        else
        {
        	Util.log("Crystal Tree: player was in " + AbstractDungeon.player.stance.name);
        }
        return damageAmount;
    }

    @Override
	public void updateDescription() {
        this.description = DESCRIPTIONS[0] + this.amount + DESCRIPTIONS[1];
    }
}
