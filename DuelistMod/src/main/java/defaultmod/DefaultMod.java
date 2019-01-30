package defaultmod;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.evacipated.cardcrawl.modthespire.lib.SpireInitializer;
import com.megacrit.cardcrawl.helpers.CardHelper;
import com.megacrit.cardcrawl.localization.CardStrings;
import com.megacrit.cardcrawl.localization.PotionStrings;
import com.megacrit.cardcrawl.localization.PowerStrings;
import com.megacrit.cardcrawl.localization.RelicStrings;
import com.megacrit.cardcrawl.unlock.UnlockTracker;

import basemod.BaseMod;
import basemod.ModLabel;
import basemod.ModPanel;
import basemod.helpers.RelicType;
import basemod.interfaces.EditCardsSubscriber;
import basemod.interfaces.EditCharactersSubscriber;
import basemod.interfaces.EditKeywordsSubscriber;
import basemod.interfaces.EditRelicsSubscriber;
import basemod.interfaces.EditStringsSubscriber;
import basemod.interfaces.PostInitializeSubscriber;

import defaultmod.potions.*;
import defaultmod.patches.*;
import defaultmod.relics.*;
import defaultmod.variables.*;
import defaultmod.cards.*;
import defaultmod.characters.*;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@SpireInitializer
public class DefaultMod
        implements EditCardsSubscriber, EditRelicsSubscriber, EditStringsSubscriber, EditKeywordsSubscriber,
        EditCharactersSubscriber, PostInitializeSubscriber {
    public static final Logger logger = LogManager.getLogger(DefaultMod.class.getName());

    //This is for the in-game mod settings panel.
    private static final String MODNAME = "Duelist Mod";
    private static final String AUTHOR = "Nyoxide";
    private static final String DESCRIPTION = "A Slay the Spire adaptation of Yu-Gi-Oh!";

    // =============== INPUT TEXTURE LOCATION =================

    // Colors (RGB)
        // Character Color
        public static final Color DEFAULT_GRAY = CardHelper.getColor(64.0f, 70.0f, 70.0f);
        
        // Potion Colors in RGB
        public static final Color PLACEHOLDER_POTION_LIQUID = CardHelper.getColor(209.0f, 53.0f, 18.0f); // Orange-ish Red
        public static final Color PLACEHOLDER_POTION_HYBRID = CardHelper.getColor(255.0f, 230.0f, 230.0f); // Near White
        public static final Color PLACEHOLDER_POTION_SPOTS = CardHelper.getColor(100.0f, 25.0f, 10.0f); // Super Dark Red/Brown
        
    // Image folder name - This is where your image folder is.
    // This is good practice in case you ever need to move/rename it without screwing up every single path.
    // In this case, it's resources/defaultModResources/images (and then, say, /cards/Strike.png).

    private static final String DEFAULT_MOD_ASSETS_FOLDER = "defaultModResources/images";

    // Card backgrounds
    private static final String ATTACK_DEFAULT_GRAY = "512/bg_attack_default_gray.png";
    private static final String POWER_DEFAULT_GRAY = "512/bg_power_default_gray.png";
    private static final String SKILL_DEFAULT_GRAY = "512/bg_skill_default_gray.png";
    private static final String ENERGY_ORB_DEFAULT_GRAY = "512/card_default_gray_orb.png";
    private static final String CARD_ENERGY_ORB = "512/card_small_orb.png";

    private static final String ATTACK_DEFAULT_GRAY_PORTRAIT = "1024/bg_attack_default_gray.png";
    private static final String POWER_DEFAULT_GRAY_PORTRAIT = "1024/bg_power_default_gray.png";
    private static final String SKILL_DEFAULT_GRAY_PORTRAIT = "1024/bg_skill_default_gray.png";
    private static final String ENERGY_ORB_DEFAULT_GRAY_PORTRAIT = "1024/card_default_gray_orb.png";

    // Card images
    /*
    public static final String DEFAULT_COMMON_ATTACK = "cards/Attack.png";
    public static final String DEFAULT_COMMON_SKILL = "cards/Skill.png";
    public static final String DEFAULT_COMMON_POWER = "cards/Power.png";
    public static final String DEFAULT_UNCOMMON_ATTACK = "cards/Attack.png";
    public static final String DEFAULT_UNCOMMON_SKILL = "cards/Skill.png";
    public static final String DEFAULT_UNCOMMON_POWER = "cards/Power.png";
    public static final String DEFAULT_RARE_ATTACK = "cards/Attack.png";
    public static final String DEFAULT_RARE_SKILL = "cards/Skill.png";
    public static final String DEFAULT_RARE_POWER = "cards/Power.png";
    */
    
    public static final String ALPHA_MAGNET = "cards/Alpha_Magnet.png";
    public static final String ANCIENT_RULES = "cards/Ancient_Rules.png";
    public static final String AXE_DESPAIR = "cards/Axe_Despair.png";
    public static final String BAD_REACTION = "cards/Bad_Reaction.png";
    public static final String BARREL_DRAGON = "cards/Barrel_Dragon.png";
    public static final String BETA_MAGNET = "cards/Beta_Magnet.png";
    public static final String BLUE_EYES = "cards/Blue_Eyes.png";
    public static final String BLUE_EYES_TOON = "cards/Blue_Eyes_Toon.png";
    public static final String BLUE_EYES_ULTIMATE = "cards/Blue_Eyes_Ultimate.png";
    public static final String BUSTER_BLADER = "cards/Buster_Blader.png";
    public static final String CANNON_SOLDIER = "cards/Cannon_Soldier.png";
    public static final String CARD_DESTRUCTION = "cards/Card_Destruction.png";
    public static final String CASTLE_DARK = "cards/Castle_Dark.png";
    public static final String CASTLE_WALLS = "cards/Castle_Walls.png";
    public static final String CATAPULT_TURTLE = "cards/Catapult_Turtle.png";
    public static final String CELTIC_GUARDIAN = "cards/Celtic_Guardian.png";
    public static final String CHANGE_HEART = "cards/Change_Heart.png";
    public static final String DARKLORD_MARIE = "cards/Darklord_Marie.png";
    public static final String DARK_HOLE = "cards/Dark_Hole.png";
    public static final String DARK_MAGICIAN = "cards/Dark_Magician.png";
    public static final String DARK_MAGICIAN_GIRL = "cards/Dark_Magician_Girl.png";
    public static final String DARK_MIRROR_FORCE = "cards/Dark_Mirror_Force.png";
    public static final String DIAN_KETO = "cards/Dian_Keto.png";
    public static final String EXODIA_HEAD = "cards/Exodia_Head.png";
    public static final String EXODIA_LEFT_ARM = "cards/Exodia_LA.png";
    public static final String EXODIA_LEFT_LEG = "cards/Exodia_LL.png";
    public static final String EXODIA_RIGHT_ARM = "cards/Exodia_RA.png";
    public static final String EXODIA_RIGHT_LEG = "cards/Exodia_RL.png";
    public static final String FEATHER_PHOENIX = "cards/Feather_Phoenix.png";
    public static final String FIEND_MEGACYBER = "cards/Fiend_Megacyber.png";
    public static final String FISSURE = "cards/Fissure.png";
    public static final String FLAME_SWORDSMAN = "cards/Flame_Swordsman.png";
    public static final String GAIA_FIERCE = "cards/Gaia_Fierce.png";
    public static final String GAMMA_MAGNET = "cards/Gamma_Magnet.png";
    public static final String GATE_GUARDIAN = "cards/Gate_Guardian.png";
    public static final String GEMINI_ELF = "cards/Gemini_Elf.png";
    public static final String GIANT_SOLDIER = "cards/Giant_Soldier.png";
    public static final String GIANT_TRUNADE = "cards/Giant_Trunade.png";
    public static final String GRACEFUL_CHARITY = "cards/Graceful_Charity.png";
    public static final String HARPIE_FEATHER = "cards/Harpie_Feather.png";
    public static final String HINOTOMA = "cards/Hinotoma.png";
    public static final String IMPERIAL_ORDER = "cards/Imperial_Order.png";
    public static final String INJECTION_FAIRY = "cards/Injection_Fairy.png";
    public static final String INSECT_QUEEN = "cards/Insect_Queen.png";
    public static final String JAM_BREEDING = "cards/Jam_Breeding.png";
    public static final String JUDGE_MAN = "cards/Judge_Man.png";
    public static final String KURIBOH = "cards/Kuriboh.png";
    public static final String LABYRINTH_WALL = "cards/Labyrinth_Wall.png";
    public static final String LEGENDARY_FISHERMAN = "cards/Legendary_Fisherman.png";
    public static final String MAGIC_CYLINDER = "cards/Magic_Cylinder.png";
    public static final String MAUSOLEUM = "cards/Mausoleum.png";
    public static final String MILLENNIUM_SHIELD = "cards/Millennium_Shield.png";
    public static final String MIRROR_FORCE = "cards/Mirror_Force.png";
    public static final String MONSTER_REBORN = "cards/Monster_Reborn.png";
    public static final String NUTRIENT_Z = "cards/Nutrient_Z.png";
    public static final String OBELISK_TORMENTOR = "cards/Obelisk_Tormentor.png";
    public static final String OJAMAGIC = "cards/Ojamagic.png";
    public static final String OJAMA_BLACK = "cards/Ojama_Black.png";
    public static final String OJAMA_GREEN = "cards/Ojama_Green.png";
    public static final String OJAMA_KING = "cards/Ojama_King.png";
    public static final String OJAMA_KNIGHT = "cards/Ojama_Knight.png";
    public static final String OJAMA_YELLOW = "cards/Ojama_Yellow.png";
    public static final String OOKAZI = "cards/Ookazi.png";
    public static final String PARASITE = "cards/Parasite.png";
    public static final String POT_AVARICE = "cards/Pot_Avarice.png";
    public static final String POT_DICHOTOMY = "cards/Pot_Dichotomy.png";
    public static final String POT_DUALITY = "cards/Pot_Duality.png";
    public static final String POT_GENEROSITY = "cards/Pot_Generosity.png";
    public static final String POT_GREED = "cards/Pot_Greed.png";
    public static final String PUMPKING = "cards/Pumpking.png";
    public static final String PUMPRINCESS = "cards/Pumprincess.png";
    public static final String RADIANT_MIRROR_FORCE = "cards/Radiant_Mirror_Force.png";
    public static final String RAIN_MERCY = "cards/Rain_Mercy.png";
    public static final String RED_EYES = "cards/Red_Eyes.png";
    public static final String RED_EYES_TOON = "cards/Red_Eyes_Toon.png";
    public static final String RED_MEDICINE = "cards/Red_Medicine.png";
    public static final String RELINQUISHED = "cards/Relinquished.png";
    public static final String SANGA_EARTH = "cards/Sanga_Earth.png";
    public static final String SANGA_THUNDER = "cards/Sanga_Thunder.png";
    public static final String SANGA_WATER = "cards/Sanga_Water.png";
    public static final String SCAPEGOAT = "cards/Scapegoat.png";
    public static final String SCRAP_FACTORY = "cards/Scrap_Factory.png";
    public static final String SEVEN_COLORED_FISH = "cards/7_Colored_Fish.png";
    public static final String SHARD_GREED = "cards/Shard_Greed.png";
    public static final String SLIFER_SKY = "cards/Slifer_Sky.png";
    public static final String STORMING_MIRROR_FORCE = "cards/Storming_Mirror_Force.png";
    public static final String SUMMONED_SKULL = "cards/Summoned_Skull.png";
    public static final String SUPERHEAVY_BENKEI = "cards/Superheavy_Benkei.png";
    public static final String SUPERHEAVY_SCALES = "cards/Superheavy_Scales.png";
    public static final String SUPERHEAVY_SWORDSMAN = "cards/Superheavy_Swordsman.png";
    public static final String SUPERHEAVY_WARAJI = "cards/Superheavy_Waraji.png";
    public static final String SWORDS_BURNING = "cards/Swords_Burning.png";
    public static final String SWORDS_CONCEALING = "cards/Swords_Concealing.png";
    public static final String SWORDS_REVEALING = "cards/Swords_Revealing.png";
    public static final String TIME_WIZARD = "cards/Time_Wizard.png";
    public static final String TOON_BARREL_DRAGON = "cards/Toon_Barrel_Dragon.png";
    public static final String TOON_BRIEFCASE = "cards/Toon_Briefcase.png";
    public static final String TOON_DARK_MAGICIAN = "cards/Toon_Dark_Magician.png";
    public static final String TOON_DARK_MAGICIAN_GIRL = "cards/Toon_Dark_Magician_Girl.png";
    public static final String TOON_GEMINI_ELF = "cards/Toon_Gemini_Elf.png";
    public static final String TOON_MERMAID = "cards/Toon_Mermaid.png";
    public static final String TOON_SUMMONED_SKULL = "cards/Toon_Summoned_Skull.png";
    public static final String TOON_WORLD = "cards/Toon_World.png";
    public static final String TRAP_HOLE = "cards/Trap_Hole.png";
    public static final String VALK_MAGNET = "cards/Valk_Magnet.png";
    public static final String WINGED_DRAGON_RA = "cards/Winged_Dragon_Ra.png";
  
    // Power images
    public static final String SUMMON_POWER = "powers/SummonPower.png";
    //public static final String JAM_POWER = "powers/JamPower.png";
    //public static final String DESPAIR_POWER = "powers/DespairPower.png";
    //public static final String OBELISK_POWER = "powers/ObeliskPower.png";
    
    // Relic images  
    public static final String PLACEHOLDER_RELIC = "relics/placeholder_relic.png";
    public static final String PLACEHOLDER_RELIC_OUTLINE = "relics/outline/placeholder_relic.png";

    public static final String PLACEHOLDER_RELIC_2 = "relics/placeholder_relic2.png";
    public static final String PLACEHOLDER_RELIC_OUTLINE_2 = "relics/outline/placeholder_relic2.png";
    
    // Character assets
    private static final String THE_DEFAULT_BUTTON = "charSelect/DefaultCharacterButton.png";
    private static final String THE_DEFAULT_PORTRAIT = "charSelect/DefaultCharacterPortraitBG.png";
    public static final String THE_DEFAULT_SHOULDER_1 = "char/defaultCharacter/shoulder.png";
    public static final String THE_DEFAULT_SHOULDER_2 = "char/defaultCharacter/shoulder2.png";
    public static final String THE_DEFAULT_CORPSE = "char/defaultCharacter/corpse.png";

    //Mod Badge
    public static final String BADGE_IMAGE = "Badge.png";

    // Animations atlas and JSON files
    public static final String THE_DEFAULT_SKELETON_ATLAS = "char/defaultCharacter/skeleton.atlas";
    public static final String THE_DEFAULT_SKELETON_JSON = "char/defaultCharacter/skeleton.json";

	

    // =============== /INPUT TEXTURE LOCATION/ =================

    // =============== IMAGE PATHS =================

    // This is the command that will link up your core assets folder (line 89) ("defaultModResources/images")
    // together with the card image (everything above) ("cards/Attack.png") and it puts a "/" between them.
    // When adding a card image, you can, in fact, just do "defaultModResources/images/cards/Attack.png" in the actual card file.
    // This however, is good practice in case you want to change your "/images" folder at any point in time.

    /**
     * @param resource the resource, must *NOT* have a leading "/"
     * @return the full path
     */
    public static final String makePath(String resource) {
        return DEFAULT_MOD_ASSETS_FOLDER + "/" + resource;
    }

    // =============== /IMAGE PATHS/ =================

    // =============== SUBSCRIBE, CREATE THE COLOR, INITIALIZE =================

    public DefaultMod() {
        logger.info("Subscribe to BaseMod hooks");

        BaseMod.subscribe(this);

        logger.info("Done subscribing");

        logger.info("Creating the color " + AbstractCardEnum.DEFAULT_GRAY.toString());

        BaseMod.addColor(AbstractCardEnum.DEFAULT_GRAY, DEFAULT_GRAY, DEFAULT_GRAY, DEFAULT_GRAY,
                DEFAULT_GRAY, DEFAULT_GRAY, DEFAULT_GRAY, DEFAULT_GRAY, makePath(ATTACK_DEFAULT_GRAY),
                makePath(SKILL_DEFAULT_GRAY), makePath(POWER_DEFAULT_GRAY),
                makePath(ENERGY_ORB_DEFAULT_GRAY), makePath(ATTACK_DEFAULT_GRAY_PORTRAIT),
                makePath(SKILL_DEFAULT_GRAY_PORTRAIT), makePath(POWER_DEFAULT_GRAY_PORTRAIT),
                makePath(ENERGY_ORB_DEFAULT_GRAY_PORTRAIT), makePath(CARD_ENERGY_ORB));

        logger.info("Done creating the color");
    }

    @SuppressWarnings("unused")
    public static void initialize() {
        logger.info("========================= Initializing Duelist Mod. Hi. =========================");
        DefaultMod defaultmod = new DefaultMod();
        logger.info("========================= /Duelist Mod Initialized/ =========================");
    }

    // ============== /SUBSCRIBE, CREATE THE COLOR, INITIALIZE/ =================

    
    // =============== LOAD THE CHARACTER =================

    @Override
    public void receiveEditCharacters() {
        logger.info("Beginning to edit characters. " + "Add " + TheDefaultEnum.THE_DEFAULT.toString());

        BaseMod.addCharacter(new TheDefault("the Default", TheDefaultEnum.THE_DEFAULT),
                makePath(THE_DEFAULT_BUTTON), makePath(THE_DEFAULT_PORTRAIT), TheDefaultEnum.THE_DEFAULT);
        
        receiveEditPotions();
        logger.info("Done editing characters");
    }

    // =============== /LOAD THE CHARACTER/ =================

    
    // =============== POST-INITIALIZE =================

    
    @Override
    public void receivePostInitialize() {

        logger.info("Loading badge image and mod options");
        // Load the Mod Badge
        Texture badgeTexture = new Texture(makePath(BADGE_IMAGE));
        
        // Create the Mod Menu
        ModPanel settingsPanel = new ModPanel();
        settingsPanel.addUIElement(new ModLabel("DuelistMod doesn't have any settings!", 400.0f, 700.0f,
                settingsPanel, (me) -> {
                }));
        BaseMod.registerModBadge(badgeTexture, MODNAME, AUTHOR, DESCRIPTION, settingsPanel);

        logger.info("Done loading badge Image and mod options");

       }

    // =============== / POST-INITIALIZE/ =================

    
    // ================ ADD POTIONS ===================

       
    public void receiveEditPotions() {
        logger.info("Beginning to edit potions");
       
        // Class Specific Potion. If you want your potion to not be class-specific, just remove the player class at the end (in this case the "TheDefaultEnum.THE_DEFAULT")
        BaseMod.addPotion(PlaceholderPotion.class, PLACEHOLDER_POTION_LIQUID, PLACEHOLDER_POTION_HYBRID, PLACEHOLDER_POTION_SPOTS, PlaceholderPotion.POTION_ID, TheDefaultEnum.THE_DEFAULT);
      
        logger.info("Done editing potions");
    }
    
    // ================ /ADD POTIONS/ ===================

    
    // ================ ADD RELICS ===================

    @Override
    public void receiveEditRelics() {
        logger.info("Adding relics");

        // This adds a character specific relic. Only when you play with the mentioned color, will you get this relic.
        BaseMod.addRelicToCustomPool(new PlaceholderRelic(), AbstractCardEnum.DEFAULT_GRAY);
        BaseMod.addRelicToCustomPool(new StarterRelic(), AbstractCardEnum.DEFAULT_GRAY);

        // This adds a relic to the Shared pool. Every character can find this relic.
        BaseMod.addRelic(new PlaceholderRelic2(), RelicType.SHARED);

        logger.info("Done adding relics!");
    }

    // ================ /ADD RELICS/ ===================

    
    
    // ================ ADD CARDS ===================

    @Override
    public void receiveEditCards() {
        logger.info("Adding variables");
        // Add the Custom Dynamic Variables
        BaseMod.addDynamicVariable(new DefaultCustomVariable());
        
        logger.info("Adding cards");
        // Add the cards
        BaseMod.addCard(new AlphaMagnet());
		BaseMod.addCard(new AncientRules());
		BaseMod.addCard(new AxeDespair());
		BaseMod.addCard(new BarrelDragon());
		BaseMod.addCard(new BetaMagnet());
		BaseMod.addCard(new BlueEyes());
		BaseMod.addCard(new BlueEyesUltimate());
		BaseMod.addCard(new BusterBlader());
		BaseMod.addCard(new CastleWalls());
		BaseMod.addCard(new CelticGuardian());
		BaseMod.addCard(new DarkMagician());
		BaseMod.addCard(new DarkMagicianGirl());
		BaseMod.addCard(new FlameSwordsman());
		BaseMod.addCard(new GaiaFierce());
		BaseMod.addCard(new GammaMagnet());
		BaseMod.addCard(new GateGuardian());
		BaseMod.addCard(new GeminiElf());
		BaseMod.addCard(new GiantSoldier());
		BaseMod.addCard(new Hinotoma());
		BaseMod.addCard(new InsectQueen());
		BaseMod.addCard(new JamBreeding());
		BaseMod.addCard(new JudgeMan());
		BaseMod.addCard(new Kuriboh());
		BaseMod.addCard(new LabyrinthWall());
		BaseMod.addCard(new Ookazi());
		BaseMod.addCard(new RedEyes());
		BaseMod.addCard(new SangaEarth());
		BaseMod.addCard(new SangaThunder());
		BaseMod.addCard(new SangaWater());
		BaseMod.addCard(new ScrapFactory());
		BaseMod.addCard(new SevenColoredFish());
		BaseMod.addCard(new SummonedSkull());
		BaseMod.addCard(new SuperheavyScales());
		
        /*
        BaseMod.addCard(new AlphaMagnet());
		BaseMod.addCard(new AncientRules());
		BaseMod.addCard(new AxeDespair());
		BaseMod.addCard(new BadReaction());
		BaseMod.addCard(new BarrelDragon());
		BaseMod.addCard(new BetaMagnet());
		BaseMod.addCard(new BlueEyes());
		BaseMod.addCard(new BlueEyesToon());
		BaseMod.addCard(new BlueEyesUltimate());
		BaseMod.addCard(new BusterBlader());
		BaseMod.addCard(new CannonSoldier());
		BaseMod.addCard(new CardDestruction());
		BaseMod.addCard(new CastleDarkIllusions());
		BaseMod.addCard(new CastleWalls());
		BaseMod.addCard(new CatapultTurtle());
		BaseMod.addCard(new CelticGuardian());
		BaseMod.addCard(new ChangeHeart());
		BaseMod.addCard(new DarkHole());
		BaseMod.addCard(new DarkMagician());
		BaseMod.addCard(new DarkMagicianGirl());
		BaseMod.addCard(new DarkMirrorForce());
		BaseMod.addCard(new DarklordMarie());
		BaseMod.addCard(new DianKeto());
		BaseMod.addCard(new ExodiaHead());
		BaseMod.addCard(new ExodiaLA());
		BaseMod.addCard(new ExodiaLL());
		BaseMod.addCard(new ExodiaRA());
		BaseMod.addCard(new ExodiaRL());
		BaseMod.addCard(new FeatherPho());
		BaseMod.addCard(new FiendMegacyber());
		BaseMod.addCard(new Fissure());
		BaseMod.addCard(new FlameSwordsman());
		BaseMod.addCard(new GaiaFierce());
		BaseMod.addCard(new GammaMagnet());
		BaseMod.addCard(new GateGuardian());
		BaseMod.addCard(new GeminiElf());
		BaseMod.addCard(new GiantSoldier());
		BaseMod.addCard(new GiantTrunade());
		BaseMod.addCard(new GracefulCharity());
		BaseMod.addCard(new HarpieFeather());
		BaseMod.addCard(new Hinotoma());
		BaseMod.addCard(new ImperialOrder());
		BaseMod.addCard(new InjectionFairy());
		BaseMod.addCard(new InsectQueen());
		BaseMod.addCard(new JamBreeding());
		BaseMod.addCard(new JudgeMan());
		BaseMod.addCard(new Kuriboh());
		BaseMod.addCard(new LabyrinthWall());
		BaseMod.addCard(new LegendaryFisherman());
		BaseMod.addCard(new MagicCylinder());
		BaseMod.addCard(new Mausoleum());
		BaseMod.addCard(new MillenniumShield());
		BaseMod.addCard(new MirrorForce());
		BaseMod.addCard(new MonsterReborn());
		BaseMod.addCard(new NutrientZ());
		BaseMod.addCard(new ObeliskTormentor());
		BaseMod.addCard(new OjamaBlack());
		BaseMod.addCard(new OjamaGreen());
		BaseMod.addCard(new OjamaKing());
		BaseMod.addCard(new OjamaKnight());
		BaseMod.addCard(new OjamaYellow());
		BaseMod.addCard(new Ojamagic());
		BaseMod.addCard(new Ookazi());
		BaseMod.addCard(new Parasite());
		BaseMod.addCard(new PotAvarice());
		BaseMod.addCard(new PotDichotomy());
		BaseMod.addCard(new PotDuality());
		BaseMod.addCard(new PotGenerosity());
		BaseMod.addCard(new PotGreed());
		BaseMod.addCard(new Pumpking());
		BaseMod.addCard(new Pumprincess());
		BaseMod.addCard(new RadiantMirrorForce());
		BaseMod.addCard(new RainMercy());
		BaseMod.addCard(new RedEyes());
		BaseMod.addCard(new RedEyesToon());
		BaseMod.addCard(new RedMedicine());
		BaseMod.addCard(new Relinquished());
		BaseMod.addCard(new SangaEarth());
		BaseMod.addCard(new SangaThunder());
		BaseMod.addCard(new SangaWater());
		BaseMod.addCard(new Scapegoat());
		BaseMod.addCard(new ScrapFactory());
		BaseMod.addCard(new SevenColoredFish());
		BaseMod.addCard(new ShardGreed());
		BaseMod.addCard(new SliferSky());
		BaseMod.addCard(new StormingMirrorForce());
		BaseMod.addCard(new SummonedSkull());
		BaseMod.addCard(new SuperheavyBenkei());
		BaseMod.addCard(new SuperheavyScales());
		BaseMod.addCard(new SuperheavySwordsman());
		BaseMod.addCard(new SuperheavyWaraji());
		BaseMod.addCard(new SwordsBurning());
		BaseMod.addCard(new SwordsConcealing());
		BaseMod.addCard(new SwordsRevealing());
		BaseMod.addCard(new TimeWizard());
		BaseMod.addCard(new ToonBarrelDragon());
		BaseMod.addCard(new ToonBriefcase());
		BaseMod.addCard(new ToonDarkMagician());
		BaseMod.addCard(new ToonDarkMagicianGirl());
		BaseMod.addCard(new ToonGeminiElf());
		BaseMod.addCard(new ToonMermaid());
		BaseMod.addCard(new ToonSummonedSkull());
		BaseMod.addCard(new ToonWorld());
		BaseMod.addCard(new TrapHole());
		BaseMod.addCard(new ValkMagnet());
		BaseMod.addCard(new WingedDragonRa());
    	*/
        
		logger.info("Making sure the cards are unlocked.");
        // Unlock the cards
        UnlockTracker.unlockCard(AlphaMagnet.ID);
		UnlockTracker.unlockCard(AncientRules.ID);
		UnlockTracker.unlockCard(AxeDespair.ID);	
		UnlockTracker.unlockCard(BarrelDragon.ID);
		UnlockTracker.unlockCard(BetaMagnet.ID);
		UnlockTracker.unlockCard(BlueEyes.ID);		
		UnlockTracker.unlockCard(BlueEyesUltimate.ID);		
		UnlockTracker.unlockCard(BusterBlader.ID);		
		UnlockTracker.unlockCard(CastleWalls.ID);		
		UnlockTracker.unlockCard(CelticGuardian.ID);		
		UnlockTracker.unlockCard(DarkMagician.ID);
		UnlockTracker.unlockCard(DarkMagicianGirl.ID);
		UnlockTracker.unlockCard(FlameSwordsman.ID);
		UnlockTracker.unlockCard(GaiaFierce.ID);
		UnlockTracker.unlockCard(GammaMagnet.ID);
		UnlockTracker.unlockCard(GateGuardian.ID);
		UnlockTracker.unlockCard(GeminiElf.ID);
		UnlockTracker.unlockCard(GiantSoldier.ID);
		UnlockTracker.unlockCard(Hinotoma.ID);
		UnlockTracker.unlockCard(InsectQueen.ID);
		UnlockTracker.unlockCard(JamBreeding.ID);
		UnlockTracker.unlockCard(JudgeMan.ID);
		UnlockTracker.unlockCard(Kuriboh.ID);
		UnlockTracker.unlockCard(LabyrinthWall.ID);
		UnlockTracker.unlockCard(Ookazi.ID);
		UnlockTracker.unlockCard(RedEyes.ID);
		UnlockTracker.unlockCard(SangaEarth.ID);
		UnlockTracker.unlockCard(SangaThunder.ID);
		UnlockTracker.unlockCard(SangaWater.ID);
		UnlockTracker.unlockCard(ScrapFactory.ID);
		UnlockTracker.unlockCard(SevenColoredFish.ID);
		UnlockTracker.unlockCard(SummonedSkull.ID);
		UnlockTracker.unlockCard(SuperheavyScales.ID);
        
		
		/*
        UnlockTracker.unlockCard(AlphaMagnet.ID);
		UnlockTracker.unlockCard(AncientRules.ID);
		UnlockTracker.unlockCard(AxeDespair.ID);
		UnlockTracker.unlockCard(BadReaction.ID);
		UnlockTracker.unlockCard(BarrelDragon.ID);
		UnlockTracker.unlockCard(BetaMagnet.ID);
		UnlockTracker.unlockCard(BlueEyes.ID);
		UnlockTracker.unlockCard(BlueEyesToon.ID);
		UnlockTracker.unlockCard(BlueEyesUltimate.ID);
		UnlockTracker.unlockCard(BusterBlader.ID);
		UnlockTracker.unlockCard(CannonSoldier.ID);
		UnlockTracker.unlockCard(CardDestruction.ID);
		UnlockTracker.unlockCard(CastleDarkIllusions.ID);
		UnlockTracker.unlockCard(CastleWalls.ID);
		UnlockTracker.unlockCard(CatapultTurtle.ID);
		UnlockTracker.unlockCard(CelticGuardian.ID);
		UnlockTracker.unlockCard(ChangeHeart.ID);
		UnlockTracker.unlockCard(DarkHole.ID);
		UnlockTracker.unlockCard(DarkMagician.ID);
		UnlockTracker.unlockCard(DarkMagicianGirl.ID);
		UnlockTracker.unlockCard(DarkMirrorForce.ID);
		UnlockTracker.unlockCard(DarklordMarie.ID);
		UnlockTracker.unlockCard(DianKeto.ID);
		UnlockTracker.unlockCard(ExodiaHead.ID);
		UnlockTracker.unlockCard(ExodiaLA.ID);
		UnlockTracker.unlockCard(ExodiaLL.ID);
		UnlockTracker.unlockCard(ExodiaRA.ID);
		UnlockTracker.unlockCard(ExodiaRL.ID);
		UnlockTracker.unlockCard(FeatherPho.ID);
		UnlockTracker.unlockCard(FiendMegacyber.ID);
		UnlockTracker.unlockCard(Fissure.ID);
		UnlockTracker.unlockCard(FlameSwordsman.ID);
		UnlockTracker.unlockCard(GaiaFierce.ID);
		UnlockTracker.unlockCard(GammaMagnet.ID);
		UnlockTracker.unlockCard(GateGuardian.ID);
		UnlockTracker.unlockCard(GeminiElf.ID);
		UnlockTracker.unlockCard(GiantSoldier.ID);
		UnlockTracker.unlockCard(GiantTrunade.ID);
		UnlockTracker.unlockCard(GracefulCharity.ID);
		UnlockTracker.unlockCard(HarpieFeather.ID);
		UnlockTracker.unlockCard(Hinotoma.ID);
		UnlockTracker.unlockCard(ImperialOrder.ID);
		UnlockTracker.unlockCard(InjectionFairy.ID);
		UnlockTracker.unlockCard(InsectQueen.ID);
		UnlockTracker.unlockCard(JamBreeding.ID);
		UnlockTracker.unlockCard(JudgeMan.ID);
		UnlockTracker.unlockCard(Kuriboh.ID);
		UnlockTracker.unlockCard(LabyrinthWall.ID);
		UnlockTracker.unlockCard(LegendaryFisherman.ID);
		UnlockTracker.unlockCard(MagicCylinder.ID);
		UnlockTracker.unlockCard(Mausoleum.ID);
		UnlockTracker.unlockCard(MillenniumShield.ID);
		UnlockTracker.unlockCard(MirrorForce.ID);
		UnlockTracker.unlockCard(MonsterReborn.ID);
		UnlockTracker.unlockCard(NutrientZ.ID);
		UnlockTracker.unlockCard(ObeliskTormentor.ID);
		UnlockTracker.unlockCard(OjamaBlack.ID);
		UnlockTracker.unlockCard(OjamaGreen.ID);
		UnlockTracker.unlockCard(OjamaKing.ID);
		UnlockTracker.unlockCard(OjamaKnight.ID);
		UnlockTracker.unlockCard(OjamaYellow.ID);
		UnlockTracker.unlockCard(Ojamagic.ID);
		UnlockTracker.unlockCard(Ookazi.ID);
		UnlockTracker.unlockCard(Parasite.ID);
		UnlockTracker.unlockCard(PotAvarice.ID);
		UnlockTracker.unlockCard(PotDichotomy.ID);
		UnlockTracker.unlockCard(PotDuality.ID);
		UnlockTracker.unlockCard(PotGenerosity.ID);
		UnlockTracker.unlockCard(PotGreed.ID);
		UnlockTracker.unlockCard(Pumpking.ID);
		UnlockTracker.unlockCard(Pumprincess.ID);
		UnlockTracker.unlockCard(RadiantMirrorForce.ID);
		UnlockTracker.unlockCard(RainMercy.ID);
		UnlockTracker.unlockCard(RedEyes.ID);
		UnlockTracker.unlockCard(RedEyesToon.ID);
		UnlockTracker.unlockCard(RedMedicine.ID);
		UnlockTracker.unlockCard(Relinquished.ID);
		UnlockTracker.unlockCard(SangaEarth.ID);
		UnlockTracker.unlockCard(SangaThunder.ID);
		UnlockTracker.unlockCard(SangaWater.ID);
		UnlockTracker.unlockCard(Scapegoat.ID);
		UnlockTracker.unlockCard(ScrapFactory.ID);
		UnlockTracker.unlockCard(SevenColoredFish.ID);
		UnlockTracker.unlockCard(ShardGreed.ID);
		UnlockTracker.unlockCard(SliferSky.ID);
		UnlockTracker.unlockCard(StormingMirrorForce.ID);
		UnlockTracker.unlockCard(SummonedSkull.ID);
		UnlockTracker.unlockCard(SuperheavyBenkei.ID);
		UnlockTracker.unlockCard(SuperheavyScales.ID);
		UnlockTracker.unlockCard(SuperheavySwordsman.ID);
		UnlockTracker.unlockCard(SuperheavyWaraji.ID);
		UnlockTracker.unlockCard(SwordsBurning.ID);
		UnlockTracker.unlockCard(SwordsConcealing.ID);
		UnlockTracker.unlockCard(SwordsRevealing.ID);
		UnlockTracker.unlockCard(TimeWizard.ID);
		UnlockTracker.unlockCard(ToonBarrelDragon.ID);
		UnlockTracker.unlockCard(ToonBriefcase.ID);
		UnlockTracker.unlockCard(ToonDarkMagician.ID);
		UnlockTracker.unlockCard(ToonDarkMagicianGirl.ID);
		UnlockTracker.unlockCard(ToonGeminiElf.ID);
		UnlockTracker.unlockCard(ToonMermaid.ID);
		UnlockTracker.unlockCard(ToonSummonedSkull.ID);
		UnlockTracker.unlockCard(ToonWorld.ID);
		UnlockTracker.unlockCard(TrapHole.ID);
		UnlockTracker.unlockCard(ValkMagnet.ID);
		UnlockTracker.unlockCard(WingedDragonRa.ID);
		*/
        logger.info("Done adding cards!");
    }

    // ================ /ADD CARDS/ ===================

    
    
    // ================ LOAD THE TEXT ===================

    @Override
    public void receiveEditStrings() {
        logger.info("Beginning to edit strings");

        // CardStrings
        BaseMod.loadCustomStringsFile(CardStrings.class,
                "defaultModResources/localization/DefaultMod-Card-Strings.json");

        // PowerStrings
        BaseMod.loadCustomStringsFile(PowerStrings.class,
                "defaultModResources/localization/DefaultMod-Power-Strings.json");

        // RelicStrings
        BaseMod.loadCustomStringsFile(RelicStrings.class,
                "defaultModResources/localization/DefaultMod-Relic-Strings.json");

        // PotionStrings
        BaseMod.loadCustomStringsFile(PotionStrings.class,
                "defaultModResources/localization/DefaultMod-Potion-Strings.json");

        logger.info("Done edittting strings");
    }

    // ================ /LOAD THE TEXT/ ===================

    // ================ LOAD THE KEYWORDS ===================

    @Override
    public void receiveEditKeywords() {
        final String[] placeholder = { "keyword", "keywords", "Keyword", "Keywords" };
        BaseMod.addKeyword(placeholder, "Whenever you play a card, gain 1 dexterity this turn only.");
        BaseMod.addKeyword(new String[] {"summon", "Summon"}, "Counts monsters currently summoned. Max 5, cannot go negative.");
        BaseMod.addKeyword(new String[] {"tribute", "Tribute"}, "Removes X summons. Unless you have enough summons to tribute, you cannot play a Tribute Summon monster.");
        BaseMod.addKeyword(new String[] {"counter", "Counter", "Counters", "counters"}, "Spell counters have no inherent effect. Used in tandem with magic monsters to trigger powerful effects.");	
        BaseMod.addKeyword(new String[] {"exodia", "Exodia"}, "OBLITERATE");
        BaseMod.addKeyword(new String[] {"Mako's"}, "Each time target attacks, gain 1 Dexterity.");
        BaseMod.addKeyword(new String[] {"Ra"}, "Gain 1 Strength. Gain 3 Dexterity.");

    }

    // ================ /LOAD THE KEYWORDS/ ===================    

    // this adds "ModName:" before the ID of any card/relic/power etc.
    // in order to avoid conflicts if any other mod uses the same ID.
    public static String makeID(String idText) {
        return "theDuelist:" + idText;
    }

}