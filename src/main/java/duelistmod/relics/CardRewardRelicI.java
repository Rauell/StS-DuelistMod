package duelistmod.relics;

import java.util.*;
import java.util.concurrent.ThreadLocalRandom;

import com.badlogic.gdx.graphics.Texture;
import com.evacipated.cardcrawl.modthespire.lib.SpireConfig;
import com.megacrit.cardcrawl.cards.*;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.helpers.PowerTip;
import com.megacrit.cardcrawl.relics.AbstractRelic.*;
import com.megacrit.cardcrawl.unlock.UnlockTracker;
import com.megacrit.cardcrawl.vfx.cardManip.ShowCardAndObtainEffect;

import duelistmod.DuelistMod;
import duelistmod.abstracts.DuelistRelic;
import duelistmod.helpers.BaseGameHelper;
import duelistmod.ui.DuelistCardSelectScreen;

public class CardRewardRelicI extends DuelistRelic
{
	// FIELDS
	public static final String ID = DuelistMod.makeID("CardRewardRelicI");
    public static final String IMG = DuelistMod.makeRelicPath("BaseGameRelic.png");
    public static final String OUTLINE = DuelistMod.makeRelicOutlinePath("BookStone_Outline.png");
    public boolean cardSelected = false;
	private DuelistCardSelectScreen dcss;
    // /FIELDS
    
    private int colorIndex = -1;

    public CardRewardRelicI() 
    { 
	    super(ID, new Texture(IMG), new Texture(OUTLINE), RelicTier.SHOP, LandingSound.MAGICAL); 
	    this.colorIndex = getRandomColors();  
	    this.setCounter(colorIndex); 
	    this.dcss = new DuelistCardSelectScreen(true);
	    setDescription(); 
    }
    @Override public String getUpdatedDescription() 
    {
    	if (this.counter > -1) { return this.DESCRIPTIONS[this.counter]; }
    	else { return this.DESCRIPTIONS[19]; }
    }

    @Override
	public boolean canSpawn()
	{
		if (DuelistMod.hasCardRewardRelic) { return false; }
		else { return true; }
	}
    
    public int getRandomColors()
    {
    	return ThreadLocalRandom.current().nextInt(0, 19);
    }
    
    public void setDescription()
	{
		description = getUpdatedDescription();
        tips.clear();
        tips.add(new PowerTip(name, description));
        initializeTips();
	}
    
    private String getColorName()
    {
    	switch (this.colorIndex)
    	{
	    	case 0:
	    		return "Ironclad";
	    	case 1:
	    		return "Defect";
	    	case 2:
	    		return "Silent";
	    	case 3:
	    		return "Watcher";
	    	case 4:
	    		return "Ironclad or Defect";
	    	case 5:
	    		return "Ironclad or Silent";
	    	case 6:
	    		return "Ironclad or Watcher";
	    	case 7:
	    		return "Silent or Defect";
	    	case 8:
	    		return "Watcher or Defect";
	    	case 9:
	    		return "Silent or Watcher";
	    	case 10:
	    		return "Colorless";
	    	case 11:
	    		return "Ironclad or Colorless";
	    	case 12:
	    		return "Silent or Colorless";
	    	case 13:
	    		return "Defect or Colorless";
	    	case 14:
	    		return "Watcher or Colorless";
	    	case 15:
	    		return "Ironclad";
	    	case 16:
	    		return "Defect";
	    	case 17:
	    		return "Silent";
	    	case 18:
	    		return "Watcher";
	    	default:
	    		return "Ironclad or Silent or Defect or Watcher or Colorless";
    	}
    }
    
