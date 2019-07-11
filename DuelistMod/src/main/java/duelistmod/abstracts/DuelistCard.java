package duelistmod.abstracts;

import java.lang.reflect.Type;
import java.util.*;
import java.util.Map.Entry;
import java.util.concurrent.ThreadLocalRandom;

import com.evacipated.cardcrawl.mod.stslib.actions.common.FetchAction;
import com.evacipated.cardcrawl.mod.stslib.actions.tempHp.AddTemporaryHPAction;
import com.evacipated.cardcrawl.mod.stslib.fields.cards.AbstractCard.*;
import com.evacipated.cardcrawl.mod.stslib.powers.abstracts.TwoAmountPower;
import com.evacipated.cardcrawl.modthespire.Loader;
import com.evacipated.cardcrawl.modthespire.lib.SpireConfig;
import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.actions.AbstractGameAction.AttackEffect;
import com.megacrit.cardcrawl.actions.animations.TalkAction;
import com.megacrit.cardcrawl.actions.common.*;
import com.megacrit.cardcrawl.actions.defect.*;
import com.megacrit.cardcrawl.cards.*;
import com.megacrit.cardcrawl.cards.DamageInfo.DamageType;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.core.*;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.helpers.GetAllInBattleInstances;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import com.megacrit.cardcrawl.orbs.*;
import com.megacrit.cardcrawl.potions.AbstractPotion;
import com.megacrit.cardcrawl.powers.*;
import com.megacrit.cardcrawl.relics.*;
import com.megacrit.cardcrawl.ui.panels.EnergyPanel;
import com.megacrit.cardcrawl.vfx.cardManip.*;

import basemod.BaseMod;
import basemod.abstracts.*;
import basemod.helpers.*;
import duelistmod.*;
import duelistmod.actions.common.*;
import duelistmod.cards.*;
import duelistmod.cards.curses.*;
import duelistmod.cards.incomplete.RainbowGravity;
import duelistmod.cards.tokens.*;
import duelistmod.cards.typecards.*;
import duelistmod.characters.FakePlayer;
import duelistmod.helpers.*;
import duelistmod.interfaces.*;
import duelistmod.orbs.*;
import duelistmod.patches.TheDuelistEnum;
import duelistmod.powers.*;
import duelistmod.relics.*;
import duelistmod.variables.*;

public abstract class DuelistCard extends CustomCard implements ModalChoice.Callback, CustomSavable <String>
{
	
	/*
	 * CONTENTS
	 * 
	 * Card Fields						// Fields for Duelist Cards
	 * Static Setup						// Hack together the orb list before the dungeon is loaded, orb list is for orb modal so every card can open random orb choices dynamically
	 * Abstract Methods					// Abstracts for Duelist Cards
	 * Constructors						// Create new Duelist Cards via constructor
	 * Super Override Functions			// Override AbstractCard and CustomCard methods
	 * Duelist Functions				// Special functions for Duelist Cards
	 * Attack Functions					// Functions that run attack and damage actions
	 * Defend Functions					// Functions that run blocking actions
	 * Power Functions					// Functions that apply, remove and modify powers for players & enemies
	 * Misc Action Functions			// Functions that perform various other actions
	 * Summon Monster Functions			// Functions that run when playing monsters that Summon
	 * Tribute Monster Functions		// Functions that run when playing monsters that Tribute
	 * Tribute Synergy Functions		// Functions that run when tributing monsters for the same type of monster (eg strength gain on dragon for dragon tributes)
	 * Increment Functions				// Functions that run when playing cards that Increment
	 * Resummon Functions				// Functions that run when playing cards that Resummon
	 * Summon Modification Functions	// For modifying the number of summons on cards
	 * Tribute Modification Functions	// For modifying the number of tributes on cards
	 * Card Modal Functions				// For opening and playing random cards from modal choice builder
	 * Orb Modal Functions				// For opening and playing random cards from modal choice builder. Specifically for use with orb cards only
	 * Orb Functions					// For channeling, evoke, invert actions
	 * Random Card Functions			// For generating random Duelist Cards (pulls cards from DefaultMod.myCards to allow card removal options to function with randomization, and other customization of how random-generation of cards is handled)
	 * Type Card Functions				// For generating selections of monster types, and modifying the function of those chosen types (ala Shard of Greed, Winged Kuriboh Lv9, etc.)
	 * Debug Print Functions			// Functions that generate some sort of helpful debug log to print
	 * 
	 */
	
	// =============== CARD FIELDS =========================================================================================================================================================
	public AttackEffect baseAFX = AttackEffect.SLASH_HORIZONTAL;
	public ArrayList<Integer> startCopies = new ArrayList<Integer>();
	public ArrayList<Integer> saveTest = new ArrayList<Integer>();
	public ArrayList<String> savedTypeMods = new ArrayList<String>();
	public ArrayList<AbstractCard> saveTestCard = new ArrayList<AbstractCard>();
	public static ArrayList<DuelistCard> allowedCardChoices = new ArrayList<DuelistCard>();
	private static ArrayList<AbstractOrb> allowedOrbs = new ArrayList<AbstractOrb>();
	private static ArrayList<AbstractOrb> allOrbs = new ArrayList<AbstractOrb>();
	private static Map<String, AbstractOrb> orbMap = new HashMap<String, AbstractOrb>();
	private ModalChoice orbModal;	
	private ModalChoice cardModal;
	private DuelistModalChoice duelistCardModal;
	public static final String UPGRADE_DESCRIPTION = "";
	public String upgradeType;
	public String exodiaName = "None";
	public String originalName;
	public String tribString = DuelistMod.tribString;
	public String originalDescription = "Uh-oh. This card had its summons or tributes modified and somehow lost the original description! Go yell at Nyoxide.";
	public boolean toon = false;
	public boolean isSummon = false;
	public boolean isTribute = false;
	public boolean isCastle = false;
	public boolean isTributesModified = false;
	public boolean isTributesModifiedForTurn = false;
	public boolean isTribModPerm = false;
	public boolean isSummonsModified = false;
	public boolean isSummonsModifiedForTurn = false;
	public boolean isSummonModPerm = false;
	public boolean isTypeAddedPerm = false;
	public boolean isSecondMagicModified = false;
	public boolean upgradedSecondMagic = false;
	public boolean upgradedTributes = false;
	public boolean upgradedSummons = false;
	public boolean inDuelistBottle = false;
	public boolean loadedTribOrSummonChange = false;
	public boolean fiendDeckDmgMod = false;
	public int secondMagic = 0;
	public int baseSecondMagic = 0;
	public int summons = 0;
	public int tributes = 0;
	public int baseSummons = 0;
	public int baseTributes = 0;
	public int tributesForTurn = 0;
	public int summonsForTurn = 0;
	public int permTribChange = 0;
	public int permSummonChange = 0;
	public int poisonAmt;
	public int upgradeDmg;
	public int upgradeBlk;
	public int upgradeSummons;
	public int playCount;
	public int decSummons;
	public int dex;
	public int dmgHolder = -1;
	public int damageA;
	public int damageB;
	public int damageC;
	public int damageD;
	public int originalDamage = -1;
	public int originalBlock = -1;
	public int standardDeckCopies = 1;
	public int dragonDeckCopies = 1;
	public int spellcasterDeckCopies = 1;
	public int natureDeckCopies = 1;
	public int creatorDeckCopies = 1;
	public int toonDeckCopies = 1;
	public int orbDeckCopies = 1;
	public int resummonDeckCopies = 1;
	public int generationDeckCopies = 1;
	public int ojamaDeckCopies = 1;
	public int healDeckCopies = 1;
	public int incrementDeckCopies = 1;
	public int exodiaDeckCopies = 1;
	public int superheavyDeckCopies = 1;
	public int aquaDeckCopies = 1;
	public int machineDeckCopies = 1;
	public int zombieDeckCopies = 1;
	public int fiendDeckCopies = 1;
	public double dynDmg = 0;
	
	public int startingOriginalDeckCopies = 1;
	public int startingOPDragDeckCopies = 1;
	public int startingOPSPDeckCopies = 1;
	public int startingOPNDeckCopies = 1;
	public int startingOPRDeckCopies = 1;
	public int startingOPHDeckCopies = 1;
	public int startingOPODeckCopies = 1;
	// =============== /CARD FIELDS/ =======================================================================================================================================================
	
	
	
	// =============== STATIC SETUP =========================================================================================================================================================
	static
    {
        AbstractPlayer realPlayer = AbstractDungeon.player;
        AbstractDungeon.player = new FakePlayer();
        allOrbs.addAll(returnRandomOrbList());
        allowedOrbs.addAll(allOrbs);
        for (AbstractOrb o : allOrbs) { orbMap.put(o.name, o); }
        resetInvertStringMap();
        AbstractDungeon.player = realPlayer;
    }
	// =============== /STATIC SETUP/ =======================================================================================================================================================
	
	
	
	// =============== ABSTRACT METHODS =========================================================================================================================================================
	public abstract String getID();
	public abstract void onTribute(DuelistCard tributingCard);		/* DEPRECATED - Implement customOnTribute() to run special tributing functions on cards, monster types are handled automatically */
	public abstract void onResummon(int summons);
	public abstract void summonThis(int summons, DuelistCard c, int var);
	public abstract void summonThis(int summons, DuelistCard c, int var, AbstractMonster m);
	// =============== /ABSTRACT METHODS/ =======================================================================================================================================================
	
	
	
	// =============== CONSTRUCTORS =========================================================================================================================================================
	public DuelistCard(String ID, String NAME, String IMG, int COST, String DESCRIPTION, CardType TYPE, CardColor COLOR, CardRarity RARITY, CardTarget TARGET)
	{
		super(ID, NAME, IMG, COST, DESCRIPTION, TYPE, COLOR, RARITY, TARGET);
		this.originalName = NAME;
		this.misc = 0;
		this.baseDamage = this.damage = 0;
		this.originalDescription = DESCRIPTION;
		this.savedTypeMods.add("default");
		setupStartingCopies();
		ModalChoiceBuilder builder = new ModalChoiceBuilder().setCallback(this).setColor(COLOR).setType(CardType.SKILL).setTitle("Choose an Orb to Channel");
		for (AbstractOrb orb : allowedOrbs) { if (DuelistMod.orbCardMap.get(orb.name) != null) { builder.addOption(DuelistMod.orbCardMap.get(orb.name)); }}
		orbModal = builder.create();
		
		ModalChoiceBuilder cardBuilder = new ModalChoiceBuilder().setCallback(this).setColor(COLOR).setType(CardType.SKILL).setTitle("Choose a Card to Play");
		for (DuelistCard c : allowedCardChoices) { cardBuilder.addOption(c); }
		cardModal = cardBuilder.create();
		
		DuelistModalChoiceBuilder duelistCardBuilder = new DuelistModalChoiceBuilder().setCallback(this).setColor(COLOR).setType(CardType.SKILL).setTitle("Choose a Card to Play");
		for (DuelistCard c : allowedCardChoices) { duelistCardBuilder.addOption(c); }
		duelistCardModal = duelistCardBuilder.create();
	}
	
	// =============== /CONSTRUCTORS/ =======================================================================================================================================================
	
	// =============== SUPER OVERRIDE FUNCTIONS =========================================================================================================================================================
	@Override
	public float calculateModifiedCardDamage(AbstractPlayer player, AbstractMonster mo, float tmp)
	{
		if (this.hasTag(Tags.DRAGON) && player().hasPower(MountainPower.POWER_ID)) { tmp = (int) Math.floor(tmp * 1.5);  }
		if (this.hasTag(Tags.SPELLCASTER) && player().hasPower(YamiPower.POWER_ID)) {  tmp = (int) Math.floor(tmp * 1.5);   }
		if ((this.hasTag(Tags.INSECT) || this.hasTag(Tags.PLANT)) && player().hasPower(VioletCrystalPower.POWER_ID)) { tmp = (int) Math.floor(tmp * 1.5);  }
		if (this.hasTag(Tags.NATURIA) && player().hasPower(SacredTreePower.POWER_ID)) {  tmp = (int) Math.floor(tmp * 1.5);   }
		if (this.hasTag(Tags.AQUA) && player().hasPower(UmiPower.POWER_ID)) {  tmp = (int) Math.floor(tmp * 1.5);   }
		if (this.hasTag(Tags.AQUA) && player().hasPower(SpikedGillmanPower.POWER_ID)) { tmp += player().getPower(SpikedGillmanPower.POWER_ID).amount;  }
		if (this.hasTag(Tags.ZOMBIE) || this.hasTag(Tags.FIEND)) { if (player().hasPower(GatesDarkPower.POWER_ID)) { tmp = (int) Math.floor(tmp * 2);  }}
		if (this.hasTag(Tags.DRAGON) && player().hasPower(TyrantWingPower.POWER_ID)) { tmp += player().getPower(TyrantWingPower.POWER_ID).amount;  }
		
		//if (DuelistMod.debug) { DuelistMod.logger.info("Updated damage for " + this.originalName + " based on power effects. New damage should read as: " + tmp);}
		return tmp;
	}
	
	@Override
	public AbstractCard makeStatEquivalentCopy()
	{
		AbstractCard card = super.makeStatEquivalentCopy();
		if (card instanceof DuelistCard)
		{
			DuelistCard dCard = (DuelistCard)card;
			dCard.tributes = this.tributes;
			dCard.summons = this.summons;
			dCard.isTributesModified = this.isTributesModified;
			dCard.isSummonsModified = this.isSummonsModified;
			dCard.isTributesModifiedForTurn = this.isTributesModifiedForTurn;
			dCard.isSummonsModifiedForTurn = this.isSummonsModifiedForTurn;
			dCard.inDuelistBottle = this.inDuelistBottle;
			dCard.baseTributes = this.baseTributes;
			dCard.baseSummons = this.baseSummons;
			dCard.isSummonModPerm = this.isSummonModPerm;
			dCard.isTribModPerm = this.isTribModPerm;
			dCard.exhaust = this.exhaust;
			dCard.originalDescription = this.originalDescription;
			ArrayList<CardTags> monsterTags = getAllMonsterTypes(this);
			dCard.tags.addAll(monsterTags);
			dCard.savedTypeMods = this.savedTypeMods;
			//dCard.baseDamage = this.baseDamage;
			if (this.hasTag(Tags.MEGATYPED))
			{
				dCard.tags.add(Tags.MEGATYPED);
			}
		}
		return card;
	}
	
	@Override
	public void resetAttributes()
	{
		super.resetAttributes();
	}
	
	@Override
	public void optionSelected(AbstractPlayer arg0, AbstractMonster arg1, int arg2) 
	{
		if (DuelistMod.debug) 
		{
			AbstractCard temp = duelistCardModal.getCard(arg2);
			AbstractCard tempB = cardModal.getCard(arg2);
			AbstractCard tempC = orbModal.getCard(arg2);
			if (DuelistMod.debug)
			{
				if (temp != null && tempB != null && tempC != null) { System.out.println("theDuelist:DuelistCard:optionSelected() ---> can I see the card we picked? the card should be one of these three:: [Duelist Modal]: " + temp.originalName + ", [CardModal]: " + tempB.originalName + ", [OrbModal]: " + tempC.originalName); }
				else { System.out.println("theDuelist:DuelistCard:optionSelected() ---> one of the modal cards was null, so we printed this unhelpful statement. sorry."); }
			}
		}
	}
	// =============== /SUPER OVERRIDE FUNCTIONS/ =======================================================================================================================================================
	
	
	
	// =============== DUELIST FUNCTIONS =========================================================================================================================================================
	@Override
	public String onSave()
	{
		String saveAttributes = "";
		saveAttributes += this.permTribChange + "~";
		saveAttributes += this.permSummonChange + "~";
		saveAttributes += DuelistMod.archRoll1 + "~";
		saveAttributes += DuelistMod.archRoll2 + "~";
		for (String s : this.savedTypeMods) { saveAttributes += s + "~"; }
		return saveAttributes;
	}
	
	@Override
	public void onLoad(String attributeString)
	{
		// If no saved string, just return
		if (attributeString == null) { return; }
		
		// Otherwise, get the saved string and split it into components
		String[] savedStrings = attributeString.split("~");
		ArrayList<String> savedTypes = new ArrayList<String>();
		String[] savedIntegers = new String[4];
		
		// Get the first 4 strings and convert back to int (perm tribute changes, perm summon changes, random pool archetype 1 & 2)
		
		for (int i = 0; i < 4; i++) { savedIntegers[i] = savedStrings[i]; }
		int[] ints = Arrays.stream(savedIntegers).mapToInt(Integer::parseInt).toArray();
		
		// Now look for any saved type modifications
		for (int j = 4; j < savedStrings.length - 1; j++) 
		{
			savedTypes.add(savedStrings[j]);
		}
		
		// Now apply saved values to the card
		if (ints[0] != 0)
		{
			this.modifyTributesPerm(ints[0]);
		}
		
		if (ints[1] != 0)
		{
			this.modifySummonsPerm(ints[1]);
		}
		
		if (ints[2] > -1)
		{
			DuelistMod.archRoll1 = ints[2];
		}
		
		if (ints[3] > -1)
		{
			DuelistMod.archRoll2 = ints[3];
		}
		
		if (!(savedTypes.contains("default"))) 
		{
			this.savedTypeMods = new ArrayList<String>();
			for (String s : savedTypes)
			{
				this.savedTypeMods.add(s);
				if (s.equals("Megatyped")) { this.makeMegatyped(); }
				else { this.tags.add(DuelistMod.typeCardMap_NameToString.get(s)); }
				this.rawDescription = this.rawDescription + " NL " + s;
			}
			this.originalDescription = this.rawDescription;
			this.isTypeAddedPerm = true;
			this.initializeDescription();
		}

		if (DuelistMod.debug) 
		{ 
			System.out.println(this.originalName + " loaded this string: [" + attributeString + "]");
			int counter = 0;
			for (int i : ints)
			{
				System.out.println("ints[" + counter + "]: " + i);
				counter++;
			}
		}
	}
	
	@Override
	public Type savedType()
	{
		return String.class;
	}
	
	protected static AbstractPlayer player() {
		return AbstractDungeon.player;
	}
	
	public DuelistCard getCard()
	{
		return this;
	}
	
	protected void upgradeName(String newName) 
	{
		this.timesUpgraded += 1;
		this.upgraded = true;
		this.name = newName;
		initializeTitle();
	}
	
	public void setupStartingCopies()
	{
		this.startCopies = new ArrayList<Integer>();
		this.startCopies.add(this.standardDeckCopies);		// 0 - Default Copies
		this.startCopies.add(this.dragonDeckCopies); 		// 1 - Dragon Copies
		this.startCopies.add(this.natureDeckCopies); 		// 2 - Nature Copies
		this.startCopies.add(this.spellcasterDeckCopies); 	// 3 - Spellcaster Copies	
		this.startCopies.add(this.toonDeckCopies); 			// 4 - Toon Copies
		this.startCopies.add(this.zombieDeckCopies); 		// 5 - Creator Copies
		this.startCopies.add(this.aquaDeckCopies); 			// 6 - Creator Copies
		this.startCopies.add(this.fiendDeckCopies); 		// 7 - Creator Copies
		this.startCopies.add(this.machineDeckCopies); 		// 8 - Creator Copies
		this.startCopies.add(this.superheavyDeckCopies); 	// 9 - Creator Copies
		this.startCopies.add(this.creatorDeckCopies); 		// 10 - Creator Copies
		this.startCopies.add(this.ojamaDeckCopies); 		// 11 - Ojama Copies
		this.startCopies.add(this.generationDeckCopies); 	// 12 - Gen Copies
		this.startCopies.add(this.orbDeckCopies); 			// 13 - Orb Copies
		this.startCopies.add(this.resummonDeckCopies); 		// 14 - Resumon Copies
		this.startCopies.add(this.incrementDeckCopies);		// 15 - Increment Copies
		this.startCopies.add(this.exodiaDeckCopies);		// 16 - Exodia Copies
		this.startCopies.add(this.healDeckCopies); 			// 17 - Heal Copies
	}
	
	public void customOnTribute(DuelistCard tc)
	{
		
	}

	public void startBattleReset()
	{
		if (this.isTribModPerm)
		{
			this.rawDescription = this.originalDescription;
			this.initializeDescription();
		}
		
		if (this.isSummonModPerm)
		{
			this.rawDescription = this.originalDescription;
			this.initializeDescription();
		}
		
		//if (this.isTypeAddedPerm)
		//{
		//	this.rawDescription = this.originalDescription;
		//	this.initializeDescription();
		//}
	}

	public void postBattleReset()
	{
		if ((this.isTributesModifiedForTurn || this.isTributesModified) && !this.isTribModPerm)
		{
			this.isTributesModifiedForTurn = false;
			this.isTributesModified = false;
			this.tributes = this.baseTributes;
			this.rawDescription = this.originalDescription;
			this.initializeDescription();
		}
		
		if ((this.isSummonsModifiedForTurn || this.isSummonsModified) && !this.isSummonModPerm)
		{
		
			this.isSummonsModifiedForTurn = false;
			this.isSummonsModified = false;
			this.summons = this.baseSummons;
			this.rawDescription = this.originalDescription;
			this.initializeDescription();
		}
		
		if (this.fiendDeckDmgMod && this.damage != this.originalDamage && this.originalDamage != -1)
		{
			this.applyPowers();
			if (DuelistMod.debug)
			{
				DuelistMod.logger.info("Triggered Fiend deck reset because of increased damage, damage value on card: " + this.damage + ", and old value: " + this.originalDamage + ", card name: " + this.originalName);
			}
		}
		
		else if (this.fiendDeckDmgMod && this.block != this.originalBlock && this.originalBlock != -1)
		{
			this.applyPowers();
			if (DuelistMod.debug)
			{
				DuelistMod.logger.info("Triggered Aqua deck reset because of increased block, block value on card: " + this.block + ", and old value: " + this.originalBlock + ", card name: " + this.originalName);
			}
		}
		
		if (this.dmgHolder != -1) { this.dmgHolder = -1; }
	}
	
	public void postTurnReset()
	{
		if (this.isTributesModifiedForTurn)
		{
			this.isTributesModifiedForTurn = false;
			this.isTributesModified = false;
			this.tributes = this.baseTributes;
			this.rawDescription = this.originalDescription;
			this.initializeDescription();
		}
		
		if (this.isSummonsModifiedForTurn)
		{		
			this.isSummonsModifiedForTurn = false;
			this.isSummonsModified = false;
			this.summons = this.baseSummons;
			this.rawDescription = this.originalDescription;
			this.initializeDescription();
		}
	}
	
	
	// UNUSED
	public static boolean purgeCard(AbstractCard toPurge) {
		return purgeCard(toPurge.uuid);
	}

	private static boolean purgeCard(UUID targetUUID) {
		for (AbstractCard c : AbstractDungeon.player.masterDeck.group) {
			if (c.uuid.equals(targetUUID)) {
				AbstractDungeon.topLevelEffects.add(new PurgeCardEffect(c, Settings.WIDTH / 2, Settings.HEIGHT / 2));
				AbstractDungeon.player.masterDeck.removeCard(c);
				return true;
			}
		}
		return false;
	}
	
	public void initializeNumberedCard() {
		playCount = 0;
	}

	public void addPlayCount() {
		for (AbstractCard c : GetAllInBattleInstances.get(this.uuid)) 
		{
			if (c instanceof DuelistCard)
			{
				DuelistCard nc = (DuelistCard) c;
				nc.playCount++;
			}
		}
	}
	// END UNUSED
	// =============== /DUELIST FUNCTIONS/ =======================================================================================================================================================
	
	
	
	// =============== ATTACK FUNCTIONS =========================================================================================================================================================
	public void attack(AbstractMonster m)
	{
		attack(m, this.baseAFX, this.damage);
	}
	
	public void thornAttack(AbstractMonster m)
	{
		thornAttack(m, this.baseAFX, this.damage);
	}
	
	public void thornAttack(AbstractMonster m, int dmg)
	{
		thornAttack(m, this.baseAFX, dmg);
	}
	
	public void thornAttack(AbstractMonster m, AttackEffect effect, int damageAmount) 
	{		
		if (this.hasTag(Tags.DRAGON) && player().hasPower(TyrantWingPower.POWER_ID)) 
		{  
			TwoAmountPower power = (TwoAmountPower) player().getPower(TyrantWingPower.POWER_ID);			
			power.amount2--;
			power.updateDescription();
		}
		AbstractDungeon.actionManager.addToBottom(new DamageAction(m, new DamageInfo(player(), damageAmount, DamageType.THORNS), effect));
	}
	
	public static void staticThornAttack(AbstractMonster m, AttackEffect effect, int damageAmount)
	{
		AbstractDungeon.actionManager.addToBottom(new DamageAction(m, new DamageInfo(player(), damageAmount, DamageType.THORNS), effect));
	}
	
	public void attack(AbstractMonster m, AttackEffect effect, int damageAmount) 
	{
		if (this.hasTag(Tags.DRAGON) && player().hasPower(TyrantWingPower.POWER_ID)) 
		{  
			TwoAmountPower power = (TwoAmountPower) player().getPower(TyrantWingPower.POWER_ID);
			power.amount2--;
			power.updateDescription();
		}
		AbstractDungeon.actionManager.addToBottom(new DamageAction(m, new DamageInfo(player(), damageAmount, damageTypeForTurn), effect));
	}
	
	public static void staticAttack(AbstractMonster m, AttackEffect effect, int damageAmount) 
	{
		AbstractDungeon.actionManager.addToBottom(new DamageAction(m, new DamageInfo(player(), damageAmount, DamageType.THORNS), effect));
	}
	
	public void attackFast(AbstractMonster m, AttackEffect effect, int damageAmount)
	{
		if (this.hasTag(Tags.DRAGON) && player().hasPower(TyrantWingPower.POWER_ID)) 
		{  
			TwoAmountPower power = (TwoAmountPower) player().getPower(TyrantWingPower.POWER_ID);
			power.amount2--;
			power.updateDescription();
		}
		AbstractDungeon.actionManager.addToBottom(new DamageAction(m, new DamageInfo(player(), damageAmount, damageTypeForTurn), effect, true));
	}

	protected void attackAllEnemies(AttackEffect effect, int[] damageAmounts) 
	{
		if (this.hasTag(Tags.DRAGON) && player().hasPower(TyrantWingPower.POWER_ID)) 
		{  
			TwoAmountPower power = (TwoAmountPower) player().getPower(TyrantWingPower.POWER_ID);
			power.amount2--;
			power.updateDescription();
		}
		AbstractDungeon.actionManager.addToBottom(new DamageAllEnemiesAction(player(), damageAmounts, damageTypeForTurn, effect));
	}

	public static void attackAll(AttackEffect effect, int[] damageAmounts, DamageType dmgForTurn)
	{
		AbstractDungeon.actionManager.addToBottom(new DamageAllEnemiesAction(player(), damageAmounts, dmgForTurn, effect));
	}
	
	public static void attackAllEnemies(int damage)
	{
		int[] damageArray = new int[] { damage, damage, damage, damage, damage, damage, damage, damage, damage, damage };
		attackAll(AbstractGameAction.AttackEffect.SLASH_HORIZONTAL, damageArray, DamageType.NORMAL);
	}

	public static void damageAllEnemiesFire(int damage)
	{
		int[] damageArray = new int[] { damage, damage, damage, damage, damage, damage, damage, damage, damage, damage };
		attackAll(AbstractGameAction.AttackEffect.FIRE, damageArray, DamageType.NORMAL);
	}

	public void damageThroughBlock(AbstractCreature m, AbstractPlayer p, int damage, AttackEffect effect)
	{
		if (this.hasTag(Tags.DRAGON) && player().hasPower(TyrantWingPower.POWER_ID)) 
		{  
			TwoAmountPower power = (TwoAmountPower) player().getPower(TyrantWingPower.POWER_ID);
			power.amount2--;
			power.updateDescription();
		}
		// Record target block and remove all of it
		int targetArmor = m.currentBlock;
		if (targetArmor > 0) { AbstractDungeon.actionManager.addToTop(new RemoveAllBlockAction(m, m)); }

		// Deal direct damage to target HP
		AbstractDungeon.actionManager.addToBottom(new DamageAction(m, new DamageInfo(p, damage, damageTypeForTurn), effect));

		// Restore original target block
		if (targetArmor > 0) { AbstractDungeon.actionManager.addToBottom(new GainBlockAction(m, m, targetArmor)); }
	}

	public void damageThroughBlockAllEnemies(AbstractPlayer p, int damage, AttackEffect effect)
	{
		ArrayList<AbstractMonster> monsters = AbstractDungeon.getMonsters().monsters;
		for (AbstractMonster m : monsters)
		{
			if (!m.isDead) 
			{ 
				damageThroughBlock(m, p, damage, effect); 
			}
		}
	}

	public static void damageAllEnemiesThornsPoison(int damage)
	{
		int[] damageArray = new int[] { damage, damage, damage, damage, damage, damage, damage, damage, damage, damage };
		attackAll(AbstractGameAction.AttackEffect.POISON, damageArray, DamageType.THORNS);
	}
	
	public static void damageAllEnemiesThornsNormal(int damage)
	{
		int[] damageArray = new int[] { damage, damage, damage, damage, damage, damage, damage, damage, damage, damage };
		attackAll(AbstractGameAction.AttackEffect.SLASH_HORIZONTAL, damageArray, DamageType.THORNS);
	}
	
	public static void damageAllEnemiesThornsFire(int damage)
	{
		int[] damageArray = new int[] { damage, damage, damage, damage, damage, damage, damage, damage, damage, damage };
		attackAll(AbstractGameAction.AttackEffect.FIRE, damageArray, DamageType.THORNS);
	}
	// =============== /ATTACK FUNCTIONS/ =======================================================================================================================================================
	
	
	
	// =============== DEFEND FUNCTIONS =========================================================================================================================================================
	protected void block() 
	{
		block(this.block);
	}

	public void block(int amount) 
	{
		if (this.hasTag(Tags.DRAGON) && player().hasPower(MountainPower.POWER_ID)) { amount = (int) Math.floor(amount * 1.5); }
		if (this.hasTag(Tags.SPELLCASTER) && player().hasPower(YamiPower.POWER_ID)) { amount = (int) Math.floor(amount * 1.5); }
		if (this.hasTag(Tags.INSECT) && player().hasPower(VioletCrystalPower.POWER_ID)) { amount = (int) Math.floor(amount * 1.5); }
		else if (this.hasTag(Tags.PLANT) && player().hasPower(VioletCrystalPower.POWER_ID)) { amount = (int) Math.floor(amount * 1.5); }
		if (this.hasTag(Tags.NATURIA) && player().hasPower(SacredTreePower.POWER_ID)) { amount = (int) Math.floor(amount * 1.5); }
		if (this.hasTag(Tags.AQUA) && player().hasPower(UmiPower.POWER_ID)) { amount = (int) Math.floor(amount * 1.5); }
		if (this.hasTag(Tags.ZOMBIE) || this.hasTag(Tags.FIEND)) { if (player().hasPower(GatesDarkPower.POWER_ID)) { amount = (int) Math.floor(amount * 2); }}
		AbstractDungeon.actionManager.addToTop(new GainBlockAction(player(), player(), amount));
	}

	public static void staticBlock(int amount) 
	{
		AbstractDungeon.actionManager.addToTop(new GainBlockAction(player(), player(), amount));
	}
	// =============== /DEFEND FUNCTIONS/ =======================================================================================================================================================
	
	
	
	// =============== POWER FUNCTIONS =========================================================================================================================================================
	protected boolean hasPower(String power) {
		return player().hasPower(power);
	}

	public static void applyPower(AbstractPower power, AbstractCreature target) {
		AbstractDungeon.actionManager.addToBottom(new ApplyPowerAction(target, player(), power, power.amount));

	}

	public static void applyPowerTop(AbstractPower power, AbstractCreature target) {
		AbstractDungeon.actionManager.addToTop(new ApplyPowerAction(target, player(), power, power.amount));

	}

	protected void applyPower(AbstractPower power, AbstractCreature target, int amount) {
		AbstractDungeon.actionManager.addToBottom(new ApplyPowerAction(target, player(), power, amount));

	}

