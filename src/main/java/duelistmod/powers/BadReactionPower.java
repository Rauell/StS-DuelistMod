package duelistmod.powers;

import com.badlogic.gdx.graphics.Texture;
import com.megacrit.cardcrawl.actions.AbstractGameAction.AttackEffect;
import com.megacrit.cardcrawl.actions.common.DamageAllEnemiesAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.cards.DamageInfo.DamageType;
import com.megacrit.cardcrawl.core.*;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.localization.PowerStrings;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import com.megacrit.cardcrawl.powers.AbstractPower;

import duelistmod.*;
import duelistmod.abstracts.DuelistCard;
import duelistmod.variables.Strings;

/* 	
 * 
 * 
 */

public class BadReactionPower extends AbstractPower 
{
    public AbstractCreature source;

    public static final String POWER_ID = duelistmod.DuelistMod.makeID("BadReactionPower");
    private static final PowerStrings powerStrings = CardCrawlGame.languagePack.getPowerStrings(POWER_ID);
    public static final String NAME = powerStrings.NAME;
    public static final String[] DESCRIPTIONS = powerStrings.DESCRIPTIONS;
    public static final String IMG = DuelistMod.makePath(Strings.BAD_REACTION_POWER);
    public int DAMAGE = 1;
    public int HP_GAIN_TRIGGER = 1;

    public BadReactionPower(final AbstractCreature owner, final AbstractCreature source, boolean upgrade, int uDmg, int uHeal) 
    {
        this.name = NAME;
        this.ID = POWER_ID;
        this.owner = owner;       
        this.type = PowerType.BUFF;
        this.isTurnBased = false;
        this.img = new Texture(IMG);
        this.source = source;
        if (upgrade)
        {
        	DAMAGE += uDmg;
        	//HP_GAIN_TRIGGER += uHeal;
        }
        this.updateDescription();
    }
    
    @Override
    public void onDrawOrDiscard() 
    {
    	if (this.amount > 0) { this.amount = 0; }
    	if (AbstractDungeon.player.hasPower(SpecialBadReactionPower.POWER_ID))
    	{
    		DuelistCard.removePower(this, this.owner);
    	}
    }
    
    @Override
    public void atStartOfTurn() 
    {
    	if (this.amount > 0) { this.amount = 0; }
    	if (AbstractDungeon.player.hasPower(SpecialBadReactionPower.POWER_ID))
    	{
    		DuelistCard.removePower(this, this.owner);
    	}
    }
    
    @Override
    public void onPlayCard(AbstractCard c, AbstractMonster m) 
    {
    	if (this.amount > 0) { this.amount = 0; }
    	if (AbstractDungeon.player.hasPower(SpecialBadReactionPower.POWER_ID))
    	{
    		DuelistCard.removePower(this, this.owner);
    	}
    }
    
    @Override
	public void atEndOfTurn(final boolean isPlayer) 
	{
    	if (this.amount > 0) { this.amount = 0; }
    	if (AbstractDungeon.player.hasPower(SpecialBadReactionPower.POWER_ID))
    	{
    		DuelistCard.removePower(this, this.owner);
    	}
	}

    @Override
    public int onHeal(int healAmount)
    {
    	if (AbstractDungeon.player.hasPower(BadReactionPower.POWER_ID))
		{
			int[] damageArray = new int[] {DAMAGE, DAMAGE, DAMAGE, DAMAGE, DAMAGE, DAMAGE, DAMAGE, DAMAGE, DAMAGE, DAMAGE, DAMAGE, DAMAGE};
			for (int i = 0; i < damageArray.length; i++) { damageArray[i] = DAMAGE * healAmount; }
			AbstractDungeon.actionManager.addToTop(new DamageAllEnemiesAction(AbstractDungeon.player, damageArray, DamageType.THORNS, AttackEffect.POISON)); 
		}
    	//return healAmount;
    	return 0;
    }

    @Override
	public void updateDescription()
    {
        this.description = DESCRIPTIONS[0] + DAMAGE + DESCRIPTIONS[1] + HP_GAIN_TRIGGER + DESCRIPTIONS[2];
    }
}
