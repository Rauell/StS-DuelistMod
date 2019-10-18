package duelistmod.powers.duelistPowers;

import com.badlogic.gdx.graphics.Texture;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.core.*;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.localization.PowerStrings;

import duelistmod.DuelistMod;
import duelistmod.abstracts.DuelistPower;
import duelistmod.variables.Tags;


public class SogenPower extends DuelistPower
{
    public AbstractCreature source;

    public static final String POWER_ID = duelistmod.DuelistMod.makeID("SogenPower");
    private static final PowerStrings powerStrings = CardCrawlGame.languagePack.getPowerStrings(POWER_ID);
    public static final String NAME = powerStrings.NAME;
    public static final String[] DESCRIPTIONS = powerStrings.DESCRIPTIONS;
    public static final String IMG = DuelistMod.makePowerPath("SogenPower.png");

    public SogenPower(int dmgMod, int blkMod) 
    {
    	this.name = NAME;
        this.ID = POWER_ID;
        this.owner = AbstractDungeon.player;        
        this.type = PowerType.BUFF;
        this.isTurnBased = false;
        this.canGoNegative = false;
        this.img = new Texture(IMG);
        this.source = AbstractDungeon.player;
        this.amount = dmgMod;
        this.amount2 = blkMod;
        this.updateDescription();
    }
    
    @Override
	public float modifyBlock(float blkAmt, AbstractCard card)
	{
		if (card.hasTag(Tags.WARRIOR)) { return blkAmt * ((this.amount / 10.00f) + 1.0f); }
		return blkAmt;
	}

    @Override
	public void updateDescription() {
        this.description = DESCRIPTIONS[0] + (this.amount * 10) + DESCRIPTIONS[1] + (this.amount2 * 10) + DESCRIPTIONS[2];
    }
}