	public static void removePower(AbstractPower power, AbstractCreature target) {

		if (target.hasPower(power.ID))
		{
			AbstractDungeon.actionManager.addToBottom(new ReducePowerAction(target, player(), power, power.amount));
		}
	}
	
	public static void removePowerAction(AbstractCreature target, AbstractPower power)
	{
		AbstractDungeon.actionManager.addToBottom(new RemoveSpecificPowerAction(target, target, power));
	}
	
	public static void removePowerAction(AbstractCreature target, String powerName)
	{
		AbstractDungeon.actionManager.addToBottom(new RemoveSpecificPowerAction(target, target, powerName));
	}

	public static void reducePower(AbstractPower power, AbstractCreature target, int reduction) {

		if (target.hasPower(power.ID))
		{
			AbstractDungeon.actionManager.addToBottom(new ReducePowerAction(target, player(), power, reduction));
		}		
	}

	public static void applyPowerToSelf(AbstractPower power) {
		applyPower(power, player());

	}

	public static void applyPowerToSelfTop(AbstractPower power) {
		applyPowerTop(power, player());

	}

	// turnNum arg does not work here, random buffs are generated globally now but I don't feel like fixing all the calls to this function
	public static AbstractPower applyRandomBuff(AbstractCreature p, int turnNum)
	{
		BuffHelper.resetRandomBuffs();

		// Get randomized buff
		int randomBuffNum = AbstractDungeon.cardRandomRng.random(DuelistMod.randomBuffs.size() - 1);
		AbstractPower randomBuff = DuelistMod.randomBuffs.get(randomBuffNum);
		for (int i = 0; i < DuelistMod.randomBuffs.size(); i++)
		{
			if (DuelistMod.debug) { System.out.println("theDuelist:DuelistCard:applyRandomBuff() ---> buffs[" + i + "]: " + DuelistMod.randomBuffs.get(i).name + " :: amount: " + DuelistMod.randomBuffs.get(i).amount); }
		}
		if (DuelistMod.debug) { System.out.println("theDuelist:DuelistCard:applyRandomBuff() ---> generated random buff: " + randomBuff.name + " :: index was: " + randomBuffNum + " :: turnNum or amount was: " + randomBuff.amount); }
		ArrayList<AbstractPower> powers = p.powers;
		//boolean found = false;
		applyPower(randomBuff, p);
		for (AbstractPower a : powers)
		{
			//if (!a.name.equals("Time Wizard")) { a.updateDescription(); }
			a.updateDescription();
		}		
		return randomBuff;
	}

	public static AbstractPower applyRandomBuffSmall(AbstractCreature p, int turnNum)
	{
		// Setup powers array for random buff selection
		AbstractPower str = new StrengthPower(p, turnNum);
		AbstractPower dex = new DexterityPower(p, 1);
		AbstractPower art = new ArtifactPower(p, turnNum);
		AbstractPower plate = new PlatedArmorPower(p, turnNum);
		AbstractPower regen = new RegenPower(p, turnNum);
		AbstractPower energy = new EnergizedPower(p, 1);
		AbstractPower thorns = new ThornsPower(p, turnNum);
		AbstractPower focus = new FocusPower(p, turnNum);
		AbstractPower[] buffs = new AbstractPower[] { str, dex, art, plate, regen, energy, thorns, focus };

		// Get randomized buff
		int randomBuffNum = AbstractDungeon.cardRandomRng.random(buffs.length - 1);
		AbstractPower randomBuff = buffs[randomBuffNum];

		ArrayList<AbstractPower> powers = p.powers;
		//boolean found = false;
		applyPower(randomBuff, p);
		for (AbstractPower a : powers)
		{
			//if (!a.name.equals("Time Wizard")) { a.updateDescription(); }
			a.updateDescription();
		}
		return randomBuff;
	}
	
	public static AbstractPower getRandomBuff(AbstractCreature p, int turnNum)
	{
		BuffHelper.resetRandomBuffs();

		// Get randomized buff
		int randomBuffNum = AbstractDungeon.cardRandomRng.random(DuelistMod.randomBuffs.size() - 1);
		AbstractPower randomBuff = DuelistMod.randomBuffs.get(randomBuffNum);
		for (int i = 0; i < DuelistMod.randomBuffs.size(); i++)
		{
			if (DuelistMod.debug) { System.out.println("theDuelist:DuelistCard:getRandomBuff() ---> buffs[" + i + "]: " + DuelistMod.randomBuffs.get(i).name + " :: amount: " + DuelistMod.randomBuffs.get(i).amount); }
		}
		if (DuelistMod.debug) { System.out.println("theDuelist:DuelistCard:getRandomBuff() ---> generated random buff: " + randomBuff.name + " :: index was: " + randomBuffNum + " :: turnNum or amount was: " + randomBuff.amount); }	
		return randomBuff;
	}
	
	public static ArrayList<AbstractPower> getRandomBuffs(AbstractCreature p, int amount, boolean replacement)
	{
		if (!replacement)
		{
			if (amount > DuelistMod.lowNoBuffs - 1) { amount = DuelistMod.lowNoBuffs - 1; }
			BuffHelper.resetRandomBuffs();
			ArrayList<AbstractPower> powerList = new ArrayList<AbstractPower>();
			ArrayList<String> powerNames = new ArrayList<String>();
			// Get randomized buff
			for (int j = 0; j < amount; j++)
			{
				int randomBuffNum = AbstractDungeon.cardRandomRng.random(DuelistMod.randomBuffs.size() - 1);
				AbstractPower randomBuff = DuelistMod.randomBuffs.get(randomBuffNum);
				while(powerNames.contains(randomBuff.name))
				{
					randomBuffNum = AbstractDungeon.cardRandomRng.random(DuelistMod.randomBuffs.size() - 1);
					randomBuff = DuelistMod.randomBuffs.get(randomBuffNum);
				}
				powerList.add(randomBuff);
				powerNames.add(randomBuff.name);
				for (int i = 0; i < DuelistMod.randomBuffs.size(); i++)
				{
					if (DuelistMod.debug) { System.out.println("theDuelist:DuelistCard:getRandomBuff() ---> buffs[" + i + "]: " + DuelistMod.randomBuffs.get(i).name + " :: amount: " + DuelistMod.randomBuffs.get(i).amount); }
				}
				if (DuelistMod.debug) { System.out.println("theDuelist:DuelistCard:getRandomBuff() ---> generated random buff: " + randomBuff.name + " :: index was: " + randomBuffNum + " :: turnNum or amount was: " + randomBuff.amount); }	
			}
			return powerList;
		}
		else
		{
			BuffHelper.resetRandomBuffs();
			ArrayList<AbstractPower> powerList = new ArrayList<AbstractPower>();
			// Get randomized buff
			for (int j = 0; j < amount; j++)
			{
				int randomBuffNum = AbstractDungeon.cardRandomRng.random(DuelistMod.randomBuffs.size() - 1);
				AbstractPower randomBuff = DuelistMod.randomBuffs.get(randomBuffNum);
				powerList.add(randomBuff);
				for (int i = 0; i < DuelistMod.randomBuffs.size(); i++)
				{
					if (DuelistMod.debug) { System.out.println("theDuelist:DuelistCard:getRandomBuff() ---> buffs[" + i + "]: " + DuelistMod.randomBuffs.get(i).name + " :: amount: " + DuelistMod.randomBuffs.get(i).amount); }
				}
				if (DuelistMod.debug) { System.out.println("theDuelist:DuelistCard:getRandomBuff() ---> generated random buff: " + randomBuff.name + " :: index was: " + randomBuffNum + " :: turnNum or amount was: " + randomBuff.amount); }	
			}
			return powerList;
		}
	}
	
	public static AbstractPower getRandomBuffSmall(AbstractCreature p, int turnNum)
	{
		// Setup powers array for random buff selection
		AbstractPower str = new StrengthPower(p, turnNum);
		AbstractPower dex = new DexterityPower(p, 1);
		AbstractPower art = new ArtifactPower(p, turnNum);
		AbstractPower plate = new PlatedArmorPower(p, turnNum);
		AbstractPower regen = new RegenPower(p, turnNum);
		AbstractPower energy = new EnergizedPower(p, 1);
		AbstractPower thorns = new ThornsPower(p, turnNum);
		AbstractPower focus = new FocusPower(p, turnNum);
		AbstractPower[] buffs = new AbstractPower[] { str, dex, art, plate, regen, energy, thorns, focus };

		// Get randomized buff
		int randomBuffNum = AbstractDungeon.cardRandomRng.random(buffs.length - 1);
		AbstractPower randomBuff = buffs[randomBuffNum];
		return randomBuff;
	}

	public static AbstractPower applyRandomBuffPlayer(AbstractPlayer p, int turnNum, boolean smallSet)
	{
		if (smallSet) { return applyRandomBuffSmall(p, turnNum); }
		else { return applyRandomBuff(p, turnNum); }
	}
	
	public static AbstractPower getRandomBuffPlayer(AbstractPlayer p, int turnNum, boolean smallSet)
	{
		if (smallSet) { return getRandomBuffSmall(p, turnNum); }
		else { return getRandomBuff(p, turnNum); }
	}
	
	public static void poisonAllEnemies(AbstractPlayer p, int amount)
	{
		if (!AbstractDungeon.getMonsters().areMonstersBasicallyDead()) 
		{
			//flash();
			for (AbstractMonster monster : AbstractDungeon.getMonsters().monsters) 
			{
				if ((!monster.isDead) && (!monster.isDying)) 
				{
					AbstractDungeon.actionManager.addToBottom(new ApplyPowerAction(monster, p, new PoisonPower(monster, p, amount), amount));
				}
			}
		}

	}
	
	public static void constrictAllEnemies(AbstractPlayer p, int amount)
	{
		if (!AbstractDungeon.getMonsters().areMonstersBasicallyDead()) 
		{
			//flash();
			for (AbstractMonster monster : AbstractDungeon.getMonsters().monsters) 
			{
				if ((!monster.isDead) && (!monster.isDying)) 
				{
					AbstractDungeon.actionManager.addToBottom(new ApplyPowerAction(monster, p, new ConstrictedPower(monster, p, amount), amount));
				}
			}
		}

	}

	// =============== /POWER FUNCTIONS/ =======================================================================================================================================================
	
	
	
	// =============== MISC ACTION FUNCTIONS =========================================================================================================================================================
	public void makeFleeting()
	{
		FleetingField.fleeting.set(this, true);
	}
	
	public void makeFleeting(boolean set)
	{
		FleetingField.fleeting.set(this, set);
	}
	
	public void makeGrave()
	{
		GraveField.grave.set(this, true);
	}
	
	public void makeGrave(boolean set)
	{
		GraveField.grave.set(this, set);
	}
	
	public void makeSoulbound(boolean set)
	{
		SoulboundField.soulbound.set(this, set);
	}
	
	public void makeRefund(boolean set, int amt)
	{
		RefundFields.baseRefund.set(this, amt);
		RefundFields.refund.set(this, amt);
	}
	
	public void makeMegatyped()
	{
		if (!this.hasTag(Tags.MEGATYPED))
		{
			for (CardTags t : DuelistMod.monsterTypes) { this.tags.add(t); }
			this.tags.add(Tags.MEGATYPED);
		}
	}
	
	public void lavaZombieEffectHandler()
	{
		if (!AbstractDungeon.player.hasEmptyOrb())
		{
			ArrayList<Lava> lavas = new ArrayList<Lava>();
			for (AbstractOrb o : AbstractDungeon.player.orbs)
			{
				if (o instanceof Lava)
				{
					lavas.add((Lava) o);
				}
			}
			
			if (lavas.size() > 0)
			{
				for (Lava blurp : lavas)
				{
					blurp.zombieTributeTrigger();
					blurp.updateDescription();
					if (DuelistMod.debug) { DuelistMod.logger.info("Lava orb triggered zombie tribute effect. BLurp"); }
				}
			}
		}
	}
	
	public void fetch(CardGroup group, boolean top)
	{
		if (top) { AbstractDungeon.actionManager.addToTop(new FetchAction(group)); }
		else { AbstractDungeon.actionManager.addToBottom(new FetchAction(group)); }
	}
	
	public void fetch(int amount, CardGroup group, boolean top)
	{
		if (top) { AbstractDungeon.actionManager.addToTop(new FetchAction(group, amount)); }
		else { AbstractDungeon.actionManager.addToBottom(new FetchAction(group, amount)); }
	}
	
	public AbstractCard makeFullCopy()
	{
		AbstractCard c = super.makeStatEquivalentCopy();
		c.exhaust = this.exhaust;
		return c;
	}
	
	public static ArrayList<CardTags> getAllMonsterTypes(AbstractCard c)
	{
		ArrayList<CardTags> toRet = new ArrayList<CardTags>();
		for (CardTags t : DuelistMod.monsterTypes)
		{
			if (c.hasTag(t)) { toRet.add(t); }
		}
		
		return toRet;
	}
	
	public static int cursedBillGoldLoss()
	{
		int loss = 0;
		for (AbstractCard c : AbstractDungeon.player.drawPile.group)
		{
			if (c instanceof CursedBill)
			{
				loss += c.magicNumber;
			}
		}
		return loss;
	}
	
	public static boolean hasSummoningCurse()
	{
		for (AbstractCard c : player().hand.group)
		{
			if (c instanceof SummoningCurse)
			{
				return true;
			}
		}
		
		return false;
	}
	
	public static boolean isPsiCurseActive()
	{
		for (AbstractCard c : player().drawPile.group)
		{
			if (c instanceof PsiCurse)
			{
				return true;
			}
		}
		
		for (AbstractCard c : player().discardPile.group)
		{
			if (c instanceof PsiCurse)
			{
				return true;
			}
		}
		
		return false;
	}
	
	public static AbstractPower getTypeAssociatedBuff(CardTags type, int turnAmount)
	{
		Map<CardTags,AbstractPower> powerTypeMap = new HashMap<CardTags,AbstractPower>();
		powerTypeMap.put(Tags.AQUA, new SpikedGillmanPower(AbstractDungeon.player, AbstractDungeon.player, turnAmount));
		powerTypeMap.put(Tags.DRAGON, new StrengthPower(AbstractDungeon.player, turnAmount));
		powerTypeMap.put(Tags.FIEND, new DoomdogPower(AbstractDungeon.player, AbstractDungeon.player, turnAmount));
		powerTypeMap.put(Tags.INSECT, new CocoonPower(AbstractDungeon.player, AbstractDungeon.player));
		powerTypeMap.put(Tags.MACHINE, new ArtifactPower(AbstractDungeon.player, turnAmount));
		powerTypeMap.put(Tags.NATURIA, new NaturiaPower(AbstractDungeon.player, AbstractDungeon.player, turnAmount));
		powerTypeMap.put(Tags.PLANT, new PlantTypePower(AbstractDungeon.player, AbstractDungeon.player, turnAmount));
		powerTypeMap.put(Tags.PREDAPLANT, new ThornsPower(AbstractDungeon.player, turnAmount));
		powerTypeMap.put(Tags.SPELLCASTER, new FocusPower(AbstractDungeon.player, turnAmount));
		powerTypeMap.put(Tags.SUPERHEAVY, new DexterityPower(AbstractDungeon.player, turnAmount));
		powerTypeMap.put(Tags.TOON, new RetainCardPower(AbstractDungeon.player, 1));
		powerTypeMap.put(Tags.ZOMBIE, new TrapHolePower(AbstractDungeon.player, AbstractDungeon.player, 1));
		
		if (!powerTypeMap.get(type).equals(null)) { return powerTypeMap.get(type); }
		else { return new StrengthPower(AbstractDungeon.player, turnAmount); }
	}
	
	public static int countMonsterTypesInPile(ArrayList<AbstractCard> pile)
	{
		ArrayList<CardTags> types = new ArrayList<CardTags>();
		for (AbstractCard c : pile)
		{
			ArrayList<CardTags> temp = getAllMonsterTypes(c);
			for (CardTags t : temp)
			{
				if (!types.contains(t))
				{
					types.add(t);
				}
			}
		}
		return types.size();
	}
	
	public static AbstractMonster getRandomMonster()
	{
		AbstractMonster m = AbstractDungeon.getRandomMonster();
		return m;
	}

	protected int getXEffect() {
		if (energyOnUse < EnergyPanel.totalCount) {
			energyOnUse = EnergyPanel.totalCount;
		}

		int effect = EnergyPanel.totalCount;
		if (energyOnUse != -1) {
			effect = energyOnUse;
		}
		if (player().hasRelic(ChemicalX.ID)) {
			effect += ChemicalX.BOOST;
			player().getRelic(ChemicalX.ID).flash();
		}
		return effect;
	}

	protected void useXEnergy() {
		AbstractDungeon.actionManager.addToTop(new LoseXEnergyAction(player(), freeToPlayOnce));
	}

	public static void damageSelf(int DAMAGE)
	{
		AbstractDungeon.actionManager.addToBottom(new LoseHPAction(AbstractDungeon.player, AbstractDungeon.player, DAMAGE, AbstractGameAction.AttackEffect.POISON));
	}
	
	public static void damageSelfNotHP(int DAMAGE)
	{
		AbstractDungeon.actionManager.addToBottom(new DamageAction(player(), new DamageInfo(player(), DAMAGE, DamageInfo.DamageType.NORMAL)));
	}

	public static void heal(AbstractPlayer p, int amount)
	{
		AbstractDungeon.actionManager.addToTop(new HealAction(p, p, amount));
	}

	protected void healMonster(AbstractMonster p, int amount)
	{
		AbstractDungeon.actionManager.addToTop(new HealAction(p, p, amount));
	}

	public static void gainGold(int amount, AbstractCreature owner, boolean rain)
	{
		if (!AbstractDungeon.player.hasRelic(Ectoplasm.ID)) 
		{
			CardCrawlGame.sound.play("GOLD_GAIN");
			AbstractDungeon.actionManager.addToBottom(new ObtainGoldAction(amount, owner, rain));
		}
	}
	
	public static void loseGold(int amount)
	{
		AbstractDungeon.player.loseGold(amount);
	}

	public static void draw(int cards) {
		AbstractDungeon.actionManager.addToTop(new DrawCardAction(player(), cards));
	}
	
	public static void drawTag(int cards, CardTags tag)
	{
		AbstractDungeon.actionManager.addToTop(new DrawFromTagAction(player(), cards, tag));	
	}
	
	public static void drawTag(int cards, CardTags tag, boolean actionManagerBottom)
	{
		if (actionManagerBottom) { AbstractDungeon.actionManager.addToBottom(new DrawFromTagAction(player(), cards, tag));	}
		else { AbstractDungeon.actionManager.addToTop(new DrawFromTagAction(player(), cards, tag));	}
		
	}

	public static void drawTags(int cards, CardTags tag, CardTags tagB, boolean actionManagerBottom)
	{
		if (actionManagerBottom) { AbstractDungeon.actionManager.addToBottom(new DrawFromBothTagsAction(player(), cards, tag, tagB));	}
		else { AbstractDungeon.actionManager.addToTop(new DrawFromBothTagsAction(player(), cards, tag, tagB));	}
		
	}
	
	public static void drawRare(int cards, CardRarity tag)
	{
		AbstractDungeon.actionManager.addToTop(new DrawFromRarityAction(player(), cards, tag));	
	}
	
	public static void drawRare(int cards, CardRarity tag, boolean actionManagerBottom)
	{
		if (actionManagerBottom) { AbstractDungeon.actionManager.addToBottom(new DrawFromRarityAction(player(), cards, tag));		}
		else { AbstractDungeon.actionManager.addToTop(new DrawFromRarityAction(player(), cards, tag)); }
		
	}
	
	public static void drawRandomType(int cards, boolean actionManagerBottom, int seed)
	{
		switch (seed)
    	{
    		case 1: drawTag(cards, Tags.DRAGON, actionManagerBottom); AbstractDungeon.actionManager.addToBottom(new TalkAction(true, "Drawing: Dragons", 1.0F, 2.0F)); break;
    		case 2: drawTag(cards, Tags.MONSTER, actionManagerBottom); AbstractDungeon.actionManager.addToBottom(new TalkAction(true, "Drawing: Monsters", 1.0F, 2.0F)); break;
    		case 3: drawTag(cards, Tags.SPELL, actionManagerBottom); AbstractDungeon.actionManager.addToBottom(new TalkAction(true, "Drawing: Spells", 1.0F, 2.0F)); break;
    		case 4: drawTag(cards, Tags.TRAP, actionManagerBottom); AbstractDungeon.actionManager.addToBottom(new TalkAction(true, "Drawing: Traps", 1.0F, 2.0F)); break;
    		case 5: 
    			CardTags randomChoice = DuelistMod.monsterTypes.get(AbstractDungeon.cardRandomRng.random(DuelistMod.monsterTypes.size() - 1));
    			drawTag(cards, randomChoice, actionManagerBottom); 
    			AbstractDungeon.actionManager.addToBottom(new TalkAction(true, "Drawing: #y" + DuelistMod.typeCardMap_NAME.get(randomChoice) + "s", 1.0F, 2.0F)); 
    			break;
    		case 6: 
    			CardTags randomChoiceB = DuelistMod.monsterTypes.get(AbstractDungeon.cardRandomRng.random(DuelistMod.monsterTypes.size() - 1));
    			drawTag(cards, randomChoiceB, actionManagerBottom); 
    			AbstractDungeon.actionManager.addToBottom(new TalkAction(true, "Drawing: #y" + DuelistMod.typeCardMap_NAME.get(randomChoiceB) + "s", 1.0F, 2.0F)); 
    			break;
    		case 7: drawTag(cards, Tags.SPELL, actionManagerBottom); AbstractDungeon.actionManager.addToBottom(new TalkAction(true, "Drawing: Spells", 1.0F, 2.0F)); break;
    		case 8: drawRare(cards, CardRarity.COMMON, actionManagerBottom); AbstractDungeon.actionManager.addToBottom(new TalkAction(true, "Drawing: Commons", 1.0F, 2.0F)); break;
    		case 9: drawRare(cards, CardRarity.UNCOMMON, actionManagerBottom); AbstractDungeon.actionManager.addToBottom(new TalkAction(true, "Drawing: Uncommons", 1.0F, 2.0F)); break;
    		case 10: drawRare(cards, CardRarity.RARE, actionManagerBottom); AbstractDungeon.actionManager.addToBottom(new TalkAction(true, "Drawing: Rares", 1.0F, 2.0F)); break;
    		case 11: drawTag(cards, Tags.MONSTER, actionManagerBottom); AbstractDungeon.actionManager.addToBottom(new TalkAction(true, "Drawing: Monsters", 1.0F, 2.0F)); break;
    		case 12: drawTag(cards, Tags.TRAP, actionManagerBottom); AbstractDungeon.actionManager.addToBottom(new TalkAction(true, "Drawing: Traps", 1.0F, 2.0F)); break;
    		default: break;
    	}
	}
	
	public void drawBottom(int cards) {
		AbstractDungeon.actionManager.addToBottom(new DrawCardAction(player(), cards));
	}

	public static void discard(int amount, boolean isRandom)
	{
		AbstractDungeon.actionManager.addToBottom(new DiscardAction(player(), player(), amount, isRandom));
	}
	
	public static void exhaust(int amount, boolean isRandom)
	{
		AbstractDungeon.actionManager.addToBottom(new ExhaustAction(player(), player(), amount, isRandom));
	}

	public static void discardTop(int amount, boolean isRandom)
	{
		AbstractDungeon.actionManager.addToTop(new DiscardAction(player(), player(), amount, isRandom, false));
	}

	public static void gainEnergy(int energy) {
		AbstractDungeon.actionManager.addToBottom(new GainEnergyAction(energy));
	}
	
	public static void addCardToHand(AbstractCard card)
	{
		if (AbstractDungeon.player.hand.group.size() < BaseMod.MAX_HAND_SIZE)
		{
			AbstractDungeon.actionManager.addToBottom(new MakeTempCardInHandAction(card, false));
		}
	}
	
	public static void gainTempHP(int amount)
	{
		AbstractDungeon.actionManager.addToTop(new AddTemporaryHPAction(AbstractDungeon.player, AbstractDungeon.player, amount));
	}
	
	public static void getPotion(AbstractPotion pot)
	{
		AbstractDungeon.actionManager.addToTop(new ObtainPotionAction(pot));
	}
	
	// =============== /MISC ACTION FUNCTIONS/ =======================================================================================================================================================
	
	
	
	// =============== SUMMON MONSTER FUNCTIONS =========================================================================================================================================================
	public void summon()
	{
		summon(AbstractDungeon.player, this.summons, this);
		//summon(p, this.summons, this);
	}
	
