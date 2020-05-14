package duelistmod.powers;

import com.badlogic.gdx.graphics.Texture;
import com.megacrit.cardcrawl.actions.utility.UseCardAction;
import com.megacrit.cardcrawl.cards.*;
import com.megacrit.cardcrawl.core.*;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.localization.PowerStrings;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import com.megacrit.cardcrawl.orbs.AbstractOrb;
import com.megacrit.cardcrawl.powers.*;

import duelistmod.DuelistMod;
import duelistmod.abstracts.DuelistCard;
import duelistmod.variables.Strings;

public class GravityAxePower extends AbstractPower 
{
    public AbstractCreature source;

    public static final String POWER_ID = DuelistMod.makeID("GravityAxePower");
    private static final PowerStrings powerStrings = CardCrawlGame.languagePack.getPowerStrings(POWER_ID);
    public static final String NAME = powerStrings.NAME;
    public static final String[] DESCRIPTIONS = powerStrings.DESCRIPTIONS;
    public static final String IMG = DuelistMod.makePath(Strings.GRAVITY_AXE_POWER);
    public int FINAL_STRENGTH = 5;
    
    public GravityAxePower(AbstractCreature owner, int strGain) 
    {
        this.name = NAME;
        this.ID = POWER_ID;
        this.owner = owner;
        this.img = new Texture(IMG);
        this.isTurnBased = false;
        this.type = PowerType.DEBUFF;
        if (owner.hasPower(StrengthPower.POWER_ID)) { int startStr = owner.getPower(StrengthPower.POWER_ID).amount + strGain; this.FINAL_STRENGTH = startStr; }
        else { this.FINAL_STRENGTH = strGain; DuelistMod.gravAxeStr = strGain; }
        this.updateDescription();
    }
    
    @Override
	public void updateDescription() 
    {
    	this.description = DESCRIPTIONS[0];
    }
    
    @Override
    public void onApplyPower(AbstractPower power, AbstractCreature target, AbstractCreature source) 
    {
    	if (power instanceof StrengthPower && target.isPlayer) { power.amount = 0; }
    }
    
    @Override
    public void onDrawOrDiscard() 
    {
    	if (AbstractDungeon.player.hasPower(StrengthPower.POWER_ID))
    	{
    		int playerStrength = AbstractDungeon.player.getPower(StrengthPower.POWER_ID).amount;
    		if (playerStrength != FINAL_STRENGTH)
    		{
    			StrengthPower instance = (StrengthPower) AbstractDungeon.player.getPower(StrengthPower.POWER_ID);
    			instance.amount = FINAL_STRENGTH;
    			instance.updateDescription();
    		}
    	}
    	else
    	{
    		DuelistCard.applyPowerToSelf(new StrengthPower(AbstractDungeon.player, FINAL_STRENGTH));
    	}
    }

    @Override
    public void atStartOfTurn() 
    {
    	if (AbstractDungeon.player.hasPower(StrengthPower.POWER_ID))
    	{
    		int playerStrength = AbstractDungeon.player.getPower(StrengthPower.POWER_ID).amount;
    		if (playerStrength != FINAL_STRENGTH)
    		{
    			StrengthPower instance = (StrengthPower) AbstractDungeon.player.getPower(StrengthPower.POWER_ID);
    			instance.amount = FINAL_STRENGTH;
    			instance.updateDescription();
    		}
    	}
    	else
    	{
    		DuelistCard.applyPowerToSelf(new StrengthPower(AbstractDungeon.player, FINAL_STRENGTH));
    	}
    }
    
    @Override
    public void onPlayCard(AbstractCard c, AbstractMonster m) 
    {
    	if (AbstractDungeon.player.hasPower(StrengthPower.POWER_ID))
    	{
    		int playerStrength = AbstractDungeon.player.getPower(StrengthPower.POWER_ID).amount;
    		if (playerStrength != FINAL_STRENGTH)
    		{
    			StrengthPower instance = (StrengthPower) AbstractDungeon.player.getPower(StrengthPower.POWER_ID);
    			instance.amount = FINAL_STRENGTH;
    			instance.updateDescription();
    		}
    	}
    	else
    	{
    		DuelistCard.applyPowerToSelf(new StrengthPower(AbstractDungeon.player, FINAL_STRENGTH));
    	}
    }

