package duelistmod.cards;

import com.megacrit.cardcrawl.actions.defect.RemoveNextOrbAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.localization.CardStrings;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import com.megacrit.cardcrawl.orbs.AbstractOrb;

import duelistmod.*;
import duelistmod.orbs.Glitch;
import duelistmod.patches.*;

public class MachineFactory extends DuelistCard 
{
    // TEXT DECLARATION
    public static final String ID = DuelistMod.makeID("MachineFactory");
    private static final CardStrings cardStrings = CardCrawlGame.languagePack.getCardStrings(ID);
    public static final String IMG = DuelistMod.makePath(Strings.MACHINE_FACTORY);
    public static final String NAME = cardStrings.NAME;
    public static final String DESCRIPTION = cardStrings.DESCRIPTION;
    public static final String UPGRADE_DESCRIPTION = cardStrings.UPGRADE_DESCRIPTION;
    // /TEXT DECLARATION/
    
    // STAT DECLARATION
    private static final CardRarity RARITY = CardRarity.UNCOMMON;
    private static final CardTarget TARGET = CardTarget.NONE;
    private static final CardType TYPE = CardType.SKILL;
    public static final CardColor COLOR = AbstractCardEnum.DUELIST_SPELLS;
    private static final int COST = 1;
    // /STAT DECLARATION/

    public MachineFactory() {
        super(ID, NAME, IMG, COST, DESCRIPTION, TYPE, COLOR, RARITY, TARGET);
        this.tags.add(Tags.SPELL);
        this.tags.add(Tags.ALL);
        this.tags.add(Tags.LEGEND_BLUE_EYES);
        this.misc = 0;
		this.originalName = this.name;
		this.exhaust = true;
    }

    // Actions the card should do.
    @Override
    public void use(AbstractPlayer p, AbstractMonster m) 
    {
    	int orbCount = 0;
    	int initCount = 0;
    	orbCount = p.filledOrbCount();
    	initCount = orbCount;
    	while (orbCount > 0)
    	{
    		AbstractDungeon.actionManager.addToTop(new RemoveNextOrbAction());
    		orbCount--;
    	}
    	if (!upgraded)
    	{
	    	for (int i = 0; i < initCount; i++)
	    	{
	    		AbstractOrb glitch = new Glitch();
	    		channelBottom(glitch);
	    	}
    	}
    	
    	else
    	{
    		evokeAll();
    		for (int i = 0; i < initCount; i++)
	    	{
    			AbstractOrb glitch = new Glitch();
	    		channelBottom(glitch);
	    	}
    	}
    	
    }

    // Which card to return when making a copy of this card.
    @Override
    public AbstractCard makeCopy() {
        return new MachineFactory();
    }

    // Upgraded stats.
    @Override
    public void upgrade() {
        if (!this.upgraded) {
            this.upgradeName();
            this.rawDescription = UPGRADE_DESCRIPTION;
            this.initializeDescription();
        }
    }

	@Override
	public void onTribute(DuelistCard tributingCard) 
	{
		
		
	}


	@Override
	public void onResummon(int summons) 
	{
		
	}

	@Override
	public void summonThis(int summons, DuelistCard c, int var) 
	{
		
		
	}

	@Override
	public void summonThis(int summons, DuelistCard c, int var, AbstractMonster m) 
	{
		
		
	}

	@Override
	public String getID() {
		return ID;
	}

	@Override
	public void optionSelected(AbstractPlayer arg0, AbstractMonster arg1, int arg2) {
		// TODO Auto-generated method stub
		
	}
}