	public static void summon(AbstractPlayer p, int SUMMONS, DuelistCard c)
	{
		if (hasSummoningCurse()) { return; }
		boolean challengeFailure = (Utilities.isCustomModActive("theDuelist:SummonRandomizer"));
		if (challengeFailure)
		{
			if (Utilities.isCustomModActive("challengethespire:Bronze Difficulty"))
			{
				if (AbstractDungeon.cardRandomRng.random(1, 3) == 1) 
				{ 
					AbstractDungeon.actionManager.addToBottom(new TalkAction(true, Strings.configFailedSummonActionText, 1.0F, 2.0F));
					return; 
				}
			}
			else if (Utilities.isCustomModActive("challengethespire:Silver Difficulty"))
			{
				if (AbstractDungeon.cardRandomRng.random(1, 2) == 1) 
				{ 
					AbstractDungeon.actionManager.addToBottom(new TalkAction(true, Strings.configFailedSummonActionText, 1.0F, 2.0F));
					return; 
				}
			}
			else if (Utilities.isCustomModActive("challengethespire:Gold Difficulty"))
			{
				if (AbstractDungeon.cardRandomRng.random(1, 3) != 1) 
				{ 
					AbstractDungeon.actionManager.addToBottom(new TalkAction(true, Strings.configFailedSummonActionText, 1.0F, 2.0F));
					return; 
				}
			}
			else if (Utilities.isCustomModActive("challengethespire:Platinum Difficulty"))
			{
				if (AbstractDungeon.cardRandomRng.random(1, 4) != 1) 
				{ 
					AbstractDungeon.actionManager.addToBottom(new TalkAction(true, Strings.configFailedSummonActionText, 1.0F, 2.0F));
					return; 
				}
			}
		}
		
		int currentDeck = 0;
		if (StarterDeckSetup.getCurrentDeck().getArchetypeCards().size() > 0) { currentDeck = StarterDeckSetup.getCurrentDeck().getIndex(); }
		if (DuelistMod.debug) { System.out.println("theDuelist:DuelistCard:summon() ---> called summon()"); }
		if (!DuelistMod.checkTrap)
		{
			if (DuelistMod.debug) { System.out.println("theDuelist:DuelistCard:summon() ---> no check trap, SUMMONS: " + SUMMONS); }
			// Check to make sure they still have summon power, if they do not give it to them with a stack of 0
			if (!p.hasPower(SummonPower.POWER_ID))
			{
				AbstractDungeon.actionManager.addToTop(new ApplyPowerAction(AbstractDungeon.player, AbstractDungeon.player, new SummonPower(AbstractDungeon.player, SUMMONS, c.originalName, "#b" + SUMMONS + " monsters summoned. Maximum of 5 Summons.", c), SUMMONS));
				int startSummons = SUMMONS;
				// Check for Pot of Generosity
				if (p.hasPower(PotGenerosityPower.POWER_ID)) { AbstractDungeon.actionManager.addToTop(new GainEnergyAction(startSummons)); }

				// Check for Summoning Sickness
				if (p.hasPower(SummonSicknessPower.POWER_ID)) { damageSelfNotHP(startSummons * p.getPower(SummonSicknessPower.POWER_ID).amount); }

				// Check for Slifer
				if (p.hasPower(SliferSkyPower.POWER_ID)) 
				{ 
					SliferSkyPower instance = (SliferSkyPower) p.getPower(SliferSkyPower.POWER_ID);
					applyPowerToSelf(new StrengthPower(p, instance.amount));
					applyPowerToSelf(new LoseStrengthPower(p, instance.amount));
				} 
				
				// Check for Goblin's Secret Remedy
				if (p.hasPower(GoblinRemedyPower.POWER_ID)) { gainTempHP(p.getPower(GoblinRemedyPower.POWER_ID).amount);  }
				
				// Check for Blizzard Dragon
				if (p.hasPower(BlizzardDragonPower.POWER_ID) && c.hasTag(Tags.DRAGON)) 
				{ 
					for (int i = 0; i < startSummons; i++) { AbstractOrb frost = new Frost(); channel(frost); }
				}
				// Check for Toon Cannon Soldier
				if (p.hasPower(ToonCannonPower.POWER_ID) && c.hasTag(Tags.TOON))
				{
					ToonCannonPower power = (ToonCannonPower) p.getPower(ToonCannonPower.POWER_ID);
					DuelistCard.damageAllEnemiesThornsPoison(power.amount);
				}
				
				// Check for Tripod Fish
				if (p.hasPower(TripodFishPower.POWER_ID) && c.hasTag(Tags.AQUA))
				{
					for (int i = 0; i < p.getPower(TripodFishPower.POWER_ID).amount; i++)
					{						
						DuelistCard randAqExh = (DuelistCard) returnTrulyRandomFromSet(Tags.AQUA);					
						AbstractDungeon.actionManager.addToTop(new RandomizedExhaustPileAction(randAqExh, true));
					}
				}
				
				int cursedBillGold = cursedBillGoldLoss();
				if (cursedBillGold > 0) { loseGold(cursedBillGold); }
				
				// Check for Power Giants
				for (AbstractCard giantCard : player().hand.group)
				{
					if (giantCard instanceof PowerGiant)
					{
						PowerGiant giant = (PowerGiant)giantCard;
						giant.damageInc();
					}
				}
				
				for (AbstractCard giantCard : player().discardPile.group)
				{
					if (giantCard instanceof PowerGiant)
					{
						PowerGiant giant = (PowerGiant)giantCard;
						giant.damageInc();
					}
				}
				
				for (AbstractCard giantCard : player().drawPile.group)
				{
					if (giantCard instanceof PowerGiant)
					{
						PowerGiant giant = (PowerGiant)giantCard;
						giant.damageInc();
					}
				}
				
				// Check for new summoned types
				ArrayList<CardTags> toRet = getAllMonsterTypes(c);
				if (toRet.size() > 0)
				{
					for (CardTags t : toRet)
					{
						if (!DuelistMod.summonedTypesThisTurn.contains(t))
						{
							DuelistMod.summonedTypesThisTurn.add(t);
							if (player().hasPower(KuribohrnPower.POWER_ID))
							{
								if (DuelistMod.kuribohrnFlipper) 
								{ 
									DuelistCard randZomb = (DuelistCard) returnTrulyRandomFromSet(Tags.ZOMBIE);
									fullResummon(randZomb, false, AbstractDungeon.getRandomMonster(), false);
								}
								DuelistMod.kuribohrnFlipper = !DuelistMod.kuribohrnFlipper;
							}
						}
					}					
					DuelistMod.lastTagSummoned = toRet.get(AbstractDungeon.cardRandomRng.random(toRet.size() - 1));
				}
			
				if (DuelistMod.debug)
				{
					int counter = 1;
					for (CardTags t : DuelistMod.summonedTypesThisTurn)
					{
						DuelistMod.logger.info("DuelistMod.summonedTypesThisTurn[" + counter + "]: " + t);
						counter++;
					}
				}
				
				if (!c.hasTag(Tags.TOKEN))
				{
					DuelistMod.summonCombatCount += startSummons;
					DuelistMod.summonRunCount += startSummons;
					DuelistMod.summonTurnCount += startSummons;
				}		
			}

			else
			{
				// Setup Pot of Generosity
				int potSummons = 0;
				int startSummons = p.getPower(SummonPower.POWER_ID).amount;
				SummonPower summonsInstance = (SummonPower)p.getPower(SummonPower.POWER_ID);
				int maxSummons = summonsInstance.MAX_SUMMONS;
				if ((startSummons + SUMMONS) > maxSummons) { potSummons = maxSummons - startSummons; }
				else { potSummons = SUMMONS; }

				// Add SUMMONS
				summonsInstance.amount += potSummons;

				if (potSummons > 0) 
				{ 
					for (int i = 0; i < potSummons; i++) 
					{ 
						summonsInstance.summonList.add(c.originalName);
						summonsInstance.actualCardSummonList.add((DuelistCard) c.makeStatEquivalentCopy());
					} 
				}

				// Check for Pot of Generosity
				if (p.hasPower(PotGenerosityPower.POWER_ID)) { AbstractDungeon.actionManager.addToTop(new GainEnergyAction(potSummons)); }

				// Check for Summoning Sickness
				if (p.hasPower(SummonSicknessPower.POWER_ID)) { damageSelfNotHP(potSummons * p.getPower(SummonSicknessPower.POWER_ID).amount); }

				// Check for Slifer
				if (p.hasPower(SliferSkyPower.POWER_ID) && potSummons > 0) 
				{ 
					SliferSkyPower instance = (SliferSkyPower) p.getPower(SliferSkyPower.POWER_ID);
					applyPowerToSelf(new StrengthPower(p, instance.amount * potSummons));
					applyPowerToSelf(new LoseStrengthPower(p, instance.amount * potSummons));
				} 
				
				
				// Check for Goblin's Secret Remedy
				if (p.hasPower(GoblinRemedyPower.POWER_ID) && potSummons > 0) { gainTempHP(p.getPower(GoblinRemedyPower.POWER_ID).amount);  }

				// Check for Blizzard Dragon
				if (p.hasPower(BlizzardDragonPower.POWER_ID) && c.hasTag(Tags.DRAGON)) 
				{ 
					for (int i = 0; i < potSummons; i++) { AbstractOrb frost = new Frost(); channel(frost); }
				}
				
				// Check for Toon Cannon Soldier
				if (p.hasPower(ToonCannonPower.POWER_ID) && c.hasTag(Tags.TOON) && potSummons > 0)
				{
					ToonCannonPower power = (ToonCannonPower) p.getPower(ToonCannonPower.POWER_ID);
					DuelistCard.damageAllEnemiesThornsPoison(power.amount);
				}
				
				// Check for Tripod Fish
				if (p.hasPower(TripodFishPower.POWER_ID) && c.hasTag(Tags.AQUA) && potSummons > 0)
				{
					for (int i = 0; i < p.getPower(TripodFishPower.POWER_ID).amount; i++)
					{						
						DuelistCard randAqExh = (DuelistCard) returnTrulyRandomFromSet(Tags.AQUA);					
						AbstractDungeon.actionManager.addToTop(new RandomizedExhaustPileAction(randAqExh, true));
					}
				}
				
				if (potSummons > 0)
				{
					int cursedBillGold = cursedBillGoldLoss();
					if (cursedBillGold > 0) { loseGold(cursedBillGold); }
				}
				
				// Check for Power Giants
				if (potSummons > 0)
				{
					for (AbstractCard giantCard : player().hand.group)
					{
						if (giantCard instanceof PowerGiant)
						{
							PowerGiant giant = (PowerGiant)giantCard;
							giant.damageInc();
						}
					}
					
					for (AbstractCard giantCard : player().discardPile.group)
					{
						if (giantCard instanceof PowerGiant)
						{
							PowerGiant giant = (PowerGiant)giantCard;
							giant.damageInc();
						}
					}
					
					for (AbstractCard giantCard : player().drawPile.group)
					{
						if (giantCard instanceof PowerGiant)
						{
							PowerGiant giant = (PowerGiant)giantCard;
							giant.damageInc();
						}
					}
				}
				
				// Check for Ultimate Offering
				if (p.hasPower(UltimateOfferingPower.POWER_ID) && potSummons == 0 && SUMMONS != 0)
				{
					if (DuelistMod.debug) { System.out.println("theDuelist:DuelistCard:summon() ---> hit Ultimate Offering: " + SUMMONS); }
					int amountToSummon = p.getPower(UltimateOfferingPower.POWER_ID).amount;
					damageSelf(3);
					incMaxSummons(p, amountToSummon);
					if (DuelistMod.debug) { System.out.println("theDuelist:DuelistCard:summon() ---> inside UO check, amountToSummon: " + amountToSummon); }
					uoSummon(p, amountToSummon, new Token("Blood Token"));
				}
				
				// Check for new summoned types
				if (potSummons > 0)
				{
					ArrayList<CardTags> toRet = getAllMonsterTypes(c);
					if (toRet.size() > 0)
					{
						for (CardTags t : toRet)
						{
							if (!DuelistMod.summonedTypesThisTurn.contains(t))
							{
								DuelistMod.summonedTypesThisTurn.add(t);
								if (player().hasPower(KuribohrnPower.POWER_ID))
								{
									if (DuelistMod.kuribohrnFlipper) 
									{ 
										DuelistCard randZomb = (DuelistCard) returnTrulyRandomFromSet(Tags.ZOMBIE);
										fullResummon(randZomb, false, AbstractDungeon.getRandomMonster(), false);
									}
									DuelistMod.kuribohrnFlipper = !DuelistMod.kuribohrnFlipper;
								}
							}
						}
						DuelistMod.lastTagSummoned = toRet.get(AbstractDungeon.cardRandomRng.random(toRet.size() - 1));
					}
				}
				
				if (DuelistMod.debug)
				{
					int counter = 1;
					for (CardTags t : DuelistMod.summonedTypesThisTurn)
					{
						DuelistMod.logger.info("DuelistMod.summonedTypesThisTurn[" + counter + "]: " + t);
						counter++;
					}
				}

				// Update UI
				summonsInstance.updateCount(summonsInstance.amount);
				summonsInstance.updateStringColors();
				summonsInstance.updateDescription();
				if (!c.hasTag(Tags.TOKEN))
				{
					DuelistMod.summonCombatCount += potSummons;
					DuelistMod.summonRunCount += potSummons;
					DuelistMod.summonTurnCount += potSummons;
				}		

				// Check for Trap Hole
				if (p.hasPower(TrapHolePower.POWER_ID) && !DuelistMod.checkTrap)
				{
					for (int i = 0; i < potSummons; i++)
					{
						TrapHolePower power = (TrapHolePower) p.getPower(TrapHolePower.POWER_ID);
						int randomNum = AbstractDungeon.cardRandomRng.random(1, 10);
						if (randomNum <= power.chance || power.chance > 10)
						{
							DuelistMod.checkTrap = true;
							power.flash();
							if (DuelistMod.debug) { System.out.println("theDuelist:DuelistCard:summon ---> triggered trap hole with roll of: " + randomNum); }
							powerTribute(p, 1, false);
							DuelistCard cardCopy = DuelistCard.newCopyOfMonster(c.originalName);
							if (cardCopy != null && !cardCopy.hasTag(Tags.EXEMPT))
							{
								AbstractMonster m = AbstractDungeon.getRandomMonster();
								fullResummon(cardCopy, c.upgraded, m, false);
								if (DuelistMod.debug) { System.out.println("theDuelist:DuelistCard:summon ---> trap hole resummoned properly"); }
							}
						}
						else
						{
							if (DuelistMod.debug) { System.out.println("theDuelist:DuelistCard:summon ---> did not trigger trap hole with roll of: " + randomNum); }
						}
					}
				}

				// Check for Yami
				if (p.hasPower(YamiPower.POWER_ID) && c.hasTag(Tags.SPELLCASTER))
				{
					spellSummon(p, 1, c);
				}

				// Update UI
				if (DuelistMod.debug) { System.out.println("theDuelist:DuelistCard:summon() ---> updating summons instance"); }
				summonsInstance.updateCount(summonsInstance.amount);
				summonsInstance.updateStringColors();
				summonsInstance.updateDescription();
				if (DuelistMod.debug) { System.out.println("theDuelist:DuelistCard:summon() ---> summons instance amount: " + summonsInstance.amount); }
			}
		}
		else
		{			
			if (DuelistMod.debug) { System.out.println("theDuelist:DuelistCard:summon() ---> check trap, SUMMONS: " + SUMMONS); }
			trapHoleSummon(p, SUMMONS, c);			
		}
	}

	public static void spellSummon(AbstractPlayer p, int SUMMONS, DuelistCard c)
	{
		if (hasSummoningCurse()) { return; }
		boolean challengeFailure = (Utilities.isCustomModActive("theDuelist:SummonRandomizer"));
		if (challengeFailure)
		{
			if (Utilities.isCustomModActive("challengethespire:Bronze Difficulty"))
			{
				if (AbstractDungeon.cardRandomRng.random(1, 3) == 1) 
				{ 
					AbstractDungeon.actionManager.addToBottom(new TalkAction(true, Strings.configFailedSummonActionText, 1.0F, 2.0F));
					return; 
				}
			}
			else if (Utilities.isCustomModActive("challengethespire:Silver Difficulty"))
			{
				if (AbstractDungeon.cardRandomRng.random(1, 2) == 1) 
				{ 
					AbstractDungeon.actionManager.addToBottom(new TalkAction(true, Strings.configFailedSummonActionText, 1.0F, 2.0F));
					return; 
				}
			}
			else if (Utilities.isCustomModActive("challengethespire:Gold Difficulty"))
			{
				if (AbstractDungeon.cardRandomRng.random(1, 3) != 1) 
				{ 
					AbstractDungeon.actionManager.addToBottom(new TalkAction(true, Strings.configFailedSummonActionText, 1.0F, 2.0F));
					return; 
				}
			}
			else if (Utilities.isCustomModActive("challengethespire:Platinum Difficulty"))
			{
				if (AbstractDungeon.cardRandomRng.random(1, 4) != 1) 
				{ 
					AbstractDungeon.actionManager.addToBottom(new TalkAction(true, Strings.configFailedSummonActionText, 1.0F, 2.0F));
					return; 
				}
			}
		}
		
		int currentDeck = 0;		
		if (StarterDeckSetup.getCurrentDeck().getArchetypeCards().size() > 0) { currentDeck = StarterDeckSetup.getCurrentDeck().getIndex(); }
		if (DuelistMod.debug) { System.out.println("theDuelist:DuelistCard:spellSummon() ---> called spellSummon()"); }
		if (!DuelistMod.checkTrap)
		{
			if (DuelistMod.debug) { System.out.println("theDuelist:DuelistCard:spellSummon() ---> no check trap, SUMMONS: " + SUMMONS); }
			// Check to make sure they still have summon power, if they do not give it to them with a stack of 0
			if (!p.hasPower(SummonPower.POWER_ID))
			{
				AbstractDungeon.actionManager.addToTop(new ApplyPowerAction(AbstractDungeon.player, AbstractDungeon.player, new SummonPower(AbstractDungeon.player, SUMMONS, c.originalName, "#b" + SUMMONS + " monsters summoned. Maximum of 5 Summons.", c), SUMMONS));
				int startSummons = SUMMONS;
				// Check for Pot of Generosity
				if (p.hasPower(PotGenerosityPower.POWER_ID)) { AbstractDungeon.actionManager.addToTop(new GainEnergyAction(startSummons)); }

				// Check for Summoning Sickness
				if (p.hasPower(SummonSicknessPower.POWER_ID)) { damageSelfNotHP(startSummons * p.getPower(SummonSicknessPower.POWER_ID).amount); }

				// Check for Slifer
				if (p.hasPower(SliferSkyPower.POWER_ID)) 
				{ 
					SliferSkyPower instance = (SliferSkyPower) p.getPower(SliferSkyPower.POWER_ID);
					applyPowerToSelf(new StrengthPower(p, instance.amount));
					applyPowerToSelf(new LoseStrengthPower(p, instance.amount));
				} 
				
				
				// Check for Goblin's Secret Remedy
				if (p.hasPower(GoblinRemedyPower.POWER_ID)) { gainTempHP(p.getPower(GoblinRemedyPower.POWER_ID).amount);  }
				
				// Check for Blizzard Dragon
				if (p.hasPower(BlizzardDragonPower.POWER_ID) && c.hasTag(Tags.DRAGON)) 
				{ 
					for (int i = 0; i < startSummons; i++) { AbstractOrb frost = new Frost(); channel(frost); }
				}
				
				// Check for Toon Cannon Soldier
				if (p.hasPower(ToonCannonPower.POWER_ID) && c.hasTag(Tags.TOON))
				{
					ToonCannonPower power = (ToonCannonPower) p.getPower(ToonCannonPower.POWER_ID);
					DuelistCard.damageAllEnemiesThornsPoison(power.amount);
				}
				
				// Check for Tripod Fish
				if (p.hasPower(TripodFishPower.POWER_ID) && c.hasTag(Tags.AQUA))
				{
					for (int i = 0; i < p.getPower(TripodFishPower.POWER_ID).amount; i++)
					{						
						DuelistCard randAqExh = (DuelistCard) returnTrulyRandomFromSet(Tags.AQUA);					
						AbstractDungeon.actionManager.addToTop(new RandomizedExhaustPileAction(randAqExh, true));
					}
				}
				
				int cursedBillGold = cursedBillGoldLoss();
				if (cursedBillGold > 0) { loseGold(cursedBillGold); }
				
				// Check for Power Giants
				for (AbstractCard giantCard : player().hand.group)
				{
					if (giantCard instanceof PowerGiant)
					{
						PowerGiant giant = (PowerGiant)giantCard;
						giant.damageInc();
					}
				}
				
				for (AbstractCard giantCard : player().discardPile.group)
				{
					if (giantCard instanceof PowerGiant)
					{
						PowerGiant giant = (PowerGiant)giantCard;
						giant.damageInc();
					}
				}
				
				for (AbstractCard giantCard : player().drawPile.group)
				{
					if (giantCard instanceof PowerGiant)
					{
						PowerGiant giant = (PowerGiant)giantCard;
						giant.damageInc();
					}
				}
				
				// Check for new summoned types			
				ArrayList<CardTags> toRet = getAllMonsterTypes(c);
				if (toRet.size() > 0)
				{
					for (CardTags t : toRet)
					{
						if (!DuelistMod.summonedTypesThisTurn.contains(t))
						{
							DuelistMod.summonedTypesThisTurn.add(t);
							if (player().hasPower(KuribohrnPower.POWER_ID))
							{
								if (DuelistMod.kuribohrnFlipper) 
								{ 
									DuelistCard randZomb = (DuelistCard) returnTrulyRandomFromSet(Tags.ZOMBIE);
									fullResummon(randZomb, false, AbstractDungeon.getRandomMonster(), false);
								}
								DuelistMod.kuribohrnFlipper = !DuelistMod.kuribohrnFlipper;
							}
						}
					}
					DuelistMod.lastTagSummoned = toRet.get(AbstractDungeon.cardRandomRng.random(toRet.size() - 1));
				}
				
				if (DuelistMod.debug)
				{
					int counter = 1;
					for (CardTags t : DuelistMod.summonedTypesThisTurn)
					{
						DuelistMod.logger.info("DuelistMod.summonedTypesThisTurn[" + counter + "]: " + t);
						counter++;
					}
				}
				
				if (!c.hasTag(Tags.TOKEN))
				{
					DuelistMod.summonCombatCount += startSummons;
					DuelistMod.summonRunCount += startSummons;
					DuelistMod.summonTurnCount += startSummons;
				}		
			}

			else
			{
				// Setup Pot of Generosity
				int potSummons = 0;
				int startSummons = p.getPower(SummonPower.POWER_ID).amount;
				SummonPower summonsInstance = (SummonPower)p.getPower(SummonPower.POWER_ID);
				int maxSummons = summonsInstance.MAX_SUMMONS;
				if ((startSummons + SUMMONS) > maxSummons) { potSummons = maxSummons - startSummons; }
				else { potSummons = SUMMONS; }

				// Add SUMMONS
				summonsInstance.amount += potSummons;

				if (potSummons > 0) 
				{ 
					for (int i = 0; i < potSummons; i++) 
					{ 
						summonsInstance.summonList.add(c.originalName);
						summonsInstance.actualCardSummonList.add((DuelistCard) c.makeStatEquivalentCopy());
					} 
				}

				// Check for Pot of Generosity
				if (p.hasPower(PotGenerosityPower.POWER_ID)) { AbstractDungeon.actionManager.addToTop(new GainEnergyAction(potSummons)); }

				// Check for Summoning Sickness
				if (p.hasPower(SummonSicknessPower.POWER_ID)) { damageSelfNotHP(potSummons * p.getPower(SummonSicknessPower.POWER_ID).amount); }

				// Check for Slifer
				if (p.hasPower(SliferSkyPower.POWER_ID) && potSummons > 0) 
				{ 
					SliferSkyPower instance = (SliferSkyPower) p.getPower(SliferSkyPower.POWER_ID);
					applyPowerToSelf(new StrengthPower(p, instance.amount * potSummons));
					applyPowerToSelf(new LoseStrengthPower(p, instance.amount * potSummons));
				} 
				
				
				// Check for Goblin's Secret Remedy
				if (p.hasPower(GoblinRemedyPower.POWER_ID) && potSummons > 0) { gainTempHP(p.getPower(GoblinRemedyPower.POWER_ID).amount);  }

				// Check for Blizzard Dragon
				if (p.hasPower(BlizzardDragonPower.POWER_ID) && c.hasTag(Tags.DRAGON)) 
				{ 
					for (int i = 0; i < potSummons; i++) { AbstractOrb frost = new Frost(); channel(frost); }
				}
				
				// Check for Toon Cannon Soldier
				if (p.hasPower(ToonCannonPower.POWER_ID) && c.hasTag(Tags.TOON) && potSummons > 0)
				{
					ToonCannonPower power = (ToonCannonPower) p.getPower(ToonCannonPower.POWER_ID);
					DuelistCard.damageAllEnemiesThornsPoison(power.amount);
				}
				
				// Check for Tripod Fish
				if (p.hasPower(TripodFishPower.POWER_ID) && c.hasTag(Tags.AQUA) && potSummons > 0)
				{
					for (int i = 0; i < p.getPower(TripodFishPower.POWER_ID).amount; i++)
					{						
						DuelistCard randAqExh = (DuelistCard) returnTrulyRandomFromSet(Tags.AQUA);					
						AbstractDungeon.actionManager.addToTop(new RandomizedExhaustPileAction(randAqExh, true));
					}
				}
				
				if (potSummons > 0)
				{
					int cursedBillGold = cursedBillGoldLoss();
					if (cursedBillGold > 0) { loseGold(cursedBillGold); }
				}
				
				// Check for Power Giants
				if (potSummons > 0)
				{
					for (AbstractCard giantCard : player().hand.group)
					{
						if (giantCard instanceof PowerGiant)
						{
							PowerGiant giant = (PowerGiant)giantCard;
							giant.damageInc();
						}
					}
					
					for (AbstractCard giantCard : player().discardPile.group)
					{
						if (giantCard instanceof PowerGiant)
						{
							PowerGiant giant = (PowerGiant)giantCard;
							giant.damageInc();
						}
					}
					
					for (AbstractCard giantCard : player().drawPile.group)
					{
						if (giantCard instanceof PowerGiant)
						{
							PowerGiant giant = (PowerGiant)giantCard;
							giant.damageInc();
						}
					}
				}
				
				// Check for new summoned types
				if (potSummons > 0)
				{
					ArrayList<CardTags> toRet = getAllMonsterTypes(c);
					if (toRet.size() > 0)
					{
						for (CardTags t : toRet)
						{
							if (!DuelistMod.summonedTypesThisTurn.contains(t))
							{
								DuelistMod.summonedTypesThisTurn.add(t);
								if (player().hasPower(KuribohrnPower.POWER_ID))
								{
									if (DuelistMod.kuribohrnFlipper) 
									{ 
										DuelistCard randZomb = (DuelistCard) returnTrulyRandomFromSet(Tags.ZOMBIE);
										fullResummon(randZomb, false, AbstractDungeon.getRandomMonster(), false);
									}
									DuelistMod.kuribohrnFlipper = !DuelistMod.kuribohrnFlipper;
								}
							}
						}
						DuelistMod.lastTagSummoned = toRet.get(AbstractDungeon.cardRandomRng.random(toRet.size() - 1));
					}
				}
				
				if (DuelistMod.debug)
				{
					int counter = 1;
					for (CardTags t : DuelistMod.summonedTypesThisTurn)
					{
						DuelistMod.logger.info("DuelistMod.summonedTypesThisTurn[" + counter + "]: " + t);
						counter++;
					}
				}
				
				// Update UI
				summonsInstance.updateCount(summonsInstance.amount);
				summonsInstance.updateStringColors();
				summonsInstance.updateDescription();
				if (!c.hasTag(Tags.TOKEN))
				{
					DuelistMod.summonCombatCount += potSummons;
					DuelistMod.summonRunCount += potSummons;
					DuelistMod.summonTurnCount += potSummons;
				}		

			}
		}

		else
		{			
			if (DuelistMod.debug) { System.out.println("theDuelist:DuelistCard:spellSummon() ---> check trap, SUMMONS: " + SUMMONS); }
			trapHoleSummon(p, SUMMONS, c);		
		}
	}


	public static void powerSummon(AbstractPlayer p, int SUMMONS, String cardName, boolean fromUO)
	{
		if (hasSummoningCurse()) { return; }
		boolean challengeFailure = (Utilities.isCustomModActive("theDuelist:SummonRandomizer"));
		if (challengeFailure)
		{
			if (Utilities.isCustomModActive("challengethespire:Bronze Difficulty"))
			{
				if (AbstractDungeon.cardRandomRng.random(1, 3) == 1) 
				{ 
					AbstractDungeon.actionManager.addToBottom(new TalkAction(true, Strings.configFailedSummonActionText, 1.0F, 2.0F));
					return; 
				}
			}
			else if (Utilities.isCustomModActive("challengethespire:Silver Difficulty"))
			{
				if (AbstractDungeon.cardRandomRng.random(1, 2) == 1) 
				{ 
					AbstractDungeon.actionManager.addToBottom(new TalkAction(true, Strings.configFailedSummonActionText, 1.0F, 2.0F));
					return; 
				}
			}
			else if (Utilities.isCustomModActive("challengethespire:Gold Difficulty"))
			{
				if (AbstractDungeon.cardRandomRng.random(1, 3) != 1) 
				{ 
					AbstractDungeon.actionManager.addToBottom(new TalkAction(true, Strings.configFailedSummonActionText, 1.0F, 2.0F));
					return; 
				}
			}
			else if (Utilities.isCustomModActive("challengethespire:Platinum Difficulty"))
			{
				if (AbstractDungeon.cardRandomRng.random(1, 4) != 1) 
				{ 
					AbstractDungeon.actionManager.addToBottom(new TalkAction(true, Strings.configFailedSummonActionText, 1.0F, 2.0F));
					return; 
				}
			}
		}
		
		int currentDeck = 0;		
		if (StarterDeckSetup.getCurrentDeck().getArchetypeCards().size() > 0) { currentDeck = StarterDeckSetup.getCurrentDeck().getIndex(); }
		if (DuelistMod.debug) { System.out.println("theDuelist:DuelistCard:powerSummon() ---> called powerSummon()"); }
		if (!DuelistMod.checkTrap)
		{
			if (DuelistMod.debug) { System.out.println("theDuelist:DuelistCard:powerSummon() ---> no check trap, SUMMONS: " + SUMMONS); }
			DuelistCard c = DuelistMod.summonMap.get(cardName);
			if (DuelistMod.debug) { System.out.println("theDuelist:DuelistCard:powerSummon() ---> c: " + c.originalName); }
			// Check to make sure they still have summon power, if they do not give it to them with a stack of 0
			if (!p.hasPower(SummonPower.POWER_ID))
			{
				//DuelistCard newSummonCard = (DuelistCard) DefaultMod.summonMap.get(cardName).makeCopy();
				AbstractDungeon.actionManager.addToTop(new ApplyPowerAction(AbstractDungeon.player, AbstractDungeon.player, new SummonPower(AbstractDungeon.player, SUMMONS, cardName, "#b" + SUMMONS + " monsters summoned. Maximum of 5 Summons."), SUMMONS));
				int startSummons = SUMMONS;
				// Check for Pot of Generosity
				if (p.hasPower(PotGenerosityPower.POWER_ID)) { AbstractDungeon.actionManager.addToTop(new GainEnergyAction(startSummons)); }

				// Check for Summoning Sickness
				if (p.hasPower(SummonSicknessPower.POWER_ID)) { damageSelfNotHP(startSummons * p.getPower(SummonSicknessPower.POWER_ID).amount); }

				// Check for Slifer
				if (p.hasPower(SliferSkyPower.POWER_ID)) 
				{ 
					SliferSkyPower instance = (SliferSkyPower) p.getPower(SliferSkyPower.POWER_ID);
					applyPowerToSelf(new StrengthPower(p, instance.amount));
					applyPowerToSelf(new LoseStrengthPower(p, instance.amount));
				} 
				
				
				// Check for Goblin's Secret Remedy
				if (p.hasPower(GoblinRemedyPower.POWER_ID)) {gainTempHP(p.getPower(GoblinRemedyPower.POWER_ID).amount); }
				
				// Check for Blizzard Dragon
				if (p.hasPower(BlizzardDragonPower.POWER_ID) && c.hasTag(Tags.DRAGON)) 
				{ 
					for (int i = 0; i < startSummons; i++) { AbstractOrb frost = new Frost(); channel(frost); }
				}
				// Check for Toon Cannon Soldier
				if (p.hasPower(ToonCannonPower.POWER_ID) && c.hasTag(Tags.TOON))
				{
					ToonCannonPower power = (ToonCannonPower) p.getPower(ToonCannonPower.POWER_ID);
					DuelistCard.damageAllEnemiesThornsPoison(power.amount);
				}
				
				// Check for Tripod Fish
				if (p.hasPower(TripodFishPower.POWER_ID) && c.hasTag(Tags.AQUA))
				{
					for (int i = 0; i < p.getPower(TripodFishPower.POWER_ID).amount; i++)
					{						
						DuelistCard randAqExh = (DuelistCard) returnTrulyRandomFromSet(Tags.AQUA);					
						AbstractDungeon.actionManager.addToTop(new RandomizedExhaustPileAction(randAqExh, true));
					}
				}
				
				int cursedBillGold = cursedBillGoldLoss();
				if (cursedBillGold > 0) { loseGold(cursedBillGold); }
				
				// Check for Power Giants
				for (AbstractCard giantCard : player().hand.group)
				{
					if (giantCard instanceof PowerGiant)
					{
						PowerGiant giant = (PowerGiant)giantCard;
						giant.damageInc();
					}
				}
				
				for (AbstractCard giantCard : player().discardPile.group)
				{
					if (giantCard instanceof PowerGiant)
					{
						PowerGiant giant = (PowerGiant)giantCard;
						giant.damageInc();
					}
				}
				
				for (AbstractCard giantCard : player().drawPile.group)
				{
					if (giantCard instanceof PowerGiant)
					{
						PowerGiant giant = (PowerGiant)giantCard;
						giant.damageInc();
					}
				}
				
				// Check for new summoned types
				ArrayList<CardTags> toRet = getAllMonsterTypes(c);
				if (toRet.size() > 0)
				{
					for (CardTags t : toRet)
					{
						if (!DuelistMod.summonedTypesThisTurn.contains(t))
						{
							DuelistMod.summonedTypesThisTurn.add(t);
							if (player().hasPower(KuribohrnPower.POWER_ID))
							{
								if (DuelistMod.kuribohrnFlipper) 
								{ 
									DuelistCard randZomb = (DuelistCard) returnTrulyRandomFromSet(Tags.ZOMBIE);
									fullResummon(randZomb, false, AbstractDungeon.getRandomMonster(), false);
								}
								DuelistMod.kuribohrnFlipper = !DuelistMod.kuribohrnFlipper;
							}
						}
						DuelistMod.lastTagSummoned = toRet.get(AbstractDungeon.cardRandomRng.random(toRet.size() - 1));
					}
				}
				
				if (DuelistMod.debug)
				{
					int counter = 1;
					for (CardTags t : DuelistMod.summonedTypesThisTurn)
					{
						DuelistMod.logger.info("DuelistMod.summonedTypesThisTurn[" + counter + "]: " + t);
						counter++;
					}
				}
				
				if (!c.hasTag(Tags.TOKEN))
				{
					DuelistMod.summonCombatCount += startSummons;
					DuelistMod.summonRunCount += startSummons;
					DuelistMod.summonTurnCount += startSummons;
				}
				
			}
			else
			{
				// Setup Pot of Generosity
				int potSummons = 0;
				int startSummons = p.getPower(SummonPower.POWER_ID).amount;
				SummonPower summonsInstance = (SummonPower)p.getPower(SummonPower.POWER_ID);
				int maxSummons = summonsInstance.MAX_SUMMONS;
				if ((startSummons + SUMMONS) > maxSummons) { potSummons = maxSummons - startSummons; }
				else { potSummons = SUMMONS; }

				// Add SUMMONS
				summonsInstance.amount += potSummons;

				if (potSummons > 0) { for (int i = 0; i < potSummons; i++) { summonsInstance.summonList.add(cardName); summonsInstance.actualCardSummonList.add((DuelistCard) c.makeStatEquivalentCopy());} }

				// Check for Pot of Generosity
				if (p.hasPower(PotGenerosityPower.POWER_ID)) { AbstractDungeon.actionManager.addToTop(new GainEnergyAction(potSummons)); }

				// Check for Summoning Sickness
				if (p.hasPower(SummonSicknessPower.POWER_ID)) { damageSelfNotHP(potSummons * p.getPower(SummonSicknessPower.POWER_ID).amount); }

				// Check for Slifer
				if (p.hasPower(SliferSkyPower.POWER_ID) && potSummons > 0) 
				{ 
					SliferSkyPower instance = (SliferSkyPower) p.getPower(SliferSkyPower.POWER_ID);
					applyPowerToSelf(new StrengthPower(p, instance.amount * potSummons));
					applyPowerToSelf(new LoseStrengthPower(p, instance.amount * potSummons));
				} 
				
				
				// Check for Goblin's Secret Remedy
				if (p.hasPower(GoblinRemedyPower.POWER_ID)) {gainTempHP(p.getPower(GoblinRemedyPower.POWER_ID).amount); }

				// Check for Blizzard Dragon
				if (p.hasPower(BlizzardDragonPower.POWER_ID) && c.hasTag(Tags.DRAGON)) 
				{ 
					for (int i = 0; i < potSummons; i++) { AbstractOrb frost = new Frost(); channel(frost); }
				}
				
				// Check for Toon Cannon Soldier
				if (p.hasPower(ToonCannonPower.POWER_ID) && c.hasTag(Tags.TOON) && potSummons > 0)
				{
					ToonCannonPower power = (ToonCannonPower) p.getPower(ToonCannonPower.POWER_ID);
					DuelistCard.damageAllEnemiesThornsPoison(power.amount);
				}
				
				// Check for Tripod Fish
				if (p.hasPower(TripodFishPower.POWER_ID) && c.hasTag(Tags.AQUA) && potSummons > 0)
				{
					for (int i = 0; i < p.getPower(TripodFishPower.POWER_ID).amount; i++)
					{						
						DuelistCard randAqExh = (DuelistCard) returnTrulyRandomFromSet(Tags.AQUA);					
						AbstractDungeon.actionManager.addToTop(new RandomizedExhaustPileAction(randAqExh, true));
					}
				}
				
				if (potSummons > 0)
				{
					int cursedBillGold = cursedBillGoldLoss();
					if (cursedBillGold > 0) { loseGold(cursedBillGold); }
				}
				
				// Check for Power Giants
				if (potSummons > 0)
				{
					for (AbstractCard giantCard : player().hand.group)
					{
						if (giantCard instanceof PowerGiant)
						{
							PowerGiant giant = (PowerGiant)giantCard;
							giant.damageInc();
						}
					}
					
					for (AbstractCard giantCard : player().discardPile.group)
					{
						if (giantCard instanceof PowerGiant)
						{
							PowerGiant giant = (PowerGiant)giantCard;
							giant.damageInc();
						}
					}
					
					for (AbstractCard giantCard : player().drawPile.group)
					{
						if (giantCard instanceof PowerGiant)
						{
							PowerGiant giant = (PowerGiant)giantCard;
							giant.damageInc();
						}
					}
				}
				
				// Check for Ultimate Offering
				if (p.hasPower(UltimateOfferingPower.POWER_ID) && potSummons == 0 && SUMMONS != 0 && !fromUO)
				{
					if (DuelistMod.debug) { System.out.println("theDuelist:DuelistCard:powerSummon() ---> hit Ultimate Offering, SUMMONS: " + SUMMONS); }
					int amountToSummon = p.getPower(UltimateOfferingPower.POWER_ID).amount;
					damageSelf(3);
					incMaxSummons(p, amountToSummon);
					if (DuelistMod.debug) { System.out.println("theDuelist:DuelistCard:powerSummon() ---> inside UO check, amountToSummon: " + amountToSummon); }
					uoSummon(p, amountToSummon, new Token("Blood Token"));
				}
				
				// Check for new summoned types
				if (potSummons > 0)
				{
					ArrayList<CardTags> toRet = getAllMonsterTypes(c);
					if (toRet.size() > 0)
					{
						for (CardTags t : toRet)
						{
							if (!DuelistMod.summonedTypesThisTurn.contains(t))
							{
								DuelistMod.summonedTypesThisTurn.add(t);
								if (player().hasPower(KuribohrnPower.POWER_ID))
								{
									if (DuelistMod.kuribohrnFlipper) 
									{ 
										DuelistCard randZomb = (DuelistCard) returnTrulyRandomFromSet(Tags.ZOMBIE);
										fullResummon(randZomb, false, AbstractDungeon.getRandomMonster(), false);
									}
									DuelistMod.kuribohrnFlipper = !DuelistMod.kuribohrnFlipper;
								}
							}
						}
						DuelistMod.lastTagSummoned = toRet.get(AbstractDungeon.cardRandomRng.random(toRet.size() - 1));
					}
				}
				
				if (DuelistMod.debug)
				{
					int counter = 1;
					for (CardTags t : DuelistMod.summonedTypesThisTurn)
					{
						DuelistMod.logger.info("DuelistMod.summonedTypesThisTurn[" + counter + "]: " + t);
						counter++;
					}
				}

				// Update UI
				summonsInstance.updateCount(summonsInstance.amount);
				summonsInstance.updateStringColors();
				summonsInstance.updateDescription();
				if (!c.hasTag(Tags.TOKEN))
				{
					DuelistMod.summonCombatCount += potSummons;
					DuelistMod.summonRunCount += potSummons;
					DuelistMod.summonTurnCount += potSummons;
				}		

				// Check for Trap Hole
				if (p.hasPower(TrapHolePower.POWER_ID) && !DuelistMod.checkTrap)
				{
					for (int i = 0; i < potSummons; i++)
					{
						TrapHolePower power = (TrapHolePower) p.getPower(TrapHolePower.POWER_ID);
						int randomNum = AbstractDungeon.cardRandomRng.random(1, 10);
						if (randomNum <= power.chance || power.chance > 10)
						{
							DuelistMod.checkTrap = true;
							power.flash();
							if (DuelistMod.debug) { System.out.println("theDuelist:DuelistCard:powerSummon ---> triggered trap hole with roll of: " + randomNum); }
							powerTribute(p, 1, false);
							DuelistCard cardCopy = DuelistCard.newCopyOfMonster(c.originalName);
							if (cardCopy != null && !cardCopy.hasTag(Tags.EXEMPT))
							{
								AbstractMonster m = AbstractDungeon.getRandomMonster();
								fullResummon(cardCopy, c.upgraded, m, false);
								if (DuelistMod.debug) { System.out.println("theDuelist:DuelistCard:powerSummon ---> trap hole resummoned properly"); }
							}
						}
						else
						{
							if (DuelistMod.debug) { System.out.println("theDuelist:DuelistCard:powerSummon ---> did not trigger trap hole with roll of: " + randomNum); }
						}
					}
				}

				// Check for Yami
				if (p.hasPower(YamiPower.POWER_ID) && c.hasTag(Tags.SPELLCASTER))
				{
					spellSummon(p, 1, c);
				}

				// Update UI
				if (DuelistMod.debug) { System.out.println("theDuelist:DuelistCard:powerSummon() ---> updating summons instance amount"); }
				summonsInstance.updateCount(summonsInstance.amount);
				summonsInstance.updateStringColors();
				summonsInstance.updateDescription();
				if (DuelistMod.debug) { System.out.println("theDuelist:DuelistCard:powerSummon() ---> summons instance amount: " + summonsInstance.amount); }
			}
		}

		else
		{
			if (DuelistMod.debug) { System.out.println("theDuelist:DuelistCard:powerSummon() ---> check trap, SUMMONS: " + SUMMONS); }
			DuelistCard c = DuelistMod.summonMap.get(cardName);
			if (DuelistMod.debug) { System.out.println("theDuelist:DuelistCard:powerSummon() ---> check trap, c: " + c.originalName); }
			trapHoleSummon(p, SUMMONS, c);
		}
	}