    @Override
	public void atEndOfTurn(final boolean isPlayer) 
	{
    	if (AbstractDungeon.player.hasPower(StrengthPower.POWER_ID))
    	{
    		int playerStrength = AbstractDungeon.player.getPower(StrengthPower.POWER_ID).amount;
    		if (playerStrength != FINAL_STRENGTH)
    		{
    			StrengthPower instance = (StrengthPower) AbstractDungeon.player.getPower(StrengthPower.POWER_ID);
    			instance.amount = FINAL_STRENGTH;
    			instance.updateDescription();
    		}
    	}
    	else
    	{
    		DuelistCard.applyPowerToSelf(new StrengthPower(AbstractDungeon.player, FINAL_STRENGTH));
    	}
	}
    
    @Override
    public void onChannel(AbstractOrb orb) 
    {
    	if (AbstractDungeon.player.hasPower(StrengthPower.POWER_ID))
    	{
    		int playerStrength = AbstractDungeon.player.getPower(StrengthPower.POWER_ID).amount;
    		if (playerStrength != FINAL_STRENGTH)
    		{
    			StrengthPower instance = (StrengthPower) AbstractDungeon.player.getPower(StrengthPower.POWER_ID);
    			instance.amount = FINAL_STRENGTH;
    			instance.updateDescription();
    		}
    	}
    	else
    	{
    		DuelistCard.applyPowerToSelf(new StrengthPower(AbstractDungeon.player, FINAL_STRENGTH));
    	}
    }
    
    @Override
    public void onExhaust(AbstractCard card) 
    {
    	if (AbstractDungeon.player.hasPower(StrengthPower.POWER_ID))
    	{
    		int playerStrength = AbstractDungeon.player.getPower(StrengthPower.POWER_ID).amount;
    		if (playerStrength != FINAL_STRENGTH)
    		{
    			StrengthPower instance = (StrengthPower) AbstractDungeon.player.getPower(StrengthPower.POWER_ID);
    			instance.amount = FINAL_STRENGTH;
    			instance.updateDescription();
    		}
    	}
    	else
    	{
    		DuelistCard.applyPowerToSelf(new StrengthPower(AbstractDungeon.player, FINAL_STRENGTH));
    	}
    }
    
    @Override
    public void onAfterUseCard(AbstractCard card, UseCardAction action) 
    {
    	if (AbstractDungeon.player.hasPower(StrengthPower.POWER_ID))
    	{
    		int playerStrength = AbstractDungeon.player.getPower(StrengthPower.POWER_ID).amount;
    		if (playerStrength != FINAL_STRENGTH)
    		{
    			StrengthPower instance = (StrengthPower) AbstractDungeon.player.getPower(StrengthPower.POWER_ID);
    			instance.amount = FINAL_STRENGTH;
    			instance.updateDescription();
    		}
    	}
    	else
    	{
    		DuelistCard.applyPowerToSelf(new StrengthPower(AbstractDungeon.player, FINAL_STRENGTH));
    	}
    }
    
    @Override
    public int onAttacked(DamageInfo info, int damageAmount)
    {
    	if (AbstractDungeon.player.hasPower(StrengthPower.POWER_ID))
    	{
    		int playerStrength = AbstractDungeon.player.getPower(StrengthPower.POWER_ID).amount;
    		if (playerStrength != FINAL_STRENGTH)
    		{
    			StrengthPower instance = (StrengthPower) AbstractDungeon.player.getPower(StrengthPower.POWER_ID);
    			instance.amount = FINAL_STRENGTH;
    			instance.updateDescription();
    		}
    	}
    	else
    	{
    		DuelistCard.applyPowerToSelf(new StrengthPower(AbstractDungeon.player, FINAL_STRENGTH));
    	}
    	return damageAmount;
    }
   
}
