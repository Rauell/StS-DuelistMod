package duelistmod.cards.pools.dragons;

import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.localization.CardStrings;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import com.megacrit.cardcrawl.powers.*;

import duelistmod.DuelistMod;
import duelistmod.abstracts.DuelistCard;
import duelistmod.helpers.Util;
import duelistmod.patches.AbstractCardEnum;
import duelistmod.powers.*;
import duelistmod.powers.duelistPowers.*;
import duelistmod.variables.Tags;

public class Berserking extends DuelistCard 
{
    // TEXT DECLARATION
    public static final String ID = DuelistMod.makeID("Berserking");
    private static final CardStrings cardStrings = CardCrawlGame.languagePack.getCardStrings(ID);
    public static final String IMG = DuelistMod.makeCardPath("Berserking.png");
    public static final String NAME = cardStrings.NAME;
    public static final String DESCRIPTION = cardStrings.DESCRIPTION;
    public static final String UPGRADE_DESCRIPTION = cardStrings.UPGRADE_DESCRIPTION;
    // /TEXT DECLARATION/

    // STAT DECLARATION
    private static final CardRarity RARITY = CardRarity.UNCOMMON;
    private static final CardTarget TARGET = CardTarget.SELF;
    private static final CardType TYPE = CardType.POWER;
    public static final CardColor COLOR = AbstractCardEnum.DUELIST_TRAPS;
    private static final int COST = 1;
    // /STAT DECLARATION/

    public Berserking() {
        super(ID, NAME, IMG, COST, DESCRIPTION, TYPE, COLOR, RARITY, TARGET);
        this.baseMagicNumber = this.magicNumber = 1;
        this.tags.add(Tags.TRAP);
        this.tags.add(Tags.NO_METRONOME);
        this.misc = 0;
        this.originalName = this.name;
    }

    // Actions the card should do.
    @Override
    public void use(AbstractPlayer p, AbstractMonster m) 
    {
    	if (p.hasPower(Dragonscales.POWER_ID)) { applyPowerToSelf(new Dragonscales(5 * p.getPower(Dragonscales.POWER_ID).amount)); }
    	if (p.hasPower(StrengthPower.POWER_ID)) { applyPowerToSelf(new Dragonscales(2 * p.getPower(StrengthPower.POWER_ID).amount)); }
    	applyPowerToSelf(new PoisonPower(p, p, 333));
    	applyPowerToSelf(new PoisonPower(p, p, 333));
    	applyPowerToSelf(new PoisonPower(p, p, 333));
    	applyPowerToSelf(new BeserkingDebuffA(333));
    	applyPowerToSelf(new BeserkingDebuffA(333));
    	applyPowerToSelf(new BeserkingDebuffA(333));
    	applyPowerToSelf(new BeserkingDebuffB());
    	applyPowerToSelf(new BeserkingDebuffB());
    	applyPowerToSelf(new BeserkingDebuffB());
    	applyPowerToSelf(new BeserkingDebuffC());
    }

    // Which card to return when making a copy of this card.
    @Override
    public AbstractCard makeCopy() {
        return new Berserking();
    }

    // Upgraded stats.
    @Override
    public void upgrade() {
        if (!this.upgraded) {
            if (this.timesUpgraded > 0) { this.upgradeName(NAME + "+" + this.timesUpgraded); }
	    	else { this.upgradeName(NAME + "+"); }
            this.upgradeBaseCost(0);
            this.rawDescription = UPGRADE_DESCRIPTION;
            this.initializeDescription(); 
        }
    }
    