	public static void trapHoleSummon(AbstractPlayer p, int SUMMONS, DuelistCard c)
	{		
		if (hasSummoningCurse()) { return; }
		boolean challengeFailure = (Utilities.isCustomModActive("theDuelist:SummonRandomizer"));
		if (challengeFailure)
		{
			if (Utilities.isCustomModActive("challengethespire:Bronze Difficulty"))
			{
				if (AbstractDungeon.cardRandomRng.random(1, 3) == 1) 
				{ 
					AbstractDungeon.actionManager.addToBottom(new TalkAction(true, Strings.configFailedSummonActionText, 1.0F, 2.0F));
					return; 
				}
			}
			else if (Utilities.isCustomModActive("challengethespire:Silver Difficulty"))
			{
				if (AbstractDungeon.cardRandomRng.random(1, 2) == 1) 
				{ 
					AbstractDungeon.actionManager.addToBottom(new TalkAction(true, Strings.configFailedSummonActionText, 1.0F, 2.0F));
					return; 
				}
			}
			else if (Utilities.isCustomModActive("challengethespire:Gold Difficulty"))
			{
				if (AbstractDungeon.cardRandomRng.random(1, 3) != 1) 
				{ 
					AbstractDungeon.actionManager.addToBottom(new TalkAction(true, Strings.configFailedSummonActionText, 1.0F, 2.0F));
					return; 
				}
			}
			else if (Utilities.isCustomModActive("challengethespire:Platinum Difficulty"))
			{
				if (AbstractDungeon.cardRandomRng.random(1, 4) != 1) 
				{ 
					AbstractDungeon.actionManager.addToBottom(new TalkAction(true, Strings.configFailedSummonActionText, 1.0F, 2.0F));
					return; 
				}
			}
		}
		
		int currentDeck = 0;		
		if (StarterDeckSetup.getCurrentDeck().getArchetypeCards().size() > 0) { currentDeck = StarterDeckSetup.getCurrentDeck().getIndex(); }
		if (DuelistMod.debug) { System.out.println("theDuelist:DuelistCard:trapHoleSummon() ---> called trapHoleSummon()"); }
		// Check to make sure they still have summon power, if they do not give it to them with a stack of 0
		if (!p.hasPower(SummonPower.POWER_ID))
		{
			AbstractDungeon.actionManager.addToTop(new ApplyPowerAction(AbstractDungeon.player, AbstractDungeon.player, new SummonPower(AbstractDungeon.player, SUMMONS, c.originalName, "#b" + SUMMONS + " monsters summoned. Maximum of 5 Summons.", c), SUMMONS));
			int startSummons = SUMMONS;
			// Check for Pot of Generosity
			if (p.hasPower(PotGenerosityPower.POWER_ID)) { AbstractDungeon.actionManager.addToTop(new GainEnergyAction(startSummons)); }

			// Check for Summoning Sickness
			if (p.hasPower(SummonSicknessPower.POWER_ID)) { damageSelfNotHP(startSummons * p.getPower(SummonSicknessPower.POWER_ID).amount); }

			// Check for Slifer
			if (p.hasPower(SliferSkyPower.POWER_ID)) 
			{ 
				SliferSkyPower instance = (SliferSkyPower) p.getPower(SliferSkyPower.POWER_ID);
				applyPowerToSelf(new StrengthPower(p, instance.amount));
				applyPowerToSelf(new LoseStrengthPower(p, instance.amount));
			} 
			
			
			// Check for Goblin's Secret Remedy
			if (p.hasPower(GoblinRemedyPower.POWER_ID)) { gainTempHP(p.getPower(GoblinRemedyPower.POWER_ID).amount);  }
			
			// Check for Blizzard Dragon
			if (p.hasPower(BlizzardDragonPower.POWER_ID) && c.hasTag(Tags.DRAGON)) 
			{ 
				for (int i = 0; i < startSummons; i++) { AbstractOrb frost = new Frost(); channel(frost); }
			}
			// Check for Toon Cannon Soldier
			if (p.hasPower(ToonCannonPower.POWER_ID) && c.hasTag(Tags.TOON))
			{
				ToonCannonPower power = (ToonCannonPower) p.getPower(ToonCannonPower.POWER_ID);
				DuelistCard.damageAllEnemiesThornsPoison(power.amount);
			}
			
			// Check for Tripod Fish
			if (p.hasPower(TripodFishPower.POWER_ID) && c.hasTag(Tags.AQUA))
			{
				for (int i = 0; i < p.getPower(TripodFishPower.POWER_ID).amount; i++)
				{						
					DuelistCard randAqExh = (DuelistCard) returnTrulyRandomFromSet(Tags.AQUA);					
					AbstractDungeon.actionManager.addToTop(new RandomizedExhaustPileAction(randAqExh, true));
				}
			}
			
			int cursedBillGold = cursedBillGoldLoss();
			if (cursedBillGold > 0) { loseGold(cursedBillGold); }
			
			// Check for Power Giants
			for (AbstractCard giantCard : player().hand.group)
			{
				if (giantCard instanceof PowerGiant)
				{
					PowerGiant giant = (PowerGiant)giantCard;
					giant.damageInc();
				}
			}
			
			for (AbstractCard giantCard : player().discardPile.group)
			{
				if (giantCard instanceof PowerGiant)
				{
					PowerGiant giant = (PowerGiant)giantCard;
					giant.damageInc();
				}
			}
			
			for (AbstractCard giantCard : player().drawPile.group)
			{
				if (giantCard instanceof PowerGiant)
				{
					PowerGiant giant = (PowerGiant)giantCard;
					giant.damageInc();
				}
			}
			
			// Check for new summoned types
			ArrayList<CardTags> toRet = getAllMonsterTypes(c);
			if (toRet.size() > 0)
			{
				for (CardTags t : toRet)
				{
					if (!DuelistMod.summonedTypesThisTurn.contains(t))
					{
						DuelistMod.summonedTypesThisTurn.add(t);
						if (player().hasPower(KuribohrnPower.POWER_ID))
						{
							if (DuelistMod.kuribohrnFlipper) 
							{ 
								DuelistCard randZomb = (DuelistCard) returnTrulyRandomFromSet(Tags.ZOMBIE);
								fullResummon(randZomb, false, AbstractDungeon.getRandomMonster(), false);
							}
							DuelistMod.kuribohrnFlipper = !DuelistMod.kuribohrnFlipper;
						}
					}
				}
				DuelistMod.lastTagSummoned = toRet.get(AbstractDungeon.cardRandomRng.random(toRet.size() - 1));
			}
			
			if (DuelistMod.debug)
			{
				int counter = 1;
				for (CardTags t : DuelistMod.summonedTypesThisTurn)
				{
					DuelistMod.logger.info("DuelistMod.summonedTypesThisTurn[" + counter + "]: " + t);
					counter++;
				}
			}
			
			if (!c.hasTag(Tags.TOKEN))
			{
				DuelistMod.summonCombatCount += startSummons;
				DuelistMod.summonRunCount += startSummons;
				DuelistMod.summonTurnCount += startSummons;
			}		
		}

		else
		{
			// Setup Pot of Generosity
			int potSummons = 0;
			int startSummons = p.getPower(SummonPower.POWER_ID).amount;
			SummonPower summonsInstance = (SummonPower)p.getPower(SummonPower.POWER_ID);
			int maxSummons = summonsInstance.MAX_SUMMONS;
			if ((startSummons + SUMMONS) > maxSummons) { potSummons = maxSummons - startSummons; }
			else { potSummons = SUMMONS; }

			// Add SUMMONS
			summonsInstance.amount += potSummons;

			if (potSummons > 0) 
			{ 
				for (int i = 0; i < potSummons; i++) 
				{ 
					summonsInstance.summonList.add(c.originalName);
					summonsInstance.actualCardSummonList.add((DuelistCard) c.makeStatEquivalentCopy());
				} 
			}

			// Check for Pot of Generosity
			if (p.hasPower(PotGenerosityPower.POWER_ID)) { AbstractDungeon.actionManager.addToTop(new GainEnergyAction(potSummons)); }

			// Check for Summoning Sickness
			if (p.hasPower(SummonSicknessPower.POWER_ID)) { damageSelfNotHP(potSummons * p.getPower(SummonSicknessPower.POWER_ID).amount); }

			// Check for Slifer
			if (p.hasPower(SliferSkyPower.POWER_ID) && potSummons > 0) 
			{ 
				SliferSkyPower instance = (SliferSkyPower) p.getPower(SliferSkyPower.POWER_ID);
				applyPowerToSelf(new StrengthPower(p, instance.amount * potSummons));
				applyPowerToSelf(new LoseStrengthPower(p, instance.amount * potSummons));
			} 
			
			// Check for Goblin's Secret Remedy
			if (p.hasPower(GoblinRemedyPower.POWER_ID) && potSummons > 0) { gainTempHP(p.getPower(GoblinRemedyPower.POWER_ID).amount);  }

			// Check for Blizzard Dragon
			if (p.hasPower(BlizzardDragonPower.POWER_ID) && c.hasTag(Tags.DRAGON)) 
			{ 
				for (int i = 0; i < potSummons; i++) { AbstractOrb frost = new Frost(); channel(frost); }
			}
			
			// Check for Toon Cannon Soldier
			if (p.hasPower(ToonCannonPower.POWER_ID) && c.hasTag(Tags.TOON) && potSummons > 0)
			{
				ToonCannonPower power = (ToonCannonPower) p.getPower(ToonCannonPower.POWER_ID);
				DuelistCard.damageAllEnemiesThornsPoison(power.amount);
			}
			
			// Check for Tripod Fish
			if (p.hasPower(TripodFishPower.POWER_ID) && c.hasTag(Tags.AQUA) && potSummons > 0)
			{
				for (int i = 0; i < p.getPower(TripodFishPower.POWER_ID).amount; i++)
				{						
					DuelistCard randAqExh = (DuelistCard) returnTrulyRandomFromSet(Tags.AQUA);					
					AbstractDungeon.actionManager.addToTop(new RandomizedExhaustPileAction(randAqExh, true));
				}
			}
			
			if (potSummons > 0)
			{
				int cursedBillGold = cursedBillGoldLoss();
				if (cursedBillGold > 0) { loseGold(cursedBillGold); }
			}
			
			// Check for Power Giants
			if (potSummons > 0)
			{
				for (AbstractCard giantCard : player().hand.group)
				{
					if (giantCard instanceof PowerGiant)
					{
						PowerGiant giant = (PowerGiant)giantCard;
						giant.damageInc();
					}
				}
				
				for (AbstractCard giantCard : player().discardPile.group)
				{
					if (giantCard instanceof PowerGiant)
					{
						PowerGiant giant = (PowerGiant)giantCard;
						giant.damageInc();
					}
				}
				
				for (AbstractCard giantCard : player().drawPile.group)
				{
					if (giantCard instanceof PowerGiant)
					{
						PowerGiant giant = (PowerGiant)giantCard;
						giant.damageInc();
					}
				}
			}
			
			// Check for Ultimate Offering
			if (p.hasPower(UltimateOfferingPower.POWER_ID) && !DuelistMod.checkUO)
			{
				//DuelistMod.checkUO = true;
				if (DuelistMod.debug) { System.out.println("theDuelist:DuelistCard:trapHoleSummon() ---> hit Ultimate Offering, SUMMONS: " + SUMMONS + " HARD CODED NOT TO TRIGGER!"); }
			}
			
			// Check for new summoned types
			if (potSummons > 0)
			{
				ArrayList<CardTags> toRet = getAllMonsterTypes(c);
				if (toRet.size() > 0)
				{
					for (CardTags t : toRet)
					{
						if (!DuelistMod.summonedTypesThisTurn.contains(t))
						{
							DuelistMod.summonedTypesThisTurn.add(t);
							if (player().hasPower(KuribohrnPower.POWER_ID))
							{
								if (DuelistMod.kuribohrnFlipper) 
								{ 
									DuelistCard randZomb = (DuelistCard) returnTrulyRandomFromSet(Tags.ZOMBIE);
									fullResummon(randZomb, false, AbstractDungeon.getRandomMonster(), false);
								}
								DuelistMod.kuribohrnFlipper = !DuelistMod.kuribohrnFlipper;
							}
						}
					}
					DuelistMod.lastTagSummoned = toRet.get(AbstractDungeon.cardRandomRng.random(toRet.size() - 1));
				}
			}
			
			if (DuelistMod.debug)
			{
				int counter = 1;
				for (CardTags t : DuelistMod.summonedTypesThisTurn)
				{
					DuelistMod.logger.info("DuelistMod.summonedTypesThisTurn[" + counter + "]: " + t);
					counter++;
				}
			}


			// Update UI
			summonsInstance.updateCount(summonsInstance.amount);
			summonsInstance.updateStringColors();
			summonsInstance.updateDescription();
			if (!c.hasTag(Tags.TOKEN))
			{
				DuelistMod.summonCombatCount += potSummons;
				DuelistMod.summonRunCount += potSummons;
				DuelistMod.summonTurnCount += potSummons;
			}		
			DuelistMod.checkUO = false;
			DuelistMod.checkTrap = false;
		}
	}

	public static void uoSummon(AbstractPlayer p, int SUMMONS, DuelistCard c)
	{		
		if (hasSummoningCurse()) { return; }
		boolean challengeFailure = (Utilities.isCustomModActive("theDuelist:SummonRandomizer"));
		if (challengeFailure)
		{
			if (Utilities.isCustomModActive("challengethespire:Bronze Difficulty"))
			{
				if (AbstractDungeon.cardRandomRng.random(1, 3) == 1) 
				{ 
					AbstractDungeon.actionManager.addToBottom(new TalkAction(true, Strings.configFailedSummonActionText, 1.0F, 2.0F));
					return; 
				}
			}
			else if (Utilities.isCustomModActive("challengethespire:Silver Difficulty"))
			{
				if (AbstractDungeon.cardRandomRng.random(1, 2) == 1) 
				{ 
					AbstractDungeon.actionManager.addToBottom(new TalkAction(true, Strings.configFailedSummonActionText, 1.0F, 2.0F));
					return; 
				}
			}
			else if (Utilities.isCustomModActive("challengethespire:Gold Difficulty"))
			{
				if (AbstractDungeon.cardRandomRng.random(1, 3) != 1) 
				{ 
					AbstractDungeon.actionManager.addToBottom(new TalkAction(true, Strings.configFailedSummonActionText, 1.0F, 2.0F));
					return; 
				}
			}
			else if (Utilities.isCustomModActive("challengethespire:Platinum Difficulty"))
			{
				if (AbstractDungeon.cardRandomRng.random(1, 4) != 1) 
				{ 
					AbstractDungeon.actionManager.addToBottom(new TalkAction(true, Strings.configFailedSummonActionText, 1.0F, 2.0F));
					return; 
				}
			}
		}
		
		int currentDeck = 0;		
		if (StarterDeckSetup.getCurrentDeck().getArchetypeCards().size() > 0) { currentDeck = StarterDeckSetup.getCurrentDeck().getIndex(); }
		if (DuelistMod.debug) { System.out.println("theDuelist:DuelistCard:uoSummon() ---> called uoSummon()"); }
		// Check to make sure they still have summon power, if they do not give it to them with a stack of 0
		if (!p.hasPower(SummonPower.POWER_ID))
		{
			AbstractDungeon.actionManager.addToTop(new ApplyPowerAction(AbstractDungeon.player, AbstractDungeon.player, new SummonPower(AbstractDungeon.player, SUMMONS, c.originalName, "#b" + SUMMONS + " monsters summoned. Maximum of 5 Summons.", c), SUMMONS));
			int startSummons = SUMMONS;
			// Check for Pot of Generosity
			if (p.hasPower(PotGenerosityPower.POWER_ID)) { AbstractDungeon.actionManager.addToTop(new GainEnergyAction(startSummons)); }

			// Check for Summoning Sickness
			if (p.hasPower(SummonSicknessPower.POWER_ID)) { damageSelfNotHP(startSummons * p.getPower(SummonSicknessPower.POWER_ID).amount); }

			// Check for Slifer
			if (p.hasPower(SliferSkyPower.POWER_ID)) 
			{ 
				SliferSkyPower instance = (SliferSkyPower) p.getPower(SliferSkyPower.POWER_ID);
				applyPowerToSelf(new StrengthPower(p, instance.amount));
				applyPowerToSelf(new LoseStrengthPower(p, instance.amount));
			} 
			
			// Check for Goblin's Secret Remedy
			if (p.hasPower(GoblinRemedyPower.POWER_ID)) { gainTempHP(p.getPower(GoblinRemedyPower.POWER_ID).amount);  }
			
			// Check for Blizzard Dragon
			if (p.hasPower(BlizzardDragonPower.POWER_ID) && c.hasTag(Tags.DRAGON)) { AbstractOrb frost = new Frost(); channel(frost); }
						
			// Check for Toon Cannon Soldier
			if (p.hasPower(ToonCannonPower.POWER_ID) && c.hasTag(Tags.TOON))
			{
				ToonCannonPower power = (ToonCannonPower) p.getPower(ToonCannonPower.POWER_ID);
				DuelistCard.damageAllEnemiesThornsPoison(power.amount);
			}
			
			// Check for Tripod Fish
			if (p.hasPower(TripodFishPower.POWER_ID) && c.hasTag(Tags.AQUA))
			{
				for (int i = 0; i < p.getPower(TripodFishPower.POWER_ID).amount; i++)
				{						
					DuelistCard randAqExh = (DuelistCard) returnTrulyRandomFromSet(Tags.AQUA);					
					AbstractDungeon.actionManager.addToTop(new RandomizedExhaustPileAction(randAqExh, true));
				}
			}
			
			int cursedBillGold = cursedBillGoldLoss();
			if (cursedBillGold > 0) { loseGold(cursedBillGold); }
			
			// Check for Power Giants
			for (AbstractCard giantCard : player().hand.group)
			{
				if (giantCard instanceof PowerGiant)
				{
					PowerGiant giant = (PowerGiant)giantCard;
					giant.damageInc();
				}
			}
			
			for (AbstractCard giantCard : player().discardPile.group)
			{
				if (giantCard instanceof PowerGiant)
				{
					PowerGiant giant = (PowerGiant)giantCard;
					giant.damageInc();
				}
			}
			
			for (AbstractCard giantCard : player().drawPile.group)
			{
				if (giantCard instanceof PowerGiant)
				{
					PowerGiant giant = (PowerGiant)giantCard;
					giant.damageInc();
				}
			}
		}

		else
		{
			// Setup Pot of Generosity
			int potSummons = 0;
			int startSummons = p.getPower(SummonPower.POWER_ID).amount;
			SummonPower summonsInstance = (SummonPower)p.getPower(SummonPower.POWER_ID);
			int maxSummons = summonsInstance.MAX_SUMMONS;
			if ((startSummons + SUMMONS) > maxSummons) { potSummons = maxSummons - startSummons; }
			else { potSummons = SUMMONS; }

			// Add SUMMONS
			summonsInstance.amount += potSummons;

			if (potSummons > 0) 
			{ 
				for (int i = 0; i < potSummons; i++) 
				{ 
					summonsInstance.summonList.add(c.originalName);
					summonsInstance.actualCardSummonList.add((DuelistCard) c.makeStatEquivalentCopy());
				} 
			}

			// Check for Pot of Generosity
			if (p.hasPower(PotGenerosityPower.POWER_ID)) { AbstractDungeon.actionManager.addToTop(new GainEnergyAction(potSummons)); }

			// Check for Summoning Sickness
			if (p.hasPower(SummonSicknessPower.POWER_ID)) { damageSelfNotHP(potSummons * p.getPower(SummonSicknessPower.POWER_ID).amount); }

			// Check for Slifer
			if (p.hasPower(SliferSkyPower.POWER_ID) && potSummons > 0) 
			{ 
				SliferSkyPower instance = (SliferSkyPower) p.getPower(SliferSkyPower.POWER_ID);
				applyPowerToSelf(new StrengthPower(p, instance.amount * potSummons));
				applyPowerToSelf(new LoseStrengthPower(p, instance.amount * potSummons));
			} 
			
			
			// Check for Goblin's Secret Remedy
			if (p.hasPower(GoblinRemedyPower.POWER_ID) && potSummons > 0) { gainTempHP(p.getPower(GoblinRemedyPower.POWER_ID).amount);  }

			// Check for Blizzard Dragon
			if (p.hasPower(BlizzardDragonPower.POWER_ID) && c.hasTag(Tags.DRAGON)) 
			{ 
				for (int i = 0; i < potSummons; i++) { AbstractOrb frost = new Frost(); channel(frost); }
			}
			
			// Check for Toon Cannon Soldier
			if (p.hasPower(ToonCannonPower.POWER_ID) && c.hasTag(Tags.TOON) && potSummons > 0)
			{
				ToonCannonPower power = (ToonCannonPower) p.getPower(ToonCannonPower.POWER_ID);
				DuelistCard.damageAllEnemiesThornsPoison(power.amount);
			}
			
			// Check for Tripod Fish
			if (p.hasPower(TripodFishPower.POWER_ID) && c.hasTag(Tags.AQUA) && potSummons > 0)
			{
				for (int i = 0; i < p.getPower(TripodFishPower.POWER_ID).amount; i++)
				{						
					DuelistCard randAqExh = (DuelistCard) returnTrulyRandomFromSet(Tags.AQUA);					
					AbstractDungeon.actionManager.addToTop(new RandomizedExhaustPileAction(randAqExh, true));
				}
			}
			
			if (potSummons > 0)
			{
				int cursedBillGold = cursedBillGoldLoss();
				if (cursedBillGold > 0) { loseGold(cursedBillGold); }
			}
			
			// Check for Power Giants
			if (potSummons > 0)
			{
				for (AbstractCard giantCard : player().hand.group)
				{
					if (giantCard instanceof PowerGiant)
					{
						PowerGiant giant = (PowerGiant)giantCard;
						giant.damageInc();
					}
				}
				
				for (AbstractCard giantCard : player().discardPile.group)
				{
					if (giantCard instanceof PowerGiant)
					{
						PowerGiant giant = (PowerGiant)giantCard;
						giant.damageInc();
					}
				}
				
				for (AbstractCard giantCard : player().drawPile.group)
				{
					if (giantCard instanceof PowerGiant)
					{
						PowerGiant giant = (PowerGiant)giantCard;
						giant.damageInc();
					}
				}
			}
			
			// Update UI
			if (DuelistMod.debug) { System.out.println("theDuelist:DuelistCard:uoSummon() ---> updating summons instance"); }
			summonsInstance.updateCount(summonsInstance.amount);
			summonsInstance.updateStringColors();
			summonsInstance.updateDescription();
			if (DuelistMod.debug) { System.out.println("theDuelist:DuelistCard:uoSummon() ---> summons instance amount: " + summonsInstance.amount); }
		}
	}
	
	
	// This function needs to match powerSummon() exactly, except for the first block of code that returns early due to curse/challenges
	// Although it is ok to add extra code on top of powerSummon() inside this function, it just needs to do everything powerSummon() does
	public static void puzzleSummon(AbstractPlayer p, int SUMMONS, String cardName, boolean fromUO)
	{
		int currentDeck = 0;		
		if (StarterDeckSetup.getCurrentDeck().getArchetypeCards().size() > 0) { currentDeck = StarterDeckSetup.getCurrentDeck().getIndex(); }
		if (DuelistMod.debug) { System.out.println("theDuelist:DuelistCard:powerSummon() ---> called powerSummon()"); }
		if (!DuelistMod.checkTrap)
		{
			if (DuelistMod.debug) { System.out.println("theDuelist:DuelistCard:powerSummon() ---> no check trap, SUMMONS: " + SUMMONS); }
			DuelistCard c = DuelistMod.summonMap.get(cardName);
			if (DuelistMod.debug) { System.out.println("theDuelist:DuelistCard:powerSummon() ---> c: " + c.originalName); }
			// Check to make sure they still have summon power, if they do not give it to them with a stack of 0
			if (!p.hasPower(SummonPower.POWER_ID))
			{
				//DuelistCard newSummonCard = (DuelistCard) DefaultMod.summonMap.get(cardName).makeCopy();
				AbstractDungeon.actionManager.addToTop(new ApplyPowerAction(AbstractDungeon.player, AbstractDungeon.player, new SummonPower(AbstractDungeon.player, SUMMONS, cardName, "#b" + SUMMONS + " monsters summoned. Maximum of 5 Summons."), SUMMONS));
				int startSummons = SUMMONS;
				// Check for Pot of Generosity
				if (p.hasPower(PotGenerosityPower.POWER_ID)) { AbstractDungeon.actionManager.addToTop(new GainEnergyAction(startSummons)); }

				// Check for Summoning Sickness
				if (p.hasPower(SummonSicknessPower.POWER_ID)) { damageSelfNotHP(startSummons * p.getPower(SummonSicknessPower.POWER_ID).amount); }

				// Check for Slifer
				if (p.hasPower(SliferSkyPower.POWER_ID)) 
				{ 
					SliferSkyPower instance = (SliferSkyPower) p.getPower(SliferSkyPower.POWER_ID);
					applyPowerToSelf(new StrengthPower(p, instance.amount));
					applyPowerToSelf(new LoseStrengthPower(p, instance.amount));
				} 
				
				
				// Check for Goblin's Secret Remedy
				if (p.hasPower(GoblinRemedyPower.POWER_ID)) { gainTempHP(p.getPower(GoblinRemedyPower.POWER_ID).amount);  }
				
				// Check for Blizzard Dragon
				if (p.hasPower(BlizzardDragonPower.POWER_ID) && c.hasTag(Tags.DRAGON)) 
				{ 
					for (int i = 0; i < startSummons; i++) { AbstractOrb frost = new Frost(); channel(frost); }
				}
				// Check for Toon Cannon Soldier
				if (p.hasPower(ToonCannonPower.POWER_ID) && c.hasTag(Tags.TOON))
				{
					ToonCannonPower power = (ToonCannonPower) p.getPower(ToonCannonPower.POWER_ID);
					DuelistCard.damageAllEnemiesThornsPoison(power.amount);
				}
				
				// Check for Tripod Fish
				if (p.hasPower(TripodFishPower.POWER_ID) && c.hasTag(Tags.AQUA))
				{
					for (int i = 0; i < p.getPower(TripodFishPower.POWER_ID).amount; i++)
					{						
						DuelistCard randAqExh = (DuelistCard) returnTrulyRandomFromSet(Tags.AQUA);					
						AbstractDungeon.actionManager.addToTop(new RandomizedExhaustPileAction(randAqExh, true));
					}
				}
				
				int cursedBillGold = cursedBillGoldLoss();
				if (cursedBillGold > 0) { loseGold(cursedBillGold); }
				
				// Check for Power Giants
				for (AbstractCard giantCard : player().hand.group)
				{
					if (giantCard instanceof PowerGiant)
					{
						PowerGiant giant = (PowerGiant)giantCard;
						giant.damageInc();
					}
				}
				
				for (AbstractCard giantCard : player().discardPile.group)
				{
					if (giantCard instanceof PowerGiant)
					{
						PowerGiant giant = (PowerGiant)giantCard;
						giant.damageInc();
					}
				}
				
				for (AbstractCard giantCard : player().drawPile.group)
				{
					if (giantCard instanceof PowerGiant)
					{
						PowerGiant giant = (PowerGiant)giantCard;
						giant.damageInc();
					}
				}
				
				// Check for new summoned types
				ArrayList<CardTags> toRet = getAllMonsterTypes(c);
				if (toRet.size() > 0)
				{
					for (CardTags t : toRet)
					{
						if (!DuelistMod.summonedTypesThisTurn.contains(t))
						{
							DuelistMod.summonedTypesThisTurn.add(t);
							if (player().hasPower(KuribohrnPower.POWER_ID))
							{
								if (DuelistMod.kuribohrnFlipper) 
								{ 
									DuelistCard randZomb = (DuelistCard) returnTrulyRandomFromSet(Tags.ZOMBIE);
									fullResummon(randZomb, false, AbstractDungeon.getRandomMonster(), false);
								}
								DuelistMod.kuribohrnFlipper = !DuelistMod.kuribohrnFlipper;
							}
						}
						DuelistMod.lastTagSummoned = toRet.get(AbstractDungeon.cardRandomRng.random(toRet.size() - 1));
					}
				}
				
				if (DuelistMod.debug)
				{
					int counter = 1;
					for (CardTags t : DuelistMod.summonedTypesThisTurn)
					{
						DuelistMod.logger.info("DuelistMod.summonedTypesThisTurn[" + counter + "]: " + t);
						counter++;
					}
				}
				
				
				if (!c.hasTag(Tags.TOKEN))
				{
					DuelistMod.summonCombatCount += startSummons;
					DuelistMod.summonRunCount += startSummons;
					DuelistMod.summonTurnCount += startSummons;
				}		
			}
			else
			{
				// Setup Pot of Generosity
				int potSummons = 0;
				int startSummons = p.getPower(SummonPower.POWER_ID).amount;
				SummonPower summonsInstance = (SummonPower)p.getPower(SummonPower.POWER_ID);
				int maxSummons = summonsInstance.MAX_SUMMONS;
				if ((startSummons + SUMMONS) > maxSummons) { potSummons = maxSummons - startSummons; }
				else { potSummons = SUMMONS; }

				// Add SUMMONS
				summonsInstance.amount += potSummons;

				if (potSummons > 0) { for (int i = 0; i < potSummons; i++) { summonsInstance.summonList.add(cardName); summonsInstance.actualCardSummonList.add((DuelistCard) c.makeStatEquivalentCopy()); } }

				// Check for Pot of Generosity
				if (p.hasPower(PotGenerosityPower.POWER_ID)) { AbstractDungeon.actionManager.addToTop(new GainEnergyAction(potSummons)); }

				// Check for Summoning Sickness
				if (p.hasPower(SummonSicknessPower.POWER_ID)) { damageSelfNotHP(potSummons * p.getPower(SummonSicknessPower.POWER_ID).amount); }

				// Check for Slifer
				if (p.hasPower(SliferSkyPower.POWER_ID) && potSummons > 0) 
				{ 
					SliferSkyPower instance = (SliferSkyPower) p.getPower(SliferSkyPower.POWER_ID);
					applyPowerToSelf(new StrengthPower(p, instance.amount * potSummons));
					applyPowerToSelf(new LoseStrengthPower(p, instance.amount * potSummons));
				} 
				
				
				// Check for Goblin's Secret Remedy
				if (p.hasPower(GoblinRemedyPower.POWER_ID) && potSummons > 0) { gainTempHP(p.getPower(GoblinRemedyPower.POWER_ID).amount);  }

				// Check for Blizzard Dragon
				if (p.hasPower(BlizzardDragonPower.POWER_ID) && c.hasTag(Tags.DRAGON)) 
				{ 
					for (int i = 0; i < potSummons; i++) { AbstractOrb frost = new Frost(); channel(frost); }
				}
				
				// Check for Toon Cannon Soldier
				if (p.hasPower(ToonCannonPower.POWER_ID) && c.hasTag(Tags.TOON) && potSummons > 0)
				{
					ToonCannonPower power = (ToonCannonPower) p.getPower(ToonCannonPower.POWER_ID);
					DuelistCard.damageAllEnemiesThornsPoison(power.amount);
				}
				
				// Check for Tripod Fish
				if (p.hasPower(TripodFishPower.POWER_ID) && c.hasTag(Tags.AQUA) && potSummons > 0)
				{
					for (int i = 0; i < p.getPower(TripodFishPower.POWER_ID).amount; i++)
					{						
						DuelistCard randAqExh = (DuelistCard) returnTrulyRandomFromSet(Tags.AQUA);					
						AbstractDungeon.actionManager.addToTop(new RandomizedExhaustPileAction(randAqExh, true));
					}
				}
				
				if (potSummons > 0)
				{
					int cursedBillGold = cursedBillGoldLoss();
					if (cursedBillGold > 0) { loseGold(cursedBillGold); }
				}
				
				// Check for Power Giants
				if (potSummons > 0)
				{
					for (AbstractCard giantCard : player().hand.group)
					{
						if (giantCard instanceof PowerGiant)
						{
							PowerGiant giant = (PowerGiant)giantCard;
							giant.damageInc();
						}
					}
					
					for (AbstractCard giantCard : player().discardPile.group)
					{
						if (giantCard instanceof PowerGiant)
						{
							PowerGiant giant = (PowerGiant)giantCard;
							giant.damageInc();
						}
					}
					
					for (AbstractCard giantCard : player().drawPile.group)
					{
						if (giantCard instanceof PowerGiant)
						{
							PowerGiant giant = (PowerGiant)giantCard;
							giant.damageInc();
						}
					}
				}
				
				// Check for Ultimate Offering
				if (p.hasPower(UltimateOfferingPower.POWER_ID) && potSummons == 0 && SUMMONS != 0 && !fromUO)
				{
					if (DuelistMod.debug) { System.out.println("theDuelist:DuelistCard:powerSummon() ---> hit Ultimate Offering, SUMMONS: " + SUMMONS); }
					int amountToSummon = p.getPower(UltimateOfferingPower.POWER_ID).amount;
					damageSelf(3);
					incMaxSummons(p, amountToSummon);
					if (DuelistMod.debug) { System.out.println("theDuelist:DuelistCard:powerSummon() ---> inside UO check, amountToSummon: " + amountToSummon); }
					uoSummon(p, amountToSummon, new Token("Blood Token"));
				}
				
				// Check for new summoned types
				if (potSummons > 0)
				{
					ArrayList<CardTags> toRet = getAllMonsterTypes(c);
					if (toRet.size() > 0)
					{
						for (CardTags t : toRet)
						{
							if (!DuelistMod.summonedTypesThisTurn.contains(t))
							{
								DuelistMod.summonedTypesThisTurn.add(t);
								if (player().hasPower(KuribohrnPower.POWER_ID))
								{
									if (DuelistMod.kuribohrnFlipper) 
									{ 
										DuelistCard randZomb = (DuelistCard) returnTrulyRandomFromSet(Tags.ZOMBIE);
										fullResummon(randZomb, false, AbstractDungeon.getRandomMonster(), false);
									}
									DuelistMod.kuribohrnFlipper = !DuelistMod.kuribohrnFlipper;
								}
							}
						}
						DuelistMod.lastTagSummoned = toRet.get(AbstractDungeon.cardRandomRng.random(toRet.size() - 1));
					}
				}
				
				if (DuelistMod.debug)
				{
					int counter = 1;
					for (CardTags t : DuelistMod.summonedTypesThisTurn)
					{
						DuelistMod.logger.info("DuelistMod.summonedTypesThisTurn[" + counter + "]: " + t);
						counter++;
					}
				}

				// Update UI
				summonsInstance.updateCount(summonsInstance.amount);
				summonsInstance.updateStringColors();
				summonsInstance.updateDescription();
				if (!c.hasTag(Tags.TOKEN))
				{
					DuelistMod.summonCombatCount += potSummons;
					DuelistMod.summonRunCount += potSummons;
					DuelistMod.summonTurnCount += potSummons;
				}		

				// Check for Trap Hole
				if (p.hasPower(TrapHolePower.POWER_ID) && !DuelistMod.checkTrap)
				{
					for (int i = 0; i < potSummons; i++)
					{
						TrapHolePower power = (TrapHolePower) p.getPower(TrapHolePower.POWER_ID);
						int randomNum = AbstractDungeon.cardRandomRng.random(1, 10);
						if (randomNum <= power.chance || power.chance > 10)
						{
							DuelistMod.checkTrap = true;
							power.flash();
							if (DuelistMod.debug) { System.out.println("theDuelist:DuelistCard:powerSummon ---> triggered trap hole with roll of: " + randomNum); }
							powerTribute(p, 1, false);
							DuelistCard cardCopy = DuelistCard.newCopyOfMonster(c.originalName);
							if (cardCopy != null && !cardCopy.hasTag(Tags.EXEMPT))
							{
								AbstractMonster m = AbstractDungeon.getRandomMonster();
								fullResummon(cardCopy, c.upgraded, m, false);
								if (DuelistMod.debug) { System.out.println("theDuelist:DuelistCard:powerSummon ---> trap hole resummoned properly"); }
							}
						}
						else
						{
							if (DuelistMod.debug) { System.out.println("theDuelist:DuelistCard:powerSummon ---> did not trigger trap hole with roll of: " + randomNum); }
						}
					}
				}

				// Check for Yami
				if (p.hasPower(YamiPower.POWER_ID) && c.hasTag(Tags.SPELLCASTER))
				{
					spellSummon(p, 1, c);
				}

				// Update UI
				if (DuelistMod.debug) { System.out.println("theDuelist:DuelistCard:powerSummon() ---> updating summons instance amount"); }
				summonsInstance.updateCount(summonsInstance.amount);
				summonsInstance.updateStringColors();
				summonsInstance.updateDescription();
				if (DuelistMod.debug) { System.out.println("theDuelist:DuelistCard:powerSummon() ---> summons instance amount: " + summonsInstance.amount); }
			}
		}

		else
		{
			if (DuelistMod.debug) { System.out.println("theDuelist:DuelistCard:powerSummon() ---> check trap, SUMMONS: " + SUMMONS); }
			DuelistCard c = DuelistMod.summonMap.get(cardName);
			if (DuelistMod.debug) { System.out.println("theDuelist:DuelistCard:powerSummon() ---> check trap, c: " + c.originalName); }
			trapHoleSummon(p, SUMMONS, c);
		}
	}
	// =============== /SUMMON MONSTER FUNCTIONS/ =======================================================================================================================================================
	
	
	
