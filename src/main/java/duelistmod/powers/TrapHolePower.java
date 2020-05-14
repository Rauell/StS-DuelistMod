package duelistmod.powers;

import com.badlogic.gdx.graphics.Texture;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.localization.PowerStrings;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import com.megacrit.cardcrawl.powers.AbstractPower;

import duelistmod.*;
import duelistmod.abstracts.DuelistCard;
import duelistmod.variables.Strings;


public class TrapHolePower extends AbstractPower 
{
    public AbstractCreature source;

    public static final String POWER_ID = DuelistMod.makeID("TrapHolePower");
    private static final PowerStrings powerStrings = CardCrawlGame.languagePack.getPowerStrings(POWER_ID);
    public static final String NAME = powerStrings.NAME;
    public static final String[] DESCRIPTIONS = powerStrings.DESCRIPTIONS;
    public static final String IMG = DuelistMod.makePath(Strings.TRAP_HOLE_POWER);
    
    public int chance = 4;

    public TrapHolePower(final AbstractCreature owner, final AbstractCreature source, int chances) 
    {
        this.name = NAME;
        this.ID = POWER_ID;
        this.owner = owner;        
        this.type = PowerType.BUFF;
        this.isTurnBased = false;
        this.img = new Texture(IMG);
        this.source = source;
        if (this.owner.hasPower(TrapHolePower.POWER_ID)) { this.amount += chances; chance += chances; }
        else { this.amount = chance = chances; }
        if (this.amount > 10) { this.amount = chance = 10; }
        this.updateDescription();
    }
    
    @Override
    public void onDrawOrDiscard() 
    {
    	if (this.amount != chance) { chance = this.amount; }
    	if (this.amount < 1) { DuelistCard.removePower(this, this.owner); }
    	if (this.amount > 10) { this.amount = chance = 10; }
    	updateDescription();
    }
    
    @Override
    public void atStartOfTurn() 
    {
    	if (this.amount != chance) { chance = this.amount; }
    	if (this.amount < 1) { DuelistCard.removePower(this, this.owner); }
    	if (this.amount > 10) { this.amount = chance = 10; }
    	updateDescription();
    }
    
    @Override
    public void onPlayCard(AbstractCard c, AbstractMonster m) 
    {
    	if (this.amount != chance) { chance = this.amount; }
    	if (this.amount < 1) { DuelistCard.removePower(this, this.owner); }
    	if (this.amount > 10) { this.amount = chance = 10; }
    	updateDescription();
    }
    
    @Override
	public void atEndOfTurn(final boolean isPlayer) 
	{
    	if (this.amount != chance) { chance = this.amount; }
    	if (this.amount < 1) { DuelistCard.removePower(this, this.owner); }
    	if (this.amount > 10) { this.amount = chance = 10; }
    	updateDescription();
	}

    @Override
	public void updateDescription() {
        this.description = DESCRIPTIONS[0] + (chance * 10) + DESCRIPTIONS[1];
    }
}
