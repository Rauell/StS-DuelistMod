package duelistmod.cards.other.tempCards;

import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.localization.CardStrings;
import duelistmod.DuelistMod;
import duelistmod.abstracts.CardPoolOptionTypeCard;
import duelistmod.characters.TheDuelist;
import duelistmod.helpers.poolhelpers.BeastPool;
import duelistmod.patches.AbstractCardEnum;
import duelistmod.relics.CardPoolRelic;
import duelistmod.variables.Strings;

public class CardPoolOptionBeast extends CardPoolOptionTypeCard {
    public static final String ID = DuelistMod.makeID("CardPoolOptionBeast");
    private static final CardStrings cardStrings = CardCrawlGame.languagePack.getCardStrings(ID);
    public static final String IMG = DuelistMod.makePath(Strings.GENERIC_TOKEN);
    public static final String NAME = cardStrings.NAME;
    public static final String DESCRIPTION = cardStrings.DESCRIPTION;
    public static final String UPGRADE_DESCRIPTION = cardStrings.UPGRADE_DESCRIPTION;

    private static final CardRarity RARITY = CardRarity.SPECIAL;
    private static final CardTarget TARGET = CardTarget.NONE;
    private static final CardType TYPE = CardType.SKILL;
    public 	static final CardColor COLOR = AbstractCardEnum.DUELIST;
    private static final int COST = -2;


    public CardPoolOptionBeast() {
    	super(ID, NAME, IMG, COST, DESCRIPTION, TYPE, COLOR, RARITY, TARGET); 
    	this.dontTriggerOnUseCard = true;
    	this.canAdd = !DuelistMod.addedBeastSet;
    }
   
    // Call this when player selects card from Options Relic
    @Override
    public void loadPool()
    {
    	DuelistMod.coloredCards.clear();
		DuelistMod.toReplacePoolWith.clear();
		DuelistMod.toReplacePoolWith.addAll(BeastPool.deck());
		DuelistMod.toReplacePoolWith.addAll(TheDuelist.cardPool.group);
		DuelistMod.addedBeastSet = true;
		DuelistMod.shouldReplacePool = true;
		DuelistMod.relicReplacement = true;
		if (AbstractDungeon.player.hasRelic(CardPoolRelic.ID)) { ((CardPoolRelic)AbstractDungeon.player.getRelic(CardPoolRelic.ID)).setDescription(); }
		CardCrawlGame.dungeon.initializeCardPools();
    }

    @Override public AbstractCard makeCopy() { return new CardPoolOptionBeast(); }
}