	// =============== TRIBUTE MONSTER FUNCTIONS =========================================================================================================================================================
	public ArrayList<DuelistCard> tribute()
	{
		return tribute(AbstractDungeon.player, this.tributes, false, this);
	}
	
	public ArrayList<DuelistCard> tribute(boolean tributeAll)
	{
		return tribute(AbstractDungeon.player, 0, tributeAll, this);
	}
	
	public static ArrayList<DuelistCard> tribute(AbstractPlayer p, int tributes, boolean tributeAll, DuelistCard card)
	{		
		ArrayList<DuelistCard> tributeList = new ArrayList<DuelistCard>();
		ArrayList<DuelistCard> cardTribList = new ArrayList<DuelistCard>();
		boolean challengeFailure = (Utilities.isCustomModActive("theDuelist:TributeRandomizer"));
		if (challengeFailure)
		{
			if (Utilities.isCustomModActive("challengethespire:Bronze Difficulty"))
			{
				if (AbstractDungeon.cardRandomRng.random(1, 3) == 1) 
				{ 
					AbstractDungeon.actionManager.addToBottom(new TalkAction(true, Strings.configFailedTribActionText, 1.0F, 2.0F));
					return tributeList;
				}
			}
			else if (Utilities.isCustomModActive("challengethespire:Silver Difficulty"))
			{
				if (AbstractDungeon.cardRandomRng.random(1, 2) == 1) 
				{ 
					AbstractDungeon.actionManager.addToBottom(new TalkAction(true, Strings.configFailedTribActionText, 1.0F, 2.0F));
					return tributeList;
				}
			}
			else if (Utilities.isCustomModActive("challengethespire:Gold Difficulty"))
			{
				if (AbstractDungeon.cardRandomRng.random(1, 3) != 1) 
				{ 
					AbstractDungeon.actionManager.addToBottom(new TalkAction(true, Strings.configFailedTribActionText, 1.0F, 2.0F));
					return tributeList;
				}
			}
			else if (Utilities.isCustomModActive("challengethespire:Platinum Difficulty"))
			{
				if (AbstractDungeon.cardRandomRng.random(1, 4) != 1) 
				{ 
					AbstractDungeon.actionManager.addToBottom(new TalkAction(true, Strings.configFailedTribActionText, 1.0F, 2.0F));
					return tributeList;
				}
			}
		}
		
		if (card.misc != 52)
		{
			// If no summons, just skip this so we don't crash
			// This should never be called without summons due to canUse() checking for tributes before use() can be run
			if (!p.hasPower(SummonPower.POWER_ID)) { return tributeList; }
			else
			{
				//	Check for Mausoleum of the Emperor
				if (p.hasPower(EmperorPower.POWER_ID))
				{
					EmperorPower empInstance = (EmperorPower)p.getPower(EmperorPower.POWER_ID);
					if (empInstance.flag)
					{
						SummonPower summonsInstance = (SummonPower)p.getPower(SummonPower.POWER_ID);

						// Check for Tomb Looter
						// Checking here because Tomb Looter should proc even when you attack with a tribute card that reduces your summons after attacking
						if (p.hasPower(TombLooterPower.POWER_ID) && card.type.equals(CardType.ATTACK))
						{
							if (getSummons(p) == getMaxSummons(p))
							{
								gainGold(p.getPower(TombLooterPower.POWER_ID).amount, p, true);
							}
						}

						if (tributeAll) { tributes = summonsInstance.amount; }
						if (summonsInstance.amount - tributes < 0) { tributes = summonsInstance.amount; summonsInstance.amount = 0; }
						else { summonsInstance.amount -= tributes; }

						// Check for Obelisk after tributing
						if (p.hasPower(ObeliskPower.POWER_ID) && tributes > 0)
						{
							ObeliskPower instance = (ObeliskPower) p.getPower(ObeliskPower.POWER_ID);
							int damageObelisk = instance.DAMAGE;
							DuelistCard.damageAllEnemiesThornsNormal(instance.DAMAGE * tributes);
						}
						
						// Check for Pharaoh's Curse
						if (p.hasPower(TributeSicknessPower.POWER_ID)) { damageSelfNotHP(tributes * p.getPower(TributeSicknessPower.POWER_ID).amount); }

						// Check for Toon Tribute power
						if (p.hasPower(TributeToonPower.POWER_ID) && tributes > 0) { AbstractDungeon.actionManager.addToTop(new RandomizedHandAction(returnTrulyRandomFromSets(Tags.MONSTER, Tags.TOON), true, true, true, true, false, false, false, false, 1, 3, 0, 0, 0, 0)); reducePower(p.getPower(TributeToonPower.POWER_ID), p, 1); }
						if (p.hasPower(TributeToonPowerB.POWER_ID) && tributes > 0) { AbstractDungeon.actionManager.addToTop(new RandomizedHandAction(returnTrulyRandomFromSet(Tags.TOON), true, true, true, true, false, false, false, false, 1, 3, 0, 0, 0, 0)); reducePower(p.getPower(TributeToonPowerB.POWER_ID), p, 1); }

						// Check for Ironhammer Giants in hand/discard/draw
						if (tributes > 0)
						{
							for (AbstractCard c : player().hand.group)
							{
								if (c instanceof IronhammerGiant)
								{
									IronhammerGiant giant = (IronhammerGiant)c;
									giant.costReduce();
								}
							}
							
							for (AbstractCard c : AbstractDungeon.player.discardPile.group)
							{
								if (c instanceof IronhammerGiant)
								{
									IronhammerGiant giant = (IronhammerGiant)c;
									giant.costReduce();
								}
							}
							
							for (AbstractCard c : AbstractDungeon.player.drawPile.group)
							{
								if (c instanceof IronhammerGiant)
								{
									IronhammerGiant giant = (IronhammerGiant)c;
									giant.costReduce();
								}
							}
						
							// Look through summonsList and remove # tributes strings
							for (int i = 0; i < tributes; i++)
							{
								if (summonsInstance.summonList.size() > 0)
								{
									int endIndex = summonsInstance.summonList.size() - 1;
									DuelistCard temp = DuelistMod.summonMap.get(summonsInstance.summonList.get(endIndex));
									if (temp != null) { tributeList.add(temp); }
									//summonsInstance.summonMap.remove(summonsInstance.summonList.get(endIndex));
									summonsInstance.summonList.remove(endIndex);
								}
								
								if (summonsInstance.actualCardSummonList.size() > 0)
								{
									int endIndex = summonsInstance.actualCardSummonList.size() - 1;
									DuelistCard temp = summonsInstance.actualCardSummonList.get(endIndex);
									if (temp != null) { cardTribList.add(temp); }
									//summonsInstance.summonMap.remove(summonsInstance.summonList.get(endIndex));
									summonsInstance.actualCardSummonList.remove(endIndex);
								}
							}							
						}

						summonsInstance.updateCount(summonsInstance.amount);
						summonsInstance.updateStringColors();
						summonsInstance.updateDescription();
						for (DuelistCard c : cardTribList) 
						{
							c.customOnTribute(card);
							c.runTributeSynergyFunctions(card);
							if (c.hasTag(Tags.AQUA))
							{
								// Check for Levia Dragon
								if (p.hasPower(LeviaDragonPower.POWER_ID))
								{
									LeviaDragonPower instance = (LeviaDragonPower) p.getPower(LeviaDragonPower.POWER_ID);
									int damageObelisk = instance.amount;
									int[] temp = new int[] {damageObelisk, damageObelisk, damageObelisk, damageObelisk, damageObelisk, damageObelisk, damageObelisk, damageObelisk, damageObelisk, damageObelisk, damageObelisk, damageObelisk};
									for (int i : temp) { i = i * tributes; }
									AbstractDungeon.actionManager.addToTop(new DamageAllEnemiesAction(p, temp, DamageType.THORNS, AbstractGameAction.AttackEffect.BLUNT_LIGHT));
								}
							}
							if (!c.hasTag(Tags.TOKEN) && c.hasTag(Tags.MONSTER))
							{
								DuelistMod.tribCombatCount++;
								DuelistMod.tribRunCount++;
								DuelistMod.tribTurnCount++;
							}
							if (DuelistMod.debug) { System.out.println("theDuelist:DuelistCard:tribute():1 ---> Called " + c.originalName + "'s customOnTribute()"); }
						}
						if (AbstractDungeon.player.hasPower(ReinforcementsPower.POWER_ID)) { DuelistCard.summon(AbstractDungeon.player, 1, card); }
						return cardTribList;
					}
					else
					{
						empInstance.flag = true;
						if (AbstractDungeon.player.hasPower(ReinforcementsPower.POWER_ID)) { DuelistCard.summon(AbstractDungeon.player, 1, card); }
						return tributeList;
					}
				}
				else
				{

					SummonPower summonsInstance = (SummonPower)p.getPower(SummonPower.POWER_ID);

					// Check for Tomb Looter
					// Checking here because Tomb Looter should proc even when you attack with a tribute card that reduces your summons after attacking
					if (p.hasPower(TombLooterPower.POWER_ID) && card.type.equals(CardType.ATTACK))
					{
						if (getSummons(p) == getMaxSummons(p))
						{
							gainGold(p.getPower(TombLooterPower.POWER_ID).amount, p, true);
						}
					}

					if (tributeAll) { tributes = summonsInstance.amount; }
					if (summonsInstance.amount - tributes < 0) { tributes = summonsInstance.amount; summonsInstance.amount = 0; }
					else { summonsInstance.amount -= tributes; }

					// Check for Obelisk after tributing
					if (p.hasPower(ObeliskPower.POWER_ID) && tributes > 0)
					{
						ObeliskPower instance = (ObeliskPower) p.getPower(ObeliskPower.POWER_ID);
						int damageObelisk = instance.DAMAGE;
						DuelistCard.damageAllEnemiesThornsNormal(instance.DAMAGE * tributes);
					}
					
					// Check for Pharaoh's Curse
					if (p.hasPower(TributeSicknessPower.POWER_ID)) { damageSelfNotHP(tributes * p.getPower(TributeSicknessPower.POWER_ID).amount); }

					// Check for Toon Tribute power
					if (p.hasPower(TributeToonPower.POWER_ID) && tributes > 0) { AbstractDungeon.actionManager.addToTop(new RandomizedHandAction(returnTrulyRandomFromSets(Tags.MONSTER, Tags.TOON), true, true, true, true, false, false, false, false, 1, 3, 0, 0, 0, 0)); reducePower(p.getPower(TributeToonPower.POWER_ID), p, 1); }
					if (p.hasPower(TributeToonPowerB.POWER_ID) && tributes > 0) { AbstractDungeon.actionManager.addToTop(new RandomizedHandAction(returnTrulyRandomFromSet(Tags.TOON), true, true, true, true, false, false, false, false, 1, 3, 0, 0, 0, 0)); reducePower(p.getPower(TributeToonPowerB.POWER_ID), p, 1); }

					// Check for Ironhammer Giants in hand/discard/draw
					if (tributes > 0)
					{
						for (AbstractCard c : player().hand.group)
						{
							if (c instanceof IronhammerGiant)
							{
								IronhammerGiant giant = (IronhammerGiant)c;
								giant.costReduce();
							}
						}
						
						for (AbstractCard c : AbstractDungeon.player.discardPile.group)
						{
							if (c instanceof IronhammerGiant)
							{
								IronhammerGiant giant = (IronhammerGiant)c;
								giant.costReduce();
							}
						}
						
						for (AbstractCard c : AbstractDungeon.player.drawPile.group)
						{
							if (c instanceof IronhammerGiant)
							{
								IronhammerGiant giant = (IronhammerGiant)c;
								giant.costReduce();
							}
						}
					
						// Look through summonsList and remove #tributes strings
						for (int i = 0; i < tributes; i++)
						{
							if (summonsInstance.summonList.size() > 0)
							{								
								int endIndex = summonsInstance.summonList.size() - 1;
								DuelistCard temp = DuelistMod.summonMap.get(summonsInstance.summonList.get(endIndex));
								if (temp != null) { tributeList.add(temp); }
								//summonsInstance.summonMap.remove(summonsInstance.summonList.get(endIndex));
								summonsInstance.summonList.remove(endIndex);								
							}
							
							if (summonsInstance.actualCardSummonList.size() > 0)
							{
								int endIndex = summonsInstance.actualCardSummonList.size() - 1;
								DuelistCard temp = summonsInstance.actualCardSummonList.get(endIndex);
								if (temp != null) { cardTribList.add(temp); }
								//summonsInstance.summonMap.remove(summonsInstance.summonList.get(endIndex));
								summonsInstance.actualCardSummonList.remove(endIndex);
							}
						}
					}


					summonsInstance.updateCount(summonsInstance.amount);
					summonsInstance.updateStringColors();
					summonsInstance.updateDescription();
					for (DuelistCard c : cardTribList) 
					{
						//c.onTribute(card); 
						c.customOnTribute(card);
						c.runTributeSynergyFunctions(card);
						if (c.hasTag(Tags.AQUA))
						{
							// Check for Levia Dragon
							if (p.hasPower(LeviaDragonPower.POWER_ID))
							{
								LeviaDragonPower instance = (LeviaDragonPower) p.getPower(LeviaDragonPower.POWER_ID);
								int damageObelisk = instance.amount;
								int[] temp = new int[] {damageObelisk, damageObelisk, damageObelisk, damageObelisk, damageObelisk, damageObelisk, damageObelisk, damageObelisk, damageObelisk, damageObelisk, damageObelisk, damageObelisk};
								for (int i : temp) { i = i * tributes; }
								AbstractDungeon.actionManager.addToTop(new DamageAllEnemiesAction(p, temp, DamageType.THORNS, AbstractGameAction.AttackEffect.BLUNT_LIGHT));
							}
						}
						if (!c.hasTag(Tags.TOKEN) && c.hasTag(Tags.MONSTER))
						{
							DuelistMod.tribCombatCount++;
							DuelistMod.tribRunCount++;
							DuelistMod.tribTurnCount++;
						}
						if (DuelistMod.debug) { System.out.println("theDuelist:DuelistCard:tribute():2 ---> Called " + c.originalName + "'s customOnTribute()"); }
					}
					if (AbstractDungeon.player.hasPower(ReinforcementsPower.POWER_ID)) { DuelistCard.summon(AbstractDungeon.player, 1, card); }
					return cardTribList;
				}
			}
		}
		else
		{
			//card.misc = 0;
			if (AbstractDungeon.player.hasPower(ReinforcementsPower.POWER_ID)) { DuelistCard.summon(AbstractDungeon.player, 1, card); }
			return tributeList;
		}

	}

	public static int powerTribute(AbstractPlayer p, int tributes, boolean tributeAll)
	{
		ArrayList<DuelistCard> cardTribList = new ArrayList<DuelistCard>();
		boolean challengeFailure = (Utilities.isCustomModActive("theDuelist:TributeRandomizer"));
		if (challengeFailure)
		{
			if (Utilities.isCustomModActive("challengethespire:Bronze Difficulty"))
			{
				if (AbstractDungeon.cardRandomRng.random(1, 3) == 1) 
				{ 
					AbstractDungeon.actionManager.addToBottom(new TalkAction(true, Strings.configFailedTribActionText, 1.0F, 2.0F));
					return 0;
				}
			}
			else if (Utilities.isCustomModActive("challengethespire:Silver Difficulty"))
			{
				if (AbstractDungeon.cardRandomRng.random(1, 2) == 1) 
				{ 
					AbstractDungeon.actionManager.addToBottom(new TalkAction(true, Strings.configFailedTribActionText, 1.0F, 2.0F));
					return 0;
				}
			}
			else if (Utilities.isCustomModActive("challengethespire:Gold Difficulty"))
			{
				if (AbstractDungeon.cardRandomRng.random(1, 3) != 1) 
				{ 
					AbstractDungeon.actionManager.addToBottom(new TalkAction(true, Strings.configFailedTribActionText, 1.0F, 2.0F));
					return 0;
				}
			}
			else if (Utilities.isCustomModActive("challengethespire:Platinum Difficulty"))
			{
				if (AbstractDungeon.cardRandomRng.random(1, 4) != 1) 
				{ 
					AbstractDungeon.actionManager.addToBottom(new TalkAction(true, Strings.configFailedTribActionText, 1.0F, 2.0F));
					return 0;
				}
			}
		}
		
		ArrayList<DuelistCard> tributeList = new ArrayList<DuelistCard>();
		// If no summons, just skip this so we don't crash
		// This should never be called without summons due to canUse() checking for tributes before use() can be run
		if (!p.hasPower(SummonPower.POWER_ID)) { return 0; }
		else
		{
			//	Check for Mausoleum of the Emperor
			if (p.hasPower(EmperorPower.POWER_ID))
			{
				EmperorPower empInstance = (EmperorPower)p.getPower(EmperorPower.POWER_ID);
				if (empInstance.flag)
				{
					SummonPower summonsInstance = (SummonPower)p.getPower(SummonPower.POWER_ID);
					if (tributeAll) { tributes = summonsInstance.amount; }
					if (summonsInstance.amount - tributes < 0) { tributes = summonsInstance.amount; summonsInstance.amount = 0; }
					else { summonsInstance.amount -= tributes; }

					// Check for Obelisk after tributing
					if (p.hasPower(ObeliskPower.POWER_ID) && tributes > 0)
					{
						ObeliskPower instance = (ObeliskPower) p.getPower(ObeliskPower.POWER_ID);
						int damageObelisk = instance.DAMAGE;
						DuelistCard.damageAllEnemiesThornsNormal(instance.DAMAGE * tributes);
					}			

					// Check for Pharaoh's Curse
					if (p.hasPower(TributeSicknessPower.POWER_ID)) { damageSelfNotHP(tributes * p.getPower(TributeSicknessPower.POWER_ID).amount); }

					// Check for Toon Tribute power
					if (p.hasPower(TributeToonPower.POWER_ID) && tributes > 0) { AbstractDungeon.actionManager.addToTop(new RandomizedHandAction(returnTrulyRandomFromSets(Tags.MONSTER, Tags.TOON), true, true, true, true, false, false, false, false, 1, 3, 0, 0, 0, 0)); reducePower(p.getPower(TributeToonPower.POWER_ID), p, 1); }
					if (p.hasPower(TributeToonPowerB.POWER_ID) && tributes > 0) { AbstractDungeon.actionManager.addToTop(new RandomizedHandAction(returnTrulyRandomFromSet(Tags.TOON), true, true, true, true, false, false, false, false, 1, 3, 0, 0, 0, 0)); reducePower(p.getPower(TributeToonPowerB.POWER_ID), p, 1); }

					// Check for Ironhammer Giants in hand/discard/draw
					if (tributes > 0)
					{
						for (AbstractCard c : player().hand.group)
						{
							if (c instanceof IronhammerGiant)
							{
								IronhammerGiant giant = (IronhammerGiant)c;
								giant.costReduce();
							}
						}
						
						for (AbstractCard c : AbstractDungeon.player.discardPile.group)
						{
							if (c instanceof IronhammerGiant)
							{
								IronhammerGiant giant = (IronhammerGiant)c;
								giant.costReduce();
							}
						}
						
						for (AbstractCard c : AbstractDungeon.player.drawPile.group)
						{
							if (c instanceof IronhammerGiant)
							{
								IronhammerGiant giant = (IronhammerGiant)c;
								giant.costReduce();
							}
						}
					}
					
					// Look through summonsList and remove #tributes strings					
					if (tributes > 0) 
					{
						for (int i = 0; i < tributes; i++)
						{
							if (summonsInstance.summonList.size() > 0)
							{
								int endIndex = summonsInstance.summonList.size() - 1;
								DuelistCard temp = DuelistMod.summonMap.get(summonsInstance.summonList.get(endIndex));
								if (temp != null) { tributeList.add(temp); }
								//summonsInstance.summonMap.remove(summonsInstance.summonList.get(endIndex));
								summonsInstance.summonList.remove(endIndex);
							}
							
							if (summonsInstance.actualCardSummonList.size() > 0)
							{
								int endIndex = summonsInstance.actualCardSummonList.size() - 1;
								DuelistCard temp = summonsInstance.actualCardSummonList.get(endIndex);
								if (temp != null) { cardTribList.add(temp); }
								//summonsInstance.summonMap.remove(summonsInstance.summonList.get(endIndex));
								summonsInstance.actualCardSummonList.remove(endIndex);
							}
						}
					}


					summonsInstance.updateCount(summonsInstance.amount);
					summonsInstance.updateStringColors();
					summonsInstance.updateDescription();
					for (DuelistCard c : cardTribList)
					{ 
						//c.onTribute(new Token());
						c.customOnTribute(new Token());
						c.runTributeSynergyFunctions(new Token());
						if (c.hasTag(Tags.AQUA))
						{
							// Check for Levia Dragon
							if (p.hasPower(LeviaDragonPower.POWER_ID))
							{
								LeviaDragonPower instance = (LeviaDragonPower) p.getPower(LeviaDragonPower.POWER_ID);
								int damageObelisk = instance.amount;
								int[] temp = new int[] {damageObelisk, damageObelisk, damageObelisk, damageObelisk, damageObelisk, damageObelisk, damageObelisk, damageObelisk, damageObelisk, damageObelisk, damageObelisk, damageObelisk};
								for (int i : temp) { i = i * tributes; }
								AbstractDungeon.actionManager.addToTop(new DamageAllEnemiesAction(p, temp, DamageType.THORNS, AbstractGameAction.AttackEffect.BLUNT_LIGHT));
							}
						}
						if (!c.hasTag(Tags.TOKEN) && c.hasTag(Tags.MONSTER))
						{
							DuelistMod.tribCombatCount++;
							DuelistMod.tribRunCount++;
							DuelistMod.tribTurnCount++;
						}
						if (DuelistMod.debug) { System.out.println("theDuelist:DuelistCard:powerTribute():1 ---> Called " + c.originalName + "'s customOnTribute()"); }
					}
					return tributes;
				}
				else
				{
					empInstance.flag = true;
					return 0;
				}
			}
			else
			{

				SummonPower summonsInstance = (SummonPower)p.getPower(SummonPower.POWER_ID);
				if (tributeAll) { tributes = summonsInstance.amount; }
				if (summonsInstance.amount - tributes < 0) { tributes = summonsInstance.amount; summonsInstance.amount = 0; }
				else { summonsInstance.amount -= tributes; }

				// Check for Obelisk after tributing
				if (p.hasPower(ObeliskPower.POWER_ID) && tributes > 0)
				{
					ObeliskPower instance = (ObeliskPower) p.getPower(ObeliskPower.POWER_ID);
					int damageObelisk = instance.DAMAGE;
					DuelistCard.damageAllEnemiesThornsNormal(instance.DAMAGE * tributes);
				}
				
				

				// Check for Pharaoh's Curse
				if (p.hasPower(TributeSicknessPower.POWER_ID)) { damageSelfNotHP(tributes * p.getPower(TributeSicknessPower.POWER_ID).amount); }

				// Check for Toon Tribute power
				if (p.hasPower(TributeToonPower.POWER_ID) && tributes > 0) { AbstractDungeon.actionManager.addToTop(new RandomizedHandAction(returnTrulyRandomFromSets(Tags.MONSTER, Tags.TOON), true, true, true, true, false, false, false, false, 1, 3, 0, 0, 0, 0)); reducePower(p.getPower(TributeToonPower.POWER_ID), p, 1); }
				if (p.hasPower(TributeToonPowerB.POWER_ID) && tributes > 0) { AbstractDungeon.actionManager.addToTop(new RandomizedHandAction(returnTrulyRandomFromSet(Tags.TOON), true, true, true, true, false, false, false, false, 1, 3, 0, 0, 0, 0)); reducePower(p.getPower(TributeToonPowerB.POWER_ID), p, 1); }

				// Check for Ironhammer Giants in hand/discard/draw
				if (tributes > 0)
				{
					for (AbstractCard c : player().hand.group)
					{
						if (c instanceof IronhammerGiant)
						{
							IronhammerGiant giant = (IronhammerGiant)c;
							giant.costReduce();
						}
					}
					
					for (AbstractCard c : AbstractDungeon.player.discardPile.group)
					{
						if (c instanceof IronhammerGiant)
						{
							IronhammerGiant giant = (IronhammerGiant)c;
							giant.costReduce();
						}
					}
					
					for (AbstractCard c : AbstractDungeon.player.drawPile.group)
					{
						if (c instanceof IronhammerGiant)
						{
							IronhammerGiant giant = (IronhammerGiant)c;
							giant.costReduce();
						}
					}
				}
				
				// Look through summonsList and remove #tributes strings
				if (tributes > 0) 
				{
					for (int i = 0; i < tributes; i++)
					{
						if (summonsInstance.summonList.size() > 0)
						{
							int endIndex = summonsInstance.summonList.size() - 1;
							DuelistCard temp = DuelistMod.summonMap.get(summonsInstance.summonList.get(endIndex));
							if (temp != null) { tributeList.add(temp); }
							//summonsInstance.summonMap.remove(summonsInstance.summonList.get(endIndex));
							summonsInstance.summonList.remove(endIndex);
						}
						
						if (summonsInstance.actualCardSummonList.size() > 0)
						{
							int endIndex = summonsInstance.actualCardSummonList.size() - 1;
							DuelistCard temp = summonsInstance.actualCardSummonList.get(endIndex);
							if (temp != null) { cardTribList.add(temp); }
							//summonsInstance.summonMap.remove(summonsInstance.summonList.get(endIndex));
							summonsInstance.actualCardSummonList.remove(endIndex);
						}
					}
				}


				summonsInstance.updateCount(summonsInstance.amount);
				summonsInstance.updateStringColors();
				summonsInstance.updateDescription();
				for (DuelistCard c : cardTribList) 
				{
					//c.onTribute(new Token()); 	
					c.customOnTribute(new Token());
					c.runTributeSynergyFunctions(new Token());
					if (c.hasTag(Tags.AQUA))
					{
						// Check for Levia Dragon
						if (p.hasPower(LeviaDragonPower.POWER_ID))
						{
							LeviaDragonPower instance = (LeviaDragonPower) p.getPower(LeviaDragonPower.POWER_ID);
							int damageObelisk = instance.amount;
							int[] temp = new int[] {damageObelisk, damageObelisk, damageObelisk, damageObelisk, damageObelisk, damageObelisk, damageObelisk, damageObelisk, damageObelisk, damageObelisk, damageObelisk, damageObelisk};
							for (int i : temp) { i = i * tributes; }
							AbstractDungeon.actionManager.addToTop(new DamageAllEnemiesAction(p, temp, DamageType.THORNS, AbstractGameAction.AttackEffect.BLUNT_LIGHT));
						}
					}
					if (!c.hasTag(Tags.TOKEN) && c.hasTag(Tags.MONSTER))
					{
						DuelistMod.tribCombatCount++;
						DuelistMod.tribRunCount++;
						DuelistMod.tribTurnCount++;
					}
					if (DuelistMod.debug) { System.out.println("theDuelist:DuelistCard:powerTribute():2 ---> Called " + c.originalName + "'s customOnTribute()"); }
				}
				return tributes;
			}
		}
	}
	
