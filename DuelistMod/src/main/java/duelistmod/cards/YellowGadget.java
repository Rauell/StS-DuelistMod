package duelistmod.cards;

import java.util.ArrayList;

import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.localization.CardStrings;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import com.megacrit.cardcrawl.powers.ArtifactPower;

import duelistmod.*;
import duelistmod.actions.common.CardSelectScreenIntoHandAction;
import duelistmod.interfaces.DuelistCard;
import duelistmod.patches.AbstractCardEnum;

public class YellowGadget extends DuelistCard 
{
	// TEXT DECLARATION
	public static final String ID = DuelistMod.makeID("YellowGadget");
	private static final CardStrings cardStrings = CardCrawlGame.languagePack.getCardStrings(ID);
	public static final String IMG = DuelistMod.makeCardPath("YellowGadget.png");
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
	// /STAT DECLARATION/

	public YellowGadget() 
	{
		super(ID, NAME, IMG, COST, DESCRIPTION, TYPE, COLOR, RARITY, TARGET);
		this.tags.add(Tags.MONSTER);
		this.tags.add(Tags.GOOD_TRIB);
		this.tags.add(Tags.MACHINE);
		this.originalName = this.name;
		this.summons = this.baseSummons = 2;
		this.isSummon = true;
		this.magicNumber = this.baseMagicNumber = 3;
	}


	// Actions the card should do.
	@Override
	public void use(AbstractPlayer p, AbstractMonster m) 
	{
		summon(p, this.summons, this);
		if (!upgraded)
		{
			ArrayList<DuelistCard> tokens = CardLibrary.getTokens();
			for (int i = 0; i < this.magicNumber; i++)
			{
				DuelistCard tk = tokens.get(AbstractDungeon.cardRandomRng.random(tokens.size() - 1));
				addCardToHand((DuelistCard)tk.makeCopy());
			}
		}
		else
		{
			ArrayList<DuelistCard> tokens = CardLibrary.getTokens();
			ArrayList<AbstractCard> abTokens = new ArrayList<AbstractCard>();
			abTokens.addAll(tokens);
			AbstractDungeon.actionManager.addToTop(new CardSelectScreenIntoHandAction(false, false, this.magicNumber, abTokens));
		}
	}

	// Which card to return when making a copy of this card.
	@Override
	public AbstractCard makeCopy() 
	{
		return new YellowGadget();
	}

	// Upgraded stats.
	@Override
	public void upgrade() 
	{
		if (canUpgrade()) 
		{
			if (this.timesUpgraded > 0) { this.upgradeName(NAME + "+" + this.timesUpgraded); }
	    	else { this.upgradeName(NAME + "+"); }
			if (timesUpgraded == 1) { this.upgradeBaseCost(0); }
			else { this.upgradeMagicNumber(1); }
			this.rawDescription = UPGRADE_DESCRIPTION;
			this.initializeDescription();
		}
	}
	
	@Override
	public boolean canUpgrade()
	{
		if (this.magicNumber < 11)
		{
			return true;
		}
		else
		{
			return false;
		}
		
	}


	@Override
	public void onTribute(DuelistCard tributingCard) 
	{
		if (tributingCard.hasTag(Tags.MACHINE))
		{
			applyPowerToSelf(new ArtifactPower(player(), DuelistMod.machineArt));
		}
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