    // Tribute canUse()
    @Override
    public boolean canUse(AbstractPlayer p, AbstractMonster m)
    {
    	if (this.specialCanUseLogic)
    	{
    		if (this.useTributeCanUse)
    		{
    			// Check super canUse()
    	    	boolean canUse = super.canUse(p, m); 
    	    	if (!canUse) { return false; }
    	    	
    	    	// Pumpking & Princess
    	  		else if (this.misc == 52) { return true; }
    	    	
    	    	// Mausoleum check
    	    	else if (p.hasPower(EmperorPower.POWER_ID))
    			{
    				EmperorPower empInstance = (EmperorPower)p.getPower(EmperorPower.POWER_ID);
    				if (!empInstance.flag)
    				{
    					return true;
    				}
    				
    				else
    				{
    					if (p.hasPower(SummonPower.POWER_ID)) { int temp = (p.getPower(SummonPower.POWER_ID).amount); if (temp >= this.tributes) { return true; } }
    				}
    			}
    	    	
    	    	// Check for # of summons >= tributes
    	    	else { if (p.hasPower(SummonPower.POWER_ID)) { int temp = (p.getPower(SummonPower.POWER_ID).amount); if (temp >= this.tributes) { return true; } } }
    	    	
    	    	// Player doesn't have something required at this point
    	    	this.cantUseMessage = this.tribString;
    	    	return false;
    		}
    		else if (this.useBothCanUse)
    		{
    			// Check for monster zones challenge
    	    	if (Util.isCustomModActive("theDuelist:SummonersChallenge") || DuelistMod.challengeLevel20)
    	    	{
    	    		if ((DuelistMod.getChallengeDiffIndex() < 3) && this.misc == 52) { return true; }
    	    		// Check for energy and other normal game checks
    	    		boolean canUse = super.canUse(p, m); 
    	        	if (!canUse) { return false; }
    	        	
    	        	// Mausoleum check
    		    	else if (p.hasPower(EmperorPower.POWER_ID))
    				{
    		    		// If mausoleum is active skip tribute check and just check monster zones for space
    					EmperorPower empInstance = (EmperorPower)p.getPower(EmperorPower.POWER_ID);
    					if (!empInstance.flag)
    					{
    						if (p.hasPower(SummonPower.POWER_ID))
    			    		{
    			    			int sums = DuelistCard.getSummons(p); int max = DuelistCard.getMaxSummons(p);
    			    			if (sums + this.summons <= max) 
    			    			{ 
    			    				return true; 
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
    			    			return true;
    			    		}
    					}
    					
    					// If no mausoleum active, check tributes and then check summons
    					else
    					{
    						if (p.hasPower(SummonPower.POWER_ID))
    			    		{ 
    			    			int sums = DuelistCard.getSummons(p); 
    			    			if (sums >= this.tributes) 
    			    			{ 
    			    				int max = DuelistCard.getMaxSummons(p);
    			    				if (sums - tributes < 0) { return true; }
    			    				else 
    			    				{ 
    			    					sums -= this.tributes;
    					    			if (sums + this.summons <= max) 
    					    			{ 
    					    				return true; 
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
    									
    			    			} 
    			    		} 
    					}
    				}
    		    	
    		    	// No mausoleum power - so just check for number of tributes and summon slots
    		    	else 
    		    	{ 
    		    		if (p.hasPower(SummonPower.POWER_ID))
    		    		{ 
    		    			int sums = DuelistCard.getSummons(p); 
    		    			if (sums >= this.tributes) 
    		    			{ 
    		    				int max = DuelistCard.getMaxSummons(p);
    		    				if (sums - tributes < 0) { return true; }
    		    				else 
    		    				{ 
    		    					sums -= this.tributes;
    				    			if (sums + this.summons <= max) 
    				    			{ 
    				    				return true; 
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
    								
    		    			} 
    		    		} 
    		    	}
    		    	
    		    	// Player doesn't have something required at this point
    		    	this.cantUseMessage = this.tribString;
    		    	return false;
    	        	
    	    	}
    	    	
    	    	// Default behavior - no monster zone challenge
    	    	else
    	    	{
    	    		boolean canUse = super.canUse(p, m); 
    	        	if (!canUse) { return false; }
    		  		// Pumpking & Princess
    		  		else if (this.misc == 52) { return true; }
    		    	
    		  		// Mausoleum check
    		    	else if (p.hasPower(EmperorPower.POWER_ID))
    				{
    					EmperorPower empInstance = (EmperorPower)p.getPower(EmperorPower.POWER_ID);
    					if (!empInstance.flag)
    					{
    						return true;
    					}
    					
    					else
    					{
    						if (p.hasPower(SummonPower.POWER_ID)) { int temp = (p.getPower(SummonPower.POWER_ID).amount); if (temp >= this.tributes) { return true; } }
    					}
    				}
    		    	
    		    	// Check for # of summons >= tributes
    		    	else { if (p.hasPower(SummonPower.POWER_ID)) { int temp = (p.getPower(SummonPower.POWER_ID).amount); if (temp >= this.tributes) { return true; } } }
    		    	
    		    	// Player doesn't have something required at this point
    		    	this.cantUseMessage = this.tribString;
    		    	return false;
    	    	}
    		}
    		else
    		{
    			// Check super canUse()
    	    	boolean canUse = super.canUse(p, m); 
    	    	if (!canUse) { return false; }

    	    	if (Util.isCustomModActive("theDuelist:SummonersChallenge") || DuelistMod.challengeLevel20)
    	    	{
    	    		if ((DuelistMod.getChallengeDiffIndex() < 3) && this.misc == 52) { return true; }
    	    		if (p.hasPower(SummonPower.POWER_ID))
    	    		{
    	    			int sums = DuelistCard.getSummons(p); int max = DuelistCard.getMaxSummons(p);
    	    			if (sums + this.summons <= max) 
    	    			{ 
    	    				return true; 
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
    	    			return true;
    	    		}
    	    	}
    	    	
    	    	else
    	    	{
    	    		return true;
    	    	}
    		}
    	}
    	else
    	{
    		return super.canUse(p, m);
    	}
    }

	@Override
	public void onTribute(DuelistCard tributingCard)
	{
		
	}

	

	@Override
	public void onResummon(int summons) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void summonThis(int summons, DuelistCard c, int var) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void summonThis(int summons, DuelistCard c, int var, AbstractMonster m) {
		// TODO Auto-generated method stub
		
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