	public static ArrayList<DuelistCard> listReturnPowerTribute(AbstractPlayer p, int tributes, boolean tributeAll)
	{
		ArrayList<DuelistCard> cardTribList = new ArrayList<DuelistCard>();
		ArrayList<DuelistCard> tributeList = new ArrayList<DuelistCard>();
		boolean challengeFailure = (Utilities.isCustomModActive("theDuelist:TributeRandomizer"));
		if (challengeFailure)
		{
			if (Utilities.isCustomModActive("challengethespire:Bronze Difficulty"))
			{
				if (AbstractDungeon.cardRandomRng.random(1, 3) == 1) 
				{ 
					AbstractDungeon.actionManager.addToBottom(new TalkAction(true, Strings.configFailedTribActionText, 1.0F, 2.0F));
					return tributeList;
				}
			}
			else if (Utilities.isCustomModActive("challengethespire:Silver Difficulty"))
			{
				if (AbstractDungeon.cardRandomRng.random(1, 2) == 1) 
				{ 
					AbstractDungeon.actionManager.addToBottom(new TalkAction(true, Strings.configFailedTribActionText, 1.0F, 2.0F));
					return tributeList;
				}
			}
			else if (Utilities.isCustomModActive("challengethespire:Gold Difficulty"))
			{
				if (AbstractDungeon.cardRandomRng.random(1, 3) != 1) 
				{ 
					AbstractDungeon.actionManager.addToBottom(new TalkAction(true, Strings.configFailedTribActionText, 1.0F, 2.0F));
					return tributeList;
				}
			}
			else if (Utilities.isCustomModActive("challengethespire:Platinum Difficulty"))
			{
				if (AbstractDungeon.cardRandomRng.random(1, 4) != 1) 
				{ 
					AbstractDungeon.actionManager.addToBottom(new TalkAction(true, Strings.configFailedTribActionText, 1.0F, 2.0F));
					return tributeList;
				}
			}
		}
		
		
		// If no summons, just skip this so we don't crash
		// This should never be called without summons due to canUse() checking for tributes before use() can be run
		if (!p.hasPower(SummonPower.POWER_ID)) { return tributeList; }
		else
		{
			//	Check for Mausoleum of the Emperor
			if (p.hasPower(EmperorPower.POWER_ID))
			{
				EmperorPower empInstance = (EmperorPower)p.getPower(EmperorPower.POWER_ID);
				if (empInstance.flag)
				{
					SummonPower summonsInstance = (SummonPower)p.getPower(SummonPower.POWER_ID);
					if (tributeAll) { tributes = summonsInstance.amount; }
					if (summonsInstance.amount - tributes < 0) { tributes = summonsInstance.amount; summonsInstance.amount = 0; }
					else { summonsInstance.amount -= tributes; }

					// Check for Obelisk after tributing
					if (p.hasPower(ObeliskPower.POWER_ID) && tributes > 0)
					{
						ObeliskPower instance = (ObeliskPower) p.getPower(ObeliskPower.POWER_ID);
						int damageObelisk = instance.DAMAGE;
						DuelistCard.damageAllEnemiesThornsNormal(instance.DAMAGE * tributes);
					}
					
					

					// Check for Pharaoh's Curse
					if (p.hasPower(TributeSicknessPower.POWER_ID)) { damageSelfNotHP(tributes * p.getPower(TributeSicknessPower.POWER_ID).amount); }

					// Check for Toon Tribute power
					if (p.hasPower(TributeToonPower.POWER_ID) && tributes > 0) { AbstractDungeon.actionManager.addToTop(new RandomizedHandAction(returnTrulyRandomFromSets(Tags.MONSTER, Tags.TOON), true, true, true, true, false, false, false, false, 1, 3, 0, 0, 0, 0)); reducePower(p.getPower(TributeToonPower.POWER_ID), p, 1); }
					if (p.hasPower(TributeToonPowerB.POWER_ID) && tributes > 0) { AbstractDungeon.actionManager.addToTop(new RandomizedHandAction(returnTrulyRandomFromSet(Tags.TOON), true, true, true, true, false, false, false, false, 1, 3, 0, 0, 0, 0)); reducePower(p.getPower(TributeToonPowerB.POWER_ID), p, 1); }

					// Check for Ironhammer Giants in hand/discard/draw
					if (tributes > 0)
					{
						for (AbstractCard c : player().hand.group)
						{
							if (c instanceof IronhammerGiant)
							{
								IronhammerGiant giant = (IronhammerGiant)c;
								giant.costReduce();
							}
						}
						
						for (AbstractCard c : AbstractDungeon.player.discardPile.group)
						{
							if (c instanceof IronhammerGiant)
							{
								IronhammerGiant giant = (IronhammerGiant)c;
								giant.costReduce();
							}
						}
						
						for (AbstractCard c : AbstractDungeon.player.drawPile.group)
						{
							if (c instanceof IronhammerGiant)
							{
								IronhammerGiant giant = (IronhammerGiant)c;
								giant.costReduce();
							}
						}
					}
					
					// Look through summonsList and remove #tributes strings					
					if (tributes > 0) 
					{
						for (int i = 0; i < tributes; i++)
						{
							if (summonsInstance.summonList.size() > 0)
							{
								int endIndex = summonsInstance.summonList.size() - 1;
								DuelistCard temp = DuelistMod.summonMap.get(summonsInstance.summonList.get(endIndex));
								if (temp != null) { tributeList.add(temp); }
								//summonsInstance.summonMap.remove(summonsInstance.summonList.get(endIndex));
								summonsInstance.summonList.remove(endIndex);
							}
							
							if (summonsInstance.actualCardSummonList.size() > 0)
							{
								int endIndex = summonsInstance.actualCardSummonList.size() - 1;
								DuelistCard temp = summonsInstance.actualCardSummonList.get(endIndex);
								if (temp != null) { cardTribList.add(temp); }
								//summonsInstance.summonMap.remove(summonsInstance.summonList.get(endIndex));
								summonsInstance.actualCardSummonList.remove(endIndex);
							}
						}
					}


					summonsInstance.updateCount(summonsInstance.amount);
					summonsInstance.updateStringColors();
					summonsInstance.updateDescription();
					for (DuelistCard c : cardTribList)
					{ 
						//c.onTribute(new Token());
						c.customOnTribute(new Token());
						c.runTributeSynergyFunctions(new Token());
						if (c.hasTag(Tags.AQUA))
						{
							// Check for Levia Dragon
							if (p.hasPower(LeviaDragonPower.POWER_ID))
							{
								LeviaDragonPower instance = (LeviaDragonPower) p.getPower(LeviaDragonPower.POWER_ID);
								int damageObelisk = instance.amount;
								int[] temp = new int[] {damageObelisk, damageObelisk, damageObelisk, damageObelisk, damageObelisk, damageObelisk, damageObelisk, damageObelisk, damageObelisk, damageObelisk, damageObelisk, damageObelisk};
								for (int i : temp) { i = i * tributes; }
								AbstractDungeon.actionManager.addToTop(new DamageAllEnemiesAction(p, temp, DamageType.THORNS, AbstractGameAction.AttackEffect.BLUNT_LIGHT));
							}
						}
						
						if (!c.hasTag(Tags.TOKEN) && c.hasTag(Tags.MONSTER))
						{
							DuelistMod.tribCombatCount++;
							DuelistMod.tribRunCount++;
							DuelistMod.tribTurnCount++;
						}
						if (DuelistMod.debug) { System.out.println("theDuelist:DuelistCard:powerTribute():1 ---> Called " + c.originalName + "'s customOnTribute()"); }
					}
					return cardTribList;
				}
				else
				{
					empInstance.flag = true;
					return tributeList;
				}
			}
			else
			{

				SummonPower summonsInstance = (SummonPower)p.getPower(SummonPower.POWER_ID);
				if (tributeAll) { tributes = summonsInstance.amount; }
				if (summonsInstance.amount - tributes < 0) { tributes = summonsInstance.amount; summonsInstance.amount = 0; }
				else { summonsInstance.amount -= tributes; }

				// Check for Obelisk after tributing
				if (p.hasPower(ObeliskPower.POWER_ID) && tributes > 0)
				{
					ObeliskPower instance = (ObeliskPower) p.getPower(ObeliskPower.POWER_ID);
					int damageObelisk = instance.DAMAGE;
					DuelistCard.damageAllEnemiesThornsNormal(instance.DAMAGE * tributes);
				}
				
				

				// Check for Pharaoh's Curse
				if (p.hasPower(TributeSicknessPower.POWER_ID)) { damageSelfNotHP(tributes * p.getPower(TributeSicknessPower.POWER_ID).amount); }

				// Check for Toon Tribute power
				if (p.hasPower(TributeToonPower.POWER_ID) && tributes > 0) { AbstractDungeon.actionManager.addToTop(new RandomizedHandAction(returnTrulyRandomFromSets(Tags.MONSTER, Tags.TOON), true, true, true, true, false, false, false, false, 1, 3, 0, 0, 0, 0)); reducePower(p.getPower(TributeToonPower.POWER_ID), p, 1); }
				if (p.hasPower(TributeToonPowerB.POWER_ID) && tributes > 0) { AbstractDungeon.actionManager.addToTop(new RandomizedHandAction(returnTrulyRandomFromSet(Tags.TOON), true, true, true, true, false, false, false, false, 1, 3, 0, 0, 0, 0)); reducePower(p.getPower(TributeToonPowerB.POWER_ID), p, 1); }

				// Check for Ironhammer Giants in hand/discard/draw
				if (tributes > 0)
				{
					for (AbstractCard c : player().hand.group)
					{
						if (c instanceof IronhammerGiant)
						{
							IronhammerGiant giant = (IronhammerGiant)c;
							giant.costReduce();
						}
					}
					
					for (AbstractCard c : AbstractDungeon.player.discardPile.group)
					{
						if (c instanceof IronhammerGiant)
						{
							IronhammerGiant giant = (IronhammerGiant)c;
							giant.costReduce();
						}
					}
					
					for (AbstractCard c : AbstractDungeon.player.drawPile.group)
					{
						if (c instanceof IronhammerGiant)
						{
							IronhammerGiant giant = (IronhammerGiant)c;
							giant.costReduce();
						}
					}
				}
				
				// Look through summonsList and remove #tributes strings
				if (tributes > 0) 
				{
					for (int i = 0; i < tributes; i++)
					{
						if (summonsInstance.summonList.size() > 0)
						{
							int endIndex = summonsInstance.summonList.size() - 1;
							DuelistCard temp = DuelistMod.summonMap.get(summonsInstance.summonList.get(endIndex));
							if (temp != null) { tributeList.add(temp); }
							//summonsInstance.summonMap.remove(summonsInstance.summonList.get(endIndex));
							summonsInstance.summonList.remove(endIndex);
						}
						
						if (summonsInstance.actualCardSummonList.size() > 0)
						{
							int endIndex = summonsInstance.actualCardSummonList.size() - 1;
							DuelistCard temp = summonsInstance.actualCardSummonList.get(endIndex);
							if (temp != null) { cardTribList.add(temp); }
							//summonsInstance.summonMap.remove(summonsInstance.summonList.get(endIndex));
							summonsInstance.actualCardSummonList.remove(endIndex);
						}
					}
				}


				summonsInstance.updateCount(summonsInstance.amount);
				summonsInstance.updateStringColors();
				summonsInstance.updateDescription();
				for (DuelistCard c : cardTribList) 
				{
					//c.onTribute(new Token()); 
					c.customOnTribute(new Token());
					c.runTributeSynergyFunctions(new Token());
					if (c.hasTag(Tags.AQUA))
					{
						// Check for Levia Dragon
						if (p.hasPower(LeviaDragonPower.POWER_ID))
						{
							LeviaDragonPower instance = (LeviaDragonPower) p.getPower(LeviaDragonPower.POWER_ID);
							int damageObelisk = instance.amount;
							int[] temp = new int[] {damageObelisk, damageObelisk, damageObelisk, damageObelisk, damageObelisk, damageObelisk, damageObelisk, damageObelisk, damageObelisk, damageObelisk, damageObelisk, damageObelisk};
							for (int i : temp) { i = i * tributes; }
							AbstractDungeon.actionManager.addToTop(new DamageAllEnemiesAction(p, temp, DamageType.THORNS, AbstractGameAction.AttackEffect.BLUNT_LIGHT));
						}
					}
					if (!c.hasTag(Tags.TOKEN) && c.hasTag(Tags.MONSTER))
					{
						DuelistMod.tribCombatCount++;
						DuelistMod.tribRunCount++;
						DuelistMod.tribTurnCount++;
					}
					if (DuelistMod.debug) { System.out.println("theDuelist:DuelistCard:powerTribute():2 ---> Called " + c.originalName + "'s customOnTribute()"); }
				}
				return cardTribList;
			}
		}
	}


	public static void tributeChecker(AbstractPlayer p, int tributes, DuelistCard tributingCard, boolean callOnTribute)
	{
		boolean challengeFailure = (Utilities.isCustomModActive("theDuelist:TributeRandomizer"));
		if (challengeFailure)
		{
			if (Utilities.isCustomModActive("challengethespire:Bronze Difficulty"))
			{
				if (AbstractDungeon.cardRandomRng.random(1, 3) == 1) 
				{ 
					AbstractDungeon.actionManager.addToBottom(new TalkAction(true, Strings.configFailedTribActionText, 1.0F, 2.0F));
					return;
				}
			}
			else if (Utilities.isCustomModActive("challengethespire:Silver Difficulty"))
			{
				if (AbstractDungeon.cardRandomRng.random(1, 2) == 1) 
				{ 
					AbstractDungeon.actionManager.addToBottom(new TalkAction(true, Strings.configFailedTribActionText, 1.0F, 2.0F));
					return;
				}
			}
			else if (Utilities.isCustomModActive("challengethespire:Gold Difficulty"))
			{
				if (AbstractDungeon.cardRandomRng.random(1, 3) != 1) 
				{ 
					AbstractDungeon.actionManager.addToBottom(new TalkAction(true, Strings.configFailedTribActionText, 1.0F, 2.0F));
					return;
				}
			}
			else if (Utilities.isCustomModActive("challengethespire:Platinum Difficulty"))
			{
				if (AbstractDungeon.cardRandomRng.random(1, 4) != 1) 
				{ 
					AbstractDungeon.actionManager.addToBottom(new TalkAction(true, Strings.configFailedTribActionText, 1.0F, 2.0F));
					return;
				}
			}
		}
		
		ArrayList<DuelistCard> tributeList = new ArrayList<DuelistCard>();
		ArrayList<DuelistCard> cardTribList = new ArrayList<DuelistCard>();
		
		// Check for Obelisk after tributing
		if (p.hasPower(ObeliskPower.POWER_ID) && tributes > 0)
		{
			ObeliskPower instance = (ObeliskPower) p.getPower(ObeliskPower.POWER_ID);
			int damageObelisk = instance.DAMAGE;
			DuelistCard.damageAllEnemiesThornsNormal(instance.DAMAGE * tributes);
		}
		
		

		// Check for Pharaoh's Curse
		if (p.hasPower(TributeSicknessPower.POWER_ID)) { damageSelfNotHP(tributes * p.getPower(TributeSicknessPower.POWER_ID).amount); }

		// Check for Toon Tribute power
		if (p.hasPower(TributeToonPower.POWER_ID) && tributes > 0) { AbstractDungeon.actionManager.addToTop(new RandomizedHandAction(returnTrulyRandomFromSets(Tags.MONSTER, Tags.TOON), true, true, true, true, false, false, false, false, 1, 3, 0, 0, 0, 0)); reducePower(p.getPower(TributeToonPower.POWER_ID), p, 1); }
		if (p.hasPower(TributeToonPowerB.POWER_ID) && tributes > 0) { AbstractDungeon.actionManager.addToTop(new RandomizedHandAction(returnTrulyRandomFromSet(Tags.TOON), true, true, true, true, false, false, false, false, 1, 3, 0, 0, 0, 0)); reducePower(p.getPower(TributeToonPowerB.POWER_ID), p, 1); }

		// Check for Ironhammer Giants in hand/discard/draw
		if (tributes > 0)
		{
			for (AbstractCard c : player().hand.group)
			{
				if (c instanceof IronhammerGiant)
				{
					IronhammerGiant giant = (IronhammerGiant)c;
					giant.costReduce();
				}
			}
			
			for (AbstractCard c : AbstractDungeon.player.discardPile.group)
			{
				if (c instanceof IronhammerGiant)
				{
					IronhammerGiant giant = (IronhammerGiant)c;
					giant.costReduce();
				}
			}
			
			for (AbstractCard c : AbstractDungeon.player.drawPile.group)
			{
				if (c instanceof IronhammerGiant)
				{
					IronhammerGiant giant = (IronhammerGiant)c;
					giant.costReduce();
				}
			}
		}
		
		if (p.hasPower(SummonPower.POWER_ID))
		{
			SummonPower summonsInstance = (SummonPower)p.getPower(SummonPower.POWER_ID);
			if (tributes > 0) 
			{
				for (int i = 0; i < tributes; i++)
				{
					if (summonsInstance.summonList.size() > 0)
					{
						int endIndex = summonsInstance.summonList.size() - 1;
						DuelistCard temp = DuelistMod.summonMap.get(summonsInstance.summonList.get(endIndex));
						if (temp != null) { tributeList.add(temp); }
					}
					
					if (summonsInstance.actualCardSummonList.size() > 0)
					{
						int endIndex = summonsInstance.actualCardSummonList.size() - 1;
						DuelistCard temp = summonsInstance.actualCardSummonList.get(endIndex);
						if (temp != null) { cardTribList.add(temp); }
					}
				}
			}
			
			if (callOnTribute)
			{
				for (DuelistCard c : cardTribList) 
				{
					//c.onTribute(tributingCard);
					c.customOnTribute(tributingCard);
					c.runTributeSynergyFunctions(tributingCard);
					if (c.hasTag(Tags.AQUA))
					{
						// Check for Levia Dragon
						if (p.hasPower(LeviaDragonPower.POWER_ID))
						{
							LeviaDragonPower instance = (LeviaDragonPower) p.getPower(LeviaDragonPower.POWER_ID);
							int damageObelisk = instance.amount;
							int[] temp = new int[] {damageObelisk, damageObelisk, damageObelisk, damageObelisk, damageObelisk, damageObelisk, damageObelisk, damageObelisk, damageObelisk, damageObelisk, damageObelisk, damageObelisk};
							for (int i : temp) { i = i * tributes; }
							AbstractDungeon.actionManager.addToTop(new DamageAllEnemiesAction(p, temp, DamageType.THORNS, AbstractGameAction.AttackEffect.BLUNT_LIGHT));
						}
					}
					if (DuelistMod.debug) { System.out.println("theDuelist:DuelistCard:tributeChecker() ---> Called " + c.originalName + "'s customOnTribute()"); }
				}
			}
			
			for (DuelistCard c : cardTribList)
			{
				if (!c.hasTag(Tags.TOKEN) && c.hasTag(Tags.MONSTER))
				{
					DuelistMod.tribCombatCount++;
					DuelistMod.tribRunCount++;
					DuelistMod.tribTurnCount++;
				}
			}
		}
	}
	// =============== /TRIBUTE MONSTER FUNCTIONS/ =======================================================================================================================================================
	
	// =============== TRIBUTE SYNERGY FUNCTIONS =========================================================================================================================================================
	
	// This function is called anytime a tribute happens
	// It automatically determines if a tribute synergy effect needs to trigger, and then triggers the appropriate one(s)
	// Also it checks for global effects that trigger whenever ANY synergy tribute occurs
	public void runTributeSynergyFunctions(DuelistCard tc)
	{
		// Special function to handle megatyped monsters, plus single check of global synergy effects
		if (this.hasTag(Tags.MEGATYPED))
		{
			megatypeTrib(tc);
			synergyTributeOneTimeChecks(tc, this);
		}
		
		// For any non-megatyped monster tributes, just loop through the monster types that the tributed card has to see if any match the tributing card
		else
		{
			ArrayList<CardTags> cardTypes = getAllMonsterTypes(this);				
			for (CardTags t : cardTypes)
			{
				// Map determines which function to run based on card tag currently iterating over
				// If tributed card has any given type from the map, we run the function that checks the tributing card for matching type
				// The functions that check matching type on the tributing cards also handle triggering the appropriate synergy effects
				switch (DuelistMod.monsterTypeTributeSynergyFunctionMap.get(t))
				{
					case 0: 
						aquaSynTrib(tc); 						
						if (DuelistMod.debug) { DuelistMod.logger.info("ran aqua syn trib automatically from tributing " + this.originalName + " for " + tc.originalName); }
						break;
					case 1: 
						dragonSynTrib(tc);						
						if (DuelistMod.debug) { DuelistMod.logger.info("ran dragon syn trib automatically from tributing " + this.originalName + " for " + tc.originalName); }
						break;
					case 2: 
						fiendSynTrib(tc); 
						if (DuelistMod.debug) { DuelistMod.logger.info("ran fiend syn trib automatically from tributing " + this.originalName + " for " + tc.originalName); }
						break;
					case 3: 
						insectSynTrib(tc); 
						if (DuelistMod.debug) { DuelistMod.logger.info("ran insect syn trib automatically from tributing " + this.originalName + " for " + tc.originalName); }
						break;
					case 4: 
						machineSynTrib(tc); 
						if (DuelistMod.debug) { DuelistMod.logger.info("ran machine syn trib automatically from tributing " + this.originalName + " for " + tc.originalName); }
						break;
					case 5: 
						naturiaSynTrib(tc);
						if (DuelistMod.debug) { DuelistMod.logger.info("ran naturia syn trib automatically from tributing " + this.originalName + " for " + tc.originalName); }
						break;
					case 6: 
						plantSynTrib(tc); 
						if (DuelistMod.debug) { DuelistMod.logger.info("ran plant syn trib automatically from tributing " + this.originalName + " for " + tc.originalName); }
						break;
					case 7: 
						predaplantSynTrib(tc); 
						if (DuelistMod.debug) { DuelistMod.logger.info("ran predaplant syn trib automatically from tributing " + this.originalName + " for " + tc.originalName); }
						break;
					case 8: 
						spellcasterSynTrib(tc); 
						if (DuelistMod.debug) { DuelistMod.logger.info("ran spellcaster syn trib automatically from tributing " + this.originalName + " for " + tc.originalName); }
						break;
					case 9: 
						superSynTrib(tc); 
						if (DuelistMod.debug) { DuelistMod.logger.info("ran superheavy syn trib automatically from tributing " + this.originalName + " for " + tc.originalName); }
						break;
					case 10: 
						toonSynTrib(tc);
						if (DuelistMod.debug) { DuelistMod.logger.info("ran toon syn trib automatically from tributing " + this.originalName + " for " + tc.originalName); }
						break;
					case 11: 
						zombieSynTrib(tc); 
						lavaZombieEffectHandler();	// increase lava orbs evoke amounts by their passive amounts, this happens every time we tribute any zombie
						if (DuelistMod.debug) { DuelistMod.logger.info("ran zombie syn trib automatically from tributing " + this.originalName + " for " + tc.originalName); }
						break;
					default: break;
				}
			}
			
			// And finally for non-megatyped cards we still need to run one-time checks for global type-agnostic synergy effects
			synergyTributeOneTimeChecks(tc, this);
		}
	}
	
	// things to check for only one time when a synergy tribute happens
	// only runs once for megatyped situations and other weirdness that may occur with type modifications
	public static void synergyTributeOneTimeChecks(DuelistCard tributingCard, DuelistCard tributedCard)
	{
		ArrayList<CardTags> tributingCardMonsterTypes = getAllMonsterTypes(tributingCard);
		ArrayList<CardTags> tributedCardMonsterTypes = getAllMonsterTypes(tributedCard);
		boolean oneMatchingType = false;
		for (CardTags t : tributingCardMonsterTypes)
		{
			if (tributedCardMonsterTypes.contains(t))
			{
				oneMatchingType = true;
				break;
			}
		}
		
		// Successful synergy tribute for at least one type that matches between the two cards involved in this tribute
		// Call any effects that trigger on any given synergy tribute in this block
		if (oneMatchingType)
		{
			if (AbstractDungeon.player.hasRelic(MillenniumScale.ID))
			{
				gainEnergy(1);
			}
		}
	}
	
	public static void megatypeTrib(DuelistCard tc)
	{
		dragonSynTrib(tc);
		machineSynTrib(tc);
		toonSynTrib(tc);
		fiendSynTrib(tc);
		aquaSynTrib(tc);
		naturiaSynTrib(tc);
		plantSynTrib(tc);
		predaplantSynTrib(tc);
		insectSynTrib(tc);
		superSynTrib(tc);
		spellcasterSynTrib(tc);
		zombieSynTrib(tc);
		if (DuelistMod.debug) { DuelistMod.logger.info("Ran megatype tribute function, tributing card: " + tc.originalName); }
	}
	
	public static void dragonSynTrib(DuelistCard tributingCard)
	{
		if (tributingCard.hasTag(Tags.DRAGON))
		{
			if (!AbstractDungeon.player.hasPower(GravityAxePower.POWER_ID)) 
			{ 
				if (!AbstractDungeon.player.hasPower(MountainPower.POWER_ID)) 
				{ 
					applyPowerToSelf(new StrengthPower(AbstractDungeon.player, DuelistMod.dragonStr)); 
					//applyPowerToSelf(new LoseStrengthPower(AbstractDungeon.player, DuelistMod.dragonStr)); 
				}
				else 
				{ 
					applyPowerToSelf(new StrengthPower(AbstractDungeon.player, DuelistMod.dragonStr + 1)); 
					//applyPowerToSelf(new LoseStrengthPower(AbstractDungeon.player, DuelistMod.dragonStr)); 
				}
			}
			
			if (AbstractDungeon.player.hasRelic(DragonRelicB.ID))
			{
				if (DuelistMod.dragonRelicBFlipper) { drawRare(1, CardRarity.RARE); }
				DuelistMod.dragonRelicBFlipper = !DuelistMod.dragonRelicBFlipper;
			}
			
			if (player().hasPower(TyrantWingPower.POWER_ID))
			{
				TwoAmountPower power = (TwoAmountPower)player().getPower(TyrantWingPower.POWER_ID);
				power.amount2++;
				power.updateDescription();
			}
			
			if (player().hasRelic(DragonRelicC.ID))
			{
				AbstractRelic relic = player().getRelic(DragonRelicC.ID);
				int roll = AbstractDungeon.cardRandomRng.random(1, 5);
				if (roll == 1)
				{
					DuelistCard.gainEnergy(1);
					relic.flash();
				}
			}
		}
	}
	
	public static void machineSynTrib(DuelistCard tributingCard)
	{
		if (tributingCard.hasTag(Tags.MACHINE))
		{
			if (!DuelistMod.machineArtifactFlipper) { applyPowerToSelf(new ArtifactPower(player(), DuelistMod.machineArt)); }
			DuelistMod.machineArtifactFlipper = !DuelistMod.machineArtifactFlipper;
		}
	}
	
	public static void toonSynTrib(DuelistCard tributingCard)
	{
		if (tributingCard.hasTag(Tags.TOON)) 
		{ 
			for (AbstractMonster m : AbstractDungeon.getMonsters().monsters)
			{
				if (!m.isDead && !m.isDying && !m.isDeadOrEscaped() && !m.isEscaping && m.currentHealth > 0)
				{
					applyPower(new VulnerablePower(m, DuelistMod.toonVuln, false), m);
				}
			}
		}
	}
	
