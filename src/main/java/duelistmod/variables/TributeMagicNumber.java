package duelistmod.variables;

import com.badlogic.gdx.graphics.Color;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.core.Settings;

import basemod.abstracts.DynamicVariable;
import duelistmod.abstracts.DuelistCard;
import duelistmod.dto.AnyDuelist;
import duelistmod.helpers.Util;

public class TributeMagicNumber extends DynamicVariable {

    @Override
    public String key() {
        return "duelist:TRIB";
    }

    @Override
    public boolean isModified(AbstractCard card) {
        if (card instanceof DuelistCard) {
            DuelistCard dc = (DuelistCard)card;
            AnyDuelist p = AnyDuelist.from(dc);
            int base = dc.tributes;
            int mod = base + dc.checkModifyTributeCostForAbstracts(p, base);
            mod = Util.modifyTributesForApexFeralTerritorial(p, dc, mod);
            return dc.isTributesModified || mod != base;
        }
        return false;
    }

    @Override
    public int value(AbstractCard card) {
        if (!(card instanceof DuelistCard)) return 0;
        DuelistCard dc = (DuelistCard)card;
        AnyDuelist p = AnyDuelist.from(dc);
        int tributes = dc.tributes;
        tributes += dc.checkModifyTributeCostForAbstracts(p, tributes);
        tributes = Util.modifyTributesForApexFeralTerritorial(p, dc, tributes);
        return tributes;
    }

    @Override
    public int baseValue(AbstractCard card) {
        if (!(card instanceof DuelistCard)) return 0;
        DuelistCard dc = (DuelistCard)card;
        dc = (DuelistCard)dc.makeCopy();
        for (int i = 0; i < card.timesUpgraded; i++) {
            dc.upgrade();
        }
        return dc.baseTributes;
    }

    @Override
    public boolean upgraded(AbstractCard card) {
        return card instanceof DuelistCard && ((DuelistCard) card).upgradedTributes;
    }
    
    @Override
    public Color getIncreasedValueColor() {
    	 return Settings.RED_TEXT_COLOR;
    }

    @Override
    public Color getDecreasedValueColor()
    {
        return Settings.GREEN_TEXT_COLOR;
    }

    @Override
    public Color getUpgradedColor(AbstractCard card) {
        if (card instanceof DuelistCard) {
            DuelistCard d = (DuelistCard)card;
            if (d.isBadTributeUpgrade) {
                return Settings.RED_TEXT_COLOR;
            }
        }
        return Settings.GREEN_TEXT_COLOR;
    }
}
