package duelistmod.powers.duelistPowers;

import com.badlogic.gdx.graphics.Texture;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.core.*;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.localization.PowerStrings;

import duelistmod.DuelistMod;
import duelistmod.abstracts.*;
import duelistmod.powers.SummonPower;
import duelistmod.relics.Leafblower;
import duelistmod.variables.*;

public class LeavesPower extends DuelistPower
{	
	public AbstractCreature source;

    public static final String POWER_ID = DuelistMod.makeID("LeavesPower");
    private static final PowerStrings powerStrings = CardCrawlGame.languagePack.getPowerStrings(POWER_ID);
    public static final String NAME = powerStrings.NAME;
    public static final String[] DESCRIPTIONS = powerStrings.DESCRIPTIONS;
    public static final String IMG = DuelistMod.makePowerPath("LeavesPower.png");
	
	public LeavesPower(int amt)
	{ 
		this.name = NAME;
        this.ID = POWER_ID;
        this.owner = AbstractDungeon.player;        
        this.type = PowerType.BUFF;
        this.isTurnBased = false;
        this.canGoNegative = false;
        this.img = new Texture(IMG);
        this.source = AbstractDungeon.player;
        this.amount = amt;
		updateDescription(); 
	}
	
	@Override
	public void atStartOfTurn()
	{
		updateDescription();
		if (this.amount2 > 0) { this.flash(); DuelistCard.staticBlock(this.amount2); }
	}
	
	@Override
	public void onTribute(DuelistCard c, DuelistCard c2) { updateDescription(); }
	
	@Override
	public void onSummon(DuelistCard c, int amt) { updateDescription(); }
	
	@Override
	public void onSynergyTribute() { updateDescription(); }

	@Override
	public void updateDescription()
	{
		calcDamage();
		if (this.amount2 < 0) { this.amount2 = 0; }
		this.description = DESCRIPTIONS[0] + this.amount2;
	}
	
	private void calcDamage()
	{
		AbstractPlayer p = AbstractDungeon.player;
		int natsSummoned = 0;
		if (p.hasPower(SummonPower.POWER_ID)) { SummonPower pow = (SummonPower)p.getPower(SummonPower.POWER_ID); natsSummoned = pow.getNumberOfTypeSummoned(Tags.NATURIA); }
		if (natsSummoned > 0)
		{
			float mod = natsSummoned * 0.2f;
			int dmg = (int)(mod * this.amount);
			if (p.hasRelic(Leafblower.ID)) { dmg = (int)(dmg * 1.3f); }
			this.amount2 = dmg;
		}
	}
}