	public static void fiendSynTrib(DuelistCard tributingCard)
	{
		if (tributingCard.hasTag(Tags.FIEND))
		{
			AbstractPlayer p = AbstractDungeon.player;
			if (p.hasPower(DoomdogPower.POWER_ID)) 
			{ 
				int dmgAmount = p.getPower(DoomdogPower.POWER_ID).amount; 
				damageAllEnemiesThornsNormal(dmgAmount); 
			}
			if (p.hasPower(RedMirrorPower.POWER_ID)) 
			{ 
				for (AbstractCard c : p.discardPile.group) 
				{ 
					if (c.cost > 0)	
					{
						c.modifyCostForTurn(-p.getPower(RedMirrorPower.POWER_ID).amount);	
						c.isCostModifiedForTurn = true;	
					}
				}
			}
			AbstractDungeon.actionManager.addToBottom(new FetchAction(p.discardPile, DuelistMod.fiendDraw)); 
		}
	}
	
	public static void aquaSynTrib(DuelistCard tributingCard)
	{
		if (tributingCard.hasTag(Tags.AQUA))
		{
			for (AbstractCard c : player().hand.group)
			{
				if (c instanceof DuelistCard)
				{
					DuelistCard dC = (DuelistCard)c;
					if (dC.baseSummons > 0)
					{
						dC.modifySummonsForTurn(DuelistMod.aquaInc);
					}
					
					if (player().hasRelic(AquaRelicB.ID) && dC.baseTributes > 0)
					{
						dC.modifyTributesForTurn(-DuelistMod.aquaInc);
					}
				}
			}
		}
	}
	
	public static void naturiaSynTrib(DuelistCard tributingCard)
	{
		
	}
	
	public static void plantSynTrib(DuelistCard tributingCard)
	{
		if (player().hasPower(VioletCrystalPower.POWER_ID) && tributingCard.hasTag(Tags.PLANT)) { constrictAllEnemies(player(), DuelistMod.plantConstricted + 2); }
		else if (tributingCard.hasTag(Tags.PLANT)) { constrictAllEnemies(player(), DuelistMod.plantConstricted); }
	}
	
	public static void predaplantSynTrib(DuelistCard tributingCard)
	{
		plantSynTrib(tributingCard);
		if (tributingCard.hasTag(Tags.PREDAPLANT)) { applyPowerToSelf(new ThornsPower(player(), DuelistMod.predaplantThorns)); }
	}
	
	public static void insectSynTrib(DuelistCard tributingCard)
	{
		if (player().hasPower(VioletCrystalPower.POWER_ID) && tributingCard.hasTag(Tags.INSECT)) { poisonAllEnemies(player(), DuelistMod.insectPoisonDmg + 2); }
		else if (tributingCard.hasTag(Tags.INSECT)) { poisonAllEnemies(player(), DuelistMod.insectPoisonDmg); }
	}
	
	public static void superSynTrib(DuelistCard tributingCard)
	{
		if (tributingCard.hasTag(Tags.SUPERHEAVY))
		{
			applyPowerToSelf(new DexterityPower(AbstractDungeon.player, DuelistMod.superheavyDex));
		}
	}
	
	public static void spellcasterSynTrib(DuelistCard tributingCard)
	{
		if (tributingCard.hasTag(Tags.SPELLCASTER))
		{
			AbstractPlayer p = AbstractDungeon.player;
			if (p.hasPower(SpellbookKnowledgePower.POWER_ID))
			{
				applyPowerToSelf(new FocusPower(p, p.getPower(SpellbookKnowledgePower.POWER_ID).amount));
			}
			
			if (p.hasPower(SpellbookMiraclePower.POWER_ID))
			{
				invert(1);
			}
			
			if (p.hasPower(SpellbookPowerPower.POWER_ID))
			{
				for (int i = 0; i < p.getPower(SpellbookPowerPower.POWER_ID).amount; i++)
				{
					DuelistCard randSpellcaster = (DuelistCard)DuelistCard.returnTrulyRandomFromSets(Tags.MONSTER, Tags.SPELLCASTER);
					AbstractDungeon.actionManager.addToTop(new RandomizedHandAction(randSpellcaster, true));
				}
			}
			
			if (p.hasPower(SpellbookLifePower.POWER_ID))
			{
				gainTempHP(p.getPower(SpellbookLifePower.POWER_ID).amount);
			}
		}
	}
	
	public static void zombieSynTrib(DuelistCard tributingCard)
	{
		
	}
	// =============== /TRIBUTE SYNERGY FUNCTIONS/ =======================================================================================================================================================
	
	
	// =============== INCREMENT FUNCTIONS =========================================================================================================================================================
	public static int getSummons(AbstractPlayer p)
	{
		if (!p.hasPower(SummonPower.POWER_ID)) { return 0; }
		else
		{
			SummonPower summonsInstance = (SummonPower)p.getPower(SummonPower.POWER_ID);
			return summonsInstance.amount;
		}
	}

	public static int getMaxSummons(AbstractPlayer p)
	{
		if (!p.hasPower(SummonPower.POWER_ID)) { return 0; }
		else
		{
			SummonPower summonsInstance = (SummonPower)p.getPower(SummonPower.POWER_ID);
			return summonsInstance.MAX_SUMMONS;
		}
	}

