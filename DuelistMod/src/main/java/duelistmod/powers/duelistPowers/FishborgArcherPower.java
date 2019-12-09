package duelistmod.powers.duelistPowers;

import com.badlogic.gdx.graphics.Texture;
import com.megacrit.cardcrawl.core.*;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.localization.PowerStrings;

import duelistmod.DuelistMod;
import duelistmod.abstracts.NoStackDuelistPower;
import duelistmod.actions.unique.FishborgArcherAction;

public class FishborgArcherPower extends NoStackDuelistPower
{	
	public AbstractCreature source;

    public static final String POWER_ID = DuelistMod.makeID("FishborgArcherPower");
    private static final PowerStrings powerStrings = CardCrawlGame.languagePack.getPowerStrings(POWER_ID);
    public static final String NAME = powerStrings.NAME;
    public static final String[] DESCRIPTIONS = powerStrings.DESCRIPTIONS;
    public static final String IMG = DuelistMod.makePowerPath("PlaceholderPower.png");
	
	public FishborgArcherPower() 
	{ 
		this(AbstractDungeon.player, AbstractDungeon.player);
	}
	
	public FishborgArcherPower(AbstractCreature owner, AbstractCreature source) 
	{ 
		//super(owner, source, stacks);
		this.name = NAME;
        this.ID = POWER_ID;
        this.owner = owner;        
        this.type = PowerType.BUFF;
        this.isTurnBased = false;
        this.canGoNegative = false;
        this.img = new Texture(IMG);
        this.source = source;
		updateDescription();
	}
	
	@Override
	public void atEndOfTurn(final boolean isPlayer) 
	{
		this.addToTop(new FishborgArcherAction(this));
	}

	@Override
	public void updateDescription()
	{
		this.description = DESCRIPTIONS[0];
	}
}
