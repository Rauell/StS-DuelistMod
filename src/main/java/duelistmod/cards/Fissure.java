package duelistmod.cards;

import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.localization.CardStrings;
import com.megacrit.cardcrawl.monsters.AbstractMonster;

import duelistmod.*;
import duelistmod.abstracts.DuelistCard;
import duelistmod.patches.AbstractCardEnum;
import duelistmod.variables.*;

public class Fissure extends DuelistCard 
{
	// TEXT DECLARATION
	public static final String ID = duelistmod.DuelistMod.makeID("Fissure");
	private static final CardStrings cardStrings = CardCrawlGame.languagePack.getCardStrings(ID);
	public static final String IMG = DuelistMod.makePath(Strings.FISSURE);
	public static final String NAME = cardStrings.NAME;
	public static final String DESCRIPTION = cardStrings.DESCRIPTION;
	public static final String UPGRADE_DESCRIPTION = cardStrings.UPGRADE_DESCRIPTION;
	// /TEXT DECLARATION/

	// STAT DECLARATION
	private static final CardRarity RARITY = CardRarity.UNCOMMON;
	private static final CardTarget TARGET = CardTarget.SELF;
	private static final CardType TYPE = CardType.ATTACK;
	public static final CardColor COLOR = AbstractCardEnum.DUELIST_SPELLS;
	private static final int COST = 1;
	// /STAT DECLARATION/

	public Fissure() {
		super(ID, NAME, IMG, COST, DESCRIPTION, TYPE, COLOR, RARITY, TARGET);
		this.baseDamage = this.damage = 3;
		this.isMultiDamage = true;
		this.tags.add(Tags.SPELL);
		this.tags.add(Tags.LEGEND_BLUE_EYES);
		this.tags.add(Tags.INCREMENT_DECK);
		this.originalName = this.name;
		this.incrementDeckCopies = 1;
		this.setupStartingCopies();
	}

	// Actions the card should do.
	@Override
	public void use(AbstractPlayer p, AbstractMonster m) 
	{
		
		AbstractMonster selected = AbstractDungeon.getRandomMonster();
		if (selected != null)
		{
			int lowestHP = selected.currentHealth;
			for (AbstractMonster mon : AbstractDungeon.getMonsters().monsters)
			{
				if (!mon.isDead && !mon.isDying && !mon.halfDead && !mon.isDeadOrEscaped() && mon.currentHealth < lowestHP && mon.currentHealth != 0)
				{
					selected = mon;
					if (DuelistMod.debug && mon.name != null) { DuelistMod.logger.info("Fissure: found a new monster with lowest HP. Old lowest HP was: " + lowestHP + " -- and new HP is: " + mon.currentHealth + " -- New Selected Monster: " + mon.name); }
					lowestHP = mon.currentHealth;
				}
			}
			int playerSummons = getSummons(p);
			int newDamage = this.damage * playerSummons;
			if (DuelistMod.debug) { DuelistMod.logger.info("Fissure: damage dealt was " + newDamage + ", summons was " + playerSummons + ", this.damage was " + this.damage); }		
			this.applyPowers();
			attack(selected, this.baseAFX, newDamage);
			if (DuelistMod.debug) { DuelistMod.logger.info("Fissure (after applyPowers function and attacking): damage dealt was " + newDamage + ", summons was " + playerSummons + ", this.damage was " + this.damage); }	
		}
	}

	// Which card to return when making a copy of this card.
	@Override
	public AbstractCard makeCopy() {
		return new Fissure();
	}

	// Upgraded stats.
	@Override
	public void upgrade() {
		if (!this.upgraded) {
			this.upgradeName();
			this.upgradeDamage(1);
			this.rawDescription = UPGRADE_DESCRIPTION;
			this.initializeDescription();
		}
	}

	@Override
	public void onTribute(DuelistCard tributingCard) {
		// TODO Auto-generated method stub

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