	public static void setMaxSummons(AbstractPlayer p, int amount)
	{
		if (p.hasPower(SummonPower.POWER_ID))
		{
			SummonPower summonsInstance = (SummonPower)p.getPower(SummonPower.POWER_ID);
			summonsInstance.MAX_SUMMONS = amount; DuelistMod.lastMaxSummons = amount;
			if (summonsInstance.MAX_SUMMONS > 5 && p.hasRelic(MillenniumKey.ID)) { summonsInstance.MAX_SUMMONS = 5; DuelistMod.lastMaxSummons = 5;}
			summonsInstance.updateCount(summonsInstance.amount);
			summonsInstance.updateStringColors();
			summonsInstance.updateDescription();
		}

		try {
			SpireConfig config = new SpireConfig("TheDuelist", "DuelistConfig",DuelistMod.duelistDefaults);
			config.setInt(DuelistMod.PROP_MAX_SUMMONS, DuelistMod.lastMaxSummons);
			config.save();
			if (DuelistMod.debug) { System.out.println("theDuelist:DuelistCard:setMaxSummons() ---> ran try block, lastMaxSummons: " + DuelistMod.lastMaxSummons); }
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public static void incMaxSummons(int amount)
	{
		incMaxSummons(AbstractDungeon.player, amount);
	}

	public static void incMaxSummons(AbstractPlayer p, int amount)
	{
		boolean curseFailure = isPsiCurseActive();
		boolean challengeFailure = (Utilities.isCustomModActive("theDuelist:MaxSummonChallenge"));
		if (challengeFailure)
		{
			if (Utilities.isCustomModActive("challengethespire:Bronze Difficulty"))
			{
				if (AbstractDungeon.cardRandomRng.random(1, 3) == 1) 
				{ 
					AbstractDungeon.actionManager.addToBottom(new TalkAction(true, Strings.configFailedIncActionText, 1.0F, 2.0F));
					return; 
				}
			}
			else if (Utilities.isCustomModActive("challengethespire:Silver Difficulty"))
			{
				if (AbstractDungeon.cardRandomRng.random(1, 2) == 1) 
				{ 
					AbstractDungeon.actionManager.addToBottom(new TalkAction(true, Strings.configFailedIncActionText, 1.0F, 2.0F));
					return; 
				}
			}
			else if (Utilities.isCustomModActive("challengethespire:Gold Difficulty"))
			{
				if (AbstractDungeon.cardRandomRng.random(1, 3) != 1) 
				{ 
					AbstractDungeon.actionManager.addToBottom(new TalkAction(true, Strings.configFailedIncActionText, 1.0F, 2.0F));
					return; 
				}
			}
			else if (Utilities.isCustomModActive("challengethespire:Platinum Difficulty"))
			{
				if (AbstractDungeon.cardRandomRng.random(1, 4) != 1) 
				{ 
					AbstractDungeon.actionManager.addToBottom(new TalkAction(true, Strings.configFailedIncActionText, 1.0F, 2.0F));
					return; 
				}
			}
		}
		if (curseFailure) { if (AbstractDungeon.cardRandomRng.random(1, 2) == 1) { AbstractDungeon.actionManager.addToBottom(new TalkAction(true, Strings.configFailedIncActionText, 1.0F, 2.0F)); return; }}
		if (DuelistMod.debug) { DuelistMod.logger.info("Incrementing Max Summons by: " + amount); }
		if (p.hasPower(SummonPower.POWER_ID))
		{
			SummonPower summonsInstance = (SummonPower)p.getPower(SummonPower.POWER_ID);
			if (summonsInstance.MAX_SUMMONS != 5 && p.hasRelic(MillenniumKey.ID)) { summonsInstance.MAX_SUMMONS = 5; DuelistMod.lastMaxSummons = 5; }
			else { summonsInstance.MAX_SUMMONS += amount; DuelistMod.lastMaxSummons += amount; }			
			summonsInstance.updateCount(summonsInstance.amount);
			summonsInstance.updateStringColors();
			summonsInstance.updateDescription();
		}
		
		else
		{
			DuelistMod.lastMaxSummons += amount;
		}
		
		if (p.hasPower(SphereKuribohPower.POWER_ID))
		{
			gainTempHP(p.getPower(SphereKuribohPower.POWER_ID).amount);
		}

		try {
			SpireConfig config = new SpireConfig("TheDuelist", "DuelistConfig",DuelistMod.duelistDefaults);
			config.setInt(DuelistMod.PROP_MAX_SUMMONS, DuelistMod.lastMaxSummons);
			config.save();
			if (DuelistMod.debug) { System.out.println("theDuelist:DuelistCard:incMaxSummons() ---> ran try block, lastMaxSummons: " + DuelistMod.lastMaxSummons); }
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static void decMaxSummons(AbstractPlayer p, int amount)
	{
		if (p.hasPower(SummonPower.POWER_ID))
		{
			SummonPower summonsInstance = (SummonPower)p.getPower(SummonPower.POWER_ID);
			if (summonsInstance.MAX_SUMMONS != 5 && p.hasRelic(MillenniumKey.ID)) { summonsInstance.MAX_SUMMONS = 5; DuelistMod.lastMaxSummons = 5;}
			else if (summonsInstance.MAX_SUMMONS - amount < 1) { summonsInstance.MAX_SUMMONS = 1; DuelistMod.lastMaxSummons = 1; }
			else { summonsInstance.MAX_SUMMONS -= amount; DuelistMod.lastMaxSummons -= amount; }
			summonsInstance.updateCount(summonsInstance.amount);
			summonsInstance.updateStringColors();
			summonsInstance.updateDescription();
		}

		try {
			SpireConfig config = new SpireConfig("TheDuelist", "DuelistConfig",DuelistMod.duelistDefaults);
			config.setInt(DuelistMod.PROP_MAX_SUMMONS, DuelistMod.lastMaxSummons);
			config.save();
			if (DuelistMod.debug) { System.out.println("theDuelist:DuelistCard:decMaxSummons() ---> ran try block, lastMaxSummons: " + DuelistMod.lastMaxSummons); }
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	// =============== /INCREMENT FUNCTIONS/ =======================================================================================================================================================
	
	
	
	// =============== RESUMMON FUNCTIONS =========================================================================================================================================================
	public void checkResummon()
	{
		if (this.hasTag(Tags.ZOMBIE)) { block(DuelistMod.zombieResummonBlock); DuelistMod.zombiesResummonedThisCombat++; DuelistMod.zombiesResummonedThisRun++; }
		if (AbstractDungeon.player.hasPower(CardSafePower.POWER_ID)) { drawTag(AbstractDungeon.player.getPower(CardSafePower.POWER_ID).amount, Tags.ZOMBIE); }
	}
	
	public static void fullResummon(DuelistCard cardCopy, boolean upgradeResummon, AbstractMonster target, boolean superFast)
	{
		if (AbstractDungeon.player.hasPower(SummonPower.POWER_ID))
		{
			SummonPower instance = (SummonPower)AbstractDungeon.player.getPower(SummonPower.POWER_ID);
			if (!instance.isMonsterSummoned(new VanityFiend().originalName) && !cardCopy.hasTag(Tags.EXEMPT) && !AbstractDungeon.player.hasPower(MortalityPower.POWER_ID))
			{
				cardCopy = (DuelistCard) cardCopy.makeStatEquivalentCopy();
				if (!cardCopy.tags.contains(Tags.TRIBUTE)) { cardCopy.misc = 52; }
				if (upgradeResummon) { cardCopy.upgrade(); }
				cardCopy.freeToPlayOnce = true;
				cardCopy.applyPowers();
				cardCopy.purgeOnUse = true;
				cardCopy.dontTriggerOnUseCard = true;
				if (superFast) { AbstractDungeon.actionManager.addToTop(new QueueCardSuperFastAction(cardCopy, target)); }
				else { AbstractDungeon.actionManager.addToTop(new QueueCardSuperFastAction(cardCopy, target, 1.0F)); }
				cardCopy.onResummon(1);
				cardCopy.checkResummon();
			}		
			else if (AbstractDungeon.player.hasPower(MortalityPower.POWER_ID))
			{
				MortalityPower pow = (MortalityPower)AbstractDungeon.player.getPower(MortalityPower.POWER_ID);
				pow.triggerOnResummon();
			}
		}
		else if (!cardCopy.hasTag(Tags.EXEMPT) && !AbstractDungeon.player.hasPower(MortalityPower.POWER_ID))
		{
			cardCopy = (DuelistCard) cardCopy.makeStatEquivalentCopy();
			if (!cardCopy.tags.contains(Tags.TRIBUTE)) { cardCopy.misc = 52; }
			if (upgradeResummon) { cardCopy.upgrade(); }
			cardCopy.freeToPlayOnce = true;
			cardCopy.applyPowers();
			cardCopy.purgeOnUse = true;
			cardCopy.dontTriggerOnUseCard = true;
			if (superFast) { AbstractDungeon.actionManager.addToTop(new QueueCardSuperFastAction(cardCopy, target)); }
			else { AbstractDungeon.actionManager.addToTop(new QueueCardSuperFastAction(cardCopy, target, 1.0F)); }
			cardCopy.onResummon(1);
			cardCopy.checkResummon();
		}
		
		else if (AbstractDungeon.player.hasPower(MortalityPower.POWER_ID))
		{
			MortalityPower pow = (MortalityPower)AbstractDungeon.player.getPower(MortalityPower.POWER_ID);
			pow.triggerOnResummon();
		}
	}
	
	public static void polyResummon(DuelistCard cardCopy, boolean upgradeResummon, AbstractMonster target, boolean superFast)
	{
		if (AbstractDungeon.player.hasPower(SummonPower.POWER_ID))
		{
			SummonPower instance = (SummonPower)AbstractDungeon.player.getPower(SummonPower.POWER_ID);
			if (!instance.isMonsterSummoned(new VanityFiend().originalName) && !AbstractDungeon.player.hasPower(MortalityPower.POWER_ID))
			{
				cardCopy = (DuelistCard) cardCopy.makeStatEquivalentCopy();
				if (!cardCopy.tags.contains(Tags.TRIBUTE)) { cardCopy.misc = 52; }
				if (upgradeResummon) { cardCopy.upgrade(); }
				cardCopy.freeToPlayOnce = true;
				cardCopy.applyPowers();
				cardCopy.purgeOnUse = true;
				cardCopy.dontTriggerOnUseCard = true;
				if (superFast) { AbstractDungeon.actionManager.addToTop(new QueueCardSuperFastAction(cardCopy, target)); }
				else { AbstractDungeon.actionManager.addToTop(new QueueCardSuperFastAction(cardCopy, target, 1.0F)); }
				cardCopy.onResummon(1);
				cardCopy.checkResummon();
			}	
			else if (AbstractDungeon.player.hasPower(MortalityPower.POWER_ID))
			{
				MortalityPower pow = (MortalityPower)AbstractDungeon.player.getPower(MortalityPower.POWER_ID);
				pow.triggerOnResummon();
			}
		}
		else if (!AbstractDungeon.player.hasPower(MortalityPower.POWER_ID))
		{
			cardCopy = (DuelistCard) cardCopy.makeStatEquivalentCopy();
			if (!cardCopy.tags.contains(Tags.TRIBUTE)) { cardCopy.misc = 52; }
			if (upgradeResummon) { cardCopy.upgrade(); }
			cardCopy.freeToPlayOnce = true;
			cardCopy.applyPowers();
			cardCopy.purgeOnUse = true;
			cardCopy.dontTriggerOnUseCard = true;
			if (superFast) { AbstractDungeon.actionManager.addToTop(new QueueCardSuperFastAction(cardCopy, target)); }
			else { AbstractDungeon.actionManager.addToTop(new QueueCardSuperFastAction(cardCopy, target, 1.0F)); }
			cardCopy.onResummon(1);
			cardCopy.checkResummon();
		}
		else 
		{
			MortalityPower pow = (MortalityPower)AbstractDungeon.player.getPower(MortalityPower.POWER_ID);
			pow.triggerOnResummon();
		}
	}
	
	public static void playNoResummon(DuelistCard cardCopy, boolean upgradeResummon, AbstractCreature target, boolean superFast)
	{
		if (AbstractDungeon.player.hasPower(SummonPower.POWER_ID))
		{
			SummonPower instance = (SummonPower)AbstractDungeon.player.getPower(SummonPower.POWER_ID);
			if (!instance.isMonsterSummoned(new VanityFiend().originalName) && !cardCopy.hasTag(Tags.EXEMPT))
			{
				cardCopy = (DuelistCard) cardCopy.makeStatEquivalentCopy();
				if (!cardCopy.tags.contains(Tags.TRIBUTE)) { cardCopy.misc = 52; }
				if (upgradeResummon) { cardCopy.upgrade(); }
				cardCopy.freeToPlayOnce = true;
				cardCopy.applyPowers();
				cardCopy.purgeOnUse = true;
				cardCopy.dontTriggerOnUseCard = true;
				if (superFast) { AbstractDungeon.actionManager.addToTop(new QueueCardSuperFastAction(cardCopy, target)); }
				else { AbstractDungeon.actionManager.addToTop(new QueueCardSuperFastAction(cardCopy, target, 1.0F)); }
			}		
		}
		else if (!cardCopy.hasTag(Tags.EXEMPT))
		{
			cardCopy = (DuelistCard) cardCopy.makeStatEquivalentCopy();
			if (!cardCopy.tags.contains(Tags.TRIBUTE)) { cardCopy.misc = 52; }
			if (upgradeResummon) { cardCopy.upgrade(); }
			cardCopy.freeToPlayOnce = true;
			cardCopy.applyPowers();
			cardCopy.purgeOnUse = true;
			cardCopy.dontTriggerOnUseCard = true;
			if (superFast) { AbstractDungeon.actionManager.addToTop(new QueueCardSuperFastAction(cardCopy, target)); }
			else { AbstractDungeon.actionManager.addToTop(new QueueCardSuperFastAction(cardCopy, target, 1.0F)); }
		}
	}
	
	public static void playNoResummon(BuffCard cardCopy, boolean upgradeResummon, AbstractMonster target, boolean superFast)
	{			
		cardCopy.freeToPlayOnce = true;
		cardCopy.applyPowers();
		cardCopy.purgeOnUse = true;
		cardCopy.dontTriggerOnUseCard = true;
		if (superFast) { AbstractDungeon.actionManager.addToTop(new QueueCardSuperFastAction(cardCopy, target)); }
		else { AbstractDungeon.actionManager.addToTop(new QueueCardSuperFastAction(cardCopy, target, 1.0F)); }		
	}
	// =============== /RESUMMON FUNCTIONS/ =======================================================================================================================================================
	
	
	
	// =============== SUMMON MODIFICATION FUNCTIONS =========================================================================================================================================================
	public void changeSummonsInBattle(int addAmount, boolean combat) { AbstractDungeon.actionManager.addToTop(new ModifySummonAction(this, addAmount, combat)); }
	
	public void upgradeSummons(int add)
	{
		this.summons = this.baseSummons += add;
		this.upgradedSummons = true;
	}
	
	public void modifySummonsPerm(int add)
	{
		if (this.summons + add <= 0)
		{
			this.baseSummons = this.summons = 0;
			int indexOfTribText = this.rawDescription.indexOf("Summon");
			int modIndex = 21;
			int indexOfNL = indexOfTribText + 21;
			if (this.rawDescription.substring(indexOfNL, indexOfNL + 4).equals(" NL ")) { modIndex += 4; }
			if (indexOfTribText > -1)
			{
				String newDesc = this.rawDescription.substring(0, indexOfTribText) + this.rawDescription.substring(indexOfTribText + modIndex);
				this.rawDescription = newDesc;
				this.originalDescription = newDesc;
				if (DuelistMod.debug) { System.out.println(this.originalName + " made a string: " + newDesc + " this.baseSummons + add : " + this.baseSummons + add); }
			}
		}
		else { this.baseSummons = this.summons += add; }
		this.isSummonsModified = true;
		this.isSummonModPerm = true;
		this.permSummonChange += add;
		this.initializeDescription();
	}
	
	public void modifySummonsForTurn(int add)
	{
		if (this.summons + add <= 0)
		{
			this.summons = 0;
			this.summonsForTurn = 0;
			int indexOfTribText = this.rawDescription.indexOf("Summon");
			int modIndex = 21;
			int indexOfNL = indexOfTribText + 21;
			if (this.rawDescription.substring(indexOfNL, indexOfNL + 4).equals(" NL ")) { modIndex += 4; }
			if (indexOfTribText > -1)
			{
				String newDesc = this.rawDescription.substring(0, indexOfTribText) + this.rawDescription.substring(indexOfTribText + modIndex);
				this.originalDescription = this.rawDescription;
				this.rawDescription = newDesc;
				if (DuelistMod.debug) { System.out.println(this.originalName + " made a string: " + newDesc + " this.summons + add : " + this.summons + add); }
			}
		}
		else { this.originalDescription = this.rawDescription; this.summonsForTurn = this.summons += add; }
		this.isSummonsModifiedForTurn = true;		
		this.isSummonsModified = true;
		this.initializeDescription();
	}
	
	public void modifySummons(int add)
	{
		if (this.summons + add <= 0)
		{
			this.summons = 0;
			int indexOfTribText = this.rawDescription.indexOf("Summon");
			int modIndex = 21;
			int indexOfNL = indexOfTribText + 21;
			if (this.rawDescription.substring(indexOfNL, indexOfNL + 4).equals(" NL ")) { modIndex += 4; }
			if (indexOfTribText > -1)
			{
				this.originalDescription = this.rawDescription;
				String newDesc = this.rawDescription.substring(0, indexOfTribText) + this.rawDescription.substring(indexOfTribText + modIndex);
				this.rawDescription = newDesc;
				if (DuelistMod.debug) { System.out.println(this.originalName + " made a string: " + newDesc + " this.baseSummons + add : " + this.baseSummons + add); }
			}
		}
		else { this.summons += add; }		
		this.isSummonsModified = true;
		this.initializeDescription();
	}
	
	public void setSummons(int set)
	{
		if (set <= 0)
		{
			this.baseSummons = this.summons = 0;
			int indexOfTribText = this.rawDescription.indexOf("Summon");
			int modIndex = 21;
			int indexOfNL = indexOfTribText + 21;
			if (this.rawDescription.substring(indexOfNL, indexOfNL + 4).equals(" NL ")) { modIndex += 4; }
			if (indexOfTribText > -1)
			{
				this.originalDescription = this.rawDescription;
				String newDesc = this.rawDescription.substring(0, indexOfTribText) + this.rawDescription.substring(indexOfTribText + modIndex);
				this.rawDescription = newDesc;
				if (DuelistMod.debug) { System.out.println(this.originalName + " made a string: " + newDesc + " this.baseSummons + add : " + this.summons); }
			}
		}
		else { this.baseSummons = this.summons = set; }		
		this.isSummonsModified = true;
		this.initializeDescription();
	}
	
	public static void addMonsterToHandModSummons(String name, int add, boolean combat)
	{
    	DuelistCard newCopy = (DuelistCard) DuelistCard.newCopyOfMonster(name).makeStatEquivalentCopy();
    	if (newCopy.summons > 0)
    	{
    		AbstractDungeon.actionManager.addToTop(new ModifySummonAction(newCopy, add, combat));
    		AbstractDungeon.effectList.add(new ShowCardAndAddToHandEffect(newCopy));
    	}
	}
	
	public static void addMonsterToDeckModSummons(String name, int add, boolean combat)
	{
    	DuelistCard newCopy = (DuelistCard) DuelistCard.newCopyOfMonster(name).makeStatEquivalentCopy();
    	if (newCopy.summons > 0)
    	{
    		AbstractDungeon.actionManager.addToTop(new ModifySummonAction(newCopy, add, combat));
    		AbstractDungeon.effectList.add(new ShowCardAndAddToDrawPileEffect(newCopy, true, false));
    	}
	}
	
	public static void obtainMonsterModSummons(String name, int add)
	{
    	DuelistCard newCopy = (DuelistCard) DuelistCard.newCopyOfMonster(name).makeStatEquivalentCopy();
    	if (newCopy.summons > 0)
    	{
    		AbstractDungeon.actionManager.addToTop(new ModifySummonPermAction(newCopy, add));
    		AbstractDungeon.effectList.add(new ShowCardAndObtainEffect(newCopy, (float) (Settings.WIDTH / 2), (float) (Settings.HEIGHT / 2)));
    	}
	}
	// =============== /SUMMON MODIFICATION FUNCTIONS/ =======================================================================================================================================================
	
	public void upgradeSecondMagic(int add)
	{
		this.secondMagic = this.baseSecondMagic += add;
		this.upgradedSecondMagic = true;
	}
	
	// =============== TRIBUTE MODIFICATION FUNCTIONS =========================================================================================================================================================
	public void changeTributesInBattle(int addAmount, boolean combat)
	{
		AbstractDungeon.actionManager.addToTop(new ModifyTributeAction(this, addAmount, combat));
	}
	
	public void upgradeTributes(int add)
	{
		this.tributes = this.baseTributes += add;
		this.upgradedTributes = true;
	}
	
	public void modifyTributesPerm(int add)
	{
		if (this.tributes + add <= 0)
		{
			this.baseTributes = this.tributes = 0;
			int indexOfTribText = this.rawDescription.indexOf("Tribute");
			int modIndex = 22;
			int indexOfNL = indexOfTribText + 22;
			if (this.rawDescription.substring(indexOfNL, indexOfNL + 4).equals(" NL ")) { modIndex += 4; }
			if (indexOfTribText > -1)
			{
				String newDesc = this.rawDescription.substring(0, indexOfTribText) + this.rawDescription.substring(indexOfTribText + modIndex);
				this.rawDescription = newDesc;
				this.originalDescription = newDesc;
				if (DuelistMod.debug) { System.out.println(this.originalName + " made a string: " + newDesc + " this.baseTributes + add : " + this.baseTributes + add); }
			}
		}
		else { this.baseTributes = this.tributes += add; }
		this.isTributesModified = true;
		this.isTribModPerm = true;
		this.permTribChange += add;
		this.initializeDescription();
	}
	
	public void modifyTributesForTurn(int add)
	{
		if (this.tributes + add <= 0)
		{
			this.tributesForTurn = 0;
			this.tributes = 0;
			int indexOfTribText = this.rawDescription.indexOf("Tribute");
			int modIndex = 22;
			int indexOfNL = indexOfTribText + 22;
			
			if (indexOfTribText > -1)
			{
				if (this.rawDescription.substring(indexOfNL, indexOfNL + 4).equals(" NL ")) { modIndex += 4; }
				String newDesc = this.rawDescription.substring(0, indexOfTribText) + this.rawDescription.substring(indexOfTribText + modIndex);
				this.originalDescription = this.rawDescription;
				this.rawDescription = newDesc;
				if (DuelistMod.debug) { System.out.println(this.originalName + " made a string: " + newDesc + " this.tributes + add : " + this.tributes + add); }
			}
		}
		else { this.originalDescription = this.rawDescription; this.tributesForTurn = this.tributes += add; }
		this.isTributesModifiedForTurn = true;
		this.isTributesModified = true;
		this.initializeDescription();
	}

	public void modifyTributes(int add)
	{
		
		if (this.tributes + add <= 0)
		{
			this.tributes = this.baseTributes = 0;
			int indexOfTribText = this.rawDescription.indexOf("Tribute");
			int modIndex = 22;
			int indexOfNL = indexOfTribText + 22;
			if (indexOfTribText > -1)
			{
				if (this.rawDescription.substring(indexOfNL, indexOfNL + 4).equals(" NL ")) { modIndex += 4; }
				this.originalDescription = this.rawDescription;
				String newDesc = this.rawDescription.substring(0, indexOfTribText) + this.rawDescription.substring(indexOfTribText + modIndex);
				this.rawDescription = newDesc;
				if (DuelistMod.debug) { System.out.println(this.originalName + " made a string: " + newDesc + " this.baseTributes + add : " + this.baseTributes + add); }
			}			
		}
		else { this.baseTributes = this.tributes += add; }
		this.isTributesModified = true; 
		this.initializeDescription();
	}
	
	public void setTributes(int set)
	{
		if (set <= 0)
		{
			this.baseTributes = this.tributes = 0;
			int indexOfTribText = this.rawDescription.indexOf("Tribute");
			int modIndex = 22;
			int indexOfNL = indexOfTribText + 22;
			if (this.rawDescription.substring(indexOfNL, indexOfNL + 4).equals(" NL ")) { modIndex += 4; }
			if (indexOfTribText > -1)
			{
				this.originalDescription = this.rawDescription;
				String newDesc = this.rawDescription.substring(0, indexOfTribText) + this.rawDescription.substring(indexOfTribText + modIndex);
				this.rawDescription = newDesc;
				if (DuelistMod.debug) { System.out.println(this.originalName + " made a string: " + newDesc + " this.tributes : " + this.tributes); }
			}
		}
		else { this.baseTributes = this.tributes = set; }
		this.isTributesModified = true; 
		this.initializeDescription();
	}
	
	public static void addMonsterToHandModTributes(String name, int add, boolean combat)
	{
    	DuelistCard newCopy = (DuelistCard) DuelistCard.newCopyOfMonster(name).makeStatEquivalentCopy();
    	if (newCopy.tributes > 0)
    	{
    		AbstractDungeon.actionManager.addToTop(new ModifyTributeAction(newCopy, add, combat));
    		AbstractDungeon.effectList.add(new ShowCardAndAddToHandEffect(newCopy));
    	}
	}
	
	public static void addMonsterToDeckModTributes(String name, int add, boolean combat)
	{
    	DuelistCard newCopy = (DuelistCard) DuelistCard.newCopyOfMonster(name).makeStatEquivalentCopy();
    	if (newCopy.tributes > 0)
    	{
    		AbstractDungeon.actionManager.addToTop(new ModifyTributeAction(newCopy, add, combat));
    		AbstractDungeon.effectList.add(new ShowCardAndAddToDrawPileEffect(newCopy, true, false));
    	}
	}
	
	public static void obtainMonsterModTributes(String name, int add)
	{
    	DuelistCard newCopy = (DuelistCard) DuelistCard.newCopyOfMonster(name).makeStatEquivalentCopy();
    	if (newCopy.tributes > 0)
    	{
    		AbstractDungeon.actionManager.addToTop(new ModifyTributePermAction(newCopy, add));
    		AbstractDungeon.effectList.add(new ShowCardAndObtainEffect(newCopy, (float) (Settings.WIDTH / 2), (float) (Settings.HEIGHT / 2)));
    	}
	}
	// =============== /TRIBUTE MODIFICATION FUNCTIONS/ =======================================================================================================================================================
	
	
	
	// =============== CARD MODAL FUNCTIONS =========================================================================================================================================================

	// =============== /CARD MODAL FUNCTIONS/ =======================================================================================================================================================

	
	// =============== ORB MODAL FUNCTIONS =========================================================================================================================================================

	// =============== /ORB MODAL FUNCTIONS/ =======================================================================================================================================================
	
	
	
	// =============== ORB FUNCTIONS =========================================================================================================================================================
	public static void checkSplash()
	{
		if (AbstractDungeon.player.hasOrb())
		{
			for (AbstractOrb o : AbstractDungeon.player.orbs)
			{
				if (o instanceof Splash)
				{
					Splash ref = (Splash)o;
					ref.triggerPassiveEffect();
				}
			}
		}
	}
	
	public static void channel(AbstractOrb orb)
	{
		AbstractDungeon.actionManager.addToTop(new ChannelAction(orb));
	}

	public static void channelBottom(AbstractOrb orb)
	{
		AbstractDungeon.actionManager.addToBottom(new ChannelAction(orb));
	}
	
	public static void zombieLavaChannel()
	{
		AbstractOrb lava = new Lava(AbstractDungeon.cardRandomRng.random(1, 10));
		channel(lava);
	}
	
	public static void channelWater()
	{
		if (Loader.isModLoaded("conspire") && Loader.isModLoaded("ReplayTheSpireMod")){ RandomOrbHelperDualMod.channelWater(); }
		else if (Loader.isModLoaded("conspire") && !Loader.isModLoaded("ReplayTheSpireMod")){ RandomOrbHelperCon.channelWater(); }
		else if (Loader.isModLoaded("ReplayTheSpireMod") && !Loader.isModLoaded("conspire")) { RandomOrbHelperRep.channelWater(); }
		else { RandomOrbHelper.channelWater(); }
	}

	public static void channelRandom()
	{
		if (Loader.isModLoaded("conspire") && Loader.isModLoaded("ReplayTheSpireMod")){ RandomOrbHelperDualMod.channelRandomOrb(); }
		else if (Loader.isModLoaded("conspire") && !Loader.isModLoaded("ReplayTheSpireMod")){ RandomOrbHelperCon.channelRandomOrb(); }
		else if (Loader.isModLoaded("ReplayTheSpireMod") && !Loader.isModLoaded("conspire")) { RandomOrbHelperRep.channelRandomOrb(); }
		else { RandomOrbHelper.channelRandomOrb(); }
	}
	
	public static void channelRandomNoGlassOrGate()
	{
		if (Loader.isModLoaded("conspire") && Loader.isModLoaded("ReplayTheSpireMod")){ RandomOrbHelperDualMod.channelRandomOrbNoGlassOrGate(); }
		else if (Loader.isModLoaded("conspire") && !Loader.isModLoaded("ReplayTheSpireMod")){ RandomOrbHelperCon.channelRandomOrbNoGlassOrGate(); }
		else if (Loader.isModLoaded("ReplayTheSpireMod") && !Loader.isModLoaded("conspire")) { RandomOrbHelperRep.channelRandomOrbNoGlassOrGate(); }
		else { RandomOrbHelper.channelRandomOrbNoGlassOrGate(); }
	}
	
	public static ArrayList<AbstractOrb> returnRandomOrbList()
	{
		ArrayList<AbstractOrb> returnOrbs = new ArrayList<AbstractOrb>();
		if (Loader.isModLoaded("conspire") && Loader.isModLoaded("ReplayTheSpireMod")){ returnOrbs.addAll(RandomOrbHelperDualMod.returnOrbList()); }
		else if (Loader.isModLoaded("conspire") && !Loader.isModLoaded("ReplayTheSpireMod")){ returnOrbs.addAll(RandomOrbHelperCon.returnOrbList()); }
		else if (Loader.isModLoaded("ReplayTheSpireMod") && !Loader.isModLoaded("conspire")) { returnOrbs.addAll(RandomOrbHelperRep.returnOrbList()); }
		else { returnOrbs.addAll(RandomOrbHelper.returnOrbList()); }
		return returnOrbs;
	}
	
	public static void resetInvertStringMap()
	{
		if (Loader.isModLoaded("conspire") && Loader.isModLoaded("ReplayTheSpireMod")){ RandomOrbHelperDualMod.resetOrbStringMap(); }
		else if (Loader.isModLoaded("conspire") && !Loader.isModLoaded("ReplayTheSpireMod")){ RandomOrbHelperCon.resetOrbStringMap(); }
		else if (Loader.isModLoaded("ReplayTheSpireMod") && !Loader.isModLoaded("conspire")) { RandomOrbHelperRep.resetOrbStringMap(); }
		else { RandomOrbHelper.resetOrbStringMap(); }
	}

	public static void evokeAll()
	{
		AbstractDungeon.actionManager.addToTop(new EvokeAllOrbsAction());
	}
	
	public static void evoke(int amount)
	{
		AbstractDungeon.actionManager.addToTop(new EvokeOrbAction(amount));
	}
	
	public static void evokeMult(int amount)
	{
		for (int i = 0; i < amount; i++)
		{
			AbstractDungeon.player.evokeWithoutLosingOrb();
		}
		AbstractDungeon.actionManager.addToTop(new RemoveNextOrbAction());
	}

	
	public static void evokeMult(int amount, AbstractOrb orb)
	{
		for (int i = 0; i < amount; i++)
		{
			orb.onEvoke();
		}
	}

	public static void invertAll(int amount)
	{
		System.out.println("(A) orb slots::::: " + AbstractDungeon.player.maxOrbs);
		if (AbstractDungeon.player.orbs.size() > 0 && AbstractDungeon.player.hasOrb())
		{
			int numberOfInverts;
			if (AbstractDungeon.player.hasRelic(InversionRelic.ID)) { amount++; }
			if (AbstractDungeon.player.hasRelic(InversionEvokeRelic.ID)) { numberOfInverts = 2; }
			else { numberOfInverts = 1; }		
			resetInvertStringMap();
			for (int i = 0; i < numberOfInverts; i++)
			{				
				int invertedOrbs = 0;
				//ArrayList<AbstractOrb> baseOrbs = new ArrayList<AbstractOrb>();
				ArrayList<String> invertOrbNames = new ArrayList<String>();
				int loopCount = AbstractDungeon.player.filledOrbCount();
				for (int j = 0; j < loopCount; j++)
				{
					//baseOrbs.add(AbstractDungeon.player.orbs.get(j));
					invertOrbNames.add(AbstractDungeon.player.orbs.get(j).name);
					evokeMult(amount, AbstractDungeon.player.orbs.get(j));
					//evokeMult(amount);
					System.out.println("Orb we added to baseOrbs: " + AbstractDungeon.player.orbs.get(j).makeCopy());
					invertedOrbs++;
				}
				AbstractDungeon.actionManager.addToTop(new RemoveAllOrbsAction());
				System.out.println("(B) orb slots::::: " + AbstractDungeon.player.maxOrbs);
				for (int j = 0; j < invertedOrbs; j++)
				{
					if (DuelistMod.invertableOrbNames.contains(invertOrbNames.get(j)))
					{
						AbstractDungeon.actionManager.addToBottom(new ChannelAction(DuelistMod.invertStringMap.get(invertOrbNames.get(j)).makeCopy()));
						if (DuelistMod.debug) { System.out.println("Orb we inverted to channel: " + invertOrbNames.get(j)); }
					}
					else
					{
						if (DuelistMod.debug) { System.out.println("Skipped inverting " + invertOrbNames.get(j) + " because we did not find an entry in the allowed invertable orbs names list"); }
					}
				}
				
			}
		}
	}
	
	public static void invertAllWithoutRemoving(int amount)
	{
		if (AbstractDungeon.player.orbs.size() > 0 && AbstractDungeon.player.hasOrb())
		{
			int numberOfInverts;
			if (AbstractDungeon.player.hasRelic(InversionRelic.ID)) { amount++; }
			if (AbstractDungeon.player.hasRelic(InversionEvokeRelic.ID)) { numberOfInverts = 2; }
			else { numberOfInverts = 1; }			
			for (int i = 0; i < numberOfInverts; i++)
			{
				resetInvertStringMap();
				int invertedOrbs = 0;
				ArrayList<String> baseOrbs = new ArrayList<String>();
				int loopCount = AbstractDungeon.player.filledOrbCount();
				for (int j = 0; j < loopCount; j++)
				{
					evokeMult(amount, AbstractDungeon.player.orbs.get(j));
					invertedOrbs++;
					baseOrbs.add(AbstractDungeon.player.orbs.get(j).name);
				}
		
				for (int j = 0; j < invertedOrbs; j++)
				{
					if (DuelistMod.invertableOrbNames.contains(baseOrbs.get(j)))
					{
						AbstractDungeon.actionManager.addToBottom(new ChannelAction(DuelistMod.invertStringMap.get(baseOrbs.get(j)).makeCopy()));
						if (DuelistMod.debug) { System.out.println("Orb we inverted to channel: " + baseOrbs.get(j)); }
					}
					else
					{
						if (DuelistMod.debug) { System.out.println("Skipped inverting " + baseOrbs.get(j) + " because we did not find an entry in the allowed invertable orbs names list"); }
					}
				}
			}
		}
	}
	
	public static void invertAllMult(int amount, int numberOfInverts)
	{
		for (int i = 0; i < numberOfInverts; i++) {	invertAll(amount); }
	}
	
	public static void invertAllWithoutRemovingMult(int amount, int numberOfInverts)
	{
		for (int i = 0; i < numberOfInverts; i++) { invertAllWithoutRemoving(amount); }
	}

	public static void invert(int amount)
	{
		if (AbstractDungeon.player.orbs.size() > 0 && AbstractDungeon.player.hasOrb())
		{
			int numberOfInverts;
			if (AbstractDungeon.player.hasRelic(InversionRelic.ID)) { amount++; }
			if (AbstractDungeon.player.hasRelic(InversionEvokeRelic.ID)) { numberOfInverts = 2; }
			else { numberOfInverts = 1; }		
			resetInvertStringMap();
			AbstractOrb o = AbstractDungeon.player.orbs.get(0);
			String orbToInvert = o.name;
			evokeMult(amount * numberOfInverts, AbstractDungeon.player.orbs.get(0));
			AbstractDungeon.actionManager.addToTop(new RemoveNextOrbAction());
			for (int i = 0; i < numberOfInverts; i++)
			{
				if (DuelistMod.invertableOrbNames.contains(orbToInvert))
				{
					AbstractDungeon.actionManager.addToBottom(new ChannelAction(DuelistMod.invertStringMap.get(orbToInvert).makeCopy()));
					if (DuelistMod.debug) { System.out.println("Orb we inverted to channel: " + orbToInvert); }
				}
				else
				{
					if (DuelistMod.debug) { System.out.println("Skipped inverting " + orbToInvert + " because we did not find an entry in the allowed invertable orbs names list"); }
				}
			}
		}
	}
	
	public static void invertIceQueen(int amount, int frosts)
	{
		if (AbstractDungeon.player.orbs.size() > 0 && AbstractDungeon.player.hasOrb())
		{
			int numberOfInverts;
			if (AbstractDungeon.player.hasRelic(InversionRelic.ID)) { amount++; }
			if (AbstractDungeon.player.hasRelic(InversionEvokeRelic.ID)) { numberOfInverts = 2; }
			else { numberOfInverts = 1; }		
			resetInvertStringMap();
			AbstractOrb o = AbstractDungeon.player.orbs.get(0);
			String orbToInvert = o.name;
			evokeMult(amount * numberOfInverts, AbstractDungeon.player.orbs.get(0));
			AbstractDungeon.actionManager.addToTop(new RemoveNextOrbAction());
			for (int i = 0; i < frosts; i++)
			{
				AbstractDungeon.actionManager.addToBottom(new ChannelAction(new Frost()));
			}
			for (int i = 0; i < numberOfInverts; i++)
			{
				if (DuelistMod.invertableOrbNames.contains(orbToInvert))
				{
					AbstractDungeon.actionManager.addToBottom(new ChannelAction(DuelistMod.invertStringMap.get(orbToInvert).makeCopy()));
					if (DuelistMod.debug) { System.out.println("Orb we inverted to channel: " + orbToInvert); }
				}
				else
				{
					if (DuelistMod.debug) { System.out.println("Skipped inverting " + orbToInvert + " because we did not find an entry in the allowed invertable orbs names list"); }
				}
			}
		}
	}

	public static void invertMult(int amount, int numberOfInverts)
	{
		for (int i = 0; i < numberOfInverts; i++) { invert(amount); }
	}
	
	public static void removeOrbs(int amount)
	{
		for (int i = 0; i < amount; i++)
		{
			AbstractDungeon.actionManager.addToTop(new RemoveNextOrbAction());
		}
	}
	// =============== /ORB FUNCTIONS/ =======================================================================================================================================================
	
	
	
	// =============== RANDOM CARD FUNCTIONS =========================================================================================================================================================
	public static DuelistCard returnRandomTributeFromSets(CardTags tag, CardTags tagB, boolean seeded, boolean fromCardPoolOnly, boolean checkBothTags)
	{
		ArrayList<DuelistCard> tribCards = new ArrayList<DuelistCard>();
		if (!fromCardPoolOnly)
		{
			for (DuelistCard c : DuelistMod.myCards)
			{
				if (c.tributes > 0 && c.hasTag(tag) && c.hasTag(tagB) && !c.hasTag(Tags.NEVER_GENERATE))
				{
					tribCards.add((DuelistCard) c.makeStatEquivalentCopy());
				}
			}
		}
		else
		{
			if (AbstractDungeon.player.chosenClass.equals(TheDuelistEnum.THE_DUELIST))
			{
				for (AbstractCard c : DuelistMod.coloredCards)
				{
					DuelistCard dC = (DuelistCard)c;
					if (checkBothTags)
					{
						if (dC.tributes > 0 && c.hasTag(tag) && c.hasTag(tagB) && !c.hasTag(Tags.NEVER_GENERATE))
						{
							tribCards.add((DuelistCard) c.makeStatEquivalentCopy());
						}
					}
					else
					{
						if ((dC.tributes > 0 && (c.hasTag(tag) || c.hasTag(tagB))) && !c.hasTag(Tags.NEVER_GENERATE))
						{
							tribCards.add((DuelistCard) c.makeStatEquivalentCopy());
						}
					}
				}
			}
			else
			{
				returnRandomTributeFromSets(tag, tagB, seeded, false, checkBothTags);
			}
		}
		
		if (seeded) { return tribCards.get(AbstractDungeon.cardRandomRng.random(tribCards.size() - 1)); }
		else { return tribCards.get(ThreadLocalRandom.current().nextInt(0, tribCards.size())); }
	}
	
	public static DuelistCard returnRandomTributeFromSet(CardTags tag, boolean seeded, boolean fromCardPoolOnly)
	{
		ArrayList<DuelistCard> tribCards = new ArrayList<DuelistCard>();
		if (!fromCardPoolOnly)
		{
			for (DuelistCard c : DuelistMod.myCards)
			{
				if (c.tributes > 0 && c.hasTag(tag) && !c.hasTag(Tags.NEVER_GENERATE))
				{
					tribCards.add((DuelistCard) c.makeStatEquivalentCopy());
				}
			}
		}
		else
		{
			for (AbstractCard c : DuelistMod.coloredCards)
			{
				DuelistCard dC = (DuelistCard)c;
				if (dC.tributes > 0 && c.hasTag(tag) && !c.hasTag(Tags.NEVER_GENERATE))
				{
					tribCards.add((DuelistCard) c.makeStatEquivalentCopy());
				}
				
			}
		}
		
		if (seeded) { return tribCards.get(AbstractDungeon.cardRandomRng.random(tribCards.size() - 1)); }
		else { return tribCards.get(ThreadLocalRandom.current().nextInt(0, tribCards.size())); }
	}
	
	public static DuelistCard returnRandomTribute(boolean seeded, boolean fromCardPoolOnly)
	{
		ArrayList<DuelistCard> tribCards = new ArrayList<DuelistCard>();
		if (!fromCardPoolOnly)
		{
			for (DuelistCard c : DuelistMod.myCards)
			{
				if (c.tributes > 0 && !c.hasTag(Tags.NEVER_GENERATE))
				{
					tribCards.add((DuelistCard) c.makeStatEquivalentCopy());
				}
			}
		}
		else
		{
			for (AbstractCard c : DuelistMod.coloredCards)
			{
				DuelistCard dC = (DuelistCard)c;
				if (dC.tributes > 0 && !c.hasTag(Tags.NEVER_GENERATE))
				{
					tribCards.add((DuelistCard) c.makeStatEquivalentCopy());
				}
				
			}
		}
		if (seeded) { return tribCards.get(AbstractDungeon.cardRandomRng.random(tribCards.size() - 1)); }
		else { return tribCards.get(ThreadLocalRandom.current().nextInt(0, tribCards.size())); }
	}
	
	public static DuelistCard returnRandomFromArray(ArrayList<DuelistCard> tributeList)
	{
		return tributeList.get(AbstractDungeon.cardRandomRng.random(tributeList.size() - 1));
	}

	public static AbstractCard returnRandomFromArrayAbstract(ArrayList<AbstractCard> tributeList)
	{
		return tributeList.get(AbstractDungeon.cardRandomRng.random(tributeList.size() - 1));
	}
	
	public static AbstractCard returnTrulyRandomFromSetUnseeded(CardTags setToFindFrom) 
	{
		ArrayList<AbstractCard> dragonGroup = new ArrayList<>();
		for (DuelistCard card : DuelistMod.myCards)
		{
			if (card.hasTag(setToFindFrom) && !card.hasTag(Tags.TOKEN) && !card.hasTag(Tags.NEVER_GENERATE)) 
			{
				dragonGroup.add(card.makeCopy());
			}
		}
		if (dragonGroup.size() > 0)
		{
			AbstractCard returnable = dragonGroup.get(ThreadLocalRandom.current().nextInt(0, dragonGroup.size()));
			while (returnable.hasTag(Tags.NEVER_GENERATE)) { returnable = dragonGroup.get(ThreadLocalRandom.current().nextInt(0, dragonGroup.size())); }
			return returnable;
		}
		else
		{
			return new Token();
		}
	}

	public static AbstractCard returnTrulyRandomFromSet(CardTags setToFindFrom) 
	{
		ArrayList<AbstractCard> dragonGroup = new ArrayList<>();
		for (DuelistCard card : DuelistMod.myCards)
		{
			if (card.hasTag(setToFindFrom) && !card.hasTag(Tags.TOKEN) && !card.hasTag(Tags.NEVER_GENERATE)) 
			{
				dragonGroup.add(card.makeCopy());
			}
		}
		if (dragonGroup.size() > 0)
		{
			AbstractCard returnable = dragonGroup.get(ThreadLocalRandom.current().nextInt(0, dragonGroup.size()));
			while (returnable.hasTag(Tags.NEVER_GENERATE)) { returnable = dragonGroup.get(ThreadLocalRandom.current().nextInt(0, dragonGroup.size())); }
			return returnable;
		}
		else
		{
			return new Token();
		}
	}
	
	public static AbstractCard returnTrulyRandomInCombatFromSet(CardTags setToFindFrom) 
	{
		if (AbstractDungeon.player.chosenClass.equals(TheDuelistEnum.THE_DUELIST))
		{
			AbstractCard c = DuelistMod.coloredCards.get(AbstractDungeon.cardRandomRng.random(DuelistMod.coloredCards.size() - 1));
			while (!c.hasTag(setToFindFrom) || c.hasTag(Tags.NEVER_GENERATE)) { c = DuelistMod.coloredCards.get(AbstractDungeon.cardRandomRng.random(DuelistMod.coloredCards.size() - 1)); }
			return c;
		}
		else
		{
			return returnTrulyRandomFromSet(setToFindFrom);
		}
	}

	public static AbstractCard returnTrulyRandomDuelistCard() 
	{
		ArrayList<AbstractCard> dragonGroup = new ArrayList<>();
		for (DuelistCard card : DuelistMod.myCards)
		{
			if (card instanceof DuelistCard && !card.hasTag(Tags.TOKEN) && !card.hasTag(Tags.NEVER_GENERATE)) 
			{
				dragonGroup.add(card.makeCopy());
			}
		}
		if (dragonGroup.size() > 0)
		{
			AbstractCard returnable = dragonGroup.get(ThreadLocalRandom.current().nextInt(0, dragonGroup.size()));
			while (returnable.hasTag(Tags.NEVER_GENERATE)) { returnable = dragonGroup.get(ThreadLocalRandom.current().nextInt(0, dragonGroup.size())); }
			return returnable;
		}
		else
		{
			return new Token();
		}
	}
	
	public static AbstractCard returnTrulyRandomDuelistCardInCombat() 
	{
		if (AbstractDungeon.player.chosenClass.equals(TheDuelistEnum.THE_DUELIST))
		{
			AbstractCard c = DuelistMod.coloredCards.get(AbstractDungeon.cardRandomRng.random(DuelistMod.coloredCards.size() - 1));
			while (!(c instanceof DuelistCard) || c.hasTag(Tags.NEVER_GENERATE)) { c = DuelistMod.coloredCards.get(AbstractDungeon.cardRandomRng.random(DuelistMod.coloredCards.size() - 1)); }
			return c;
		}
		else
		{
			return returnTrulyRandomDuelistCard();
		}
	}

	public static AbstractCard returnTrulyRandomFromSets(CardTags setToFindFrom, CardTags anotherSetToFindFrom) 
	{
		ArrayList<AbstractCard> dragonGroup = new ArrayList<>();
		for (DuelistCard card : DuelistMod.myCards)
		{
			if (card.hasTag(setToFindFrom) && card.hasTag(anotherSetToFindFrom) && !card.hasTag(Tags.TOKEN) && !card.hasTag(Tags.NEVER_GENERATE)) 
			{
				dragonGroup.add(card.makeCopy());
			}
		}
		if (dragonGroup.size() > 0)
		{
			AbstractCard returnable = dragonGroup.get(ThreadLocalRandom.current().nextInt(0, dragonGroup.size()));
			while (returnable.hasTag(Tags.NEVER_GENERATE)) { returnable = dragonGroup.get(ThreadLocalRandom.current().nextInt(0, dragonGroup.size())); }
			return returnable;
		}
		else
		{
			return new Token();
		}
	}

	public static AbstractCard returnTrulyRandomFromEitherSet(CardTags setToFindFrom, CardTags anotherSetToFindFrom) 
	{
		ArrayList<AbstractCard> dragonGroup = new ArrayList<>();
		for (DuelistCard card : DuelistMod.myCards)
		{
			if ((card.hasTag(setToFindFrom) || card.hasTag(anotherSetToFindFrom)) && !card.hasTag(Tags.TOKEN) && !card.hasTag(Tags.NEVER_GENERATE)) 
			{
				dragonGroup.add(card.makeCopy());
			}
		}
		if (dragonGroup.size() > 0)
		{
			AbstractCard returnable = dragonGroup.get(ThreadLocalRandom.current().nextInt(0, dragonGroup.size()));
			while (returnable.hasTag(Tags.NEVER_GENERATE)) { returnable = dragonGroup.get(ThreadLocalRandom.current().nextInt(0, dragonGroup.size())); }
			return returnable;
		}
		else
		{
			return new Token();
		}
	}

	public static AbstractCard returnTrulyRandomFromOnlyFirstSet(CardTags setToFindFrom, CardTags excludeSet) 
	{
		ArrayList<AbstractCard> dragonGroup = new ArrayList<>();
		for (DuelistCard card : DuelistMod.myCards)
		{
			if (card.hasTag(setToFindFrom) && !card.hasTag(excludeSet) && !card.hasTag(Tags.TOKEN) && !card.hasTag(Tags.NEVER_GENERATE)) 
			{
				dragonGroup.add(card.makeCopy());
			}
		}
		if (dragonGroup.size() > 0)
		{
			AbstractCard returnable = dragonGroup.get(ThreadLocalRandom.current().nextInt(0, dragonGroup.size()));
			while (returnable.hasTag(Tags.NEVER_GENERATE)) { returnable = dragonGroup.get(ThreadLocalRandom.current().nextInt(0, dragonGroup.size())); }
			return returnable;
		}
		else
		{
			return new Token();
		}
	}

	public static DuelistCard newCopyOfMonster(String name)
	{
		DuelistCard find = DuelistMod.summonMap.get(name);
		if (find != null) { return (DuelistCard) find.makeCopy(); }
		else { return new Token(); }
	}

	// =============== /RANDOM CARD FUNCTIONS/ =======================================================================================================================================================

	// =============== TYPE CARD FUNCTIONS =========================================================================================================================================================
	public String generateDynamicTypeCardDesc(int magic, CardTags tag)
	{
		String res = "";
		String tagString = tag.toString().toLowerCase();
		String temp = tagString.substring(0, 1).toUpperCase();
		tagString = temp + tagString.substring(1);
		
		if (this instanceof ShardGreed)
		{
			if (magic < 2) { res = Strings.configGreedShardA + tagString + Strings.configGreedShardB; }
			else { res = Strings.configGreedShardC + magic + " " + tagString + Strings.configWingedTextB; }
			
		}
		
		if (this instanceof RainbowJar)
		{
			res = Strings.configRainbowJarA + tagString + Strings.configRainbowJarB;
		}
		
		if (this instanceof WingedKuriboh9 || this instanceof WingedKuriboh10)
		{
			if (magic < 2) { res = Strings.configWingedTextA + magic + " " + tagString + Strings.configGreedShardB; }
			else { res = Strings.configWingedTextA + magic + " " + tagString + Strings.configWingedTextB; }
		}
		
		if (this instanceof YamiForm)
		{
			 res = Strings.configYamiFormA + tagString + Strings.configYamiFormB;
		}
		
		if (this instanceof RainbowGravity)
		{
			res = Strings.configRainbow + tagString + Strings.configRainbowB;
		}
		
		if (this instanceof TributeToken)
		{
			res = "Tribute a monster with a monster of " + tagString + " type.";
		}
		
		if (this instanceof SummonToken)
		{
			res = "Summon a random " + tagString + " monster.";
		}
		
		return res;
	}
	
	public static String generateDynamicTypeCardDesc(int magic, CardTags tag, DuelistCard callingCard, int randomTypes)
	{
		String res = "";
		String tagString = tag.toString().toLowerCase();
		String temp = tagString.substring(0, 1).toUpperCase();
		tagString = temp + tagString.substring(1);
		
		if (callingCard instanceof ShardGreed)
		{
			if (magic < 2) { res = Strings.configGreedShardA + tagString + Strings.configGreedShardB; }
			else { res = Strings.configGreedShardC + magic + " " + tagString + Strings.configWingedTextB; }
		}
		
		if (callingCard instanceof RainbowJar)
		{
			res = Strings.configRainbowJarA + tagString + Strings.configRainbowJarB;
		}
		
		if (callingCard instanceof WingedKuriboh9 || callingCard instanceof WingedKuriboh10)
		{
			if (magic < 2) { res = Strings.configWingedTextA + magic + " " + tagString + Strings.configGreedShardB; }
			else { res = Strings.configWingedTextA + magic + " " + tagString + Strings.configWingedTextB; }
		}
		
		if (callingCard instanceof YamiForm)
		{
			 res = Strings.configYamiFormA + tagString + Strings.configYamiFormB;
		}
		
		return res;
	}
	
	public ArrayList<DuelistCard> generateTypeCards(int magic)
	{
		return generateTypeCards(magic, false);
	}
	
	public ArrayList<DuelistCard> generateTypeCards(int magic, boolean customDesc)
	{
		ArrayList<DuelistCard> typeCards = new ArrayList<DuelistCard>();
		for (CardTags t : DuelistMod.monsterTypes)
		{
			if (customDesc) { typeCards.add(new DynamicTypeCard(DuelistMod.typeCardMap_ID.get(t), DuelistMod.typeCardMap_NAME.get(t), DuelistMod.typeCardMap_IMG.get(t), generateDynamicTypeCardDesc(magic, t), t, this, magic)); }
			else { typeCards.add(new DynamicTypeCard(DuelistMod.typeCardMap_ID.get(t), DuelistMod.typeCardMap_NAME.get(t), DuelistMod.typeCardMap_IMG.get(t), DuelistMod.typeCardMap_DESC.get(t), t, this, magic)); }
			
		}
		return typeCards;
	}
	
	public static ArrayList<DuelistCard> generateTypeCards(int magic, boolean customDesc, DuelistCard callingCard)
	{
		ArrayList<DuelistCard> typeCards = new ArrayList<DuelistCard>();
		for (CardTags t : DuelistMod.monsterTypes)
		{
			if (customDesc) { typeCards.add(new DynamicTypeCard(DuelistMod.typeCardMap_ID.get(t), DuelistMod.typeCardMap_NAME.get(t), DuelistMod.typeCardMap_IMG.get(t), generateDynamicTypeCardDesc(magic, t, callingCard, DuelistMod.monsterTypes.size()), t, callingCard, magic)); }
			else { typeCards.add(new DynamicTypeCard(DuelistMod.typeCardMap_ID.get(t), DuelistMod.typeCardMap_NAME.get(t), DuelistMod.typeCardMap_IMG.get(t), DuelistMod.typeCardMap_DESC.get(t), t, callingCard, magic)); }
			
		}
		return typeCards;
	}
	
	public static ArrayList<DuelistCard> generateTypeCards(int magic, boolean customDesc, DuelistCard callingCard, int numberOfRandomTypes, boolean seeded)
	{
		ArrayList<DuelistCard> typeCards = new ArrayList<DuelistCard>();
		ArrayList<CardTags> types = new ArrayList<CardTags>();
		if (numberOfRandomTypes > DuelistMod.monsterTypes.size()) { numberOfRandomTypes = DuelistMod.monsterTypes.size(); }
		if (numberOfRandomTypes == DuelistMod.monsterTypes.size()) { return generateTypeCards(magic, customDesc, callingCard); }
		else 
		{
			types.addAll(DuelistMod.monsterTypes);
			for (int i = 0; i < DuelistMod.monsterTypes.size() - numberOfRandomTypes; i++)
			{
				if (seeded)
				{
					types.remove(AbstractDungeon.cardRandomRng.random(types.size() - 1));
				}
				else
				{
					types.remove(ThreadLocalRandom.current().nextInt(0, types.size()));
				}
			}
		}
		
		for (CardTags t : types)
		{
			if (customDesc) { typeCards.add(new DynamicTypeCard(DuelistMod.typeCardMap_ID.get(t), DuelistMod.typeCardMap_NAME.get(t), DuelistMod.typeCardMap_IMG.get(t), generateDynamicTypeCardDesc(magic, t, callingCard, numberOfRandomTypes), t, callingCard, magic)); }
			else { typeCards.add(new DynamicTypeCard(DuelistMod.typeCardMap_ID.get(t), DuelistMod.typeCardMap_NAME.get(t), DuelistMod.typeCardMap_IMG.get(t), DuelistMod.typeCardMap_DESC.get(t), t, callingCard, magic)); }
		}
		return typeCards;
	}
	// =============== /TYPE CARD FUNCTIONS/ =========================================================================================================================================================

	// =============== DEBUG PRINT FUNCTIONS =========================================================================================================================================================
	public static void printSetDetails(CardTags[] setsToFindFrom) 
	{
		// Map that holds set info for printing at end
		Map<CardTags, Integer> tagMap = new HashMap<CardTags, Integer>();
		Map<CardTags, ArrayList<DuelistCard>> tagSet = new HashMap<CardTags, ArrayList<DuelistCard>>();
		for (CardTags t : setsToFindFrom) { tagMap.put(t, 0); tagSet.put(t, new ArrayList<DuelistCard>()); }

		// Check all cards in library
		for (DuelistCard potentialMatchCard : DuelistMod.myCards)
		{
			// See if check card is missing any match tags
			for (CardTags t : setsToFindFrom) 
			{
				if (potentialMatchCard.hasTag(t))
				{
					tagMap.put(t, tagMap.get(t) + 1);
					tagSet.get(t).add(potentialMatchCard);
				}

			}
		}

		Set<Entry<CardTags, Integer>> set = tagMap.entrySet();
		for (Entry<CardTags, Integer> t : set)
		{
			System.out.println("theDuelist:DuelistCard:printSetDetails() --- > START OF SET: " + t.getKey() + " --- " + t.getValue());
			for (DuelistCard c : tagSet.get(t.getKey()))
			{
				System.out.println(c.name);
			}
			System.out.println("theDuelist:DuelistCard:printSetDetails() --- > END OF SET: " + t.getKey());
		} 

	}
	// =============== /DEBUG PRINT FUNCTIONS/ =======================================================================================================================================================
}