    private ArrayList<AbstractCard> getColorCards()
    {
    	ArrayList<AbstractCard> all = new ArrayList<AbstractCard>();
    	if (this.colorIndex < 0) { this.colorIndex = getRandomColors(); setDescription(); }
    	switch (this.colorIndex)
    	{
	    	case 0:
	    		return BaseGameHelper.getAllIroncladCards();
	    	case 1:
	    		return BaseGameHelper.getAllDefectCards();
	    	case 2:
	    		return BaseGameHelper.getAllSilentCards();
	    	case 3:
	    		return BaseGameHelper.getAllWatcherCards();
	    	case 4:
	    		all.clear();
	    		all.addAll(BaseGameHelper.getAllIroncladCards());
	    		all.addAll(BaseGameHelper.getAllDefectCards());
	    		return all;
	    	case 5:
	    		all.clear();
	    		all.addAll(BaseGameHelper.getAllIroncladCards());
	    		all.addAll(BaseGameHelper.getAllSilentCards());
	    		return all;
	    	case 6:
	    		all.clear();
	    		all.addAll(BaseGameHelper.getAllIroncladCards());
	    		all.addAll(BaseGameHelper.getAllWatcherCards());
	    		return all;
	    	case 7:
	    		all.clear();
	    		all.addAll(BaseGameHelper.getAllSilentCards());
	    		all.addAll(BaseGameHelper.getAllDefectCards());
	    		return all;
	    	case 8:
	    		all.clear();
	    		all.addAll(BaseGameHelper.getAllWatcherCards());
	    		all.addAll(BaseGameHelper.getAllDefectCards());
	    		return all;
	    	case 9:
	    		all.clear();
	    		all.addAll(BaseGameHelper.getAllSilentCards());
	    		all.addAll(BaseGameHelper.getAllWatcherCards());
	    		return all;
	    	case 10:
	    		all.clear();
	    		all.addAll(BaseGameHelper.getAllColorlessCards());
	    		return all;
	    	case 11:
	    		all.clear();
	    		all.addAll(BaseGameHelper.getAllIroncladCards());
	    		all.addAll(BaseGameHelper.getAllColorlessCards());
	    		return all;
	    	case 12:
	    		all.clear();
	    		all.addAll(BaseGameHelper.getAllSilentCards());
	    		all.addAll(BaseGameHelper.getAllColorlessCards());
	    		return all;
	    	case 13:
	    		all.clear();
	    		all.addAll(BaseGameHelper.getAllDefectCards());
	    		all.addAll(BaseGameHelper.getAllColorlessCards());
	    		return all;
	     	case 14:
	    		all.clear();
	    		all.addAll(BaseGameHelper.getAllWatcherCards());
	    		all.addAll(BaseGameHelper.getAllColorlessCards());
	    		return all;
	     	case 15:
	    		return BaseGameHelper.getAllIroncladCards();
	    	case 16:
	    		return BaseGameHelper.getAllDefectCards();
	    	case 17:
	    		return BaseGameHelper.getAllSilentCards();
	    	case 18:
	    		return BaseGameHelper.getAllWatcherCards();
	    	default:
	    		all.clear();
	    		all.addAll(BaseGameHelper.getAllIroncladCards());
	    		all.addAll(BaseGameHelper.getAllSilentCards());
	    		all.addAll(BaseGameHelper.getAllDefectCards());
	    		all.addAll(BaseGameHelper.getAllWatcherCards());
	    		all.addAll(BaseGameHelper.getAllColorlessCards());
	    		return all;
    	}
    }
    
    @Override
    public void onEquip()
    {
    	DuelistMod.hasCardRewardRelic = true;
    	
    	CardGroup group = new CardGroup(CardGroup.CardGroupType.UNSPECIFIED);
		ArrayList<AbstractCard> myCardsCopy = new ArrayList<AbstractCard>();
		myCardsCopy.addAll(getColorCards());
		List<AbstractCard> list = myCardsCopy;
		for (AbstractCard c : list)
		{
			group.addToBottom(c);
			UnlockTracker.unlockCard(c.cardID);
		}
		AbstractDungeon.gridSelectScreen = this.dcss;
		DuelistMod.wasViewingSelectScreen = true;
		((DuelistCardSelectScreen)AbstractDungeon.gridSelectScreen).open(group, 1, "Select any " + getColorName() + " card to add to your deck");
		
        try 
		{
			SpireConfig config = new SpireConfig("TheDuelist", "DuelistConfig",DuelistMod.duelistDefaults);
			config.setBool(DuelistMod.PROP_CARD_REWARD_RELIC, DuelistMod.hasCardRewardRelic);
			config.save();
		} catch (Exception e) { e.printStackTrace(); }
    }
    

    @Override
    public void onUnequip()
    {
        DuelistMod.hasCardRewardRelic = false;        
        try 
		{
			SpireConfig config = new SpireConfig("TheDuelist", "DuelistConfig",DuelistMod.duelistDefaults);
			config.setBool(DuelistMod.PROP_CARD_REWARD_RELIC, DuelistMod.hasCardRewardRelic);
			config.save();
		} catch (Exception e) { e.printStackTrace(); }
    }
    
    
    @Override
	public void update() 
	{
		super.update();
		if (!cardSelected && !this.dcss.selectedCards.isEmpty()) 
		{
			cardSelected = true;
			AbstractDungeon.effectList.add(new ShowCardAndObtainEffect(this.dcss.selectedCards.get(0).makeCopy(), (float)Settings.WIDTH / 2.0f, (float)Settings.HEIGHT / 2.0f));
			this.dcss.selectedCards.clear();
			AbstractDungeon.closeCurrentScreen();
			//AbstractDungeon.gridSelectScreen = new GridCardSelectScreen();
		}
	}
    
    @Override
    public int getPrice()
    {
    	return 250;
    }
   
}
