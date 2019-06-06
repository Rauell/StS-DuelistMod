package duelistmod.cards;

import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.localization.CardStrings;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import com.megacrit.cardcrawl.orbs.AbstractOrb;

import duelistmod.*;
import duelistmod.interfaces.DuelistCard;
import duelistmod.orbs.Air;
import duelistmod.patches.AbstractCardEnum;
import duelistmod.powers.*;

public class ToonDarkMagicianGirl extends DuelistCard 
{
	// TEXT DECLARATION
	public static final String ID = duelistmod.DuelistMod.makeID("ToonDarkMagicianGirl");
	private static final CardStrings cardStrings = CardCrawlGame.languagePack.getCardStrings(ID);
	public static final String IMG = DuelistMod.makePath(Strings.TOON_DARK_MAGICIAN_GIRL);
	public static final String NAME = cardStrings.NAME;
	public static final String DESCRIPTION = cardStrings.DESCRIPTION;
	public static final String UPGRADE_DESCRIPTION = cardStrings.UPGRADE_DESCRIPTION;
	// /TEXT DECLARATION/

	// STAT DECLARATION
	private static final CardRarity RARITY = CardRarity.UNCOMMON;
	private static final CardTarget TARGET = CardTarget.NONE;
	private static final CardType TYPE = CardType.SKILL;
	public static final CardColor COLOR = AbstractCardEnum.DUELIST_MONSTERS;
	private static final int COST = 1;
	private static final int SUMMONS = 2;
	private static final int OVERFLOW_AMT = 3;
	//private static final int U_OVERFLOW = 2;
	//private static int MIN_TURNS_ROLL = 4;
	//private static int MAX_TURNS_ROLL = 8;
	// /STAT DECLARATION/

	public ToonDarkMagicianGirl() {
		super(ID, NAME, IMG, COST, DESCRIPTION, TYPE, COLOR, RARITY, TARGET);
		this.magicNumber = this.baseMagicNumber = OVERFLOW_AMT;
		this.toon = true;
		this.tags.add(Tags.MONSTER);
		this.tags.add(Tags.TOON);
		this.tags.add(Tags.SPELLCASTER);
		this.originalName = this.name;
		this.summons = this.baseSummons = SUMMONS;
		this.isSummon = true;
		this.block = this.baseBlock = 10;
	}

	// Actions the card should do.
	@Override
	public void use(AbstractPlayer p, AbstractMonster m) 
	{
		summon(p, this.summons, this);
		block(this.block);
		AbstractOrb air = new Air();
		channel(air);
	}

	@Override
	public void triggerOnEndOfPlayerTurn() 
	{
		/*
		// If overflows remaining
		if (this.magicNumber > 0) 
		{
			// Get player reference
			AbstractPlayer p = AbstractDungeon.player;

			// Remove 1 overflow
			AbstractDungeon.actionManager.addToBottom(new ModifyMagicNumberAction(this, -1));

			// Get random number of turns for buff to apply for
			int randomTurnNum = AbstractDungeon.cardRandomRng.random(MIN_TURNS_ROLL, MAX_TURNS_ROLL);

			// Get random buff with random tumber of turns
			applyRandomBuffPlayer(p, randomTurnNum, false);

		}
		*/
	}

	// Which card to return when making a copy of this card.
	@Override
	public AbstractCard makeCopy() {
		return new ToonDarkMagicianGirl();
	}

	// Upgraded stats.
	@Override
	public void upgrade() {
		if (!this.upgraded) {
			this.upgradeName();
			//this.upgradeMagicNumber(U_OVERFLOW);
			this.upgradeBlock(5);
			this.rawDescription = UPGRADE_DESCRIPTION;
			this.initializeDescription();
		}
	}

    // Checking for Monster Zones if the challenge is enabled
    @Override
    public boolean canUse(AbstractPlayer p, AbstractMonster m)
    {
    	// Check super canUse()
    	boolean canUse = super.canUse(p, m); 
    	if (!canUse) { return false; }

    	if (Utilities.isCustomModActive("theDuelist:SummonersChallenge") || DuelistMod.challengeMode)
    	{
    		if ((DuelistMod.getChallengeDiffIndex() < 3) && this.misc == 52) { return true; }
    		if (p.hasPower(SummonPower.POWER_ID))
    		{
    			int sums = DuelistCard.getSummons(p); int max = DuelistCard.getMaxSummons(p);
    			if (sums + this.summons <= max) 
    			{ 
    				// Pumpking & Princess
            		if (this.misc == 52) { return true; }
            		
    				// Toon World
    		    	if (p.hasPower(ToonWorldPower.POWER_ID) || p.hasPower(ToonKingdomPower.POWER_ID)) { return true; }
    		    	this.cantUseMessage = DuelistMod.toonWorldString; return false; 
    			}
    			else 
    			{ 
    				if (sums < max) 
    				{ 
    					if (max - sums > 1) { this.cantUseMessage = "You only have " + (max - sums) + " monster zones"; }
    					else { this.cantUseMessage = "You only have " + (max - sums) + " monster zone"; }
    					
    				}
    				else { this.cantUseMessage = "No monster zones remaining"; }
    				return false; 
    			}
    		}
    		else
    		{
    			// Pumpking & Princess
        		if (this.misc == 52) { return true; }
        		
    			// Toon World
    	    	if (p.hasPower(ToonWorldPower.POWER_ID) || p.hasPower(ToonKingdomPower.POWER_ID)) { return true; }
    	    	this.cantUseMessage = DuelistMod.toonWorldString; return false;
    		}
    	}
    	
    	else
    	{
    		// Pumpking & Princess
    		if (this.misc == 52) { return true; }
    		
    		// Toon World
        	if (p.hasPower(ToonWorldPower.POWER_ID) || p.hasPower(ToonKingdomPower.POWER_ID)) { return true; }
        	this.cantUseMessage = DuelistMod.toonWorldString; return false;
    	}
    }

	@Override
	public void onTribute(DuelistCard tributingCard) 
	{
		toonSynTrib(tributingCard);
		spellcasterSynTrib(tributingCard);
	}


	@Override
	public void onResummon(int summons) {
		// TODO Auto-generated method stub

	}

	@Override
	public void summonThis(int summons, DuelistCard c, int var)
	{
		
	}

	@Override
	public void summonThis(int summons, DuelistCard c, int var, AbstractMonster m) {
		
		
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