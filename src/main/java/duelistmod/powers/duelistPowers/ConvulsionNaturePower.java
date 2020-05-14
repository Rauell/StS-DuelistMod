package duelistmod.powers.duelistPowers;

import java.util.ArrayList;

import com.badlogic.gdx.graphics.Texture;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.core.*;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.localization.PowerStrings;
import com.megacrit.cardcrawl.monsters.AbstractMonster;

import duelistmod.DuelistMod;
import duelistmod.abstracts.*;
import duelistmod.variables.Tags;

public class ConvulsionNaturePower extends DuelistPower
{	
	public AbstractCreature source;

    public static final String POWER_ID = DuelistMod.makeID("ConvulsionNaturePower");
    private static final PowerStrings powerStrings = CardCrawlGame.languagePack.getPowerStrings(POWER_ID);
    public static final String NAME = powerStrings.NAME;
    public static final String[] DESCRIPTIONS = powerStrings.DESCRIPTIONS;
    //public static final String IMG = DuelistMod.makePowerPath("ConvulsionNaturePower.png");
    public static final String IMG = DuelistMod.makePowerPath("ConvulsionNaturePower.png");
    
	public ConvulsionNaturePower(int amt)
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
		ArrayList<AbstractCard> list = DuelistCard.findAllOfTypeForResummon(Tags.NATURIA, Tags.MONSTER, this.amount);
		for (AbstractCard c : list)
		{
			AbstractMonster mon = AbstractDungeon.getRandomMonster();
			if (mon != null) {
				DuelistCard.resummon(c, mon);
			}
		}
	}

	@Override
	public void updateDescription()
	{
		if (this.amount == 1) { this.description = DESCRIPTIONS[0] + this.amount + DESCRIPTIONS[1]; }
		else { this.description = DESCRIPTIONS[0] + this.amount + DESCRIPTIONS[2]; }
		
	}
}
